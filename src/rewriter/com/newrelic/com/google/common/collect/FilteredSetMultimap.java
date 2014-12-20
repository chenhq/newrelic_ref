package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract interface FilteredSetMultimap<K, V> extends FilteredMultimap<K, V>, SetMultimap<K, V>
{
  public abstract SetMultimap<K, V> unfiltered();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.FilteredSetMultimap
 * JD-Core Version:    0.6.2
 */