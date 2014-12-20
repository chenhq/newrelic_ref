/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class FalseFileFilter
/*    */   implements IOFileFilter, Serializable
/*    */ {
/* 36 */   public static final IOFileFilter FALSE = new FalseFileFilter();
/*    */ 
/* 43 */   public static final IOFileFilter INSTANCE = FALSE;
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 58 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean accept(File dir, String name)
/*    */   {
/* 69 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.FalseFileFilter
 * JD-Core Version:    0.6.2
 */