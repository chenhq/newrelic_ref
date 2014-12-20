/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ import com.newrelic.javassist.compiler.TokenId;
/*    */ 
/*    */ public class Stmnt extends ASTList
/*    */   implements TokenId
/*    */ {
/*    */   protected int operatorId;
/*    */ 
/*    */   public Stmnt(int op, ASTree _head, ASTList _tail)
/*    */   {
/* 28 */     super(_head, _tail);
/* 29 */     this.operatorId = op;
/*    */   }
/*    */ 
/*    */   public Stmnt(int op, ASTree _head) {
/* 33 */     super(_head);
/* 34 */     this.operatorId = op;
/*    */   }
/*    */ 
/*    */   public Stmnt(int op) {
/* 38 */     this(op, null);
/*    */   }
/*    */ 
/*    */   public static Stmnt make(int op, ASTree oprand1, ASTree oprand2) {
/* 42 */     return new Stmnt(op, oprand1, new ASTList(oprand2));
/*    */   }
/*    */ 
/*    */   public static Stmnt make(int op, ASTree op1, ASTree op2, ASTree op3) {
/* 46 */     return new Stmnt(op, op1, new ASTList(op2, new ASTList(op3)));
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 49 */     v.atStmnt(this);
/*    */   }
/* 51 */   public int getOperator() { return this.operatorId; }
/*    */ 
/*    */   protected String getTag() {
/* 54 */     if (this.operatorId < 128) {
/* 55 */       return "stmnt:" + (char)this.operatorId;
/*    */     }
/* 57 */     return "stmnt:" + this.operatorId;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Stmnt
 * JD-Core Version:    0.6.2
 */