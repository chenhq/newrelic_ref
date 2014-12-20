/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V>
/*     */   implements SetMultimap<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 7431625294878419160L;
/*     */ 
/*     */   protected AbstractSetMultimap(Map<K, Collection<V>> map)
/*     */   {
/*  44 */     super(map);
/*     */   }
/*     */ 
/*     */   abstract Set<V> createCollection();
/*     */ 
/*     */   Set<V> createUnmodifiableEmptyCollection() {
/*  50 */     return ImmutableSet.of();
/*     */   }
/*     */ 
/*     */   public Set<V> get(@Nullable K key)
/*     */   {
/*  63 */     return (Set)super.get(key);
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entries()
/*     */   {
/*  74 */     return (Set)super.entries();
/*     */   }
/*     */ 
/*     */   public Set<V> removeAll(@Nullable Object key)
/*     */   {
/*  85 */     return (Set)super.removeAll(key);
/*     */   }
/*     */ 
/*     */   public Set<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/*  99 */     return (Set)super.replaceValues(key, values);
/*     */   }
/*     */ 
/*     */   public Map<K, Collection<V>> asMap()
/*     */   {
/* 109 */     return super.asMap();
/*     */   }
/*     */ 
/*     */   public boolean put(@Nullable K key, @Nullable V value)
/*     */   {
/* 121 */     return super.put(key, value);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 132 */     return super.equals(object);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractSetMultimap
 * JD-Core Version:    0.6.2
 */