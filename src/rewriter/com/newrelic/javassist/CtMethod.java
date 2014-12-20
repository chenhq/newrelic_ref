/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ 
/*     */ public final class CtMethod extends CtBehavior
/*     */ {
/*     */   protected String cachedStringRep;
/*     */ 
/*     */   CtMethod(MethodInfo minfo, CtClass declaring)
/*     */   {
/*  37 */     super(declaring, minfo);
/*  38 */     this.cachedStringRep = null;
/*     */   }
/*     */ 
/*     */   public CtMethod(CtClass returnType, String mname, CtClass[] parameters, CtClass declaring)
/*     */   {
/*  54 */     this(null, declaring);
/*  55 */     ConstPool cp = declaring.getClassFile2().getConstPool();
/*  56 */     String desc = Descriptor.ofMethod(returnType, parameters);
/*  57 */     this.methodInfo = new MethodInfo(cp, mname, desc);
/*  58 */     setModifiers(1025);
/*     */   }
/*     */ 
/*     */   public CtMethod(CtMethod src, CtClass declaring, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 112 */     this(null, declaring);
/* 113 */     copy(src, false, map);
/*     */   }
/*     */ 
/*     */   public static CtMethod make(String src, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 129 */     return CtNewMethod.make(src, declaring);
/*     */   }
/*     */ 
/*     */   public static CtMethod make(MethodInfo minfo, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 144 */     if (declaring.getClassFile2().getConstPool() != minfo.getConstPool()) {
/* 145 */       throw new CannotCompileException("bad declaring class");
/*     */     }
/* 147 */     return new CtMethod(minfo, declaring);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 156 */     return getStringRep().hashCode();
/*     */   }
/*     */ 
/*     */   void nameReplaced()
/*     */   {
/* 164 */     this.cachedStringRep = null;
/*     */   }
/*     */ 
/*     */   final String getStringRep()
/*     */   {
/* 170 */     if (this.cachedStringRep == null) {
/* 171 */       this.cachedStringRep = (this.methodInfo.getName() + Descriptor.getParamDescriptor(this.methodInfo.getDescriptor()));
/*     */     }
/*     */ 
/* 174 */     return this.cachedStringRep;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 182 */     return (obj != null) && ((obj instanceof CtMethod)) && (((CtMethod)obj).getStringRep().equals(getStringRep()));
/*     */   }
/*     */ 
/*     */   public String getLongName()
/*     */   {
/* 193 */     return getDeclaringClass().getName() + "." + getName() + Descriptor.toString(getSignature());
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 201 */     return this.methodInfo.getName();
/*     */   }
/*     */ 
/*     */   public void setName(String newname)
/*     */   {
/* 208 */     this.declaringClass.checkModify();
/* 209 */     this.methodInfo.setName(newname);
/*     */   }
/*     */ 
/*     */   public CtClass getReturnType()
/*     */     throws NotFoundException
/*     */   {
/* 216 */     return getReturnType0();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 224 */     CodeAttribute ca = getMethodInfo2().getCodeAttribute();
/* 225 */     if (ca == null) {
/* 226 */       return (getModifiers() & 0x400) != 0;
/*     */     }
/* 228 */     CodeIterator it = ca.iterator();
/*     */     try {
/* 230 */       return (it.hasNext()) && (it.byteAt(it.next()) == 177) && (!it.hasNext());
/*     */     }
/*     */     catch (BadBytecode e) {
/*     */     }
/* 234 */     return false;
/*     */   }
/*     */ 
/*     */   public void setBody(CtMethod src, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 254 */     setBody0(src.declaringClass, src.methodInfo, this.declaringClass, this.methodInfo, map);
/*     */   }
/*     */ 
/*     */   public void setWrappedBody(CtMethod mbody, ConstParameter constParam)
/*     */     throws CannotCompileException
/*     */   {
/* 272 */     this.declaringClass.checkModify();
/*     */ 
/* 274 */     CtClass clazz = getDeclaringClass();
/*     */     CtClass[] params;
/*     */     CtClass retType;
/*     */     try
/*     */     {
/* 278 */       params = getParameterTypes();
/* 279 */       retType = getReturnType();
/*     */     }
/*     */     catch (NotFoundException e) {
/* 282 */       throw new CannotCompileException(e);
/*     */     }
/*     */ 
/* 285 */     Bytecode code = CtNewWrappedMethod.makeBody(clazz, clazz.getClassFile2(), mbody, params, retType, constParam);
/*     */ 
/* 290 */     CodeAttribute cattr = code.toCodeAttribute();
/* 291 */     this.methodInfo.setCodeAttribute(cattr);
/* 292 */     this.methodInfo.setAccessFlags(this.methodInfo.getAccessFlags() & 0xFFFFFBFF);
/*     */   }
/*     */ 
/*     */   static class StringConstParameter extends CtMethod.ConstParameter
/*     */   {
/*     */     String param;
/*     */ 
/*     */     StringConstParameter(String s)
/*     */     {
/* 419 */       this.param = s;
/*     */     }
/*     */ 
/*     */     int compile(Bytecode code) throws CannotCompileException {
/* 423 */       code.addLdc(this.param);
/* 424 */       return 1;
/*     */     }
/*     */ 
/*     */     String descriptor() {
/* 428 */       return "([Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;";
/*     */     }
/*     */ 
/*     */     String constDescriptor() {
/* 432 */       return "([Ljava/lang/Object;Ljava/lang/String;)V";
/*     */     }
/*     */   }
/*     */ 
/*     */   static class LongConstParameter extends CtMethod.ConstParameter
/*     */   {
/*     */     long param;
/*     */ 
/*     */     LongConstParameter(long l)
/*     */     {
/* 398 */       this.param = l;
/*     */     }
/*     */ 
/*     */     int compile(Bytecode code) throws CannotCompileException {
/* 402 */       code.addLconst(this.param);
/* 403 */       return 2;
/*     */     }
/*     */ 
/*     */     String descriptor() {
/* 407 */       return "([Ljava/lang/Object;J)Ljava/lang/Object;";
/*     */     }
/*     */ 
/*     */     String constDescriptor() {
/* 411 */       return "([Ljava/lang/Object;J)V";
/*     */     }
/*     */   }
/*     */ 
/*     */   static class IntConstParameter extends CtMethod.ConstParameter
/*     */   {
/*     */     int param;
/*     */ 
/*     */     IntConstParameter(int i)
/*     */     {
/* 377 */       this.param = i;
/*     */     }
/*     */ 
/*     */     int compile(Bytecode code) throws CannotCompileException {
/* 381 */       code.addIconst(this.param);
/* 382 */       return 1;
/*     */     }
/*     */ 
/*     */     String descriptor() {
/* 386 */       return "([Ljava/lang/Object;I)Ljava/lang/Object;";
/*     */     }
/*     */ 
/*     */     String constDescriptor() {
/* 390 */       return "([Ljava/lang/Object;I)V";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ConstParameter
/*     */   {
/*     */     public static ConstParameter integer(int i)
/*     */     {
/* 315 */       return new CtMethod.IntConstParameter(i);
/*     */     }
/*     */ 
/*     */     public static ConstParameter integer(long i)
/*     */     {
/* 324 */       return new CtMethod.LongConstParameter(i);
/*     */     }
/*     */ 
/*     */     public static ConstParameter string(String s)
/*     */     {
/* 333 */       return new CtMethod.StringConstParameter(s);
/*     */     }
/*     */ 
/*     */     int compile(Bytecode code)
/*     */       throws CannotCompileException
/*     */     {
/* 342 */       return 0;
/*     */     }
/*     */ 
/*     */     String descriptor() {
/* 346 */       return defaultDescriptor();
/*     */     }
/*     */ 
/*     */     static String defaultDescriptor()
/*     */     {
/* 353 */       return "([Ljava/lang/Object;)Ljava/lang/Object;";
/*     */     }
/*     */ 
/*     */     String constDescriptor()
/*     */     {
/* 362 */       return defaultConstDescriptor();
/*     */     }
/*     */ 
/*     */     static String defaultConstDescriptor()
/*     */     {
/* 369 */       return "([Ljava/lang/Object;)V";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtMethod
 * JD-Core Version:    0.6.2
 */