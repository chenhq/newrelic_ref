/*     */ package com.newrelic.com.google.common.hash;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.math.LongMath;
/*     */ import com.newrelic.com.google.common.primitives.Ints;
/*     */ import com.newrelic.com.google.common.primitives.Longs;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Arrays;
/*     */ 
/*     */  enum BloomFilterStrategies
/*     */   implements BloomFilter.Strategy
/*     */ {
/*  44 */   MURMUR128_MITZ_32, 
/*     */ 
/*  90 */   MURMUR128_MITZ_64;
/*     */ 
/*     */   static final class BitArray
/*     */   {
/*     */     final long[] data;
/*     */     long bitCount;
/*     */ 
/*     */     BitArray(long bits)
/*     */     {
/* 145 */       this(new long[Ints.checkedCast(LongMath.divide(bits, 64L, RoundingMode.CEILING))]);
/*     */     }
/*     */ 
/*     */     BitArray(long[] data)
/*     */     {
/* 150 */       Preconditions.checkArgument(data.length > 0, "data length is zero!");
/* 151 */       this.data = data;
/* 152 */       long bitCount = 0L;
/* 153 */       for (long value : data) {
/* 154 */         bitCount += Long.bitCount(value);
/*     */       }
/* 156 */       this.bitCount = bitCount;
/*     */     }
/*     */ 
/*     */     boolean set(long index)
/*     */     {
/* 161 */       if (!get(index)) {
/* 162 */         this.data[((int)(index >>> 6))] |= 1L << (int)index;
/* 163 */         this.bitCount += 1L;
/* 164 */         return true;
/*     */       }
/* 166 */       return false;
/*     */     }
/*     */ 
/*     */     boolean get(long index) {
/* 170 */       return (this.data[((int)(index >>> 6))] & 1L << (int)index) != 0L;
/*     */     }
/*     */ 
/*     */     long bitSize()
/*     */     {
/* 175 */       return this.data.length * 64L;
/*     */     }
/*     */ 
/*     */     long bitCount()
/*     */     {
/* 180 */       return this.bitCount;
/*     */     }
/*     */ 
/*     */     BitArray copy() {
/* 184 */       return new BitArray((long[])this.data.clone());
/*     */     }
/*     */ 
/*     */     void putAll(BitArray array)
/*     */     {
/* 189 */       Preconditions.checkArgument(this.data.length == array.data.length, "BitArrays must be of equal length (%s != %s)", new Object[] { Integer.valueOf(this.data.length), Integer.valueOf(array.data.length) });
/*     */ 
/* 191 */       this.bitCount = 0L;
/* 192 */       for (int i = 0; i < this.data.length; i++) {
/* 193 */         this.data[i] |= array.data[i];
/* 194 */         this.bitCount += Long.bitCount(this.data[i]);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 199 */       if ((o instanceof BitArray)) {
/* 200 */         BitArray bitArray = (BitArray)o;
/* 201 */         return Arrays.equals(this.data, bitArray.data);
/*     */       }
/* 203 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 207 */       return Arrays.hashCode(this.data);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.BloomFilterStrategies
 * JD-Core Version:    0.6.2
 */