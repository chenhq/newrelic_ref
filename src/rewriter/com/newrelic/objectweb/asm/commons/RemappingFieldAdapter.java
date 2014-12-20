package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.AnnotationVisitor;
import com.newrelic.objectweb.asm.Attribute;
import com.newrelic.objectweb.asm.FieldVisitor;

public class RemappingFieldAdapter
  implements FieldVisitor
{
  private final FieldVisitor fv;
  private final Remapper remapper;

  public RemappingFieldAdapter(FieldVisitor paramFieldVisitor, Remapper paramRemapper)
  {
    this.fv = paramFieldVisitor;
    this.remapper = paramRemapper;
  }

  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = this.fv.visitAnnotation(this.remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? null : new RemappingAnnotationAdapter(localAnnotationVisitor, this.remapper);
  }

  public void visitAttribute(Attribute paramAttribute)
  {
    this.fv.visitAttribute(paramAttribute);
  }

  public void visitEnd()
  {
    this.fv.visitEnd();
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.RemappingFieldAdapter
 * JD-Core Version:    0.6.2
 */