package com.newrelic.com.google.common.collect;

import com.newrelic.com.google.common.annotations.GwtCompatible;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface ClassToInstanceMap<B> extends Map<Class<? extends B>, B>
{
  public abstract <T extends B> T getInstance(Class<T> paramClass);

  public abstract <T extends B> T putInstance(Class<T> paramClass, @Nullable T paramT);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ClassToInstanceMap
 * JD-Core Version:    0.6.2
 */