/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.org.reflections.vfs.Vfs.File;
/*    */ 
/*    */ public class ResourcesScanner extends AbstractScanner
/*    */ {
/*    */   public boolean acceptsInput(String file)
/*    */   {
/*  9 */     return !file.endsWith(".class");
/*    */   }
/*    */ 
/*    */   public void scan(Vfs.File file) {
/* 13 */     getStore().put(file.getName(), file.getRelativePath());
/*    */   }
/*    */ 
/*    */   public void scan(Object cls) {
/* 17 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.ResourcesScanner
 * JD-Core Version:    0.6.2
 */