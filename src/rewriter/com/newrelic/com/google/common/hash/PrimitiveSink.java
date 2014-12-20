package com.newrelic.com.google.common.hash;

import com.newrelic.com.google.common.annotations.Beta;
import java.nio.charset.Charset;

@Beta
public abstract interface PrimitiveSink
{
  public abstract PrimitiveSink putByte(byte paramByte);

  public abstract PrimitiveSink putBytes(byte[] paramArrayOfByte);

  public abstract PrimitiveSink putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract PrimitiveSink putShort(short paramShort);

  public abstract PrimitiveSink putInt(int paramInt);

  public abstract PrimitiveSink putLong(long paramLong);

  public abstract PrimitiveSink putFloat(float paramFloat);

  public abstract PrimitiveSink putDouble(double paramDouble);

  public abstract PrimitiveSink putBoolean(boolean paramBoolean);

  public abstract PrimitiveSink putChar(char paramChar);

  public abstract PrimitiveSink putUnencodedChars(CharSequence paramCharSequence);

  public abstract PrimitiveSink putString(CharSequence paramCharSequence, Charset paramCharset);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.hash.PrimitiveSink
 * JD-Core Version:    0.6.2
 */