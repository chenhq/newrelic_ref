/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Beta
/*     */ public abstract class DiscreteDomain<C extends Comparable>
/*     */ {
/*     */   public static DiscreteDomain<Integer> integers()
/*     */   {
/*  54 */     return IntegerDomain.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static DiscreteDomain<Long> longs()
/*     */   {
/* 101 */     return LongDomain.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static DiscreteDomain<BigInteger> bigIntegers()
/*     */   {
/* 155 */     return BigIntegerDomain.INSTANCE;
/*     */   }
/*     */ 
/*     */   public abstract C next(C paramC);
/*     */ 
/*     */   public abstract C previous(C paramC);
/*     */ 
/*     */   public abstract long distance(C paramC1, C paramC2);
/*     */ 
/*     */   public C minValue()
/*     */   {
/* 245 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */   public C maxValue()
/*     */   {
/* 260 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */   private static final class BigIntegerDomain extends DiscreteDomain<BigInteger>
/*     */     implements Serializable
/*     */   {
/* 160 */     private static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
/*     */ 
/* 162 */     private static final BigInteger MIN_LONG = BigInteger.valueOf(-9223372036854775808L);
/*     */ 
/* 164 */     private static final BigInteger MAX_LONG = BigInteger.valueOf(9223372036854775807L);
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public BigInteger next(BigInteger value)
/*     */     {
/* 168 */       return value.add(BigInteger.ONE);
/*     */     }
/*     */ 
/*     */     public BigInteger previous(BigInteger value) {
/* 172 */       return value.subtract(BigInteger.ONE);
/*     */     }
/*     */ 
/*     */     public long distance(BigInteger start, BigInteger end) {
/* 176 */       return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/* 180 */       return INSTANCE;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 185 */       return "DiscreteDomain.bigIntegers()";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class LongDomain extends DiscreteDomain<Long>
/*     */     implements Serializable
/*     */   {
/* 106 */     private static final LongDomain INSTANCE = new LongDomain();
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public Long next(Long value)
/*     */     {
/* 109 */       long l = value.longValue();
/* 110 */       return l == 9223372036854775807L ? null : Long.valueOf(l + 1L);
/*     */     }
/*     */ 
/*     */     public Long previous(Long value) {
/* 114 */       long l = value.longValue();
/* 115 */       return l == -9223372036854775808L ? null : Long.valueOf(l - 1L);
/*     */     }
/*     */ 
/*     */     public long distance(Long start, Long end) {
/* 119 */       long result = end.longValue() - start.longValue();
/* 120 */       if ((end.longValue() > start.longValue()) && (result < 0L)) {
/* 121 */         return 9223372036854775807L;
/*     */       }
/* 123 */       if ((end.longValue() < start.longValue()) && (result > 0L)) {
/* 124 */         return -9223372036854775808L;
/*     */       }
/* 126 */       return result;
/*     */     }
/*     */ 
/*     */     public Long minValue() {
/* 130 */       return Long.valueOf(-9223372036854775808L);
/*     */     }
/*     */ 
/*     */     public Long maxValue() {
/* 134 */       return Long.valueOf(9223372036854775807L);
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/* 138 */       return INSTANCE;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 143 */       return "DiscreteDomain.longs()";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class IntegerDomain extends DiscreteDomain<Integer>
/*     */     implements Serializable
/*     */   {
/*  59 */     private static final IntegerDomain INSTANCE = new IntegerDomain();
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public Integer next(Integer value)
/*     */     {
/*  62 */       int i = value.intValue();
/*  63 */       return i == 2147483647 ? null : Integer.valueOf(i + 1);
/*     */     }
/*     */ 
/*     */     public Integer previous(Integer value) {
/*  67 */       int i = value.intValue();
/*  68 */       return i == -2147483648 ? null : Integer.valueOf(i - 1);
/*     */     }
/*     */ 
/*     */     public long distance(Integer start, Integer end) {
/*  72 */       return end.intValue() - start.intValue();
/*     */     }
/*     */ 
/*     */     public Integer minValue() {
/*  76 */       return Integer.valueOf(-2147483648);
/*     */     }
/*     */ 
/*     */     public Integer maxValue() {
/*  80 */       return Integer.valueOf(2147483647);
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/*  84 */       return INSTANCE;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  89 */       return "DiscreteDomain.integers()";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.DiscreteDomain
 * JD-Core Version:    0.6.2
 */