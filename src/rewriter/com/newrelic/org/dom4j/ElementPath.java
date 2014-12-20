package com.newrelic.org.dom4j;

public abstract interface ElementPath
{
  public abstract int size();

  public abstract Element getElement(int paramInt);

  public abstract String getPath();

  public abstract Element getCurrent();

  public abstract void addHandler(String paramString, ElementHandler paramElementHandler);

  public abstract void removeHandler(String paramString);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.ElementPath
 * JD-Core Version:    0.6.2
 */