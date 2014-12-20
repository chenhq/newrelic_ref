/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class CondExpr extends ASTList
/*    */ {
/*    */   public CondExpr(ASTree cond, ASTree thenp, ASTree elsep)
/*    */   {
/* 25 */     super(cond, new ASTList(thenp, new ASTList(elsep)));
/*    */   }
/*    */   public ASTree condExpr() {
/* 28 */     return head();
/*    */   }
/* 30 */   public void setCond(ASTree t) { setHead(t); } 
/*    */   public ASTree thenExpr() {
/* 32 */     return tail().head();
/*    */   }
/* 34 */   public void setThen(ASTree t) { tail().setHead(t); } 
/*    */   public ASTree elseExpr() {
/* 36 */     return tail().tail().head();
/*    */   }
/* 38 */   public void setElse(ASTree t) { tail().tail().setHead(t); } 
/*    */   public String getTag() {
/* 40 */     return "?:";
/*    */   }
/* 42 */   public void accept(Visitor v) throws CompileError { v.atCondExpr(this); }
/*    */ 
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.CondExpr
 * JD-Core Version:    0.6.2
 */