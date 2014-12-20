package com.newrelic.org.reflections.scanners;

import com.newrelic.com.google.common.collect.Multimap;
import com.newrelic.org.reflections.adapters.MetadataAdapter;
import java.util.Iterator;
import java.util.List;

public class FieldAnnotationsScanner extends AbstractScanner
{
  public void scan(Object cls)
  {
    String className = getMetadataAdapter().getClassName(cls);
    List fields = getMetadataAdapter().getFields(cls);
    for (Iterator i$ = fields.iterator(); i$.hasNext(); ) { field = i$.next();
      List fieldAnnotations = getMetadataAdapter().getFieldAnnotationNames(field);
      for (String fieldAnnotation : fieldAnnotations)
      {
        if (acceptResult(fieldAnnotation)) {
          String fieldName = getMetadataAdapter().getFieldName(field);
          getStore().put(fieldAnnotation, String.format("%s.%s", new Object[] { className, fieldName }));
        }
      }
    }
    Object field;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.FieldAnnotationsScanner
 * JD-Core Version:    0.6.2
 */