package com.newrelic.javassist.util.proxy;

import java.lang.reflect.Method;

public abstract interface MethodHandler
{
  public abstract Object invoke(Object paramObject, Method paramMethod1, Method paramMethod2, Object[] paramArrayOfObject)
    throws Throwable;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.MethodHandler
 * JD-Core Version:    0.6.2
 */