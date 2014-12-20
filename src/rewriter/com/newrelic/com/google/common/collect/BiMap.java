package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface BiMap<K, V> extends Map<K, V>
{
  public abstract V put(@Nullable K paramK, @Nullable V paramV);

  public abstract V forcePut(@Nullable K paramK, @Nullable V paramV);

  public abstract void putAll(Map<? extends K, ? extends V> paramMap);

  public abstract Set<V> values();

  public abstract BiMap<V, K> inverse();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.BiMap
 * JD-Core Version:    0.6.2
 */