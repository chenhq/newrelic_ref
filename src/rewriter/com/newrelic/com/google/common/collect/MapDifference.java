package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface MapDifference<K, V>
{
  public abstract boolean areEqual();

  public abstract Map<K, V> entriesOnlyOnLeft();

  public abstract Map<K, V> entriesOnlyOnRight();

  public abstract Map<K, V> entriesInCommon();

  public abstract Map<K, ValueDifference<V>> entriesDiffering();

  public abstract boolean equals(@Nullable Object paramObject);

  public abstract int hashCode();

  public static abstract interface ValueDifference<V>
  {
    public abstract V leftValue();

    public abstract V rightValue();

    public abstract boolean equals(@Nullable Object paramObject);

    public abstract int hashCode();
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.MapDifference
 * JD-Core Version:    0.6.2
 */