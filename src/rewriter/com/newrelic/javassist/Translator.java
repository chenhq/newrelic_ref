package com.newrelic.javassist;

public abstract interface Translator
{
  public abstract void start(ClassPool paramClassPool)
    throws NotFoundException, CannotCompileException;

  public abstract void onLoad(ClassPool paramClassPool, String paramString)
    throws NotFoundException, CannotCompileException;
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.Translator
 * JD-Core Version:    0.6.2
 */