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
/*     */ public class Instanceof extends Expr
/*     */ {
/*     */   protected Instanceof(int pos, CodeIterator i, CtClass declaring, MethodInfo m)
/*     */   {
/*  32 */     super(pos, i, declaring, m);
/*     */   }
/*     */ 
/*     */   public CtBehavior where()
/*     */   {
/*  39 */     return super.where();
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/*  48 */     return super.getLineNumber();
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  58 */     return super.getFileName();
/*     */   }
/*     */ 
/*     */   public CtClass getType()
/*     */     throws NotFoundException
/*     */   {
/*  67 */     ConstPool cp = getConstPool();
/*  68 */     int pos = this.currentPos;
/*  69 */     int index = this.iterator.u16bitAt(pos + 1);
/*  70 */     String name = cp.getClassInfo(index);
/*  71 */     return this.thisClass.getClassPool().getCtClass(name);
/*     */   }
/*     */ 
/*     */   public CtClass[] mayThrow()
/*     */   {
/*  81 */     return super.mayThrow();
/*     */   }
/*     */ 
/*     */   public void replace(String statement)
/*     */     throws CannotCompileException
/*     */   {
/*  93 */     this.thisClass.getClassFile();
/*  94 */     ConstPool constPool = getConstPool();
/*  95 */     int pos = this.currentPos;
/*  96 */     int index = this.iterator.u16bitAt(pos + 1);
/*     */ 
/*  98 */     Javac jc = new Javac(this.thisClass);
/*  99 */     ClassPool cp = this.thisClass.getClassPool();
/* 100 */     CodeAttribute ca = this.iterator.get();
/*     */     try
/*     */     {
/* 103 */       CtClass[] params = { cp.get("java.lang.Object") };
/*     */ 
/* 105 */       CtClass retType = CtClass.booleanType;
/*     */ 
/* 107 */       int paramVar = ca.getMaxLocals();
/* 108 */       jc.recordParams("java.lang.Object", params, true, paramVar, withinStatic());
/*     */ 
/* 110 */       int retVar = jc.recordReturnType(retType, true);
/* 111 */       jc.recordProceed(new ProceedForInstanceof(index));
/*     */ 
/* 114 */       jc.recordType(getType());
/*     */ 
/* 118 */       checkResultValue(retType, statement);
/*     */ 
/* 120 */       Bytecode bytecode = jc.getBytecode();
/* 121 */       storeStack(params, true, paramVar, bytecode);
/* 122 */       jc.recordLocalVariables(ca, pos);
/*     */ 
/* 124 */       bytecode.addConstZero(retType);
/* 125 */       bytecode.addStore(retVar, retType);
/*     */ 
/* 127 */       jc.compileStmnt(statement);
/* 128 */       bytecode.addLoad(retVar, retType);
/*     */ 
/* 130 */       replace0(pos, bytecode, 3);
/*     */     } catch (CompileError e) {
/* 132 */       throw new CannotCompileException(e); } catch (NotFoundException e) {
/* 133 */       throw new CannotCompileException(e);
/*     */     } catch (BadBytecode e) {
/* 135 */       throw new CannotCompileException("broken method");
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ProceedForInstanceof implements ProceedHandler
/*     */   {
/*     */     int index;
/*     */ 
/*     */     ProceedForInstanceof(int i)
/*     */     {
/* 145 */       this.index = i;
/*     */     }
/*     */ 
/*     */     public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 151 */       if (gen.getMethodArgsLength(args) != 1) {
/* 152 */         throw new CompileError("$proceed() cannot take more than one parameter for instanceof");
/*     */       }
/*     */ 
/* 156 */       gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
/* 157 */       bytecode.addOpcode(193);
/* 158 */       bytecode.addIndex(this.index);
/* 159 */       gen.setType(CtClass.booleanType);
/*     */     }
/*     */ 
/*     */     public void setReturnType(JvstTypeChecker c, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 165 */       c.atMethodArgs(args, new int[1], new int[1], new String[1]);
/* 166 */       c.setType(CtClass.booleanType);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.Instanceof
 * JD-Core Version:    0.6.2
 */