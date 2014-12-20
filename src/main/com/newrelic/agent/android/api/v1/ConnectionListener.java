package com.newrelic.agent.android.api.v1;

public abstract interface ConnectionListener
{
  public abstract void connected(ConnectionEvent paramConnectionEvent);

  public abstract void disconnected(ConnectionEvent paramConnectionEvent);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.api.v1.ConnectionListener
 * JD-Core Version:    0.6.2
 */