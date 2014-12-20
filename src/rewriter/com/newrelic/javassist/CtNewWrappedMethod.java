/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.AccessFlag;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.SyntheticAttribute;
/*     */ import com.newrelic.javassist.compiler.JvstCodeGen;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ class CtNewWrappedMethod
/*     */ {
/*     */   private static final String addedWrappedMethod = "_added_m$";
/*     */ 
/*     */   public static CtMethod wrapped(CtClass returnType, String mname, CtClass[] parameterTypes, CtClass[] exceptionTypes, CtMethod body, CtMethod.ConstParameter constParam, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/*  34 */     CtMethod mt = new CtMethod(returnType, mname, parameterTypes, declaring);
/*     */ 
/*  36 */     mt.setModifiers(body.getModifiers());
/*     */     try {
/*  38 */       mt.setExceptionTypes(exceptionTypes);
/*     */     }
/*     */     catch (NotFoundException e) {
/*  41 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/*  44 */     Bytecode code = makeBody(declaring, declaring.getClassFile2(), body, parameterTypes, returnType, constParam);
/*     */ 
/*  46 */     mt.getMethodInfo2().setCodeAttribute(code.toCodeAttribute());
/*  47 */     return mt;
/*     */   }
/*     */ 
/*     */   static Bytecode makeBody(CtClass clazz, ClassFile classfile, CtMethod wrappedBody, CtClass[] parameters, CtClass returnType, CtMethod.ConstParameter cparam)
/*     */     throws CannotCompileException
/*     */   {
/*  57 */     boolean isStatic = Modifier.isStatic(wrappedBody.getModifiers());
/*  58 */     Bytecode code = new Bytecode(classfile.getConstPool(), 0, 0);
/*  59 */     int stacksize = makeBody0(clazz, classfile, wrappedBody, isStatic, parameters, returnType, cparam, code);
/*     */ 
/*  61 */     code.setMaxStack(stacksize);
/*  62 */     code.setMaxLocals(isStatic, parameters, 0);
/*  63 */     return code;
/*     */   }
/*     */ 
/*     */   protected static int makeBody0(CtClass clazz, ClassFile classfile, CtMethod wrappedBody, boolean isStatic, CtClass[] parameters, CtClass returnType, CtMethod.ConstParameter cparam, Bytecode code)
/*     */     throws CannotCompileException
/*     */   {
/*  76 */     if (!(clazz instanceof CtClassType)) {
/*  77 */       throw new CannotCompileException("bad declaring class" + clazz.getName());
/*     */     }
/*     */ 
/*  80 */     if (!isStatic) {
/*  81 */       code.addAload(0);
/*     */     }
/*  83 */     int stacksize = compileParameterList(code, parameters, isStatic ? 0 : 1);
/*     */     String desc;
/*     */     int stacksize2;
/*     */     String desc;
/*  87 */     if (cparam == null) {
/*  88 */       int stacksize2 = 0;
/*  89 */       desc = CtMethod.ConstParameter.defaultDescriptor();
/*     */     }
/*     */     else {
/*  92 */       stacksize2 = cparam.compile(code);
/*  93 */       desc = cparam.descriptor();
/*     */     }
/*     */ 
/*  96 */     checkSignature(wrappedBody, desc);
/*     */     String bodyname;
/*     */     try {
/* 100 */       bodyname = addBodyMethod((CtClassType)clazz, classfile, wrappedBody);
/*     */     }
/*     */     catch (BadBytecode e)
/*     */     {
/* 107 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/* 110 */     if (isStatic)
/* 111 */       code.addInvokestatic(Bytecode.THIS, bodyname, desc);
/*     */     else {
/* 113 */       code.addInvokespecial(Bytecode.THIS, bodyname, desc);
/*     */     }
/* 115 */     compileReturn(code, returnType);
/*     */ 
/* 117 */     if (stacksize < stacksize2 + 2) {
/* 118 */       stacksize = stacksize2 + 2;
/*     */     }
/* 120 */     return stacksize;
/*     */   }
/*     */ 
/*     */   private static void checkSignature(CtMethod wrappedBody, String descriptor)
/*     */     throws CannotCompileException
/*     */   {
/* 127 */     if (!descriptor.equals(wrappedBody.getMethodInfo2().getDescriptor()))
/* 128 */       throw new CannotCompileException("wrapped method with a bad signature: " + wrappedBody.getDeclaringClass().getName() + '.' + wrappedBody.getName());
/*     */   }
/*     */ 
/*     */   private static String addBodyMethod(CtClassType clazz, ClassFile classfile, CtMethod src)
/*     */     throws BadBytecode, CannotCompileException
/*     */   {
/* 139 */     Hashtable bodies = clazz.getHiddenMethods();
/* 140 */     String bodyname = (String)bodies.get(src);
/* 141 */     if (bodyname == null) {
/*     */       do
/* 143 */         bodyname = "_added_m$" + clazz.getUniqueNumber();
/* 144 */       while (classfile.getMethod(bodyname) != null);
/* 145 */       ClassMap map = new ClassMap();
/* 146 */       map.put(src.getDeclaringClass().getName(), clazz.getName());
/* 147 */       MethodInfo body = new MethodInfo(classfile.getConstPool(), bodyname, src.getMethodInfo2(), map);
/*     */ 
/* 150 */       int acc = body.getAccessFlags();
/* 151 */       body.setAccessFlags(AccessFlag.setPrivate(acc));
/* 152 */       body.addAttribute(new SyntheticAttribute(classfile.getConstPool()));
/*     */ 
/* 154 */       classfile.addMethod(body);
/* 155 */       bodies.put(src, bodyname);
/* 156 */       CtMember.Cache cache = clazz.hasMemberCache();
/* 157 */       if (cache != null) {
/* 158 */         cache.addMethod(new CtMethod(body, clazz));
/*     */       }
/*     */     }
/* 161 */     return bodyname;
/*     */   }
/*     */ 
/*     */   static int compileParameterList(Bytecode code, CtClass[] params, int regno)
/*     */   {
/* 173 */     return JvstCodeGen.compileParameterList(code, params, regno);
/*     */   }
/*     */ 
/*     */   private static void compileReturn(Bytecode code, CtClass type)
/*     */   {
/* 180 */     if (type.isPrimitive()) {
/* 181 */       CtPrimitiveType pt = (CtPrimitiveType)type;
/* 182 */       if (pt != CtClass.voidType) {
/* 183 */         String wrapper = pt.getWrapperName();
/* 184 */         code.addCheckcast(wrapper);
/* 185 */         code.addInvokevirtual(wrapper, pt.getGetMethodName(), pt.getGetMethodDescriptor());
/*     */       }
/*     */ 
/* 189 */       code.addOpcode(pt.getReturnOp());
/*     */     }
/*     */     else {
/* 192 */       code.addCheckcast(type);
/* 193 */       code.addOpcode(176);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtNewWrappedMethod
 * JD-Core Version:    0.6.2
 */