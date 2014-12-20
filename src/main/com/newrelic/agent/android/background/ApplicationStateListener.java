package com.newrelic.agent.android.background;

public abstract interface ApplicationStateListener
{
  public abstract void applicationForegrounded(ApplicationStateEvent paramApplicationStateEvent);

  public abstract void applicationBackgrounded(ApplicationStateEvent paramApplicationStateEvent);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.background.ApplicationStateListener
 * JD-Core Version:    0.6.2
 */