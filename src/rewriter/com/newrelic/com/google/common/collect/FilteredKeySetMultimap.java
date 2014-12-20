/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Predicate;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class FilteredKeySetMultimap<K, V> extends FilteredKeyMultimap<K, V>
/*    */   implements FilteredSetMultimap<K, V>
/*    */ {
/*    */   FilteredKeySetMultimap(SetMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*    */   {
/* 37 */     super(unfiltered, keyPredicate);
/*    */   }
/*    */ 
/*    */   public SetMultimap<K, V> unfiltered()
/*    */   {
/* 42 */     return (SetMultimap)this.unfiltered;
/*    */   }
/*    */ 
/*    */   public Set<V> get(K key)
/*    */   {
/* 47 */     return (Set)super.get(key);
/*    */   }
/*    */ 
/*    */   public Set<V> removeAll(Object key)
/*    */   {
/* 52 */     return (Set)super.removeAll(key);
/*    */   }
/*    */ 
/*    */   public Set<V> replaceValues(K key, Iterable<? extends V> values)
/*    */   {
/* 57 */     return (Set)super.replaceValues(key, values);
/*    */   }
/*    */ 
/*    */   public Set<Map.Entry<K, V>> entries()
/*    */   {
/* 62 */     return (Set)super.entries();
/*    */   }
/*    */ 
/*    */   Set<Map.Entry<K, V>> createEntries()
/*    */   {
/* 67 */     return new EntrySet();
/*    */   }
/*    */   class EntrySet extends FilteredKeyMultimap<K, V>.Entries implements Set<Map.Entry<K, V>> {
/* 70 */     EntrySet() { super(); }
/*    */ 
/*    */     public int hashCode() {
/* 73 */       return Sets.hashCodeImpl(this);
/*    */     }
/*    */ 
/*    */     public boolean equals(@Nullable Object o)
/*    */     {
/* 78 */       return Sets.equalsImpl(this, o);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.FilteredKeySetMultimap
 * JD-Core Version:    0.6.2
 */