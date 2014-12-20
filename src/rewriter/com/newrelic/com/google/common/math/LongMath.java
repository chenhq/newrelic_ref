/*     */ package com.newrelic.com.google.common.math;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.math.RoundingMode;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class LongMath
/*     */ {
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final long MAX_POWER_OF_SQRT2_UNSIGNED = -5402926248376769404L;
/*     */ 
/*     */   @VisibleForTesting
/* 169 */   static final byte[] maxLog10ForLeadingZeros = { 19, 18, 18, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 12, 11, 11, 11, 10, 10, 10, 9, 9, 9, 9, 8, 8, 8, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 0, 0, 0 };
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   @VisibleForTesting
/* 176 */   static final long[] powersOf10 = { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   @VisibleForTesting
/* 201 */   static final long[] halfPowersOf10 = { 3L, 31L, 316L, 3162L, 31622L, 316227L, 3162277L, 31622776L, 316227766L, 3162277660L, 31622776601L, 316227766016L, 3162277660168L, 31622776601683L, 316227766016837L, 3162277660168379L, 31622776601683793L, 316227766016837933L, 3162277660168379331L };
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
/* 618 */   static final long[] factorials = { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
/*     */ 
/* 727 */   static final int[] biggestBinomials = { 2147483647, 2147483647, 2147483647, 3810779, 121977, 16175, 4337, 1733, 887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68, 67, 67, 66, 66, 66, 66 };
/*     */ 
/*     */   @VisibleForTesting
/* 736 */   static final int[] biggestSimpleBinomials = { 2147483647, 2147483647, 2147483647, 2642246, 86251, 11724, 3218, 1313, 684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62, 61, 61, 61 };
/*     */ 
/*     */   public static boolean isPowerOfTwo(long x)
/*     */   {
/*  62 */     return (x > 0L ? 1 : 0) & ((x & x - 1L) == 0L ? 1 : 0);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int lessThanBranchFree(long x, long y)
/*     */   {
/*  73 */     return (int)((x - y ^ 0xFFFFFFFF ^ 0xFFFFFFFF) >>> 63);
/*     */   }
/*     */ 
/*     */   public static int log2(long x, RoundingMode mode)
/*     */   {
/*  86 */     MathPreconditions.checkPositive("x", x);
/*  87 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/*  89 */       MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
/*     */     case 2:
/*     */     case 3:
/*  93 */       return 63 - Long.numberOfLeadingZeros(x);
/*     */     case 4:
/*     */     case 5:
/*  97 */       return 64 - Long.numberOfLeadingZeros(x - 1L);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 103 */       int leadingZeros = Long.numberOfLeadingZeros(x);
/* 104 */       long cmp = -5402926248376769404L >>> leadingZeros;
/*     */ 
/* 106 */       int logFloor = 63 - leadingZeros;
/* 107 */       return logFloor + lessThanBranchFree(cmp, x);
/*     */     }
/*     */ 
/* 110 */     throw new AssertionError("impossible");
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static int log10(long x, RoundingMode mode)
/*     */   {
/* 128 */     MathPreconditions.checkPositive("x", x);
/* 129 */     int logFloor = log10Floor(x);
/* 130 */     long floorPow = powersOf10[logFloor];
/* 131 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 133 */       MathPreconditions.checkRoundingUnnecessary(x == floorPow);
/*     */     case 2:
/*     */     case 3:
/* 137 */       return logFloor;
/*     */     case 4:
/*     */     case 5:
/* 140 */       return logFloor + lessThanBranchFree(floorPow, x);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 145 */       return logFloor + lessThanBranchFree(halfPowersOf10[logFloor], x);
/*     */     }
/* 147 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   static int log10Floor(long x)
/*     */   {
/* 160 */     int y = maxLog10ForLeadingZeros[Long.numberOfLeadingZeros(x)];
/*     */ 
/* 165 */     return y - lessThanBranchFree(x, powersOf10[y]);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long pow(long b, int k)
/*     */   {
/* 232 */     MathPreconditions.checkNonNegative("exponent", k);
/* 233 */     if ((-2L <= b) && (b <= 2L)) {
/* 234 */       switch ((int)b) {
/*     */       case 0:
/* 236 */         return k == 0 ? 1L : 0L;
/*     */       case 1:
/* 238 */         return 1L;
/*     */       case -1:
/* 240 */         return (k & 0x1) == 0 ? 1L : -1L;
/*     */       case 2:
/* 242 */         return k < 64 ? 1L << k : 0L;
/*     */       case -2:
/* 244 */         if (k < 64) {
/* 245 */           return (k & 0x1) == 0 ? 1L << k : -(1L << k);
/*     */         }
/* 247 */         return 0L;
/*     */       }
/*     */ 
/* 250 */       throw new AssertionError();
/*     */     }
/*     */ 
/* 253 */     for (long accum = 1L; ; k >>= 1) {
/* 254 */       switch (k) {
/*     */       case 0:
/* 256 */         return accum;
/*     */       case 1:
/* 258 */         return accum * b;
/*     */       }
/* 260 */       accum *= ((k & 0x1) == 0 ? 1L : b);
/* 261 */       b *= b;
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long sqrt(long x, RoundingMode mode)
/*     */   {
/* 276 */     MathPreconditions.checkNonNegative("x", x);
/* 277 */     if (fitsInInt(x)) {
/* 278 */       return IntMath.sqrt((int)x, mode);
/*     */     }
/*     */ 
/* 295 */     long guess = ()Math.sqrt(x);
/*     */ 
/* 297 */     long guessSquared = guess * guess;
/*     */ 
/* 300 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 302 */       MathPreconditions.checkRoundingUnnecessary(guessSquared == x);
/* 303 */       return guess;
/*     */     case 2:
/*     */     case 3:
/* 306 */       if (x < guessSquared) {
/* 307 */         return guess - 1L;
/*     */       }
/* 309 */       return guess;
/*     */     case 4:
/*     */     case 5:
/* 312 */       if (x > guessSquared) {
/* 313 */         return guess + 1L;
/*     */       }
/* 315 */       return guess;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 319 */       long sqrtFloor = guess - (x < guessSquared ? 1 : 0);
/* 320 */       long halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
/*     */ 
/* 332 */       return sqrtFloor + lessThanBranchFree(halfSquare, x);
/*     */     }
/* 334 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long divide(long p, long q, RoundingMode mode)
/*     */   {
/* 348 */     Preconditions.checkNotNull(mode);
/* 349 */     long div = p / q;
/* 350 */     long rem = p - q * div;
/*     */ 
/* 352 */     if (rem == 0L) {
/* 353 */       return div;
/*     */     }
/*     */ 
/* 363 */     int signum = 0x1 | (int)((p ^ q) >> 63);
/*     */     boolean increment;
/*     */     boolean increment;
/* 365 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 367 */       MathPreconditions.checkRoundingUnnecessary(rem == 0L);
/*     */     case 2:
/* 370 */       increment = false;
/* 371 */       break;
/*     */     case 4:
/* 373 */       increment = true;
/* 374 */       break;
/*     */     case 5:
/* 376 */       increment = signum > 0;
/* 377 */       break;
/*     */     case 3:
/* 379 */       increment = signum < 0;
/* 380 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 384 */       long absRem = Math.abs(rem);
/* 385 */       long cmpRemToHalfDivisor = absRem - (Math.abs(q) - absRem);
/*     */ 
/* 388 */       if (cmpRemToHalfDivisor == 0L)
/* 389 */         increment = (mode == RoundingMode.HALF_UP ? 1 : 0) | (mode == RoundingMode.HALF_EVEN ? 1 : 0) & ((div & 1L) != 0L ? 1 : 0);
/*     */       else {
/* 391 */         increment = cmpRemToHalfDivisor > 0L;
/*     */       }
/* 393 */       break;
/*     */     default:
/* 395 */       throw new AssertionError();
/*     */     }
/* 397 */     return increment ? div + signum : div;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static int mod(long x, int m)
/*     */   {
/* 421 */     return (int)mod(x, m);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long mod(long x, long m)
/*     */   {
/* 444 */     if (m <= 0L) {
/* 445 */       throw new ArithmeticException("Modulus must be positive");
/*     */     }
/* 447 */     long result = x % m;
/* 448 */     return result >= 0L ? result : result + m;
/*     */   }
/*     */ 
/*     */   public static long gcd(long a, long b)
/*     */   {
/* 463 */     MathPreconditions.checkNonNegative("a", a);
/* 464 */     MathPreconditions.checkNonNegative("b", b);
/* 465 */     if (a == 0L)
/*     */     {
/* 468 */       return b;
/* 469 */     }if (b == 0L) {
/* 470 */       return a;
/*     */     }
/*     */ 
/* 476 */     int aTwos = Long.numberOfTrailingZeros(a);
/* 477 */     a >>= aTwos;
/* 478 */     int bTwos = Long.numberOfTrailingZeros(b);
/* 479 */     b >>= bTwos;
/* 480 */     while (a != b)
/*     */     {
/* 488 */       long delta = a - b;
/*     */ 
/* 490 */       long minDeltaOrZero = delta & delta >> 63;
/*     */ 
/* 493 */       a = delta - minDeltaOrZero - minDeltaOrZero;
/*     */ 
/* 496 */       b += minDeltaOrZero;
/* 497 */       a >>= Long.numberOfTrailingZeros(a);
/*     */     }
/* 499 */     return a << Math.min(aTwos, bTwos);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedAdd(long a, long b)
/*     */   {
/* 509 */     long result = a + b;
/* 510 */     MathPreconditions.checkNoOverflow(((a ^ b) < 0L ? 1 : 0) | ((a ^ result) >= 0L ? 1 : 0));
/* 511 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedSubtract(long a, long b)
/*     */   {
/* 521 */     long result = a - b;
/* 522 */     MathPreconditions.checkNoOverflow(((a ^ b) >= 0L ? 1 : 0) | ((a ^ result) >= 0L ? 1 : 0));
/* 523 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedMultiply(long a, long b)
/*     */   {
/* 534 */     int leadingZeros = Long.numberOfLeadingZeros(a) + Long.numberOfLeadingZeros(a ^ 0xFFFFFFFF) + Long.numberOfLeadingZeros(b) + Long.numberOfLeadingZeros(b ^ 0xFFFFFFFF);
/*     */ 
/* 546 */     if (leadingZeros > 65) {
/* 547 */       return a * b;
/*     */     }
/* 549 */     MathPreconditions.checkNoOverflow(leadingZeros >= 64);
/* 550 */     MathPreconditions.checkNoOverflow((a >= 0L ? 1 : 0) | (b != -9223372036854775808L ? 1 : 0));
/* 551 */     long result = a * b;
/* 552 */     MathPreconditions.checkNoOverflow((a == 0L) || (result / a == b));
/* 553 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedPow(long b, int k)
/*     */   {
/* 564 */     MathPreconditions.checkNonNegative("exponent", k);
/* 565 */     if (((b >= -2L ? 1 : 0) & (b <= 2L ? 1 : 0)) != 0) {
/* 566 */       switch ((int)b) {
/*     */       case 0:
/* 568 */         return k == 0 ? 1L : 0L;
/*     */       case 1:
/* 570 */         return 1L;
/*     */       case -1:
/* 572 */         return (k & 0x1) == 0 ? 1L : -1L;
/*     */       case 2:
/* 574 */         MathPreconditions.checkNoOverflow(k < 63);
/* 575 */         return 1L << k;
/*     */       case -2:
/* 577 */         MathPreconditions.checkNoOverflow(k < 64);
/* 578 */         return (k & 0x1) == 0 ? 1L << k : -1L << k;
/*     */       }
/* 580 */       throw new AssertionError();
/*     */     }
/*     */ 
/* 583 */     long accum = 1L;
/*     */     while (true) {
/* 585 */       switch (k) {
/*     */       case 0:
/* 587 */         return accum;
/*     */       case 1:
/* 589 */         return checkedMultiply(accum, b);
/*     */       }
/* 591 */       if ((k & 0x1) != 0) {
/* 592 */         accum = checkedMultiply(accum, b);
/*     */       }
/* 594 */       k >>= 1;
/* 595 */       if (k > 0) {
/* 596 */         MathPreconditions.checkNoOverflow(b <= 3037000499L);
/* 597 */         b *= b;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long factorial(int n)
/*     */   {
/* 614 */     MathPreconditions.checkNonNegative("n", n);
/* 615 */     return n < factorials.length ? factorials[n] : 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public static long binomial(int n, int k)
/*     */   {
/* 649 */     MathPreconditions.checkNonNegative("n", n);
/* 650 */     MathPreconditions.checkNonNegative("k", k);
/* 651 */     Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", new Object[] { Integer.valueOf(k), Integer.valueOf(n) });
/* 652 */     if (k > n >> 1) {
/* 653 */       k = n - k;
/*     */     }
/* 655 */     switch (k) {
/*     */     case 0:
/* 657 */       return 1L;
/*     */     case 1:
/* 659 */       return n;
/*     */     }
/* 661 */     if (n < factorials.length)
/* 662 */       return factorials[n] / (factorials[k] * factorials[(n - k)]);
/* 663 */     if ((k >= biggestBinomials.length) || (n > biggestBinomials[k]))
/* 664 */       return 9223372036854775807L;
/* 665 */     if ((k < biggestSimpleBinomials.length) && (n <= biggestSimpleBinomials[k]))
/*     */     {
/* 667 */       long result = n--;
/* 668 */       for (int i = 2; i <= k; i++) {
/* 669 */         result *= n;
/* 670 */         result /= i;
/*     */ 
/* 668 */         n--;
/*     */       }
/*     */ 
/* 672 */       return result;
/*     */     }
/* 674 */     int nBits = log2(n, RoundingMode.CEILING);
/*     */ 
/* 676 */     long result = 1L;
/* 677 */     long numerator = n--;
/* 678 */     long denominator = 1L;
/*     */ 
/* 680 */     int numeratorBits = nBits;
/*     */ 
/* 688 */     for (int i = 2; i <= k; n--) {
/* 689 */       if (numeratorBits + nBits < 63)
/*     */       {
/* 691 */         numerator *= n;
/* 692 */         denominator *= i;
/* 693 */         numeratorBits += nBits;
/*     */       }
/*     */       else
/*     */       {
/* 697 */         result = multiplyFraction(result, numerator, denominator);
/* 698 */         numerator = n;
/* 699 */         denominator = i;
/* 700 */         numeratorBits = nBits;
/*     */       }
/* 688 */       i++;
/*     */     }
/*     */ 
/* 703 */     return multiplyFraction(result, numerator, denominator);
/*     */   }
/*     */ 
/*     */   static long multiplyFraction(long x, long numerator, long denominator)
/*     */   {
/* 712 */     if (x == 1L) {
/* 713 */       return numerator / denominator;
/*     */     }
/* 715 */     long commonDivisor = gcd(x, denominator);
/* 716 */     x /= commonDivisor;
/* 717 */     denominator /= commonDivisor;
/*     */ 
/* 720 */     return x * (numerator / denominator);
/*     */   }
/*     */ 
/*     */   static boolean fitsInInt(long x)
/*     */   {
/* 744 */     return (int)x == x;
/*     */   }
/*     */ 
/*     */   public static long mean(long x, long y)
/*     */   {
/* 757 */     return (x & y) + ((x ^ y) >> 1);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.math.LongMath
 * JD-Core Version:    0.6.2
 */