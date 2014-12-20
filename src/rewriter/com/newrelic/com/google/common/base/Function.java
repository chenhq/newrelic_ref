package com.newrelic.com.google.common.base;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface Function<F, T>
{
  @Nullable
  public abstract T apply(@Nullable F paramF);

  public abstract boolean equals(@Nullable Object paramObject);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Function
 * JD-Core Version:    0.6.2
 */