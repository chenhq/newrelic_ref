/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Objects;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingMap<K, V> extends ForwardingObject
/*     */   implements Map<K, V>
/*     */ {
/*     */   protected abstract Map<K, V> delegate();
/*     */ 
/*     */   public int size()
/*     */   {
/*  70 */     return delegate().size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  75 */     return delegate().isEmpty();
/*     */   }
/*     */ 
/*     */   public V remove(Object object)
/*     */   {
/*  80 */     return delegate().remove(object);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  85 */     delegate().clear();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/*  90 */     return delegate().containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/*  95 */     return delegate().containsValue(value);
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object key)
/*     */   {
/* 100 */     return delegate().get(key);
/*     */   }
/*     */ 
/*     */   public V put(K key, V value)
/*     */   {
/* 105 */     return delegate().put(key, value);
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> map)
/*     */   {
/* 110 */     delegate().putAll(map);
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 115 */     return delegate().keySet();
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 120 */     return delegate().values();
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet()
/*     */   {
/* 125 */     return delegate().entrySet();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 129 */     return (object == this) || (delegate().equals(object));
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 133 */     return delegate().hashCode();
/*     */   }
/*     */ 
/*     */   protected void standardPutAll(Map<? extends K, ? extends V> map)
/*     */   {
/* 145 */     Maps.putAllImpl(this, map);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected V standardRemove(@Nullable Object key)
/*     */   {
/* 161 */     Iterator entryIterator = entrySet().iterator();
/* 162 */     while (entryIterator.hasNext()) {
/* 163 */       Map.Entry entry = (Map.Entry)entryIterator.next();
/* 164 */       if (Objects.equal(entry.getKey(), key)) {
/* 165 */         Object value = entry.getValue();
/* 166 */         entryIterator.remove();
/* 167 */         return value;
/*     */       }
/*     */     }
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   protected void standardClear()
/*     */   {
/* 181 */     Iterators.clear(entrySet().iterator());
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardContainsKey(@Nullable Object key)
/*     */   {
/* 212 */     return Maps.containsKeyImpl(this, key);
/*     */   }
/*     */ 
/*     */   protected boolean standardContainsValue(@Nullable Object value)
/*     */   {
/* 242 */     return Maps.containsValueImpl(this, value);
/*     */   }
/*     */ 
/*     */   protected boolean standardIsEmpty()
/*     */   {
/* 274 */     return !entrySet().iterator().hasNext();
/*     */   }
/*     */ 
/*     */   protected boolean standardEquals(@Nullable Object object)
/*     */   {
/* 285 */     return Maps.equalsImpl(this, object);
/*     */   }
/*     */ 
/*     */   protected int standardHashCode()
/*     */   {
/* 296 */     return Sets.hashCodeImpl(entrySet());
/*     */   }
/*     */ 
/*     */   protected String standardToString()
/*     */   {
/* 307 */     return Maps.toStringImpl(this);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected abstract class StandardEntrySet extends Maps.EntrySet<K, V>
/*     */   {
/*     */     public StandardEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     Map<K, V> map()
/*     */     {
/* 262 */       return ForwardingMap.this;
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected class StandardValues extends Maps.Values<K, V>
/*     */   {
/*     */     public StandardValues()
/*     */     {
/* 229 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected class StandardKeySet extends Maps.KeySet<K, V>
/*     */   {
/*     */     public StandardKeySet()
/*     */     {
/* 199 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingMap
 * JD-Core Version:    0.6.2
 */