/*    */ package com.newrelic.org.reflections.vfs;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.AbstractIterator;
/*    */ import com.newrelic.com.google.common.collect.Lists;
/*    */ import java.io.File;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Stack;
/*    */ 
/*    */ public class SystemDir
/*    */   implements Vfs.Dir
/*    */ {
/*    */   private final File file;
/*    */ 
/*    */   public SystemDir(File file)
/*    */   {
/* 19 */     if ((file == null) || (!file.exists()) || (!file.isDirectory()) || (!file.canRead())) {
/* 20 */       throw new RuntimeException("cannot use dir " + file);
/*    */     }
/*    */ 
/* 23 */     this.file = file;
/*    */   }
/*    */ 
/*    */   public String getPath() {
/* 27 */     return this.file.getPath().replace("\\", "/");
/*    */   }
/*    */ 
/*    */   public Iterable<Vfs.File> getFiles() {
/* 31 */     return new Iterable() {
/*    */       public Iterator<Vfs.File> iterator() {
/* 33 */         return new AbstractIterator() {
/* 34 */           final Stack<File> stack = new Stack();
/*    */ 
/*    */           protected Vfs.File computeNext()
/*    */           {
/* 38 */             while (!this.stack.isEmpty()) {
/* 39 */               File file = (File)this.stack.pop();
/* 40 */               if (file.isDirectory())
/* 41 */                 this.stack.addAll(SystemDir.listFiles(file));
/*    */               else {
/* 43 */                 return new SystemFile(SystemDir.this, file);
/*    */               }
/*    */             }
/*    */ 
/* 47 */             return (Vfs.File)endOfData();
/*    */           }
/*    */         };
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   private static List<File> listFiles(File file) {
/* 55 */     File[] files = file.listFiles();
/*    */ 
/* 57 */     if (files != null) {
/* 58 */       return Lists.newArrayList(files);
/*    */     }
/* 60 */     return Lists.newArrayList();
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 68 */     return this.file.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.vfs.SystemDir
 * JD-Core Version:    0.6.2
 */