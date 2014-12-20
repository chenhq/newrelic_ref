/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class ByteMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public ByteMemberValue(int index, ConstPool cp)
/*     */   {
/*  38 */     super('B', cp);
/*  39 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public ByteMemberValue(byte b, ConstPool cp)
/*     */   {
/*  48 */     super('B', cp);
/*  49 */     setValue(b);
/*     */   }
/*     */ 
/*     */   public ByteMemberValue(ConstPool cp)
/*     */   {
/*  56 */     super('B', cp);
/*  57 */     setValue((byte)0);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  61 */     return new Byte(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  65 */     return Byte.TYPE;
/*     */   }
/*     */ 
/*     */   public byte getValue()
/*     */   {
/*  72 */     return (byte)this.cp.getIntegerInfo(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(byte newValue)
/*     */   {
/*  79 */     this.valueIndex = this.cp.addIntegerInfo(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  86 */     return Byte.toString(getValue());
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
/* 100 */     visitor.visitByteMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.ByteMemberValue
 * JD-Core Version:    0.6.2
 */