/*     */ package com.newrelic.com.google.common.reflect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Function;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.collect.ForwardingMap;
/*     */ import com.newrelic.com.google.common.collect.ForwardingMapEntry;
/*     */ import com.newrelic.com.google.common.collect.ForwardingSet;
/*     */ import com.newrelic.com.google.common.collect.Iterators;
/*     */ import com.newrelic.com.google.common.collect.Maps;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public final class MutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B>
/*     */   implements TypeToInstanceMap<B>
/*     */ {
/*     */   private final Map<TypeToken<? extends B>, B> backingMap;
/*     */ 
/*     */   public MutableTypeToInstanceMap()
/*     */   {
/*  46 */     this.backingMap = Maps.newHashMap();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public <T extends B> T getInstance(Class<T> type) {
/*  51 */     return trustedGet(TypeToken.of(type));
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public <T extends B> T putInstance(Class<T> type, @Nullable T value)
/*     */   {
/*  57 */     return trustedPut(TypeToken.of(type), value);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public <T extends B> T getInstance(TypeToken<T> type)
/*     */   {
/*  63 */     return trustedGet(type.rejectTypeVariables());
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public <T extends B> T putInstance(TypeToken<T> type, @Nullable T value)
/*     */   {
/*  69 */     return trustedPut(type.rejectTypeVariables(), value);
/*     */   }
/*     */ 
/*     */   public B put(TypeToken<? extends B> key, B value)
/*     */   {
/*  74 */     throw new UnsupportedOperationException("Please use putInstance() instead.");
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> map)
/*     */   {
/*  79 */     throw new UnsupportedOperationException("Please use putInstance() instead.");
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<TypeToken<? extends B>, B>> entrySet() {
/*  83 */     return UnmodifiableEntry.transformEntries(super.entrySet());
/*     */   }
/*     */ 
/*     */   protected Map<TypeToken<? extends B>, B> delegate() {
/*  87 */     return this.backingMap;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private <T extends B> T trustedPut(TypeToken<T> type, @Nullable T value)
/*     */   {
/*  93 */     return this.backingMap.put(type, value);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private <T extends B> T trustedGet(TypeToken<T> type)
/*     */   {
/*  99 */     return this.backingMap.get(type);
/*     */   }
/*     */ 
/*     */   private static final class UnmodifiableEntry<K, V> extends ForwardingMapEntry<K, V>
/*     */   {
/*     */     private final Map.Entry<K, V> delegate;
/*     */ 
/*     */     static <K, V> Set<Map.Entry<K, V>> transformEntries(Set<Map.Entry<K, V>> entries) {
/* 107 */       return new ForwardingSet() {
/*     */         protected Set<Map.Entry<K, V>> delegate() {
/* 109 */           return this.val$entries;
/*     */         }
/*     */         public Iterator<Map.Entry<K, V>> iterator() {
/* 112 */           return MutableTypeToInstanceMap.UnmodifiableEntry.transformEntries(super.iterator());
/*     */         }
/*     */         public Object[] toArray() {
/* 115 */           return standardToArray();
/*     */         }
/*     */         public <T> T[] toArray(T[] array) {
/* 118 */           return standardToArray(array);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     private static <K, V> Iterator<Map.Entry<K, V>> transformEntries(Iterator<Map.Entry<K, V>> entries) {
/* 124 */       return Iterators.transform(entries, new Function() {
/*     */         public Map.Entry<K, V> apply(Map.Entry<K, V> entry) {
/* 126 */           return new MutableTypeToInstanceMap.UnmodifiableEntry(entry, null);
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     private UnmodifiableEntry(Map.Entry<K, V> delegate) {
/* 132 */       this.delegate = ((Map.Entry)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */ 
/*     */     protected Map.Entry<K, V> delegate() {
/* 136 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public V setValue(V value) {
/* 140 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.reflect.MutableTypeToInstanceMap
 * JD-Core Version:    0.6.2
 */