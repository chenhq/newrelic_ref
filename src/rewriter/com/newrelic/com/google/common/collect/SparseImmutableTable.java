/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Immutable
/*     */ final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V>
/*     */ {
/*     */   private final ImmutableMap<R, Map<C, V>> rowMap;
/*     */   private final ImmutableMap<C, Map<R, V>> columnMap;
/*     */   private final int[] iterationOrderRow;
/*     */   private final int[] iterationOrderColumn;
/*     */ 
/*     */   SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace)
/*     */   {
/*  39 */     Map rowIndex = Maps.newHashMap();
/*  40 */     Map rows = Maps.newLinkedHashMap();
/*  41 */     for (Iterator i$ = rowSpace.iterator(); i$.hasNext(); ) { Object row = i$.next();
/*  42 */       rowIndex.put(row, Integer.valueOf(rows.size()));
/*  43 */       rows.put(row, new LinkedHashMap());
/*     */     }
/*  45 */     Map columns = Maps.newLinkedHashMap();
/*  46 */     for (Iterator i$ = columnSpace.iterator(); i$.hasNext(); ) { Object col = i$.next();
/*  47 */       columns.put(col, new LinkedHashMap());
/*     */     }
/*  49 */     int[] iterationOrderRow = new int[cellList.size()];
/*  50 */     int[] iterationOrderColumn = new int[cellList.size()];
/*  51 */     for (int i = 0; i < cellList.size(); i++) {
/*  52 */       Table.Cell cell = (Table.Cell)cellList.get(i);
/*  53 */       Object rowKey = cell.getRowKey();
/*  54 */       Object columnKey = cell.getColumnKey();
/*  55 */       Object value = cell.getValue();
/*     */ 
/*  57 */       iterationOrderRow[i] = ((Integer)rowIndex.get(rowKey)).intValue();
/*  58 */       Map thisRow = (Map)rows.get(rowKey);
/*  59 */       iterationOrderColumn[i] = thisRow.size();
/*  60 */       Object oldValue = thisRow.put(columnKey, value);
/*  61 */       if (oldValue != null) {
/*  62 */         throw new IllegalArgumentException("Duplicate value for row=" + rowKey + ", column=" + columnKey + ": " + value + ", " + oldValue);
/*     */       }
/*     */ 
/*  65 */       ((Map)columns.get(columnKey)).put(rowKey, value);
/*     */     }
/*  67 */     this.iterationOrderRow = iterationOrderRow;
/*  68 */     this.iterationOrderColumn = iterationOrderColumn;
/*  69 */     ImmutableMap.Builder rowBuilder = ImmutableMap.builder();
/*  70 */     for (Map.Entry row : rows.entrySet()) {
/*  71 */       rowBuilder.put(row.getKey(), ImmutableMap.copyOf((Map)row.getValue()));
/*     */     }
/*  73 */     this.rowMap = rowBuilder.build();
/*     */ 
/*  75 */     ImmutableMap.Builder columnBuilder = ImmutableMap.builder();
/*  76 */     for (Map.Entry col : columns.entrySet()) {
/*  77 */       columnBuilder.put(col.getKey(), ImmutableMap.copyOf((Map)col.getValue()));
/*     */     }
/*  79 */     this.columnMap = columnBuilder.build();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<C, Map<R, V>> columnMap() {
/*  83 */     return this.columnMap;
/*     */   }
/*     */ 
/*     */   public ImmutableMap<R, Map<C, V>> rowMap() {
/*  87 */     return this.rowMap;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  92 */     return this.iterationOrderRow.length;
/*     */   }
/*     */ 
/*     */   Table.Cell<R, C, V> getCell(int index)
/*     */   {
/*  97 */     int rowIndex = this.iterationOrderRow[index];
/*  98 */     Map.Entry rowEntry = (Map.Entry)this.rowMap.entrySet().asList().get(rowIndex);
/*  99 */     ImmutableMap row = (ImmutableMap)rowEntry.getValue();
/* 100 */     int columnIndex = this.iterationOrderColumn[index];
/* 101 */     Map.Entry colEntry = (Map.Entry)row.entrySet().asList().get(columnIndex);
/* 102 */     return cellOf(rowEntry.getKey(), colEntry.getKey(), colEntry.getValue());
/*     */   }
/*     */ 
/*     */   V getValue(int index)
/*     */   {
/* 107 */     int rowIndex = this.iterationOrderRow[index];
/* 108 */     ImmutableMap row = (ImmutableMap)this.rowMap.values().asList().get(rowIndex);
/* 109 */     int columnIndex = this.iterationOrderColumn[index];
/* 110 */     return row.values().asList().get(columnIndex);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.SparseImmutableTable
 * JD-Core Version:    0.6.2
 */