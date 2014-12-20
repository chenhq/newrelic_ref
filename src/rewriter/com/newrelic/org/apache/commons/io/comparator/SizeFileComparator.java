/*     */ package com.newrelic.org.apache.commons.io.comparator;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.FileUtils;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public class SizeFileComparator extends AbstractFileComparator
/*     */   implements Serializable
/*     */ {
/*  55 */   public static final Comparator<File> SIZE_COMPARATOR = new SizeFileComparator();
/*     */ 
/*  58 */   public static final Comparator<File> SIZE_REVERSE = new ReverseComparator(SIZE_COMPARATOR);
/*     */ 
/*  64 */   public static final Comparator<File> SIZE_SUMDIR_COMPARATOR = new SizeFileComparator(true);
/*     */ 
/*  70 */   public static final Comparator<File> SIZE_SUMDIR_REVERSE = new ReverseComparator(SIZE_SUMDIR_COMPARATOR);
/*     */   private final boolean sumDirectoryContents;
/*     */ 
/*     */   public SizeFileComparator()
/*     */   {
/*  79 */     this.sumDirectoryContents = false;
/*     */   }
/*     */ 
/*     */   public SizeFileComparator(boolean sumDirectoryContents)
/*     */   {
/*  94 */     this.sumDirectoryContents = sumDirectoryContents;
/*     */   }
/*     */ 
/*     */   public int compare(File file1, File file2)
/*     */   {
/* 109 */     long size1 = 0L;
/* 110 */     if (file1.isDirectory())
/* 111 */       size1 = (this.sumDirectoryContents) && (file1.exists()) ? FileUtils.sizeOfDirectory(file1) : 0L;
/*     */     else {
/* 113 */       size1 = file1.length();
/*     */     }
/* 115 */     long size2 = 0L;
/* 116 */     if (file2.isDirectory())
/* 117 */       size2 = (this.sumDirectoryContents) && (file2.exists()) ? FileUtils.sizeOfDirectory(file2) : 0L;
/*     */     else {
/* 119 */       size2 = file2.length();
/*     */     }
/* 121 */     long result = size1 - size2;
/* 122 */     if (result < 0L)
/* 123 */       return -1;
/* 124 */     if (result > 0L) {
/* 125 */       return 1;
/*     */     }
/* 127 */     return 0;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 138 */     return super.toString() + "[sumDirectoryContents=" + this.sumDirectoryContents + "]";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.comparator.SizeFileComparator
 * JD-Core Version:    0.6.2
 */