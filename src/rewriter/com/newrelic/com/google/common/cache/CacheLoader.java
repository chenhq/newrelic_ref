/*     */ package com.newrelic.com.google.common.cache;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Function;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Supplier;
/*     */ import com.newrelic.com.google.common.util.concurrent.Futures;
/*     */ import com.newrelic.com.google.common.util.concurrent.ListenableFuture;
/*     */ import com.newrelic.com.google.common.util.concurrent.ListenableFutureTask;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class CacheLoader<K, V>
/*     */ {
/*     */   public abstract V load(K paramK)
/*     */     throws Exception;
/*     */ 
/*     */   @GwtIncompatible("Futures")
/*     */   public ListenableFuture<V> reload(K key, V oldValue)
/*     */     throws Exception
/*     */   {
/*  95 */     Preconditions.checkNotNull(key);
/*  96 */     Preconditions.checkNotNull(oldValue);
/*  97 */     return Futures.immediateFuture(load(key));
/*     */   }
/*     */ 
/*     */   public Map<K, V> loadAll(Iterable<? extends K> keys)
/*     */     throws Exception
/*     */   {
/* 125 */     throw new UnsupportedLoadingOperationException();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <K, V> CacheLoader<K, V> from(Function<K, V> function)
/*     */   {
/* 138 */     return new FunctionToCacheLoader(function);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <V> CacheLoader<Object, V> from(Supplier<V> supplier)
/*     */   {
/* 168 */     return new SupplierToCacheLoader(supplier);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   @GwtIncompatible("Executor + Futures")
/*     */   public static <K, V> CacheLoader<K, V> asyncReloading(CacheLoader<K, V> loader, final Executor executor)
/*     */   {
/* 184 */     Preconditions.checkNotNull(loader);
/* 185 */     Preconditions.checkNotNull(executor);
/* 186 */     return new CacheLoader()
/*     */     {
/*     */       public V load(K key) throws Exception {
/* 189 */         return this.val$loader.load(key);
/*     */       }
/*     */ 
/*     */       public ListenableFuture<V> reload(final K key, final V oldValue) throws Exception
/*     */       {
/* 194 */         ListenableFutureTask task = ListenableFutureTask.create(new Callable()
/*     */         {
/*     */           public V call() throws Exception {
/* 197 */             return CacheLoader.1.this.val$loader.reload(key, oldValue).get();
/*     */           }
/*     */         });
/* 200 */         executor.execute(task);
/* 201 */         return task;
/*     */       }
/*     */ 
/*     */       public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception
/*     */       {
/* 206 */         return this.val$loader.loadAll(keys);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static final class InvalidCacheLoadException extends RuntimeException
/*     */   {
/*     */     public InvalidCacheLoadException(String message)
/*     */     {
/* 237 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnsupportedLoadingOperationException extends UnsupportedOperationException
/*     */   {
/*     */   }
/*     */ 
/*     */   private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V>
/*     */     implements Serializable
/*     */   {
/*     */     private final Supplier<V> computingSupplier;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public SupplierToCacheLoader(Supplier<V> computingSupplier)
/*     */     {
/* 216 */       this.computingSupplier = ((Supplier)Preconditions.checkNotNull(computingSupplier));
/*     */     }
/*     */ 
/*     */     public V load(Object key)
/*     */     {
/* 221 */       Preconditions.checkNotNull(key);
/* 222 */       return this.computingSupplier.get();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V>
/*     */     implements Serializable
/*     */   {
/*     */     private final Function<K, V> computingFunction;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public FunctionToCacheLoader(Function<K, V> computingFunction)
/*     */     {
/* 146 */       this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */     }
/*     */ 
/*     */     public V load(K key)
/*     */     {
/* 151 */       return this.computingFunction.apply(Preconditions.checkNotNull(key));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.CacheLoader
 * JD-Core Version:    0.6.2
 */