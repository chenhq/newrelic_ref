package com.newrelic.org.reflections.serializers;

import com.newrelic.org.reflections.Reflections;
import java.io.File;
import java.io.InputStream;

public abstract interface Serializer
{
  public abstract Reflections read(InputStream paramInputStream);

  public abstract File save(Reflections paramReflections, String paramString);

  public abstract String toString(Reflections paramReflections);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.serializers.Serializer
 * JD-Core Version:    0.6.2
 */