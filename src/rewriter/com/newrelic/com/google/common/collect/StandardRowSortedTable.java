/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Supplier;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ 
/*     */ @GwtCompatible
/*     */ class StandardRowSortedTable<R, C, V> extends StandardTable<R, C, V>
/*     */   implements RowSortedTable<R, C, V>
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   StandardRowSortedTable(SortedMap<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory)
/*     */   {
/*  59 */     super(backingMap, factory);
/*     */   }
/*     */ 
/*     */   private SortedMap<R, Map<C, V>> sortedBackingMap() {
/*  63 */     return (SortedMap)this.backingMap;
/*     */   }
/*     */ 
/*     */   public SortedSet<R> rowKeySet()
/*     */   {
/*  73 */     return (SortedSet)rowMap().keySet();
/*     */   }
/*     */ 
/*     */   public SortedMap<R, Map<C, V>> rowMap()
/*     */   {
/*  83 */     return (SortedMap)super.rowMap();
/*     */   }
/*     */ 
/*     */   SortedMap<R, Map<C, V>> createRowMap()
/*     */   {
/*  88 */     return new RowSortedMap(null);
/*     */   }
/*     */   private class RowSortedMap extends StandardTable<R, C, V>.RowMap implements SortedMap<R, Map<C, V>> {
/*  91 */     private RowSortedMap() { super(); }
/*     */ 
/*     */     public SortedSet<R> keySet() {
/*  94 */       return (SortedSet)super.keySet();
/*     */     }
/*     */ 
/*     */     SortedSet<R> createKeySet()
/*     */     {
/*  99 */       return new Maps.SortedKeySet(this);
/*     */     }
/*     */ 
/*     */     public Comparator<? super R> comparator()
/*     */     {
/* 104 */       return StandardRowSortedTable.this.sortedBackingMap().comparator();
/*     */     }
/*     */ 
/*     */     public R firstKey()
/*     */     {
/* 109 */       return StandardRowSortedTable.this.sortedBackingMap().firstKey();
/*     */     }
/*     */ 
/*     */     public R lastKey()
/*     */     {
/* 114 */       return StandardRowSortedTable.this.sortedBackingMap().lastKey();
/*     */     }
/*     */ 
/*     */     public SortedMap<R, Map<C, V>> headMap(R toKey)
/*     */     {
/* 119 */       Preconditions.checkNotNull(toKey);
/* 120 */       return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().headMap(toKey), StandardRowSortedTable.this.factory).rowMap();
/*     */     }
/*     */ 
/*     */     public SortedMap<R, Map<C, V>> subMap(R fromKey, R toKey)
/*     */     {
/* 126 */       Preconditions.checkNotNull(fromKey);
/* 127 */       Preconditions.checkNotNull(toKey);
/* 128 */       return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().subMap(fromKey, toKey), StandardRowSortedTable.this.factory).rowMap();
/*     */     }
/*     */ 
/*     */     public SortedMap<R, Map<C, V>> tailMap(R fromKey)
/*     */     {
/* 134 */       Preconditions.checkNotNull(fromKey);
/* 135 */       return new StandardRowSortedTable(StandardRowSortedTable.this.sortedBackingMap().tailMap(fromKey), StandardRowSortedTable.this.factory).rowMap();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.StandardRowSortedTable
 * JD-Core Version:    0.6.2
 */