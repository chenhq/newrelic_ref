/*     */ package com.newrelic.org.apache.commons.io.filefilter;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class SizeFileFilter extends AbstractFileFilter
/*     */   implements Serializable
/*     */ {
/*     */   private final long size;
/*     */   private final boolean acceptLarger;
/*     */ 
/*     */   public SizeFileFilter(long size)
/*     */   {
/*  58 */     this(size, true);
/*     */   }
/*     */ 
/*     */   public SizeFileFilter(long size, boolean acceptLarger)
/*     */   {
/*  71 */     if (size < 0L) {
/*  72 */       throw new IllegalArgumentException("The size must be non-negative");
/*     */     }
/*  74 */     this.size = size;
/*  75 */     this.acceptLarger = acceptLarger;
/*     */   }
/*     */ 
/*     */   public boolean accept(File file)
/*     */   {
/*  92 */     boolean smaller = file.length() < this.size;
/*  93 */     return this.acceptLarger ? false : !smaller ? true : smaller;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 103 */     String condition = this.acceptLarger ? ">=" : "<";
/* 104 */     return super.toString() + "(" + condition + this.size + ")";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.SizeFileFilter
 * JD-Core Version:    0.6.2
 */