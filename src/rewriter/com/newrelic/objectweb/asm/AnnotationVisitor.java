package com.newrelic.objectweb.asm;

public abstract interface AnnotationVisitor
{
  public abstract void visit(String paramString, Object paramObject);

  public abstract void visitEnum(String paramString1, String paramString2, String paramString3);

  public abstract AnnotationVisitor visitAnnotation(String paramString1, String paramString2);

  public abstract AnnotationVisitor visitArray(String paramString);

  public abstract void visitEnd();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.AnnotationVisitor
 * JD-Core Version:    0.6.2
 */