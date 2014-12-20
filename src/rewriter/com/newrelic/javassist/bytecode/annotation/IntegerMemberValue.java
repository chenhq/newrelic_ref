/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class IntegerMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public IntegerMemberValue(int index, ConstPool cp)
/*     */   {
/*  39 */     super('I', cp);
/*  40 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public IntegerMemberValue(ConstPool cp, int value)
/*     */   {
/*  55 */     super('I', cp);
/*  56 */     setValue(value);
/*     */   }
/*     */ 
/*     */   public IntegerMemberValue(ConstPool cp)
/*     */   {
/*  63 */     super('I', cp);
/*  64 */     setValue(0);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  68 */     return new Integer(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  72 */     return Integer.TYPE;
/*     */   }
/*     */ 
/*     */   public int getValue()
/*     */   {
/*  79 */     return this.cp.getIntegerInfo(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(int newValue)
/*     */   {
/*  86 */     this.valueIndex = this.cp.addIntegerInfo(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  93 */     return Integer.toString(getValue());
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/* 100 */     writer.constValueIndex(getValue());
/*     */   }
/*     */ 
/*     */   public void accept(MemberValueVisitor visitor)
/*     */   {
/* 107 */     visitor.visitIntegerMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.IntegerMemberValue
 * JD-Core Version:    0.6.2
 */