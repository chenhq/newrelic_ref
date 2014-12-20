/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Objects;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingMapEntry<K, V> extends ForwardingObject
/*     */   implements Map.Entry<K, V>
/*     */ {
/*     */   protected abstract Map.Entry<K, V> delegate();
/*     */ 
/*     */   public K getKey()
/*     */   {
/*  66 */     return delegate().getKey();
/*     */   }
/*     */ 
/*     */   public V getValue()
/*     */   {
/*  71 */     return delegate().getValue();
/*     */   }
/*     */ 
/*     */   public V setValue(V value)
/*     */   {
/*  76 */     return delegate().setValue(value);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/*  80 */     return delegate().equals(object);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  84 */     return delegate().hashCode();
/*     */   }
/*     */ 
/*     */   protected boolean standardEquals(@Nullable Object object)
/*     */   {
/*  96 */     if ((object instanceof Map.Entry)) {
/*  97 */       Map.Entry that = (Map.Entry)object;
/*  98 */       return (Objects.equal(getKey(), that.getKey())) && (Objects.equal(getValue(), that.getValue()));
/*     */     }
/*     */ 
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   protected int standardHashCode()
/*     */   {
/* 112 */     Object k = getKey();
/* 113 */     Object v = getValue();
/* 114 */     return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected String standardToString()
/*     */   {
/* 126 */     return getKey() + "=" + getValue();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingMapEntry
 * JD-Core Version:    0.6.2
 */