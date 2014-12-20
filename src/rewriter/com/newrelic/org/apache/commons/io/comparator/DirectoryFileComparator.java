/*    */ package com.newrelic.org.apache.commons.io.comparator;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ 
/*    */ public class DirectoryFileComparator extends AbstractFileComparator
/*    */   implements Serializable
/*    */ {
/* 49 */   public static final Comparator<File> DIRECTORY_COMPARATOR = new DirectoryFileComparator();
/*    */ 
/* 52 */   public static final Comparator<File> DIRECTORY_REVERSE = new ReverseComparator(DIRECTORY_COMPARATOR);
/*    */ 
/*    */   public int compare(File file1, File file2)
/*    */   {
/* 63 */     return getType(file1) - getType(file2);
/*    */   }
/*    */ 
/*    */   private int getType(File file)
/*    */   {
/* 73 */     if (file.isDirectory()) {
/* 74 */       return 1;
/*    */     }
/* 76 */     return 2;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.comparator.DirectoryFileComparator
 * JD-Core Version:    0.6.2
 */