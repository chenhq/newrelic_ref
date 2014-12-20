/*    */ package com.newrelic.org.apache.commons.io.comparator;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class DefaultFileComparator extends AbstractFileComparator
/*    */   implements Serializable
/*    */ {
/* 50 */   public static final Comparator<File> DEFAULT_COMPARATOR = new DefaultFileComparator();
/*    */ 
/* 53 */   public static final Comparator<File> DEFAULT_REVERSE = new ReverseComparator(DEFAULT_COMPARATOR);
/*    */ 
/*    */   public int compare(File file1, File file2)
/*    */   {
/* 64 */     return file1.compareTo(file2);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.comparator.DefaultFileComparator
 * JD-Core Version:    0.6.2
 */