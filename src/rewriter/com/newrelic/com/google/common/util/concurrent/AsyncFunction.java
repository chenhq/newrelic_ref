package com.newrelic.com.google.common.util.concurrent;

public abstract interface AsyncFunction<I, O>
{
  public abstract ListenableFuture<O> apply(I paramI)
    throws Exception;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.AsyncFunction
 * JD-Core Version:    0.6.2
 */