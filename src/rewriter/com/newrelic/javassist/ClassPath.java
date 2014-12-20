package com.newrelic.javassist;

import java.io.InputStream;
import java.net.URL;

public abstract interface ClassPath
{
  public abstract InputStream openClassfile(String paramString)
    throws NotFoundException;

  public abstract URL find(String paramString);

  public abstract void close();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.ClassPath
 * JD-Core Version:    0.6.2
 */