package com.newrelic.agent.android.instrumentation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
public @interface WrapReturn
{
  public abstract String className();

  public abstract String methodName();

  public abstract String methodDesc();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.WrapReturn
 * JD-Core Version:    0.6.2
 */