package com.newrelic.org.dom4j.jaxb;

import javax.xml.bind.Element;

public abstract interface JAXBObjectHandler
{
  public abstract void handleObject(Element paramElement)
    throws Exception;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.jaxb.JAXBObjectHandler
 * JD-Core Version:    0.6.2
 */