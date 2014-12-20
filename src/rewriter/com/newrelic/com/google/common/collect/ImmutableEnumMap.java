/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class ImmutableEnumMap<K extends Enum<K>, V> extends ImmutableMap<K, V>
/*     */ {
/*     */   private final transient EnumMap<K, V> delegate;
/*     */ 
/*     */   static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> map)
/*     */   {
/*  38 */     switch (map.size()) {
/*     */     case 0:
/*  40 */       return ImmutableMap.of();
/*     */     case 1:
/*  42 */       Map.Entry entry = (Map.Entry)Iterables.getOnlyElement(map.entrySet());
/*  43 */       return ImmutableMap.of(entry.getKey(), entry.getValue());
/*     */     }
/*     */ 
/*  46 */     return new ImmutableEnumMap(map);
/*     */   }
/*     */ 
/*     */   private ImmutableEnumMap(EnumMap<K, V> delegate)
/*     */   {
/*  53 */     this.delegate = delegate;
/*  54 */     Preconditions.checkArgument(!delegate.isEmpty());
/*     */   }
/*     */ 
/*     */   ImmutableSet<K> createKeySet()
/*     */   {
/*  59 */     return new ImmutableSet()
/*     */     {
/*     */       public boolean contains(Object object)
/*     */       {
/*  63 */         return ImmutableEnumMap.this.delegate.containsKey(object);
/*     */       }
/*     */ 
/*     */       public int size()
/*     */       {
/*  68 */         return ImmutableEnumMap.this.size();
/*     */       }
/*     */ 
/*     */       public UnmodifiableIterator<K> iterator()
/*     */       {
/*  73 */         return Iterators.unmodifiableIterator(ImmutableEnumMap.this.delegate.keySet().iterator());
/*     */       }
/*     */ 
/*     */       boolean isPartialView()
/*     */       {
/*  78 */         return true;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  85 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/*  90 */     return this.delegate.containsKey(key);
/*     */   }
/*     */ 
/*     */   public V get(Object key)
/*     */   {
/*  95 */     return this.delegate.get(key);
/*     */   }
/*     */ 
/*     */   ImmutableSet<Map.Entry<K, V>> createEntrySet()
/*     */   {
/* 100 */     return new ImmutableMapEntrySet()
/*     */     {
/*     */       ImmutableMap<K, V> map()
/*     */       {
/* 104 */         return ImmutableEnumMap.this;
/*     */       }
/*     */ 
/*     */       public UnmodifiableIterator<Map.Entry<K, V>> iterator()
/*     */       {
/* 109 */         return new UnmodifiableIterator() {
/* 110 */           private final Iterator<Map.Entry<K, V>> backingIterator = ImmutableEnumMap.this.delegate.entrySet().iterator();
/*     */ 
/*     */           public boolean hasNext()
/*     */           {
/* 114 */             return this.backingIterator.hasNext();
/*     */           }
/*     */ 
/*     */           public Map.Entry<K, V> next()
/*     */           {
/* 119 */             Map.Entry entry = (Map.Entry)this.backingIterator.next();
/* 120 */             return Maps.immutableEntry(entry.getKey(), entry.getValue());
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/* 129 */     return false;
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 134 */     return new EnumSerializedForm(this.delegate);
/*     */   }
/*     */ 
/*     */   private static class EnumSerializedForm<K extends Enum<K>, V> implements Serializable
/*     */   {
/*     */     final EnumMap<K, V> delegate;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     EnumSerializedForm(EnumMap<K, V> delegate) {
/* 144 */       this.delegate = delegate;
/*     */     }
/*     */     Object readResolve() {
/* 147 */       return new ImmutableEnumMap(this.delegate, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableEnumMap
 * JD-Core Version:    0.6.2
 */