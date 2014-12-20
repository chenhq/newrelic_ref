/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Predicate;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class FilteredKeyListMultimap<K, V> extends FilteredKeyMultimap<K, V>
/*    */   implements ListMultimap<K, V>
/*    */ {
/*    */   FilteredKeyListMultimap(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*    */   {
/* 35 */     super(unfiltered, keyPredicate);
/*    */   }
/*    */ 
/*    */   public ListMultimap<K, V> unfiltered()
/*    */   {
/* 40 */     return (ListMultimap)super.unfiltered();
/*    */   }
/*    */ 
/*    */   public List<V> get(K key)
/*    */   {
/* 45 */     return (List)super.get(key);
/*    */   }
/*    */ 
/*    */   public List<V> removeAll(@Nullable Object key)
/*    */   {
/* 50 */     return (List)super.removeAll(key);
/*    */   }
/*    */ 
/*    */   public List<V> replaceValues(K key, Iterable<? extends V> values)
/*    */   {
/* 55 */     return (List)super.replaceValues(key, values);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.FilteredKeyListMultimap
 * JD-Core Version:    0.6.2
 */