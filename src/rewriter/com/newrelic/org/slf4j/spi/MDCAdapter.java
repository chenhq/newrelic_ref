package com.newrelic.org.slf4j.spi;

import java.util.Map;

public abstract interface MDCAdapter
{
  public abstract void put(String paramString1, String paramString2);

  public abstract String get(String paramString);

  public abstract void remove(String paramString);

  public abstract void clear();

  public abstract Map getCopyOfContextMap();

  public abstract void setContextMap(Map paramMap);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.spi.MDCAdapter
 * JD-Core Version:    0.6.2
 */