package com.newrelic.javassist.scopedpool;

import com.newrelic.javassist.ClassPool;

public abstract interface ScopedClassPoolFactory
{
  public abstract ScopedClassPool create(ClassLoader paramClassLoader, ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);

  public abstract ScopedClassPool create(ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.scopedpool.ScopedClassPoolFactory
 * JD-Core Version:    0.6.2
 */