package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract interface Constraint<E>
{
  public abstract E checkElement(E paramE);

  public abstract String toString();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Constraint
 * JD-Core Version:    0.6.2
 */