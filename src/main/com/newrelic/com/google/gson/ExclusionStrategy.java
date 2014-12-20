package com.newrelic.com.google.gson;

public abstract interface ExclusionStrategy
{
  public abstract boolean shouldSkipField(FieldAttributes paramFieldAttributes);

  public abstract boolean shouldSkipClass(Class<?> paramClass);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.ExclusionStrategy
 * JD-Core Version:    0.6.2
 */