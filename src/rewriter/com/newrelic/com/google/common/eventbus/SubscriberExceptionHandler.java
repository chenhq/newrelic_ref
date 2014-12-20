package com.newrelic.com.google.common.eventbus;

public abstract interface SubscriberExceptionHandler
{
  public abstract void handleException(Throwable paramThrowable, SubscriberExceptionContext paramSubscriberExceptionContext);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.SubscriberExceptionHandler
 * JD-Core Version:    0.6.2
 */