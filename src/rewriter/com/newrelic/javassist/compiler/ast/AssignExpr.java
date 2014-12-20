/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class AssignExpr extends Expr
/*    */ {
/*    */   private AssignExpr(int op, ASTree _head, ASTList _tail)
/*    */   {
/* 29 */     super(op, _head, _tail);
/*    */   }
/*    */ 
/*    */   public static AssignExpr makeAssign(int op, ASTree oprand1, ASTree oprand2)
/*    */   {
/* 34 */     return new AssignExpr(op, oprand1, new ASTList(oprand2));
/*    */   }
/*    */ 
/*    */   public void accept(Visitor v) throws CompileError {
/* 38 */     v.atAssignExpr(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.AssignExpr
 * JD-Core Version:    0.6.2
 */