/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class InstanceOfExpr extends CastExpr
/*    */ {
/*    */   public InstanceOfExpr(ASTList className, int dim, ASTree expr)
/*    */   {
/* 25 */     super(className, dim, expr);
/*    */   }
/*    */ 
/*    */   public InstanceOfExpr(int type, int dim, ASTree expr) {
/* 29 */     super(type, dim, expr);
/*    */   }
/*    */ 
/*    */   public String getTag() {
/* 33 */     return "instanceof:" + this.castType + ":" + this.arrayDim;
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v) throws CompileError {
/* 37 */     v.atInstanceOfExpr(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.InstanceOfExpr
 * JD-Core Version:    0.6.2
 */