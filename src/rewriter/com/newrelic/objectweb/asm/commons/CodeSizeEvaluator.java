package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.Label;
import com.newrelic.objectweb.asm.MethodAdapter;
import com.newrelic.objectweb.asm.MethodVisitor;
import com.newrelic.objectweb.asm.Opcodes;

public class CodeSizeEvaluator extends MethodAdapter
  implements Opcodes
{
  private int minSize;
  private int maxSize;

  public CodeSizeEvaluator(MethodVisitor paramMethodVisitor)
  {
    super(paramMethodVisitor);
  }

  public int getMinSize()
  {
    return this.minSize;
  }

  public int getMaxSize()
  {
    return this.maxSize;
  }

  public void visitInsn(int paramInt)
  {
    this.minSize += 1;
    this.maxSize += 1;
    if (this.mv != null)
      this.mv.visitInsn(paramInt);
  }

  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 17)
    {
      this.minSize += 3;
      this.maxSize += 3;
    }
    else
    {
      this.minSize += 2;
      this.maxSize += 2;
    }
    if (this.mv != null)
      this.mv.visitIntInsn(paramInt1, paramInt2);
  }

  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 4) && (paramInt1 != 169))
    {
      this.minSize += 1;
      this.maxSize += 1;
    }
    else if (paramInt2 >= 256)
    {
      this.minSize += 4;
      this.maxSize += 4;
    }
    else
    {
      this.minSize += 2;
      this.maxSize += 2;
    }
    if (this.mv != null)
      this.mv.visitVarInsn(paramInt1, paramInt2);
  }

  public void visitTypeInsn(int paramInt, String paramString)
  {
    this.minSize += 3;
    this.maxSize += 3;
    if (this.mv != null)
      this.mv.visitTypeInsn(paramInt, paramString);
  }

  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.minSize += 3;
    this.maxSize += 3;
    if (this.mv != null)
      this.mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
  }

  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    if ((paramInt == 185) || (paramInt == 186))
    {
      this.minSize += 5;
      this.maxSize += 5;
    }
    else
    {
      this.minSize += 3;
      this.maxSize += 3;
    }
    if (this.mv != null)
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
  }

  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    this.minSize += 3;
    if ((paramInt == 167) || (paramInt == 168))
      this.maxSize += 5;
    else
      this.maxSize += 8;
    if (this.mv != null)
      this.mv.visitJumpInsn(paramInt, paramLabel);
  }

  public void visitLdcInsn(Object paramObject)
  {
    if (((paramObject instanceof Long)) || ((paramObject instanceof Double)))
    {
      this.minSize += 3;
      this.maxSize += 3;
    }
    else
    {
      this.minSize += 2;
      this.maxSize += 3;
    }
    if (this.mv != null)
      this.mv.visitLdcInsn(paramObject);
  }

  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    if ((paramInt1 > 255) || (paramInt2 > 127) || (paramInt2 < -128))
    {
      this.minSize += 6;
      this.maxSize += 6;
    }
    else
    {
      this.minSize += 3;
      this.maxSize += 3;
    }
    if (this.mv != null)
      this.mv.visitIincInsn(paramInt1, paramInt2);
  }

  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label[] paramArrayOfLabel)
  {
    this.minSize += 13 + paramArrayOfLabel.length * 4;
    this.maxSize += 16 + paramArrayOfLabel.length * 4;
    if (this.mv != null)
      this.mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramArrayOfLabel);
  }

  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    this.minSize += 9 + paramArrayOfInt.length * 8;
    this.maxSize += 12 + paramArrayOfInt.length * 8;
    if (this.mv != null)
      this.mv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
  }

  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    this.minSize += 4;
    this.maxSize += 4;
    if (this.mv != null)
      this.mv.visitMultiANewArrayInsn(paramString, paramInt);
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.CodeSizeEvaluator
 * JD-Core Version:    0.6.2
 */