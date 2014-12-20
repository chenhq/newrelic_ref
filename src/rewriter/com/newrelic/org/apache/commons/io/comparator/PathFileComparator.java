/*     */ package com.newrelic.org.apache.commons.io.comparator;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.IOCase;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public class PathFileComparator extends AbstractFileComparator
/*     */   implements Serializable
/*     */ {
/*  55 */   public static final Comparator<File> PATH_COMPARATOR = new PathFileComparator();
/*     */ 
/*  58 */   public static final Comparator<File> PATH_REVERSE = new ReverseComparator(PATH_COMPARATOR);
/*     */ 
/*  61 */   public static final Comparator<File> PATH_INSENSITIVE_COMPARATOR = new PathFileComparator(IOCase.INSENSITIVE);
/*     */ 
/*  64 */   public static final Comparator<File> PATH_INSENSITIVE_REVERSE = new ReverseComparator(PATH_INSENSITIVE_COMPARATOR);
/*     */ 
/*  67 */   public static final Comparator<File> PATH_SYSTEM_COMPARATOR = new PathFileComparator(IOCase.SYSTEM);
/*     */ 
/*  70 */   public static final Comparator<File> PATH_SYSTEM_REVERSE = new ReverseComparator(PATH_SYSTEM_COMPARATOR);
/*     */   private final IOCase caseSensitivity;
/*     */ 
/*     */   public PathFileComparator()
/*     */   {
/*  79 */     this.caseSensitivity = IOCase.SENSITIVE;
/*     */   }
/*     */ 
/*     */   public PathFileComparator(IOCase caseSensitivity)
/*     */   {
/*  88 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*     */   }
/*     */ 
/*     */   public int compare(File file1, File file2)
/*     */   {
/* 103 */     return this.caseSensitivity.checkCompareTo(file1.getPath(), file2.getPath());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 113 */     return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.comparator.PathFileComparator
 * JD-Core Version:    0.6.2
 */