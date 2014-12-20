package com.newrelic.org.slf4j;

import java.io.Serializable;
import java.util.Iterator;

public abstract interface Marker extends Serializable
{
  public static final String ANY_MARKER = "*";
  public static final String ANY_NON_NULL_MARKER = "+";

  public abstract String getName();

  public abstract void add(Marker paramMarker);

  public abstract boolean remove(Marker paramMarker);

  /** @deprecated */
  public abstract boolean hasChildren();

  public abstract boolean hasReferences();

  public abstract Iterator iterator();

  public abstract boolean contains(Marker paramMarker);

  public abstract boolean contains(String paramString);

  public abstract boolean equals(Object paramObject);

  public abstract int hashCode();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.Marker
 * JD-Core Version:    0.6.2
 */