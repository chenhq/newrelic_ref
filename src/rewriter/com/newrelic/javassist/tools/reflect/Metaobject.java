/*     */ package com.newrelic.javassist.tools.reflect;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class Metaobject
/*     */   implements Serializable
/*     */ {
/*     */   protected ClassMetaobject classmetaobject;
/*     */   protected Metalevel baseobject;
/*     */   protected Method[] methods;
/*     */ 
/*     */   public Metaobject(Object self, Object[] args)
/*     */   {
/*  60 */     this.baseobject = ((Metalevel)self);
/*  61 */     this.classmetaobject = this.baseobject._getClass();
/*  62 */     this.methods = this.classmetaobject.getReflectiveMethods();
/*     */   }
/*     */ 
/*     */   protected Metaobject()
/*     */   {
/*  71 */     this.baseobject = null;
/*  72 */     this.classmetaobject = null;
/*  73 */     this.methods = null;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/*  77 */     out.writeObject(this.baseobject);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  83 */     this.baseobject = ((Metalevel)in.readObject());
/*  84 */     this.classmetaobject = this.baseobject._getClass();
/*  85 */     this.methods = this.classmetaobject.getReflectiveMethods();
/*     */   }
/*     */ 
/*     */   public final ClassMetaobject getClassMetaobject()
/*     */   {
/*  94 */     return this.classmetaobject;
/*     */   }
/*     */ 
/*     */   public final Object getObject()
/*     */   {
/* 101 */     return this.baseobject;
/*     */   }
/*     */ 
/*     */   public final void setObject(Object self)
/*     */   {
/* 110 */     this.baseobject = ((Metalevel)self);
/* 111 */     this.classmetaobject = this.baseobject._getClass();
/* 112 */     this.methods = this.classmetaobject.getReflectiveMethods();
/*     */ 
/* 115 */     this.baseobject._setMetaobject(this);
/*     */   }
/*     */ 
/*     */   public final String getMethodName(int identifier)
/*     */   {
/* 123 */     String mname = this.methods[identifier].getName();
/* 124 */     int j = 3;
/*     */     while (true) {
/* 126 */       char c = mname.charAt(j++);
/* 127 */       if ((c < '0') || ('9' < c)) {
/*     */         break;
/*     */       }
/*     */     }
/* 131 */     return mname.substring(j);
/*     */   }
/*     */ 
/*     */   public final Class[] getParameterTypes(int identifier)
/*     */   {
/* 140 */     return this.methods[identifier].getParameterTypes();
/*     */   }
/*     */ 
/*     */   public final Class getReturnType(int identifier)
/*     */   {
/* 148 */     return this.methods[identifier].getReturnType();
/*     */   }
/*     */ 
/*     */   public Object trapFieldRead(String name)
/*     */   {
/* 159 */     Class jc = getClassMetaobject().getJavaClass();
/*     */     try {
/* 161 */       return jc.getField(name).get(getObject());
/*     */     }
/*     */     catch (NoSuchFieldException e) {
/* 164 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 167 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void trapFieldWrite(String name, Object value)
/*     */   {
/* 179 */     Class jc = getClassMetaobject().getJavaClass();
/*     */     try {
/* 181 */       jc.getField(name).set(getObject(), value);
/*     */     }
/*     */     catch (NoSuchFieldException e) {
/* 184 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 187 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object trapMethodcall(int identifier, Object[] args)
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 227 */       return this.methods[identifier].invoke(getObject(), args);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 230 */       throw e.getTargetException();
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 233 */       throw new CannotInvokeException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.Metaobject
 * JD-Core Version:    0.6.2
 */