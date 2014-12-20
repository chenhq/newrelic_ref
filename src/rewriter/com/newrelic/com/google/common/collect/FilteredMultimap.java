package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import com.newrelic.com.google.common.base.Predicate;
import java.util.Map.Entry;

@GwtCompatible
abstract interface FilteredMultimap<K, V> extends Multimap<K, V>
{
  public abstract Multimap<K, V> unfiltered();

  public abstract Predicate<? super Map.Entry<K, V>> entryPredicate();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.FilteredMultimap
 * JD-Core Version:    0.6.2
 */