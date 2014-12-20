/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NavigableSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ abstract class AbstractSortedMultiset<E> extends AbstractMultiset<E>
/*     */   implements SortedMultiset<E>
/*     */ {
/*     */ 
/*     */   @GwtTransient
/*     */   final Comparator<? super E> comparator;
/*     */   private transient SortedMultiset<E> descendingMultiset;
/*     */ 
/*     */   AbstractSortedMultiset()
/*     */   {
/*  43 */     this(Ordering.natural());
/*     */   }
/*     */ 
/*     */   AbstractSortedMultiset(Comparator<? super E> comparator) {
/*  47 */     this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */   }
/*     */ 
/*     */   public NavigableSet<E> elementSet()
/*     */   {
/*  52 */     return (NavigableSet)super.elementSet();
/*     */   }
/*     */ 
/*     */   NavigableSet<E> createElementSet()
/*     */   {
/*  57 */     return new SortedMultisets.NavigableElementSet(this);
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/*  62 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> firstEntry()
/*     */   {
/*  67 */     Iterator entryIterator = entryIterator();
/*  68 */     return entryIterator.hasNext() ? (Multiset.Entry)entryIterator.next() : null;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> lastEntry()
/*     */   {
/*  73 */     Iterator entryIterator = descendingEntryIterator();
/*  74 */     return entryIterator.hasNext() ? (Multiset.Entry)entryIterator.next() : null;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> pollFirstEntry()
/*     */   {
/*  79 */     Iterator entryIterator = entryIterator();
/*  80 */     if (entryIterator.hasNext()) {
/*  81 */       Multiset.Entry result = (Multiset.Entry)entryIterator.next();
/*  82 */       result = Multisets.immutableEntry(result.getElement(), result.getCount());
/*  83 */       entryIterator.remove();
/*  84 */       return result;
/*     */     }
/*  86 */     return null;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> pollLastEntry()
/*     */   {
/*  91 */     Iterator entryIterator = descendingEntryIterator();
/*  92 */     if (entryIterator.hasNext()) {
/*  93 */       Multiset.Entry result = (Multiset.Entry)entryIterator.next();
/*  94 */       result = Multisets.immutableEntry(result.getElement(), result.getCount());
/*  95 */       entryIterator.remove();
/*  96 */       return result;
/*     */     }
/*  98 */     return null;
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> subMultiset(@Nullable E fromElement, BoundType fromBoundType, @Nullable E toElement, BoundType toBoundType)
/*     */   {
/* 105 */     Preconditions.checkNotNull(fromBoundType);
/* 106 */     Preconditions.checkNotNull(toBoundType);
/* 107 */     return tailMultiset(fromElement, fromBoundType).headMultiset(toElement, toBoundType);
/*     */   }
/*     */ 
/*     */   abstract Iterator<Multiset.Entry<E>> descendingEntryIterator();
/*     */ 
/*     */   Iterator<E> descendingIterator() {
/* 113 */     return Multisets.iteratorImpl(descendingMultiset());
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> descendingMultiset()
/*     */   {
/* 120 */     SortedMultiset result = this.descendingMultiset;
/* 121 */     return result == null ? (this.descendingMultiset = createDescendingMultiset()) : result;
/*     */   }
/*     */ 
/*     */   SortedMultiset<E> createDescendingMultiset() {
/* 125 */     return new DescendingMultiset()
/*     */     {
/*     */       SortedMultiset<E> forwardMultiset() {
/* 128 */         return AbstractSortedMultiset.this;
/*     */       }
/*     */ 
/*     */       Iterator<Multiset.Entry<E>> entryIterator()
/*     */       {
/* 133 */         return AbstractSortedMultiset.this.descendingEntryIterator();
/*     */       }
/*     */ 
/*     */       public Iterator<E> iterator()
/*     */       {
/* 138 */         return AbstractSortedMultiset.this.descendingIterator();
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractSortedMultiset
 * JD-Core Version:    0.6.2
 */