/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class FileFileFilter extends AbstractFileFilter
/*    */   implements Serializable
/*    */ {
/* 43 */   public static final IOFileFilter FILE = new FileFileFilter();
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 59 */     return file.isFile();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.FileFileFilter
 * JD-Core Version:    0.6.2
 */