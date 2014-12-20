/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Immutable
/*     */ final class DenseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V>
/*     */ {
/*     */   private final ImmutableMap<R, Integer> rowKeyToIndex;
/*     */   private final ImmutableMap<C, Integer> columnKeyToIndex;
/*     */   private final ImmutableMap<R, Map<C, V>> rowMap;
/*     */   private final ImmutableMap<C, Map<R, V>> columnMap;
/*     */   private final int[] rowCounts;
/*     */   private final int[] columnCounts;
/*     */   private final V[][] values;
/*     */   private final int[] iterationOrderRow;
/*     */   private final int[] iterationOrderColumn;
/*     */ 
/*     */   private static <E> ImmutableMap<E, Integer> makeIndex(ImmutableSet<E> set)
/*     */   {
/*  44 */     ImmutableMap.Builder indexBuilder = ImmutableMap.builder();
/*  45 */     int i = 0;
/*  46 */     for (Iterator i$ = set.iterator(); i$.hasNext(); ) { Object key = i$.next();
/*  47 */       indexBuilder.put(key, Integer.valueOf(i));
/*  48 */       i++;
/*     */     }
/*  50 */     return indexBuilder.build();
/*     */   }
/*     */ 
/*     */   DenseImmutableTable(ImmutableList<Table.Cell<R, C, V>> cellList, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace)
/*     */   {
/*  56 */     Object[][] array = (Object[][])new Object[rowSpace.size()][columnSpace.size()];
/*  57 */     this.values = array;
/*  58 */     this.rowKeyToIndex = makeIndex(rowSpace);
/*  59 */     this.columnKeyToIndex = makeIndex(columnSpace);
/*  60 */     this.rowCounts = new int[this.rowKeyToIndex.size()];
/*  61 */     this.columnCounts = new int[this.columnKeyToIndex.size()];
/*  62 */     int[] iterationOrderRow = new int[cellList.size()];
/*  63 */     int[] iterationOrderColumn = new int[cellList.size()];
/*  64 */     for (int i = 0; i < cellList.size(); i++) {
/*  65 */       Table.Cell cell = (Table.Cell)cellList.get(i);
/*  66 */       Object rowKey = cell.getRowKey();
/*  67 */       Object columnKey = cell.getColumnKey();
/*  68 */       int rowIndex = ((Integer)this.rowKeyToIndex.get(rowKey)).intValue();
/*  69 */       int columnIndex = ((Integer)this.columnKeyToIndex.get(columnKey)).intValue();
/*  70 */       Object existingValue = this.values[rowIndex][columnIndex];
/*  71 */       Preconditions.checkArgument(existingValue == null, "duplicate key: (%s, %s)", new Object[] { rowKey, columnKey });
/*  72 */       this.values[rowIndex][columnIndex] = cell.getValue();
/*  73 */       this.rowCounts[rowIndex] += 1;
/*  74 */       this.columnCounts[columnIndex] += 1;
/*  75 */       iterationOrderRow[i] = rowIndex;
/*  76 */       iterationOrderColumn[i] = columnIndex;
/*     */     }
/*  78 */     this.iterationOrderRow = iterationOrderRow;
/*  79 */     this.iterationOrderColumn = iterationOrderColumn;
/*  80 */     this.rowMap = new RowMap(null);
/*  81 */     this.columnMap = new ColumnMap(null);
/*     */   }
/*     */ 
/*     */   public ImmutableMap<C, Map<R, V>> columnMap()
/*     */   {
/* 243 */     return this.columnMap;
/*     */   }
/*     */ 
/*     */   public ImmutableMap<R, Map<C, V>> rowMap()
/*     */   {
/* 248 */     return this.rowMap;
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 253 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 254 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 255 */     return (rowIndex == null) || (columnIndex == null) ? null : this.values[rowIndex.intValue()][columnIndex.intValue()];
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 261 */     return this.iterationOrderRow.length;
/*     */   }
/*     */ 
/*     */   Table.Cell<R, C, V> getCell(int index)
/*     */   {
/* 266 */     int rowIndex = this.iterationOrderRow[index];
/* 267 */     int columnIndex = this.iterationOrderColumn[index];
/* 268 */     Object rowKey = rowKeySet().asList().get(rowIndex);
/* 269 */     Object columnKey = columnKeySet().asList().get(columnIndex);
/* 270 */     Object value = this.values[rowIndex][columnIndex];
/* 271 */     return cellOf(rowKey, columnKey, value);
/*     */   }
/*     */ 
/*     */   V getValue(int index)
/*     */   {
/* 276 */     return this.values[this.iterationOrderRow[index]][this.iterationOrderColumn[index]];
/*     */   }
/*     */ 
/*     */   private final class ColumnMap extends DenseImmutableTable.ImmutableArrayMap<C, Map<R, V>>
/*     */   {
/*     */     private ColumnMap()
/*     */     {
/* 223 */       super();
/*     */     }
/*     */ 
/*     */     ImmutableMap<C, Integer> keyToIndex()
/*     */     {
/* 228 */       return DenseImmutableTable.this.columnKeyToIndex;
/*     */     }
/*     */ 
/*     */     Map<R, V> getValue(int keyIndex)
/*     */     {
/* 233 */       return new DenseImmutableTable.Column(DenseImmutableTable.this, keyIndex);
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 238 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class RowMap extends DenseImmutableTable.ImmutableArrayMap<R, Map<C, V>>
/*     */   {
/*     */     private RowMap()
/*     */     {
/* 202 */       super();
/*     */     }
/*     */ 
/*     */     ImmutableMap<R, Integer> keyToIndex()
/*     */     {
/* 207 */       return DenseImmutableTable.this.rowKeyToIndex;
/*     */     }
/*     */ 
/*     */     Map<C, V> getValue(int keyIndex)
/*     */     {
/* 212 */       return new DenseImmutableTable.Row(DenseImmutableTable.this, keyIndex);
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 217 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class Column extends DenseImmutableTable.ImmutableArrayMap<R, V>
/*     */   {
/*     */     private final int columnIndex;
/*     */ 
/*     */     Column(int columnIndex)
/*     */     {
/* 180 */       super();
/* 181 */       this.columnIndex = columnIndex;
/*     */     }
/*     */ 
/*     */     ImmutableMap<R, Integer> keyToIndex()
/*     */     {
/* 186 */       return DenseImmutableTable.this.rowKeyToIndex;
/*     */     }
/*     */ 
/*     */     V getValue(int keyIndex)
/*     */     {
/* 191 */       return DenseImmutableTable.this.values[keyIndex][this.columnIndex];
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 196 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class Row extends DenseImmutableTable.ImmutableArrayMap<C, V>
/*     */   {
/*     */     private final int rowIndex;
/*     */ 
/*     */     Row(int rowIndex)
/*     */     {
/* 156 */       super();
/* 157 */       this.rowIndex = rowIndex;
/*     */     }
/*     */ 
/*     */     ImmutableMap<C, Integer> keyToIndex()
/*     */     {
/* 162 */       return DenseImmutableTable.this.columnKeyToIndex;
/*     */     }
/*     */ 
/*     */     V getValue(int keyIndex)
/*     */     {
/* 167 */       return DenseImmutableTable.this.values[this.rowIndex][keyIndex];
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 172 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class ImmutableArrayMap<K, V> extends ImmutableMap<K, V>
/*     */   {
/*     */     private final int size;
/*     */ 
/*     */     ImmutableArrayMap(int size)
/*     */     {
/*  91 */       this.size = size;
/*     */     }
/*     */ 
/*     */     abstract ImmutableMap<K, Integer> keyToIndex();
/*     */ 
/*     */     private boolean isFull()
/*     */     {
/*  98 */       return this.size == keyToIndex().size();
/*     */     }
/*     */ 
/*     */     K getKey(int index) {
/* 102 */       return keyToIndex().keySet().asList().get(index);
/*     */     }
/*     */     @Nullable
/*     */     abstract V getValue(int paramInt);
/*     */ 
/*     */     ImmutableSet<K> createKeySet() {
/* 109 */       return isFull() ? keyToIndex().keySet() : super.createKeySet();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 114 */       return this.size;
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object key)
/*     */     {
/* 119 */       Integer keyIndex = (Integer)keyToIndex().get(key);
/* 120 */       return keyIndex == null ? null : getValue(keyIndex.intValue());
/*     */     }
/*     */ 
/*     */     ImmutableSet<Map.Entry<K, V>> createEntrySet()
/*     */     {
/* 125 */       return new ImmutableMapEntrySet() {
/*     */         ImmutableMap<K, V> map() {
/* 127 */           return DenseImmutableTable.ImmutableArrayMap.this;
/*     */         }
/*     */ 
/*     */         public UnmodifiableIterator<Map.Entry<K, V>> iterator()
/*     */         {
/* 132 */           return new AbstractIterator() {
/* 133 */             private int index = -1;
/* 134 */             private final int maxIndex = DenseImmutableTable.ImmutableArrayMap.this.keyToIndex().size();
/*     */ 
/*     */             protected Map.Entry<K, V> computeNext()
/*     */             {
/* 138 */               for (this.index += 1; this.index < this.maxIndex; this.index += 1) {
/* 139 */                 Object value = DenseImmutableTable.ImmutableArrayMap.this.getValue(this.index);
/* 140 */                 if (value != null) {
/* 141 */                   return Maps.immutableEntry(DenseImmutableTable.ImmutableArrayMap.this.getKey(this.index), value);
/*     */                 }
/*     */               }
/* 144 */               return (Map.Entry)endOfData();
/*     */             }
/*     */           };
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.DenseImmutableTable
 * JD-Core Version:    0.6.2
 */