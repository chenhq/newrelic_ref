/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.primitives.Ints;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ final class RegularImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E>
/*     */ {
/*     */   private final transient RegularImmutableSortedSet<E> elementSet;
/*     */   private final transient int[] counts;
/*     */   private final transient long[] cumulativeCounts;
/*     */   private final transient int offset;
/*     */   private final transient int length;
/*     */ 
/*     */   RegularImmutableSortedMultiset(RegularImmutableSortedSet<E> elementSet, int[] counts, long[] cumulativeCounts, int offset, int length)
/*     */   {
/*  44 */     this.elementSet = elementSet;
/*  45 */     this.counts = counts;
/*  46 */     this.cumulativeCounts = cumulativeCounts;
/*  47 */     this.offset = offset;
/*  48 */     this.length = length;
/*     */   }
/*     */ 
/*     */   Multiset.Entry<E> getEntry(int index)
/*     */   {
/*  53 */     return Multisets.immutableEntry(this.elementSet.asList().get(index), this.counts[(this.offset + index)]);
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> firstEntry()
/*     */   {
/*  60 */     return getEntry(0);
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> lastEntry()
/*     */   {
/*  65 */     return getEntry(this.length - 1);
/*     */   }
/*     */ 
/*     */   public int count(@Nullable Object element)
/*     */   {
/*  70 */     int index = this.elementSet.indexOf(element);
/*  71 */     return index == -1 ? 0 : this.counts[(index + this.offset)];
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  76 */     long size = this.cumulativeCounts[(this.offset + this.length)] - this.cumulativeCounts[this.offset];
/*  77 */     return Ints.saturatedCast(size);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<E> elementSet()
/*     */   {
/*  82 */     return this.elementSet;
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */   {
/*  87 */     return getSubMultiset(0, this.elementSet.headIndex(upperBound, Preconditions.checkNotNull(boundType) == BoundType.CLOSED));
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */   {
/*  92 */     return getSubMultiset(this.elementSet.tailIndex(lowerBound, Preconditions.checkNotNull(boundType) == BoundType.CLOSED), this.length);
/*     */   }
/*     */ 
/*     */   ImmutableSortedMultiset<E> getSubMultiset(int from, int to)
/*     */   {
/*  97 */     Preconditions.checkPositionIndexes(from, to, this.length);
/*  98 */     if (from == to)
/*  99 */       return emptyMultiset(comparator());
/* 100 */     if ((from == 0) && (to == this.length)) {
/* 101 */       return this;
/*     */     }
/* 103 */     RegularImmutableSortedSet subElementSet = (RegularImmutableSortedSet)this.elementSet.getSubSet(from, to);
/*     */ 
/* 105 */     return new RegularImmutableSortedMultiset(subElementSet, this.counts, this.cumulativeCounts, this.offset + from, to - from);
/*     */   }
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/* 112 */     return (this.offset > 0) || (this.length < this.counts.length);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.RegularImmutableSortedMultiset
 * JD-Core Version:    0.6.2
 */