package com.newrelic.agent.android.tracing;

public abstract interface TraceLifecycleAware
{
  public abstract void onEnterMethod();

  public abstract void onExitMethod();

  public abstract void onTraceStart(ActivityTrace paramActivityTrace);

  public abstract void onTraceComplete(ActivityTrace paramActivityTrace);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.tracing.TraceLifecycleAware
 * JD-Core Version:    0.6.2
 */