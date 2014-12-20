/*    */ package com.newrelic.org.reflections.vfs;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.zip.ZipEntry;
/*    */ 
/*    */ public class ZipFile
/*    */   implements Vfs.File
/*    */ {
/*    */   private final ZipDir root;
/*    */   private final ZipEntry entry;
/*    */ 
/*    */   public ZipFile(ZipDir root, ZipEntry entry)
/*    */   {
/* 13 */     this.root = root;
/* 14 */     this.entry = entry;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 18 */     String name = this.entry.getName();
/* 19 */     return name.substring(name.lastIndexOf("/") + 1);
/*    */   }
/*    */ 
/*    */   public String getRelativePath() {
/* 23 */     return this.entry.getName();
/*    */   }
/*    */ 
/*    */   public InputStream openInputStream() throws IOException {
/* 27 */     return this.root.jarFile.getInputStream(this.entry);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 32 */     return this.root.getPath() + "!" + File.separatorChar + this.entry.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.vfs.ZipFile
 * JD-Core Version:    0.6.2
 */