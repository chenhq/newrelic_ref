/*     */ package com.newrelic.javassist.tools.rmi;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class ObjectImporter
/*     */   implements Serializable
/*     */ {
/*  75 */   private final byte[] endofline = { 13, 10 };
/*     */   private String servername;
/*     */   private String orgServername;
/*     */   private int port;
/*     */   private int orgPort;
/*  79 */   protected byte[] lookupCommand = "POST /lookup HTTP/1.0".getBytes();
/*  80 */   protected byte[] rmiCommand = "POST /rmi HTTP/1.0".getBytes();
/*     */ 
/* 185 */   private static final Class[] proxyConstructorParamTypes = { ObjectImporter.class, Integer.TYPE };
/*     */ 
/*     */   public ObjectImporter(Applet applet)
/*     */   {
/*  91 */     URL codebase = applet.getCodeBase();
/*  92 */     this.orgServername = (this.servername = codebase.getHost());
/*  93 */     this.orgPort = (this.port = codebase.getPort());
/*     */   }
/*     */ 
/*     */   public ObjectImporter(String servername, int port)
/*     */   {
/* 110 */     this.orgServername = (this.servername = servername);
/* 111 */     this.orgPort = (this.port = port);
/*     */   }
/*     */ 
/*     */   public Object getObject(String name)
/*     */   {
/*     */     try
/*     */     {
/* 123 */       return lookupObject(name);
/*     */     } catch (ObjectNotFoundException e) {
/*     */     }
/* 126 */     return null;
/*     */   }
/*     */ 
/*     */   public void setHttpProxy(String host, int port)
/*     */   {
/* 135 */     String proxyHeader = "POST http://" + this.orgServername + ":" + this.orgPort;
/* 136 */     String cmd = proxyHeader + "/lookup HTTP/1.0";
/* 137 */     this.lookupCommand = cmd.getBytes();
/* 138 */     cmd = proxyHeader + "/rmi HTTP/1.0";
/* 139 */     this.rmiCommand = cmd.getBytes();
/* 140 */     this.servername = host;
/* 141 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public Object lookupObject(String name)
/*     */     throws ObjectNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 155 */       Socket sock = new Socket(this.servername, this.port);
/* 156 */       OutputStream out = sock.getOutputStream();
/* 157 */       out.write(this.lookupCommand);
/* 158 */       out.write(this.endofline);
/* 159 */       out.write(this.endofline);
/*     */ 
/* 161 */       ObjectOutputStream dout = new ObjectOutputStream(out);
/* 162 */       dout.writeUTF(name);
/* 163 */       dout.flush();
/*     */ 
/* 165 */       InputStream in = new BufferedInputStream(sock.getInputStream());
/* 166 */       skipHeader(in);
/* 167 */       ObjectInputStream din = new ObjectInputStream(in);
/* 168 */       int n = din.readInt();
/* 169 */       String classname = din.readUTF();
/* 170 */       din.close();
/* 171 */       dout.close();
/* 172 */       sock.close();
/*     */ 
/* 174 */       if (n >= 0)
/* 175 */         return createProxy(n, classname);
/*     */     }
/*     */     catch (Exception e) {
/* 178 */       e.printStackTrace();
/* 179 */       throw new ObjectNotFoundException(name, e);
/*     */     }
/*     */ 
/* 182 */     throw new ObjectNotFoundException(name);
/*     */   }
/*     */ 
/*     */   private Object createProxy(int oid, String classname)
/*     */     throws Exception
/*     */   {
/* 189 */     Class c = Class.forName(classname);
/* 190 */     Constructor cons = c.getConstructor(proxyConstructorParamTypes);
/* 191 */     return cons.newInstance(new Object[] { this, new Integer(oid) });
/*     */   }
/*     */ 
/*     */   public Object call(int objectid, int methodid, Object[] args)
/*     */     throws RemoteException
/*     */   {
/*     */     boolean result;
/*     */     Object rvalue;
/*     */     String errmsg;
/*     */     try
/*     */     {
/* 224 */       Socket sock = new Socket(this.servername, this.port);
/* 225 */       OutputStream out = new BufferedOutputStream(sock.getOutputStream());
/*     */ 
/* 227 */       out.write(this.rmiCommand);
/* 228 */       out.write(this.endofline);
/* 229 */       out.write(this.endofline);
/*     */ 
/* 231 */       ObjectOutputStream dout = new ObjectOutputStream(out);
/* 232 */       dout.writeInt(objectid);
/* 233 */       dout.writeInt(methodid);
/* 234 */       writeParameters(dout, args);
/* 235 */       dout.flush();
/*     */ 
/* 237 */       InputStream ins = new BufferedInputStream(sock.getInputStream());
/* 238 */       skipHeader(ins);
/* 239 */       ObjectInputStream din = new ObjectInputStream(ins);
/* 240 */       result = din.readBoolean();
/* 241 */       rvalue = null;
/* 242 */       errmsg = null;
/* 243 */       if (result)
/* 244 */         rvalue = din.readObject();
/*     */       else {
/* 246 */         errmsg = din.readUTF();
/*     */       }
/* 248 */       din.close();
/* 249 */       dout.close();
/* 250 */       sock.close();
/*     */ 
/* 252 */       if ((rvalue instanceof RemoteRef)) {
/* 253 */         RemoteRef ref = (RemoteRef)rvalue;
/* 254 */         rvalue = createProxy(ref.oid, ref.classname);
/*     */       }
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/* 258 */       throw new RemoteException(e);
/*     */     }
/*     */     catch (IOException e) {
/* 261 */       throw new RemoteException(e);
/*     */     }
/*     */     catch (Exception e) {
/* 264 */       throw new RemoteException(e);
/*     */     }
/*     */ 
/* 267 */     if (result) {
/* 268 */       return rvalue;
/*     */     }
/* 270 */     throw new RemoteException(errmsg);
/*     */   }
/*     */ 
/*     */   private void skipHeader(InputStream in) throws IOException
/*     */   {
/*     */     int len;
/*     */     do {
/* 277 */       len = 0;
/*     */       int c;
/* 278 */       while (((c = in.read()) >= 0) && (c != 13)) {
/* 279 */         len++;
/*     */       }
/* 281 */       in.read();
/* 282 */     }while (len > 0);
/*     */   }
/*     */ 
/*     */   private void writeParameters(ObjectOutputStream dout, Object[] params)
/*     */     throws IOException
/*     */   {
/* 288 */     int n = params.length;
/* 289 */     dout.writeInt(n);
/* 290 */     for (int i = 0; i < n; i++)
/* 291 */       if ((params[i] instanceof Proxy)) {
/* 292 */         Proxy p = (Proxy)params[i];
/* 293 */         dout.writeObject(new RemoteRef(p._getObjectId()));
/*     */       }
/*     */       else {
/* 296 */         dout.writeObject(params[i]);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.rmi.ObjectImporter
 * JD-Core Version:    0.6.2
 */