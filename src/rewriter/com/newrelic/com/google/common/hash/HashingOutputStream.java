/*    */ package com.newrelic.com.google.common.hash;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ @Beta
/*    */ public final class HashingOutputStream extends FilterOutputStream
/*    */ {
/*    */   private final Hasher hasher;
/*    */ 
/*    */   public HashingOutputStream(HashFunction hashFunction, OutputStream out)
/*    */   {
/* 46 */     super((OutputStream)Preconditions.checkNotNull(out));
/* 47 */     this.hasher = ((Hasher)Preconditions.checkNotNull(hashFunction.newHasher()));
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException {
/* 51 */     this.hasher.putByte((byte)b);
/* 52 */     this.out.write(b);
/*    */   }
/*    */ 
/*    */   public void write(byte[] bytes, int off, int len) throws IOException {
/* 56 */     this.hasher.putBytes(bytes, off, len);
/* 57 */     this.out.write(bytes, off, len);
/*    */   }
/*    */ 
/*    */   public HashCode hash()
/*    */   {
/* 65 */     return this.hasher.hash();
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 72 */     this.out.close();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.HashingOutputStream
 * JD-Core Version:    0.6.2
 */