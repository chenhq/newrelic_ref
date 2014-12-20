package com.newrelic.objectweb.asm;

public abstract interface FieldVisitor
{
  public abstract AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean);

  public abstract void visitAttribute(Attribute paramAttribute);

  public abstract void visitEnd();
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.FieldVisitor
 * JD-Core Version:    0.6.2
 */