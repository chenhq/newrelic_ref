package com.newrelic.javassist.tools.reflect;

public abstract interface Metalevel
{
  public abstract ClassMetaobject _getClass();

  public abstract Metaobject _getMetaobject();

  public abstract void _setMetaobject(Metaobject paramMetaobject);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.Metalevel
 * JD-Core Version:    0.6.2
 */