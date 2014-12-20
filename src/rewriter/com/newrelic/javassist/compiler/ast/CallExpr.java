/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ import com.newrelic.javassist.compiler.MemberResolver.Method;
/*    */ 
/*    */ public class CallExpr extends Expr
/*    */ {
/*    */   private MemberResolver.Method method;
/*    */ 
/*    */   private CallExpr(ASTree _head, ASTList _tail)
/*    */   {
/* 29 */     super(67, _head, _tail);
/* 30 */     this.method = null;
/*    */   }
/*    */ 
/*    */   public void setMethod(MemberResolver.Method m) {
/* 34 */     this.method = m;
/*    */   }
/*    */ 
/*    */   public MemberResolver.Method getMethod() {
/* 38 */     return this.method;
/*    */   }
/*    */ 
/*    */   public static CallExpr makeCall(ASTree target, ASTree args) {
/* 42 */     return new CallExpr(target, new ASTList(args));
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 45 */     v.atCallExpr(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.CallExpr
 * JD-Core Version:    0.6.2
 */