/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URI;
/*    */ import java.net.URL;
/*    */ 
/*    */ final class DirClassPath
/*    */   implements ClassPath
/*    */ {
/*    */   String directory;
/*    */ 
/*    */   DirClassPath(String dirName)
/*    */   {
/* 38 */     this.directory = dirName;
/*    */   }
/*    */ 
/*    */   public InputStream openClassfile(String classname) {
/*    */     try {
/* 43 */       char sep = File.separatorChar;
/* 44 */       String filename = this.directory + sep + classname.replace('.', sep) + ".class";
/*    */ 
/* 46 */       return new FileInputStream(filename.toString());
/*    */     } catch (FileNotFoundException e) {
/*    */     } catch (SecurityException e) {
/*    */     }
/* 50 */     return null;
/*    */   }
/*    */ 
/*    */   public URL find(String classname) {
/* 54 */     char sep = File.separatorChar;
/* 55 */     String filename = this.directory + sep + classname.replace('.', sep) + ".class";
/*    */ 
/* 57 */     File f = new File(filename);
/* 58 */     if (f.exists())
/*    */       try {
/* 60 */         return f.getCanonicalFile().toURI().toURL();
/*    */       } catch (MalformedURLException e) {
/*    */       }
/*    */       catch (IOException e) {
/*    */       }
/* 65 */     return null;
/*    */   }
/*    */   public void close() {
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 71 */     return this.directory;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.DirClassPath
 * JD-Core Version:    0.6.2
 */