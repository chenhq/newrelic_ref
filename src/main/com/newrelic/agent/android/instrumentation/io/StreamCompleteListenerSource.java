package com.newrelic.agent.android.instrumentation.io;

public abstract interface StreamCompleteListenerSource
{
  public abstract void addStreamCompleteListener(StreamCompleteListener paramStreamCompleteListener);

  public abstract void removeStreamCompleteListener(StreamCompleteListener paramStreamCompleteListener);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.io.StreamCompleteListenerSource
 * JD-Core Version:    0.6.2
 */