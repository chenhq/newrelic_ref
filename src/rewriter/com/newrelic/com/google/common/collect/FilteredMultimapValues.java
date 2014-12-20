/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Objects;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import com.newrelic.com.google.common.base.Predicate;
/*    */ import com.newrelic.com.google.common.base.Predicates;
/*    */ import java.util.AbstractCollection;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map.Entry;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class FilteredMultimapValues<K, V> extends AbstractCollection<V>
/*    */ {
/*    */   private final FilteredMultimap<K, V> multimap;
/*    */ 
/*    */   FilteredMultimapValues(FilteredMultimap<K, V> multimap)
/*    */   {
/* 42 */     this.multimap = ((FilteredMultimap)Preconditions.checkNotNull(multimap));
/*    */   }
/*    */ 
/*    */   public Iterator<V> iterator()
/*    */   {
/* 47 */     return Maps.valueIterator(this.multimap.entries().iterator());
/*    */   }
/*    */ 
/*    */   public boolean contains(@Nullable Object o)
/*    */   {
/* 52 */     return this.multimap.containsValue(o);
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 57 */     return this.multimap.size();
/*    */   }
/*    */ 
/*    */   public boolean remove(@Nullable Object o)
/*    */   {
/* 62 */     Predicate entryPredicate = this.multimap.entryPredicate();
/* 63 */     Iterator unfilteredItr = this.multimap.unfiltered().entries().iterator();
/* 64 */     while (unfilteredItr.hasNext()) {
/* 65 */       Map.Entry entry = (Map.Entry)unfilteredItr.next();
/* 66 */       if ((entryPredicate.apply(entry)) && (Objects.equal(entry.getValue(), o))) {
/* 67 */         unfilteredItr.remove();
/* 68 */         return true;
/*    */       }
/*    */     }
/* 71 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean removeAll(Collection<?> c)
/*    */   {
/* 76 */     return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.in(c))));
/*    */   }
/*    */ 
/*    */   public boolean retainAll(Collection<?> c)
/*    */   {
/* 84 */     return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(c)))));
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 92 */     this.multimap.clear();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.FilteredMultimapValues
 * JD-Core Version:    0.6.2
 */