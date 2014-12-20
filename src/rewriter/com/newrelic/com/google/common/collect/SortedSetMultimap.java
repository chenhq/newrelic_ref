package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface SortedSetMultimap<K, V> extends SetMultimap<K, V>
{
  public abstract SortedSet<V> get(@Nullable K paramK);

  public abstract SortedSet<V> removeAll(@Nullable Object paramObject);

  public abstract SortedSet<V> replaceValues(K paramK, Iterable<? extends V> paramIterable);

  public abstract Map<K, Collection<V>> asMap();

  public abstract Comparator<? super V> valueComparator();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.SortedSetMultimap
 * JD-Core Version:    0.6.2
 */