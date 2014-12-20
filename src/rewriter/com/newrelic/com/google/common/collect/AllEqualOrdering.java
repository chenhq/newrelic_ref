/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class AllEqualOrdering extends Ordering<Object>
/*    */   implements Serializable
/*    */ {
/* 33 */   static final AllEqualOrdering INSTANCE = new AllEqualOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public int compare(@Nullable Object left, @Nullable Object right)
/*    */   {
/* 37 */     return 0;
/*    */   }
/*    */ 
/*    */   public <E> List<E> sortedCopy(Iterable<E> iterable)
/*    */   {
/* 42 */     return Lists.newArrayList(iterable);
/*    */   }
/*    */ 
/*    */   public <E> ImmutableList<E> immutableSortedCopy(Iterable<E> iterable)
/*    */   {
/* 47 */     return ImmutableList.copyOf(iterable);
/*    */   }
/*    */ 
/*    */   public <S> Ordering<S> reverse()
/*    */   {
/* 53 */     return this;
/*    */   }
/*    */ 
/*    */   private Object readResolve() {
/* 57 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     return "Ordering.allEqual()";
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AllEqualOrdering
 * JD-Core Version:    0.6.2
 */