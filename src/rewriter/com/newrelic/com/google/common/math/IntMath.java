/*     */ package com.newrelic.com.google.common.math;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.math.RoundingMode;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class IntMath
/*     */ {
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
/*     */ 
/*     */   @VisibleForTesting
/* 169 */   static final byte[] maxLog10ForLeadingZeros = { 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0, 0 };
/*     */ 
/*     */   @VisibleForTesting
/* 172 */   static final int[] powersOf10 = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
/*     */ 
/*     */   @VisibleForTesting
/* 176 */   static final int[] halfPowersOf10 = { 3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, 2147483647 };
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final int FLOOR_SQRT_MAX_INT = 46340;
/* 502 */   private static final int[] factorials = { 1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600 };
/*     */ 
/*     */   @VisibleForTesting
/* 550 */   static int[] biggestBinomials = { 2147483647, 2147483647, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33 };
/*     */ 
/*     */   public static boolean isPowerOfTwo(int x)
/*     */   {
/*  63 */     return (x > 0 ? 1 : 0) & ((x & x - 1) == 0 ? 1 : 0);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int lessThanBranchFree(int x, int y)
/*     */   {
/*  75 */     return (x - y ^ 0xFFFFFFFF ^ 0xFFFFFFFF) >>> 31;
/*     */   }
/*     */ 
/*     */   public static int log2(int x, RoundingMode mode)
/*     */   {
/*  88 */     MathPreconditions.checkPositive("x", x);
/*  89 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/*  91 */       MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
/*     */     case 2:
/*     */     case 3:
/*  95 */       return 31 - Integer.numberOfLeadingZeros(x);
/*     */     case 4:
/*     */     case 5:
/*  99 */       return 32 - Integer.numberOfLeadingZeros(x - 1);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 105 */       int leadingZeros = Integer.numberOfLeadingZeros(x);
/* 106 */       int cmp = -1257966797 >>> leadingZeros;
/*     */ 
/* 108 */       int logFloor = 31 - leadingZeros;
/* 109 */       return logFloor + lessThanBranchFree(cmp, x);
/*     */     }
/*     */ 
/* 112 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("need BigIntegerMath to adequately test")
/*     */   public static int log10(int x, RoundingMode mode)
/*     */   {
/* 129 */     MathPreconditions.checkPositive("x", x);
/* 130 */     int logFloor = log10Floor(x);
/* 131 */     int floorPow = powersOf10[logFloor];
/* 132 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 134 */       MathPreconditions.checkRoundingUnnecessary(x == floorPow);
/*     */     case 2:
/*     */     case 3:
/* 138 */       return logFloor;
/*     */     case 4:
/*     */     case 5:
/* 141 */       return logFloor + lessThanBranchFree(floorPow, x);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 146 */       return logFloor + lessThanBranchFree(halfPowersOf10[logFloor], x);
/*     */     }
/* 148 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   private static int log10Floor(int x)
/*     */   {
/* 160 */     int y = maxLog10ForLeadingZeros[Integer.numberOfLeadingZeros(x)];
/*     */ 
/* 165 */     return y - lessThanBranchFree(x, powersOf10[y]);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("failing tests")
/*     */   public static int pow(int b, int k)
/*     */   {
/* 190 */     MathPreconditions.checkNonNegative("exponent", k);
/* 191 */     switch (b) {
/*     */     case 0:
/* 193 */       return k == 0 ? 1 : 0;
/*     */     case 1:
/* 195 */       return 1;
/*     */     case -1:
/* 197 */       return (k & 0x1) == 0 ? 1 : -1;
/*     */     case 2:
/* 199 */       return k < 32 ? 1 << k : 0;
/*     */     case -2:
/* 201 */       if (k < 32) {
/* 202 */         return (k & 0x1) == 0 ? 1 << k : -(1 << k);
/*     */       }
/* 204 */       return 0;
/*     */     }
/*     */ 
/* 209 */     for (int accum = 1; ; k >>= 1) {
/* 210 */       switch (k) {
/*     */       case 0:
/* 212 */         return accum;
/*     */       case 1:
/* 214 */         return b * accum;
/*     */       }
/* 216 */       accum *= ((k & 0x1) == 0 ? 1 : b);
/* 217 */       b *= b;
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("need BigIntegerMath to adequately test")
/*     */   public static int sqrt(int x, RoundingMode mode)
/*     */   {
/* 232 */     MathPreconditions.checkNonNegative("x", x);
/* 233 */     int sqrtFloor = sqrtFloor(x);
/* 234 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 236 */       MathPreconditions.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x);
/*     */     case 2:
/*     */     case 3:
/* 239 */       return sqrtFloor;
/*     */     case 4:
/*     */     case 5:
/* 242 */       return sqrtFloor + lessThanBranchFree(sqrtFloor * sqrtFloor, x);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 246 */       int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
/*     */ 
/* 258 */       return sqrtFloor + lessThanBranchFree(halfSquare, x);
/*     */     }
/* 260 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   private static int sqrtFloor(int x)
/*     */   {
/* 267 */     return (int)Math.sqrt(x);
/*     */   }
/*     */ 
/*     */   public static int divide(int p, int q, RoundingMode mode)
/*     */   {
/* 279 */     Preconditions.checkNotNull(mode);
/* 280 */     if (q == 0) {
/* 281 */       throw new ArithmeticException("/ by zero");
/*     */     }
/* 283 */     int div = p / q;
/* 284 */     int rem = p - q * div;
/*     */ 
/* 286 */     if (rem == 0) {
/* 287 */       return div;
/*     */     }
/*     */ 
/* 297 */     int signum = 0x1 | (p ^ q) >> 31;
/*     */     boolean increment;
/*     */     boolean increment;
/* 299 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 301 */       MathPreconditions.checkRoundingUnnecessary(rem == 0);
/*     */     case 2:
/* 304 */       increment = false;
/* 305 */       break;
/*     */     case 4:
/* 307 */       increment = true;
/* 308 */       break;
/*     */     case 5:
/* 310 */       increment = signum > 0;
/* 311 */       break;
/*     */     case 3:
/* 313 */       increment = signum < 0;
/* 314 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 318 */       int absRem = Math.abs(rem);
/* 319 */       int cmpRemToHalfDivisor = absRem - (Math.abs(q) - absRem);
/*     */ 
/* 322 */       if (cmpRemToHalfDivisor == 0) {
/* 323 */         if (mode != RoundingMode.HALF_UP);
/* 323 */         increment = ((mode == RoundingMode.HALF_EVEN ? 1 : 0) & ((div & 0x1) != 0 ? 1 : 0)) != 0;
/*     */       } else {
/* 325 */         increment = cmpRemToHalfDivisor > 0;
/*     */       }
/* 327 */       break;
/*     */     default:
/* 329 */       throw new AssertionError();
/*     */     }
/* 331 */     return increment ? div + signum : div;
/*     */   }
/*     */ 
/*     */   public static int mod(int x, int m)
/*     */   {
/* 351 */     if (m <= 0) {
/* 352 */       throw new ArithmeticException("Modulus " + m + " must be > 0");
/*     */     }
/* 354 */     int result = x % m;
/* 355 */     return result >= 0 ? result : result + m;
/*     */   }
/*     */ 
/*     */   public static int gcd(int a, int b)
/*     */   {
/* 370 */     MathPreconditions.checkNonNegative("a", a);
/* 371 */     MathPreconditions.checkNonNegative("b", b);
/* 372 */     if (a == 0)
/*     */     {
/* 375 */       return b;
/* 376 */     }if (b == 0) {
/* 377 */       return a;
/*     */     }
/*     */ 
/* 383 */     int aTwos = Integer.numberOfTrailingZeros(a);
/* 384 */     a >>= aTwos;
/* 385 */     int bTwos = Integer.numberOfTrailingZeros(b);
/* 386 */     b >>= bTwos;
/* 387 */     while (a != b)
/*     */     {
/* 395 */       int delta = a - b;
/*     */ 
/* 397 */       int minDeltaOrZero = delta & delta >> 31;
/*     */ 
/* 400 */       a = delta - minDeltaOrZero - minDeltaOrZero;
/*     */ 
/* 403 */       b += minDeltaOrZero;
/* 404 */       a >>= Integer.numberOfTrailingZeros(a);
/*     */     }
/* 406 */     return a << Math.min(aTwos, bTwos);
/*     */   }
/*     */ 
/*     */   public static int checkedAdd(int a, int b)
/*     */   {
/* 415 */     long result = a + b;
/* 416 */     MathPreconditions.checkNoOverflow(result == (int)result);
/* 417 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static int checkedSubtract(int a, int b)
/*     */   {
/* 426 */     long result = a - b;
/* 427 */     MathPreconditions.checkNoOverflow(result == (int)result);
/* 428 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static int checkedMultiply(int a, int b)
/*     */   {
/* 437 */     long result = a * b;
/* 438 */     MathPreconditions.checkNoOverflow(result == (int)result);
/* 439 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static int checkedPow(int b, int k)
/*     */   {
/* 451 */     MathPreconditions.checkNonNegative("exponent", k);
/* 452 */     switch (b) {
/*     */     case 0:
/* 454 */       return k == 0 ? 1 : 0;
/*     */     case 1:
/* 456 */       return 1;
/*     */     case -1:
/* 458 */       return (k & 0x1) == 0 ? 1 : -1;
/*     */     case 2:
/* 460 */       MathPreconditions.checkNoOverflow(k < 31);
/* 461 */       return 1 << k;
/*     */     case -2:
/* 463 */       MathPreconditions.checkNoOverflow(k < 32);
/* 464 */       return (k & 0x1) == 0 ? 1 << k : -1 << k;
/*     */     }
/*     */ 
/* 468 */     int accum = 1;
/*     */     while (true) {
/* 470 */       switch (k) {
/*     */       case 0:
/* 472 */         return accum;
/*     */       case 1:
/* 474 */         return checkedMultiply(accum, b);
/*     */       }
/* 476 */       if ((k & 0x1) != 0) {
/* 477 */         accum = checkedMultiply(accum, b);
/*     */       }
/* 479 */       k >>= 1;
/* 480 */       if (k > 0) {
/* 481 */         MathPreconditions.checkNoOverflow((-46340 <= b ? 1 : 0) & (b <= 46340 ? 1 : 0));
/* 482 */         b *= b;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int factorial(int n)
/*     */   {
/* 498 */     MathPreconditions.checkNonNegative("n", n);
/* 499 */     return n < factorials.length ? factorials[n] : 2147483647;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("need BigIntegerMath to adequately test")
/*     */   public static int binomial(int n, int k)
/*     */   {
/* 525 */     MathPreconditions.checkNonNegative("n", n);
/* 526 */     MathPreconditions.checkNonNegative("k", k);
/* 527 */     Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", new Object[] { Integer.valueOf(k), Integer.valueOf(n) });
/* 528 */     if (k > n >> 1) {
/* 529 */       k = n - k;
/*     */     }
/* 531 */     if ((k >= biggestBinomials.length) || (n > biggestBinomials[k])) {
/* 532 */       return 2147483647;
/*     */     }
/* 534 */     switch (k) {
/*     */     case 0:
/* 536 */       return 1;
/*     */     case 1:
/* 538 */       return n;
/*     */     }
/* 540 */     long result = 1L;
/* 541 */     for (int i = 0; i < k; i++) {
/* 542 */       result *= (n - i);
/* 543 */       result /= (i + 1);
/*     */     }
/* 545 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static int mean(int x, int y)
/*     */   {
/* 580 */     return (x & y) + ((x ^ y) >> 1);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.math.IntMath
 * JD-Core Version:    0.6.2
 */