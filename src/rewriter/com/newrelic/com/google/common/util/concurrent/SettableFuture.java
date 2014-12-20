/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public final class SettableFuture<V> extends AbstractFuture<V>
/*    */ {
/*    */   public static <V> SettableFuture<V> create()
/*    */   {
/* 34 */     return new SettableFuture();
/*    */   }
/*    */ 
/*    */   public boolean set(@Nullable V value)
/*    */   {
/* 53 */     return super.set(value);
/*    */   }
/*    */ 
/*    */   public boolean setException(Throwable throwable)
/*    */   {
/* 68 */     return super.setException(throwable);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.SettableFuture
 * JD-Core Version:    0.6.2
 */