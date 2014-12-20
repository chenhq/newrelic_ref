package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.AnnotationVisitor;

public class RemappingAnnotationAdapter
  implements AnnotationVisitor
{
  private final AnnotationVisitor av;
  private final Remapper renamer;

  public RemappingAnnotationAdapter(AnnotationVisitor paramAnnotationVisitor, Remapper paramRemapper)
  {
    this.av = paramAnnotationVisitor;
    this.renamer = paramRemapper;
  }

  public void visit(String paramString, Object paramObject)
  {
    this.av.visit(paramString, this.renamer.mapValue(paramObject));
  }

  public void visitEnum(String paramString1, String paramString2, String paramString3)
  {
    this.av.visitEnum(paramString1, this.renamer.mapDesc(paramString2), paramString3);
  }

  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2)
  {
    AnnotationVisitor localAnnotationVisitor = this.av.visitAnnotation(paramString1, this.renamer.mapDesc(paramString2));
    return localAnnotationVisitor == this.av ? this : localAnnotationVisitor == null ? null : new RemappingAnnotationAdapter(localAnnotationVisitor, this.renamer);
  }

  public AnnotationVisitor visitArray(String paramString)
  {
    AnnotationVisitor localAnnotationVisitor = this.av.visitArray(paramString);
    return localAnnotationVisitor == this.av ? this : localAnnotationVisitor == null ? null : new RemappingAnnotationAdapter(localAnnotationVisitor, this.renamer);
  }

  public void visitEnd()
  {
    this.av.visitEnd();
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.RemappingAnnotationAdapter
 * JD-Core Version:    0.6.2
 */