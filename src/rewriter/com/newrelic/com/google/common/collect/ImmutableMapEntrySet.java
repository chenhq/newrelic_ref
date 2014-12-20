/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.Map.Entry;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>>
/*    */ {
/*    */   abstract ImmutableMap<K, V> map();
/*    */ 
/*    */   public int size()
/*    */   {
/* 41 */     return map().size();
/*    */   }
/*    */ 
/*    */   public boolean contains(@Nullable Object object)
/*    */   {
/* 46 */     if ((object instanceof Map.Entry)) {
/* 47 */       Map.Entry entry = (Map.Entry)object;
/* 48 */       Object value = map().get(entry.getKey());
/* 49 */       return (value != null) && (value.equals(entry.getValue()));
/*    */     }
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 56 */     return map().isPartialView();
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   Object writeReplace()
/*    */   {
/* 62 */     return new EntrySetSerializedForm(map());
/*    */   }
/*    */   @GwtIncompatible("serialization")
/*    */   private static class EntrySetSerializedForm<K, V> implements Serializable { final ImmutableMap<K, V> map;
/*    */     private static final long serialVersionUID = 0L;
/*    */ 
/* 69 */     EntrySetSerializedForm(ImmutableMap<K, V> map) { this.map = map; }
/*    */ 
/*    */     Object readResolve() {
/* 72 */       return this.map.entrySet();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableMapEntrySet
 * JD-Core Version:    0.6.2
 */