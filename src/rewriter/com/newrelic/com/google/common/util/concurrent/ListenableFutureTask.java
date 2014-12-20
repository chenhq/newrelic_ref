/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.FutureTask;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class ListenableFutureTask<V> extends FutureTask<V>
/*    */   implements ListenableFuture<V>
/*    */ {
/* 43 */   private final ExecutionList executionList = new ExecutionList();
/*    */ 
/*    */   public static <V> ListenableFutureTask<V> create(Callable<V> callable)
/*    */   {
/* 53 */     return new ListenableFutureTask(callable);
/*    */   }
/*    */ 
/*    */   public static <V> ListenableFutureTask<V> create(Runnable runnable, @Nullable V result)
/*    */   {
/* 70 */     return new ListenableFutureTask(runnable, result);
/*    */   }
/*    */ 
/*    */   ListenableFutureTask(Callable<V> callable) {
/* 74 */     super(callable);
/*    */   }
/*    */ 
/*    */   ListenableFutureTask(Runnable runnable, @Nullable V result) {
/* 78 */     super(runnable, result);
/*    */   }
/*    */ 
/*    */   public void addListener(Runnable listener, Executor exec)
/*    */   {
/* 83 */     this.executionList.add(listener, exec);
/*    */   }
/*    */ 
/*    */   protected void done()
/*    */   {
/* 91 */     this.executionList.execute();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.ListenableFutureTask
 * JD-Core Version:    0.6.2
 */