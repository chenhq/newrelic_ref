package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.Label;
import com.newrelic.objectweb.asm.MethodAdapter;
import com.newrelic.objectweb.asm.MethodVisitor;
import com.newrelic.objectweb.asm.Opcodes;
import com.newrelic.objectweb.asm.Type;

public class LocalVariablesSorter extends MethodAdapter
{
  private static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
  private int[] mapping = new int[40];
  private Object[] newLocals = new Object[20];
  protected final int firstLocal;
  protected int nextLocal;
  private boolean changed;

  public LocalVariablesSorter(int paramInt, String paramString, MethodVisitor paramMethodVisitor)
  {
    super(paramMethodVisitor);
    Type[] arrayOfType = Type.getArgumentTypes(paramString);
    this.nextLocal = ((0x8 & paramInt) == 0 ? 1 : 0);
    for (int i = 0; i < arrayOfType.length; i++)
      this.nextLocal += arrayOfType[i].getSize();
    this.firstLocal = this.nextLocal;
  }

  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    Type localType;
    switch (paramInt1)
    {
    case 22:
    case 55:
      localType = Type.LONG_TYPE;
      break;
    case 24:
    case 57:
      localType = Type.DOUBLE_TYPE;
      break;
    case 23:
    case 56:
      localType = Type.FLOAT_TYPE;
      break;
    case 21:
    case 54:
      localType = Type.INT_TYPE;
      break;
    default:
      localType = OBJECT_TYPE;
    }
    this.mv.visitVarInsn(paramInt1, remap(paramInt2, localType));
  }

  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    this.mv.visitIincInsn(remap(paramInt1, Type.INT_TYPE), paramInt2);
  }

  public void visitMaxs(int paramInt1, int paramInt2)
  {
    this.mv.visitMaxs(paramInt1, this.nextLocal);
  }

  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    int i = remap(paramInt, Type.getType(paramString2));
    this.mv.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, i);
  }

  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    if (paramInt1 != -1)
      throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
    if (!this.changed)
    {
      this.mv.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
      return;
    }
    Object[] arrayOfObject = new Object[this.newLocals.length];
    System.arraycopy(this.newLocals, 0, arrayOfObject, 0, arrayOfObject.length);
    int i = 0;
    for (int j = 0; j < paramInt2; j++)
    {
      Object localObject1 = paramArrayOfObject1[j];
      int m = (localObject1 == Opcodes.LONG) || (localObject1 == Opcodes.DOUBLE) ? 2 : 1;
      if (localObject1 != Opcodes.TOP)
      {
        Type localType = OBJECT_TYPE;
        if (localObject1 == Opcodes.INTEGER)
          localType = Type.INT_TYPE;
        else if (localObject1 == Opcodes.FLOAT)
          localType = Type.FLOAT_TYPE;
        else if (localObject1 == Opcodes.LONG)
          localType = Type.LONG_TYPE;
        else if (localObject1 == Opcodes.DOUBLE)
          localType = Type.DOUBLE_TYPE;
        else if ((localObject1 instanceof String))
          localType = Type.getObjectType((String)localObject1);
        setFrameLocal(remap(i, localType), localObject1);
      }
      i += m;
    }
    i = 0;
    j = 0;
    for (int k = 0; i < this.newLocals.length; k++)
    {
      Object localObject2 = this.newLocals[(i++)];
      if ((localObject2 != null) && (localObject2 != Opcodes.TOP))
      {
        this.newLocals[k] = localObject2;
        j = k + 1;
        if ((localObject2 == Opcodes.LONG) || (localObject2 == Opcodes.DOUBLE))
          i++;
      }
      else
      {
        this.newLocals[k] = Opcodes.TOP;
      }
    }
    this.mv.visitFrame(paramInt1, j, this.newLocals, paramInt3, paramArrayOfObject2);
    this.newLocals = arrayOfObject;
  }

  public int newLocal(Type paramType)
  {
    Object localObject;
    switch (paramType.getSort())
    {
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
      localObject = Opcodes.INTEGER;
      break;
    case 6:
      localObject = Opcodes.FLOAT;
      break;
    case 7:
      localObject = Opcodes.LONG;
      break;
    case 8:
      localObject = Opcodes.DOUBLE;
      break;
    case 9:
      localObject = paramType.getDescriptor();
      break;
    default:
      localObject = paramType.getInternalName();
    }
    int i = this.nextLocal;
    this.nextLocal += paramType.getSize();
    setLocalType(i, paramType);
    setFrameLocal(i, localObject);
    return i;
  }

  protected void setLocalType(int paramInt, Type paramType)
  {
  }

  private void setFrameLocal(int paramInt, Object paramObject)
  {
    int i = this.newLocals.length;
    if (paramInt >= i)
    {
      Object[] arrayOfObject = new Object[Math.max(2 * i, paramInt + 1)];
      System.arraycopy(this.newLocals, 0, arrayOfObject, 0, i);
      this.newLocals = arrayOfObject;
    }
    this.newLocals[paramInt] = paramObject;
  }

  private int remap(int paramInt, Type paramType)
  {
    if (paramInt + paramType.getSize() <= this.firstLocal)
      return paramInt;
    int i = 2 * paramInt + paramType.getSize() - 1;
    int j = this.mapping.length;
    if (i >= j)
    {
      int[] arrayOfInt = new int[Math.max(2 * j, i + 1)];
      System.arraycopy(this.mapping, 0, arrayOfInt, 0, j);
      this.mapping = arrayOfInt;
    }
    int k = this.mapping[i];
    if (k == 0)
    {
      k = newLocalMapping(paramType);
      setLocalType(k, paramType);
      this.mapping[i] = (k + 1);
    }
    else
    {
      k--;
    }
    if (k != paramInt)
      this.changed = true;
    return k;
  }

  protected int newLocalMapping(Type paramType)
  {
    int i = this.nextLocal;
    this.nextLocal += paramType.getSize();
    return i;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.LocalVariablesSorter
 * JD-Core Version:    0.6.2
 */