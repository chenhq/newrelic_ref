package com.newrelic.org.reflections.scanners;

import com.newrelic.com.google.common.collect.Multimap;
import com.newrelic.org.reflections.adapters.MetadataAdapter;
import java.util.Iterator;
import java.util.List;

public class MethodAnnotationsScanner extends AbstractScanner
{
  public void scan(Object cls)
  {
    for (Iterator i$ = getMetadataAdapter().getMethods(cls).iterator(); i$.hasNext(); ) { method = i$.next();
      for (String methodAnnotation : getMetadataAdapter().getMethodAnnotationNames(method))
        if (acceptResult(methodAnnotation))
          getStore().put(methodAnnotation, getMetadataAdapter().getMethodFullKey(cls, method));
    }
    Object method;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.MethodAnnotationsScanner
 * JD-Core Version:    0.6.2
 */