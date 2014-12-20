/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
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
/*     */ public class NewArray extends Expr
/*     */ {
/*     */   int opcode;
/*     */ 
/*     */   protected NewArray(int pos, CodeIterator i, CtClass declaring, MethodInfo m, int op)
/*     */   {
/*  34 */     super(pos, i, declaring, m);
/*  35 */     this.opcode = op;
/*     */   }
/*     */ 
/*     */   public CtBehavior where()
/*     */   {
/*  42 */     return super.where();
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
/*  70 */     return super.mayThrow();
/*     */   }
/*     */ 
/*     */   public CtClass getComponentType()
/*     */     throws NotFoundException
/*     */   {
/*  80 */     if (this.opcode == 188) {
/*  81 */       int atype = this.iterator.byteAt(this.currentPos + 1);
/*  82 */       return getPrimitiveType(atype);
/*     */     }
/*  84 */     if ((this.opcode == 189) || (this.opcode == 197))
/*     */     {
/*  86 */       int index = this.iterator.u16bitAt(this.currentPos + 1);
/*  87 */       String desc = getConstPool().getClassInfo(index);
/*  88 */       int dim = Descriptor.arrayDimension(desc);
/*  89 */       desc = Descriptor.toArrayComponent(desc, dim);
/*  90 */       return Descriptor.toCtClass(desc, this.thisClass.getClassPool());
/*     */     }
/*     */ 
/*  93 */     throw new RuntimeException("bad opcode: " + this.opcode);
/*     */   }
/*     */ 
/*     */   CtClass getPrimitiveType(int atype) {
/*  97 */     switch (atype) {
/*     */     case 4:
/*  99 */       return CtClass.booleanType;
/*     */     case 5:
/* 101 */       return CtClass.charType;
/*     */     case 6:
/* 103 */       return CtClass.floatType;
/*     */     case 7:
/* 105 */       return CtClass.doubleType;
/*     */     case 8:
/* 107 */       return CtClass.byteType;
/*     */     case 9:
/* 109 */       return CtClass.shortType;
/*     */     case 10:
/* 111 */       return CtClass.intType;
/*     */     case 11:
/* 113 */       return CtClass.longType;
/*     */     }
/* 115 */     throw new RuntimeException("bad atype: " + atype);
/*     */   }
/*     */ 
/*     */   public int getDimension()
/*     */   {
/* 123 */     if (this.opcode == 188)
/* 124 */       return 1;
/* 125 */     if ((this.opcode == 189) || (this.opcode == 197))
/*     */     {
/* 127 */       int index = this.iterator.u16bitAt(this.currentPos + 1);
/* 128 */       String desc = getConstPool().getClassInfo(index);
/* 129 */       return Descriptor.arrayDimension(desc) + (this.opcode == 189 ? 1 : 0);
/*     */     }
/*     */ 
/* 133 */     throw new RuntimeException("bad opcode: " + this.opcode);
/*     */   }
/*     */ 
/*     */   public int getCreatedDimensions()
/*     */   {
/* 142 */     if (this.opcode == 197) {
/* 143 */       return this.iterator.byteAt(this.currentPos + 3);
/*     */     }
/* 145 */     return 1;
/*     */   }
/*     */ 
/*     */   public void replace(String statement)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 160 */       replace2(statement);
/*     */     } catch (CompileError e) {
/* 162 */       throw new CannotCompileException(e); } catch (NotFoundException e) {
/* 163 */       throw new CannotCompileException(e);
/*     */     } catch (BadBytecode e) {
/* 165 */       throw new CannotCompileException("broken method");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void replace2(String statement)
/*     */     throws CompileError, NotFoundException, BadBytecode, CannotCompileException
/*     */   {
/* 173 */     this.thisClass.getClassFile();
/* 174 */     ConstPool constPool = getConstPool();
/* 175 */     int pos = this.currentPos;
/*     */ 
/* 178 */     int index = 0;
/* 179 */     int dim = 1;
/*     */     int codeLength;
/* 181 */     if (this.opcode == 188) {
/* 182 */       index = this.iterator.byteAt(this.currentPos + 1);
/* 183 */       CtPrimitiveType cpt = (CtPrimitiveType)getPrimitiveType(index);
/* 184 */       String desc = "[" + cpt.getDescriptor();
/* 185 */       codeLength = 2;
/*     */     }
/*     */     else
/*     */     {
/*     */       int codeLength;
/* 187 */       if (this.opcode == 189) {
/* 188 */         index = this.iterator.u16bitAt(pos + 1);
/* 189 */         String desc = constPool.getClassInfo(index);
/* 190 */         if (desc.startsWith("["))
/* 191 */           desc = "[" + desc;
/*     */         else {
/* 193 */           desc = "[L" + desc + ";";
/*     */         }
/* 195 */         codeLength = 3;
/*     */       }
/*     */       else
/*     */       {
/*     */         int codeLength;
/* 197 */         if (this.opcode == 197) {
/* 198 */           index = this.iterator.u16bitAt(this.currentPos + 1);
/* 199 */           String desc = constPool.getClassInfo(index);
/* 200 */           dim = this.iterator.byteAt(this.currentPos + 3);
/* 201 */           codeLength = 4;
/*     */         }
/*     */         else {
/* 204 */           throw new RuntimeException("bad opcode: " + this.opcode);
/*     */         }
/*     */       }
/*     */     }
/*     */     int codeLength;
/*     */     String desc;
/* 206 */     CtClass retType = Descriptor.toCtClass(desc, this.thisClass.getClassPool());
/*     */ 
/* 208 */     Javac jc = new Javac(this.thisClass);
/* 209 */     CodeAttribute ca = this.iterator.get();
/*     */ 
/* 211 */     CtClass[] params = new CtClass[dim];
/* 212 */     for (int i = 0; i < dim; i++) {
/* 213 */       params[i] = CtClass.intType;
/*     */     }
/* 215 */     int paramVar = ca.getMaxLocals();
/* 216 */     jc.recordParams("java.lang.Object", params, true, paramVar, withinStatic());
/*     */ 
/* 221 */     checkResultValue(retType, statement);
/* 222 */     int retVar = jc.recordReturnType(retType, true);
/* 223 */     jc.recordProceed(new ProceedForArray(retType, this.opcode, index, dim));
/*     */ 
/* 225 */     Bytecode bytecode = jc.getBytecode();
/* 226 */     storeStack(params, true, paramVar, bytecode);
/* 227 */     jc.recordLocalVariables(ca, pos);
/*     */ 
/* 229 */     bytecode.addOpcode(1);
/* 230 */     bytecode.addAstore(retVar);
/*     */ 
/* 232 */     jc.compileStmnt(statement);
/* 233 */     bytecode.addAload(retVar);
/*     */ 
/* 235 */     replace0(pos, bytecode, codeLength);
/*     */   }
/*     */   static class ProceedForArray implements ProceedHandler {
/*     */     CtClass arrayType;
/*     */     int opcode;
/*     */     int index;
/*     */     int dimension;
/*     */ 
/* 246 */     ProceedForArray(CtClass type, int op, int i, int dim) { this.arrayType = type;
/* 247 */       this.opcode = op;
/* 248 */       this.index = i;
/* 249 */       this.dimension = dim;
/*     */     }
/*     */ 
/*     */     public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 255 */       int num = gen.getMethodArgsLength(args);
/* 256 */       if (num != this.dimension) {
/* 257 */         throw new CompileError("$proceed() with a wrong number of parameters");
/*     */       }
/*     */ 
/* 260 */       gen.atMethodArgs(args, new int[num], new int[num], new String[num]);
/*     */ 
/* 262 */       bytecode.addOpcode(this.opcode);
/* 263 */       if (this.opcode == 189) {
/* 264 */         bytecode.addIndex(this.index);
/* 265 */       } else if (this.opcode == 188) {
/* 266 */         bytecode.add(this.index);
/*     */       } else {
/* 268 */         bytecode.addIndex(this.index);
/* 269 */         bytecode.add(this.dimension);
/* 270 */         bytecode.growStack(1 - this.dimension);
/*     */       }
/*     */ 
/* 273 */       gen.setType(this.arrayType);
/*     */     }
/*     */ 
/*     */     public void setReturnType(JvstTypeChecker c, ASTList args)
/*     */       throws CompileError
/*     */     {
/* 279 */       c.setType(this.arrayType);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.NewArray
 * JD-Core Version:    0.6.2
 */