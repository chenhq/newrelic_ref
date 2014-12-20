package com.newrelic.agent.android.instrumentation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
public @interface Trace
{
  public static final String NULL = "";

  public abstract String metricName();

  public abstract boolean skipTransactionTrace();

  public abstract MetricCategory category();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.Trace
 * JD-Core Version:    0.6.2
 */