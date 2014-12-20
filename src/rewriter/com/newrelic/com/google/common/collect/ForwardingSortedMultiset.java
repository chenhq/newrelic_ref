/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ForwardingSortedMultiset<E> extends ForwardingMultiset<E>
/*     */   implements SortedMultiset<E>
/*     */ {
/*     */   protected abstract SortedMultiset<E> delegate();
/*     */ 
/*     */   public NavigableSet<E> elementSet()
/*     */   {
/*  54 */     return (NavigableSet)super.elementSet();
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/*  77 */     return delegate().comparator();
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> descendingMultiset()
/*     */   {
/*  82 */     return delegate().descendingMultiset();
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> firstEntry()
/*     */   {
/* 108 */     return delegate().firstEntry();
/*     */   }
/*     */ 
/*     */   protected Multiset.Entry<E> standardFirstEntry()
/*     */   {
/* 118 */     Iterator entryIterator = entrySet().iterator();
/* 119 */     if (!entryIterator.hasNext()) {
/* 120 */       return null;
/*     */     }
/* 122 */     Multiset.Entry entry = (Multiset.Entry)entryIterator.next();
/* 123 */     return Multisets.immutableEntry(entry.getElement(), entry.getCount());
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> lastEntry()
/*     */   {
/* 128 */     return delegate().lastEntry();
/*     */   }
/*     */ 
/*     */   protected Multiset.Entry<E> standardLastEntry()
/*     */   {
/* 139 */     Iterator entryIterator = descendingMultiset().entrySet().iterator();
/*     */ 
/* 142 */     if (!entryIterator.hasNext()) {
/* 143 */       return null;
/*     */     }
/* 145 */     Multiset.Entry entry = (Multiset.Entry)entryIterator.next();
/* 146 */     return Multisets.immutableEntry(entry.getElement(), entry.getCount());
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> pollFirstEntry()
/*     */   {
/* 151 */     return delegate().pollFirstEntry();
/*     */   }
/*     */ 
/*     */   protected Multiset.Entry<E> standardPollFirstEntry()
/*     */   {
/* 161 */     Iterator entryIterator = entrySet().iterator();
/* 162 */     if (!entryIterator.hasNext()) {
/* 163 */       return null;
/*     */     }
/* 165 */     Multiset.Entry entry = (Multiset.Entry)entryIterator.next();
/* 166 */     entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
/* 167 */     entryIterator.remove();
/* 168 */     return entry;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> pollLastEntry()
/*     */   {
/* 173 */     return delegate().pollLastEntry();
/*     */   }
/*     */ 
/*     */   protected Multiset.Entry<E> standardPollLastEntry()
/*     */   {
/* 184 */     Iterator entryIterator = descendingMultiset().entrySet().iterator();
/*     */ 
/* 187 */     if (!entryIterator.hasNext()) {
/* 188 */       return null;
/*     */     }
/* 190 */     Multiset.Entry entry = (Multiset.Entry)entryIterator.next();
/* 191 */     entry = Multisets.immutableEntry(entry.getElement(), entry.getCount());
/* 192 */     entryIterator.remove();
/* 193 */     return entry;
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */   {
/* 198 */     return delegate().headMultiset(upperBound, boundType);
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType)
/*     */   {
/* 204 */     return delegate().subMultiset(lowerBound, lowerBoundType, upperBound, upperBoundType);
/*     */   }
/*     */ 
/*     */   protected SortedMultiset<E> standardSubMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType)
/*     */   {
/* 217 */     return tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */   {
/* 222 */     return delegate().tailMultiset(lowerBound, boundType);
/*     */   }
/*     */ 
/*     */   protected abstract class StandardDescendingMultiset extends DescendingMultiset<E>
/*     */   {
/*     */     public StandardDescendingMultiset()
/*     */     {
/*     */     }
/*     */ 
/*     */     SortedMultiset<E> forwardMultiset()
/*     */     {
/* 102 */       return ForwardingSortedMultiset.this;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected class StandardElementSet extends SortedMultisets.NavigableElementSet<E>
/*     */   {
/*     */     public StandardElementSet()
/*     */     {
/*  71 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingSortedMultiset
 * JD-Core Version:    0.6.2
 */