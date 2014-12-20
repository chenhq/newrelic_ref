/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ import com.newrelic.javassist.compiler.Javac;
/*     */ 
/*     */ public class CtNewConstructor
/*     */ {
/*     */   public static final int PASS_NONE = 0;
/*     */   public static final int PASS_ARRAY = 1;
/*     */   public static final int PASS_PARAMS = 2;
/*     */ 
/*     */   public static CtConstructor make(String src, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/*  67 */     Javac compiler = new Javac(declaring);
/*     */     try {
/*  69 */       CtMember obj = compiler.compile(src);
/*  70 */       if ((obj instanceof CtConstructor))
/*     */       {
/*  72 */         return (CtConstructor)obj;
/*     */       }
/*     */     }
/*     */     catch (CompileError e) {
/*  76 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/*  79 */     throw new CannotCompileException("not a constructor");
/*     */   }
/*     */ 
/*     */   public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, String body, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 100 */       CtConstructor cc = new CtConstructor(parameters, declaring);
/* 101 */       cc.setExceptionTypes(exceptions);
/* 102 */       cc.setBody(body);
/* 103 */       return cc;
/*     */     }
/*     */     catch (NotFoundException e) {
/* 106 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CtConstructor copy(CtConstructor c, CtClass declaring, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 126 */     return new CtConstructor(c, declaring, map);
/*     */   }
/*     */ 
/*     */   public static CtConstructor defaultConstructor(CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 138 */     CtConstructor cons = new CtConstructor((CtClass[])null, declaring);
/*     */ 
/* 140 */     ConstPool cp = declaring.getClassFile2().getConstPool();
/* 141 */     Bytecode code = new Bytecode(cp, 1, 1);
/* 142 */     code.addAload(0);
/*     */     try {
/* 144 */       code.addInvokespecial(declaring.getSuperclass(), "<init>", "()V");
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/* 148 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/* 151 */     code.add(177);
/*     */ 
/* 154 */     cons.getMethodInfo2().setCodeAttribute(code.toCodeAttribute());
/* 155 */     return cons;
/*     */   }
/*     */ 
/*     */   public static CtConstructor skeleton(CtClass[] parameters, CtClass[] exceptions, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 180 */     return make(parameters, exceptions, 0, null, null, declaring);
/*     */   }
/*     */ 
/*     */   public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 199 */     return make(parameters, exceptions, 2, null, null, declaring);
/*     */   }
/*     */ 
/*     */   public static CtConstructor make(CtClass[] parameters, CtClass[] exceptions, int howto, CtMethod body, CtMethod.ConstParameter cparam, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 313 */     return CtNewWrappedConstructor.wrapped(parameters, exceptions, howto, body, cparam, declaring);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtNewConstructor
 * JD-Core Version:    0.6.2
 */