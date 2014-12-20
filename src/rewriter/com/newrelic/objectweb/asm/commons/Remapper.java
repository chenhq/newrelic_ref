package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.Type;
import com.newrelic.objectweb.asm.signature.SignatureReader;
import com.newrelic.objectweb.asm.signature.SignatureVisitor;
import com.newrelic.objectweb.asm.signature.SignatureWriter;

public abstract class Remapper
{
  public String mapDesc(String paramString)
  {
    Type localType = Type.getType(paramString);
    switch (localType.getSort())
    {
    case 9:
      String str1 = mapDesc(localType.getElementType().getDescriptor());
      for (int i = 0; i < localType.getDimensions(); i++)
        str1 = '[' + str1;
      return str1;
    case 10:
      String str2 = map(localType.getInternalName());
      if (str2 != null)
        return 'L' + str2 + ';';
      break;
    }
    return paramString;
  }

  private Type mapType(Type paramType)
  {
    String str;
    switch (paramType.getSort())
    {
    case 9:
      str = mapDesc(paramType.getElementType().getDescriptor());
      for (int i = 0; i < paramType.getDimensions(); i++)
        str = '[' + str;
      return Type.getType(str);
    case 10:
      str = map(paramType.getInternalName());
      if (str != null)
        return Type.getObjectType(str);
      break;
    }
    return paramType;
  }

  public String mapType(String paramString)
  {
    if (paramString == null)
      return null;
    return mapType(Type.getObjectType(paramString)).getInternalName();
  }

  public String[] mapTypes(String[] paramArrayOfString)
  {
    String[] arrayOfString = null;
    int i = 0;
    for (int j = 0; j < paramArrayOfString.length; j++)
    {
      String str1 = paramArrayOfString[j];
      String str2 = map(str1);
      if ((str2 != null) && (arrayOfString == null))
      {
        arrayOfString = new String[paramArrayOfString.length];
        if (j > 0)
          System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, j);
        i = 1;
      }
      if (i != 0)
        arrayOfString[j] = (str2 == null ? str1 : str2);
    }
    return i != 0 ? arrayOfString : paramArrayOfString;
  }

  public String mapMethodDesc(String paramString)
  {
    if ("()V".equals(paramString))
      return paramString;
    Type[] arrayOfType = Type.getArgumentTypes(paramString);
    String str = "(";
    for (int i = 0; i < arrayOfType.length; i++)
      str = str + mapDesc(arrayOfType[i].getDescriptor());
    Type localType = Type.getReturnType(paramString);
    if (localType == Type.VOID_TYPE)
      return str + ")V";
    return str + ')' + mapDesc(localType.getDescriptor());
  }

  public Object mapValue(Object paramObject)
  {
    return (paramObject instanceof Type) ? mapType((Type)paramObject) : paramObject;
  }

  public String mapSignature(String paramString, boolean paramBoolean)
  {
    if (paramString == null)
      return null;
    SignatureReader localSignatureReader = new SignatureReader(paramString);
    SignatureWriter localSignatureWriter = new SignatureWriter();
    SignatureVisitor localSignatureVisitor = createRemappingSignatureAdapter(localSignatureWriter);
    if (paramBoolean)
      localSignatureReader.acceptType(localSignatureVisitor);
    else
      localSignatureReader.accept(localSignatureVisitor);
    return localSignatureWriter.toString();
  }

  protected SignatureVisitor createRemappingSignatureAdapter(SignatureVisitor paramSignatureVisitor)
  {
    return new RemappingSignatureAdapter(paramSignatureVisitor, this);
  }

  public String mapMethodName(String paramString1, String paramString2, String paramString3)
  {
    return paramString2;
  }

  public String mapFieldName(String paramString1, String paramString2, String paramString3)
  {
    return paramString2;
  }

  public String map(String paramString)
  {
    return paramString;
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.Remapper
 * JD-Core Version:    0.6.2
 */