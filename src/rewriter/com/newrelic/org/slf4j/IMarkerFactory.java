package com.newrelic.org.slf4j;

public abstract interface IMarkerFactory
{
  public abstract Marker getMarker(String paramString);

  public abstract boolean exists(String paramString);

  public abstract boolean detachMarker(String paramString);

  public abstract Marker getDetachedMarker(String paramString);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.IMarkerFactory
 * JD-Core Version:    0.6.2
 */