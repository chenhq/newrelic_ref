package com.newrelic.javassist.scopedpool;

import com.newrelic.javassist.ClassPool;
import java.util.Map;

public abstract interface ScopedClassPoolRepository
{
  public abstract void setClassPoolFactory(ScopedClassPoolFactory paramScopedClassPoolFactory);

  public abstract ScopedClassPoolFactory getClassPoolFactory();

  public abstract boolean isPrune();

  public abstract void setPrune(boolean paramBoolean);

  public abstract ScopedClassPool createScopedClassPool(ClassLoader paramClassLoader, ClassPool paramClassPool);

  public abstract ClassPool findClassPool(ClassLoader paramClassLoader);

  public abstract ClassPool registerClassLoader(ClassLoader paramClassLoader);

  public abstract Map getRegisteredCLs();

  public abstract void clearUnregisteredClassLoaders();

  public abstract void unregisterClassLoader(ClassLoader paramClassLoader);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.scopedpool.ScopedClassPoolRepository
 * JD-Core Version:    0.6.2
 */