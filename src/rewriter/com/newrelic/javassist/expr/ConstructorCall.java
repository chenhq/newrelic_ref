/*    */ package com.newrelic.javassist.expr;
/*    */ 
/*    */ import com.newrelic.javassist.CtClass;
/*    */ import com.newrelic.javassist.CtConstructor;
/*    */ import com.newrelic.javassist.CtMethod;
/*    */ import com.newrelic.javassist.NotFoundException;
/*    */ import com.newrelic.javassist.bytecode.CodeIterator;
/*    */ import com.newrelic.javassist.bytecode.MethodInfo;
/*    */ 
/*    */ public class ConstructorCall extends MethodCall
/*    */ {
/*    */   protected ConstructorCall(int pos, CodeIterator i, CtClass decl, MethodInfo m)
/*    */   {
/* 36 */     super(pos, i, decl, m);
/*    */   }
/*    */ 
/*    */   public String getMethodName()
/*    */   {
/* 43 */     return isSuper() ? "super" : "this";
/*    */   }
/*    */ 
/*    */   public CtMethod getMethod()
/*    */     throws NotFoundException
/*    */   {
/* 52 */     throw new NotFoundException("this is a constructor call.  Call getConstructor().");
/*    */   }
/*    */ 
/*    */   public CtConstructor getConstructor()
/*    */     throws NotFoundException
/*    */   {
/* 59 */     return getCtClass().getConstructor(getSignature());
/*    */   }
/*    */ 
/*    */   public boolean isSuper()
/*    */   {
/* 67 */     return super.isSuper();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.expr.ConstructorCall
 * JD-Core Version:    0.6.2
 */