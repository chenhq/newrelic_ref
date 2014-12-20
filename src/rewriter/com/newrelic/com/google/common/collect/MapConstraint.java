package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.Beta;
import com.newrelic.com.google.common.annotations.GwtCompatible;
import javax.annotation.Nullable;

@GwtCompatible
@Beta
public abstract interface MapConstraint<K, V>
{
  public abstract void checkKeyValue(@Nullable K paramK, @Nullable V paramV);

  public abstract String toString();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.MapConstraint
 * JD-Core Version:    0.6.2
 */