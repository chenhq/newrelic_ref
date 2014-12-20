/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ @Beta
/*     */ @GwtIncompatible("hasn't been tested yet")
/*     */ public abstract class ImmutableSortedMultiset<E> extends ImmutableSortedMultisetFauxverideShim<E>
/*     */   implements SortedMultiset<E>
/*     */ {
/*  86 */   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
/*     */ 
/*  88 */   private static final ImmutableSortedMultiset<Comparable> NATURAL_EMPTY_MULTISET = new EmptyImmutableSortedMultiset(NATURAL_ORDER);
/*     */   transient ImmutableSortedMultiset<E> descendingMultiset;
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> of()
/*     */   {
/*  96 */     return NATURAL_EMPTY_MULTISET;
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E element)
/*     */   {
/* 103 */     RegularImmutableSortedSet elementSet = (RegularImmutableSortedSet)ImmutableSortedSet.of(element);
/*     */ 
/* 105 */     int[] counts = { 1 };
/* 106 */     long[] cumulativeCounts = { 0L, 1L };
/* 107 */     return new RegularImmutableSortedMultiset(elementSet, counts, cumulativeCounts, 0, 1);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2)
/*     */   {
/* 118 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3)
/*     */   {
/* 129 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 141 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3, e4 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 153 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3, e4, e5 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] remaining)
/*     */   {
/* 165 */     int size = remaining.length + 6;
/* 166 */     List all = Lists.newArrayListWithCapacity(size);
/* 167 */     Collections.addAll(all, new Comparable[] { e1, e2, e3, e4, e5, e6 });
/* 168 */     Collections.addAll(all, remaining);
/* 169 */     return copyOf(Ordering.natural(), all);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] elements)
/*     */   {
/* 179 */     return copyOf(Ordering.natural(), Arrays.asList(elements));
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 208 */     Ordering naturalOrder = Ordering.natural();
/* 209 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 226 */     Ordering naturalOrder = Ordering.natural();
/* 227 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements)
/*     */   {
/* 238 */     Preconditions.checkNotNull(comparator);
/* 239 */     return new Builder(comparator).addAll(elements).build();
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements)
/*     */   {
/* 254 */     if ((elements instanceof ImmutableSortedMultiset))
/*     */     {
/* 256 */       ImmutableSortedMultiset multiset = (ImmutableSortedMultiset)elements;
/* 257 */       if (comparator.equals(multiset.comparator())) {
/* 258 */         if (multiset.isPartialView()) {
/* 259 */           return copyOfSortedEntries(comparator, multiset.entrySet().asList());
/*     */         }
/* 261 */         return multiset;
/*     */       }
/*     */     }
/*     */ 
/* 265 */     elements = Lists.newArrayList(elements);
/* 266 */     TreeMultiset sortedCopy = TreeMultiset.create((Comparator)Preconditions.checkNotNull(comparator));
/* 267 */     Iterables.addAll(sortedCopy, elements);
/* 268 */     return copyOfSortedEntries(comparator, sortedCopy.entrySet());
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> sortedMultiset)
/*     */   {
/* 286 */     return copyOfSortedEntries(sortedMultiset.comparator(), Lists.newArrayList(sortedMultiset.entrySet()));
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(Comparator<? super E> comparator, Collection<Multiset.Entry<E>> entries)
/*     */   {
/* 292 */     if (entries.isEmpty()) {
/* 293 */       return emptyMultiset(comparator);
/*     */     }
/* 295 */     ImmutableList.Builder elementsBuilder = new ImmutableList.Builder(entries.size());
/* 296 */     int[] counts = new int[entries.size()];
/* 297 */     long[] cumulativeCounts = new long[entries.size() + 1];
/* 298 */     int i = 0;
/* 299 */     for (Multiset.Entry entry : entries) {
/* 300 */       elementsBuilder.add(entry.getElement());
/* 301 */       counts[i] = entry.getCount();
/* 302 */       cumulativeCounts[(i + 1)] = (cumulativeCounts[i] + counts[i]);
/* 303 */       i++;
/*     */     }
/* 305 */     return new RegularImmutableSortedMultiset(new RegularImmutableSortedSet(elementsBuilder.build(), comparator), counts, cumulativeCounts, 0, entries.size());
/*     */   }
/*     */ 
/*     */   static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> comparator)
/*     */   {
/* 312 */     if (NATURAL_ORDER.equals(comparator)) {
/* 313 */       return NATURAL_EMPTY_MULTISET;
/*     */     }
/* 315 */     return new EmptyImmutableSortedMultiset(comparator);
/*     */   }
/*     */ 
/*     */   public final Comparator<? super E> comparator()
/*     */   {
/* 322 */     return elementSet().comparator();
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedSet<E> elementSet();
/*     */ 
/*     */   public ImmutableSortedMultiset<E> descendingMultiset()
/*     */   {
/* 332 */     ImmutableSortedMultiset result = this.descendingMultiset;
/* 333 */     if (result == null) {
/* 334 */       return this.descendingMultiset = new DescendingImmutableSortedMultiset(this);
/*     */     }
/* 336 */     return result;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final Multiset.Entry<E> pollFirstEntry()
/*     */   {
/* 350 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final Multiset.Entry<E> pollLastEntry()
/*     */   {
/* 364 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedMultiset<E> headMultiset(E paramE, BoundType paramBoundType);
/*     */ 
/*     */   public ImmutableSortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType)
/*     */   {
/* 373 */     Preconditions.checkArgument(comparator().compare(lowerBound, upperBound) <= 0, "Expected lowerBound <= upperBound but %s > %s", new Object[] { lowerBound, upperBound });
/*     */ 
/* 375 */     return tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedMultiset<E> tailMultiset(E paramE, BoundType paramBoundType);
/*     */ 
/*     */   public static <E> Builder<E> orderedBy(Comparator<E> comparator)
/*     */   {
/* 390 */     return new Builder(comparator);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<E>> Builder<E> reverseOrder()
/*     */   {
/* 402 */     return new Builder(Ordering.natural().reverse());
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<E>> Builder<E> naturalOrder()
/*     */   {
/* 416 */     return new Builder(Ordering.natural());
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 573 */     return new SerializedForm(this);
/*     */   }
/*     */ 
/*     */   private static final class SerializedForm<E>
/*     */     implements Serializable
/*     */   {
/*     */     Comparator<? super E> comparator;
/*     */     E[] elements;
/*     */     int[] counts;
/*     */ 
/*     */     SerializedForm(SortedMultiset<E> multiset)
/*     */     {
/* 549 */       this.comparator = multiset.comparator();
/* 550 */       int n = multiset.entrySet().size();
/* 551 */       this.elements = ((Object[])new Object[n]);
/* 552 */       this.counts = new int[n];
/* 553 */       int i = 0;
/* 554 */       for (Multiset.Entry entry : multiset.entrySet()) {
/* 555 */         this.elements[i] = entry.getElement();
/* 556 */         this.counts[i] = entry.getCount();
/* 557 */         i++;
/*     */       }
/*     */     }
/*     */ 
/*     */     Object readResolve() {
/* 562 */       int n = this.elements.length;
/* 563 */       ImmutableSortedMultiset.Builder builder = new ImmutableSortedMultiset.Builder(this.comparator);
/* 564 */       for (int i = 0; i < n; i++) {
/* 565 */         builder.addCopies(this.elements[i], this.counts[i]);
/*     */       }
/* 567 */       return builder.build();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Builder<E> extends ImmutableMultiset.Builder<E>
/*     */   {
/*     */     public Builder(Comparator<? super E> comparator)
/*     */     {
/* 444 */       super();
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 456 */       super.add(element);
/* 457 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addCopies(E element, int occurrences)
/*     */     {
/* 473 */       super.addCopies(element, occurrences);
/* 474 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> setCount(E element, int count)
/*     */     {
/* 489 */       super.setCount(element, count);
/* 490 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 502 */       super.add(elements);
/* 503 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 515 */       super.addAll(elements);
/* 516 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 528 */       super.addAll(elements);
/* 529 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSortedMultiset<E> build()
/*     */     {
/* 538 */       return ImmutableSortedMultiset.copyOfSorted((SortedMultiset)this.contents);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableSortedMultiset
 * JD-Core Version:    0.6.2
 */