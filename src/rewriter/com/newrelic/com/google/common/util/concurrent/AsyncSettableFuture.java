/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ final class AsyncSettableFuture<V> extends ForwardingListenableFuture<V>
/*    */ {
/* 44 */   private final NestedFuture<V> nested = new NestedFuture(null);
/* 45 */   private final ListenableFuture<V> dereferenced = Futures.dereference(this.nested);
/*    */ 
/*    */   public static <V> AsyncSettableFuture<V> create()
/*    */   {
/* 41 */     return new AsyncSettableFuture();
/*    */   }
/*    */ 
/*    */   protected ListenableFuture<V> delegate()
/*    */   {
/* 50 */     return this.dereferenced;
/*    */   }
/*    */ 
/*    */   public boolean setFuture(ListenableFuture<? extends V> future)
/*    */   {
/* 58 */     return this.nested.setFuture((ListenableFuture)Preconditions.checkNotNull(future));
/*    */   }
/*    */ 
/*    */   public boolean setValue(@Nullable V value)
/*    */   {
/* 67 */     return setFuture(Futures.immediateFuture(value));
/*    */   }
/*    */ 
/*    */   public boolean setException(Throwable exception)
/*    */   {
/* 76 */     return setFuture(Futures.immediateFailedFuture(exception));
/*    */   }
/*    */ 
/*    */   public boolean isSet()
/*    */   {
/* 88 */     return this.nested.isDone();
/*    */   }
/*    */ 
/*    */   private static final class NestedFuture<V> extends AbstractFuture<ListenableFuture<? extends V>> {
/*    */     boolean setFuture(ListenableFuture<? extends V> value) {
/* 93 */       boolean result = set(value);
/* 94 */       if (isCancelled()) {
/* 95 */         value.cancel(wasInterrupted());
/*    */       }
/* 97 */       return result;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.AsyncSettableFuture
 * JD-Core Version:    0.6.2
 */