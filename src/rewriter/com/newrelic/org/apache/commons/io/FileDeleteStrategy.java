/*     */ package com.newrelic.org.apache.commons.io;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ 
/*     */ public class FileDeleteStrategy
/*     */ {
/*  40 */   public static final FileDeleteStrategy NORMAL = new FileDeleteStrategy("Normal");
/*     */ 
/*  45 */   public static final FileDeleteStrategy FORCE = new ForceFileDeleteStrategy();
/*     */   private final String name;
/*     */ 
/*     */   protected FileDeleteStrategy(String name)
/*     */   {
/*  57 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public boolean deleteQuietly(File fileToDelete)
/*     */   {
/*  72 */     if ((fileToDelete == null) || (!fileToDelete.exists()))
/*  73 */       return true;
/*     */     try
/*     */     {
/*  76 */       return doDelete(fileToDelete); } catch (IOException ex) {
/*     */     }
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   public void delete(File fileToDelete)
/*     */     throws IOException
/*     */   {
/*  93 */     if ((fileToDelete.exists()) && (!doDelete(fileToDelete)))
/*  94 */       throw new IOException("Deletion failed: " + fileToDelete);
/*     */   }
/*     */ 
/*     */   protected boolean doDelete(File fileToDelete)
/*     */     throws IOException
/*     */   {
/* 115 */     return fileToDelete.delete();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 126 */     return "FileDeleteStrategy[" + this.name + "]";
/*     */   }
/*     */ 
/*     */   static class ForceFileDeleteStrategy extends FileDeleteStrategy
/*     */   {
/*     */     ForceFileDeleteStrategy()
/*     */     {
/* 136 */       super();
/*     */     }
/*     */ 
/*     */     protected boolean doDelete(File fileToDelete)
/*     */       throws IOException
/*     */     {
/* 152 */       FileUtils.forceDelete(fileToDelete);
/* 153 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.FileDeleteStrategy
 * JD-Core Version:    0.6.2
 */