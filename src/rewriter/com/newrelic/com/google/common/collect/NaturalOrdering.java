/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class NaturalOrdering extends Ordering<Comparable>
/*    */   implements Serializable
/*    */ {
/* 30 */   static final NaturalOrdering INSTANCE = new NaturalOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public int compare(Comparable left, Comparable right)
/*    */   {
/* 33 */     Preconditions.checkNotNull(left);
/* 34 */     Preconditions.checkNotNull(right);
/* 35 */     return left.compareTo(right);
/*    */   }
/*    */ 
/*    */   public <S extends Comparable> Ordering<S> reverse() {
/* 39 */     return ReverseNaturalOrdering.INSTANCE;
/*    */   }
/*    */ 
/*    */   private Object readResolve()
/*    */   {
/* 44 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 48 */     return "Ordering.natural()";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.NaturalOrdering
 * JD-Core Version:    0.6.2
 */