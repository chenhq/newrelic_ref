/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Function;
/*     */ import com.newrelic.com.google.common.base.Optional;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class FluentIterable<E>
/*     */   implements Iterable<E>
/*     */ {
/*     */   private final Iterable<E> iterable;
/*     */ 
/*     */   protected FluentIterable()
/*     */   {
/*  78 */     this.iterable = this;
/*     */   }
/*     */ 
/*     */   FluentIterable(Iterable<E> iterable) {
/*  82 */     this.iterable = ((Iterable)Preconditions.checkNotNull(iterable));
/*     */   }
/*     */ 
/*     */   public static <E> FluentIterable<E> from(final Iterable<E> iterable)
/*     */   {
/*  90 */     return (iterable instanceof FluentIterable) ? (FluentIterable)iterable : new FluentIterable(iterable)
/*     */     {
/*     */       public Iterator<E> iterator()
/*     */       {
/*  94 */         return iterable.iterator();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> FluentIterable<E> from(FluentIterable<E> iterable)
/*     */   {
/* 109 */     return (FluentIterable)Preconditions.checkNotNull(iterable);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 118 */     return Iterables.toString(this.iterable);
/*     */   }
/*     */ 
/*     */   public final int size()
/*     */   {
/* 125 */     return Iterables.size(this.iterable);
/*     */   }
/*     */ 
/*     */   public final boolean contains(@Nullable Object element)
/*     */   {
/* 133 */     return Iterables.contains(this.iterable, element);
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public final FluentIterable<E> cycle()
/*     */   {
/* 151 */     return from(Iterables.cycle(this.iterable));
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public final FluentIterable<E> filter(Predicate<? super E> predicate)
/*     */   {
/* 160 */     return from(Iterables.filter(this.iterable, predicate));
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   @GwtIncompatible("Class.isInstance")
/*     */   public final <T> FluentIterable<T> filter(Class<T> type)
/*     */   {
/* 171 */     return from(Iterables.filter(this.iterable, type));
/*     */   }
/*     */ 
/*     */   public final boolean anyMatch(Predicate<? super E> predicate)
/*     */   {
/* 178 */     return Iterables.any(this.iterable, predicate);
/*     */   }
/*     */ 
/*     */   public final boolean allMatch(Predicate<? super E> predicate)
/*     */   {
/* 186 */     return Iterables.all(this.iterable, predicate);
/*     */   }
/*     */ 
/*     */   public final Optional<E> firstMatch(Predicate<? super E> predicate)
/*     */   {
/* 197 */     return Iterables.tryFind(this.iterable, predicate);
/*     */   }
/*     */ 
/*     */   public final <T> FluentIterable<T> transform(Function<? super E, T> function)
/*     */   {
/* 209 */     return from(Iterables.transform(this.iterable, function));
/*     */   }
/*     */ 
/*     */   public <T> FluentIterable<T> transformAndConcat(Function<? super E, ? extends Iterable<? extends T>> function)
/*     */   {
/* 225 */     return from(Iterables.concat(transform(function)));
/*     */   }
/*     */ 
/*     */   public final Optional<E> first()
/*     */   {
/* 236 */     Iterator iterator = this.iterable.iterator();
/* 237 */     return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.absent();
/*     */   }
/*     */ 
/*     */   public final Optional<E> last()
/*     */   {
/* 253 */     if ((this.iterable instanceof List)) {
/* 254 */       List list = (List)this.iterable;
/* 255 */       if (list.isEmpty()) {
/* 256 */         return Optional.absent();
/*     */       }
/* 258 */       return Optional.of(list.get(list.size() - 1));
/*     */     }
/* 260 */     Iterator iterator = this.iterable.iterator();
/* 261 */     if (!iterator.hasNext()) {
/* 262 */       return Optional.absent();
/*     */     }
/*     */ 
/* 270 */     if ((this.iterable instanceof SortedSet)) {
/* 271 */       SortedSet sortedSet = (SortedSet)this.iterable;
/* 272 */       return Optional.of(sortedSet.last());
/*     */     }
/*     */     while (true)
/*     */     {
/* 276 */       Object current = iterator.next();
/* 277 */       if (!iterator.hasNext())
/* 278 */         return Optional.of(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public final FluentIterable<E> skip(int numberToSkip)
/*     */   {
/* 302 */     return from(Iterables.skip(this.iterable, numberToSkip));
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public final FluentIterable<E> limit(int size)
/*     */   {
/* 317 */     return from(Iterables.limit(this.iterable, size));
/*     */   }
/*     */ 
/*     */   public final boolean isEmpty()
/*     */   {
/* 324 */     return !this.iterable.iterator().hasNext();
/*     */   }
/*     */ 
/*     */   public final ImmutableList<E> toList()
/*     */   {
/* 334 */     return ImmutableList.copyOf(this.iterable);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public final ImmutableList<E> toSortedList(Comparator<? super E> comparator)
/*     */   {
/* 348 */     return Ordering.from(comparator).immutableSortedCopy(this.iterable);
/*     */   }
/*     */ 
/*     */   public final ImmutableSet<E> toSet()
/*     */   {
/* 358 */     return ImmutableSet.copyOf(this.iterable);
/*     */   }
/*     */ 
/*     */   public final ImmutableSortedSet<E> toSortedSet(Comparator<? super E> comparator)
/*     */   {
/* 372 */     return ImmutableSortedSet.copyOf(comparator, this.iterable);
/*     */   }
/*     */ 
/*     */   public final <V> ImmutableMap<E, V> toMap(Function<? super E, V> valueFunction)
/*     */   {
/* 386 */     return Maps.toMap(this.iterable, valueFunction);
/*     */   }
/*     */ 
/*     */   public final <K> ImmutableListMultimap<K, E> index(Function<? super E, K> keyFunction)
/*     */   {
/* 408 */     return Multimaps.index(this.iterable, keyFunction);
/*     */   }
/*     */ 
/*     */   public final <K> ImmutableMap<K, E> uniqueIndex(Function<? super E, K> keyFunction)
/*     */   {
/* 424 */     return Maps.uniqueIndex(this.iterable, keyFunction);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Array.newArray(Class, int)")
/*     */   public final E[] toArray(Class<E> type)
/*     */   {
/* 436 */     return Iterables.toArray(this.iterable, type);
/*     */   }
/*     */ 
/*     */   public final <C extends Collection<? super E>> C copyInto(C collection)
/*     */   {
/* 448 */     Preconditions.checkNotNull(collection);
/*     */     Iterator i$;
/* 449 */     if ((this.iterable instanceof Collection))
/* 450 */       collection.addAll(Collections2.cast(this.iterable));
/*     */     else {
/* 452 */       for (i$ = this.iterable.iterator(); i$.hasNext(); ) { Object item = i$.next();
/* 453 */         collection.add(item);
/*     */       }
/*     */     }
/* 456 */     return collection;
/*     */   }
/*     */ 
/*     */   public final E get(int position)
/*     */   {
/* 468 */     return Iterables.get(this.iterable, position);
/*     */   }
/*     */ 
/*     */   private static class FromIterableFunction<E>
/*     */     implements Function<Iterable<E>, FluentIterable<E>>
/*     */   {
/*     */     public FluentIterable<E> apply(Iterable<E> fromObject)
/*     */     {
/* 478 */       return FluentIterable.from(fromObject);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.FluentIterable
 * JD-Core Version:    0.6.2
 */