package com.newrelic.org.dom4j;

public abstract interface Attribute extends Node
{
  public abstract QName getQName();

  public abstract Namespace getNamespace();

  public abstract void setNamespace(Namespace paramNamespace);

  public abstract String getNamespacePrefix();

  public abstract String getNamespaceURI();

  public abstract String getQualifiedName();

  public abstract String getValue();

  public abstract void setValue(String paramString);

  public abstract Object getData();

  public abstract void setData(Object paramObject);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.dom4j.Attribute
 * JD-Core Version:    0.6.2
 */