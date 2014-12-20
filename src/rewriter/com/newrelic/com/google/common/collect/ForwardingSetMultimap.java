/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingSetMultimap<K, V> extends ForwardingMultimap<K, V>
/*    */   implements SetMultimap<K, V>
/*    */ {
/*    */   protected abstract SetMultimap<K, V> delegate();
/*    */ 
/*    */   public Set<Map.Entry<K, V>> entries()
/*    */   {
/* 42 */     return delegate().entries();
/*    */   }
/*    */ 
/*    */   public Set<V> get(@Nullable K key) {
/* 46 */     return delegate().get(key);
/*    */   }
/*    */ 
/*    */   public Set<V> removeAll(@Nullable Object key) {
/* 50 */     return delegate().removeAll(key);
/*    */   }
/*    */ 
/*    */   public Set<V> replaceValues(K key, Iterable<? extends V> values) {
/* 54 */     return delegate().replaceValues(key, values);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingSetMultimap
 * JD-Core Version:    0.6.2
 */