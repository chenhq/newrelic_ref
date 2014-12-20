/*    */ package com.newrelic.com.google.common.cache;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.collect.ImmutableMap;
/*    */ import com.newrelic.com.google.common.collect.Maps;
/*    */ import com.newrelic.com.google.common.util.concurrent.UncheckedExecutionException;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ 
/*    */ @Beta
/*    */ public abstract class AbstractLoadingCache<K, V> extends AbstractCache<K, V>
/*    */   implements LoadingCache<K, V>
/*    */ {
/*    */   public V getUnchecked(K key)
/*    */   {
/*    */     try
/*    */     {
/* 53 */       return get(key);
/*    */     } catch (ExecutionException e) {
/* 55 */       throw new UncheckedExecutionException(e.getCause());
/*    */     }
/*    */   }
/*    */ 
/*    */   public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*    */   {
/* 61 */     Map result = Maps.newLinkedHashMap();
/* 62 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 63 */       if (!result.containsKey(key)) {
/* 64 */         result.put(key, get(key));
/*    */       }
/*    */     }
/* 67 */     return ImmutableMap.copyOf(result);
/*    */   }
/*    */ 
/*    */   public final V apply(K key)
/*    */   {
/* 72 */     return getUnchecked(key);
/*    */   }
/*    */ 
/*    */   public void refresh(K key)
/*    */   {
/* 77 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.AbstractLoadingCache
 * JD-Core Version:    0.6.2
 */