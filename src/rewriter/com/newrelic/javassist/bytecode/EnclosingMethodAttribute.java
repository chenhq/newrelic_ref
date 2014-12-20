/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class EnclosingMethodAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "EnclosingMethod";
/*     */ 
/*     */   EnclosingMethodAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  34 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   public EnclosingMethodAttribute(ConstPool cp, String className, String methodName, String methodDesc)
/*     */   {
/*  47 */     super(cp, "EnclosingMethod");
/*  48 */     int ci = cp.addClassInfo(className);
/*  49 */     int ni = cp.addNameAndTypeInfo(methodName, methodDesc);
/*  50 */     byte[] bvalue = new byte[4];
/*  51 */     bvalue[0] = ((byte)(ci >>> 8));
/*  52 */     bvalue[1] = ((byte)ci);
/*  53 */     bvalue[2] = ((byte)(ni >>> 8));
/*  54 */     bvalue[3] = ((byte)ni);
/*  55 */     set(bvalue);
/*     */   }
/*     */ 
/*     */   public EnclosingMethodAttribute(ConstPool cp, String className)
/*     */   {
/*  66 */     super(cp, "EnclosingMethod");
/*  67 */     int ci = cp.addClassInfo(className);
/*  68 */     int ni = 0;
/*  69 */     byte[] bvalue = new byte[4];
/*  70 */     bvalue[0] = ((byte)(ci >>> 8));
/*  71 */     bvalue[1] = ((byte)ci);
/*  72 */     bvalue[2] = ((byte)(ni >>> 8));
/*  73 */     bvalue[3] = ((byte)ni);
/*  74 */     set(bvalue);
/*     */   }
/*     */ 
/*     */   public int classIndex()
/*     */   {
/*  81 */     return ByteArray.readU16bit(get(), 0);
/*     */   }
/*     */ 
/*     */   public int methodIndex()
/*     */   {
/*  88 */     return ByteArray.readU16bit(get(), 2);
/*     */   }
/*     */ 
/*     */   public String className()
/*     */   {
/*  95 */     return getConstPool().getClassInfo(classIndex());
/*     */   }
/*     */ 
/*     */   public String methodName()
/*     */   {
/* 102 */     ConstPool cp = getConstPool();
/* 103 */     int mi = methodIndex();
/* 104 */     int ni = cp.getNameAndTypeName(mi);
/* 105 */     return cp.getUtf8Info(ni);
/*     */   }
/*     */ 
/*     */   public String methodDescriptor()
/*     */   {
/* 112 */     ConstPool cp = getConstPool();
/* 113 */     int mi = methodIndex();
/* 114 */     int ti = cp.getNameAndTypeDescriptor(mi);
/* 115 */     return cp.getUtf8Info(ti);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 127 */     if (methodIndex() == 0) {
/* 128 */       return new EnclosingMethodAttribute(newCp, className());
/*     */     }
/* 130 */     return new EnclosingMethodAttribute(newCp, className(), methodName(), methodDescriptor());
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.EnclosingMethodAttribute
 * JD-Core Version:    0.6.2
 */