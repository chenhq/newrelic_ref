/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ public class ByteArray
/*    */ {
/*    */   public static int readU16bit(byte[] code, int index)
/*    */   {
/* 26 */     return (code[index] & 0xFF) << 8 | code[(index + 1)] & 0xFF;
/*    */   }
/*    */ 
/*    */   public static int readS16bit(byte[] code, int index)
/*    */   {
/* 33 */     return code[index] << 8 | code[(index + 1)] & 0xFF;
/*    */   }
/*    */ 
/*    */   public static void write16bit(int value, byte[] code, int index)
/*    */   {
/* 40 */     code[index] = ((byte)(value >>> 8));
/* 41 */     code[(index + 1)] = ((byte)value);
/*    */   }
/*    */ 
/*    */   public static int read32bit(byte[] code, int index)
/*    */   {
/* 48 */     return code[index] << 24 | (code[(index + 1)] & 0xFF) << 16 | (code[(index + 2)] & 0xFF) << 8 | code[(index + 3)] & 0xFF;
/*    */   }
/*    */ 
/*    */   public static void write32bit(int value, byte[] code, int index)
/*    */   {
/* 56 */     code[index] = ((byte)(value >>> 24));
/* 57 */     code[(index + 1)] = ((byte)(value >>> 16));
/* 58 */     code[(index + 2)] = ((byte)(value >>> 8));
/* 59 */     code[(index + 3)] = ((byte)value);
/*    */   }
/*    */ 
/*    */   static void copy32bit(byte[] src, int isrc, byte[] dest, int idest)
/*    */   {
/* 71 */     dest[idest] = src[isrc];
/* 72 */     dest[(idest + 1)] = src[(isrc + 1)];
/* 73 */     dest[(idest + 2)] = src[(isrc + 2)];
/* 74 */     dest[(idest + 3)] = src[(isrc + 3)];
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ByteArray
 * JD-Core Version:    0.6.2
 */