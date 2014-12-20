package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface SetMultimap<K, V> extends Multimap<K, V>
{
  public abstract Set<V> get(@Nullable K paramK);

  public abstract Set<V> removeAll(@Nullable Object paramObject);

  public abstract Set<V> replaceValues(K paramK, Iterable<? extends V> paramIterable);

  public abstract Set<Map.Entry<K, V>> entries();

  public abstract Map<K, Collection<V>> asMap();

  public abstract boolean equals(@Nullable Object paramObject);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.SetMultimap
 * JD-Core Version:    0.6.2
 */