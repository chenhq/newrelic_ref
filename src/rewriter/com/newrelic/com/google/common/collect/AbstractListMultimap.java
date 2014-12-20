/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V>
/*     */   implements ListMultimap<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 6588350623831699109L;
/*     */ 
/*     */   protected AbstractListMultimap(Map<K, Collection<V>> map)
/*     */   {
/*  46 */     super(map);
/*     */   }
/*     */ 
/*     */   abstract List<V> createCollection();
/*     */ 
/*     */   List<V> createUnmodifiableEmptyCollection()
/*     */   {
/*  53 */     return ImmutableList.of();
/*     */   }
/*     */ 
/*     */   public List<V> get(@Nullable K key)
/*     */   {
/*  66 */     return (List)super.get(key);
/*     */   }
/*     */ 
/*     */   public List<V> removeAll(@Nullable Object key)
/*     */   {
/*  77 */     return (List)super.removeAll(key);
/*     */   }
/*     */ 
/*     */   public List<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/*  89 */     return (List)super.replaceValues(key, values);
/*     */   }
/*     */ 
/*     */   public boolean put(@Nullable K key, @Nullable V value)
/*     */   {
/* 100 */     return super.put(key, value);
/*     */   }
/*     */ 
/*     */   public Map<K, Collection<V>> asMap()
/*     */   {
/* 110 */     return super.asMap();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 121 */     return super.equals(object);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractListMultimap
 * JD-Core Version:    0.6.2
 */