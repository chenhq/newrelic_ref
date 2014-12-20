package com.newrelic.com.google.common.eventbus;

import com.newrelic.com.google.common.collect.Multimap;

abstract interface SubscriberFindingStrategy
{
  public abstract Multimap<Class<?>, EventSubscriber> findAllSubscribers(Object paramObject);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.SubscriberFindingStrategy
 * JD-Core Version:    0.6.2
 */