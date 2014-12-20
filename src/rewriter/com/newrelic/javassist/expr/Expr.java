/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtBehavior;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtConstructor;
/*     */ import com.newrelic.javassist.CtPrimitiveType;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.Bytecode;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator.Gap;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.ExceptionTable;
/*     */ import com.newrelic.javassist.bytecode.ExceptionsAttribute;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.Opcode;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public abstract class Expr
/*     */   implements Opcode
/*     */ {
/*     */   int currentPos;
/*     */   CodeIterator iterator;
/*     */   CtClass thisClass;
/*     */   MethodInfo thisMethod;
/*     */   boolean edited;
/*     */   int maxLocals;
/*     */   int maxStack;
/*     */   static final String javaLangObject = "java.lang.Object";
/*     */ 
/*     */   protected Expr(int pos, CodeIterator i, CtClass declaring, MethodInfo m)
/*     */   {
/*  58 */     this.currentPos = pos;
/*  59 */     this.iterator = i;
/*  60 */     this.thisClass = declaring;
/*  61 */     this.thisMethod = m;
/*     */   }
/*     */ 
/*     */   public CtClass getEnclosingClass()
/*     */   {
/*  70 */     return this.thisClass;
/*     */   }
/*     */   protected final ConstPool getConstPool() {
/*  73 */     return this.thisMethod.getConstPool();
/*     */   }
/*     */ 
/*     */   protected final boolean edited() {
/*  77 */     return this.edited;
/*     */   }
/*     */ 
/*     */   protected final int locals() {
/*  81 */     return this.maxLocals;
/*     */   }
/*     */ 
/*     */   protected final int stack() {
/*  85 */     return this.maxStack;
/*     */   }
/*     */ 
/*     */   protected final boolean withinStatic()
/*     */   {
/*  92 */     return (this.thisMethod.getAccessFlags() & 0x8) != 0;
/*     */   }
/*     */ 
/*     */   public CtBehavior where()
/*     */   {
/*  99 */     MethodInfo mi = this.thisMethod;
/* 100 */     CtBehavior[] cb = this.thisClass.getDeclaredBehaviors();
/* 101 */     for (int i = cb.length - 1; i >= 0; i--) {
/* 102 */       if (cb[i].getMethodInfo2() == mi)
/* 103 */         return cb[i];
/*     */     }
/* 105 */     CtConstructor init = this.thisClass.getClassInitializer();
/* 106 */     if ((init != null) && (init.getMethodInfo2() == mi)) {
/* 107 */       return init;
/*     */     }
/*     */ 
/* 114 */     for (int i = cb.length - 1; i >= 0; i--) {
/* 115 */       if ((this.thisMethod.getName().equals(cb[i].getMethodInfo2().getName())) && (this.thisMethod.getDescriptor().equals(cb[i].getMethodInfo2().getDescriptor())))
/*     */       {
/* 118 */         return cb[i];
/*     */       }
/*     */     }
/*     */ 
/* 122 */     throw new RuntimeException("fatal: not found");
/*     */   }
/*     */ 
/*     */   public CtClass[] mayThrow()
/*     */   {
/* 132 */     ClassPool pool = this.thisClass.getClassPool();
/* 133 */     ConstPool cp = this.thisMethod.getConstPool();
/* 134 */     LinkedList list = new LinkedList();
/*     */     try {
/* 136 */       CodeAttribute ca = this.thisMethod.getCodeAttribute();
/* 137 */       ExceptionTable et = ca.getExceptionTable();
/* 138 */       int pos = this.currentPos;
/* 139 */       int n = et.size();
/* 140 */       for (int i = 0; i < n; i++)
/* 141 */         if ((et.startPc(i) <= pos) && (pos < et.endPc(i))) {
/* 142 */           int t = et.catchType(i);
/* 143 */           if (t > 0)
/*     */             try {
/* 145 */               addClass(list, pool.get(cp.getClassInfo(t)));
/*     */             }
/*     */             catch (NotFoundException e)
/*     */             {
/*     */             }
/*     */         }
/*     */     }
/*     */     catch (NullPointerException e) {
/*     */     }
/* 154 */     ExceptionsAttribute ea = this.thisMethod.getExceptionsAttribute();
/* 155 */     if (ea != null) {
/* 156 */       String[] exceptions = ea.getExceptions();
/* 157 */       if (exceptions != null) {
/* 158 */         int n = exceptions.length;
/* 159 */         for (int i = 0; i < n; i++)
/*     */           try {
/* 161 */             addClass(list, pool.get(exceptions[i]));
/*     */           }
/*     */           catch (NotFoundException e)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/* 168 */     return (CtClass[])list.toArray(new CtClass[list.size()]);
/*     */   }
/*     */ 
/*     */   private static void addClass(LinkedList list, CtClass c) {
/* 172 */     Iterator it = list.iterator();
/* 173 */     while (it.hasNext()) {
/* 174 */       if (it.next() == c)
/* 175 */         return;
/*     */     }
/* 177 */     list.add(c);
/*     */   }
/*     */ 
/*     */   public int indexOfBytecode()
/*     */   {
/* 186 */     return this.currentPos;
/*     */   }
/*     */ 
/*     */   public int getLineNumber()
/*     */   {
/* 195 */     return this.thisMethod.getLineNumber(this.currentPos);
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 204 */     ClassFile cf = this.thisClass.getClassFile2();
/* 205 */     if (cf == null) {
/* 206 */       return null;
/*     */     }
/* 208 */     return cf.getSourceFile();
/*     */   }
/*     */ 
/*     */   static final boolean checkResultValue(CtClass retType, String prog)
/*     */     throws CannotCompileException
/*     */   {
/* 216 */     boolean hasIt = prog.indexOf("$_") >= 0;
/* 217 */     if ((!hasIt) && (retType != CtClass.voidType)) {
/* 218 */       throw new CannotCompileException("the resulting value is not stored in $_");
/*     */     }
/*     */ 
/* 222 */     return hasIt;
/*     */   }
/*     */ 
/*     */   static final void storeStack(CtClass[] params, boolean isStaticCall, int regno, Bytecode bytecode)
/*     */   {
/* 234 */     storeStack0(0, params.length, params, regno + 1, bytecode);
/* 235 */     if (isStaticCall) {
/* 236 */       bytecode.addOpcode(1);
/*     */     }
/* 238 */     bytecode.addAstore(regno);
/*     */   }
/*     */ 
/*     */   private static void storeStack0(int i, int n, CtClass[] params, int regno, Bytecode bytecode)
/*     */   {
/* 243 */     if (i >= n) {
/* 244 */       return;
/*     */     }
/* 246 */     CtClass c = params[i];
/*     */     int size;
/*     */     int size;
/* 248 */     if ((c instanceof CtPrimitiveType))
/* 249 */       size = ((CtPrimitiveType)c).getDataSize();
/*     */     else {
/* 251 */       size = 1;
/*     */     }
/* 253 */     storeStack0(i + 1, n, params, regno + size, bytecode);
/* 254 */     bytecode.addStore(regno, c);
/*     */   }
/*     */ 
/*     */   public abstract void replace(String paramString)
/*     */     throws CannotCompileException;
/*     */ 
/*     */   public void replace(String statement, ExprEditor recursive)
/*     */     throws CannotCompileException
/*     */   {
/* 284 */     replace(statement);
/* 285 */     if (recursive != null)
/* 286 */       runEditor(recursive, this.iterator);
/*     */   }
/*     */ 
/*     */   protected void replace0(int pos, Bytecode bytecode, int size) throws BadBytecode
/*     */   {
/* 291 */     byte[] code = bytecode.get();
/* 292 */     this.edited = true;
/* 293 */     int gap = code.length - size;
/* 294 */     for (int i = 0; i < size; i++) {
/* 295 */       this.iterator.writeByte(0, pos + i);
/*     */     }
/* 297 */     if (gap > 0) {
/* 298 */       pos = this.iterator.insertGapAt(pos, gap, false).position;
/*     */     }
/* 300 */     this.iterator.write(code, pos);
/* 301 */     this.iterator.insert(bytecode.getExceptionTable(), pos);
/* 302 */     this.maxLocals = bytecode.getMaxLocals();
/* 303 */     this.maxStack = bytecode.getMaxStack();
/*     */   }
/*     */ 
/*     */   protected void runEditor(ExprEditor ed, CodeIterator oldIterator)
/*     */     throws CannotCompileException
/*     */   {
/* 309 */     CodeAttribute codeAttr = oldIterator.get();
/* 310 */     int orgLocals = codeAttr.getMaxLocals();
/* 311 */     int orgStack = codeAttr.getMaxStack();
/* 312 */     int newLocals = locals();
/* 313 */     codeAttr.setMaxStack(stack());
/* 314 */     codeAttr.setMaxLocals(newLocals);
/* 315 */     ExprEditor.LoopContext context = new ExprEditor.LoopContext(newLocals);
/*     */ 
/* 317 */     int size = oldIterator.getCodeLength();
/* 318 */     int endPos = oldIterator.lookAhead();
/* 319 */     oldIterator.move(this.currentPos);
/* 320 */     if (ed.doit(this.thisClass, this.thisMethod, context, oldIterator, endPos)) {
/* 321 */       this.edited = true;
/*     */     }
/* 323 */     oldIterator.move(endPos + oldIterator.getCodeLength() - size);
/* 324 */     codeAttr.setMaxLocals(orgLocals);
/* 325 */     codeAttr.setMaxStack(orgStack);
/* 326 */     this.maxLocals = context.maxLocals;
/* 327 */     this.maxStack += context.maxStack;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.Expr
 * JD-Core Version:    0.6.2
 */