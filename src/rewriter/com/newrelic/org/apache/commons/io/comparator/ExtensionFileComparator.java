/*     */ package com.newrelic.org.apache.commons.io.comparator;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.FilenameUtils;
/*     */ import com.newrelic.org.apache.commons.io.IOCase;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public class ExtensionFileComparator extends AbstractFileComparator
/*     */   implements Serializable
/*     */ {
/*  57 */   public static final Comparator<File> EXTENSION_COMPARATOR = new ExtensionFileComparator();
/*     */ 
/*  60 */   public static final Comparator<File> EXTENSION_REVERSE = new ReverseComparator(EXTENSION_COMPARATOR);
/*     */ 
/*  63 */   public static final Comparator<File> EXTENSION_INSENSITIVE_COMPARATOR = new ExtensionFileComparator(IOCase.INSENSITIVE);
/*     */ 
/*  67 */   public static final Comparator<File> EXTENSION_INSENSITIVE_REVERSE = new ReverseComparator(EXTENSION_INSENSITIVE_COMPARATOR);
/*     */ 
/*  71 */   public static final Comparator<File> EXTENSION_SYSTEM_COMPARATOR = new ExtensionFileComparator(IOCase.SYSTEM);
/*     */ 
/*  74 */   public static final Comparator<File> EXTENSION_SYSTEM_REVERSE = new ReverseComparator(EXTENSION_SYSTEM_COMPARATOR);
/*     */   private final IOCase caseSensitivity;
/*     */ 
/*     */   public ExtensionFileComparator()
/*     */   {
/*  83 */     this.caseSensitivity = IOCase.SENSITIVE;
/*     */   }
/*     */ 
/*     */   public ExtensionFileComparator(IOCase caseSensitivity)
/*     */   {
/*  92 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*     */   }
/*     */ 
/*     */   public int compare(File file1, File file2)
/*     */   {
/* 107 */     String suffix1 = FilenameUtils.getExtension(file1.getName());
/* 108 */     String suffix2 = FilenameUtils.getExtension(file2.getName());
/* 109 */     return this.caseSensitivity.checkCompareTo(suffix1, suffix2);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 119 */     return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.comparator.ExtensionFileComparator
 * JD-Core Version:    0.6.2
 */