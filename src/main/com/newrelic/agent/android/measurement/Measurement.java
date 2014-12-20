package com.newrelic.agent.android.measurement;

public abstract interface Measurement
{
  public abstract MeasurementType getType();

  public abstract String getName();

  public abstract String getScope();

  public abstract long getStartTime();

  public abstract double getStartTimeInSeconds();

  public abstract long getEndTime();

  public abstract double getEndTimeInSeconds();

  public abstract long getExclusiveTime();

  public abstract double getExclusiveTimeInSeconds();

  public abstract ThreadInfo getThreadInfo();

  public abstract boolean isInstantaneous();

  public abstract void finish();

  public abstract boolean isFinished();

  public abstract double asDouble();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.Measurement
 * JD-Core Version:    0.6.2
 */