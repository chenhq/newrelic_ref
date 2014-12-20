package com.newrelic.com.google.common.collect;

import java.util.SortedSet;

abstract interface SortedMultisetBridge<E> extends Multiset<E>
{
  public abstract SortedSet<E> elementSet();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.SortedMultisetBridge
 * JD-Core Version:    0.6.2
 */