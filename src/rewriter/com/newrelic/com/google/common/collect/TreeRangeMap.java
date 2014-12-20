/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Objects;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.base.Predicates;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtIncompatible("NavigableMap")
/*     */ public final class TreeRangeMap<K extends Comparable, V>
/*     */   implements RangeMap<K, V>
/*     */ {
/*     */   private final NavigableMap<Cut<K>, RangeMapEntry<K, V>> entriesByLowerBound;
/* 262 */   private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap()
/*     */   {
/*     */     @Nullable
/*     */     public Object get(Comparable key)
/*     */     {
/* 267 */       return null;
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     public Map.Entry<Range, Object> getEntry(Comparable key)
/*     */     {
/* 273 */       return null;
/*     */     }
/*     */ 
/*     */     public Range span()
/*     */     {
/* 278 */       throw new NoSuchElementException();
/*     */     }
/*     */ 
/*     */     public void put(Range range, Object value)
/*     */     {
/* 283 */       Preconditions.checkNotNull(range);
/* 284 */       throw new IllegalArgumentException("Cannot insert range " + range + " into an empty subRangeMap");
/*     */     }
/*     */ 
/*     */     public void putAll(RangeMap rangeMap)
/*     */     {
/* 290 */       if (!rangeMap.asMapOfRanges().isEmpty())
/* 291 */         throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap");
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void remove(Range range)
/*     */     {
/* 301 */       Preconditions.checkNotNull(range);
/*     */     }
/*     */ 
/*     */     public Map<Range, Object> asMapOfRanges()
/*     */     {
/* 306 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     public RangeMap subRangeMap(Range range)
/*     */     {
/* 311 */       Preconditions.checkNotNull(range);
/* 312 */       return this;
/*     */     }
/* 262 */   };
/*     */ 
/*     */   public static <K extends Comparable, V> TreeRangeMap<K, V> create()
/*     */   {
/*  61 */     return new TreeRangeMap();
/*     */   }
/*     */ 
/*     */   private TreeRangeMap() {
/*  65 */     this.entriesByLowerBound = Maps.newTreeMap();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public V get(K key)
/*     */   {
/* 108 */     Map.Entry entry = getEntry(key);
/* 109 */     return entry == null ? null : entry.getValue();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<Range<K>, V> getEntry(K key)
/*     */   {
/* 115 */     Map.Entry mapEntry = this.entriesByLowerBound.floorEntry(Cut.belowValue(key));
/*     */ 
/* 117 */     if ((mapEntry != null) && (((RangeMapEntry)mapEntry.getValue()).contains(key))) {
/* 118 */       return (Map.Entry)mapEntry.getValue();
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */ 
/*     */   public void put(Range<K> range, V value)
/*     */   {
/* 126 */     if (!range.isEmpty()) {
/* 127 */       Preconditions.checkNotNull(value);
/* 128 */       remove(range);
/* 129 */       this.entriesByLowerBound.put(range.lowerBound, new RangeMapEntry(range, value));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void putAll(RangeMap<K, V> rangeMap)
/*     */   {
/* 135 */     for (Map.Entry entry : rangeMap.asMapOfRanges().entrySet())
/* 136 */       put((Range)entry.getKey(), entry.getValue());
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 142 */     this.entriesByLowerBound.clear();
/*     */   }
/*     */ 
/*     */   public Range<K> span()
/*     */   {
/* 147 */     Map.Entry firstEntry = this.entriesByLowerBound.firstEntry();
/* 148 */     Map.Entry lastEntry = this.entriesByLowerBound.lastEntry();
/* 149 */     if (firstEntry == null) {
/* 150 */       throw new NoSuchElementException();
/*     */     }
/* 152 */     return Range.create(((RangeMapEntry)firstEntry.getValue()).getKey().lowerBound, ((RangeMapEntry)lastEntry.getValue()).getKey().upperBound);
/*     */   }
/*     */ 
/*     */   private void putRangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, V value)
/*     */   {
/* 158 */     this.entriesByLowerBound.put(lowerBound, new RangeMapEntry(lowerBound, upperBound, value));
/*     */   }
/*     */ 
/*     */   public void remove(Range<K> rangeToRemove)
/*     */   {
/* 163 */     if (rangeToRemove.isEmpty()) {
/* 164 */       return;
/*     */     }
/*     */ 
/* 171 */     Map.Entry mapEntryBelowToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.lowerBound);
/*     */ 
/* 173 */     if (mapEntryBelowToTruncate != null)
/*     */     {
/* 175 */       RangeMapEntry rangeMapEntry = (RangeMapEntry)mapEntryBelowToTruncate.getValue();
/* 176 */       if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.lowerBound) > 0)
/*     */       {
/* 178 */         if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0)
/*     */         {
/* 181 */           putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry.getUpperBound(), ((RangeMapEntry)mapEntryBelowToTruncate.getValue()).getValue());
/*     */         }
/*     */ 
/* 185 */         putRangeMapEntry(rangeMapEntry.getLowerBound(), rangeToRemove.lowerBound, ((RangeMapEntry)mapEntryBelowToTruncate.getValue()).getValue());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 190 */     Map.Entry mapEntryAboveToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.upperBound);
/*     */ 
/* 192 */     if (mapEntryAboveToTruncate != null)
/*     */     {
/* 194 */       RangeMapEntry rangeMapEntry = (RangeMapEntry)mapEntryAboveToTruncate.getValue();
/* 195 */       if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0)
/*     */       {
/* 198 */         putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry.getUpperBound(), ((RangeMapEntry)mapEntryAboveToTruncate.getValue()).getValue());
/*     */ 
/* 200 */         this.entriesByLowerBound.remove(rangeToRemove.lowerBound);
/*     */       }
/*     */     }
/* 203 */     this.entriesByLowerBound.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
/*     */   }
/*     */ 
/*     */   public Map<Range<K>, V> asMapOfRanges()
/*     */   {
/* 208 */     return new AsMapOfRanges(null);
/*     */   }
/*     */ 
/*     */   public RangeMap<K, V> subRangeMap(Range<K> subRange)
/*     */   {
/* 250 */     if (subRange.equals(Range.all())) {
/* 251 */       return this;
/*     */     }
/* 253 */     return new SubRangeMap(subRange);
/*     */   }
/*     */ 
/*     */   private RangeMap<K, V> emptySubRangeMap()
/*     */   {
/* 259 */     return EMPTY_SUB_RANGE_MAP;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object o)
/*     */   {
/* 592 */     if ((o instanceof RangeMap)) {
/* 593 */       RangeMap rangeMap = (RangeMap)o;
/* 594 */       return asMapOfRanges().equals(rangeMap.asMapOfRanges());
/*     */     }
/* 596 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 601 */     return asMapOfRanges().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 606 */     return this.entriesByLowerBound.values().toString();
/*     */   }
/*     */ 
/*     */   private class SubRangeMap
/*     */     implements RangeMap<K, V>
/*     */   {
/*     */     private final Range<K> subRange;
/*     */ 
/*     */     SubRangeMap()
/*     */     {
/* 321 */       this.subRange = subRange;
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     public V get(K key)
/*     */     {
/* 327 */       return this.subRange.contains(key) ? TreeRangeMap.this.get(key) : null;
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     public Map.Entry<Range<K>, V> getEntry(K key)
/*     */     {
/* 335 */       if (this.subRange.contains(key)) {
/* 336 */         Map.Entry entry = TreeRangeMap.this.getEntry(key);
/* 337 */         if (entry != null) {
/* 338 */           return Maps.immutableEntry(((Range)entry.getKey()).intersection(this.subRange), entry.getValue());
/*     */         }
/*     */       }
/* 341 */       return null;
/*     */     }
/*     */ 
/*     */     public Range<K> span()
/*     */     {
/* 347 */       Map.Entry lowerEntry = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
/*     */       Cut lowerBound;
/*     */       Cut lowerBound;
/* 349 */       if ((lowerEntry != null) && (((TreeRangeMap.RangeMapEntry)lowerEntry.getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0))
/*     */       {
/* 351 */         lowerBound = this.subRange.lowerBound;
/*     */       } else {
/* 353 */         lowerBound = (Cut)TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
/* 354 */         if ((lowerBound == null) || (lowerBound.compareTo(this.subRange.upperBound) >= 0)) {
/* 355 */           throw new NoSuchElementException();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 360 */       Map.Entry upperEntry = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);
/*     */ 
/* 362 */       if (upperEntry == null)
/* 363 */         throw new NoSuchElementException();
/*     */       Cut upperBound;
/*     */       Cut upperBound;
/* 364 */       if (((TreeRangeMap.RangeMapEntry)upperEntry.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0)
/* 365 */         upperBound = this.subRange.upperBound;
/*     */       else {
/* 367 */         upperBound = ((TreeRangeMap.RangeMapEntry)upperEntry.getValue()).getUpperBound();
/*     */       }
/* 369 */       return Range.create(lowerBound, upperBound);
/*     */     }
/*     */ 
/*     */     public void put(Range<K> range, V value)
/*     */     {
/* 374 */       Preconditions.checkArgument(this.subRange.encloses(range), "Cannot put range %s into a subRangeMap(%s)", new Object[] { range, this.subRange });
/*     */ 
/* 376 */       TreeRangeMap.this.put(range, value);
/*     */     }
/*     */ 
/*     */     public void putAll(RangeMap<K, V> rangeMap)
/*     */     {
/* 381 */       if (rangeMap.asMapOfRanges().isEmpty()) {
/* 382 */         return;
/*     */       }
/* 384 */       Range span = rangeMap.span();
/* 385 */       Preconditions.checkArgument(this.subRange.encloses(span), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", new Object[] { span, this.subRange });
/*     */ 
/* 387 */       TreeRangeMap.this.putAll(rangeMap);
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 392 */       TreeRangeMap.this.remove(this.subRange);
/*     */     }
/*     */ 
/*     */     public void remove(Range<K> range)
/*     */     {
/* 397 */       if (range.isConnected(this.subRange))
/* 398 */         TreeRangeMap.this.remove(range.intersection(this.subRange));
/*     */     }
/*     */ 
/*     */     public RangeMap<K, V> subRangeMap(Range<K> range)
/*     */     {
/* 404 */       if (!range.isConnected(this.subRange)) {
/* 405 */         return TreeRangeMap.this.emptySubRangeMap();
/*     */       }
/* 407 */       return TreeRangeMap.this.subRangeMap(range.intersection(this.subRange));
/*     */     }
/*     */ 
/*     */     public Map<Range<K>, V> asMapOfRanges()
/*     */     {
/* 413 */       return new SubRangeMapAsMap();
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object o)
/*     */     {
/* 418 */       if ((o instanceof RangeMap)) {
/* 419 */         RangeMap rangeMap = (RangeMap)o;
/* 420 */         return asMapOfRanges().equals(rangeMap.asMapOfRanges());
/*     */       }
/* 422 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 427 */       return asMapOfRanges().hashCode();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 432 */       return asMapOfRanges().toString();
/*     */     }
/*     */     class SubRangeMapAsMap extends AbstractMap<Range<K>, V> {
/*     */       SubRangeMapAsMap() {
/*     */       }
/*     */ 
/*     */       public boolean containsKey(Object key) {
/* 439 */         return get(key) != null;
/*     */       }
/*     */ 
/*     */       public V get(Object key)
/*     */       {
/*     */         try {
/* 445 */           if ((key instanceof Range))
/*     */           {
/* 447 */             Range r = (Range)key;
/* 448 */             if ((!TreeRangeMap.SubRangeMap.this.subRange.encloses(r)) || (r.isEmpty())) {
/* 449 */               return null;
/*     */             }
/* 451 */             TreeRangeMap.RangeMapEntry candidate = null;
/* 452 */             if (r.lowerBound.compareTo(TreeRangeMap.SubRangeMap.this.subRange.lowerBound) == 0)
/*     */             {
/* 454 */               Map.Entry entry = TreeRangeMap.this.entriesByLowerBound.floorEntry(r.lowerBound);
/*     */ 
/* 456 */               if (entry != null)
/* 457 */                 candidate = (TreeRangeMap.RangeMapEntry)entry.getValue();
/*     */             }
/*     */             else {
/* 460 */               candidate = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(r.lowerBound);
/*     */             }
/*     */ 
/* 463 */             if ((candidate != null) && (candidate.getKey().isConnected(TreeRangeMap.SubRangeMap.this.subRange)) && (candidate.getKey().intersection(TreeRangeMap.SubRangeMap.this.subRange).equals(r)))
/*     */             {
/* 465 */               return candidate.getValue();
/*     */             }
/*     */           }
/*     */         } catch (ClassCastException e) {
/* 469 */           return null;
/*     */         }
/* 471 */         return null;
/*     */       }
/*     */ 
/*     */       public V remove(Object key)
/*     */       {
/* 476 */         Object value = get(key);
/* 477 */         if (value != null)
/*     */         {
/* 479 */           Range range = (Range)key;
/* 480 */           TreeRangeMap.this.remove(range);
/* 481 */           return value;
/*     */         }
/* 483 */         return null;
/*     */       }
/*     */ 
/*     */       public void clear()
/*     */       {
/* 488 */         TreeRangeMap.SubRangeMap.this.clear();
/*     */       }
/*     */ 
/*     */       private boolean removeEntryIf(Predicate<? super Map.Entry<Range<K>, V>> predicate) {
/* 492 */         List toRemove = Lists.newArrayList();
/* 493 */         for (Map.Entry entry : entrySet()) {
/* 494 */           if (predicate.apply(entry)) {
/* 495 */             toRemove.add(entry.getKey());
/*     */           }
/*     */         }
/* 498 */         for (Range range : toRemove) {
/* 499 */           TreeRangeMap.this.remove(range);
/*     */         }
/* 501 */         return !toRemove.isEmpty();
/*     */       }
/*     */ 
/*     */       public Set<Range<K>> keySet()
/*     */       {
/* 506 */         return new Maps.KeySet(this)
/*     */         {
/*     */           public boolean remove(@Nullable Object o) {
/* 509 */             return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.remove(o) != null;
/*     */           }
/*     */ 
/*     */           public boolean retainAll(Collection<?> c)
/*     */           {
/* 514 */             return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.keyFunction()));
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/*     */       public Set<Map.Entry<Range<K>, V>> entrySet()
/*     */       {
/* 521 */         return new Maps.EntrySet()
/*     */         {
/*     */           Map<Range<K>, V> map() {
/* 524 */             return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this;
/*     */           }
/*     */ 
/*     */           public Iterator<Map.Entry<Range<K>, V>> iterator()
/*     */           {
/* 529 */             if (TreeRangeMap.SubRangeMap.this.subRange.isEmpty()) {
/* 530 */               return Iterators.emptyIterator();
/*     */             }
/* 532 */             Cut cutToStart = (Cut)Objects.firstNonNull(TreeRangeMap.this.entriesByLowerBound.floorKey(TreeRangeMap.SubRangeMap.this.subRange.lowerBound), TreeRangeMap.SubRangeMap.this.subRange.lowerBound);
/*     */ 
/* 535 */             final Iterator backingItr = TreeRangeMap.this.entriesByLowerBound.tailMap(cutToStart, true).values().iterator();
/*     */ 
/* 537 */             return new AbstractIterator()
/*     */             {
/*     */               protected Map.Entry<Range<K>, V> computeNext()
/*     */               {
/* 541 */                 while (backingItr.hasNext()) {
/* 542 */                   TreeRangeMap.RangeMapEntry entry = (TreeRangeMap.RangeMapEntry)backingItr.next();
/* 543 */                   if (entry.getLowerBound().compareTo(TreeRangeMap.SubRangeMap.this.subRange.upperBound) >= 0)
/*     */                     break;
/* 545 */                   if (entry.getUpperBound().compareTo(TreeRangeMap.SubRangeMap.this.subRange.lowerBound) > 0)
/*     */                   {
/* 547 */                     return Maps.immutableEntry(entry.getKey().intersection(TreeRangeMap.SubRangeMap.this.subRange), entry.getValue());
/*     */                   }
/*     */                 }
/*     */ 
/* 551 */                 return (Map.Entry)endOfData();
/*     */               }
/*     */             };
/*     */           }
/*     */ 
/*     */           public boolean retainAll(Collection<?> c)
/*     */           {
/* 558 */             return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.not(Predicates.in(c)));
/*     */           }
/*     */ 
/*     */           public int size()
/*     */           {
/* 563 */             return Iterators.size(iterator());
/*     */           }
/*     */ 
/*     */           public boolean isEmpty()
/*     */           {
/* 568 */             return !iterator().hasNext();
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/*     */       public Collection<V> values()
/*     */       {
/* 575 */         return new Maps.Values(this)
/*     */         {
/*     */           public boolean removeAll(Collection<?> c) {
/* 578 */             return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.in(c), Maps.valueFunction()));
/*     */           }
/*     */ 
/*     */           public boolean retainAll(Collection<?> c)
/*     */           {
/* 583 */             return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.valueFunction()));
/*     */           }
/*     */         };
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class AsMapOfRanges extends AbstractMap<Range<K>, V>
/*     */   {
/*     */     private AsMapOfRanges()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean containsKey(@Nullable Object key)
/*     */     {
/* 215 */       return get(key) != null;
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object key)
/*     */     {
/* 220 */       if ((key instanceof Range)) {
/* 221 */         Range range = (Range)key;
/* 222 */         TreeRangeMap.RangeMapEntry rangeMapEntry = (TreeRangeMap.RangeMapEntry)TreeRangeMap.this.entriesByLowerBound.get(range.lowerBound);
/* 223 */         if ((rangeMapEntry != null) && (rangeMapEntry.getKey().equals(range))) {
/* 224 */           return rangeMapEntry.getValue();
/*     */         }
/*     */       }
/* 227 */       return null;
/*     */     }
/*     */ 
/*     */     public Set<Map.Entry<Range<K>, V>> entrySet()
/*     */     {
/* 232 */       return new AbstractSet()
/*     */       {
/*     */         public Iterator<Map.Entry<Range<K>, V>> iterator()
/*     */         {
/* 237 */           return TreeRangeMap.this.entriesByLowerBound.values().iterator();
/*     */         }
/*     */ 
/*     */         public int size()
/*     */         {
/* 242 */           return TreeRangeMap.this.entriesByLowerBound.size();
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class RangeMapEntry<K extends Comparable, V> extends AbstractMapEntry<Range<K>, V>
/*     */   {
/*     */     private final Range<K> range;
/*     */     private final V value;
/*     */ 
/*     */     RangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, V value)
/*     */     {
/*  74 */       this(Range.create(lowerBound, upperBound), value);
/*     */     }
/*     */ 
/*     */     RangeMapEntry(Range<K> range, V value) {
/*  78 */       this.range = range;
/*  79 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public Range<K> getKey()
/*     */     {
/*  84 */       return this.range;
/*     */     }
/*     */ 
/*     */     public V getValue()
/*     */     {
/*  89 */       return this.value;
/*     */     }
/*     */ 
/*     */     public boolean contains(K value) {
/*  93 */       return this.range.contains(value);
/*     */     }
/*     */ 
/*     */     Cut<K> getLowerBound() {
/*  97 */       return this.range.lowerBound;
/*     */     }
/*     */ 
/*     */     Cut<K> getUpperBound() {
/* 101 */       return this.range.upperBound;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.TreeRangeMap
 * JD-Core Version:    0.6.2
 */