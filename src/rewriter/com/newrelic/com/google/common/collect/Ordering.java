/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import com.newrelic.com.google.common.base.Function;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class Ordering<T>
/*     */   implements Comparator<T>
/*     */ {
/*     */   static final int LEFT_IS_GREATER = 1;
/*     */   static final int RIGHT_IS_GREATER = -1;
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <C extends Comparable> Ordering<C> natural()
/*     */   {
/* 106 */     return NaturalOrdering.INSTANCE;
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> from(Comparator<T> comparator)
/*     */   {
/* 124 */     return (comparator instanceof Ordering) ? (Ordering)comparator : new ComparatorOrdering(comparator);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> from(Ordering<T> ordering)
/*     */   {
/* 136 */     return (Ordering)Preconditions.checkNotNull(ordering);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> explicit(List<T> valuesInOrder)
/*     */   {
/* 162 */     return new ExplicitOrdering(valuesInOrder);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> explicit(T leastValue, T[] remainingValuesInOrder)
/*     */   {
/* 191 */     return explicit(Lists.asList(leastValue, remainingValuesInOrder));
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static Ordering<Object> allEqual()
/*     */   {
/* 223 */     return AllEqualOrdering.INSTANCE;
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static Ordering<Object> usingToString()
/*     */   {
/* 235 */     return UsingToStringOrdering.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static Ordering<Object> arbitrary()
/*     */   {
/* 255 */     return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<S> reverse()
/*     */   {
/* 331 */     return new ReverseOrdering(this);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<S> nullsFirst()
/*     */   {
/* 342 */     return new NullsFirstOrdering(this);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<S> nullsLast()
/*     */   {
/* 353 */     return new NullsLastOrdering(this);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <F> Ordering<F> onResultOf(Function<F, ? extends T> function)
/*     */   {
/* 367 */     return new ByFunctionOrdering(function, this);
/*     */   }
/*     */ 
/*     */   <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
/* 371 */     return onResultOf(Maps.keyFunction());
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <U extends T> Ordering<U> compound(Comparator<? super U> secondaryComparator)
/*     */   {
/* 388 */     return new CompoundOrdering(this, (Comparator)Preconditions.checkNotNull(secondaryComparator));
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> comparators)
/*     */   {
/* 409 */     return new CompoundOrdering(comparators);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<Iterable<S>> lexicographical()
/*     */   {
/* 438 */     return new LexicographicalOrdering(this);
/*     */   }
/*     */ 
/*     */   public abstract int compare(@Nullable T paramT1, @Nullable T paramT2);
/*     */ 
/*     */   public <E extends T> E min(Iterator<E> iterator)
/*     */   {
/* 461 */     Object minSoFar = iterator.next();
/*     */ 
/* 463 */     while (iterator.hasNext()) {
/* 464 */       minSoFar = min(minSoFar, iterator.next());
/*     */     }
/*     */ 
/* 467 */     return minSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> E min(Iterable<E> iterable)
/*     */   {
/* 480 */     return min(iterable.iterator());
/*     */   }
/*     */ 
/*     */   public <E extends T> E min(@Nullable E a, @Nullable E b)
/*     */   {
/* 497 */     return compare(a, b) <= 0 ? a : b;
/*     */   }
/*     */ 
/*     */   public <E extends T> E min(@Nullable E a, @Nullable E b, @Nullable E c, E[] rest)
/*     */   {
/* 513 */     Object minSoFar = min(min(a, b), c);
/*     */ 
/* 515 */     for (Object r : rest) {
/* 516 */       minSoFar = min(minSoFar, r);
/*     */     }
/*     */ 
/* 519 */     return minSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> E max(Iterator<E> iterator)
/*     */   {
/* 537 */     Object maxSoFar = iterator.next();
/*     */ 
/* 539 */     while (iterator.hasNext()) {
/* 540 */       maxSoFar = max(maxSoFar, iterator.next());
/*     */     }
/*     */ 
/* 543 */     return maxSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> E max(Iterable<E> iterable)
/*     */   {
/* 556 */     return max(iterable.iterator());
/*     */   }
/*     */ 
/*     */   public <E extends T> E max(@Nullable E a, @Nullable E b)
/*     */   {
/* 573 */     return compare(a, b) >= 0 ? a : b;
/*     */   }
/*     */ 
/*     */   public <E extends T> E max(@Nullable E a, @Nullable E b, @Nullable E c, E[] rest)
/*     */   {
/* 589 */     Object maxSoFar = max(max(a, b), c);
/*     */ 
/* 591 */     for (Object r : rest) {
/* 592 */       maxSoFar = max(maxSoFar, r);
/*     */     }
/*     */ 
/* 595 */     return maxSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> List<E> leastOf(Iterable<E> iterable, int k)
/*     */   {
/* 613 */     if ((iterable instanceof Collection)) {
/* 614 */       Collection collection = (Collection)iterable;
/* 615 */       if (collection.size() <= 2L * k)
/*     */       {
/* 621 */         Object[] array = (Object[])collection.toArray();
/* 622 */         Arrays.sort(array, this);
/* 623 */         if (array.length > k) {
/* 624 */           array = ObjectArrays.arraysCopyOf(array, k);
/*     */         }
/* 626 */         return Collections.unmodifiableList(Arrays.asList(array));
/*     */       }
/*     */     }
/* 629 */     return leastOf(iterable.iterator(), k);
/*     */   }
/*     */ 
/*     */   public <E extends T> List<E> leastOf(Iterator<E> elements, int k)
/*     */   {
/* 647 */     Preconditions.checkNotNull(elements);
/* 648 */     CollectPreconditions.checkNonnegative(k, "k");
/*     */ 
/* 650 */     if ((k == 0) || (!elements.hasNext()))
/* 651 */       return ImmutableList.of();
/* 652 */     if (k >= 1073741823)
/*     */     {
/* 654 */       ArrayList list = Lists.newArrayList(elements);
/* 655 */       Collections.sort(list, this);
/* 656 */       if (list.size() > k) {
/* 657 */         list.subList(k, list.size()).clear();
/*     */       }
/* 659 */       list.trimToSize();
/* 660 */       return Collections.unmodifiableList(list);
/*     */     }
/*     */ 
/* 677 */     int bufferCap = k * 2;
/*     */ 
/* 679 */     Object[] buffer = (Object[])new Object[bufferCap];
/* 680 */     Object threshold = elements.next();
/* 681 */     buffer[0] = threshold;
/* 682 */     int bufferSize = 1;
/*     */ 
/* 686 */     while ((bufferSize < k) && (elements.hasNext())) {
/* 687 */       Object e = elements.next();
/* 688 */       buffer[(bufferSize++)] = e;
/* 689 */       threshold = max(threshold, e);
/*     */     }
/*     */ 
/* 692 */     while (elements.hasNext()) {
/* 693 */       Object e = elements.next();
/* 694 */       if (compare(e, threshold) < 0)
/*     */       {
/* 698 */         buffer[(bufferSize++)] = e;
/* 699 */         if (bufferSize == bufferCap)
/*     */         {
/* 702 */           int left = 0;
/* 703 */           int right = bufferCap - 1;
/*     */ 
/* 705 */           int minThresholdPosition = 0;
/*     */ 
/* 709 */           while (left < right) {
/* 710 */             int pivotIndex = left + right + 1 >>> 1;
/* 711 */             int pivotNewIndex = partition(buffer, left, right, pivotIndex);
/* 712 */             if (pivotNewIndex > k) {
/* 713 */               right = pivotNewIndex - 1; } else {
/* 714 */               if (pivotNewIndex >= k) break;
/* 715 */               left = Math.max(pivotNewIndex, left + 1);
/* 716 */               minThresholdPosition = pivotNewIndex;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 721 */           bufferSize = k;
/*     */ 
/* 723 */           threshold = buffer[minThresholdPosition];
/* 724 */           for (int i = minThresholdPosition + 1; i < bufferSize; i++) {
/* 725 */             threshold = max(threshold, buffer[i]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 730 */     Arrays.sort(buffer, 0, bufferSize, this);
/*     */ 
/* 732 */     bufferSize = Math.min(bufferSize, k);
/* 733 */     return Collections.unmodifiableList(Arrays.asList(ObjectArrays.arraysCopyOf(buffer, bufferSize)));
/*     */   }
/*     */ 
/*     */   private <E extends T> int partition(E[] values, int left, int right, int pivotIndex)
/*     */   {
/* 740 */     Object pivotValue = values[pivotIndex];
/*     */ 
/* 742 */     values[pivotIndex] = values[right];
/* 743 */     values[right] = pivotValue;
/*     */ 
/* 745 */     int storeIndex = left;
/* 746 */     for (int i = left; i < right; i++) {
/* 747 */       if (compare(values[i], pivotValue) < 0) {
/* 748 */         ObjectArrays.swap(values, storeIndex, i);
/* 749 */         storeIndex++;
/*     */       }
/*     */     }
/* 752 */     ObjectArrays.swap(values, right, storeIndex);
/* 753 */     return storeIndex;
/*     */   }
/*     */ 
/*     */   public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k)
/*     */   {
/* 773 */     return reverse().leastOf(iterable, k);
/*     */   }
/*     */ 
/*     */   public <E extends T> List<E> greatestOf(Iterator<E> iterator, int k)
/*     */   {
/* 791 */     return reverse().leastOf(iterator, k);
/*     */   }
/*     */ 
/*     */   public <E extends T> List<E> sortedCopy(Iterable<E> elements)
/*     */   {
/* 814 */     Object[] array = (Object[])Iterables.toArray(elements);
/* 815 */     Arrays.sort(array, this);
/* 816 */     return Lists.newArrayList(Arrays.asList(array));
/*     */   }
/*     */ 
/*     */   public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> elements)
/*     */   {
/* 840 */     Object[] array = (Object[])Iterables.toArray(elements);
/* 841 */     for (Object e : array) {
/* 842 */       Preconditions.checkNotNull(e);
/*     */     }
/* 844 */     Arrays.sort(array, this);
/* 845 */     return ImmutableList.asImmutableList(array);
/*     */   }
/*     */ 
/*     */   public boolean isOrdered(Iterable<? extends T> iterable)
/*     */   {
/* 855 */     Iterator it = iterable.iterator();
/* 856 */     if (it.hasNext()) {
/* 857 */       Object prev = it.next();
/* 858 */       while (it.hasNext()) {
/* 859 */         Object next = it.next();
/* 860 */         if (compare(prev, next) > 0) {
/* 861 */           return false;
/*     */         }
/* 863 */         prev = next;
/*     */       }
/*     */     }
/* 866 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isStrictlyOrdered(Iterable<? extends T> iterable)
/*     */   {
/* 876 */     Iterator it = iterable.iterator();
/* 877 */     if (it.hasNext()) {
/* 878 */       Object prev = it.next();
/* 879 */       while (it.hasNext()) {
/* 880 */         Object next = it.next();
/* 881 */         if (compare(prev, next) >= 0) {
/* 882 */           return false;
/*     */         }
/* 884 */         prev = next;
/*     */       }
/*     */     }
/* 887 */     return true;
/*     */   }
/*     */ 
/*     */   public int binarySearch(List<? extends T> sortedList, @Nullable T key)
/*     */   {
/* 899 */     return Collections.binarySearch(sortedList, key, this);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class IncomparableValueException extends ClassCastException
/*     */   {
/*     */     final Object value;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     IncomparableValueException(Object value)
/*     */     {
/* 914 */       super();
/* 915 */       this.value = value;
/*     */     }
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class ArbitraryOrdering extends Ordering<Object>
/*     */   {
/* 263 */     private Map<Object, Integer> uids = Platform.tryWeakKeys(new MapMaker()).makeComputingMap(new Function()
/*     */     {
/* 267 */       final AtomicInteger counter = new AtomicInteger(0);
/*     */ 
/*     */       public Integer apply(Object from) {
/* 270 */         return Integer.valueOf(this.counter.getAndIncrement());
/*     */       }
/*     */     });
/*     */ 
/*     */     public int compare(Object left, Object right)
/*     */     {
/* 275 */       if (left == right)
/* 276 */         return 0;
/* 277 */       if (left == null)
/* 278 */         return -1;
/* 279 */       if (right == null) {
/* 280 */         return 1;
/*     */       }
/* 282 */       int leftCode = identityHashCode(left);
/* 283 */       int rightCode = identityHashCode(right);
/* 284 */       if (leftCode != rightCode) {
/* 285 */         return leftCode < rightCode ? -1 : 1;
/*     */       }
/*     */ 
/* 289 */       int result = ((Integer)this.uids.get(left)).compareTo((Integer)this.uids.get(right));
/* 290 */       if (result == 0) {
/* 291 */         throw new AssertionError();
/*     */       }
/* 293 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 297 */       return "Ordering.arbitrary()";
/*     */     }
/*     */ 
/*     */     int identityHashCode(Object object)
/*     */     {
/* 309 */       return System.identityHashCode(object);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ArbitraryOrderingHolder
/*     */   {
/* 259 */     static final Ordering<Object> ARBITRARY_ORDERING = new Ordering.ArbitraryOrdering();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Ordering
 * JD-Core Version:    0.6.2
 */