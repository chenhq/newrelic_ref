/*    */ package com.newrelic.com.google.common.io;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public final class CountingOutputStream extends FilterOutputStream
/*    */ {
/*    */   private long count;
/*    */ 
/*    */   public CountingOutputStream(@Nullable OutputStream out)
/*    */   {
/* 44 */     super(out);
/*    */   }
/*    */ 
/*    */   public long getCount()
/*    */   {
/* 49 */     return this.count;
/*    */   }
/*    */ 
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 53 */     this.out.write(b, off, len);
/* 54 */     this.count += len;
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException {
/* 58 */     this.out.write(b);
/* 59 */     this.count += 1L;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 66 */     this.out.close();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.CountingOutputStream
 * JD-Core Version:    0.6.2
 */