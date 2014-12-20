/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class AbstractTable<R, C, V>
/*     */   implements Table<R, C, V>
/*     */ {
/*     */   private transient Set<Table.Cell<R, C, V>> cellSet;
/*     */   private transient Collection<V> values;
/*     */ 
/*     */   public boolean containsRow(@Nullable Object rowKey)
/*     */   {
/*  38 */     return Maps.safeContainsKey(rowMap(), rowKey);
/*     */   }
/*     */ 
/*     */   public boolean containsColumn(@Nullable Object columnKey)
/*     */   {
/*  43 */     return Maps.safeContainsKey(columnMap(), columnKey);
/*     */   }
/*     */ 
/*     */   public Set<R> rowKeySet()
/*     */   {
/*  48 */     return rowMap().keySet();
/*     */   }
/*     */ 
/*     */   public Set<C> columnKeySet()
/*     */   {
/*  53 */     return columnMap().keySet();
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/*  58 */     for (Map row : rowMap().values()) {
/*  59 */       if (row.containsValue(value)) {
/*  60 */         return true;
/*     */       }
/*     */     }
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  68 */     Map row = (Map)Maps.safeGet(rowMap(), rowKey);
/*  69 */     return (row != null) && (Maps.safeContainsKey(row, columnKey));
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  74 */     Map row = (Map)Maps.safeGet(rowMap(), rowKey);
/*  75 */     return row == null ? null : Maps.safeGet(row, columnKey);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  80 */     return size() == 0;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  85 */     Iterators.clear(cellSet().iterator());
/*     */   }
/*     */ 
/*     */   public V remove(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  90 */     Map row = (Map)Maps.safeGet(rowMap(), rowKey);
/*  91 */     return row == null ? null : Maps.safeRemove(row, columnKey);
/*     */   }
/*     */ 
/*     */   public V put(R rowKey, C columnKey, V value)
/*     */   {
/*  96 */     return row(rowKey).put(columnKey, value);
/*     */   }
/*     */ 
/*     */   public void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 101 */     for (Table.Cell cell : table.cellSet())
/* 102 */       put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*     */   }
/*     */ 
/*     */   public Set<Table.Cell<R, C, V>> cellSet()
/*     */   {
/* 110 */     Set result = this.cellSet;
/* 111 */     return result == null ? (this.cellSet = createCellSet()) : result;
/*     */   }
/*     */ 
/*     */   Set<Table.Cell<R, C, V>> createCellSet() {
/* 115 */     return new CellSet();
/*     */   }
/*     */ 
/*     */   abstract Iterator<Table.Cell<R, C, V>> cellIterator();
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 163 */     Collection result = this.values;
/* 164 */     return result == null ? (this.values = createValues()) : result;
/*     */   }
/*     */ 
/*     */   Collection<V> createValues() {
/* 168 */     return new Values();
/*     */   }
/*     */ 
/*     */   Iterator<V> valuesIterator() {
/* 172 */     return new TransformedIterator(cellSet().iterator())
/*     */     {
/*     */       V transform(Table.Cell<R, C, V> cell) {
/* 175 */         return cell.getValue();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj)
/*     */   {
/* 203 */     return Tables.equalsImpl(this, obj);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 207 */     return cellSet().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 214 */     return rowMap().toString();
/*     */   }
/*     */ 
/*     */   class Values extends AbstractCollection<V>
/*     */   {
/*     */     Values()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Iterator<V> iterator()
/*     */     {
/* 183 */       return AbstractTable.this.valuesIterator();
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 188 */       return AbstractTable.this.containsValue(o);
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 193 */       AbstractTable.this.clear();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 198 */       return AbstractTable.this.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   class CellSet extends AbstractSet<Table.Cell<R, C, V>>
/*     */   {
/*     */     CellSet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 123 */       if ((o instanceof Table.Cell)) {
/* 124 */         Table.Cell cell = (Table.Cell)o;
/* 125 */         Map row = (Map)Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
/* 126 */         return (row != null) && (Collections2.safeContains(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue())));
/*     */       }
/*     */ 
/* 129 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(@Nullable Object o)
/*     */     {
/* 134 */       if ((o instanceof Table.Cell)) {
/* 135 */         Table.Cell cell = (Table.Cell)o;
/* 136 */         Map row = (Map)Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
/* 137 */         return (row != null) && (Collections2.safeRemove(row.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue())));
/*     */       }
/*     */ 
/* 140 */       return false;
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 145 */       AbstractTable.this.clear();
/*     */     }
/*     */ 
/*     */     public Iterator<Table.Cell<R, C, V>> iterator()
/*     */     {
/* 150 */       return AbstractTable.this.cellIterator();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 155 */       return AbstractTable.this.size();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractTable
 * JD-Core Version:    0.6.2
 */