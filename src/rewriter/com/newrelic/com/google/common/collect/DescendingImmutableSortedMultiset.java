/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E>
/*    */ {
/*    */   private final transient ImmutableSortedMultiset<E> forward;
/*    */ 
/*    */   DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> forward)
/*    */   {
/* 29 */     this.forward = forward;
/*    */   }
/*    */ 
/*    */   public int count(@Nullable Object element)
/*    */   {
/* 34 */     return this.forward.count(element);
/*    */   }
/*    */ 
/*    */   public Multiset.Entry<E> firstEntry()
/*    */   {
/* 39 */     return this.forward.lastEntry();
/*    */   }
/*    */ 
/*    */   public Multiset.Entry<E> lastEntry()
/*    */   {
/* 44 */     return this.forward.firstEntry();
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 49 */     return this.forward.size();
/*    */   }
/*    */ 
/*    */   public ImmutableSortedSet<E> elementSet()
/*    */   {
/* 54 */     return this.forward.elementSet().descendingSet();
/*    */   }
/*    */ 
/*    */   Multiset.Entry<E> getEntry(int index)
/*    */   {
/* 59 */     return (Multiset.Entry)this.forward.entrySet().asList().reverse().get(index);
/*    */   }
/*    */ 
/*    */   public ImmutableSortedMultiset<E> descendingMultiset()
/*    */   {
/* 64 */     return this.forward;
/*    */   }
/*    */ 
/*    */   public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*    */   {
/* 69 */     return this.forward.tailMultiset(upperBound, boundType).descendingMultiset();
/*    */   }
/*    */ 
/*    */   public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*    */   {
/* 74 */     return this.forward.headMultiset(lowerBound, boundType).descendingMultiset();
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 79 */     return this.forward.isPartialView();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.DescendingImmutableSortedMultiset
 * JD-Core Version:    0.6.2
 */