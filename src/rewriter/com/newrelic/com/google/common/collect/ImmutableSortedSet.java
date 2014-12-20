/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E>
/*     */   implements NavigableSet<E>, SortedIterable<E>
/*     */ {
/*  97 */   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
/*     */ 
/* 100 */   private static final ImmutableSortedSet<Comparable> NATURAL_EMPTY_SET = new EmptyImmutableSortedSet(NATURAL_ORDER);
/*     */   final transient Comparator<? super E> comparator;
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   transient ImmutableSortedSet<E> descendingSet;
/*     */ 
/*     */   private static <E> ImmutableSortedSet<E> emptySet()
/*     */   {
/* 105 */     return NATURAL_EMPTY_SET;
/*     */   }
/*     */ 
/*     */   static <E> ImmutableSortedSet<E> emptySet(Comparator<? super E> comparator)
/*     */   {
/* 110 */     if (NATURAL_ORDER.equals(comparator)) {
/* 111 */       return emptySet();
/*     */     }
/* 113 */     return new EmptyImmutableSortedSet(comparator);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> of()
/*     */   {
/* 121 */     return emptySet();
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E element)
/*     */   {
/* 129 */     return new RegularImmutableSortedSet(ImmutableList.of(element), Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2)
/*     */   {
/* 143 */     return construct(Ordering.natural(), 2, new Comparable[] { e1, e2 });
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3)
/*     */   {
/* 156 */     return construct(Ordering.natural(), 3, new Comparable[] { e1, e2, e3 });
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 169 */     return construct(Ordering.natural(), 4, new Comparable[] { e1, e2, e3, e4 });
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 182 */     return construct(Ordering.natural(), 5, new Comparable[] { e1, e2, e3, e4, e5 });
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] remaining)
/*     */   {
/* 196 */     Comparable[] contents = new Comparable[6 + remaining.length];
/* 197 */     contents[0] = e1;
/* 198 */     contents[1] = e2;
/* 199 */     contents[2] = e3;
/* 200 */     contents[3] = e4;
/* 201 */     contents[4] = e5;
/* 202 */     contents[5] = e6;
/* 203 */     System.arraycopy(remaining, 0, contents, 6, remaining.length);
/* 204 */     return construct(Ordering.natural(), contents.length, (Comparable[])contents);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] elements)
/*     */   {
/* 219 */     return construct(Ordering.natural(), elements.length, (Object[])elements.clone());
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 253 */     Ordering naturalOrder = Ordering.natural();
/* 254 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> elements)
/*     */   {
/* 291 */     Ordering naturalOrder = Ordering.natural();
/* 292 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 311 */     Ordering naturalOrder = Ordering.natural();
/* 312 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements)
/*     */   {
/* 326 */     return new Builder(comparator).addAll(elements).build();
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements)
/*     */   {
/* 344 */     Preconditions.checkNotNull(comparator);
/* 345 */     boolean hasSameComparator = SortedIterables.hasSameComparator(comparator, elements);
/*     */ 
/* 348 */     if ((hasSameComparator) && ((elements instanceof ImmutableSortedSet)))
/*     */     {
/* 350 */       ImmutableSortedSet original = (ImmutableSortedSet)elements;
/* 351 */       if (!original.isPartialView()) {
/* 352 */         return original;
/*     */       }
/*     */     }
/*     */ 
/* 356 */     Object[] array = (Object[])Iterables.toArray(elements);
/* 357 */     return construct(comparator, array.length, array);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Collection<? extends E> elements)
/*     */   {
/* 380 */     return copyOf(comparator, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> sortedSet)
/*     */   {
/* 401 */     Comparator comparator = SortedIterables.comparator(sortedSet);
/* 402 */     ImmutableList list = ImmutableList.copyOf(sortedSet);
/* 403 */     if (list.isEmpty()) {
/* 404 */       return emptySet(comparator);
/*     */     }
/* 406 */     return new RegularImmutableSortedSet(list, comparator);
/*     */   }
/*     */ 
/*     */   static <E> ImmutableSortedSet<E> construct(Comparator<? super E> comparator, int n, E[] contents)
/*     */   {
/* 424 */     if (n == 0) {
/* 425 */       return emptySet(comparator);
/*     */     }
/* 427 */     ObjectArrays.checkElementsNotNull(contents, n);
/* 428 */     Arrays.sort(contents, 0, n, comparator);
/* 429 */     int uniques = 1;
/* 430 */     for (int i = 1; i < n; i++) {
/* 431 */       Object cur = contents[i];
/* 432 */       Object prev = contents[(uniques - 1)];
/* 433 */       if (comparator.compare(cur, prev) != 0) {
/* 434 */         contents[(uniques++)] = cur;
/*     */       }
/*     */     }
/* 437 */     Arrays.fill(contents, uniques, n, null);
/* 438 */     return new RegularImmutableSortedSet(ImmutableList.asImmutableList(contents, uniques), comparator);
/*     */   }
/*     */ 
/*     */   public static <E> Builder<E> orderedBy(Comparator<E> comparator)
/*     */   {
/* 451 */     return new Builder(comparator);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<?>> Builder<E> reverseOrder()
/*     */   {
/* 459 */     return new Builder(Ordering.natural().reverse());
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<?>> Builder<E> naturalOrder()
/*     */   {
/* 470 */     return new Builder(Ordering.natural());
/*     */   }
/*     */ 
/*     */   int unsafeCompare(Object a, Object b)
/*     */   {
/* 569 */     return unsafeCompare(this.comparator, a, b);
/*     */   }
/*     */ 
/*     */   static int unsafeCompare(Comparator<?> comparator, Object a, Object b)
/*     */   {
/* 578 */     Comparator unsafeComparator = comparator;
/* 579 */     return unsafeComparator.compare(a, b);
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet(Comparator<? super E> comparator)
/*     */   {
/* 585 */     this.comparator = comparator;
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/* 597 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   public abstract UnmodifiableIterator<E> iterator();
/*     */ 
/*     */   public ImmutableSortedSet<E> headSet(E toElement)
/*     */   {
/* 616 */     return headSet(toElement, false);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> headSet(E toElement, boolean inclusive)
/*     */   {
/* 625 */     return headSetImpl(Preconditions.checkNotNull(toElement), inclusive);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<E> subSet(E fromElement, E toElement)
/*     */   {
/* 643 */     return subSet(fromElement, true, toElement, false);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/* 653 */     Preconditions.checkNotNull(fromElement);
/* 654 */     Preconditions.checkNotNull(toElement);
/* 655 */     Preconditions.checkArgument(this.comparator.compare(fromElement, toElement) <= 0);
/* 656 */     return subSetImpl(fromElement, fromInclusive, toElement, toInclusive);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<E> tailSet(E fromElement)
/*     */   {
/* 672 */     return tailSet(fromElement, true);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> tailSet(E fromElement, boolean inclusive)
/*     */   {
/* 681 */     return tailSetImpl(Preconditions.checkNotNull(fromElement), inclusive);
/*     */   }
/*     */ 
/*     */   abstract ImmutableSortedSet<E> headSetImpl(E paramE, boolean paramBoolean);
/*     */ 
/*     */   abstract ImmutableSortedSet<E> subSetImpl(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2);
/*     */ 
/*     */   abstract ImmutableSortedSet<E> tailSetImpl(E paramE, boolean paramBoolean);
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E lower(E e)
/*     */   {
/* 701 */     return Iterators.getNext(headSet(e, false).descendingIterator(), null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E floor(E e)
/*     */   {
/* 710 */     return Iterators.getNext(headSet(e, true).descendingIterator(), null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E ceiling(E e)
/*     */   {
/* 719 */     return Iterables.getFirst(tailSet(e, true), null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E higher(E e)
/*     */   {
/* 728 */     return Iterables.getFirst(tailSet(e, false), null);
/*     */   }
/*     */ 
/*     */   public E first()
/*     */   {
/* 733 */     return iterator().next();
/*     */   }
/*     */ 
/*     */   public E last()
/*     */   {
/* 738 */     return descendingIterator().next();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public final E pollFirst()
/*     */   {
/* 752 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public final E pollLast()
/*     */   {
/* 766 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> descendingSet()
/*     */   {
/* 779 */     ImmutableSortedSet result = this.descendingSet;
/* 780 */     if (result == null) {
/* 781 */       result = this.descendingSet = createDescendingSet();
/* 782 */       result.descendingSet = this;
/*     */     }
/* 784 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   ImmutableSortedSet<E> createDescendingSet() {
/* 789 */     return new DescendingImmutableSortedSet(this);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public abstract UnmodifiableIterator<E> descendingIterator();
/*     */ 
/*     */   abstract int indexOf(@Nullable Object paramObject);
/*     */ 
/*     */   private void readObject(ObjectInputStream stream)
/*     */     throws InvalidObjectException
/*     */   {
/* 829 */     throw new InvalidObjectException("Use SerializedForm");
/*     */   }
/*     */ 
/*     */   Object writeReplace() {
/* 833 */     return new SerializedForm(this.comparator, toArray());
/*     */   }
/*     */ 
/*     */   private static class SerializedForm<E>
/*     */     implements Serializable
/*     */   {
/*     */     final Comparator<? super E> comparator;
/*     */     final Object[] elements;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public SerializedForm(Comparator<? super E> comparator, Object[] elements)
/*     */     {
/* 815 */       this.comparator = comparator;
/* 816 */       this.elements = elements;
/*     */     }
/*     */ 
/*     */     Object readResolve()
/*     */     {
/* 821 */       return new ImmutableSortedSet.Builder(this.comparator).add((Object[])this.elements).build();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Builder<E> extends ImmutableSet.Builder<E>
/*     */   {
/*     */     private final Comparator<? super E> comparator;
/*     */ 
/*     */     public Builder(Comparator<? super E> comparator)
/*     */     {
/* 498 */       this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 512 */       super.add(element);
/* 513 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 525 */       super.add(elements);
/* 526 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 538 */       super.addAll(elements);
/* 539 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 551 */       super.addAll(elements);
/* 552 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSortedSet<E> build()
/*     */     {
/* 561 */       Object[] contentsArray = (Object[])this.contents;
/* 562 */       ImmutableSortedSet result = ImmutableSortedSet.construct(this.comparator, this.size, contentsArray);
/* 563 */       this.size = result.size();
/* 564 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableSortedSet
 * JD-Core Version:    0.6.2
 */