/*    */ package com.newrelic.org.apache.commons.io.comparator;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ 
/*    */ class ReverseComparator extends AbstractFileComparator
/*    */   implements Serializable
/*    */ {
/*    */   private final Comparator<File> delegate;
/*    */ 
/*    */   public ReverseComparator(Comparator<File> delegate)
/*    */   {
/* 40 */     if (delegate == null) {
/* 41 */       throw new IllegalArgumentException("Delegate comparator is missing");
/*    */     }
/* 43 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */   public int compare(File file1, File file2)
/*    */   {
/* 55 */     return this.delegate.compare(file2, file1);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 65 */     return super.toString() + "[" + this.delegate.toString() + "]";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.comparator.ReverseComparator
 * JD-Core Version:    0.6.2
 */