/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class DoubleMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public DoubleMemberValue(int index, ConstPool cp)
/*     */   {
/*  40 */     super('D', cp);
/*  41 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public DoubleMemberValue(double d, ConstPool cp)
/*     */   {
/*  50 */     super('D', cp);
/*  51 */     setValue(d);
/*     */   }
/*     */ 
/*     */   public DoubleMemberValue(ConstPool cp)
/*     */   {
/*  58 */     super('D', cp);
/*  59 */     setValue(0.0D);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  63 */     return new Double(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  67 */     return Double.TYPE;
/*     */   }
/*     */ 
/*     */   public double getValue()
/*     */   {
/*  74 */     return this.cp.getDoubleInfo(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(double newValue)
/*     */   {
/*  81 */     this.valueIndex = this.cp.addDoubleInfo(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  88 */     return Double.toString(getValue());
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
/* 102 */     visitor.visitDoubleMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.DoubleMemberValue
 * JD-Core Version:    0.6.2
 */