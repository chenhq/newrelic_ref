/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ abstract class ImmutableSortedMapFauxverideShim<K, V> extends ImmutableMap<K, V>
/*     */ {
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap.Builder<K, V> builder()
/*     */   {
/*  38 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1)
/*     */   {
/*  51 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  65 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/*  80 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/*  95 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 110 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableSortedMapFauxverideShim
 * JD-Core Version:    0.6.2
 */