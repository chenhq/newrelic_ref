/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ 
/*     */ final class JarDirClassPath
/*     */   implements ClassPath
/*     */ {
/*     */   JarClassPath[] jars;
/*     */ 
/*     */   JarDirClassPath(String dirName)
/*     */     throws NotFoundException
/*     */   {
/*  79 */     File[] files = new File(dirName).listFiles(new FilenameFilter() {
/*     */       public boolean accept(File dir, String name) {
/*  81 */         name = name.toLowerCase();
/*  82 */         return (name.endsWith(".jar")) || (name.endsWith(".zip"));
/*     */       }
/*     */     });
/*  86 */     if (files != null) {
/*  87 */       this.jars = new JarClassPath[files.length];
/*  88 */       for (int i = 0; i < files.length; i++)
/*  89 */         this.jars[i] = new JarClassPath(files[i].getPath());
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream openClassfile(String classname) throws NotFoundException {
/*  94 */     if (this.jars != null) {
/*  95 */       for (int i = 0; i < this.jars.length; i++) {
/*  96 */         InputStream is = this.jars[i].openClassfile(classname);
/*  97 */         if (is != null)
/*  98 */           return is;
/*     */       }
/*     */     }
/* 101 */     return null;
/*     */   }
/*     */ 
/*     */   public URL find(String classname) {
/* 105 */     if (this.jars != null) {
/* 106 */       for (int i = 0; i < this.jars.length; i++) {
/* 107 */         URL url = this.jars[i].find(classname);
/* 108 */         if (url != null)
/* 109 */           return url;
/*     */       }
/*     */     }
/* 112 */     return null;
/*     */   }
/*     */ 
/*     */   public void close() {
/* 116 */     if (this.jars != null)
/* 117 */       for (int i = 0; i < this.jars.length; i++)
/* 118 */         this.jars[i].close();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.JarDirClassPath
 * JD-Core Version:    0.6.2
 */