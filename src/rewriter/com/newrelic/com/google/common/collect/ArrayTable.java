/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*     */ import com.newrelic.com.google.common.base.Objects;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class ArrayTable<R, C, V> extends AbstractTable<R, C, V>
/*     */   implements Serializable
/*     */ {
/*     */   private final ImmutableList<R> rowList;
/*     */   private final ImmutableList<C> columnList;
/*     */   private final ImmutableMap<R, Integer> rowKeyToIndex;
/*     */   private final ImmutableMap<C, Integer> columnKeyToIndex;
/*     */   private final V[][] array;
/*     */   private transient ArrayTable<R, C, V>.ColumnMap columnMap;
/*     */   private transient ArrayTable<R, C, V>.RowMap rowMap;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <R, C, V> ArrayTable<R, C, V> create(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys)
/*     */   {
/*  99 */     return new ArrayTable(rowKeys, columnKeys);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> ArrayTable<R, C, V> create(Table<R, C, V> table)
/*     */   {
/* 131 */     return (table instanceof ArrayTable) ? new ArrayTable((ArrayTable)table) : new ArrayTable(table);
/*     */   }
/*     */ 
/*     */   private ArrayTable(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys)
/*     */   {
/* 146 */     this.rowList = ImmutableList.copyOf(rowKeys);
/* 147 */     this.columnList = ImmutableList.copyOf(columnKeys);
/* 148 */     Preconditions.checkArgument(!this.rowList.isEmpty());
/* 149 */     Preconditions.checkArgument(!this.columnList.isEmpty());
/*     */ 
/* 156 */     this.rowKeyToIndex = index(this.rowList);
/* 157 */     this.columnKeyToIndex = index(this.columnList);
/*     */ 
/* 160 */     Object[][] tmpArray = (Object[][])new Object[this.rowList.size()][this.columnList.size()];
/*     */ 
/* 162 */     this.array = tmpArray;
/*     */ 
/* 164 */     eraseAll();
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableMap<E, Integer> index(List<E> list) {
/* 168 */     ImmutableMap.Builder columnBuilder = ImmutableMap.builder();
/* 169 */     for (int i = 0; i < list.size(); i++) {
/* 170 */       columnBuilder.put(list.get(i), Integer.valueOf(i));
/*     */     }
/* 172 */     return columnBuilder.build();
/*     */   }
/*     */ 
/*     */   private ArrayTable(Table<R, C, V> table) {
/* 176 */     this(table.rowKeySet(), table.columnKeySet());
/* 177 */     putAll(table);
/*     */   }
/*     */ 
/*     */   private ArrayTable(ArrayTable<R, C, V> table) {
/* 181 */     this.rowList = table.rowList;
/* 182 */     this.columnList = table.columnList;
/* 183 */     this.rowKeyToIndex = table.rowKeyToIndex;
/* 184 */     this.columnKeyToIndex = table.columnKeyToIndex;
/*     */ 
/* 186 */     Object[][] copy = (Object[][])new Object[this.rowList.size()][this.columnList.size()];
/* 187 */     this.array = copy;
/*     */ 
/* 189 */     eraseAll();
/* 190 */     for (int i = 0; i < this.rowList.size(); i++)
/* 191 */       System.arraycopy(table.array[i], 0, copy[i], 0, table.array[i].length);
/*     */   }
/*     */ 
/*     */   public ImmutableList<R> rowKeyList()
/*     */   {
/* 305 */     return this.rowList;
/*     */   }
/*     */ 
/*     */   public ImmutableList<C> columnKeyList()
/*     */   {
/* 313 */     return this.columnList;
/*     */   }
/*     */ 
/*     */   public V at(int rowIndex, int columnIndex)
/*     */   {
/* 332 */     Preconditions.checkElementIndex(rowIndex, this.rowList.size());
/* 333 */     Preconditions.checkElementIndex(columnIndex, this.columnList.size());
/* 334 */     return this.array[rowIndex][columnIndex];
/*     */   }
/*     */ 
/*     */   public V set(int rowIndex, int columnIndex, @Nullable V value)
/*     */   {
/* 354 */     Preconditions.checkElementIndex(rowIndex, this.rowList.size());
/* 355 */     Preconditions.checkElementIndex(columnIndex, this.columnList.size());
/* 356 */     Object oldValue = this.array[rowIndex][columnIndex];
/* 357 */     this.array[rowIndex][columnIndex] = value;
/* 358 */     return oldValue;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("reflection")
/*     */   public V[][] toArray(Class<V> valueClass)
/*     */   {
/* 375 */     Object[][] copy = (Object[][])Array.newInstance(valueClass, new int[] { this.rowList.size(), this.columnList.size() });
/*     */ 
/* 377 */     for (int i = 0; i < this.rowList.size(); i++) {
/* 378 */       System.arraycopy(this.array[i], 0, copy[i], 0, this.array[i].length);
/*     */     }
/* 380 */     return copy;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void clear()
/*     */   {
/* 391 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void eraseAll()
/*     */   {
/* 399 */     for (Object[] row : this.array)
/* 400 */       Arrays.fill(row, null);
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 410 */     return (containsRow(rowKey)) && (containsColumn(columnKey));
/*     */   }
/*     */ 
/*     */   public boolean containsColumn(@Nullable Object columnKey)
/*     */   {
/* 419 */     return this.columnKeyToIndex.containsKey(columnKey);
/*     */   }
/*     */ 
/*     */   public boolean containsRow(@Nullable Object rowKey)
/*     */   {
/* 428 */     return this.rowKeyToIndex.containsKey(rowKey);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 433 */     for (Object[] row : this.array) {
/* 434 */       for (Object element : row) {
/* 435 */         if (Objects.equal(value, element)) {
/* 436 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 440 */     return false;
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 445 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 446 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 447 */     return (rowIndex == null) || (columnIndex == null) ? null : at(rowIndex.intValue(), columnIndex.intValue());
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 456 */     return false;
/*     */   }
/*     */ 
/*     */   public V put(R rowKey, C columnKey, @Nullable V value)
/*     */   {
/* 467 */     Preconditions.checkNotNull(rowKey);
/* 468 */     Preconditions.checkNotNull(columnKey);
/* 469 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 470 */     Preconditions.checkArgument(rowIndex != null, "Row %s not in %s", new Object[] { rowKey, this.rowList });
/* 471 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 472 */     Preconditions.checkArgument(columnIndex != null, "Column %s not in %s", new Object[] { columnKey, this.columnList });
/*     */ 
/* 474 */     return set(rowIndex.intValue(), columnIndex.intValue(), value);
/*     */   }
/*     */ 
/*     */   public void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 495 */     super.putAll(table);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public V remove(Object rowKey, Object columnKey)
/*     */   {
/* 506 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public V erase(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 523 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 524 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 525 */     if ((rowIndex == null) || (columnIndex == null)) {
/* 526 */       return null;
/*     */     }
/* 528 */     return set(rowIndex.intValue(), columnIndex.intValue(), null);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 535 */     return this.rowList.size() * this.columnList.size();
/*     */   }
/*     */ 
/*     */   public Set<Table.Cell<R, C, V>> cellSet()
/*     */   {
/* 553 */     return super.cellSet();
/*     */   }
/*     */ 
/*     */   Iterator<Table.Cell<R, C, V>> cellIterator()
/*     */   {
/* 558 */     return new AbstractIndexedListIterator(size()) {
/*     */       protected Table.Cell<R, C, V> get(final int index) {
/* 560 */         return new Tables.AbstractCell() {
/* 561 */           final int rowIndex = index / ArrayTable.this.columnList.size();
/* 562 */           final int columnIndex = index % ArrayTable.this.columnList.size();
/*     */ 
/*     */           public R getRowKey() {
/* 565 */             return ArrayTable.this.rowList.get(this.rowIndex);
/*     */           }
/*     */ 
/*     */           public C getColumnKey() {
/* 569 */             return ArrayTable.this.columnList.get(this.columnIndex);
/*     */           }
/*     */ 
/*     */           public V getValue() {
/* 573 */             return ArrayTable.this.at(this.rowIndex, this.columnIndex);
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Map<R, V> column(C columnKey)
/*     */   {
/* 594 */     Preconditions.checkNotNull(columnKey);
/* 595 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 596 */     return columnIndex == null ? ImmutableMap.of() : new Column(columnIndex.intValue());
/*     */   }
/*     */ 
/*     */   public ImmutableSet<C> columnKeySet()
/*     */   {
/* 632 */     return this.columnKeyToIndex.keySet();
/*     */   }
/*     */ 
/*     */   public Map<C, Map<R, V>> columnMap()
/*     */   {
/* 639 */     ColumnMap map = this.columnMap;
/* 640 */     return map == null ? (this.columnMap = new ColumnMap(null)) : map;
/*     */   }
/*     */ 
/*     */   public Map<C, V> row(R rowKey)
/*     */   {
/* 684 */     Preconditions.checkNotNull(rowKey);
/* 685 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 686 */     return rowIndex == null ? ImmutableMap.of() : new Row(rowIndex.intValue());
/*     */   }
/*     */ 
/*     */   public ImmutableSet<R> rowKeySet()
/*     */   {
/* 721 */     return this.rowKeyToIndex.keySet();
/*     */   }
/*     */ 
/*     */   public Map<R, Map<C, V>> rowMap()
/*     */   {
/* 728 */     RowMap map = this.rowMap;
/* 729 */     return map == null ? (this.rowMap = new RowMap(null)) : map;
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 769 */     return super.values();
/*     */   }
/*     */ 
/*     */   private class RowMap extends ArrayTable.ArrayMap<R, Map<C, V>>
/*     */   {
/*     */     private RowMap()
/*     */     {
/* 734 */       super(null);
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 739 */       return "Row";
/*     */     }
/*     */ 
/*     */     Map<C, V> getValue(int index)
/*     */     {
/* 744 */       return new ArrayTable.Row(ArrayTable.this, index);
/*     */     }
/*     */ 
/*     */     Map<C, V> setValue(int index, Map<C, V> newValue)
/*     */     {
/* 749 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Map<C, V> put(R key, Map<C, V> value)
/*     */     {
/* 754 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Row extends ArrayTable.ArrayMap<C, V>
/*     */   {
/*     */     final int rowIndex;
/*     */ 
/*     */     Row(int rowIndex)
/*     */     {
/* 693 */       super(null);
/* 694 */       this.rowIndex = rowIndex;
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 699 */       return "Column";
/*     */     }
/*     */ 
/*     */     V getValue(int index)
/*     */     {
/* 704 */       return ArrayTable.this.at(this.rowIndex, index);
/*     */     }
/*     */ 
/*     */     V setValue(int index, V newValue)
/*     */     {
/* 709 */       return ArrayTable.this.set(this.rowIndex, index, newValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ColumnMap extends ArrayTable.ArrayMap<C, Map<R, V>>
/*     */   {
/*     */     private ColumnMap()
/*     */     {
/* 645 */       super(null);
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 650 */       return "Column";
/*     */     }
/*     */ 
/*     */     Map<R, V> getValue(int index)
/*     */     {
/* 655 */       return new ArrayTable.Column(ArrayTable.this, index);
/*     */     }
/*     */ 
/*     */     Map<R, V> setValue(int index, Map<R, V> newValue)
/*     */     {
/* 660 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Map<R, V> put(C key, Map<R, V> value)
/*     */     {
/* 665 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Column extends ArrayTable.ArrayMap<R, V>
/*     */   {
/*     */     final int columnIndex;
/*     */ 
/*     */     Column(int columnIndex)
/*     */     {
/* 604 */       super(null);
/* 605 */       this.columnIndex = columnIndex;
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 610 */       return "Row";
/*     */     }
/*     */ 
/*     */     V getValue(int index)
/*     */     {
/* 615 */       return ArrayTable.this.at(index, this.columnIndex);
/*     */     }
/*     */ 
/*     */     V setValue(int index, V newValue)
/*     */     {
/* 620 */       return ArrayTable.this.set(index, this.columnIndex, newValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class ArrayMap<K, V> extends Maps.ImprovedAbstractMap<K, V>
/*     */   {
/*     */     private final ImmutableMap<K, Integer> keyIndex;
/*     */ 
/*     */     private ArrayMap(ImmutableMap<K, Integer> keyIndex)
/*     */     {
/* 199 */       this.keyIndex = keyIndex;
/*     */     }
/*     */ 
/*     */     public Set<K> keySet()
/*     */     {
/* 204 */       return this.keyIndex.keySet();
/*     */     }
/*     */ 
/*     */     K getKey(int index) {
/* 208 */       return this.keyIndex.keySet().asList().get(index); } 
/*     */     abstract String getKeyRole();
/*     */ 
/*     */     @Nullable
/*     */     abstract V getValue(int paramInt);
/*     */ 
/*     */     @Nullable
/*     */     abstract V setValue(int paramInt, V paramV);
/*     */ 
/* 219 */     public int size() { return this.keyIndex.size(); }
/*     */ 
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 224 */       return this.keyIndex.isEmpty();
/*     */     }
/*     */ 
/*     */     protected Set<Map.Entry<K, V>> createEntrySet()
/*     */     {
/* 229 */       return new Maps.EntrySet()
/*     */       {
/*     */         Map<K, V> map() {
/* 232 */           return ArrayTable.ArrayMap.this;
/*     */         }
/*     */ 
/*     */         public Iterator<Map.Entry<K, V>> iterator()
/*     */         {
/* 237 */           return new AbstractIndexedListIterator(size())
/*     */           {
/*     */             protected Map.Entry<K, V> get(final int index) {
/* 240 */               return new AbstractMapEntry()
/*     */               {
/*     */                 public K getKey() {
/* 243 */                   return ArrayTable.ArrayMap.this.getKey(index);
/*     */                 }
/*     */ 
/*     */                 public V getValue()
/*     */                 {
/* 248 */                   return ArrayTable.ArrayMap.this.getValue(index);
/*     */                 }
/*     */ 
/*     */                 public V setValue(V value)
/*     */                 {
/* 253 */                   return ArrayTable.ArrayMap.this.setValue(index, value);
/*     */                 }
/*     */               };
/*     */             }
/*     */           };
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public boolean containsKey(@Nullable Object key)
/*     */     {
/* 266 */       return this.keyIndex.containsKey(key);
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object key)
/*     */     {
/* 271 */       Integer index = (Integer)this.keyIndex.get(key);
/* 272 */       if (index == null) {
/* 273 */         return null;
/*     */       }
/* 275 */       return getValue(index.intValue());
/*     */     }
/*     */ 
/*     */     public V put(K key, V value)
/*     */     {
/* 281 */       Integer index = (Integer)this.keyIndex.get(key);
/* 282 */       if (index == null) {
/* 283 */         throw new IllegalArgumentException(getKeyRole() + " " + key + " not in " + this.keyIndex.keySet());
/*     */       }
/*     */ 
/* 286 */       return setValue(index.intValue(), value);
/*     */     }
/*     */ 
/*     */     public V remove(Object key)
/*     */     {
/* 291 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 296 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ArrayTable
 * JD-Core Version:    0.6.2
 */