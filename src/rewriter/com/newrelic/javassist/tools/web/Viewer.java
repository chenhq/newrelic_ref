/*     */ package com.newrelic.javassist.tools.web;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ 
/*     */ public class Viewer extends ClassLoader
/*     */ {
/*     */   private String server;
/*     */   private int port;
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Throwable
/*     */   {
/*  58 */     if (args.length >= 3) {
/*  59 */       Viewer cl = new Viewer(args[0], Integer.parseInt(args[1]));
/*  60 */       String[] args2 = new String[args.length - 3];
/*  61 */       System.arraycopy(args, 3, args2, 0, args.length - 3);
/*  62 */       cl.run(args[2], args2);
/*     */     }
/*     */     else {
/*  65 */       System.err.println("Usage: java javassist.tools.web.Viewer <host> <port> class [args ...]");
/*     */     }
/*     */   }
/*     */ 
/*     */   public Viewer(String host, int p)
/*     */   {
/*  76 */     this.server = host;
/*  77 */     this.port = p;
/*     */   }
/*     */ 
/*     */   public String getServer()
/*     */   {
/*  83 */     return this.server;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  88 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void run(String classname, String[] args)
/*     */     throws Throwable
/*     */   {
/*  99 */     Class c = loadClass(classname);
/*     */     try {
/* 101 */       c.getDeclaredMethod("main", new Class[] { new String[0].getClass() }).invoke(null, new Object[] { args });
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 105 */       throw e.getTargetException();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized Class loadClass(String name, boolean resolve)
/*     */     throws ClassNotFoundException
/*     */   {
/* 115 */     Class c = findLoadedClass(name);
/* 116 */     if (c == null) {
/* 117 */       c = findClass(name);
/*     */     }
/* 119 */     if (c == null) {
/* 120 */       throw new ClassNotFoundException(name);
/*     */     }
/* 122 */     if (resolve) {
/* 123 */       resolveClass(c);
/*     */     }
/* 125 */     return c;
/*     */   }
/*     */ 
/*     */   protected Class findClass(String name)
/*     */     throws ClassNotFoundException
/*     */   {
/* 139 */     Class c = null;
/* 140 */     if ((name.startsWith("java.")) || (name.startsWith("javax.")) || (name.equals("com.newrelic.javassist.tools.web.Viewer")))
/*     */     {
/* 142 */       c = findSystemClass(name);
/*     */     }
/* 144 */     if (c == null)
/*     */       try {
/* 146 */         byte[] b = fetchClass(name);
/* 147 */         if (b != null)
/* 148 */           c = defineClass(name, b, 0, b.length);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/* 153 */     return c;
/*     */   }
/*     */ 
/*     */   protected byte[] fetchClass(String classname)
/*     */     throws Exception
/*     */   {
/* 163 */     URL url = new URL("http", this.server, this.port, "/" + classname.replace('.', '/') + ".class");
/*     */ 
/* 165 */     URLConnection con = url.openConnection();
/* 166 */     con.connect();
/* 167 */     int size = con.getContentLength();
/* 168 */     InputStream s = con.getInputStream();
/*     */     byte[] b;
/*     */     byte[] b;
/* 169 */     if (size <= 0) {
/* 170 */       b = readStream(s);
/*     */     } else {
/* 172 */       b = new byte[size];
/* 173 */       int len = 0;
/*     */       do {
/* 175 */         int n = s.read(b, len, size - len);
/* 176 */         if (n < 0) {
/* 177 */           s.close();
/* 178 */           throw new IOException("the stream was closed: " + classname);
/*     */         }
/*     */ 
/* 181 */         len += n;
/* 182 */       }while (len < size);
/*     */     }
/*     */ 
/* 185 */     s.close();
/* 186 */     return b;
/*     */   }
/*     */ 
/*     */   private byte[] readStream(InputStream fin) throws IOException {
/* 190 */     byte[] buf = new byte[4096];
/* 191 */     int size = 0;
/* 192 */     int len = 0;
/*     */     do {
/* 194 */       size += len;
/* 195 */       if (buf.length - size <= 0) {
/* 196 */         byte[] newbuf = new byte[buf.length * 2];
/* 197 */         System.arraycopy(buf, 0, newbuf, 0, size);
/* 198 */         buf = newbuf;
/*     */       }
/*     */ 
/* 201 */       len = fin.read(buf, size, buf.length - size);
/* 202 */     }while (len >= 0);
/*     */ 
/* 204 */     byte[] result = new byte[size];
/* 205 */     System.arraycopy(buf, 0, result, 0, size);
/* 206 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.web.Viewer
 * JD-Core Version:    0.6.2
 */