package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;

@GwtCompatible
abstract interface SortedIterable<T> extends Iterable<T>
{
  public abstract Comparator<? super T> comparator();

  public abstract Iterator<T> iterator();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.SortedIterable
 * JD-Core Version:    0.6.2
 */