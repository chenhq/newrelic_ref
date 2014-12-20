package com.newrelic.org.slf4j.spi;

import com.newrelic.org.slf4j.ILoggerFactory;

public abstract interface LoggerFactoryBinder
{
  public abstract ILoggerFactory getLoggerFactory();

  public abstract String getLoggerFactoryClassStr();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.spi.LoggerFactoryBinder
 * JD-Core Version:    0.6.2
 */