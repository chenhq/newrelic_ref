/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class ClassMemberValue extends MemberValue
/*     */ {
/*     */   int valueIndex;
/*     */ 
/*     */   public ClassMemberValue(int index, ConstPool cp)
/*     */   {
/*  40 */     super('c', cp);
/*  41 */     this.valueIndex = index;
/*     */   }
/*     */ 
/*     */   public ClassMemberValue(String className, ConstPool cp)
/*     */   {
/*  50 */     super('c', cp);
/*  51 */     setValue(className);
/*     */   }
/*     */ 
/*     */   public ClassMemberValue(ConstPool cp)
/*     */   {
/*  59 */     super('c', cp);
/*  60 */     setValue("java.lang.Class");
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method m) throws ClassNotFoundException
/*     */   {
/*  65 */     String classname = getValue();
/*  66 */     if (classname.equals("void"))
/*  67 */       return Void.TYPE;
/*  68 */     if (classname.equals("int"))
/*  69 */       return Integer.TYPE;
/*  70 */     if (classname.equals("byte"))
/*  71 */       return Byte.TYPE;
/*  72 */     if (classname.equals("long"))
/*  73 */       return Long.TYPE;
/*  74 */     if (classname.equals("double"))
/*  75 */       return Double.TYPE;
/*  76 */     if (classname.equals("float"))
/*  77 */       return Float.TYPE;
/*  78 */     if (classname.equals("char"))
/*  79 */       return Character.TYPE;
/*  80 */     if (classname.equals("short"))
/*  81 */       return Short.TYPE;
/*  82 */     if (classname.equals("boolean")) {
/*  83 */       return Boolean.TYPE;
/*     */     }
/*  85 */     return loadClass(cl, classname);
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) throws ClassNotFoundException {
/*  89 */     return loadClass(cl, "java.lang.Class");
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  98 */     String v = this.cp.getUtf8Info(this.valueIndex);
/*  99 */     return Descriptor.toClassName(v);
/*     */   }
/*     */ 
/*     */   public void setValue(String newClassName)
/*     */   {
/* 108 */     String setTo = Descriptor.of(newClassName);
/* 109 */     this.valueIndex = this.cp.addUtf8Info(setTo);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 116 */     return "<" + getValue() + " class>";
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/* 123 */     writer.classInfoIndex(this.cp.getUtf8Info(this.valueIndex));
/*     */   }
/*     */ 
/*     */   public void accept(MemberValueVisitor visitor)
/*     */   {
/* 130 */     visitor.visitClassMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.ClassMemberValue
 * JD-Core Version:    0.6.2
 */