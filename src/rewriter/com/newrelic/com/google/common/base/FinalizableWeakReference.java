/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
/*    */ 
/*    */ public abstract class FinalizableWeakReference<T> extends WeakReference<T>
/*    */   implements FinalizableReference
/*    */ {
/*    */   protected FinalizableWeakReference(T referent, FinalizableReferenceQueue queue)
/*    */   {
/* 39 */     super(referent, queue.queue);
/* 40 */     queue.cleanUp();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.FinalizableWeakReference
 * JD-Core Version:    0.6.2
 */