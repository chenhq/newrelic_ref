/*     */ package com.newrelic.org.apache.commons.io.output;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class CountingOutputStream extends ProxyOutputStream
/*     */ {
/*  33 */   private long count = 0L;
/*     */ 
/*     */   public CountingOutputStream(OutputStream out)
/*     */   {
/*  41 */     super(out);
/*     */   }
/*     */ 
/*     */   protected synchronized void beforeWrite(int n)
/*     */   {
/*  54 */     this.count += n;
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/*  69 */     long result = getByteCount();
/*  70 */     if (result > 2147483647L) {
/*  71 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*     */     }
/*  73 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public int resetCount()
/*     */   {
/*  87 */     long result = resetByteCount();
/*  88 */     if (result > 2147483647L) {
/*  89 */       throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
/*     */     }
/*  91 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public synchronized long getByteCount()
/*     */   {
/* 105 */     return this.count;
/*     */   }
/*     */ 
/*     */   public synchronized long resetByteCount()
/*     */   {
/* 119 */     long tmp = this.count;
/* 120 */     this.count = 0L;
/* 121 */     return tmp;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.CountingOutputStream
 * JD-Core Version:    0.6.2
 */