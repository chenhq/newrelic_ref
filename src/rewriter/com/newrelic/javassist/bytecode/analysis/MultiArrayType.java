/*     */ package com.newrelic.javassist.bytecode.analysis;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ 
/*     */ public class MultiArrayType extends Type
/*     */ {
/*     */   private MultiType component;
/*     */   private int dims;
/*     */ 
/*     */   public MultiArrayType(MultiType component, int dims)
/*     */   {
/*  31 */     super(null);
/*  32 */     this.component = component;
/*  33 */     this.dims = dims;
/*     */   }
/*     */ 
/*     */   public CtClass getCtClass() {
/*  37 */     CtClass clazz = this.component.getCtClass();
/*  38 */     if (clazz == null) {
/*  39 */       return null;
/*     */     }
/*  41 */     ClassPool pool = clazz.getClassPool();
/*  42 */     if (pool == null) {
/*  43 */       pool = ClassPool.getDefault();
/*     */     }
/*  45 */     String name = arrayName(clazz.getName(), this.dims);
/*     */     try
/*     */     {
/*  48 */       return pool.get(name);
/*     */     } catch (NotFoundException e) {
/*  50 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean popChanged() {
/*  55 */     return this.component.popChanged();
/*     */   }
/*     */ 
/*     */   public int getDimensions() {
/*  59 */     return this.dims;
/*     */   }
/*     */ 
/*     */   public Type getComponent() {
/*  63 */     return this.dims == 1 ? this.component : new MultiArrayType(this.component, this.dims - 1);
/*     */   }
/*     */ 
/*     */   public int getSize() {
/*  67 */     return 1;
/*     */   }
/*     */ 
/*     */   public boolean isArray() {
/*  71 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isAssignableFrom(Type type) {
/*  75 */     throw new UnsupportedOperationException("Not implemented");
/*     */   }
/*     */ 
/*     */   public boolean isReference() {
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isAssignableTo(Type type) {
/*  83 */     if (eq(type.getCtClass(), Type.OBJECT.getCtClass())) {
/*  84 */       return true;
/*     */     }
/*  86 */     if (eq(type.getCtClass(), Type.CLONEABLE.getCtClass())) {
/*  87 */       return true;
/*     */     }
/*  89 */     if (eq(type.getCtClass(), Type.SERIALIZABLE.getCtClass())) {
/*  90 */       return true;
/*     */     }
/*  92 */     if (!type.isArray()) {
/*  93 */       return false;
/*     */     }
/*  95 */     Type typeRoot = getRootComponent(type);
/*  96 */     int typeDims = type.getDimensions();
/*     */ 
/*  98 */     if (typeDims > this.dims) {
/*  99 */       return false;
/*     */     }
/* 101 */     if (typeDims < this.dims) {
/* 102 */       if (eq(typeRoot.getCtClass(), Type.OBJECT.getCtClass())) {
/* 103 */         return true;
/*     */       }
/* 105 */       if (eq(typeRoot.getCtClass(), Type.CLONEABLE.getCtClass())) {
/* 106 */         return true;
/*     */       }
/* 108 */       if (eq(typeRoot.getCtClass(), Type.SERIALIZABLE.getCtClass())) {
/* 109 */         return true;
/*     */       }
/* 111 */       return false;
/*     */     }
/*     */ 
/* 114 */     return this.component.isAssignableTo(typeRoot);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 118 */     if (!(o instanceof MultiArrayType))
/* 119 */       return false;
/* 120 */     MultiArrayType multi = (MultiArrayType)o;
/*     */ 
/* 122 */     return (this.component.equals(multi.component)) && (this.dims == multi.dims);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 127 */     return arrayName(this.component.toString(), this.dims);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.MultiArrayType
 * JD-Core Version:    0.6.2
 */