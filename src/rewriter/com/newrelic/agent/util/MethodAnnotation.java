package com.newrelic.agent.util;

import java.util.Map;

public abstract interface MethodAnnotation
{
  public abstract String getMethodName();

  public abstract String getMethodDesc();

  public abstract String getClassName();

  public abstract String getName();

  public abstract Map<String, Object> getAttributes();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.util.MethodAnnotation
 * JD-Core Version:    0.6.2
 */