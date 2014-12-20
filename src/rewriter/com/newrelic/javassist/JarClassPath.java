/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ 
/*     */ final class JarClassPath
/*     */   implements ClassPath
/*     */ {
/*     */   JarFile jarfile;
/*     */   String jarfileURL;
/*     */ 
/*     */   JarClassPath(String pathname)
/*     */     throws NotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 128 */       this.jarfile = new JarFile(pathname);
/* 129 */       this.jarfileURL = new File(pathname).getCanonicalFile().toURI().toURL().toString();
/*     */ 
/* 131 */       return;
/*     */     } catch (IOException e) {
/*     */     }
/* 134 */     throw new NotFoundException(pathname);
/*     */   }
/*     */ 
/*     */   public InputStream openClassfile(String classname) throws NotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 141 */       String jarname = classname.replace('.', '/') + ".class";
/* 142 */       JarEntry je = this.jarfile.getJarEntry(jarname);
/* 143 */       if (je != null) {
/* 144 */         return this.jarfile.getInputStream(je);
/*     */       }
/* 146 */       return null;
/*     */     } catch (IOException e) {
/*     */     }
/* 149 */     throw new NotFoundException("broken jar file?: " + this.jarfile.getName());
/*     */   }
/*     */ 
/*     */   public URL find(String classname)
/*     */   {
/* 154 */     String jarname = classname.replace('.', '/') + ".class";
/* 155 */     JarEntry je = this.jarfile.getJarEntry(jarname);
/* 156 */     if (je != null)
/*     */       try {
/* 158 */         return new URL("jar:" + this.jarfileURL + "!/" + jarname);
/*     */       }
/*     */       catch (MalformedURLException e) {
/*     */       }
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   public void close() {
/*     */     try {
/* 167 */       this.jarfile.close();
/* 168 */       this.jarfile = null;
/*     */     } catch (IOException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 174 */     return this.jarfile == null ? "<null>" : this.jarfile.toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.JarClassPath
 * JD-Core Version:    0.6.2
 */