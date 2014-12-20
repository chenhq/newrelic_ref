/*     */ package com.newrelic.javassist.tools.rmi;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.tools.web.BadHttpRequest;
/*     */ import com.newrelic.javassist.tools.web.Webserver;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InvalidClassException;
/*     */ import java.io.NotSerializableException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class AppletServer extends Webserver
/*     */ {
/*     */   private StubGenerator stubGen;
/*     */   private Hashtable exportedNames;
/*     */   private Vector exportedObjects;
/*  42 */   private static final byte[] okHeader = "HTTP/1.0 200 OK\r\n\r\n".getBytes();
/*     */ 
/*     */   public AppletServer(String port)
/*     */     throws IOException, NotFoundException, CannotCompileException
/*     */   {
/*  53 */     this(Integer.parseInt(port));
/*     */   }
/*     */ 
/*     */   public AppletServer(int port)
/*     */     throws IOException, NotFoundException, CannotCompileException
/*     */   {
/*  64 */     this(ClassPool.getDefault(), new StubGenerator(), port);
/*     */   }
/*     */ 
/*     */   public AppletServer(int port, ClassPool src)
/*     */     throws IOException, NotFoundException, CannotCompileException
/*     */   {
/*  76 */     this(new ClassPool(src), new StubGenerator(), port);
/*     */   }
/*     */ 
/*     */   private AppletServer(ClassPool loader, StubGenerator gen, int port)
/*     */     throws IOException, NotFoundException, CannotCompileException
/*     */   {
/*  82 */     super(port);
/*  83 */     this.exportedNames = new Hashtable();
/*  84 */     this.exportedObjects = new Vector();
/*  85 */     this.stubGen = gen;
/*  86 */     addTranslator(loader, gen);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  93 */     super.run();
/*     */   }
/*     */ 
/*     */   public synchronized int exportObject(String name, Object obj)
/*     */     throws CannotCompileException
/*     */   {
/* 111 */     Class clazz = obj.getClass();
/* 112 */     ExportedObject eo = new ExportedObject();
/* 113 */     eo.object = obj;
/* 114 */     eo.methods = clazz.getMethods();
/* 115 */     this.exportedObjects.addElement(eo);
/* 116 */     eo.identifier = (this.exportedObjects.size() - 1);
/* 117 */     if (name != null)
/* 118 */       this.exportedNames.put(name, eo);
/*     */     try
/*     */     {
/* 121 */       this.stubGen.makeProxyClass(clazz);
/*     */     }
/*     */     catch (NotFoundException e) {
/* 124 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/* 127 */     return eo.identifier;
/*     */   }
/*     */ 
/*     */   public void doReply(InputStream in, OutputStream out, String cmd)
/*     */     throws IOException, BadHttpRequest
/*     */   {
/* 136 */     if (cmd.startsWith("POST /rmi "))
/* 137 */       processRMI(in, out);
/* 138 */     else if (cmd.startsWith("POST /lookup "))
/* 139 */       lookupName(cmd, in, out);
/*     */     else
/* 141 */       super.doReply(in, out, cmd);
/*     */   }
/*     */ 
/*     */   private void processRMI(InputStream ins, OutputStream outs)
/*     */     throws IOException
/*     */   {
/* 147 */     ObjectInputStream in = new ObjectInputStream(ins);
/*     */ 
/* 149 */     int objectId = in.readInt();
/* 150 */     int methodId = in.readInt();
/* 151 */     Exception err = null;
/* 152 */     Object rvalue = null;
/*     */     try {
/* 154 */       ExportedObject eo = (ExportedObject)this.exportedObjects.elementAt(objectId);
/*     */ 
/* 156 */       Object[] args = readParameters(in);
/* 157 */       rvalue = convertRvalue(eo.methods[methodId].invoke(eo.object, args));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 161 */       err = e;
/* 162 */       logging2(e.toString());
/*     */     }
/*     */ 
/* 165 */     outs.write(okHeader);
/* 166 */     ObjectOutputStream out = new ObjectOutputStream(outs);
/* 167 */     if (err != null) {
/* 168 */       out.writeBoolean(false);
/* 169 */       out.writeUTF(err.toString());
/*     */     }
/*     */     else {
/*     */       try {
/* 173 */         out.writeBoolean(true);
/* 174 */         out.writeObject(rvalue);
/*     */       }
/*     */       catch (NotSerializableException e) {
/* 177 */         logging2(e.toString());
/*     */       }
/*     */       catch (InvalidClassException e) {
/* 180 */         logging2(e.toString());
/*     */       }
/*     */     }
/* 183 */     out.flush();
/* 184 */     out.close();
/* 185 */     in.close();
/*     */   }
/*     */ 
/*     */   private Object[] readParameters(ObjectInputStream in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 191 */     int n = in.readInt();
/* 192 */     Object[] args = new Object[n];
/* 193 */     for (int i = 0; i < n; i++) {
/* 194 */       Object a = in.readObject();
/* 195 */       if ((a instanceof RemoteRef)) {
/* 196 */         RemoteRef ref = (RemoteRef)a;
/* 197 */         ExportedObject eo = (ExportedObject)this.exportedObjects.elementAt(ref.oid);
/*     */ 
/* 199 */         a = eo.object;
/*     */       }
/*     */ 
/* 202 */       args[i] = a;
/*     */     }
/*     */ 
/* 205 */     return args;
/*     */   }
/*     */ 
/*     */   private Object convertRvalue(Object rvalue)
/*     */     throws CannotCompileException
/*     */   {
/* 211 */     if (rvalue == null) {
/* 212 */       return null;
/*     */     }
/* 214 */     String classname = rvalue.getClass().getName();
/* 215 */     if (this.stubGen.isProxyClass(classname)) {
/* 216 */       return new RemoteRef(exportObject(null, rvalue), classname);
/*     */     }
/* 218 */     return rvalue;
/*     */   }
/*     */ 
/*     */   private void lookupName(String cmd, InputStream ins, OutputStream outs)
/*     */     throws IOException
/*     */   {
/* 224 */     ObjectInputStream in = new ObjectInputStream(ins);
/* 225 */     String name = DataInputStream.readUTF(in);
/* 226 */     ExportedObject found = (ExportedObject)this.exportedNames.get(name);
/* 227 */     outs.write(okHeader);
/* 228 */     ObjectOutputStream out = new ObjectOutputStream(outs);
/* 229 */     if (found == null) {
/* 230 */       logging2(name + "not found.");
/* 231 */       out.writeInt(-1);
/* 232 */       out.writeUTF("error");
/*     */     }
/*     */     else {
/* 235 */       logging2(name);
/* 236 */       out.writeInt(found.identifier);
/* 237 */       out.writeUTF(found.object.getClass().getName());
/*     */     }
/*     */ 
/* 240 */     out.flush();
/* 241 */     out.close();
/* 242 */     in.close();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.rmi.AppletServer
 * JD-Core Version:    0.6.2
 */