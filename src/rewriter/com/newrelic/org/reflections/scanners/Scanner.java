package com.newrelic.org.reflections.scanners;

import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.com.google.common.collect.Multimap;
import com.newrelic.org.reflections.Configuration;
import com.newrelic.org.reflections.vfs.Vfs.File;

public abstract interface Scanner
{
  public abstract void setConfiguration(Configuration paramConfiguration);

  public abstract Multimap<String, String> getStore();

  public abstract void setStore(Multimap<String, String> paramMultimap);

  public abstract Scanner filterResultsBy(Predicate<String> paramPredicate);

  public abstract boolean acceptsInput(String paramString);

  public abstract void scan(Vfs.File paramFile);

  public abstract boolean acceptResult(String paramString);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.Scanner
 * JD-Core Version:    0.6.2
 */