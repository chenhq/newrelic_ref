/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class EnumMemberValue extends MemberValue
/*     */ {
/*     */   int typeIndex;
/*     */   int valueIndex;
/*     */ 
/*     */   public EnumMemberValue(int type, int value, ConstPool cp)
/*     */   {
/*  44 */     super('e', cp);
/*  45 */     this.typeIndex = type;
/*  46 */     this.valueIndex = value;
/*     */   }
/*     */ 
/*     */   public EnumMemberValue(ConstPool cp)
/*     */   {
/*  54 */     super('e', cp);
/*  55 */     this.typeIndex = (this.valueIndex = 0);
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/*  62 */       return getType(cl).getField(getValue()).get(null);
/*     */     }
/*     */     catch (NoSuchFieldException e) {
/*  65 */       throw new ClassNotFoundException(getType() + "." + getValue());
/*     */     } catch (IllegalAccessException e) {
/*     */     }
/*  68 */     throw new ClassNotFoundException(getType() + "." + getValue());
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) throws ClassNotFoundException
/*     */   {
/*  73 */     return loadClass(cl, getType());
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  82 */     return Descriptor.toClassName(this.cp.getUtf8Info(this.typeIndex));
/*     */   }
/*     */ 
/*     */   public void setType(String typename)
/*     */   {
/*  91 */     this.typeIndex = this.cp.addUtf8Info(Descriptor.of(typename));
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  98 */     return this.cp.getUtf8Info(this.valueIndex);
/*     */   }
/*     */ 
/*     */   public void setValue(String name)
/*     */   {
/* 105 */     this.valueIndex = this.cp.addUtf8Info(name);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 109 */     return getType() + "." + getValue();
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/* 116 */     writer.enumConstValue(this.cp.getUtf8Info(this.typeIndex), getValue());
/*     */   }
/*     */ 
/*     */   public void accept(MemberValueVisitor visitor)
/*     */   {
/* 123 */     visitor.visitEnumMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.EnumMemberValue
 * JD-Core Version:    0.6.2
 */