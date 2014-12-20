/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ public final class CtPrimitiveType extends CtClass
/*     */ {
/*     */   private char descriptor;
/*     */   private String wrapperName;
/*     */   private String getMethodName;
/*     */   private String mDescriptor;
/*     */   private int returnOp;
/*     */   private int arrayType;
/*     */   private int dataSize;
/*     */ 
/*     */   CtPrimitiveType(String name, char desc, String wrapper, String methodName, String mDesc, int opcode, int atype, int size)
/*     */   {
/*  34 */     super(name);
/*  35 */     this.descriptor = desc;
/*  36 */     this.wrapperName = wrapper;
/*  37 */     this.getMethodName = methodName;
/*  38 */     this.mDescriptor = mDesc;
/*  39 */     this.returnOp = opcode;
/*  40 */     this.arrayType = atype;
/*  41 */     this.dataSize = size;
/*     */   }
/*     */ 
/*     */   public boolean isPrimitive()
/*     */   {
/*  49 */     return true;
/*     */   }
/*     */ 
/*     */   public int getModifiers()
/*     */   {
/*  58 */     return 17;
/*     */   }
/*     */ 
/*     */   public char getDescriptor()
/*     */   {
/*  65 */     return this.descriptor;
/*     */   }
/*     */ 
/*     */   public String getWrapperName()
/*     */   {
/*  72 */     return this.wrapperName;
/*     */   }
/*     */ 
/*     */   public String getGetMethodName()
/*     */   {
/*  80 */     return this.getMethodName;
/*     */   }
/*     */ 
/*     */   public String getGetMethodDescriptor()
/*     */   {
/*  88 */     return this.mDescriptor;
/*     */   }
/*     */ 
/*     */   public int getReturnOp()
/*     */   {
/*  95 */     return this.returnOp;
/*     */   }
/*     */ 
/*     */   public int getArrayType()
/*     */   {
/* 103 */     return this.arrayType;
/*     */   }
/*     */ 
/*     */   public int getDataSize()
/*     */   {
/* 110 */     return this.dataSize;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtPrimitiveType
 * JD-Core Version:    0.6.2
 */