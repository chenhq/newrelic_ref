/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ 
/*     */ public class URLClassPath
/*     */   implements ClassPath
/*     */ {
/*     */   protected String hostname;
/*     */   protected int port;
/*     */   protected String directory;
/*     */   protected String packageName;
/*     */ 
/*     */   public URLClassPath(String host, int port, String directory, String packageName)
/*     */   {
/*  61 */     this.hostname = host;
/*  62 */     this.port = port;
/*  63 */     this.directory = directory;
/*  64 */     this.packageName = packageName;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  68 */     return this.hostname + ":" + this.port + this.directory;
/*     */   }
/*     */ 
/*     */   public InputStream openClassfile(String classname)
/*     */   {
/*     */     try
/*     */     {
/*  78 */       URLConnection con = openClassfile0(classname);
/*  79 */       if (con != null)
/*  80 */         return con.getInputStream();
/*     */     } catch (IOException e) {
/*     */     }
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */   private URLConnection openClassfile0(String classname) throws IOException {
/*  87 */     if ((this.packageName == null) || (classname.startsWith(this.packageName))) {
/*  88 */       String jarname = this.directory + classname.replace('.', '/') + ".class";
/*     */ 
/*  90 */       return fetchClass0(this.hostname, this.port, jarname);
/*     */     }
/*     */ 
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */   public URL find(String classname)
/*     */   {
/*     */     try
/*     */     {
/* 103 */       URLConnection con = openClassfile0(classname);
/* 104 */       InputStream is = con.getInputStream();
/* 105 */       if (is != null) {
/* 106 */         is.close();
/* 107 */         return con.getURL();
/*     */       }
/*     */     } catch (IOException e) {
/*     */     }
/* 111 */     return null;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static byte[] fetchClass(String host, int port, String directory, String classname)
/*     */     throws IOException
/*     */   {
/* 134 */     URLConnection con = fetchClass0(host, port, directory + classname.replace('.', '/') + ".class");
/*     */ 
/* 136 */     int size = con.getContentLength();
/* 137 */     InputStream s = con.getInputStream();
/*     */     byte[] b;
/*     */     try
/*     */     {
/*     */       byte[] b;
/* 139 */       if (size <= 0) {
/* 140 */         b = ClassPoolTail.readStream(s);
/*     */       } else {
/* 142 */         b = new byte[size];
/* 143 */         int len = 0;
/*     */         do {
/* 145 */           int n = s.read(b, len, size - len);
/* 146 */           if (n < 0) {
/* 147 */             throw new IOException("the stream was closed: " + classname);
/*     */           }
/*     */ 
/* 150 */           len += n;
/* 151 */         }while (len < size);
/*     */       }
/*     */     }
/*     */     finally {
/* 155 */       s.close();
/*     */     }
/*     */ 
/* 158 */     return b;
/*     */   }
/*     */ 
/*     */   private static URLConnection fetchClass0(String host, int port, String filename)
/*     */     throws IOException
/*     */   {
/*     */     URL url;
/*     */     try
/*     */     {
/* 167 */       url = new URL("http", host, port, filename);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 171 */       throw new IOException("invalid URL?");
/*     */     }
/*     */ 
/* 174 */     URLConnection con = url.openConnection();
/* 175 */     con.connect();
/* 176 */     return con;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.URLClassPath
 * JD-Core Version:    0.6.2
 */