/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtField;
/*     */ import com.newrelic.javassist.CtPrimitiveType;
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
/*     */ public class FieldAccess extends Expr
/*     */ {
/*     */   int opcode;
/*     */ 
/*     */   protected FieldAccess(int pos, CodeIterator i, CtClass declaring, MethodInfo m, int op)
/*     */   {
/*  31 */     super(pos, i, declaring, m);
/*  32 */     this.opcode = op;
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
/*  57 */     return super.getFileName();
/*     */   }
/*     */ 
/*     */   public boolean isStatic()
/*     */   {
/*  64 */     return isStatic(this.opcode);
/*     */   }
/*     */ 
/*     */   static boolean isStatic(int c) {
/*  68 */     return (c == 178) || (c == 179);
/*     */   }
/*     */ 
/*     */   public boolean isReader()
/*     */   {
/*  75 */     return (this.opcode == 180) || (this.opcode == 178);
/*     */   }
/*     */ 
/*     */   public boolean isWriter()
/*     */   {
/*  82 */     return (this.opcode == 181) || (this.opcode == 179);
/*     */   }
/*     */ 
/*     */   private CtClass getCtClass()
/*     */     throws NotFoundException
/*     */   {
/*  89 */     return this.thisClass.getClassPool().get(getClassName());
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/*  96 */     int index = this.iterator.u16bitAt(this.currentPos + 1);
/*  97 */     return getConstPool().getFieldrefClassName(index);
/*     */   }
/*     */ 
/*     */   public String getFieldName()
/*     */   {
/* 104 */     int index = this.iterator.u16bitAt(this.currentPos + 1);
/* 105 */     return getConstPool().getFieldrefName(index);
/*     */   }
/*     */ 
/*     */   public CtField getField()
/*     */     throws NotFoundException
/*     */   {
/* 112 */     CtClass cc = getCtClass();
/* 113 */     return cc.getField(getFieldName());
/*     */   }
/*     */ 
/*     */   public CtClass[] mayThrow()
/*     */   {
/* 123 */     return super.mayThrow();
/*     */   }
/*     */ 
/*     */   public String getSignature()
/*     */   {
/* 135 */     int index = this.iterator.u16bitAt(this.currentPos + 1);
/* 136 */     return getConstPool().getFieldrefType(index);
/*     */   }
/*     */ 
/*     */   public void replace(String statement)
/*     */     throws CannotCompileException
/*     */   {
/* 150 */     this.thisClass.getClassFile();
/* 151 */     ConstPool constPool = getConstPool();
/* 152 */     int pos = this.currentPos;
/* 153 */     int index = this.iterator.u16bitAt(pos + 1);
/*     */ 
/* 155 */     Javac jc = new Javac(this.thisClass);
/* 156 */     CodeAttribute ca = this.iterator.get();
/*     */     try
/*     */     {
/* 160 */       CtClass fieldType = Descriptor.toCtClass(constPool.getFieldrefType(index), this.thisClass.getClassPool());
/*     */ 
/* 163 */       boolean read = isReader();
/*     */       CtClass retType;
/*     */       CtClass[] params;
/*     */       CtClass retType;
/* 164 */       if (read) {
/* 165 */         CtClass[] params = new CtClass[0];
/* 166 */         retType = fieldType;
/*     */       }
/*     */       else {
/* 169 */         params = new CtClass[1];
/* 170 */         params[0] = fieldType;
/* 171 */         retType = CtClass.voidType;
/*     */       }
/*     */ 
/* 174 */       int paramVar = ca.getMaxLocals();
/* 175 */       jc.recordParams(constPool.getFieldrefClassName(index), params, true, paramVar, withinStatic());
/*     */ 
/* 180 */       boolean included = checkResultValue(retType, statement);
/* 181 */       if (read) {
/* 182 */         included = true;
/*     */       }
/* 184 */       int retVar = jc.recordReturnType(retType, included);
/* 185 */       if (read) {
/* 186 */         jc.recordProceed(new ProceedForRead(retType, this.opcode, index, paramVar));
/*     */       }
/*     */       else
/*     */       {
/* 190 */         jc.recordType(fieldType);
/* 191 */         jc.recordProceed(new ProceedForWrite(params[0], this.opcode, index, paramVar));
/*     */       }
/*     */ 
/* 195 */       Bytecode bytecode = jc.getBytecode();
/* 196 */       storeStack(params, isStatic(), paramVar, bytecode);
/* 197 */       jc.recordLocalVariables(ca, pos);
/*     */ 
/* 199 */       if (included) {
/* 200 */         if (retType == CtClass.voidType) {
/* 201 */           bytecode.addOpcode(1);
/* 202 */           bytecode.addAstore(retVar);
/*     */         }
/*     */         else {
/* 205 */           bytecode.addConstZero(retType);
/* 206 */           bytecode.addStore(retVar, retType);
/*     */         }
/*     */       }
/* 209 */       jc.compileStmnt(statement);
/* 210 */       if (read) {
/* 211 */         bytecode.addLoad(retVar, retType);
/*     */       }
/* 213 */       replace0(pos, bytecode, 3);
/*     */     } catch (CompileError e) {
/* 215 */       throw new CannotCompileException(e); } catch (NotFoundException e) {
/* 216 */       throw new CannotCompileException(e);
/*     */     } catch (BadBytecode e) {
/* 218 */       throw new CannotCompileException("broken method");
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ProceedForWrite
/*     */     implements ProceedHandler
/*     */   {
/*     */     CtClass fieldType;
/*     */     int opcode;
/*     */     int targetVar;
/*     */     int index;
/*     */ 
/*     */     ProceedForWrite(CtClass type, int op, int i, int var)
/*     */     {
/* 278 */       this.fieldType = type;
/* 279 */       this.targetVar = var;
/* 280 */       this.opcode = op;
/* 281 */       this.index = i;
/*     */     }
/*     */ 
/*     */     public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 287 */       if (gen.getMethodArgsLength(args) != 1)
/* 288 */         throw new CompileError("$proceed() cannot take more than one parameter for field writing");
/*     */       int stack;
/*     */       int stack;
/* 293 */       if (FieldAccess.isStatic(this.opcode)) {
/* 294 */         stack = 0;
/*     */       } else {
/* 296 */         stack = -1;
/* 297 */         bytecode.addAload(this.targetVar);
/*     */       }
/*     */ 
/* 300 */       gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
/* 301 */       gen.doNumCast(this.fieldType);
/* 302 */       if ((this.fieldType instanceof CtPrimitiveType))
/* 303 */         stack -= ((CtPrimitiveType)this.fieldType).getDataSize();
/*     */       else {
/* 305 */         stack--;
/*     */       }
/* 307 */       bytecode.add(this.opcode);
/* 308 */       bytecode.addIndex(this.index);
/* 309 */       bytecode.growStack(stack);
/* 310 */       gen.setType(CtClass.voidType);
/* 311 */       gen.addNullIfVoid();
/*     */     }
/*     */ 
/*     */     public void setReturnType(JvstTypeChecker c, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 317 */       c.atMethodArgs(args, new int[1], new int[1], new String[1]);
/* 318 */       c.setType(CtClass.voidType);
/* 319 */       c.addNullIfVoid();
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ProceedForRead
/*     */     implements ProceedHandler
/*     */   {
/*     */     CtClass fieldType;
/*     */     int opcode;
/*     */     int targetVar;
/*     */     int index;
/*     */ 
/*     */     ProceedForRead(CtClass type, int op, int i, int var)
/*     */     {
/* 230 */       this.fieldType = type;
/* 231 */       this.targetVar = var;
/* 232 */       this.opcode = op;
/* 233 */       this.index = i;
/*     */     }
/*     */ 
/*     */     public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 239 */       if ((args != null) && (!gen.isParamListName(args)))
/* 240 */         throw new CompileError("$proceed() cannot take a parameter for field reading");
/*     */       int stack;
/*     */       int stack;
/* 244 */       if (FieldAccess.isStatic(this.opcode)) {
/* 245 */         stack = 0;
/*     */       } else {
/* 247 */         stack = -1;
/* 248 */         bytecode.addAload(this.targetVar);
/*     */       }
/*     */ 
/* 251 */       if ((this.fieldType instanceof CtPrimitiveType))
/* 252 */         stack += ((CtPrimitiveType)this.fieldType).getDataSize();
/*     */       else {
/* 254 */         stack++;
/*     */       }
/* 256 */       bytecode.add(this.opcode);
/* 257 */       bytecode.addIndex(this.index);
/* 258 */       bytecode.growStack(stack);
/* 259 */       gen.setType(this.fieldType);
/*     */     }
/*     */ 
/*     */     public void setReturnType(JvstTypeChecker c, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 265 */       c.setType(this.fieldType);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.FieldAccess
 * JD-Core Version:    0.6.2
 */