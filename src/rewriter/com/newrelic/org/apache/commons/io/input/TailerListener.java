package com.newrelic.org.apache.commons.io.input;

public abstract interface TailerListener
{
  public abstract void init(Tailer paramTailer);

  public abstract void fileNotFound();

  public abstract void fileRotated();

  public abstract void handle(String paramString);

  public abstract void handle(Exception paramException);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.TailerListener
 * JD-Core Version:    0.6.2
 */