package com.newrelic.com.google.common.io;

import java.io.IOException;

@Deprecated
public abstract interface OutputSupplier<T>
{
  public abstract T getOutput()
    throws IOException;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.OutputSupplier
 * JD-Core Version:    0.6.2
 */