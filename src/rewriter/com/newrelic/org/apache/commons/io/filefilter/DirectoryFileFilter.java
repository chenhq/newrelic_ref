/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class DirectoryFileFilter extends AbstractFileFilter
/*    */   implements Serializable
/*    */ {
/* 47 */   public static final IOFileFilter DIRECTORY = new DirectoryFileFilter();
/*    */ 
/* 54 */   public static final IOFileFilter INSTANCE = DIRECTORY;
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 70 */     return file.isDirectory();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.DirectoryFileFilter
 * JD-Core Version:    0.6.2
 */