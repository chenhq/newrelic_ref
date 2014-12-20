/*     */ package com.newrelic.javassist.expr;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*     */ import com.newrelic.javassist.bytecode.CodeIterator;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import com.newrelic.javassist.bytecode.ExceptionTable;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ 
/*     */ public class ExprEditor
/*     */ {
/*     */   public boolean doit(CtClass clazz, MethodInfo minfo)
/*     */     throws CannotCompileException
/*     */   {
/*  81 */     CodeAttribute codeAttr = minfo.getCodeAttribute();
/*  82 */     if (codeAttr == null) {
/*  83 */       return false;
/*     */     }
/*  85 */     CodeIterator iterator = codeAttr.iterator();
/*  86 */     boolean edited = false;
/*  87 */     LoopContext context = new LoopContext(codeAttr.getMaxLocals());
/*     */ 
/*  89 */     while (iterator.hasNext()) {
/*  90 */       if (loopBody(iterator, clazz, minfo, context))
/*  91 */         edited = true;
/*     */     }
/*  93 */     ExceptionTable et = codeAttr.getExceptionTable();
/*  94 */     int n = et.size();
/*  95 */     for (int i = 0; i < n; i++) {
/*  96 */       Handler h = new Handler(et, i, iterator, clazz, minfo);
/*  97 */       edit(h);
/*  98 */       if (h.edited()) {
/*  99 */         edited = true;
/* 100 */         context.updateMax(h.locals(), h.stack());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 106 */     if (codeAttr.getMaxLocals() < context.maxLocals) {
/* 107 */       codeAttr.setMaxLocals(context.maxLocals);
/*     */     }
/* 109 */     codeAttr.setMaxStack(codeAttr.getMaxStack() + context.maxStack);
/*     */     try {
/* 111 */       if (edited)
/* 112 */         minfo.rebuildStackMapIf6(clazz.getClassPool(), clazz.getClassFile2());
/*     */     }
/*     */     catch (BadBytecode b)
/*     */     {
/* 116 */       throw new CannotCompileException(b.getMessage(), b);
/*     */     }
/*     */ 
/* 119 */     return edited;
/*     */   }
/*     */ 
/*     */   boolean doit(CtClass clazz, MethodInfo minfo, LoopContext context, CodeIterator iterator, int endPos)
/*     */     throws CannotCompileException
/*     */   {
/* 129 */     boolean edited = false;
/* 130 */     while ((iterator.hasNext()) && (iterator.lookAhead() < endPos)) {
/* 131 */       int size = iterator.getCodeLength();
/* 132 */       if (loopBody(iterator, clazz, minfo, context)) {
/* 133 */         edited = true;
/* 134 */         int size2 = iterator.getCodeLength();
/* 135 */         if (size != size2) {
/* 136 */           endPos += size2 - size;
/*     */         }
/*     */       }
/*     */     }
/* 140 */     return edited;
/*     */   }
/*     */ 
/*     */   final boolean loopBody(CodeIterator iterator, CtClass clazz, MethodInfo minfo, LoopContext context)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 180 */       Expr expr = null;
/* 181 */       int pos = iterator.next();
/* 182 */       int c = iterator.byteAt(pos);
/*     */ 
/* 184 */       if (c >= 178)
/*     */       {
/* 186 */         if (c < 188) {
/* 187 */           if ((c == 184) || (c == 185) || (c == 182))
/*     */           {
/* 190 */             expr = new MethodCall(pos, iterator, clazz, minfo);
/* 191 */             edit((MethodCall)expr);
/*     */           }
/* 193 */           else if ((c == 180) || (c == 178) || (c == 181) || (c == 179))
/*     */           {
/* 196 */             expr = new FieldAccess(pos, iterator, clazz, minfo, c);
/* 197 */             edit((FieldAccess)expr);
/*     */           }
/* 199 */           else if (c == 187) {
/* 200 */             int index = iterator.u16bitAt(pos + 1);
/* 201 */             context.newList = new NewOp(context.newList, pos, minfo.getConstPool().getClassInfo(index));
/*     */           }
/* 204 */           else if (c == 183) {
/* 205 */             NewOp newList = context.newList;
/* 206 */             if ((newList != null) && (minfo.getConstPool().isConstructor(newList.type, iterator.u16bitAt(pos + 1)) > 0))
/*     */             {
/* 209 */               expr = new NewExpr(pos, iterator, clazz, minfo, newList.type, newList.pos);
/*     */ 
/* 211 */               edit((NewExpr)expr);
/* 212 */               context.newList = newList.next;
/*     */             }
/*     */             else {
/* 215 */               MethodCall mcall = new MethodCall(pos, iterator, clazz, minfo);
/* 216 */               if (mcall.getMethodName().equals("<init>")) {
/* 217 */                 ConstructorCall ccall = new ConstructorCall(pos, iterator, clazz, minfo);
/* 218 */                 expr = ccall;
/* 219 */                 edit(ccall);
/*     */               }
/*     */               else {
/* 222 */                 expr = mcall;
/* 223 */                 edit(mcall);
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/* 229 */         else if ((c == 188) || (c == 189) || (c == 197))
/*     */         {
/* 231 */           expr = new NewArray(pos, iterator, clazz, minfo, c);
/* 232 */           edit((NewArray)expr);
/*     */         }
/* 234 */         else if (c == 193) {
/* 235 */           expr = new Instanceof(pos, iterator, clazz, minfo);
/* 236 */           edit((Instanceof)expr);
/*     */         }
/* 238 */         else if (c == 192) {
/* 239 */           expr = new Cast(pos, iterator, clazz, minfo);
/* 240 */           edit((Cast)expr);
/*     */         }
/*     */       }
/*     */ 
/* 244 */       if ((expr != null) && (expr.edited())) {
/* 245 */         context.updateMax(expr.locals(), expr.stack());
/* 246 */         return true;
/*     */       }
/*     */ 
/* 249 */       return false;
/*     */     }
/*     */     catch (BadBytecode e) {
/* 252 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void edit(NewExpr e)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void edit(NewArray a)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void edit(MethodCall m)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void edit(ConstructorCall c)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void edit(FieldAccess f)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void edit(Instanceof i)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void edit(Cast c)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void edit(Handler h)
/*     */     throws CannotCompileException
/*     */   {
/*     */   }
/*     */ 
/*     */   static final class LoopContext
/*     */   {
/*     */     ExprEditor.NewOp newList;
/*     */     int maxLocals;
/*     */     int maxStack;
/*     */ 
/*     */     LoopContext(int locals)
/*     */     {
/* 161 */       this.maxLocals = locals;
/* 162 */       this.maxStack = 0;
/* 163 */       this.newList = null;
/*     */     }
/*     */ 
/*     */     void updateMax(int locals, int stack) {
/* 167 */       if (this.maxLocals < locals) {
/* 168 */         this.maxLocals = locals;
/*     */       }
/* 170 */       if (this.maxStack < stack)
/* 171 */         this.maxStack = stack;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class NewOp
/*     */   {
/*     */     NewOp next;
/*     */     int pos;
/*     */     String type;
/*     */ 
/*     */     NewOp(NewOp n, int p, String t)
/*     */     {
/* 149 */       this.next = n;
/* 150 */       this.pos = p;
/* 151 */       this.type = t;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.ExprEditor
 * JD-Core Version:    0.6.2
 */