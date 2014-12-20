/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class FloatMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public FloatMemberValue(int index, ConstPool cp)
/*     */   {
/*  40 */     super('F', cp);
/*  41 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public FloatMemberValue(float f, ConstPool cp)
/*     */   {
/*  50 */     super('F', cp);
/*  51 */     setValue(f);
/*     */   }
/*     */ 
/*     */   public FloatMemberValue(ConstPool cp)
/*     */   {
/*  58 */     super('F', cp);
/*  59 */     setValue(0.0F);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  63 */     return new Float(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  67 */     return Float.TYPE;
/*     */   }
/*     */ 
/*     */   public float getValue()
/*     */   {
/*  74 */     return this.cp.getFloatInfo(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(float newValue)
/*     */   {
/*  81 */     this.valueIndex = this.cp.addFloatInfo(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  88 */     return Float.toString(getValue());
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/*  95 */     writer.constValueIndex(getValue());
/*     */   }
/*     */ 
/*     */   public void accept(MemberValueVisitor visitor)
/*     */   {
/* 102 */     visitor.visitFloatMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.FloatMemberValue
 * JD-Core Version:    0.6.2
 */