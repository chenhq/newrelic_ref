/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ @Beta
/*    */ public final class FakeTimeLimiter
/*    */   implements TimeLimiter
/*    */ {
/*    */   public <T> T newProxy(T target, Class<T> interfaceType, long timeoutDuration, TimeUnit timeoutUnit)
/*    */   {
/* 41 */     Preconditions.checkNotNull(target);
/* 42 */     Preconditions.checkNotNull(interfaceType);
/* 43 */     Preconditions.checkNotNull(timeoutUnit);
/* 44 */     return target;
/*    */   }
/*    */ 
/*    */   public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible)
/*    */     throws Exception
/*    */   {
/* 50 */     Preconditions.checkNotNull(timeoutUnit);
/* 51 */     return callable.call();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.FakeTimeLimiter
 * JD-Core Version:    0.6.2
 */