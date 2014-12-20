package com.newrelic.com.google.common.hash;

import com.newrelic.com.google.common.annotations.Beta;
import java.nio.charset.Charset;

@Beta
public abstract interface HashFunction
{
  public abstract Hasher newHasher();

  public abstract Hasher newHasher(int paramInt);

  public abstract HashCode hashInt(int paramInt);

  public abstract HashCode hashLong(long paramLong);

  public abstract HashCode hashBytes(byte[] paramArrayOfByte);

  public abstract HashCode hashBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract HashCode hashUnencodedChars(CharSequence paramCharSequence);

  public abstract HashCode hashString(CharSequence paramCharSequence, Charset paramCharset);

  public abstract <T> HashCode hashObject(T paramT, Funnel<? super T> paramFunnel);

  public abstract int bits();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.HashFunction
 * JD-Core Version:    0.6.2
 */