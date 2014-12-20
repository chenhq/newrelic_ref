package com.newrelic.com.google.common.cache;

import com.newrelic.com.google.common.annotations.Beta;
import com.newrelic.com.google.common.annotations.GwtCompatible;
import com.newrelic.com.google.common.base.Function;
import com.newrelic.com.google.common.collect.ImmutableMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public abstract interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V>
{
  public abstract V get(K paramK)
    throws ExecutionException;

  public abstract V getUnchecked(K paramK);

  public abstract ImmutableMap<K, V> getAll(Iterable<? extends K> paramIterable)
    throws ExecutionException;

  @Deprecated
  public abstract V apply(K paramK);

  public abstract void refresh(K paramK);

  public abstract ConcurrentMap<K, V> asMap();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.LoadingCache
 * JD-Core Version:    0.6.2
 */