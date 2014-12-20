package com.newrelic.agent.android.instrumentation;

import java.lang.annotation.Annotation;

public @interface ReplaceCallSite
{
  public abstract boolean isStatic();

  public abstract String scope();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.ReplaceCallSite
 * JD-Core Version:    0.6.2
 */