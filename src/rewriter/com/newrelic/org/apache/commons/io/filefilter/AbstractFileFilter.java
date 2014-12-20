/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public abstract class AbstractFileFilter
/*    */   implements IOFileFilter
/*    */ {
/*    */   public boolean accept(File file)
/*    */   {
/* 40 */     return accept(file.getParentFile(), file.getName());
/*    */   }
/*    */ 
/*    */   public boolean accept(File dir, String name)
/*    */   {
/* 51 */     return accept(new File(dir, name));
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 61 */     return getClass().getSimpleName();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.AbstractFileFilter
 * JD-Core Version:    0.6.2
 */