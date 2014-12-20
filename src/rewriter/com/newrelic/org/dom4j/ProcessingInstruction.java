package com.newrelic.org.dom4j;

import java.util.Map;

public abstract interface ProcessingInstruction extends Node
{
  public abstract String getTarget();

  public abstract void setTarget(String paramString);

  public abstract String getText();

  public abstract String getValue(String paramString);

  public abstract Map getValues();

  public abstract void setValue(String paramString1, String paramString2);

  public abstract void setValues(Map paramMap);

  public abstract boolean removeValue(String paramString);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.ProcessingInstruction
 * JD-Core Version:    0.6.2
 */