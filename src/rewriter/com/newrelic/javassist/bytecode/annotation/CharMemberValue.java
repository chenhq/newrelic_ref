/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class CharMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public CharMemberValue(int index, ConstPool cp)
/*     */   {
/*  39 */     super('C', cp);
/*  40 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public CharMemberValue(char c, ConstPool cp)
/*     */   {
/*  49 */     super('C', cp);
/*  50 */     setValue(c);
/*     */   }
/*     */ 
/*     */   public CharMemberValue(ConstPool cp)
/*     */   {
/*  57 */     super('C', cp);
/*  58 */     setValue('\000');
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) {
/*  62 */     return new Character(getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) {
/*  66 */     return Character.TYPE;
/*     */   }
/*     */ 
/*     */   public char getValue()
/*     */   {
/*  73 */     return (char)this.cp.getIntegerInfo(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(char newValue)
/*     */   {
/*  80 */     this.valueIndex = this.cp.addIntegerInfo(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  87 */     return Character.toString(getValue());
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
/* 101 */     visitor.visitCharMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.CharMemberValue
 * JD-Core Version:    0.6.2
 */