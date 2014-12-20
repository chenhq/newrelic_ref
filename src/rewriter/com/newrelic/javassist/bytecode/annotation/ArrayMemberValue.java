/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class ArrayMemberValue extends MemberValue
/*     */ {
/*     */   MemberValue type;
/*     */   MemberValue[] values;
/*     */ 
/*     */   public ArrayMemberValue(ConstPool cp)
/*     */   {
/*  37 */     super('[', cp);
/*  38 */     this.type = null;
/*  39 */     this.values = null;
/*     */   }
/*     */ 
/*     */   public ArrayMemberValue(MemberValue t, ConstPool cp)
/*     */   {
/*  48 */     super('[', cp);
/*  49 */     this.type = t;
/*  50 */     this.values = null;
/*     */   }
/*     */ 
/*     */   Object getValue(ClassLoader cl, ClassPool cp, Method method)
/*     */     throws ClassNotFoundException
/*     */   {
/*  56 */     if (this.values == null) {
/*  57 */       throw new ClassNotFoundException("no array elements found: " + method.getName());
/*     */     }
/*     */ 
/*  60 */     int size = this.values.length;
/*     */     Class clazz;
/*  62 */     if (this.type == null) {
/*  63 */       Class clazz = method.getReturnType().getComponentType();
/*  64 */       if ((clazz == null) || (size > 0))
/*  65 */         throw new ClassNotFoundException("broken array type: " + method.getName());
/*     */     }
/*     */     else
/*     */     {
/*  69 */       clazz = this.type.getType(cl);
/*     */     }
/*  71 */     Object a = Array.newInstance(clazz, size);
/*  72 */     for (int i = 0; i < size; i++) {
/*  73 */       Array.set(a, i, this.values[i].getValue(cl, cp, method));
/*     */     }
/*  75 */     return a;
/*     */   }
/*     */ 
/*     */   Class getType(ClassLoader cl) throws ClassNotFoundException {
/*  79 */     if (this.type == null) {
/*  80 */       throw new ClassNotFoundException("no array type specified");
/*     */     }
/*  82 */     Object a = Array.newInstance(this.type.getType(cl), 0);
/*  83 */     return a.getClass();
/*     */   }
/*     */ 
/*     */   public MemberValue getType()
/*     */   {
/*  92 */     return this.type;
/*     */   }
/*     */ 
/*     */   public MemberValue[] getValue()
/*     */   {
/*  99 */     return this.values;
/*     */   }
/*     */ 
/*     */   public void setValue(MemberValue[] elements)
/*     */   {
/* 106 */     this.values = elements;
/* 107 */     if ((elements != null) && (elements.length > 0))
/* 108 */       this.type = elements[0];
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 115 */     StringBuffer buf = new StringBuffer("{");
/* 116 */     if (this.values != null) {
/* 117 */       for (int i = 0; i < this.values.length; i++) {
/* 118 */         buf.append(this.values[i].toString());
/* 119 */         if (i + 1 < this.values.length) {
/* 120 */           buf.append(", ");
/*     */         }
/*     */       }
/*     */     }
/* 124 */     buf.append("}");
/* 125 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public void write(AnnotationsWriter writer)
/*     */     throws IOException
/*     */   {
/* 132 */     int num = this.values.length;
/* 133 */     writer.arrayValue(num);
/* 134 */     for (int i = 0; i < num; i++)
/* 135 */       this.values[i].write(writer);
/*     */   }
/*     */ 
/*     */   public void accept(MemberValueVisitor visitor)
/*     */   {
/* 142 */     visitor.visitArrayMemberValue(this);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.ArrayMemberValue
 * JD-Core Version:    0.6.2
 */