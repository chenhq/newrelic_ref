/*     */ package com.newrelic.org.apache.commons.io.filefilter;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.FileUtils;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class AgeFileFilter extends AbstractFileFilter
/*     */   implements Serializable
/*     */ {
/*     */   private final long cutoff;
/*     */   private final boolean acceptOlder;
/*     */ 
/*     */   public AgeFileFilter(long cutoff)
/*     */   {
/*  65 */     this(cutoff, true);
/*     */   }
/*     */ 
/*     */   public AgeFileFilter(long cutoff, boolean acceptOlder)
/*     */   {
/*  77 */     this.acceptOlder = acceptOlder;
/*  78 */     this.cutoff = cutoff;
/*     */   }
/*     */ 
/*     */   public AgeFileFilter(Date cutoffDate)
/*     */   {
/*  88 */     this(cutoffDate, true);
/*     */   }
/*     */ 
/*     */   public AgeFileFilter(Date cutoffDate, boolean acceptOlder)
/*     */   {
/* 100 */     this(cutoffDate.getTime(), acceptOlder);
/*     */   }
/*     */ 
/*     */   public AgeFileFilter(File cutoffReference)
/*     */   {
/* 111 */     this(cutoffReference, true);
/*     */   }
/*     */ 
/*     */   public AgeFileFilter(File cutoffReference, boolean acceptOlder)
/*     */   {
/* 125 */     this(cutoffReference.lastModified(), acceptOlder);
/*     */   }
/*     */ 
/*     */   public boolean accept(File file)
/*     */   {
/* 143 */     boolean newer = FileUtils.isFileNewer(file, this.cutoff);
/* 144 */     return this.acceptOlder ? false : !newer ? true : newer;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 154 */     String condition = this.acceptOlder ? "<=" : ">";
/* 155 */     return super.toString() + "(" + condition + this.cutoff + ")";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.AgeFileFilter
 * JD-Core Version:    0.6.2
 */