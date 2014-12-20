/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class BinExpr extends Expr
/*    */ {
/*    */   private BinExpr(int op, ASTree _head, ASTList _tail)
/*    */   {
/* 33 */     super(op, _head, _tail);
/*    */   }
/*    */ 
/*    */   public static BinExpr makeBin(int op, ASTree oprand1, ASTree oprand2) {
/* 37 */     return new BinExpr(op, oprand1, new ASTList(oprand2));
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 40 */     v.atBinExpr(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.BinExpr
 * JD-Core Version:    0.6.2
 */