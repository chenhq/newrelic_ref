/*    */ package com.newrelic.javassist.bytecode.annotation;
/*    */ 
/*    */ import com.newrelic.javassist.ClassPool;
/*    */ import com.newrelic.javassist.bytecode.ConstPool;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class AnnotationMemberValue extends MemberValue
/*    */ {
/*    */   Annotation value;
/*    */ 
/*    */   public AnnotationMemberValue(ConstPool cp)
/*    */   {
/* 35 */     this(null, cp);
/*    */   }
/*    */ 
/*    */   public AnnotationMemberValue(Annotation a, ConstPool cp)
/*    */   {
/* 43 */     super('@', cp);
/* 44 */     this.value = a;
/*    */   }
/*    */ 
/*    */   Object getValue(ClassLoader cl, ClassPool cp, Method m)
/*    */     throws ClassNotFoundException
/*    */   {
/* 50 */     return AnnotationImpl.make(cl, getType(cl), cp, this.value);
/*    */   }
/*    */ 
/*    */   Class getType(ClassLoader cl) throws ClassNotFoundException {
/* 54 */     if (this.value == null) {
/* 55 */       throw new ClassNotFoundException("no type specified");
/*    */     }
/* 57 */     return loadClass(cl, this.value.getTypeName());
/*    */   }
/*    */ 
/*    */   public Annotation getValue()
/*    */   {
/* 64 */     return this.value;
/*    */   }
/*    */ 
/*    */   public void setValue(Annotation newValue)
/*    */   {
/* 71 */     this.value = newValue;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 78 */     return this.value.toString();
/*    */   }
/*    */ 
/*    */   public void write(AnnotationsWriter writer)
/*    */     throws IOException
/*    */   {
/* 85 */     writer.annotationValue();
/* 86 */     this.value.write(writer);
/*    */   }
/*    */ 
/*    */   public void accept(MemberValueVisitor visitor)
/*    */   {
/* 93 */     visitor.visitAnnotationMemberValue(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.AnnotationMemberValue
 * JD-Core Version:    0.6.2
 */