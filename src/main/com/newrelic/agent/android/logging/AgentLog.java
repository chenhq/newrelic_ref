package com.newrelic.agent.android.logging;

public abstract interface AgentLog
{
  public static final int DEBUG = 5;
  public static final int VERBOSE = 4;
  public static final int INFO = 3;
  public static final int WARNING = 2;
  public static final int ERROR = 1;

  public abstract void debug(String paramString);

  public abstract void verbose(String paramString);

  public abstract void info(String paramString);

  public abstract void warning(String paramString);

  public abstract void error(String paramString);

  public abstract void error(String paramString, Throwable paramThrowable);

  public abstract int getLevel();

  public abstract void setLevel(int paramInt);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.logging.AgentLog
 * JD-Core Version:    0.6.2
 */