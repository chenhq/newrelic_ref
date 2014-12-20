package com.newrelic.com.google.common.io;

import com.newrelic.com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public abstract interface LineProcessor<T>
{
  public abstract boolean processLine(String paramString)
    throws IOException;

  public abstract T getResult();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.LineProcessor
 * JD-Core Version:    0.6.2
 */