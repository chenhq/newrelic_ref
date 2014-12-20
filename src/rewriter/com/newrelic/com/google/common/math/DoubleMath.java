/*     */ package com.newrelic.com.google.common.math;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.primitives.Booleans;
/*     */ import java.math.BigInteger;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class DoubleMath
/*     */ {
/*     */   private static final double MIN_INT_AS_DOUBLE = -2147483648.0D;
/*     */   private static final double MAX_INT_AS_DOUBLE = 2147483647.0D;
/*     */   private static final double MIN_LONG_AS_DOUBLE = -9.223372036854776E+18D;
/*     */   private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E+18D;
/* 220 */   private static final double LN_2 = Math.log(2.0D);
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final int MAX_FACTORIAL = 170;
/*     */ 
/*     */   @VisibleForTesting
/* 313 */   static final double[] everySixteenthFactorial = { 1.0D, 20922789888000.0D, 2.631308369336935E+35D, 1.241391559253607E+61D, 1.268869321858842E+89D, 7.156945704626381E+118D, 9.916779348709497E+149D, 1.974506857221074E+182D, 3.856204823625804E+215D, 5.550293832739304E+249D, 4.714723635992062E+284D };
/*     */ 
/*     */   @GwtIncompatible("#isMathematicalInteger, com.google.common.math.DoubleUtils")
/*     */   static double roundIntermediate(double x, RoundingMode mode)
/*     */   {
/*  58 */     if (!DoubleUtils.isFinite(x)) {
/*  59 */       throw new ArithmeticException("input is infinite or NaN");
/*     */     }
/*  61 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/*  63 */       MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(x));
/*  64 */       return x;
/*     */     case 2:
/*  67 */       if ((x >= 0.0D) || (isMathematicalInteger(x))) {
/*  68 */         return x;
/*     */       }
/*  70 */       return x - 1.0D;
/*     */     case 3:
/*  74 */       if ((x <= 0.0D) || (isMathematicalInteger(x))) {
/*  75 */         return x;
/*     */       }
/*  77 */       return x + 1.0D;
/*     */     case 4:
/*  81 */       return x;
/*     */     case 5:
/*  84 */       if (isMathematicalInteger(x)) {
/*  85 */         return x;
/*     */       }
/*  87 */       return x + Math.copySign(1.0D, x);
/*     */     case 6:
/*  91 */       return Math.rint(x);
/*     */     case 7:
/*  94 */       double z = Math.rint(x);
/*  95 */       if (Math.abs(x - z) == 0.5D) {
/*  96 */         return x + Math.copySign(0.5D, x);
/*     */       }
/*  98 */       return z;
/*     */     case 8:
/* 103 */       double z = Math.rint(x);
/* 104 */       if (Math.abs(x - z) == 0.5D) {
/* 105 */         return x;
/*     */       }
/* 107 */       return z;
/*     */     }
/*     */ 
/* 112 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("#roundIntermediate")
/*     */   public static int roundToInt(double x, RoundingMode mode)
/*     */   {
/* 132 */     double z = roundIntermediate(x, mode);
/* 133 */     MathPreconditions.checkInRange((z > -2147483649.0D ? 1 : 0) & (z < 2147483648.0D ? 1 : 0));
/* 134 */     return (int)z;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("#roundIntermediate")
/*     */   public static long roundToLong(double x, RoundingMode mode)
/*     */   {
/* 156 */     double z = roundIntermediate(x, mode);
/* 157 */     MathPreconditions.checkInRange((-9.223372036854776E+18D - z < 1.0D ? 1 : 0) & (z < 9.223372036854776E+18D ? 1 : 0));
/* 158 */     return ()z;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("#roundIntermediate, java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
/*     */   public static BigInteger roundToBigInteger(double x, RoundingMode mode)
/*     */   {
/* 182 */     x = roundIntermediate(x, mode);
/* 183 */     if (((-9.223372036854776E+18D - x < 1.0D ? 1 : 0) & (x < 9.223372036854776E+18D ? 1 : 0)) != 0) {
/* 184 */       return BigInteger.valueOf(()x);
/*     */     }
/* 186 */     int exponent = Math.getExponent(x);
/* 187 */     long significand = DoubleUtils.getSignificand(x);
/* 188 */     BigInteger result = BigInteger.valueOf(significand).shiftLeft(exponent - 52);
/* 189 */     return x < 0.0D ? result.negate() : result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("com.newrelic.com.google.common.math.DoubleUtils")
/*     */   public static boolean isPowerOfTwo(double x)
/*     */   {
/* 198 */     return (x > 0.0D) && (DoubleUtils.isFinite(x)) && (LongMath.isPowerOfTwo(DoubleUtils.getSignificand(x)));
/*     */   }
/*     */ 
/*     */   public static double log2(double x)
/*     */   {
/* 217 */     return Math.log(x) / LN_2;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
/*     */   public static int log2(double x, RoundingMode mode)
/*     */   {
/* 234 */     Preconditions.checkArgument((x > 0.0D) && (DoubleUtils.isFinite(x)), "x must be positive and finite");
/* 235 */     int exponent = Math.getExponent(x);
/* 236 */     if (!DoubleUtils.isNormal(x))
/* 237 */       return log2(x * 4503599627370496.0D, mode) - 52;
/*     */     boolean increment;
/* 242 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 244 */       MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
/*     */     case 2:
/* 247 */       increment = false;
/* 248 */       break;
/*     */     case 3:
/* 250 */       increment = !isPowerOfTwo(x);
/* 251 */       break;
/*     */     case 4:
/* 253 */       increment = (exponent < 0 ? 1 : 0) & (!isPowerOfTwo(x) ? 1 : 0);
/* 254 */       break;
/*     */     case 5:
/* 256 */       increment = (exponent >= 0 ? 1 : 0) & (!isPowerOfTwo(x) ? 1 : 0);
/* 257 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 261 */       double xScaled = DoubleUtils.scaleNormalize(x);
/*     */ 
/* 264 */       increment = xScaled * xScaled > 2.0D;
/* 265 */       break;
/*     */     default:
/* 267 */       throw new AssertionError();
/*     */     }
/* 269 */     return increment ? exponent + 1 : exponent;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.Math.getExponent, com.google.common.math.DoubleUtils")
/*     */   public static boolean isMathematicalInteger(double x)
/*     */   {
/* 280 */     return (DoubleUtils.isFinite(x)) && ((x == 0.0D) || (52 - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(x)) <= Math.getExponent(x)));
/*     */   }
/*     */ 
/*     */   public static double factorial(int n)
/*     */   {
/* 295 */     MathPreconditions.checkNonNegative("n", n);
/* 296 */     if (n > 170) {
/* 297 */       return (1.0D / 0.0D);
/*     */     }
/*     */ 
/* 301 */     double accum = 1.0D;
/* 302 */     for (int i = 1 + (n & 0xFFFFFFF0); i <= n; i++) {
/* 303 */       accum *= i;
/*     */     }
/* 305 */     return accum * everySixteenthFactorial[(n >> 4)];
/*     */   }
/*     */ 
/*     */   public static boolean fuzzyEquals(double a, double b, double tolerance)
/*     */   {
/* 352 */     MathPreconditions.checkNonNegative("tolerance", tolerance);
/* 353 */     return (Math.copySign(a - b, 1.0D) <= tolerance) || (a == b) || ((Double.isNaN(a)) && (Double.isNaN(b)));
/*     */   }
/*     */ 
/*     */   public static int fuzzyCompare(double a, double b, double tolerance)
/*     */   {
/* 375 */     if (fuzzyEquals(a, b, tolerance))
/* 376 */       return 0;
/* 377 */     if (a < b)
/* 378 */       return -1;
/* 379 */     if (a > b) {
/* 380 */       return 1;
/*     */     }
/* 382 */     return Booleans.compare(Double.isNaN(a), Double.isNaN(b));
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("MeanAccumulator")
/*     */   public static double mean(double[] values)
/*     */   {
/* 411 */     MeanAccumulator accumulator = new MeanAccumulator(null);
/* 412 */     for (double value : values) {
/* 413 */       accumulator.add(value);
/*     */     }
/* 415 */     return accumulator.mean();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("MeanAccumulator")
/*     */   public static double mean(int[] values)
/*     */   {
/* 424 */     MeanAccumulator accumulator = new MeanAccumulator(null);
/* 425 */     for (int value : values) {
/* 426 */       accumulator.add(value);
/*     */     }
/* 428 */     return accumulator.mean();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("MeanAccumulator")
/*     */   public static double mean(long[] values)
/*     */   {
/* 438 */     MeanAccumulator accumulator = new MeanAccumulator(null);
/* 439 */     for (long value : values) {
/* 440 */       accumulator.add(value);
/*     */     }
/* 442 */     return accumulator.mean();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("MeanAccumulator")
/*     */   public static double mean(Iterable<? extends Number> values)
/*     */   {
/* 452 */     MeanAccumulator accumulator = new MeanAccumulator(null);
/* 453 */     for (Number value : values) {
/* 454 */       accumulator.add(value.doubleValue());
/*     */     }
/* 456 */     return accumulator.mean();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("MeanAccumulator")
/*     */   public static double mean(Iterator<? extends Number> values)
/*     */   {
/* 466 */     MeanAccumulator accumulator = new MeanAccumulator(null);
/* 467 */     while (values.hasNext()) {
/* 468 */       accumulator.add(((Number)values.next()).doubleValue());
/*     */     }
/* 470 */     return accumulator.mean();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("com.newrelic.com.google.common.math.DoubleUtils")
/*     */   private static final class MeanAccumulator
/*     */   {
/* 389 */     private long count = 0L;
/* 390 */     private double mean = 0.0D;
/*     */ 
/*     */     void add(double value) {
/* 393 */       Preconditions.checkArgument(DoubleUtils.isFinite(value));
/* 394 */       this.count += 1L;
/*     */ 
/* 396 */       this.mean += (value - this.mean) / this.count;
/*     */     }
/*     */ 
/*     */     double mean() {
/* 400 */       Preconditions.checkArgument(this.count > 0L, "Cannot take mean of 0 values");
/* 401 */       return this.mean;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.math.DoubleMath
 * JD-Core Version:    0.6.2
 */