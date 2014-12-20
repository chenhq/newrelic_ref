/*    */ package com.newrelic.com.google.common.cache;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.util.concurrent.Executor;
/*    */ 
/*    */ @Beta
/*    */ public final class RemovalListeners
/*    */ {
/*    */   public static <K, V> RemovalListener<K, V> asynchronous(final RemovalListener<K, V> listener, Executor executor)
/*    */   {
/* 46 */     Preconditions.checkNotNull(listener);
/* 47 */     Preconditions.checkNotNull(executor);
/* 48 */     return new RemovalListener()
/*    */     {
/*    */       public void onRemoval(final RemovalNotification<K, V> notification) {
/* 51 */         this.val$executor.execute(new Runnable()
/*    */         {
/*    */           public void run() {
/* 54 */             RemovalListeners.1.this.val$listener.onRemoval(notification);
/*    */           }
/*    */         });
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.RemovalListeners
 * JD-Core Version:    0.6.2
 */