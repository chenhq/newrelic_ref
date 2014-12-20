/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ @GwtCompatible
/*    */ abstract class TransformedListIterator<F, T> extends TransformedIterator<F, T>
/*    */   implements ListIterator<T>
/*    */ {
/*    */   TransformedListIterator(ListIterator<? extends F> backingIterator)
/*    */   {
/* 35 */     super(backingIterator);
/*    */   }
/*    */ 
/*    */   private ListIterator<? extends F> backingIterator() {
/* 39 */     return Iterators.cast(this.backingIterator);
/*    */   }
/*    */ 
/*    */   public final boolean hasPrevious()
/*    */   {
/* 44 */     return backingIterator().hasPrevious();
/*    */   }
/*    */ 
/*    */   public final T previous()
/*    */   {
/* 49 */     return transform(backingIterator().previous());
/*    */   }
/*    */ 
/*    */   public final int nextIndex()
/*    */   {
/* 54 */     return backingIterator().nextIndex();
/*    */   }
/*    */ 
/*    */   public final int previousIndex()
/*    */   {
/* 59 */     return backingIterator().previousIndex();
/*    */   }
/*    */ 
/*    */   public void set(T element)
/*    */   {
/* 64 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public void add(T element)
/*    */   {
/* 69 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.TransformedListIterator
 * JD-Core Version:    0.6.2
 */