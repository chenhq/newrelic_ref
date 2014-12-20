package com.newrelic.com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public abstract interface ListenableFuture<V> extends Future<V>
{
  public abstract void addListener(Runnable paramRunnable, Executor paramExecutor);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.ListenableFuture
 * JD-Core Version:    0.6.2
 */