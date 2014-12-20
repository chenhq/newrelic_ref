/*     */ package com.newrelic.com.google.common.cache;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.collect.ImmutableMap;
/*     */ import com.newrelic.com.google.common.collect.Maps;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public abstract class AbstractCache<K, V>
/*     */   implements Cache<K, V>
/*     */ {
/*     */   public V get(K key, Callable<? extends V> valueLoader)
/*     */     throws ExecutionException
/*     */   {
/*  55 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<K, V> getAllPresent(Iterable<?> keys)
/*     */   {
/*  69 */     Map result = Maps.newLinkedHashMap();
/*  70 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/*  71 */       if (!result.containsKey(key))
/*     */       {
/*  73 */         Object castKey = key;
/*  74 */         result.put(castKey, getIfPresent(key));
/*     */       }
/*     */     }
/*  77 */     return ImmutableMap.copyOf(result);
/*     */   }
/*     */ 
/*     */   public void put(K key, V value)
/*     */   {
/*  85 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> m)
/*     */   {
/*  93 */     for (Map.Entry entry : m.entrySet())
/*  94 */       put(entry.getKey(), entry.getValue());
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/*     */   }
/*     */ 
/*     */   public long size()
/*     */   {
/* 103 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void invalidate(Object key)
/*     */   {
/* 108 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void invalidateAll(Iterable<?> keys)
/*     */   {
/* 116 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 117 */       invalidate(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invalidateAll()
/*     */   {
/* 123 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public CacheStats stats()
/*     */   {
/* 128 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ConcurrentMap<K, V> asMap()
/*     */   {
/* 133 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static final class SimpleStatsCounter
/*     */     implements AbstractCache.StatsCounter
/*     */   {
/* 206 */     private final LongAddable hitCount = LongAddables.create();
/* 207 */     private final LongAddable missCount = LongAddables.create();
/* 208 */     private final LongAddable loadSuccessCount = LongAddables.create();
/* 209 */     private final LongAddable loadExceptionCount = LongAddables.create();
/* 210 */     private final LongAddable totalLoadTime = LongAddables.create();
/* 211 */     private final LongAddable evictionCount = LongAddables.create();
/*     */ 
/*     */     public void recordHits(int count)
/*     */     {
/* 223 */       this.hitCount.add(count);
/*     */     }
/*     */ 
/*     */     public void recordMisses(int count)
/*     */     {
/* 231 */       this.missCount.add(count);
/*     */     }
/*     */ 
/*     */     public void recordLoadSuccess(long loadTime)
/*     */     {
/* 236 */       this.loadSuccessCount.increment();
/* 237 */       this.totalLoadTime.add(loadTime);
/*     */     }
/*     */ 
/*     */     public void recordLoadException(long loadTime)
/*     */     {
/* 242 */       this.loadExceptionCount.increment();
/* 243 */       this.totalLoadTime.add(loadTime);
/*     */     }
/*     */ 
/*     */     public void recordEviction()
/*     */     {
/* 248 */       this.evictionCount.increment();
/*     */     }
/*     */ 
/*     */     public CacheStats snapshot()
/*     */     {
/* 253 */       return new CacheStats(this.hitCount.sum(), this.missCount.sum(), this.loadSuccessCount.sum(), this.loadExceptionCount.sum(), this.totalLoadTime.sum(), this.evictionCount.sum());
/*     */     }
/*     */ 
/*     */     public void incrementBy(AbstractCache.StatsCounter other)
/*     */     {
/* 266 */       CacheStats otherStats = other.snapshot();
/* 267 */       this.hitCount.add(otherStats.hitCount());
/* 268 */       this.missCount.add(otherStats.missCount());
/* 269 */       this.loadSuccessCount.add(otherStats.loadSuccessCount());
/* 270 */       this.loadExceptionCount.add(otherStats.loadExceptionCount());
/* 271 */       this.totalLoadTime.add(otherStats.totalLoadTime());
/* 272 */       this.evictionCount.add(otherStats.evictionCount());
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static abstract interface StatsCounter
/*     */   {
/*     */     public abstract void recordHits(int paramInt);
/*     */ 
/*     */     public abstract void recordMisses(int paramInt);
/*     */ 
/*     */     public abstract void recordLoadSuccess(long paramLong);
/*     */ 
/*     */     public abstract void recordLoadException(long paramLong);
/*     */ 
/*     */     public abstract void recordEviction();
/*     */ 
/*     */     public abstract CacheStats snapshot();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.AbstractCache
 * JD-Core Version:    0.6.2
 */