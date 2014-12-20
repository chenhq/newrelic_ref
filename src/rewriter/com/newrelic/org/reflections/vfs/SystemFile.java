/*    */ package com.newrelic.org.reflections.vfs;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class SystemFile
/*    */   implements Vfs.File
/*    */ {
/*    */   private final SystemDir root;
/*    */   private final File file;
/*    */ 
/*    */   public SystemFile(SystemDir root, File file)
/*    */   {
/* 13 */     this.root = root;
/* 14 */     this.file = file;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 18 */     return this.file.getName();
/*    */   }
/*    */ 
/*    */   public String getRelativePath() {
/* 22 */     String filepath = this.file.getPath().replace("\\", "/");
/* 23 */     if (filepath.startsWith(this.root.getPath())) {
/* 24 */       return filepath.substring(this.root.getPath().length() + 1);
/*    */     }
/*    */ 
/* 27 */     return null;
/*    */   }
/*    */ 
/*    */   public InputStream openInputStream() {
/*    */     try {
/* 32 */       return new FileInputStream(this.file);
/*    */     } catch (FileNotFoundException e) {
/* 34 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 40 */     return this.file.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.vfs.SystemFile
 * JD-Core Version:    0.6.2
 */