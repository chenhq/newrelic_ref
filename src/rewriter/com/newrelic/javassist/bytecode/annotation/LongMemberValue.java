/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class LongMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public LongMemberValue(int index, ConstPool cp)
/*     */   {
/*  39 */     super('J', cp);
/*  40 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public LongMemberValue(long j, ConstPool cp)
/*     */   {
/*  49 */     super('J', cp);
/*  50 */     setValue(j);
/*     */   }
/*     */ 
/*     */   public LongMemberValue(ConstPool cp)
/*     */   {
/*  57 */     super('J', cp);
/*  58 */     setValue(0L);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  62 */     return new Long(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  66 */     return Long.TYPE;
/*     */   }
/*     */ 
/*     */   public long getValue()
/*     */   {
/*  73 */     return this.cp.getLongInfo(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(long newValue)
/*     */   {
/*  80 */     this.valueIndex = this.cp.addLongInfo(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  87 */     return Long.toString(getValue());
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
/* 101 */     visitor.visitLongMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.LongMemberValue
 * JD-Core Version:    0.6.2
 */