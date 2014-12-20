package com.newrelic.agent.android.activity;

import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementPool;
import com.newrelic.agent.android.measurement.ThreadInfo;

public abstract interface MeasuredActivity
{
  public abstract String getName();

  public abstract String getMetricName();

  public abstract void setName(String paramString);

  public abstract String getBackgroundMetricName();

  public abstract long getStartTime();

  public abstract long getEndTime();

  public abstract ThreadInfo getStartingThread();

  public abstract ThreadInfo getEndingThread();

  public abstract boolean isAutoInstrumented();

  public abstract Measurement getStartingMeasurement();

  public abstract Measurement getEndingMeasurement();

  public abstract MeasurementPool getMeasurementPool();

  public abstract void finish();

  public abstract boolean isFinished();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.activity.MeasuredActivity
 * JD-Core Version:    0.6.2
 */