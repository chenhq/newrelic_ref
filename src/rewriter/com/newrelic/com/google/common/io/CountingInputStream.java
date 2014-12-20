/*    */ package com.newrelic.com.google.common.io;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public final class CountingInputStream extends FilterInputStream
/*    */ {
/*    */   private long count;
/* 37 */   private long mark = -1L;
/*    */ 
/*    */   public CountingInputStream(@Nullable InputStream in)
/*    */   {
/* 45 */     super(in);
/*    */   }
/*    */ 
/*    */   public long getCount()
/*    */   {
/* 50 */     return this.count;
/*    */   }
/*    */ 
/*    */   public int read() throws IOException {
/* 54 */     int result = this.in.read();
/* 55 */     if (result != -1) {
/* 56 */       this.count += 1L;
/*    */     }
/* 58 */     return result;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 62 */     int result = this.in.read(b, off, len);
/* 63 */     if (result != -1) {
/* 64 */       this.count += result;
/*    */     }
/* 66 */     return result;
/*    */   }
/*    */ 
/*    */   public long skip(long n) throws IOException {
/* 70 */     long result = this.in.skip(n);
/* 71 */     this.count += result;
/* 72 */     return result;
/*    */   }
/*    */ 
/*    */   public synchronized void mark(int readlimit) {
/* 76 */     this.in.mark(readlimit);
/* 77 */     this.mark = this.count;
/*    */   }
/*    */ 
/*    */   public synchronized void reset() throws IOException
/*    */   {
/* 82 */     if (!this.in.markSupported()) {
/* 83 */       throw new IOException("Mark not supported");
/*    */     }
/* 85 */     if (this.mark == -1L) {
/* 86 */       throw new IOException("Mark not set");
/*    */     }
/*    */ 
/* 89 */     this.in.reset();
/* 90 */     this.count = this.mark;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.CountingInputStream
 * JD-Core Version:    0.6.2
 */