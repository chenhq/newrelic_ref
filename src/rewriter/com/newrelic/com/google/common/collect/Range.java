/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Function;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Range<C extends Comparable>
/*     */   implements Predicate<C>, Serializable
/*     */ {
/* 117 */   private static final Function<Range, Cut> LOWER_BOUND_FN = new Function()
/*     */   {
/*     */     public Cut apply(Range range) {
/* 120 */       return range.lowerBound;
/*     */     }
/* 117 */   };
/*     */ 
/* 129 */   private static final Function<Range, Cut> UPPER_BOUND_FN = new Function()
/*     */   {
/*     */     public Cut apply(Range range) {
/* 132 */       return range.upperBound;
/*     */     }
/* 129 */   };
/*     */ 
/* 141 */   static final Ordering<Range<?>> RANGE_LEX_ORDERING = new Ordering()
/*     */   {
/*     */     public int compare(Range<?> left, Range<?> right) {
/* 144 */       return ComparisonChain.start().compare(left.lowerBound, right.lowerBound).compare(left.upperBound, right.upperBound).result();
/*     */     }
/* 141 */   };
/*     */ 
/* 305 */   private static final Range<Comparable> ALL = new Range(Cut.belowAll(), Cut.aboveAll());
/*     */   final Cut<C> lowerBound;
/*     */   final Cut<C> upperBound;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> lowerBoundFn()
/*     */   {
/* 126 */     return LOWER_BOUND_FN;
/*     */   }
/*     */ 
/*     */   static <C extends Comparable<?>> Function<Range<C>, Cut<C>> upperBoundFn()
/*     */   {
/* 138 */     return UPPER_BOUND_FN;
/*     */   }
/*     */ 
/*     */   static <C extends Comparable<?>> Range<C> create(Cut<C> lowerBound, Cut<C> upperBound)
/*     */   {
/* 153 */     return new Range(lowerBound, upperBound);
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> open(C lower, C upper)
/*     */   {
/* 165 */     return create(Cut.aboveValue(lower), Cut.belowValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> closed(C lower, C upper)
/*     */   {
/* 177 */     return create(Cut.belowValue(lower), Cut.aboveValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> closedOpen(C lower, C upper)
/*     */   {
/* 190 */     return create(Cut.belowValue(lower), Cut.belowValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> openClosed(C lower, C upper)
/*     */   {
/* 203 */     return create(Cut.aboveValue(lower), Cut.aboveValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> range(C lower, BoundType lowerType, C upper, BoundType upperType)
/*     */   {
/* 217 */     Preconditions.checkNotNull(lowerType);
/* 218 */     Preconditions.checkNotNull(upperType);
/*     */ 
/* 220 */     Cut lowerBound = lowerType == BoundType.OPEN ? Cut.aboveValue(lower) : Cut.belowValue(lower);
/*     */ 
/* 223 */     Cut upperBound = upperType == BoundType.OPEN ? Cut.belowValue(upper) : Cut.aboveValue(upper);
/*     */ 
/* 226 */     return create(lowerBound, upperBound);
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> lessThan(C endpoint)
/*     */   {
/* 236 */     return create(Cut.belowAll(), Cut.belowValue(endpoint));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> atMost(C endpoint)
/*     */   {
/* 246 */     return create(Cut.belowAll(), Cut.aboveValue(endpoint));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> upTo(C endpoint, BoundType boundType)
/*     */   {
/* 257 */     switch (4.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */     case 1:
/* 259 */       return lessThan(endpoint);
/*     */     case 2:
/* 261 */       return atMost(endpoint);
/*     */     }
/* 263 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> greaterThan(C endpoint)
/*     */   {
/* 274 */     return create(Cut.aboveValue(endpoint), Cut.aboveAll());
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> atLeast(C endpoint)
/*     */   {
/* 284 */     return create(Cut.belowValue(endpoint), Cut.aboveAll());
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> downTo(C endpoint, BoundType boundType)
/*     */   {
/* 295 */     switch (4.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */     case 1:
/* 297 */       return greaterThan(endpoint);
/*     */     case 2:
/* 299 */       return atLeast(endpoint);
/*     */     }
/* 301 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> all()
/*     */   {
/* 315 */     return ALL;
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> singleton(C value)
/*     */   {
/* 326 */     return closed(value, value);
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> values)
/*     */   {
/* 342 */     Preconditions.checkNotNull(values);
/* 343 */     if ((values instanceof ContiguousSet)) {
/* 344 */       return ((ContiguousSet)values).range();
/*     */     }
/* 346 */     Iterator valueIterator = values.iterator();
/* 347 */     Comparable min = (Comparable)Preconditions.checkNotNull(valueIterator.next());
/* 348 */     Comparable max = min;
/* 349 */     while (valueIterator.hasNext()) {
/* 350 */       Comparable value = (Comparable)Preconditions.checkNotNull(valueIterator.next());
/* 351 */       min = (Comparable)Ordering.natural().min(min, value);
/* 352 */       max = (Comparable)Ordering.natural().max(max, value);
/*     */     }
/* 354 */     return closed(min, max);
/*     */   }
/*     */ 
/*     */   private Range(Cut<C> lowerBound, Cut<C> upperBound)
/*     */   {
/* 361 */     if ((lowerBound.compareTo(upperBound) > 0) || (lowerBound == Cut.aboveAll()) || (upperBound == Cut.belowAll()))
/*     */     {
/* 363 */       throw new IllegalArgumentException("Invalid range: " + toString(lowerBound, upperBound));
/*     */     }
/* 365 */     this.lowerBound = ((Cut)Preconditions.checkNotNull(lowerBound));
/* 366 */     this.upperBound = ((Cut)Preconditions.checkNotNull(upperBound));
/*     */   }
/*     */ 
/*     */   public boolean hasLowerBound()
/*     */   {
/* 373 */     return this.lowerBound != Cut.belowAll();
/*     */   }
/*     */ 
/*     */   public C lowerEndpoint()
/*     */   {
/* 383 */     return this.lowerBound.endpoint();
/*     */   }
/*     */ 
/*     */   public BoundType lowerBoundType()
/*     */   {
/* 394 */     return this.lowerBound.typeAsLowerBound();
/*     */   }
/*     */ 
/*     */   public boolean hasUpperBound()
/*     */   {
/* 401 */     return this.upperBound != Cut.aboveAll();
/*     */   }
/*     */ 
/*     */   public C upperEndpoint()
/*     */   {
/* 411 */     return this.upperBound.endpoint();
/*     */   }
/*     */ 
/*     */   public BoundType upperBoundType()
/*     */   {
/* 422 */     return this.upperBound.typeAsUpperBound();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 435 */     return this.lowerBound.equals(this.upperBound);
/*     */   }
/*     */ 
/*     */   public boolean contains(C value)
/*     */   {
/* 444 */     Preconditions.checkNotNull(value);
/*     */ 
/* 446 */     return (this.lowerBound.isLessThan(value)) && (!this.upperBound.isLessThan(value));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean apply(C input)
/*     */   {
/* 456 */     return contains(input);
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Iterable<? extends C> values)
/*     */   {
/* 464 */     if (Iterables.isEmpty(values)) {
/* 465 */       return true;
/*     */     }
/*     */ 
/* 469 */     if ((values instanceof SortedSet)) {
/* 470 */       SortedSet set = cast(values);
/* 471 */       Comparator comparator = set.comparator();
/* 472 */       if ((Ordering.natural().equals(comparator)) || (comparator == null)) {
/* 473 */         return (contains((Comparable)set.first())) && (contains((Comparable)set.last()));
/*     */       }
/*     */     }
/*     */ 
/* 477 */     for (Comparable value : values) {
/* 478 */       if (!contains(value)) {
/* 479 */         return false;
/*     */       }
/*     */     }
/* 482 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean encloses(Range<C> other)
/*     */   {
/* 510 */     return (this.lowerBound.compareTo(other.lowerBound) <= 0) && (this.upperBound.compareTo(other.upperBound) >= 0);
/*     */   }
/*     */ 
/*     */   public boolean isConnected(Range<C> other)
/*     */   {
/* 539 */     return (this.lowerBound.compareTo(other.upperBound) <= 0) && (other.lowerBound.compareTo(this.upperBound) <= 0);
/*     */   }
/*     */ 
/*     */   public Range<C> intersection(Range<C> connectedRange)
/*     */   {
/* 560 */     int lowerCmp = this.lowerBound.compareTo(connectedRange.lowerBound);
/* 561 */     int upperCmp = this.upperBound.compareTo(connectedRange.upperBound);
/* 562 */     if ((lowerCmp >= 0) && (upperCmp <= 0))
/* 563 */       return this;
/* 564 */     if ((lowerCmp <= 0) && (upperCmp >= 0)) {
/* 565 */       return connectedRange;
/*     */     }
/* 567 */     Cut newLower = lowerCmp >= 0 ? this.lowerBound : connectedRange.lowerBound;
/* 568 */     Cut newUpper = upperCmp <= 0 ? this.upperBound : connectedRange.upperBound;
/* 569 */     return create(newLower, newUpper);
/*     */   }
/*     */ 
/*     */   public Range<C> span(Range<C> other)
/*     */   {
/* 585 */     int lowerCmp = this.lowerBound.compareTo(other.lowerBound);
/* 586 */     int upperCmp = this.upperBound.compareTo(other.upperBound);
/* 587 */     if ((lowerCmp <= 0) && (upperCmp >= 0))
/* 588 */       return this;
/* 589 */     if ((lowerCmp >= 0) && (upperCmp <= 0)) {
/* 590 */       return other;
/*     */     }
/* 592 */     Cut newLower = lowerCmp <= 0 ? this.lowerBound : other.lowerBound;
/* 593 */     Cut newUpper = upperCmp >= 0 ? this.upperBound : other.upperBound;
/* 594 */     return create(newLower, newUpper);
/*     */   }
/*     */ 
/*     */   public Range<C> canonical(DiscreteDomain<C> domain)
/*     */   {
/* 623 */     Preconditions.checkNotNull(domain);
/* 624 */     Cut lower = this.lowerBound.canonical(domain);
/* 625 */     Cut upper = this.upperBound.canonical(domain);
/* 626 */     return (lower == this.lowerBound) && (upper == this.upperBound) ? this : create(lower, upper);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 637 */     if ((object instanceof Range)) {
/* 638 */       Range other = (Range)object;
/* 639 */       return (this.lowerBound.equals(other.lowerBound)) && (this.upperBound.equals(other.upperBound));
/*     */     }
/*     */ 
/* 642 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 647 */     return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 655 */     return toString(this.lowerBound, this.upperBound);
/*     */   }
/*     */ 
/*     */   private static String toString(Cut<?> lowerBound, Cut<?> upperBound) {
/* 659 */     StringBuilder sb = new StringBuilder(16);
/* 660 */     lowerBound.describeAsLowerBound(sb);
/* 661 */     sb.append('‥');
/* 662 */     upperBound.describeAsUpperBound(sb);
/* 663 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private static <T> SortedSet<T> cast(Iterable<T> iterable)
/*     */   {
/* 670 */     return (SortedSet)iterable;
/*     */   }
/*     */ 
/*     */   Object readResolve() {
/* 674 */     if (equals(ALL)) {
/* 675 */       return all();
/*     */     }
/* 677 */     return this;
/*     */   }
/*     */ 
/*     */   static int compareOrThrow(Comparable left, Comparable right)
/*     */   {
/* 683 */     return left.compareTo(right);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Range
 * JD-Core Version:    0.6.2
 */