/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V>
/*    */   implements ListMultimap<K, V>
/*    */ {
/*    */   protected abstract ListMultimap<K, V> delegate();
/*    */ 
/*    */   public List<V> get(@Nullable K key)
/*    */   {
/* 44 */     return delegate().get(key);
/*    */   }
/*    */ 
/*    */   public List<V> removeAll(@Nullable Object key) {
/* 48 */     return delegate().removeAll(key);
/*    */   }
/*    */ 
/*    */   public List<V> replaceValues(K key, Iterable<? extends V> values) {
/* 52 */     return delegate().replaceValues(key, values);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingListMultimap
 * JD-Core Version:    0.6.2
 */