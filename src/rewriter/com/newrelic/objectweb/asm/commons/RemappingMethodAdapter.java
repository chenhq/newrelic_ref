package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.AnnotationVisitor;
import com.newrelic.objectweb.asm.Label;
import com.newrelic.objectweb.asm.MethodVisitor;

public class RemappingMethodAdapter extends LocalVariablesSorter
{
  protected final Remapper remapper;

  public RemappingMethodAdapter(int paramInt, String paramString, MethodVisitor paramMethodVisitor, Remapper paramRemapper)
  {
    super(paramInt, paramString, paramMethodVisitor);
    this.remapper = paramRemapper;
  }

  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    super.visitFieldInsn(paramInt, this.remapper.mapType(paramString1), this.remapper.mapFieldName(paramString1, paramString2, paramString3), this.remapper.mapDesc(paramString3));
  }

  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    super.visitMethodInsn(paramInt, this.remapper.mapType(paramString1), this.remapper.mapMethodName(paramString1, paramString2, paramString3), this.remapper.mapMethodDesc(paramString3));
  }

  public void visitTypeInsn(int paramInt, String paramString)
  {
    super.visitTypeInsn(paramInt, this.remapper.mapType(paramString));
  }

  public void visitLdcInsn(Object paramObject)
  {
    super.visitLdcInsn(this.remapper.mapValue(paramObject));
  }

  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    super.visitMultiANewArrayInsn(this.remapper.mapDesc(paramString), paramInt);
  }

  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    super.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString == null ? null : this.remapper.mapType(paramString));
  }

  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    super.visitLocalVariable(paramString1, this.remapper.mapDesc(paramString2), this.remapper.mapSignature(paramString3, true), paramLabel1, paramLabel2, paramInt);
  }

  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = this.mv.visitAnnotation(this.remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, this.remapper);
  }

  public AnnotationVisitor visitAnnotationDefault()
  {
    AnnotationVisitor localAnnotationVisitor = this.mv.visitAnnotationDefault();
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, this.remapper);
  }

  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    AnnotationVisitor localAnnotationVisitor = this.mv.visitParameterAnnotation(paramInt, this.remapper.mapDesc(paramString), paramBoolean);
    return localAnnotationVisitor == null ? localAnnotationVisitor : new RemappingAnnotationAdapter(localAnnotationVisitor, this.remapper);
  }

  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    super.visitFrame(paramInt1, paramInt2, remapEntries(paramInt2, paramArrayOfObject1), paramInt3, remapEntries(paramInt3, paramArrayOfObject2));
  }

  private Object[] remapEntries(int paramInt, Object[] paramArrayOfObject)
  {
    for (int i = 0; i < paramInt; i++)
      if ((paramArrayOfObject[i] instanceof String))
      {
        Object[] arrayOfObject = new Object[paramInt];
        if (i > 0)
          System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, i);
        do
        {
          Object localObject = paramArrayOfObject[i];
          arrayOfObject[(i++)] = ((localObject instanceof String) ? this.remapper.mapType((String)localObject) : localObject);
        }
        while (i < paramInt);
        return arrayOfObject;
      }
    return paramArrayOfObject;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.RemappingMethodAdapter
 * JD-Core Version:    0.6.2
 */