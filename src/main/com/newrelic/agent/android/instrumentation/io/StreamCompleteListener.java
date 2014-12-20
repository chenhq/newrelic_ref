package com.newrelic.agent.android.instrumentation.io;

public abstract interface StreamCompleteListener
{
  public abstract void streamComplete(StreamCompleteEvent paramStreamCompleteEvent);

  public abstract void streamError(StreamCompleteEvent paramStreamCompleteEvent);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.io.StreamCompleteListener
 * JD-Core Version:    0.6.2
 */