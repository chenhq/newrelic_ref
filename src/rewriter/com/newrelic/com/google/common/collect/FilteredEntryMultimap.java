/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Objects;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.base.Predicates;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ class FilteredEntryMultimap<K, V> extends AbstractMultimap<K, V>
/*     */   implements FilteredMultimap<K, V>
/*     */ {
/*     */   final Multimap<K, V> unfiltered;
/*     */   final Predicate<? super Map.Entry<K, V>> predicate;
/*     */ 
/*     */   FilteredEntryMultimap(Multimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate)
/*     */   {
/*  51 */     this.unfiltered = ((Multimap)Preconditions.checkNotNull(unfiltered));
/*  52 */     this.predicate = ((Predicate)Preconditions.checkNotNull(predicate));
/*     */   }
/*     */ 
/*     */   public Multimap<K, V> unfiltered()
/*     */   {
/*  57 */     return this.unfiltered;
/*     */   }
/*     */ 
/*     */   public Predicate<? super Map.Entry<K, V>> entryPredicate()
/*     */   {
/*  62 */     return this.predicate;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  67 */     return entries().size();
/*     */   }
/*     */ 
/*     */   private boolean satisfies(K key, V value) {
/*  71 */     return this.predicate.apply(Maps.immutableEntry(key, value));
/*     */   }
/*     */ 
/*     */   static <E> Collection<E> filterCollection(Collection<E> collection, Predicate<? super E> predicate)
/*     */   {
/*  90 */     if ((collection instanceof Set)) {
/*  91 */       return Sets.filter((Set)collection, predicate);
/*     */     }
/*  93 */     return Collections2.filter(collection, predicate);
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/*  99 */     return asMap().get(key) != null;
/*     */   }
/*     */ 
/*     */   public Collection<V> removeAll(@Nullable Object key)
/*     */   {
/* 104 */     return (Collection)Objects.firstNonNull(asMap().remove(key), unmodifiableEmptyCollection());
/*     */   }
/*     */ 
/*     */   Collection<V> unmodifiableEmptyCollection()
/*     */   {
/* 109 */     return (this.unfiltered instanceof SetMultimap) ? Collections.emptySet() : Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 116 */     entries().clear();
/*     */   }
/*     */ 
/*     */   public Collection<V> get(K key)
/*     */   {
/* 121 */     return filterCollection(this.unfiltered.get(key), new ValuePredicate(key));
/*     */   }
/*     */ 
/*     */   Collection<Map.Entry<K, V>> createEntries()
/*     */   {
/* 126 */     return filterCollection(this.unfiltered.entries(), this.predicate);
/*     */   }
/*     */ 
/*     */   Collection<V> createValues()
/*     */   {
/* 131 */     return new FilteredMultimapValues(this);
/*     */   }
/*     */ 
/*     */   Iterator<Map.Entry<K, V>> entryIterator()
/*     */   {
/* 136 */     throw new AssertionError("should never be called");
/*     */   }
/*     */ 
/*     */   Map<K, Collection<V>> createAsMap()
/*     */   {
/* 141 */     return new AsMap();
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 146 */     return asMap().keySet();
/*     */   }
/*     */ 
/*     */   boolean removeEntriesIf(Predicate<? super Map.Entry<K, Collection<V>>> predicate) {
/* 150 */     Iterator entryIterator = this.unfiltered.asMap().entrySet().iterator();
/* 151 */     boolean changed = false;
/* 152 */     while (entryIterator.hasNext()) {
/* 153 */       Map.Entry entry = (Map.Entry)entryIterator.next();
/* 154 */       Object key = entry.getKey();
/* 155 */       Collection collection = filterCollection((Collection)entry.getValue(), new ValuePredicate(key));
/* 156 */       if ((!collection.isEmpty()) && (predicate.apply(Maps.immutableEntry(key, collection)))) {
/* 157 */         if (collection.size() == ((Collection)entry.getValue()).size())
/* 158 */           entryIterator.remove();
/*     */         else {
/* 160 */           collection.clear();
/*     */         }
/* 162 */         changed = true;
/*     */       }
/*     */     }
/* 165 */     return changed;
/*     */   }
/*     */ 
/*     */   Multiset<K> createKeys()
/*     */   {
/* 326 */     return new Keys();
/*     */   }
/*     */ 
/*     */   class Keys extends Multimaps.Keys<K, V> {
/*     */     Keys() {
/* 331 */       super();
/*     */     }
/*     */ 
/*     */     public int remove(@Nullable Object key, int occurrences)
/*     */     {
/* 336 */       CollectPreconditions.checkNonnegative(occurrences, "occurrences");
/* 337 */       if (occurrences == 0) {
/* 338 */         return count(key);
/*     */       }
/* 340 */       Collection collection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
/* 341 */       if (collection == null) {
/* 342 */         return 0;
/*     */       }
/*     */ 
/* 345 */       Object k = key;
/* 346 */       int oldCount = 0;
/* 347 */       Iterator itr = collection.iterator();
/* 348 */       while (itr.hasNext()) {
/* 349 */         Object v = itr.next();
/* 350 */         if (FilteredEntryMultimap.this.satisfies(k, v)) {
/* 351 */           oldCount++;
/* 352 */           if (oldCount <= occurrences) {
/* 353 */             itr.remove();
/*     */           }
/*     */         }
/*     */       }
/* 357 */       return oldCount;
/*     */     }
/*     */ 
/*     */     public Set<Multiset.Entry<K>> entrySet()
/*     */     {
/* 362 */       return new Multisets.EntrySet()
/*     */       {
/*     */         Multiset<K> multiset()
/*     */         {
/* 366 */           return FilteredEntryMultimap.Keys.this;
/*     */         }
/*     */ 
/*     */         public Iterator<Multiset.Entry<K>> iterator()
/*     */         {
/* 371 */           return FilteredEntryMultimap.Keys.this.entryIterator();
/*     */         }
/*     */ 
/*     */         public int size()
/*     */         {
/* 376 */           return FilteredEntryMultimap.this.keySet().size();
/*     */         }
/*     */ 
/*     */         private boolean removeEntriesIf(final Predicate<? super Multiset.Entry<K>> predicate) {
/* 380 */           return FilteredEntryMultimap.this.removeEntriesIf(new Predicate()
/*     */           {
/*     */             public boolean apply(Map.Entry<K, Collection<V>> entry)
/*     */             {
/* 384 */               return predicate.apply(Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size()));
/*     */             }
/*     */           });
/*     */         }
/*     */ 
/*     */         public boolean removeAll(Collection<?> c)
/*     */         {
/* 392 */           return removeEntriesIf(Predicates.in(c));
/*     */         }
/*     */ 
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 397 */           return removeEntriesIf(Predicates.not(Predicates.in(c)));
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ 
/*     */   class AsMap extends Maps.ImprovedAbstractMap<K, Collection<V>>
/*     */   {
/*     */     AsMap()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean containsKey(@Nullable Object key)
/*     */     {
/* 171 */       return get(key) != null;
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 176 */       FilteredEntryMultimap.this.clear();
/*     */     }
/*     */ 
/*     */     public Collection<V> get(@Nullable Object key)
/*     */     {
/* 181 */       Collection result = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
/* 182 */       if (result == null) {
/* 183 */         return null;
/*     */       }
/*     */ 
/* 186 */       Object k = key;
/* 187 */       result = FilteredEntryMultimap.filterCollection(result, new FilteredEntryMultimap.ValuePredicate(FilteredEntryMultimap.this, k));
/* 188 */       return result.isEmpty() ? null : result;
/*     */     }
/*     */ 
/*     */     public Collection<V> remove(@Nullable Object key)
/*     */     {
/* 193 */       Collection collection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
/* 194 */       if (collection == null) {
/* 195 */         return null;
/*     */       }
/*     */ 
/* 198 */       Object k = key;
/* 199 */       List result = Lists.newArrayList();
/* 200 */       Iterator itr = collection.iterator();
/* 201 */       while (itr.hasNext()) {
/* 202 */         Object v = itr.next();
/* 203 */         if (FilteredEntryMultimap.this.satisfies(k, v)) {
/* 204 */           itr.remove();
/* 205 */           result.add(v);
/*     */         }
/*     */       }
/* 208 */       if (result.isEmpty())
/* 209 */         return null;
/* 210 */       if ((FilteredEntryMultimap.this.unfiltered instanceof SetMultimap)) {
/* 211 */         return Collections.unmodifiableSet(Sets.newLinkedHashSet(result));
/*     */       }
/* 213 */       return Collections.unmodifiableList(result);
/*     */     }
/*     */ 
/*     */     Set<K> createKeySet()
/*     */     {
/* 219 */       return new Maps.KeySet(this)
/*     */       {
/*     */         public boolean removeAll(Collection<?> c) {
/* 222 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.in(c)));
/*     */         }
/*     */ 
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 227 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(c))));
/*     */         }
/*     */ 
/*     */         public boolean remove(@Nullable Object o)
/*     */         {
/* 232 */           return FilteredEntryMultimap.AsMap.this.remove(o) != null;
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     Set<Map.Entry<K, Collection<V>>> createEntrySet()
/*     */     {
/* 239 */       return new Maps.EntrySet()
/*     */       {
/*     */         Map<K, Collection<V>> map() {
/* 242 */           return FilteredEntryMultimap.AsMap.this;
/*     */         }
/*     */ 
/*     */         public Iterator<Map.Entry<K, Collection<V>>> iterator()
/*     */         {
/* 247 */           return new AbstractIterator() {
/* 248 */             final Iterator<Map.Entry<K, Collection<V>>> backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
/*     */ 
/*     */             protected Map.Entry<K, Collection<V>> computeNext()
/*     */             {
/* 253 */               while (this.backingIterator.hasNext()) {
/* 254 */                 Map.Entry entry = (Map.Entry)this.backingIterator.next();
/* 255 */                 Object key = entry.getKey();
/* 256 */                 Collection collection = FilteredEntryMultimap.filterCollection((Collection)entry.getValue(), new FilteredEntryMultimap.ValuePredicate(FilteredEntryMultimap.this, key));
/*     */ 
/* 258 */                 if (!collection.isEmpty()) {
/* 259 */                   return Maps.immutableEntry(key, collection);
/*     */                 }
/*     */               }
/* 262 */               return (Map.Entry)endOfData();
/*     */             }
/*     */           };
/*     */         }
/*     */ 
/*     */         public boolean removeAll(Collection<?> c)
/*     */         {
/* 269 */           return FilteredEntryMultimap.this.removeEntriesIf(Predicates.in(c));
/*     */         }
/*     */ 
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 274 */           return FilteredEntryMultimap.this.removeEntriesIf(Predicates.not(Predicates.in(c)));
/*     */         }
/*     */ 
/*     */         public int size()
/*     */         {
/* 279 */           return Iterators.size(iterator());
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     Collection<Collection<V>> createValues()
/*     */     {
/* 286 */       return new Maps.Values(this)
/*     */       {
/*     */         public boolean remove(@Nullable Object o) {
/* 289 */           if ((o instanceof Collection)) {
/* 290 */             Collection c = (Collection)o;
/* 291 */             Iterator entryIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
/*     */ 
/* 293 */             while (entryIterator.hasNext()) {
/* 294 */               Map.Entry entry = (Map.Entry)entryIterator.next();
/* 295 */               Object key = entry.getKey();
/* 296 */               Collection collection = FilteredEntryMultimap.filterCollection((Collection)entry.getValue(), new FilteredEntryMultimap.ValuePredicate(FilteredEntryMultimap.this, key));
/*     */ 
/* 298 */               if ((!collection.isEmpty()) && (c.equals(collection))) {
/* 299 */                 if (collection.size() == ((Collection)entry.getValue()).size())
/* 300 */                   entryIterator.remove();
/*     */                 else {
/* 302 */                   collection.clear();
/*     */                 }
/* 304 */                 return true;
/*     */               }
/*     */             }
/*     */           }
/* 308 */           return false;
/*     */         }
/*     */ 
/*     */         public boolean removeAll(Collection<?> c)
/*     */         {
/* 313 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.in(c)));
/*     */         }
/*     */ 
/*     */         public boolean retainAll(Collection<?> c)
/*     */         {
/* 318 */           return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(c))));
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ 
/*     */   final class ValuePredicate
/*     */     implements Predicate<V>
/*     */   {
/*     */     private final K key;
/*     */ 
/*     */     ValuePredicate()
/*     */     {
/*  79 */       this.key = key;
/*     */     }
/*     */ 
/*     */     public boolean apply(@Nullable V value)
/*     */     {
/*  84 */       return FilteredEntryMultimap.this.satisfies(this.key, value);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.FilteredEntryMultimap
 * JD-Core Version:    0.6.2
 */