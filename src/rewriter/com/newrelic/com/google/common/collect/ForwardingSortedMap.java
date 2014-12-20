/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.SortedMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V>
/*     */   implements SortedMap<K, V>
/*     */ {
/*     */   protected abstract SortedMap<K, V> delegate();
/*     */ 
/*     */   public Comparator<? super K> comparator()
/*     */   {
/*  67 */     return delegate().comparator();
/*     */   }
/*     */ 
/*     */   public K firstKey()
/*     */   {
/*  72 */     return delegate().firstKey();
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> headMap(K toKey)
/*     */   {
/*  77 */     return delegate().headMap(toKey);
/*     */   }
/*     */ 
/*     */   public K lastKey()
/*     */   {
/*  82 */     return delegate().lastKey();
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> subMap(K fromKey, K toKey)
/*     */   {
/*  87 */     return delegate().subMap(fromKey, toKey);
/*     */   }
/*     */ 
/*     */   public SortedMap<K, V> tailMap(K fromKey)
/*     */   {
/*  92 */     return delegate().tailMap(fromKey);
/*     */   }
/*     */ 
/*     */   private int unsafeCompare(Object k1, Object k2)
/*     */   {
/* 113 */     Comparator comparator = comparator();
/* 114 */     if (comparator == null) {
/* 115 */       return ((Comparable)k1).compareTo(k2);
/*     */     }
/* 117 */     return comparator.compare(k1, k2);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardContainsKey(@Nullable Object key)
/*     */   {
/*     */     try
/*     */     {
/* 133 */       SortedMap self = this;
/* 134 */       Object ceilingKey = self.tailMap(key).firstKey();
/* 135 */       return unsafeCompare(ceilingKey, key) == 0;
/*     */     } catch (ClassCastException e) {
/* 137 */       return false;
/*     */     } catch (NoSuchElementException e) {
/* 139 */       return false; } catch (NullPointerException e) {
/*     */     }
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected SortedMap<K, V> standardSubMap(K fromKey, K toKey)
/*     */   {
/* 154 */     Preconditions.checkArgument(unsafeCompare(fromKey, toKey) <= 0, "fromKey must be <= toKey");
/* 155 */     return tailMap(fromKey).headMap(toKey);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected class StandardKeySet extends Maps.SortedKeySet<K, V>
/*     */   {
/*     */     public StandardKeySet()
/*     */     {
/* 106 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingSortedMap
 * JD-Core Version:    0.6.2
 */