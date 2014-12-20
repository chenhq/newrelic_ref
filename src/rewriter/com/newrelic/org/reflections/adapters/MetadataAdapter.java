package com.newrelic.org.reflections.adapters;

import com.newrelic.org.reflections.vfs.Vfs.File;
import java.util.List;

public abstract interface MetadataAdapter<C, F, M>
{
  public abstract String getClassName(C paramC);

  public abstract String getSuperclassName(C paramC);

  public abstract List<String> getInterfacesNames(C paramC);

  public abstract List<F> getFields(C paramC);

  public abstract List<M> getMethods(C paramC);

  public abstract String getMethodName(M paramM);

  public abstract List<String> getParameterNames(M paramM);

  public abstract List<String> getClassAnnotationNames(C paramC);

  public abstract List<String> getFieldAnnotationNames(F paramF);

  public abstract List<String> getMethodAnnotationNames(M paramM);

  public abstract List<String> getParameterAnnotationNames(M paramM, int paramInt);

  public abstract String getReturnTypeName(M paramM);

  public abstract String getFieldName(F paramF);

  public abstract C getOfCreateClassObject(Vfs.File paramFile)
    throws Exception;

  public abstract String getMethodModifier(M paramM);

  public abstract String getMethodKey(C paramC, M paramM);

  public abstract String getMethodFullKey(C paramC, M paramM);

  public abstract boolean isPublic(Object paramObject);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.adapters.MetadataAdapter
 * JD-Core Version:    0.6.2
 */