package com.newrelic.javassist.util.proxy;

public abstract interface ProxyObject
{
  public abstract void setHandler(MethodHandler paramMethodHandler);

  public abstract MethodHandler getHandler();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.ProxyObject
 * JD-Core Version:    0.6.2
 */