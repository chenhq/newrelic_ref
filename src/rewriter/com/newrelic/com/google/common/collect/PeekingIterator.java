package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract interface PeekingIterator<E> extends Iterator<E>
{
  public abstract E peek();

  public abstract E next();

  public abstract void remove();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.PeekingIterator
 * JD-Core Version:    0.6.2
 */