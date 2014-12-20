/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.Map.Entry;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ final class ImmutableMapValues<K, V> extends ImmutableCollection<V>
/*    */ {
/*    */   private final ImmutableMap<K, V> map;
/*    */ 
/*    */   ImmutableMapValues(ImmutableMap<K, V> map)
/*    */   {
/* 38 */     this.map = map;
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 43 */     return this.map.size();
/*    */   }
/*    */ 
/*    */   public UnmodifiableIterator<V> iterator()
/*    */   {
/* 48 */     return Maps.valueIterator(this.map.entrySet().iterator());
/*    */   }
/*    */ 
/*    */   public boolean contains(@Nullable Object object)
/*    */   {
/* 53 */     return (object != null) && (Iterators.contains(iterator(), object));
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 58 */     return true;
/*    */   }
/*    */ 
/*    */   ImmutableList<V> createAsList()
/*    */   {
/* 63 */     final ImmutableList entryList = this.map.entrySet().asList();
/* 64 */     return new ImmutableAsList()
/*    */     {
/*    */       public V get(int index) {
/* 67 */         return ((Map.Entry)entryList.get(index)).getValue();
/*    */       }
/*    */ 
/*    */       ImmutableCollection<V> delegateCollection()
/*    */       {
/* 72 */         return ImmutableMapValues.this;
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   Object writeReplace() {
/* 79 */     return new SerializedForm(this.map);
/*    */   }
/*    */   @GwtIncompatible("serialization")
/*    */   private static class SerializedForm<V> implements Serializable { final ImmutableMap<?, V> map;
/*    */     private static final long serialVersionUID = 0L;
/*    */ 
/* 86 */     SerializedForm(ImmutableMap<?, V> map) { this.map = map; }
/*    */ 
/*    */     Object readResolve() {
/* 89 */       return this.map.values();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableMapValues
 * JD-Core Version:    0.6.2
 */