/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableBiMap<K, V> extends ImmutableMap<K, V>
/*     */   implements BiMap<K, V>
/*     */ {
/* 214 */   private static final Map.Entry<?, ?>[] EMPTY_ENTRY_ARRAY = new Map.Entry[0];
/*     */ 
/*     */   public static <K, V> ImmutableBiMap<K, V> of()
/*     */   {
/*  50 */     return EmptyImmutableBiMap.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1)
/*     */   {
/*  57 */     return new SingletonImmutableBiMap(k1, v1);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  66 */     return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2) });
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/*  76 */     return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/*  86 */     return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableBiMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/*  97 */     return new RegularImmutableBiMap(new ImmutableMapEntry.TerminalEntry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5) });
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> builder()
/*     */   {
/* 108 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableBiMap<K, V> copyOf(Map<? extends K, ? extends V> map)
/*     */   {
/* 192 */     if ((map instanceof ImmutableBiMap))
/*     */     {
/* 194 */       ImmutableBiMap bimap = (ImmutableBiMap)map;
/*     */ 
/* 197 */       if (!bimap.isPartialView()) {
/* 198 */         return bimap;
/*     */       }
/*     */     }
/* 201 */     Map.Entry[] entries = (Map.Entry[])map.entrySet().toArray(EMPTY_ENTRY_ARRAY);
/* 202 */     switch (entries.length) {
/*     */     case 0:
/* 204 */       return of();
/*     */     case 1:
/* 207 */       Map.Entry entry = entries[0];
/* 208 */       return of(entry.getKey(), entry.getValue());
/*     */     }
/* 210 */     return new RegularImmutableBiMap(entries);
/*     */   }
/*     */ 
/*     */   public abstract ImmutableBiMap<V, K> inverse();
/*     */ 
/*     */   public ImmutableSet<V> values()
/*     */   {
/* 232 */     return inverse().keySet();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public V forcePut(K key, V value)
/*     */   {
/* 244 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 268 */     return new SerializedForm(this);
/*     */   }
/*     */ 
/*     */   private static class SerializedForm extends ImmutableMap.SerializedForm
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SerializedForm(ImmutableBiMap<?, ?> bimap)
/*     */     {
/* 258 */       super();
/*     */     }
/*     */     Object readResolve() {
/* 261 */       ImmutableBiMap.Builder builder = new ImmutableBiMap.Builder();
/* 262 */       return createMap(builder);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Builder<K, V> extends ImmutableMap.Builder<K, V>
/*     */   {
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 144 */       super.put(key, value);
/* 145 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Map<? extends K, ? extends V> map)
/*     */     {
/* 156 */       super.putAll(map);
/* 157 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableBiMap<K, V> build()
/*     */     {
/* 166 */       switch (this.size) {
/*     */       case 0:
/* 168 */         return ImmutableBiMap.of();
/*     */       case 1:
/* 170 */         return ImmutableBiMap.of(this.entries[0].getKey(), this.entries[0].getValue());
/*     */       }
/* 172 */       return new RegularImmutableBiMap(this.size, this.entries);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableBiMap
 * JD-Core Version:    0.6.2
 */