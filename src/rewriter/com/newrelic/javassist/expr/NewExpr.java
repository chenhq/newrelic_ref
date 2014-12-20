/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtConstructor;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ import com.newrelic.javassist.compiler.Javac;
/*     */ import com.newrelic.javassist.compiler.JvstCodeGen;
/*     */ import com.newrelic.javassist.compiler.JvstTypeChecker;
/*     */ import com.newrelic.javassist.compiler.ProceedHandler;
/*     */ import com.newrelic.javassist.compiler.ast.ASTList;
/*     */ 
/*     */ public class NewExpr extends Expr
/*     */ {
/*     */   String newTypeName;
/*     */   int newPos;
/*     */ 
/*     */   protected NewExpr(int pos, CodeIterator i, CtClass declaring, MethodInfo m, String type, int np)
/*     */   {
/*  35 */     super(pos, i, declaring, m);
/*  36 */     this.newTypeName = type;
/*  37 */     this.newPos = np;
/*     */   }
/*     */ 
/*     */   public CtBehavior where()
/*     */   {
/*  58 */     return super.where();
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/*  67 */     return super.getLineNumber();
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  76 */     return super.getFileName();
/*     */   }
/*     */ 
/*     */   private CtClass getCtClass()
/*     */     throws NotFoundException
/*     */   {
/*  83 */     return this.thisClass.getClassPool().get(this.newTypeName);
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/*  90 */     return this.newTypeName;
/*     */   }
/*     */ 
/*     */   public String getSignature()
/*     */   {
/* 104 */     ConstPool constPool = getConstPool();
/* 105 */     int methodIndex = this.iterator.u16bitAt(this.currentPos + 1);
/* 106 */     return constPool.getMethodrefType(methodIndex);
/*     */   }
/*     */ 
/*     */   public CtConstructor getConstructor()
/*     */     throws NotFoundException
/*     */   {
/* 113 */     ConstPool cp = getConstPool();
/* 114 */     int index = this.iterator.u16bitAt(this.currentPos + 1);
/* 115 */     String desc = cp.getMethodrefType(index);
/* 116 */     return getCtClass().getConstructor(desc);
/*     */   }
/*     */ 
/*     */   public CtClass[] mayThrow()
/*     */   {
/* 126 */     return super.mayThrow();
/*     */   }
/*     */ 
/*     */   private int canReplace()
/*     */     throws CannotCompileException
/*     */   {
/* 141 */     int op = this.iterator.byteAt(this.newPos + 3);
/* 142 */     if (op == 89)
/* 143 */       return 4;
/* 144 */     if ((op == 90) && (this.iterator.byteAt(this.newPos + 4) == 95))
/*     */     {
/* 146 */       return 5;
/*     */     }
/* 148 */     return 3;
/*     */   }
/*     */ 
/*     */   public void replace(String statement)
/*     */     throws CannotCompileException
/*     */   {
/* 162 */     this.thisClass.getClassFile();
/*     */ 
/* 164 */     int bytecodeSize = 3;
/* 165 */     int pos = this.newPos;
/*     */ 
/* 167 */     int newIndex = this.iterator.u16bitAt(pos + 1);
/*     */ 
/* 171 */     int codeSize = canReplace();
/* 172 */     int end = pos + codeSize;
/* 173 */     for (int i = pos; i < end; i++) {
/* 174 */       this.iterator.writeByte(0, i);
/*     */     }
/* 176 */     ConstPool constPool = getConstPool();
/* 177 */     pos = this.currentPos;
/* 178 */     int methodIndex = this.iterator.u16bitAt(pos + 1);
/*     */ 
/* 180 */     String signature = constPool.getMethodrefType(methodIndex);
/*     */ 
/* 182 */     Javac jc = new Javac(this.thisClass);
/* 183 */     ClassPool cp = this.thisClass.getClassPool();
/* 184 */     CodeAttribute ca = this.iterator.get();
/*     */     try {
/* 186 */       CtClass[] params = Descriptor.getParameterTypes(signature, cp);
/* 187 */       CtClass newType = cp.get(this.newTypeName);
/* 188 */       int paramVar = ca.getMaxLocals();
/* 189 */       jc.recordParams(this.newTypeName, params, true, paramVar, withinStatic());
/*     */ 
/* 191 */       int retVar = jc.recordReturnType(newType, true);
/* 192 */       jc.recordProceed(new ProceedForNew(newType, newIndex, methodIndex));
/*     */ 
/* 197 */       checkResultValue(newType, statement);
/*     */ 
/* 199 */       Bytecode bytecode = jc.getBytecode();
/* 200 */       storeStack(params, true, paramVar, bytecode);
/* 201 */       jc.recordLocalVariables(ca, pos);
/*     */ 
/* 203 */       bytecode.addConstZero(newType);
/* 204 */       bytecode.addStore(retVar, newType);
/*     */ 
/* 206 */       jc.compileStmnt(statement);
/* 207 */       if (codeSize > 3) {
/* 208 */         bytecode.addAload(retVar);
/*     */       }
/* 210 */       replace0(pos, bytecode, 3);
/*     */     } catch (CompileError e) {
/* 212 */       throw new CannotCompileException(e); } catch (NotFoundException e) {
/* 213 */       throw new CannotCompileException(e);
/*     */     } catch (BadBytecode e) {
/* 215 */       throw new CannotCompileException("broken method");
/*     */     }
/*     */   }
/*     */   static class ProceedForNew implements ProceedHandler { CtClass newType;
/*     */     int newIndex;
/*     */     int methodIndex;
/*     */ 
/* 224 */     ProceedForNew(CtClass nt, int ni, int mi) { this.newType = nt;
/* 225 */       this.newIndex = ni;
/* 226 */       this.methodIndex = mi;
/*     */     }
/*     */ 
/*     */     public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 232 */       bytecode.addOpcode(187);
/* 233 */       bytecode.addIndex(this.newIndex);
/* 234 */       bytecode.addOpcode(89);
/* 235 */       gen.atMethodCallCore(this.newType, "<init>", args, false, true, -1, null);
/*     */ 
/* 237 */       gen.setType(this.newType);
/*     */     }
/*     */ 
/*     */     public void setReturnType(JvstTypeChecker c, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 243 */       c.atMethodCallCore(this.newType, "<init>", args);
/* 244 */       c.setType(this.newType);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.NewExpr
 * JD-Core Version:    0.6.2
 */