package com.newrelic.com.google.common.util.concurrent;

import com.newrelic.com.google.common.annotations.Beta;

@Beta
public abstract interface FutureFallback<V>
{
  public abstract ListenableFuture<V> create(Throwable paramThrowable)
    throws Exception;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.FutureFallback
 * JD-Core Version:    0.6.2
 */