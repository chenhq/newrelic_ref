/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class NotFileFilter extends AbstractFileFilter
/*    */   implements Serializable
/*    */ {
/*    */   private final IOFileFilter filter;
/*    */ 
/*    */   public NotFileFilter(IOFileFilter filter)
/*    */   {
/* 41 */     if (filter == null) {
/* 42 */       throw new IllegalArgumentException("The filter must not be null");
/*    */     }
/* 44 */     this.filter = filter;
/*    */   }
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 55 */     return !this.filter.accept(file);
/*    */   }
/*    */ 
/*    */   public boolean accept(File file, String name)
/*    */   {
/* 67 */     return !this.filter.accept(file, name);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 77 */     return super.toString() + "(" + this.filter.toString() + ")";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.NotFileFilter
 * JD-Core Version:    0.6.2
 */