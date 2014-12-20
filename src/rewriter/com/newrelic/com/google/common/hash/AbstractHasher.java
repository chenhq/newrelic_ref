/*    */ package com.newrelic.com.google.common.hash;
/*    */ 
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ abstract class AbstractHasher
/*    */   implements Hasher
/*    */ {
/*    */   public final Hasher putBoolean(boolean b)
/*    */   {
/* 28 */     return putByte((byte)(b ? 1 : 0));
/*    */   }
/*    */ 
/*    */   public final Hasher putDouble(double d) {
/* 32 */     return putLong(Double.doubleToRawLongBits(d));
/*    */   }
/*    */ 
/*    */   public final Hasher putFloat(float f) {
/* 36 */     return putInt(Float.floatToRawIntBits(f));
/*    */   }
/*    */ 
/*    */   public Hasher putUnencodedChars(CharSequence charSequence) {
/* 40 */     int i = 0; for (int len = charSequence.length(); i < len; i++) {
/* 41 */       putChar(charSequence.charAt(i));
/*    */     }
/* 43 */     return this;
/*    */   }
/*    */ 
/*    */   public Hasher putString(CharSequence charSequence, Charset charset) {
/* 47 */     return putBytes(charSequence.toString().getBytes(charset));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.AbstractHasher
 * JD-Core Version:    0.6.2
 */