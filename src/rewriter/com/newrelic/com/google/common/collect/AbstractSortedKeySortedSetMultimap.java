/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Collection;
/*    */ import java.util.SortedMap;
/*    */ import java.util.SortedSet;
/*    */ 
/*    */ @GwtCompatible
/*    */ abstract class AbstractSortedKeySortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V>
/*    */ {
/*    */   AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> map)
/*    */   {
/* 38 */     super(map);
/*    */   }
/*    */ 
/*    */   public SortedMap<K, Collection<V>> asMap()
/*    */   {
/* 43 */     return (SortedMap)super.asMap();
/*    */   }
/*    */ 
/*    */   SortedMap<K, Collection<V>> backingMap()
/*    */   {
/* 48 */     return (SortedMap)super.backingMap();
/*    */   }
/*    */ 
/*    */   public SortedSet<K> keySet()
/*    */   {
/* 53 */     return (SortedSet)super.keySet();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractSortedKeySortedSetMultimap
 * JD-Core Version:    0.6.2
 */