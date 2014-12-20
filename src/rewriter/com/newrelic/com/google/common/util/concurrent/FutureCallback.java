package com.newrelic.com.google.common.util.concurrent;

import javax.annotation.Nullable;

public abstract interface FutureCallback<V>
{
  public abstract void onSuccess(@Nullable V paramV);

  public abstract void onFailure(Throwable paramThrowable);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.FutureCallback
 * JD-Core Version:    0.6.2
 */