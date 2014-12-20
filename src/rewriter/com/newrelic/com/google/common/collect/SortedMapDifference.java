package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.SortedMap;

@GwtCompatible
public abstract interface SortedMapDifference<K, V> extends MapDifference<K, V>
{
  public abstract SortedMap<K, V> entriesOnlyOnLeft();

  public abstract SortedMap<K, V> entriesOnlyOnRight();

  public abstract SortedMap<K, V> entriesInCommon();

  public abstract SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.SortedMapDifference
 * JD-Core Version:    0.6.2
 */