/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V>
/*     */   implements NavigableMap<K, V>
/*     */ {
/*  65 */   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
/*     */ 
/*  67 */   private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new EmptyImmutableSortedMap(NATURAL_ORDER);
/*     */   private transient ImmutableSortedMap<K, V> descendingMap;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> comparator)
/*     */   {
/*  71 */     if (Ordering.natural().equals(comparator)) {
/*  72 */       return of();
/*     */     }
/*  74 */     return new EmptyImmutableSortedMap(comparator);
/*     */   }
/*     */ 
/*     */   static <K, V> ImmutableSortedMap<K, V> fromSortedEntries(Comparator<? super K> comparator, int size, Map.Entry<K, V>[] entries)
/*     */   {
/*  82 */     if (size == 0) {
/*  83 */       return emptyMap(comparator);
/*     */     }
/*     */ 
/*  86 */     ImmutableList.Builder keyBuilder = ImmutableList.builder();
/*  87 */     ImmutableList.Builder valueBuilder = ImmutableList.builder();
/*  88 */     for (int i = 0; i < size; i++) {
/*  89 */       Map.Entry entry = entries[i];
/*  90 */       keyBuilder.add(entry.getKey());
/*  91 */       valueBuilder.add(entry.getValue());
/*     */     }
/*     */ 
/*  94 */     return new RegularImmutableSortedMap(new RegularImmutableSortedSet(keyBuilder.build(), comparator), valueBuilder.build());
/*     */   }
/*     */ 
/*     */   static <K, V> ImmutableSortedMap<K, V> from(ImmutableSortedSet<K> keySet, ImmutableList<V> valueList)
/*     */   {
/* 101 */     if (keySet.isEmpty()) {
/* 102 */       return emptyMap(keySet.comparator());
/*     */     }
/* 104 */     return new RegularImmutableSortedMap((RegularImmutableSortedSet)keySet, valueList);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> of()
/*     */   {
/* 117 */     return NATURAL_EMPTY_MAP;
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1)
/*     */   {
/* 125 */     return from(ImmutableSortedSet.of(k1), ImmutableList.of(v1));
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/* 138 */     return fromEntries(Ordering.natural(), false, 2, new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2) });
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/* 151 */     return fromEntries(Ordering.natural(), false, 3, new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 165 */     return fromEntries(Ordering.natural(), false, 4, new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 179 */     return fromEntries(Ordering.natural(), false, 5, new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5) });
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map)
/*     */   {
/* 205 */     Ordering naturalOrder = Ordering.natural();
/* 206 */     return copyOfInternal(map, naturalOrder);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map, Comparator<? super K> comparator)
/*     */   {
/* 223 */     return copyOfInternal(map, (Comparator)Preconditions.checkNotNull(comparator));
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> map)
/*     */   {
/* 239 */     Comparator comparator = map.comparator();
/* 240 */     if (comparator == null)
/*     */     {
/* 243 */       comparator = NATURAL_ORDER;
/*     */     }
/* 245 */     return copyOfInternal(map, comparator);
/*     */   }
/*     */ 
/*     */   private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> map, Comparator<? super K> comparator)
/*     */   {
/* 250 */     boolean sameComparator = false;
/* 251 */     if ((map instanceof SortedMap)) {
/* 252 */       SortedMap sortedMap = (SortedMap)map;
/* 253 */       Comparator comparator2 = sortedMap.comparator();
/* 254 */       sameComparator = comparator2 == null ? false : comparator == NATURAL_ORDER ? true : comparator.equals(comparator2);
/*     */     }
/*     */ 
/* 259 */     if ((sameComparator) && ((map instanceof ImmutableSortedMap)))
/*     */     {
/* 263 */       ImmutableSortedMap kvMap = (ImmutableSortedMap)map;
/* 264 */       if (!kvMap.isPartialView()) {
/* 265 */         return kvMap;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 273 */     Map.Entry[] entries = (Map.Entry[])map.entrySet().toArray(new Map.Entry[0]);
/*     */ 
/* 275 */     return fromEntries(comparator, sameComparator, entries.length, entries);
/*     */   }
/*     */ 
/*     */   static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean sameComparator, int size, Map.Entry<K, V>[] entries)
/*     */   {
/* 280 */     for (int i = 0; i < size; i++) {
/* 281 */       Map.Entry entry = entries[i];
/* 282 */       entries[i] = entryOf(entry.getKey(), entry.getValue());
/*     */     }
/* 284 */     if (!sameComparator) {
/* 285 */       sortEntries(comparator, size, entries);
/* 286 */       validateEntries(size, entries, comparator);
/*     */     }
/*     */ 
/* 289 */     return fromSortedEntries(comparator, size, entries);
/*     */   }
/*     */ 
/*     */   private static <K, V> void sortEntries(Comparator<? super K> comparator, int size, Map.Entry<K, V>[] entries)
/*     */   {
/* 294 */     Arrays.sort(entries, 0, size, Ordering.from(comparator).onKeys());
/*     */   }
/*     */ 
/*     */   private static <K, V> void validateEntries(int size, Map.Entry<K, V>[] entries, Comparator<? super K> comparator)
/*     */   {
/* 299 */     for (int i = 1; i < size; i++)
/* 300 */       checkNoConflict(comparator.compare(entries[(i - 1)].getKey(), entries[i].getKey()) != 0, "key", entries[(i - 1)], entries[i]);
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<?>, V> Builder<K, V> naturalOrder()
/*     */   {
/* 311 */     return new Builder(Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> orderedBy(Comparator<K> comparator)
/*     */   {
/* 323 */     return new Builder(comparator);
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<?>, V> Builder<K, V> reverseOrder()
/*     */   {
/* 331 */     return new Builder(Ordering.natural().reverse());
/*     */   }
/*     */ 
/*     */   ImmutableSortedMap()
/*     */   {
/*     */   }
/*     */ 
/*     */   ImmutableSortedMap(ImmutableSortedMap<K, V> descendingMap)
/*     */   {
/* 416 */     this.descendingMap = descendingMap;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 421 */     return values().size();
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/* 425 */     return values().contains(value);
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/* 429 */     return (keySet().isPartialView()) || (values().isPartialView());
/*     */   }
/*     */ 
/*     */   public ImmutableSet<Map.Entry<K, V>> entrySet()
/*     */   {
/* 437 */     return super.entrySet();
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedSet<K> keySet();
/*     */ 
/*     */   public abstract ImmutableCollection<V> values();
/*     */ 
/*     */   public Comparator<? super K> comparator()
/*     */   {
/* 459 */     return keySet().comparator();
/*     */   }
/*     */ 
/*     */   public K firstKey()
/*     */   {
/* 464 */     return keySet().first();
/*     */   }
/*     */ 
/*     */   public K lastKey()
/*     */   {
/* 469 */     return keySet().last();
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> headMap(K toKey)
/*     */   {
/* 484 */     return headMap(toKey, false);
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedMap<K, V> headMap(K paramK, boolean paramBoolean);
/*     */ 
/*     */   public ImmutableSortedMap<K, V> subMap(K fromKey, K toKey)
/*     */   {
/* 517 */     return subMap(fromKey, true, toKey, false);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
/*     */   {
/* 538 */     Preconditions.checkNotNull(fromKey);
/* 539 */     Preconditions.checkNotNull(toKey);
/* 540 */     Preconditions.checkArgument(comparator().compare(fromKey, toKey) <= 0, "expected fromKey <= toKey but %s > %s", new Object[] { fromKey, toKey });
/*     */ 
/* 542 */     return headMap(toKey, toInclusive).tailMap(fromKey, fromInclusive);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> tailMap(K fromKey)
/*     */   {
/* 557 */     return tailMap(fromKey, true);
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedMap<K, V> tailMap(K paramK, boolean paramBoolean);
/*     */ 
/*     */   public Map.Entry<K, V> lowerEntry(K key)
/*     */   {
/* 578 */     return headMap(key, false).lastEntry();
/*     */   }
/*     */ 
/*     */   public K lowerKey(K key)
/*     */   {
/* 583 */     return Maps.keyOrNull(lowerEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> floorEntry(K key)
/*     */   {
/* 588 */     return headMap(key, true).lastEntry();
/*     */   }
/*     */ 
/*     */   public K floorKey(K key)
/*     */   {
/* 593 */     return Maps.keyOrNull(floorEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> ceilingEntry(K key)
/*     */   {
/* 598 */     return tailMap(key, true).firstEntry();
/*     */   }
/*     */ 
/*     */   public K ceilingKey(K key)
/*     */   {
/* 603 */     return Maps.keyOrNull(ceilingEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> higherEntry(K key)
/*     */   {
/* 608 */     return tailMap(key, false).firstEntry();
/*     */   }
/*     */ 
/*     */   public K higherKey(K key)
/*     */   {
/* 613 */     return Maps.keyOrNull(higherEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> firstEntry()
/*     */   {
/* 618 */     return isEmpty() ? null : (Map.Entry)entrySet().asList().get(0);
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> lastEntry()
/*     */   {
/* 623 */     return isEmpty() ? null : (Map.Entry)entrySet().asList().get(size() - 1);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final Map.Entry<K, V> pollFirstEntry()
/*     */   {
/* 635 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public final Map.Entry<K, V> pollLastEntry()
/*     */   {
/* 647 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> descendingMap()
/*     */   {
/* 654 */     ImmutableSortedMap result = this.descendingMap;
/* 655 */     if (result == null) {
/* 656 */       result = this.descendingMap = createDescendingMap();
/*     */     }
/* 658 */     return result;
/*     */   }
/*     */ 
/*     */   abstract ImmutableSortedMap<K, V> createDescendingMap();
/*     */ 
/*     */   public ImmutableSortedSet<K> navigableKeySet()
/*     */   {
/* 665 */     return keySet();
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<K> descendingKeySet()
/*     */   {
/* 670 */     return keySet().descendingSet();
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 694 */     return new SerializedForm(this);
/*     */   }
/*     */ 
/*     */   private static class SerializedForm extends ImmutableMap.SerializedForm
/*     */   {
/*     */     private final Comparator<Object> comparator;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SerializedForm(ImmutableSortedMap<?, ?> sortedMap)
/*     */     {
/* 683 */       super();
/* 684 */       this.comparator = sortedMap.comparator();
/*     */     }
/*     */     Object readResolve() {
/* 687 */       ImmutableSortedMap.Builder builder = new ImmutableSortedMap.Builder(this.comparator);
/* 688 */       return createMap(builder);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Builder<K, V> extends ImmutableMap.Builder<K, V>
/*     */   {
/*     */     private final Comparator<? super K> comparator;
/*     */ 
/*     */     public Builder(Comparator<? super K> comparator)
/*     */     {
/* 363 */       this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 372 */       super.put(key, value);
/* 373 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry)
/*     */     {
/* 385 */       super.put(entry);
/* 386 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Map<? extends K, ? extends V> map)
/*     */     {
/* 397 */       super.putAll(map);
/* 398 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSortedMap<K, V> build()
/*     */     {
/* 408 */       return ImmutableSortedMap.fromEntries(this.comparator, false, this.size, this.entries);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableSortedMap
 * JD-Core Version:    0.6.2
 */