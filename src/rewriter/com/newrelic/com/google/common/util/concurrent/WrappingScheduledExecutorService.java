/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.ScheduledFuture;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ abstract class WrappingScheduledExecutorService extends WrappingExecutorService
/*    */   implements ScheduledExecutorService
/*    */ {
/*    */   final ScheduledExecutorService delegate;
/*    */ 
/*    */   protected WrappingScheduledExecutorService(ScheduledExecutorService delegate)
/*    */   {
/* 36 */     super(delegate);
/* 37 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */   public final ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
/*    */   {
/* 42 */     return this.delegate.schedule(wrapTask(command), delay, unit);
/*    */   }
/*    */ 
/*    */   public final <V> ScheduledFuture<V> schedule(Callable<V> task, long delay, TimeUnit unit)
/*    */   {
/* 47 */     return this.delegate.schedule(wrapTask(task), delay, unit);
/*    */   }
/*    */ 
/*    */   public final ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
/*    */   {
/* 53 */     return this.delegate.scheduleAtFixedRate(wrapTask(command), initialDelay, period, unit);
/*    */   }
/*    */ 
/*    */   public final ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
/*    */   {
/* 59 */     return this.delegate.scheduleWithFixedDelay(wrapTask(command), initialDelay, delay, unit);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.WrappingScheduledExecutorService
 * JD-Core Version:    0.6.2
 */