/*    */ package com.newrelic.org.apache.commons.io.filefilter;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class CanReadFileFilter extends AbstractFileFilter
/*    */   implements Serializable
/*    */ {
/* 66 */   public static final IOFileFilter CAN_READ = new CanReadFileFilter();
/*    */ 
/* 69 */   public static final IOFileFilter CANNOT_READ = new NotFileFilter(CAN_READ);
/*    */ 
/* 72 */   public static final IOFileFilter READ_ONLY = new AndFileFilter(CAN_READ, CanWriteFileFilter.CANNOT_WRITE);
/*    */ 
/*    */   public boolean accept(File file)
/*    */   {
/* 90 */     return file.canRead();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.CanReadFileFilter
 * JD-Core Version:    0.6.2
 */