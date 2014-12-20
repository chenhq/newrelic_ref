/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class EmptyFileFilter extends AbstractFileFilter
/*    */   implements Serializable
/*    */ {
/* 57 */   public static final IOFileFilter EMPTY = new EmptyFileFilter();
/*    */ 
/* 60 */   public static final IOFileFilter NOT_EMPTY = new NotFileFilter(EMPTY);
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 77 */     if (file.isDirectory()) {
/* 78 */       File[] files = file.listFiles();
/* 79 */       return (files == null) || (files.length == 0);
/*    */     }
/* 81 */     return file.length() == 0L;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.EmptyFileFilter
 * JD-Core Version:    0.6.2
 */