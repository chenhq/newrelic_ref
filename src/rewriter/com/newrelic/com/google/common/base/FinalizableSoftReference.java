/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import java.lang.ref.SoftReference;
/*    */ 
/*    */ public abstract class FinalizableSoftReference<T> extends SoftReference<T>
/*    */   implements FinalizableReference
/*    */ {
/*    */   protected FinalizableSoftReference(T referent, FinalizableReferenceQueue queue)
/*    */   {
/* 39 */     super(referent, queue.queue);
/* 40 */     queue.cleanUp();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.FinalizableSoftReference
 * JD-Core Version:    0.6.2
 */