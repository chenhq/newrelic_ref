/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingObject
/*    */ {
/*    */   protected abstract Object delegate();
/*    */ 
/*    */   public String toString()
/*    */   {
/* 72 */     return delegate().toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingObject
 * JD-Core Version:    0.6.2
 */