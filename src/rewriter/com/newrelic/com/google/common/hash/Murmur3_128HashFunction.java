/*     */ package com.newrelic.com.google.common.hash;
/*     */ 
/*     */ import com.newrelic.com.google.common.primitives.UnsignedBytes;
/*     */ import java.io.Serializable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ final class Murmur3_128HashFunction extends AbstractStreamingHashFunction
/*     */   implements Serializable
/*     */ {
/*     */   private final int seed;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   Murmur3_128HashFunction(int seed)
/*     */   {
/*  48 */     this.seed = seed;
/*     */   }
/*     */ 
/*     */   public int bits() {
/*  52 */     return 128;
/*     */   }
/*     */ 
/*     */   public Hasher newHasher() {
/*  56 */     return new Murmur3_128Hasher(this.seed);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  61 */     return "Hashing.murmur3_128(" + this.seed + ")";
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/*  66 */     if ((object instanceof Murmur3_128HashFunction)) {
/*  67 */       Murmur3_128HashFunction other = (Murmur3_128HashFunction)object;
/*  68 */       return this.seed == other.seed;
/*     */     }
/*  70 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  75 */     return getClass().hashCode() ^ this.seed; } 
/*     */   private static final class Murmur3_128Hasher extends AbstractStreamingHashFunction.AbstractStreamingHasher { private static final int CHUNK_SIZE = 16;
/*     */     private static final long C1 = -8663945395140668459L;
/*     */     private static final long C2 = 5545529020109919103L;
/*     */     private long h1;
/*     */     private long h2;
/*     */     private int length;
/*     */ 
/*  87 */     Murmur3_128Hasher(int seed) { super();
/*  88 */       this.h1 = seed;
/*  89 */       this.h2 = seed;
/*  90 */       this.length = 0; }
/*     */ 
/*     */     protected void process(ByteBuffer bb)
/*     */     {
/*  94 */       long k1 = bb.getLong();
/*  95 */       long k2 = bb.getLong();
/*  96 */       bmix64(k1, k2);
/*  97 */       this.length += 16;
/*     */     }
/*     */ 
/*     */     private void bmix64(long k1, long k2) {
/* 101 */       this.h1 ^= mixK1(k1);
/*     */ 
/* 103 */       this.h1 = Long.rotateLeft(this.h1, 27);
/* 104 */       this.h1 += this.h2;
/* 105 */       this.h1 = (this.h1 * 5L + 1390208809L);
/*     */ 
/* 107 */       this.h2 ^= mixK2(k2);
/*     */ 
/* 109 */       this.h2 = Long.rotateLeft(this.h2, 31);
/* 110 */       this.h2 += this.h1;
/* 111 */       this.h2 = (this.h2 * 5L + 944331445L);
/*     */     }
/*     */ 
/*     */     protected void processRemaining(ByteBuffer bb) {
/* 115 */       long k1 = 0L;
/* 116 */       long k2 = 0L;
/* 117 */       this.length += bb.remaining();
/* 118 */       switch (bb.remaining()) {
/*     */       case 15:
/* 120 */         k2 ^= UnsignedBytes.toInt(bb.get(14)) << 48;
/*     */       case 14:
/* 122 */         k2 ^= UnsignedBytes.toInt(bb.get(13)) << 40;
/*     */       case 13:
/* 124 */         k2 ^= UnsignedBytes.toInt(bb.get(12)) << 32;
/*     */       case 12:
/* 126 */         k2 ^= UnsignedBytes.toInt(bb.get(11)) << 24;
/*     */       case 11:
/* 128 */         k2 ^= UnsignedBytes.toInt(bb.get(10)) << 16;
/*     */       case 10:
/* 130 */         k2 ^= UnsignedBytes.toInt(bb.get(9)) << 8;
/*     */       case 9:
/* 132 */         k2 ^= UnsignedBytes.toInt(bb.get(8));
/*     */       case 8:
/* 134 */         k1 ^= bb.getLong();
/* 135 */         break;
/*     */       case 7:
/* 137 */         k1 ^= UnsignedBytes.toInt(bb.get(6)) << 48;
/*     */       case 6:
/* 139 */         k1 ^= UnsignedBytes.toInt(bb.get(5)) << 40;
/*     */       case 5:
/* 141 */         k1 ^= UnsignedBytes.toInt(bb.get(4)) << 32;
/*     */       case 4:
/* 143 */         k1 ^= UnsignedBytes.toInt(bb.get(3)) << 24;
/*     */       case 3:
/* 145 */         k1 ^= UnsignedBytes.toInt(bb.get(2)) << 16;
/*     */       case 2:
/* 147 */         k1 ^= UnsignedBytes.toInt(bb.get(1)) << 8;
/*     */       case 1:
/* 149 */         k1 ^= UnsignedBytes.toInt(bb.get(0));
/* 150 */         break;
/*     */       default:
/* 152 */         throw new AssertionError("Should never get here.");
/*     */       }
/* 154 */       this.h1 ^= mixK1(k1);
/* 155 */       this.h2 ^= mixK2(k2);
/*     */     }
/*     */ 
/*     */     public HashCode makeHash() {
/* 159 */       this.h1 ^= this.length;
/* 160 */       this.h2 ^= this.length;
/*     */ 
/* 162 */       this.h1 += this.h2;
/* 163 */       this.h2 += this.h1;
/*     */ 
/* 165 */       this.h1 = fmix64(this.h1);
/* 166 */       this.h2 = fmix64(this.h2);
/*     */ 
/* 168 */       this.h1 += this.h2;
/* 169 */       this.h2 += this.h1;
/*     */ 
/* 171 */       return HashCode.fromBytesNoCopy(ByteBuffer.wrap(new byte[16]).order(ByteOrder.LITTLE_ENDIAN).putLong(this.h1).putLong(this.h2).array());
/*     */     }
/*     */ 
/*     */     private static long fmix64(long k)
/*     */     {
/* 180 */       k ^= k >>> 33;
/* 181 */       k *= -49064778989728563L;
/* 182 */       k ^= k >>> 33;
/* 183 */       k *= -4265267296055464877L;
/* 184 */       k ^= k >>> 33;
/* 185 */       return k;
/*     */     }
/*     */ 
/*     */     private static long mixK1(long k1) {
/* 189 */       k1 *= -8663945395140668459L;
/* 190 */       k1 = Long.rotateLeft(k1, 31);
/* 191 */       k1 *= 5545529020109919103L;
/* 192 */       return k1;
/*     */     }
/*     */ 
/*     */     private static long mixK2(long k2) {
/* 196 */       k2 *= 5545529020109919103L;
/* 197 */       k2 = Long.rotateLeft(k2, 33);
/* 198 */       k2 *= -8663945395140668459L;
/* 199 */       return k2;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.Murmur3_128HashFunction
 * JD-Core Version:    0.6.2
 */