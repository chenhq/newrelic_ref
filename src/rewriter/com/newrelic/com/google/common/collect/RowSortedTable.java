package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.Beta;
import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
@Beta
public abstract interface RowSortedTable<R, C, V> extends Table<R, C, V>
{
  public abstract SortedSet<R> rowKeySet();

  public abstract SortedMap<R, Map<C, V>> rowMap();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.RowSortedTable
 * JD-Core Version:    0.6.2
 */