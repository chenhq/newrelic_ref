package com.newrelic.javassist.util.proxy;

import java.lang.reflect.Method;

public abstract interface MethodFilter
{
  public abstract boolean isHandled(Method paramMethod);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.MethodFilter
 * JD-Core Version:    0.6.2
 */