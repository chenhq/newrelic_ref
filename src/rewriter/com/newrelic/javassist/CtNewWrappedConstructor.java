/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ import com.newrelic.javassist.bytecode.Bytecode;
/*    */ import com.newrelic.javassist.bytecode.ClassFile;
/*    */ import com.newrelic.javassist.bytecode.Descriptor;
/*    */ import com.newrelic.javassist.bytecode.MethodInfo;
/*    */ 
/*    */ class CtNewWrappedConstructor extends CtNewWrappedMethod
/*    */ {
/*    */   private static final int PASS_NONE = 0;
/*    */   private static final int PASS_PARAMS = 2;
/*    */ 
/*    */   public static CtConstructor wrapped(CtClass[] parameterTypes, CtClass[] exceptionTypes, int howToCallSuper, CtMethod body, CtMethod.ConstParameter constParam, CtClass declaring)
/*    */     throws CannotCompileException
/*    */   {
/*    */     try
/*    */     {
/* 35 */       CtConstructor cons = new CtConstructor(parameterTypes, declaring);
/* 36 */       cons.setExceptionTypes(exceptionTypes);
/* 37 */       Bytecode code = makeBody(declaring, declaring.getClassFile2(), howToCallSuper, body, parameterTypes, constParam);
/*    */ 
/* 40 */       cons.getMethodInfo2().setCodeAttribute(code.toCodeAttribute());
/* 41 */       return cons;
/*    */     }
/*    */     catch (NotFoundException e) {
/* 44 */       throw new CannotCompileException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected static Bytecode makeBody(CtClass declaring, ClassFile classfile, int howToCallSuper, CtMethod wrappedBody, CtClass[] parameters, CtMethod.ConstParameter cparam)
/*    */     throws CannotCompileException
/*    */   {
/* 57 */     int superclazz = classfile.getSuperclassId();
/* 58 */     Bytecode code = new Bytecode(classfile.getConstPool(), 0, 0);
/* 59 */     code.setMaxLocals(false, parameters, 0);
/* 60 */     code.addAload(0);
/*    */     int stacksize;
/* 61 */     if (howToCallSuper == 0) {
/* 62 */       int stacksize = 1;
/* 63 */       code.addInvokespecial(superclazz, "<init>", "()V");
/*    */     }
/* 65 */     else if (howToCallSuper == 2) {
/* 66 */       int stacksize = code.addLoadParameters(parameters, 1) + 1;
/* 67 */       code.addInvokespecial(superclazz, "<init>", Descriptor.ofConstructor(parameters));
/*    */     }
/*    */     else
/*    */     {
/* 71 */       stacksize = compileParameterList(code, parameters, 1);
/*    */       String desc;
/*    */       int stacksize2;
/*    */       String desc;
/* 73 */       if (cparam == null) {
/* 74 */         int stacksize2 = 2;
/* 75 */         desc = CtMethod.ConstParameter.defaultConstDescriptor();
/*    */       }
/*    */       else {
/* 78 */         stacksize2 = cparam.compile(code) + 2;
/* 79 */         desc = cparam.constDescriptor();
/*    */       }
/*    */ 
/* 82 */       if (stacksize < stacksize2) {
/* 83 */         stacksize = stacksize2;
/*    */       }
/* 85 */       code.addInvokespecial(superclazz, "<init>", desc);
/*    */     }
/*    */ 
/* 88 */     if (wrappedBody == null) {
/* 89 */       code.add(177);
/*    */     } else {
/* 91 */       int stacksize2 = makeBody0(declaring, classfile, wrappedBody, false, parameters, CtClass.voidType, cparam, code);
/*    */ 
/* 94 */       if (stacksize < stacksize2) {
/* 95 */         stacksize = stacksize2;
/*    */       }
/*    */     }
/* 98 */     code.setMaxStack(stacksize);
/* 99 */     return code;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtNewWrappedConstructor
 * JD-Core Version:    0.6.2
 */