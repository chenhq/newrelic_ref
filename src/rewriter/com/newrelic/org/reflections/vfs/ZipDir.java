/*    */ package com.newrelic.org.reflections.vfs;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.AbstractIterator;
/*    */ import java.io.IOException;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Iterator;
/*    */ import java.util.jar.JarFile;
/*    */ import java.util.zip.ZipEntry;
/*    */ 
/*    */ public class ZipDir
/*    */   implements Vfs.Dir
/*    */ {
/*    */   final java.util.zip.ZipFile jarFile;
/*    */ 
/*    */   public ZipDir(JarFile jarFile)
/*    */   {
/* 20 */     this.jarFile = jarFile;
/*    */   }
/*    */ 
/*    */   public String getPath() {
/* 24 */     return this.jarFile.getName();
/*    */   }
/*    */ 
/*    */   public Iterable<Vfs.File> getFiles() {
/* 28 */     return new Iterable() {
/*    */       public Iterator<Vfs.File> iterator() {
/* 30 */         return new AbstractIterator() {
/* 31 */           final Enumeration<? extends ZipEntry> entries = ZipDir.this.jarFile.entries();
/*    */ 
/*    */           protected Vfs.File computeNext() {
/* 34 */             while (this.entries.hasMoreElements()) {
/* 35 */               ZipEntry entry = (ZipEntry)this.entries.nextElement();
/* 36 */               if (!entry.isDirectory()) {
/* 37 */                 return new ZipFile(ZipDir.this, entry);
/*    */               }
/*    */             }
/*    */ 
/* 41 */             return (Vfs.File)endOfData();
/*    */           } } ;
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public void close() {
/*    */     try {
/* 49 */       this.jarFile.close(); } catch (IOException e) { e.printStackTrace(); }
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 54 */     return this.jarFile.getName();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.vfs.ZipDir
 * JD-Core Version:    0.6.2
 */