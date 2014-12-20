package com.newrelic.javassist.bytecode.annotation;

public abstract interface MemberValueVisitor
{
  public abstract void visitAnnotationMemberValue(AnnotationMemberValue paramAnnotationMemberValue);

  public abstract void visitArrayMemberValue(ArrayMemberValue paramArrayMemberValue);

  public abstract void visitBooleanMemberValue(BooleanMemberValue paramBooleanMemberValue);

  public abstract void visitByteMemberValue(ByteMemberValue paramByteMemberValue);

  public abstract void visitCharMemberValue(CharMemberValue paramCharMemberValue);

  public abstract void visitDoubleMemberValue(DoubleMemberValue paramDoubleMemberValue);

  public abstract void visitEnumMemberValue(EnumMemberValue paramEnumMemberValue);

  public abstract void visitFloatMemberValue(FloatMemberValue paramFloatMemberValue);

  public abstract void visitIntegerMemberValue(IntegerMemberValue paramIntegerMemberValue);

  public abstract void visitLongMemberValue(LongMemberValue paramLongMemberValue);

  public abstract void visitShortMemberValue(ShortMemberValue paramShortMemberValue);

  public abstract void visitStringMemberValue(StringMemberValue paramStringMemberValue);

  public abstract void visitClassMemberValue(ClassMemberValue paramClassMemberValue);
}

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.MemberValueVisitor
 * JD-Core Version:    0.6.2
 */