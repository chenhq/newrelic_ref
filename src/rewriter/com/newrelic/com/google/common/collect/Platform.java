/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Function;
/*    */ import com.newrelic.com.google.common.base.Predicate;
/*    */ import java.lang.reflect.Array;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.NavigableMap;
/*    */ import java.util.NavigableSet;
/*    */ import java.util.Set;
/*    */ import java.util.SortedMap;
/*    */ import java.util.SortedSet;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/*    */   static <T> T[] newArray(T[] reference, int length)
/*    */   {
/* 48 */     Class type = reference.getClass().getComponentType();
/*    */ 
/* 53 */     Object[] result = (Object[])Array.newInstance(type, length);
/* 54 */     return result;
/*    */   }
/*    */ 
/*    */   static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
/* 58 */     return Collections.newSetFromMap(map);
/*    */   }
/*    */ 
/*    */   static MapMaker tryWeakKeys(MapMaker mapMaker)
/*    */   {
/* 68 */     return mapMaker.weakKeys();
/*    */   }
/*    */ 
/*    */   static <K, V1, V2> SortedMap<K, V2> mapsTransformEntriesSortedMap(SortedMap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*    */   {
/* 74 */     return (fromMap instanceof NavigableMap) ? Maps.transformEntries((NavigableMap)fromMap, transformer) : Maps.transformEntriesIgnoreNavigable(fromMap, transformer);
/*    */   }
/*    */ 
/*    */   static <K, V> SortedMap<K, V> mapsAsMapSortedSet(SortedSet<K> set, Function<? super K, V> function)
/*    */   {
/* 81 */     return (set instanceof NavigableSet) ? Maps.asMap((NavigableSet)set, function) : Maps.asMapSortedIgnoreNavigable(set, function);
/*    */   }
/*    */ 
/*    */   static <E> SortedSet<E> setsFilterSortedSet(SortedSet<E> set, Predicate<? super E> predicate)
/*    */   {
/* 88 */     return (set instanceof NavigableSet) ? Sets.filter((NavigableSet)set, predicate) : Sets.filterSortedIgnoreNavigable(set, predicate);
/*    */   }
/*    */ 
/*    */   static <K, V> SortedMap<K, V> mapsFilterSortedMap(SortedMap<K, V> map, Predicate<? super Map.Entry<K, V>> predicate)
/*    */   {
/* 95 */     return (map instanceof NavigableMap) ? Maps.filterEntries((NavigableMap)map, predicate) : Maps.filterSortedIgnoreNavigable(map, predicate);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Platform
 * JD-Core Version:    0.6.2
 */