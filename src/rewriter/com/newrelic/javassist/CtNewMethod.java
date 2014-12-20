/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.ExceptionsAttribute;
/*     */ import com.newrelic.javassist.bytecode.FieldInfo;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ import com.newrelic.javassist.compiler.Javac;
/*     */ 
/*     */ public class CtNewMethod
/*     */ {
/*     */   public static CtMethod make(String src, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/*  44 */     return make(src, declaring, null, null);
/*     */   }
/*     */ 
/*     */   public static CtMethod make(String src, CtClass declaring, String delegateObj, String delegateMethod)
/*     */     throws CannotCompileException
/*     */   {
/*  68 */     Javac compiler = new Javac(declaring);
/*     */     try {
/*  70 */       if (delegateMethod != null) {
/*  71 */         compiler.recordProceed(delegateObj, delegateMethod);
/*     */       }
/*  73 */       CtMember obj = compiler.compile(src);
/*  74 */       if ((obj instanceof CtMethod))
/*  75 */         return (CtMethod)obj;
/*     */     }
/*     */     catch (CompileError e) {
/*  78 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/*  81 */     throw new CannotCompileException("not a method");
/*     */   }
/*     */ 
/*     */   public static CtMethod make(CtClass returnType, String mname, CtClass[] parameters, CtClass[] exceptions, String body, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 105 */     return make(1, returnType, mname, parameters, exceptions, body, declaring);
/*     */   }
/*     */ 
/*     */   public static CtMethod make(int modifiers, CtClass returnType, String mname, CtClass[] parameters, CtClass[] exceptions, String body, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 133 */       CtMethod cm = new CtMethod(returnType, mname, parameters, declaring);
/*     */ 
/* 135 */       cm.setModifiers(modifiers);
/* 136 */       cm.setExceptionTypes(exceptions);
/* 137 */       cm.setBody(body);
/* 138 */       return cm;
/*     */     }
/*     */     catch (NotFoundException e) {
/* 141 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CtMethod copy(CtMethod src, CtClass declaring, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 162 */     return new CtMethod(src, declaring, map);
/*     */   }
/*     */ 
/*     */   public static CtMethod copy(CtMethod src, String name, CtClass declaring, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 184 */     CtMethod cm = new CtMethod(src, declaring, map);
/* 185 */     cm.setName(name);
/* 186 */     return cm;
/*     */   }
/*     */ 
/*     */   public static CtMethod abstractMethod(CtClass returnType, String mname, CtClass[] parameters, CtClass[] exceptions, CtClass declaring)
/*     */     throws NotFoundException
/*     */   {
/* 207 */     CtMethod cm = new CtMethod(returnType, mname, parameters, declaring);
/* 208 */     cm.setExceptionTypes(exceptions);
/* 209 */     return cm;
/*     */   }
/*     */ 
/*     */   public static CtMethod getter(String methodName, CtField field)
/*     */     throws CannotCompileException
/*     */   {
/* 224 */     FieldInfo finfo = field.getFieldInfo2();
/* 225 */     String fieldType = finfo.getDescriptor();
/* 226 */     String desc = "()" + fieldType;
/* 227 */     ConstPool cp = finfo.getConstPool();
/* 228 */     MethodInfo minfo = new MethodInfo(cp, methodName, desc);
/* 229 */     minfo.setAccessFlags(1);
/*     */ 
/* 231 */     Bytecode code = new Bytecode(cp, 2, 1);
/*     */     try {
/* 233 */       String fieldName = finfo.getName();
/* 234 */       if ((finfo.getAccessFlags() & 0x8) == 0) {
/* 235 */         code.addAload(0);
/* 236 */         code.addGetfield(Bytecode.THIS, fieldName, fieldType);
/*     */       }
/*     */       else {
/* 239 */         code.addGetstatic(Bytecode.THIS, fieldName, fieldType);
/*     */       }
/* 241 */       code.addReturn(field.getType());
/*     */     }
/*     */     catch (NotFoundException e) {
/* 244 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/* 247 */     minfo.setCodeAttribute(code.toCodeAttribute());
/* 248 */     return new CtMethod(minfo, field.getDeclaringClass());
/*     */   }
/*     */ 
/*     */   public static CtMethod setter(String methodName, CtField field)
/*     */     throws CannotCompileException
/*     */   {
/* 265 */     FieldInfo finfo = field.getFieldInfo2();
/* 266 */     String fieldType = finfo.getDescriptor();
/* 267 */     String desc = "(" + fieldType + ")V";
/* 268 */     ConstPool cp = finfo.getConstPool();
/* 269 */     MethodInfo minfo = new MethodInfo(cp, methodName, desc);
/* 270 */     minfo.setAccessFlags(1);
/*     */ 
/* 272 */     Bytecode code = new Bytecode(cp, 3, 3);
/*     */     try {
/* 274 */       String fieldName = finfo.getName();
/* 275 */       if ((finfo.getAccessFlags() & 0x8) == 0) {
/* 276 */         code.addAload(0);
/* 277 */         code.addLoad(1, field.getType());
/* 278 */         code.addPutfield(Bytecode.THIS, fieldName, fieldType);
/*     */       }
/*     */       else {
/* 281 */         code.addLoad(1, field.getType());
/* 282 */         code.addPutstatic(Bytecode.THIS, fieldName, fieldType);
/*     */       }
/*     */ 
/* 285 */       code.addReturn(null);
/*     */     }
/*     */     catch (NotFoundException e) {
/* 288 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/* 291 */     minfo.setCodeAttribute(code.toCodeAttribute());
/* 292 */     return new CtMethod(minfo, field.getDeclaringClass());
/*     */   }
/*     */ 
/*     */   public static CtMethod delegator(CtMethod delegate, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 320 */       return delegator0(delegate, declaring);
/*     */     }
/*     */     catch (NotFoundException e) {
/* 323 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static CtMethod delegator0(CtMethod delegate, CtClass declaring)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 330 */     MethodInfo deleInfo = delegate.getMethodInfo2();
/* 331 */     String methodName = deleInfo.getName();
/* 332 */     String desc = deleInfo.getDescriptor();
/* 333 */     ConstPool cp = declaring.getClassFile2().getConstPool();
/* 334 */     MethodInfo minfo = new MethodInfo(cp, methodName, desc);
/* 335 */     minfo.setAccessFlags(deleInfo.getAccessFlags());
/*     */ 
/* 337 */     ExceptionsAttribute eattr = deleInfo.getExceptionsAttribute();
/* 338 */     if (eattr != null) {
/* 339 */       minfo.setExceptionsAttribute((ExceptionsAttribute)eattr.copy(cp, null));
/*     */     }
/*     */ 
/* 342 */     Bytecode code = new Bytecode(cp, 0, 0);
/* 343 */     boolean isStatic = Modifier.isStatic(delegate.getModifiers());
/* 344 */     CtClass deleClass = delegate.getDeclaringClass();
/* 345 */     CtClass[] params = delegate.getParameterTypes();
/*     */     int s;
/* 347 */     if (isStatic) {
/* 348 */       int s = code.addLoadParameters(params, 0);
/* 349 */       code.addInvokestatic(deleClass, methodName, desc);
/*     */     }
/*     */     else {
/* 352 */       code.addLoad(0, deleClass);
/* 353 */       s = code.addLoadParameters(params, 1);
/* 354 */       code.addInvokespecial(deleClass, methodName, desc);
/*     */     }
/*     */ 
/* 357 */     code.addReturn(delegate.getReturnType());
/* 358 */     code.setMaxLocals(++s);
/* 359 */     code.setMaxStack(s < 2 ? 2 : s);
/* 360 */     minfo.setCodeAttribute(code.toCodeAttribute());
/* 361 */     return new CtMethod(minfo, declaring);
/*     */   }
/*     */ 
/*     */   public static CtMethod wrapped(CtClass returnType, String mname, CtClass[] parameterTypes, CtClass[] exceptionTypes, CtMethod body, CtMethod.ConstParameter constParam, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 467 */     return CtNewWrappedMethod.wrapped(returnType, mname, parameterTypes, exceptionTypes, body, constParam, declaring);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtNewMethod
 * JD-Core Version:    0.6.2
 */