/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ abstract class AbstractNavigableMap<K, V> extends AbstractMap<K, V>
/*     */   implements NavigableMap<K, V>
/*     */ {
/*     */   @Nullable
/*     */   public abstract V get(@Nullable Object paramObject);
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> firstEntry()
/*     */   {
/*  44 */     return (Map.Entry)Iterators.getNext(entryIterator(), null);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> lastEntry()
/*     */   {
/*  50 */     return (Map.Entry)Iterators.getNext(descendingEntryIterator(), null);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> pollFirstEntry()
/*     */   {
/*  56 */     return (Map.Entry)Iterators.pollNext(entryIterator());
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> pollLastEntry()
/*     */   {
/*  62 */     return (Map.Entry)Iterators.pollNext(descendingEntryIterator());
/*     */   }
/*     */ 
/*     */   public K firstKey()
/*     */   {
/*  67 */     Map.Entry entry = firstEntry();
/*  68 */     if (entry == null) {
/*  69 */       throw new NoSuchElementException();
/*     */     }
/*  71 */     return entry.getKey();
/*     */   }
/*     */ 
/*     */   public K lastKey()
/*     */   {
/*  77 */     Map.Entry entry = lastEntry();
/*  78 */     if (entry == null) {
/*  79 */       throw new NoSuchElementException();
/*     */     }
/*  81 */     return entry.getKey();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> lowerEntry(K key)
/*     */   {
/*  88 */     return headMap(key, false).lastEntry();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> floorEntry(K key)
/*     */   {
/*  94 */     return headMap(key, true).lastEntry();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> ceilingEntry(K key)
/*     */   {
/* 100 */     return tailMap(key, true).firstEntry();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Map.Entry<K, V> higherEntry(K key)
/*     */   {
/* 106 */     return tailMap(key, false).firstEntry();
/*     */   }
/*     */ 
/*     */   public K lowerKey(K key)
/*     */   {
/* 111 */     return Maps.keyOrNull(lowerEntry(key));
/*     */   }
/*     */ 
/*     */   public K floorKey(K key)
/*     */   {
/* 116 */     return Maps.keyOrNull(floorEntry(key));
/*     */   }
/*     */ 
/*     */   public K ceilingKey(K key)
/*     */   {
/* 121 */     return Maps.keyOrNull(ceilingEntry(key));
/*     */   }
/*     */ 
/*     */   public K higherKey(K key)
/*     */   {
/* 126 */     return Maps.keyOrNull(higherEntry(key));
/*     */   }
/*     */ 
/*     */   abstract Iterator<Map.Entry<K, V>> entryIterator();
/*     */ 
/*     */   abstract Iterator<Map.Entry<K, V>> descendingEntryIterator();
/*     */ 
/*     */   public SortedMap<K, V> subMap(K fromKey, K toKey)
/*     */   {
/* 135 */     return subMap(fromKey, true, toKey, false);
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> headMap(K toKey)
/*     */   {
/* 140 */     return headMap(toKey, false);
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> tailMap(K fromKey)
/*     */   {
/* 145 */     return tailMap(fromKey, true);
/*     */   }
/*     */ 
/*     */   public NavigableSet<K> navigableKeySet()
/*     */   {
/* 150 */     return new Maps.NavigableKeySet(this);
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 155 */     return navigableKeySet();
/*     */   }
/*     */ 
/*     */   public abstract int size();
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet()
/*     */   {
/* 163 */     return new Maps.EntrySet()
/*     */     {
/*     */       Map<K, V> map() {
/* 166 */         return AbstractNavigableMap.this;
/*     */       }
/*     */ 
/*     */       public Iterator<Map.Entry<K, V>> iterator()
/*     */       {
/* 171 */         return AbstractNavigableMap.this.entryIterator();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public NavigableSet<K> descendingKeySet()
/*     */   {
/* 178 */     return descendingMap().navigableKeySet();
/*     */   }
/*     */ 
/*     */   public NavigableMap<K, V> descendingMap()
/*     */   {
/* 183 */     return new DescendingMap(null);
/*     */   }
/*     */   private final class DescendingMap extends Maps.DescendingMap<K, V> {
/*     */     private DescendingMap() {
/*     */     }
/*     */     NavigableMap<K, V> forward() {
/* 189 */       return AbstractNavigableMap.this;
/*     */     }
/*     */ 
/*     */     Iterator<Map.Entry<K, V>> entryIterator()
/*     */     {
/* 194 */       return AbstractNavigableMap.this.descendingEntryIterator();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractNavigableMap
 * JD-Core Version:    0.6.2
 */