package com.newrelic.com.google.common.cache;

import com.newrelic.com.google.common.annotations.Beta;
import com.newrelic.com.google.common.annotations.GwtCompatible;

@Beta
@GwtCompatible
public abstract interface RemovalListener<K, V>
{
  public abstract void onRemoval(RemovalNotification<K, V> paramRemovalNotification);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.RemovalListener
 * JD-Core Version:    0.6.2
 */