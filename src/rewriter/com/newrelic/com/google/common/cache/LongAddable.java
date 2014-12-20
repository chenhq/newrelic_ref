package com.newrelic.com.google.common.cache;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract interface LongAddable
{
  public abstract void increment();

  public abstract void add(long paramLong);

  public abstract long sum();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.LongAddable
 * JD-Core Version:    0.6.2
 */