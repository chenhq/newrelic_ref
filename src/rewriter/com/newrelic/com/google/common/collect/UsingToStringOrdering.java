/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class UsingToStringOrdering extends Ordering<Object>
/*    */   implements Serializable
/*    */ {
/* 30 */   static final UsingToStringOrdering INSTANCE = new UsingToStringOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public int compare(Object left, Object right)
/*    */   {
/* 33 */     return left.toString().compareTo(right.toString());
/*    */   }
/*    */ 
/*    */   private Object readResolve()
/*    */   {
/* 38 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 42 */     return "Ordering.usingToString()";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.UsingToStringOrdering
 * JD-Core Version:    0.6.2
 */