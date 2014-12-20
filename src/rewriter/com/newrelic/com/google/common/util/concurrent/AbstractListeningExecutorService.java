/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import java.util.concurrent.AbstractExecutorService;
/*    */ import java.util.concurrent.Callable;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public abstract class AbstractListeningExecutorService extends AbstractExecutorService
/*    */   implements ListeningExecutorService
/*    */ {
/*    */   protected final <T> ListenableFutureTask<T> newTaskFor(Runnable runnable, T value)
/*    */   {
/* 42 */     return ListenableFutureTask.create(runnable, value);
/*    */   }
/*    */ 
/*    */   protected final <T> ListenableFutureTask<T> newTaskFor(Callable<T> callable) {
/* 46 */     return ListenableFutureTask.create(callable);
/*    */   }
/*    */ 
/*    */   public ListenableFuture<?> submit(Runnable task) {
/* 50 */     return (ListenableFuture)super.submit(task);
/*    */   }
/*    */ 
/*    */   public <T> ListenableFuture<T> submit(Runnable task, @Nullable T result) {
/* 54 */     return (ListenableFuture)super.submit(task, result);
/*    */   }
/*    */ 
/*    */   public <T> ListenableFuture<T> submit(Callable<T> task) {
/* 58 */     return (ListenableFuture)super.submit(task);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.AbstractListeningExecutorService
 * JD-Core Version:    0.6.2
 */