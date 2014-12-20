/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class BooleanMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public BooleanMemberValue(int index, ConstPool cp)
/*     */   {
/*  38 */     super('Z', cp);
/*  39 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public BooleanMemberValue(boolean b, ConstPool cp)
/*     */   {
/*  48 */     super('Z', cp);
/*  49 */     setValue(b);
/*     */   }
/*     */ 
/*     */   public BooleanMemberValue(ConstPool cp)
/*     */   {
/*  56 */     super('Z', cp);
/*  57 */     setValue(false);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  61 */     return new Boolean(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  65 */     return Boolean.TYPE;
/*     */   }
/*     */ 
/*     */   public boolean getValue()
/*     */   {
/*  72 */     return this.cp.getIntegerInfo(this.valueIndex) != 0;
/*     */   }
/*     */ 
/*     */   public void setValue(boolean newValue)
/*     */   {
/*  79 */     this.valueIndex = this.cp.addIntegerInfo(newValue ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  86 */     return getValue() ? "true" : "false";
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/*  93 */     writer.constValueIndex(getValue());
/*     */   }
/*     */ 
/*     */   public void accept(MemberValueVisitor visitor)
/*     */   {
/* 100 */     visitor.visitBooleanMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.BooleanMemberValue
 * JD-Core Version:    0.6.2
 */