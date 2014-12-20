package com.newrelic.com.google.common.base;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface Predicate<T>
{
  public abstract boolean apply(@Nullable T paramT);

  public abstract boolean equals(@Nullable Object paramObject);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Predicate
 * JD-Core Version:    0.6.2
 */