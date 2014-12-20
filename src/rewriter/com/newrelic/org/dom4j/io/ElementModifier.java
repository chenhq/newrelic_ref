package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.Element;

public abstract interface ElementModifier
{
  public abstract Element modifyElement(Element paramElement)
    throws Exception;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.io.ElementModifier
 * JD-Core Version:    0.6.2
 */