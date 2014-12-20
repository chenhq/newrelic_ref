/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.ExceptionTable;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ import com.newrelic.javassist.compiler.Javac;
/*     */ 
/*     */ public class Handler extends Expr
/*     */ {
/*  26 */   private static String EXCEPTION_NAME = "$1";
/*     */   private ExceptionTable etable;
/*     */   private int index;
/*     */ 
/*     */   protected Handler(ExceptionTable et, int nth, CodeIterator it, CtClass declaring, MethodInfo m)
/*     */   {
/*  35 */     super(et.handlerPc(nth), it, declaring, m);
/*  36 */     this.etable = et;
/*  37 */     this.index = nth;
/*     */   }
/*     */ 
/*     */   public CtBehavior where()
/*     */   {
/*  43 */     return super.where();
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/*  51 */     return super.getLineNumber();
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  60 */     return super.getFileName();
/*     */   }
/*     */ 
/*     */   public CtClass[] mayThrow()
/*     */   {
/*  67 */     return super.mayThrow();
/*     */   }
/*     */ 
/*     */   public CtClass getType()
/*     */     throws NotFoundException
/*     */   {
/*  75 */     int type = this.etable.catchType(this.index);
/*  76 */     if (type == 0) {
/*  77 */       return null;
/*     */     }
/*  79 */     ConstPool cp = getConstPool();
/*  80 */     String name = cp.getClassInfo(type);
/*  81 */     return this.thisClass.getClassPool().getCtClass(name);
/*     */   }
/*     */ 
/*     */   public boolean isFinally()
/*     */   {
/*  89 */     return this.etable.catchType(this.index) == 0;
/*     */   }
/*     */ 
/*     */   public void replace(String statement)
/*     */     throws CannotCompileException
/*     */   {
/*  98 */     throw new RuntimeException("not implemented yet");
/*     */   }
/*     */ 
/*     */   public void insertBefore(String src)
/*     */     throws CannotCompileException
/*     */   {
/* 109 */     this.edited = true;
/*     */ 
/* 111 */     ConstPool cp = getConstPool();
/* 112 */     CodeAttribute ca = this.iterator.get();
/* 113 */     Javac jv = new Javac(this.thisClass);
/* 114 */     Bytecode b = jv.getBytecode();
/* 115 */     b.setStackDepth(1);
/* 116 */     b.setMaxLocals(ca.getMaxLocals());
/*     */     try
/*     */     {
/* 119 */       CtClass type = getType();
/* 120 */       int var = jv.recordVariable(type, EXCEPTION_NAME);
/* 121 */       jv.recordReturnType(type, false);
/* 122 */       b.addAstore(var);
/* 123 */       jv.compileStmnt(src);
/* 124 */       b.addAload(var);
/*     */ 
/* 126 */       int oldHandler = this.etable.handlerPc(this.index);
/* 127 */       b.addOpcode(167);
/* 128 */       b.addIndex(oldHandler - this.iterator.getCodeLength() - b.currentPc() + 1);
/*     */ 
/* 131 */       this.maxStack = b.getMaxStack();
/* 132 */       this.maxLocals = b.getMaxLocals();
/*     */ 
/* 134 */       int pos = this.iterator.append(b.get());
/* 135 */       this.iterator.append(b.getExceptionTable(), pos);
/* 136 */       this.etable.setHandlerPc(this.index, pos);
/*     */     }
/*     */     catch (NotFoundException e) {
/* 139 */       throw new CannotCompileException(e);
/*     */     }
/*     */     catch (CompileError e) {
/* 142 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.Handler
 * JD-Core Version:    0.6.2
 */