/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class TrueFileFilter
/*    */   implements IOFileFilter, Serializable
/*    */ {
/* 35 */   public static final IOFileFilter TRUE = new TrueFileFilter();
/*    */ 
/* 42 */   public static final IOFileFilter INSTANCE = TRUE;
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 57 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean accept(File dir, String name)
/*    */   {
/* 68 */     return true;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.TrueFileFilter
 * JD-Core Version:    0.6.2
 */