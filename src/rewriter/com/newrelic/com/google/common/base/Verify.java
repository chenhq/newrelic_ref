/*     */ package com.newrelic.com.google.common.base;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class Verify
/*     */ {
/*     */   public static void verify(boolean expression)
/*     */   {
/*  97 */     if (!expression)
/*  98 */       throw new VerifyException();
/*     */   }
/*     */ 
/*     */   public static void verify(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object[] errorMessageArgs)
/*     */   {
/* 122 */     if (!expression)
/* 123 */       throw new VerifyException(Preconditions.format(errorMessageTemplate, errorMessageArgs));
/*     */   }
/*     */ 
/*     */   public static <T> T verifyNotNull(@Nullable T reference)
/*     */   {
/* 134 */     return verifyNotNull(reference, "expected a non-null reference", new Object[0]);
/*     */   }
/*     */ 
/*     */   public static <T> T verifyNotNull(@Nullable T reference, @Nullable String errorMessageTemplate, @Nullable Object[] errorMessageArgs)
/*     */   {
/* 156 */     verify(reference != null, errorMessageTemplate, errorMessageArgs);
/* 157 */     return reference;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Verify
 * JD-Core Version:    0.6.2
 */