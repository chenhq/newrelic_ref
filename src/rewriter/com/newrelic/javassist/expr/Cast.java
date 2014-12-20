/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ import com.newrelic.javassist.compiler.Javac;
/*     */ import com.newrelic.javassist.compiler.JvstCodeGen;
/*     */ import com.newrelic.javassist.compiler.JvstTypeChecker;
/*     */ import com.newrelic.javassist.compiler.ProceedHandler;
/*     */ import com.newrelic.javassist.compiler.ast.ASTList;
/*     */ 
/*     */ public class Cast extends Expr
/*     */ {
/*     */   protected Cast(int pos, CodeIterator i, CtClass declaring, MethodInfo m)
/*     */   {
/*  31 */     super(pos, i, declaring, m);
/*     */   }
/*     */ 
/*     */   public CtBehavior where()
/*     */   {
/*  38 */     return super.where();
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/*  47 */     return super.getLineNumber();
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  56 */     return super.getFileName();
/*     */   }
/*     */ 
/*     */   public CtClass getType()
/*     */     throws NotFoundException
/*     */   {
/*  64 */     ConstPool cp = getConstPool();
/*  65 */     int pos = this.currentPos;
/*  66 */     int index = this.iterator.u16bitAt(pos + 1);
/*  67 */     String name = cp.getClassInfo(index);
/*  68 */     return this.thisClass.getClassPool().getCtClass(name);
/*     */   }
/*     */ 
/*     */   public CtClass[] mayThrow()
/*     */   {
/*  78 */     return super.mayThrow();
/*     */   }
/*     */ 
/*     */   public void replace(String statement)
/*     */     throws CannotCompileException
/*     */   {
/*  90 */     this.thisClass.getClassFile();
/*  91 */     ConstPool constPool = getConstPool();
/*  92 */     int pos = this.currentPos;
/*  93 */     int index = this.iterator.u16bitAt(pos + 1);
/*     */ 
/*  95 */     Javac jc = new Javac(this.thisClass);
/*  96 */     ClassPool cp = this.thisClass.getClassPool();
/*  97 */     CodeAttribute ca = this.iterator.get();
/*     */     try
/*     */     {
/* 100 */       CtClass[] params = { cp.get("java.lang.Object") };
/*     */ 
/* 102 */       CtClass retType = getType();
/*     */ 
/* 104 */       int paramVar = ca.getMaxLocals();
/* 105 */       jc.recordParams("java.lang.Object", params, true, paramVar, withinStatic());
/*     */ 
/* 107 */       int retVar = jc.recordReturnType(retType, true);
/* 108 */       jc.recordProceed(new ProceedForCast(index, retType));
/*     */ 
/* 112 */       checkResultValue(retType, statement);
/*     */ 
/* 114 */       Bytecode bytecode = jc.getBytecode();
/* 115 */       storeStack(params, true, paramVar, bytecode);
/* 116 */       jc.recordLocalVariables(ca, pos);
/*     */ 
/* 118 */       bytecode.addConstZero(retType);
/* 119 */       bytecode.addStore(retVar, retType);
/*     */ 
/* 121 */       jc.compileStmnt(statement);
/* 122 */       bytecode.addLoad(retVar, retType);
/*     */ 
/* 124 */       replace0(pos, bytecode, 3);
/*     */     } catch (CompileError e) {
/* 126 */       throw new CannotCompileException(e); } catch (NotFoundException e) {
/* 127 */       throw new CannotCompileException(e);
/*     */     } catch (BadBytecode e) {
/* 129 */       throw new CannotCompileException("broken method");
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ProceedForCast implements ProceedHandler
/*     */   {
/*     */     int index;
/*     */     CtClass retType;
/*     */ 
/*     */     ProceedForCast(int i, CtClass t) {
/* 140 */       this.index = i;
/* 141 */       this.retType = t;
/*     */     }
/*     */ 
/*     */     public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 147 */       if (gen.getMethodArgsLength(args) != 1) {
/* 148 */         throw new CompileError("$proceed() cannot take more than one parameter for cast");
/*     */       }
/*     */ 
/* 152 */       gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
/* 153 */       bytecode.addOpcode(192);
/* 154 */       bytecode.addIndex(this.index);
/* 155 */       gen.setType(this.retType);
/*     */     }
/*     */ 
/*     */     public void setReturnType(JvstTypeChecker c, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 161 */       c.atMethodArgs(args, new int[1], new int[1], new String[1]);
/* 162 */       c.setType(this.retType);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.Cast
 * JD-Core Version:    0.6.2
 */