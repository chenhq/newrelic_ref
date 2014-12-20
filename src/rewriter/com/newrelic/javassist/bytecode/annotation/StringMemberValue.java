/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class StringMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public StringMemberValue(int index, ConstPool cp)
/*     */   {
/*  39 */     super('s', cp);
/*  40 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public StringMemberValue(String str, ConstPool cp)
/*     */   {
/*  49 */     super('s', cp);
/*  50 */     setValue(str);
/*     */   }
/*     */ 
/*     */   public StringMemberValue(ConstPool cp)
/*     */   {
/*  57 */     super('s', cp);
/*  58 */     setValue("");
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  62 */     return getValue();
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  66 */     return String.class;
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  73 */     return this.cp.getUtf8Info(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(String newValue)
/*     */   {
/*  80 */     this.valueIndex = this.cp.addUtf8Info(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  87 */     return "\"" + getValue() + "\"";
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
/* 101 */     visitor.visitStringMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.StringMemberValue
 * JD-Core Version:    0.6.2
 */