/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtMethod;
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
/*     */ 
/*     */ public class MethodCall extends Expr
/*     */ {
/*     */   protected MethodCall(int pos, CodeIterator i, CtClass declaring, MethodInfo m)
/*     */   {
/*  31 */     super(pos, i, declaring, m);
/*     */   }
/*     */ 
/*     */   private int getNameAndType(ConstPool cp) {
/*  35 */     int pos = this.currentPos;
/*  36 */     int c = this.iterator.byteAt(pos);
/*  37 */     int index = this.iterator.u16bitAt(pos + 1);
/*     */ 
/*  39 */     if (c == 185) {
/*  40 */       return cp.getInterfaceMethodrefNameAndType(index);
/*     */     }
/*  42 */     return cp.getMethodrefNameAndType(index);
/*     */   }
/*     */ 
/*     */   public CtBehavior where()
/*     */   {
/*  49 */     return super.where();
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/*  58 */     return super.getLineNumber();
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/*  67 */     return super.getFileName();
/*     */   }
/*     */ 
/*     */   protected CtClass getCtClass()
/*     */     throws NotFoundException
/*     */   {
/*  75 */     return this.thisClass.getClassPool().get(getClassName());
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/*  85 */     ConstPool cp = getConstPool();
/*  86 */     int pos = this.currentPos;
/*  87 */     int c = this.iterator.byteAt(pos);
/*  88 */     int index = this.iterator.u16bitAt(pos + 1);
/*     */     String cname;
/*     */     String cname;
/*  90 */     if (c == 185)
/*  91 */       cname = cp.getInterfaceMethodrefClassName(index);
/*     */     else {
/*  93 */       cname = cp.getMethodrefClassName(index);
/*     */     }
/*  95 */     if (cname.charAt(0) == '[') {
/*  96 */       cname = Descriptor.toClassName(cname);
/*     */     }
/*  98 */     return cname;
/*     */   }
/*     */ 
/*     */   public String getMethodName()
/*     */   {
/* 105 */     ConstPool cp = getConstPool();
/* 106 */     int nt = getNameAndType(cp);
/* 107 */     return cp.getUtf8Info(cp.getNameAndTypeName(nt));
/*     */   }
/*     */ 
/*     */   public CtMethod getMethod()
/*     */     throws NotFoundException
/*     */   {
/* 114 */     return getCtClass().getMethod(getMethodName(), getSignature());
/*     */   }
/*     */ 
/*     */   public String getSignature()
/*     */   {
/* 128 */     ConstPool cp = getConstPool();
/* 129 */     int nt = getNameAndType(cp);
/* 130 */     return cp.getUtf8Info(cp.getNameAndTypeDescriptor(nt));
/*     */   }
/*     */ 
/*     */   public CtClass[] mayThrow()
/*     */   {
/* 140 */     return super.mayThrow();
/*     */   }
/*     */ 
/*     */   public boolean isSuper()
/*     */   {
/* 148 */     return (this.iterator.byteAt(this.currentPos) == 183) && (!where().getDeclaringClass().getName().equals(getClassName()));
/*     */   }
/*     */ 
/*     */   public void replace(String statement)
/*     */     throws CannotCompileException
/*     */   {
/* 179 */     this.thisClass.getClassFile();
/* 180 */     ConstPool constPool = getConstPool();
/* 181 */     int pos = this.currentPos;
/* 182 */     int index = this.iterator.u16bitAt(pos + 1);
/*     */ 
/* 186 */     int c = this.iterator.byteAt(pos);
/*     */     String signature;
/* 187 */     if (c == 185) {
/* 188 */       int opcodeSize = 5;
/* 189 */       String classname = constPool.getInterfaceMethodrefClassName(index);
/* 190 */       String methodname = constPool.getInterfaceMethodrefName(index);
/* 191 */       signature = constPool.getInterfaceMethodrefType(index);
/*     */     }
/*     */     else
/*     */     {
/*     */       String signature;
/* 193 */       if ((c == 184) || (c == 183) || (c == 182))
/*     */       {
/* 195 */         int opcodeSize = 3;
/* 196 */         String classname = constPool.getMethodrefClassName(index);
/* 197 */         String methodname = constPool.getMethodrefName(index);
/* 198 */         signature = constPool.getMethodrefType(index);
/*     */       }
/*     */       else {
/* 201 */         throw new CannotCompileException("not method invocation");
/*     */       }
/*     */     }
/*     */     String signature;
/*     */     String methodname;
/*     */     String classname;
/*     */     int opcodeSize;
/* 203 */     Javac jc = new Javac(this.thisClass);
/* 204 */     ClassPool cp = this.thisClass.getClassPool();
/* 205 */     CodeAttribute ca = this.iterator.get();
/*     */     try {
/* 207 */       CtClass[] params = Descriptor.getParameterTypes(signature, cp);
/* 208 */       CtClass retType = Descriptor.getReturnType(signature, cp);
/* 209 */       int paramVar = ca.getMaxLocals();
/* 210 */       jc.recordParams(classname, params, true, paramVar, withinStatic());
/*     */ 
/* 212 */       int retVar = jc.recordReturnType(retType, true);
/* 213 */       if (c == 184)
/* 214 */         jc.recordStaticProceed(classname, methodname);
/* 215 */       else if (c == 183) {
/* 216 */         jc.recordSpecialProceed("$0", classname, methodname, signature);
/*     */       }
/*     */       else {
/* 219 */         jc.recordProceed("$0", methodname);
/*     */       }
/*     */ 
/* 223 */       checkResultValue(retType, statement);
/*     */ 
/* 225 */       Bytecode bytecode = jc.getBytecode();
/* 226 */       storeStack(params, c == 184, paramVar, bytecode);
/* 227 */       jc.recordLocalVariables(ca, pos);
/*     */ 
/* 229 */       if (retType != CtClass.voidType) {
/* 230 */         bytecode.addConstZero(retType);
/* 231 */         bytecode.addStore(retVar, retType);
/*     */       }
/*     */ 
/* 234 */       jc.compileStmnt(statement);
/* 235 */       if (retType != CtClass.voidType) {
/* 236 */         bytecode.addLoad(retVar, retType);
/*     */       }
/* 238 */       replace0(pos, bytecode, opcodeSize);
/*     */     } catch (CompileError e) {
/* 240 */       throw new CannotCompileException(e); } catch (NotFoundException e) {
/* 241 */       throw new CannotCompileException(e);
/*     */     } catch (BadBytecode e) {
/* 243 */       throw new CannotCompileException("broken method");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.MethodCall
 * JD-Core Version:    0.6.2
 */