package com.newrelic.objectweb.asm.commons;

import com.newrelic.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter
  implements SignatureVisitor
{
  private final SignatureVisitor v;
  private final Remapper remapper;
  private String className;

  public RemappingSignatureAdapter(SignatureVisitor paramSignatureVisitor, Remapper paramRemapper)
  {
    this.v = paramSignatureVisitor;
    this.remapper = paramRemapper;
  }

  public void visitClassType(String paramString)
  {
    this.className = paramString;
    this.v.visitClassType(this.remapper.mapType(paramString));
  }

  public void visitInnerClassType(String paramString)
  {
    this.className = (this.className + '$' + paramString);
    String str = this.remapper.mapType(this.className);
    this.v.visitInnerClassType(str.substring(str.lastIndexOf('$') + 1));
  }

  public void visitFormalTypeParameter(String paramString)
  {
    this.v.visitFormalTypeParameter(paramString);
  }

  public void visitTypeVariable(String paramString)
  {
    this.v.visitTypeVariable(paramString);
  }

  public SignatureVisitor visitArrayType()
  {
    this.v.visitArrayType();
    return this;
  }

  public void visitBaseType(char paramChar)
  {
    this.v.visitBaseType(paramChar);
  }

  public SignatureVisitor visitClassBound()
  {
    this.v.visitClassBound();
    return this;
  }

  public SignatureVisitor visitExceptionType()
  {
    this.v.visitExceptionType();
    return this;
  }

  public SignatureVisitor visitInterface()
  {
    this.v.visitInterface();
    return this;
  }

  public SignatureVisitor visitInterfaceBound()
  {
    this.v.visitInterfaceBound();
    return this;
  }

  public SignatureVisitor visitParameterType()
  {
    this.v.visitParameterType();
    return this;
  }

  public SignatureVisitor visitReturnType()
  {
    this.v.visitReturnType();
    return this;
  }

  public SignatureVisitor visitSuperclass()
  {
    this.v.visitSuperclass();
    return this;
  }

  public void visitTypeArgument()
  {
    this.v.visitTypeArgument();
  }

  public SignatureVisitor visitTypeArgument(char paramChar)
  {
    this.v.visitTypeArgument(paramChar);
    return this;
  }

  public void visitEnd()
  {
    this.v.visitEnd();
  }
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.objectweb.asm.commons.RemappingSignatureAdapter
 * JD-Core Version:    0.6.2
 */