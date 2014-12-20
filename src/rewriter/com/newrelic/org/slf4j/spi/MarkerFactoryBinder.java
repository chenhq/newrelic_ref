package com.newrelic.org.slf4j.spi;

import com.newrelic.org.slf4j.IMarkerFactory;

public abstract interface MarkerFactoryBinder
{
  public abstract IMarkerFactory getMarkerFactory();

  public abstract String getMarkerFactoryClassStr();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.spi.MarkerFactoryBinder
 * JD-Core Version:    0.6.2
 */