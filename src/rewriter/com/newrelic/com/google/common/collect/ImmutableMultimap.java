/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ImmutableMultimap<K, V> extends AbstractMultimap<K, V>
/*     */   implements Serializable
/*     */ {
/*     */   final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
/*     */   final transient int size;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of()
/*     */   {
/*  70 */     return ImmutableListMultimap.of();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1)
/*     */   {
/*  77 */     return ImmutableListMultimap.of(k1, v1);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  84 */     return ImmutableListMultimap.of(k1, v1, k2, v2);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/*  92 */     return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 100 */     return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 108 */     return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> builder()
/*     */   {
/* 118 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 290 */     if ((multimap instanceof ImmutableMultimap))
/*     */     {
/* 292 */       ImmutableMultimap kvMultimap = (ImmutableMultimap)multimap;
/*     */ 
/* 294 */       if (!kvMultimap.isPartialView()) {
/* 295 */         return kvMultimap;
/*     */       }
/*     */     }
/* 298 */     return ImmutableListMultimap.copyOf(multimap);
/*     */   }
/*     */ 
/*     */   ImmutableMultimap(ImmutableMap<K, ? extends ImmutableCollection<V>> map, int size)
/*     */   {
/* 322 */     this.map = map;
/* 323 */     this.size = size;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ImmutableCollection<V> removeAll(Object key)
/*     */   {
/* 337 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public ImmutableCollection<V> replaceValues(K key, Iterable<? extends V> values)
/*     */   {
/* 350 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void clear()
/*     */   {
/* 362 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public abstract ImmutableCollection<V> get(K paramK);
/*     */ 
/*     */   public abstract ImmutableMultimap<V, K> inverse();
/*     */ 
/*     */   @Deprecated
/*     */   public boolean put(K key, V value)
/*     */   {
/* 392 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean putAll(K key, Iterable<? extends V> values)
/*     */   {
/* 404 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 416 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean remove(Object key, Object value)
/*     */   {
/* 428 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/* 438 */     return this.map.isPartialView();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/* 445 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 450 */     return (value != null) && (super.containsValue(value));
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 455 */     return this.size;
/*     */   }
/*     */ 
/*     */   public ImmutableSet<K> keySet()
/*     */   {
/* 467 */     return this.map.keySet();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<K, Collection<V>> asMap()
/*     */   {
/* 477 */     return this.map;
/*     */   }
/*     */ 
/*     */   Map<K, Collection<V>> createAsMap()
/*     */   {
/* 482 */     throw new AssertionError("should never be called");
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<Map.Entry<K, V>> entries()
/*     */   {
/* 492 */     return (ImmutableCollection)super.entries();
/*     */   }
/*     */ 
/*     */   ImmutableCollection<Map.Entry<K, V>> createEntries()
/*     */   {
/* 497 */     return new EntryCollection(this);
/*     */   }
/*     */ 
/*     */   UnmodifiableIterator<Map.Entry<K, V>> entryIterator()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 36	com/newrelic/com/google/common/collect/ImmutableMultimap$1
/*     */     //   3: dup
/*     */     //   4: aload_0
/*     */     //   5: invokespecial 181	com/newrelic/com/google/common/collect/ImmutableMultimap$1:<init>	(Lcom/newrelic/com/google/common/collect/ImmutableMultimap;)V
/*     */     //   8: areturn
/*     */   }
/*     */ 
/*     */   public ImmutableMultiset<K> keys()
/*     */   {
/* 573 */     return (ImmutableMultiset)super.keys();
/*     */   }
/*     */ 
/*     */   ImmutableMultiset<K> createKeys()
/*     */   {
/* 578 */     return new Keys();
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<V> values()
/*     */   {
/* 623 */     return (ImmutableCollection)super.values();
/*     */   }
/*     */ 
/*     */   ImmutableCollection<V> createValues()
/*     */   {
/* 628 */     return new Values(this); } 
/*     */   UnmodifiableIterator<V> valueIterator() { // Byte code:
/*     */     //   0: new 34	com/newrelic/com/google/common/collect/ImmutableMultimap$2
/*     */     //   3: dup
/*     */     //   4: aload_0
/*     */     //   5: invokespecial 196	com/newrelic/com/google/common/collect/ImmutableMultimap$2:<init>	(Lcom/newrelic/com/google/common/collect/ImmutableMultimap;)V
/*     */     //   8: areturn }
/*     */ 
/*     */   private static final class Values<K, V> extends ImmutableCollection<V> { private final transient ImmutableMultimap<K, V> multimap;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 645 */     Values(ImmutableMultimap<K, V> multimap) { this.multimap = multimap; }
/*     */ 
/*     */ 
/*     */     public boolean contains(@Nullable Object object)
/*     */     {
/* 650 */       return this.multimap.containsValue(object);
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<V> iterator() {
/* 654 */       return this.multimap.valueIterator();
/*     */     }
/*     */ 
/*     */     @GwtIncompatible("not present in emulated superclass")
/*     */     int copyIntoArray(Object[] dst, int offset)
/*     */     {
/* 660 */       for (ImmutableCollection valueCollection : this.multimap.map.values()) {
/* 661 */         offset = valueCollection.copyIntoArray(dst, offset);
/*     */       }
/* 663 */       return offset;
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 668 */       return this.multimap.size();
/*     */     }
/*     */ 
/*     */     boolean isPartialView() {
/* 672 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   class Keys extends ImmutableMultiset<K>
/*     */   {
/*     */     Keys()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object object)
/*     */     {
/* 585 */       return ImmutableMultimap.this.containsKey(object);
/*     */     }
/*     */ 
/*     */     public int count(@Nullable Object element)
/*     */     {
/* 590 */       Collection values = (Collection)ImmutableMultimap.this.map.get(element);
/* 591 */       return values == null ? 0 : values.size();
/*     */     }
/*     */ 
/*     */     public Set<K> elementSet()
/*     */     {
/* 596 */       return ImmutableMultimap.this.keySet();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 601 */       return ImmutableMultimap.this.size();
/*     */     }
/*     */ 
/*     */     Multiset.Entry<K> getEntry(int index)
/*     */     {
/* 606 */       Map.Entry entry = (Map.Entry)ImmutableMultimap.this.map.entrySet().asList().get(index);
/* 607 */       return Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size());
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 612 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private abstract class Itr<T> extends UnmodifiableIterator<T>
/*     */   {
/* 533 */     final Iterator<Map.Entry<K, Collection<V>>> mapIterator = ImmutableMultimap.this.asMap().entrySet().iterator();
/* 534 */     K key = null;
/* 535 */     Iterator<V> valueIterator = Iterators.emptyIterator();
/*     */ 
/*     */     private Itr() {
/*     */     }
/*     */     abstract T output(K paramK, V paramV);
/*     */ 
/* 541 */     public boolean hasNext() { return (this.mapIterator.hasNext()) || (this.valueIterator.hasNext()); }
/*     */ 
/*     */ 
/*     */     public T next()
/*     */     {
/* 546 */       if (!this.valueIterator.hasNext()) {
/* 547 */         Map.Entry mapEntry = (Map.Entry)this.mapIterator.next();
/* 548 */         this.key = mapEntry.getKey();
/* 549 */         this.valueIterator = ((Collection)mapEntry.getValue()).iterator();
/*     */       }
/* 551 */       return output(this.key, this.valueIterator.next());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class EntryCollection<K, V> extends ImmutableCollection<Map.Entry<K, V>>
/*     */   {
/*     */     final ImmutableMultimap<K, V> multimap;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     EntryCollection(ImmutableMultimap<K, V> multimap)
/*     */     {
/* 505 */       this.multimap = multimap;
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
/* 509 */       return this.multimap.entryIterator();
/*     */     }
/*     */ 
/*     */     boolean isPartialView() {
/* 513 */       return this.multimap.isPartialView();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 518 */       return this.multimap.size();
/*     */     }
/*     */ 
/*     */     public boolean contains(Object object) {
/* 522 */       if ((object instanceof Map.Entry)) {
/* 523 */         Map.Entry entry = (Map.Entry)object;
/* 524 */         return this.multimap.containsEntry(entry.getKey(), entry.getValue());
/*     */       }
/* 526 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java serialization is not supported")
/*     */   static class FieldSettersHolder
/*     */   {
/* 310 */     static final Serialization.FieldSetter<ImmutableMultimap> MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
/*     */ 
/* 313 */     static final Serialization.FieldSetter<ImmutableMultimap> SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
/*     */ 
/* 316 */     static final Serialization.FieldSetter<ImmutableSetMultimap> EMPTY_SET_FIELD_SETTER = Serialization.getFieldSetter(ImmutableSetMultimap.class, "emptySet");
/*     */   }
/*     */ 
/*     */   public static class Builder<K, V>
/*     */   {
/* 155 */     Multimap<K, V> builderMultimap = new ImmutableMultimap.BuilderMultimap();
/*     */     Comparator<? super K> keyComparator;
/*     */     Comparator<? super V> valueComparator;
/*     */ 
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 169 */       CollectPreconditions.checkEntryNotNull(key, value);
/* 170 */       this.builderMultimap.put(key, value);
/* 171 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry)
/*     */     {
/* 180 */       return put(entry.getKey(), entry.getValue());
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, Iterable<? extends V> values)
/*     */     {
/* 191 */       if (key == null) {
/* 192 */         throw new NullPointerException("null key in entry: null=" + Iterables.toString(values));
/*     */       }
/*     */ 
/* 195 */       Collection valueList = this.builderMultimap.get(key);
/* 196 */       for (Iterator i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 197 */         CollectPreconditions.checkEntryNotNull(key, value);
/* 198 */         valueList.add(value);
/*     */       }
/* 200 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, V[] values)
/*     */     {
/* 210 */       return putAll(key, Arrays.asList(values));
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap)
/*     */     {
/* 224 */       for (Map.Entry entry : multimap.asMap().entrySet()) {
/* 225 */         putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */       }
/* 227 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator)
/*     */     {
/* 236 */       this.keyComparator = ((Comparator)Preconditions.checkNotNull(keyComparator));
/* 237 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator)
/*     */     {
/* 246 */       this.valueComparator = ((Comparator)Preconditions.checkNotNull(valueComparator));
/* 247 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableMultimap<K, V> build()
/*     */     {
/* 254 */       if (this.valueComparator != null) {
/* 255 */         for (Collection values : this.builderMultimap.asMap().values()) {
/* 256 */           List list = (List)values;
/* 257 */           Collections.sort(list, this.valueComparator);
/*     */         }
/*     */       }
/* 260 */       if (this.keyComparator != null) {
/* 261 */         Multimap sortedCopy = new ImmutableMultimap.BuilderMultimap();
/* 262 */         List entries = Lists.newArrayList(this.builderMultimap.asMap().entrySet());
/*     */ 
/* 264 */         Collections.sort(entries, Ordering.from(this.keyComparator).onKeys());
/*     */ 
/* 267 */         for (Map.Entry entry : entries) {
/* 268 */           sortedCopy.putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */         }
/* 270 */         this.builderMultimap = sortedCopy;
/*     */       }
/* 272 */       return ImmutableMultimap.copyOf(this.builderMultimap);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class BuilderMultimap<K, V> extends AbstractMapBasedMultimap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     BuilderMultimap()
/*     */     {
/* 128 */       super();
/*     */     }
/*     */     Collection<V> createCollection() {
/* 131 */       return Lists.newArrayList();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableMultimap
 * JD-Core Version:    0.6.2
 */