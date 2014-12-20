/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ import com.newrelic.javassist.compiler.TokenId;
/*    */ 
/*    */ public class Expr extends ASTList
/*    */   implements TokenId
/*    */ {
/*    */   protected int operatorId;
/*    */ 
/*    */   Expr(int op, ASTree _head, ASTList _tail)
/*    */   {
/* 34 */     super(_head, _tail);
/* 35 */     this.operatorId = op;
/*    */   }
/*    */ 
/*    */   Expr(int op, ASTree _head) {
/* 39 */     super(_head);
/* 40 */     this.operatorId = op;
/*    */   }
/*    */ 
/*    */   public static Expr make(int op, ASTree oprand1, ASTree oprand2) {
/* 44 */     return new Expr(op, oprand1, new ASTList(oprand2));
/*    */   }
/*    */ 
/*    */   public static Expr make(int op, ASTree oprand1) {
/* 48 */     return new Expr(op, oprand1);
/*    */   }
/*    */   public int getOperator() {
/* 51 */     return this.operatorId;
/*    */   }
/* 53 */   public void setOperator(int op) { this.operatorId = op; } 
/*    */   public ASTree oprand1() {
/* 55 */     return getLeft();
/*    */   }
/*    */   public void setOprand1(ASTree expr) {
/* 58 */     setLeft(expr);
/*    */   }
/*    */   public ASTree oprand2() {
/* 61 */     return getRight().getLeft();
/*    */   }
/*    */   public void setOprand2(ASTree expr) {
/* 64 */     getRight().setLeft(expr);
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 67 */     v.atExpr(this);
/*    */   }
/*    */   public String getName() {
/* 70 */     int id = this.operatorId;
/* 71 */     if (id < 128)
/* 72 */       return String.valueOf((char)id);
/* 73 */     if ((350 <= id) && (id <= 371))
/* 74 */       return opNames[(id - 350)];
/* 75 */     if (id == 323) {
/* 76 */       return "instanceof";
/*    */     }
/* 78 */     return String.valueOf(id);
/*    */   }
/*    */ 
/*    */   protected String getTag() {
/* 82 */     return "op:" + getName();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Expr
 * JD-Core Version:    0.6.2
 */