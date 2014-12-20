package com.newrelic.com.google.common.io;

import com.newrelic.com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public abstract interface ByteProcessor<T>
{
  public abstract boolean processBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;

  public abstract T getResult();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.ByteProcessor
 * JD-Core Version:    0.6.2
 */