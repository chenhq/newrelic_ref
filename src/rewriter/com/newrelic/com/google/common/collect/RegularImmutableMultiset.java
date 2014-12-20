/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Map.Entry;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ class RegularImmutableMultiset<E> extends ImmutableMultiset<E>
/*    */ {
/*    */   private final transient ImmutableMap<E, Integer> map;
/*    */   private final transient int size;
/*    */ 
/*    */   RegularImmutableMultiset(ImmutableMap<E, Integer> map, int size)
/*    */   {
/* 39 */     this.map = map;
/* 40 */     this.size = size;
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 45 */     return this.map.isPartialView();
/*    */   }
/*    */ 
/*    */   public int count(@Nullable Object element)
/*    */   {
/* 50 */     Integer value = (Integer)this.map.get(element);
/* 51 */     return value == null ? 0 : value.intValue();
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 56 */     return this.size;
/*    */   }
/*    */ 
/*    */   public boolean contains(@Nullable Object element)
/*    */   {
/* 61 */     return this.map.containsKey(element);
/*    */   }
/*    */ 
/*    */   public ImmutableSet<E> elementSet()
/*    */   {
/* 66 */     return this.map.keySet();
/*    */   }
/*    */ 
/*    */   Multiset.Entry<E> getEntry(int index)
/*    */   {
/* 71 */     Map.Entry mapEntry = (Map.Entry)this.map.entrySet().asList().get(index);
/* 72 */     return Multisets.immutableEntry(mapEntry.getKey(), ((Integer)mapEntry.getValue()).intValue());
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 77 */     return this.map.hashCode();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.RegularImmutableMultiset
 * JD-Core Version:    0.6.2
 */