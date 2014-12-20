package com.newrelic.com.google.common.hash;

import com.newrelic.com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public abstract interface Funnel<T> extends Serializable
{
  public abstract void funnel(T paramT, PrimitiveSink paramPrimitiveSink);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.Funnel
 * JD-Core Version:    0.6.2
 */