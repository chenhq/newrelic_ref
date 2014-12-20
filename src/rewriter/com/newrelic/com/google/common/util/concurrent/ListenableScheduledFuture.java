package com.newrelic.com.google.common.util.concurrent;

import com.newrelic.com.google.common.annotations.Beta;
import java.util.concurrent.ScheduledFuture;

@Beta
public abstract interface ListenableScheduledFuture<V> extends ScheduledFuture<V>, ListenableFuture<V>
{
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.ListenableScheduledFuture
 * JD-Core Version:    0.6.2
 */