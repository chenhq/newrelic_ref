package com.newrelic.org.reflections;

import com.newrelic.org.reflections.adapters.MetadataAdapter;
import com.newrelic.org.reflections.scanners.Scanner;
import com.newrelic.org.reflections.serializers.Serializer;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public abstract interface Configuration
{
  public abstract Set<Scanner> getScanners();

  public abstract Set<URL> getUrls();

  public abstract MetadataAdapter getMetadataAdapter();

  public abstract boolean acceptsInput(String paramString);

  public abstract ExecutorService getExecutorService();

  public abstract Serializer getSerializer();

  public abstract ClassLoader[] getClassLoaders();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.Configuration
 * JD-Core Version:    0.6.2
 */