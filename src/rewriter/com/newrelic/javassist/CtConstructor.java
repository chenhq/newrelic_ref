/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator.Gap;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.Descriptor.Iterator;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ import com.newrelic.javassist.compiler.Javac;
/*     */ 
/*     */ public final class CtConstructor extends CtBehavior
/*     */ {
/*     */   protected CtConstructor(MethodInfo minfo, CtClass declaring)
/*     */   {
/*  37 */     super(declaring, minfo);
/*     */   }
/*     */ 
/*     */   public CtConstructor(CtClass[] parameters, CtClass declaring)
/*     */   {
/*  56 */     this((MethodInfo)null, declaring);
/*  57 */     ConstPool cp = declaring.getClassFile2().getConstPool();
/*  58 */     String desc = Descriptor.ofConstructor(parameters);
/*  59 */     this.methodInfo = new MethodInfo(cp, "<init>", desc);
/*  60 */     setModifiers(1);
/*     */   }
/*     */ 
/*     */   public CtConstructor(CtConstructor src, CtClass declaring, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 100 */     this((MethodInfo)null, declaring);
/* 101 */     copy(src, true, map);
/*     */   }
/*     */ 
/*     */   public boolean isConstructor()
/*     */   {
/* 108 */     return this.methodInfo.isConstructor();
/*     */   }
/*     */ 
/*     */   public boolean isClassInitializer()
/*     */   {
/* 115 */     return this.methodInfo.isStaticInitializer();
/*     */   }
/*     */ 
/*     */   public String getLongName()
/*     */   {
/* 125 */     return getDeclaringClass().getName() + (isConstructor() ? Descriptor.toString(getSignature()) : ".<clinit>()");
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 137 */     if (this.methodInfo.isStaticInitializer()) {
/* 138 */       return "<clinit>";
/*     */     }
/* 140 */     return this.declaringClass.getSimpleName();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 151 */     CodeAttribute ca = getMethodInfo2().getCodeAttribute();
/* 152 */     if (ca == null) {
/* 153 */       return false;
/*     */     }
/*     */ 
/* 156 */     ConstPool cp = ca.getConstPool();
/* 157 */     CodeIterator it = ca.iterator();
/*     */     try
/*     */     {
/* 160 */       int op0 = it.byteAt(it.next());
/*     */       int pos;
/*     */       int desc;
/* 161 */       return (op0 == 177) || ((op0 == 42) && (it.byteAt(pos = it.next()) == 183) && ((desc = cp.isConstructor(getSuperclassName(), it.u16bitAt(pos + 1))) != 0) && ("()V".equals(cp.getUtf8Info(desc))) && (it.byteAt(it.next()) == 177) && (!it.hasNext()));
/*     */     }
/*     */     catch (BadBytecode e)
/*     */     {
/*     */     }
/*     */ 
/* 171 */     return false;
/*     */   }
/*     */ 
/*     */   private String getSuperclassName() {
/* 175 */     ClassFile cf = this.declaringClass.getClassFile2();
/* 176 */     return cf.getSuperclass();
/*     */   }
/*     */ 
/*     */   public boolean callsSuper()
/*     */     throws CannotCompileException
/*     */   {
/* 185 */     CodeAttribute codeAttr = this.methodInfo.getCodeAttribute();
/* 186 */     if (codeAttr != null) {
/* 187 */       CodeIterator it = codeAttr.iterator();
/*     */       try {
/* 189 */         int index = it.skipSuperConstructor();
/* 190 */         return index >= 0;
/*     */       }
/*     */       catch (BadBytecode e) {
/* 193 */         throw new CannotCompileException(e);
/*     */       }
/*     */     }
/*     */ 
/* 197 */     return false;
/*     */   }
/*     */ 
/*     */   public void setBody(String src)
/*     */     throws CannotCompileException
/*     */   {
/* 210 */     if (src == null) {
/* 211 */       if (isClassInitializer())
/* 212 */         src = ";";
/*     */       else
/* 214 */         src = "super();";
/*     */     }
/* 216 */     super.setBody(src);
/*     */   }
/*     */ 
/*     */   public void setBody(CtConstructor src, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 234 */     setBody0(src.declaringClass, src.methodInfo, this.declaringClass, this.methodInfo, map);
/*     */   }
/*     */ 
/*     */   public void insertBeforeBody(String src)
/*     */     throws CannotCompileException
/*     */   {
/* 247 */     CtClass cc = this.declaringClass;
/* 248 */     cc.checkModify();
/* 249 */     if (isClassInitializer()) {
/* 250 */       throw new CannotCompileException("class initializer");
/*     */     }
/* 252 */     CodeAttribute ca = this.methodInfo.getCodeAttribute();
/* 253 */     CodeIterator iterator = ca.iterator();
/* 254 */     Bytecode b = new Bytecode(this.methodInfo.getConstPool(), ca.getMaxStack(), ca.getMaxLocals());
/*     */ 
/* 256 */     b.setStackDepth(ca.getMaxStack());
/* 257 */     Javac jv = new Javac(b, cc);
/*     */     try {
/* 259 */       jv.recordParams(getParameterTypes(), false);
/* 260 */       jv.compileStmnt(src);
/* 261 */       ca.setMaxStack(b.getMaxStack());
/* 262 */       ca.setMaxLocals(b.getMaxLocals());
/* 263 */       iterator.skipConstructor();
/* 264 */       int pos = iterator.insertEx(b.get());
/* 265 */       iterator.insert(b.getExceptionTable(), pos);
/* 266 */       this.methodInfo.rebuildStackMapIf6(cc.getClassPool(), cc.getClassFile2());
/*     */     }
/*     */     catch (NotFoundException e) {
/* 269 */       throw new CannotCompileException(e);
/*     */     }
/*     */     catch (CompileError e) {
/* 272 */       throw new CannotCompileException(e);
/*     */     }
/*     */     catch (BadBytecode e) {
/* 275 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   int getStartPosOfBody(CodeAttribute ca)
/*     */     throws CannotCompileException
/*     */   {
/* 283 */     CodeIterator ci = ca.iterator();
/*     */     try {
/* 285 */       ci.skipConstructor();
/* 286 */       return ci.next();
/*     */     }
/*     */     catch (BadBytecode e) {
/* 289 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public CtMethod toMethod(String name, CtClass declaring)
/*     */     throws CannotCompileException
/*     */   {
/* 316 */     return toMethod(name, declaring, null);
/*     */   }
/*     */ 
/*     */   public CtMethod toMethod(String name, CtClass declaring, ClassMap map)
/*     */     throws CannotCompileException
/*     */   {
/* 350 */     CtMethod method = new CtMethod(null, declaring);
/* 351 */     method.copy(this, false, map);
/* 352 */     if (isConstructor()) {
/* 353 */       MethodInfo minfo = method.getMethodInfo2();
/* 354 */       CodeAttribute ca = minfo.getCodeAttribute();
/* 355 */       if (ca != null) {
/* 356 */         removeConsCall(ca);
/*     */         try {
/* 358 */           this.methodInfo.rebuildStackMapIf6(declaring.getClassPool(), declaring.getClassFile2());
/*     */         }
/*     */         catch (BadBytecode e)
/*     */         {
/* 362 */           throw new CannotCompileException(e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 367 */     method.setName(name);
/* 368 */     return method;
/*     */   }
/*     */ 
/*     */   private static void removeConsCall(CodeAttribute ca)
/*     */     throws CannotCompileException
/*     */   {
/* 374 */     CodeIterator iterator = ca.iterator();
/*     */     try {
/* 376 */       int pos = iterator.skipConstructor();
/* 377 */       if (pos >= 0) {
/* 378 */         int mref = iterator.u16bitAt(pos + 1);
/* 379 */         String desc = ca.getConstPool().getMethodrefType(mref);
/* 380 */         int num = Descriptor.numOfParameters(desc) + 1;
/* 381 */         if (num > 3) {
/* 382 */           pos = iterator.insertGapAt(pos, num - 3, false).position;
/*     */         }
/* 384 */         iterator.writeByte(87, pos++);
/* 385 */         iterator.writeByte(0, pos);
/* 386 */         iterator.writeByte(0, pos + 1);
/* 387 */         Descriptor.Iterator it = new Descriptor.Iterator(desc);
/*     */         while (true) {
/* 389 */           it.next();
/* 390 */           if (!it.isParameter()) break;
/* 391 */           iterator.writeByte(it.is2byte() ? 88 : 87, pos++);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (BadBytecode e)
/*     */     {
/* 399 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtConstructor
 * JD-Core Version:    0.6.2
 */