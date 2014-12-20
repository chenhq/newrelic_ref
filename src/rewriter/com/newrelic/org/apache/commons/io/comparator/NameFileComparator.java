/*     */ package com.newrelic.org.apache.commons.io.comparator;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.IOCase;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public class NameFileComparator extends AbstractFileComparator
/*     */   implements Serializable
/*     */ {
/*  55 */   public static final Comparator<File> NAME_COMPARATOR = new NameFileComparator();
/*     */ 
/*  58 */   public static final Comparator<File> NAME_REVERSE = new ReverseComparator(NAME_COMPARATOR);
/*     */ 
/*  61 */   public static final Comparator<File> NAME_INSENSITIVE_COMPARATOR = new NameFileComparator(IOCase.INSENSITIVE);
/*     */ 
/*  64 */   public static final Comparator<File> NAME_INSENSITIVE_REVERSE = new ReverseComparator(NAME_INSENSITIVE_COMPARATOR);
/*     */ 
/*  67 */   public static final Comparator<File> NAME_SYSTEM_COMPARATOR = new NameFileComparator(IOCase.SYSTEM);
/*     */ 
/*  70 */   public static final Comparator<File> NAME_SYSTEM_REVERSE = new ReverseComparator(NAME_SYSTEM_COMPARATOR);
/*     */   private final IOCase caseSensitivity;
/*     */ 
/*     */   public NameFileComparator()
/*     */   {
/*  79 */     this.caseSensitivity = IOCase.SENSITIVE;
/*     */   }
/*     */ 
/*     */   public NameFileComparator(IOCase caseSensitivity)
/*     */   {
/*  88 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*     */   }
/*     */ 
/*     */   public int compare(File file1, File file2)
/*     */   {
/* 102 */     return this.caseSensitivity.checkCompareTo(file1.getName(), file2.getName());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 112 */     return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.comparator.NameFileComparator
 * JD-Core Version:    0.6.2
 */