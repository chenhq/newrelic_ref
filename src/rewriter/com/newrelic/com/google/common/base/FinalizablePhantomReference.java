/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import java.lang.ref.PhantomReference;
/*    */ 
/*    */ public abstract class FinalizablePhantomReference<T> extends PhantomReference<T>
/*    */   implements FinalizableReference
/*    */ {
/*    */   protected FinalizablePhantomReference(T referent, FinalizableReferenceQueue queue)
/*    */   {
/* 41 */     super(referent, queue.queue);
/* 42 */     queue.cleanUp();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.FinalizablePhantomReference
 * JD-Core Version:    0.6.2
 */