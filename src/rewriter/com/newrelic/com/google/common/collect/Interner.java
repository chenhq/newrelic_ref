package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.Beta;

@Beta
public abstract interface Interner<E>
{
  public abstract E intern(E paramE);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Interner
 * JD-Core Version:    0.6.2
 */