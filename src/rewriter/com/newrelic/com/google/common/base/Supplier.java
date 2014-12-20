package com.newrelic.com.google.common.base;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract interface Supplier<T>
{
  public abstract T get();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Supplier
 * JD-Core Version:    0.6.2
 */