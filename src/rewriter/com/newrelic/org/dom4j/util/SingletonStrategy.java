package com.newrelic.org.dom4j.util;

public abstract interface SingletonStrategy
{
  public abstract Object instance();

  public abstract void reset();

  public abstract void setSingletonClassName(String paramString);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.util.SingletonStrategy
 * JD-Core Version:    0.6.2
 */