/*     */ package com.newrelic.org.apache.commons.io.input;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class CountingInputStream extends ProxyInputStream
/*     */ {
/*     */   private long count;
/*     */ 
/*     */   public CountingInputStream(InputStream in)
/*     */   {
/*  42 */     super(in);
/*     */   }
/*     */ 
/*     */   public synchronized long skip(long length)
/*     */     throws IOException
/*     */   {
/*  58 */     long skip = super.skip(length);
/*  59 */     this.count += skip;
/*  60 */     return skip;
/*     */   }
/*     */ 
/*     */   protected synchronized void afterRead(int n)
/*     */   {
/*  71 */     if (n != -1)
/*  72 */       this.count += n;
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/*  88 */     long result = getByteCount();
/*  89 */     if (result > 2147483647L) {
/*  90 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*     */     }
/*  92 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public int resetCount()
/*     */   {
/* 106 */     long result = resetByteCount();
/* 107 */     if (result > 2147483647L) {
/* 108 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*     */     }
/* 110 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public synchronized long getByteCount()
/*     */   {
/* 124 */     return this.count;
/*     */   }
/*     */ 
/*     */   public synchronized long resetByteCount()
/*     */   {
/* 138 */     long tmp = this.count;
/* 139 */     this.count = 0L;
/* 140 */     return tmp;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.CountingInputStream
 * JD-Core Version:    0.6.2
 */