/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class ShortMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public ShortMemberValue(int index, ConstPool cp)
/*     */   {
/*  39 */     super('S', cp);
/*  40 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public ShortMemberValue(short s, ConstPool cp)
/*     */   {
/*  49 */     super('S', cp);
/*  50 */     setValue(s);
/*     */   }
/*     */ 
/*     */   public ShortMemberValue(ConstPool cp)
/*     */   {
/*  57 */     super('S', cp);
/*  58 */     setValue((short)0);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  62 */     return new Short(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  66 */     return Short.TYPE;
/*     */   }
/*     */ 
/*     */   public short getValue()
/*     */   {
/*  73 */     return (short)this.cp.getIntegerInfo(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(short newValue)
/*     */   {
/*  80 */     this.valueIndex = this.cp.addIntegerInfo(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  87 */     return Short.toString(getValue());
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/*  94 */     writer.constValueIndex(getValue());
/*     */   }
/*     */ 
/*     */   public void accept(MemberValueVisitor visitor)
/*     */   {
/* 101 */     visitor.visitShortMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.ShortMemberValue
 * JD-Core Version:    0.6.2
 */