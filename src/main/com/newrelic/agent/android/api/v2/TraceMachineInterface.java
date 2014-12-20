package com.newrelic.agent.android.api.v2;

public abstract interface TraceMachineInterface
{
  public abstract long getCurrentThreadId();

  public abstract String getCurrentThreadName();

  public abstract boolean isUIThread();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.api.v2.TraceMachineInterface
 * JD-Core Version:    0.6.2
 */