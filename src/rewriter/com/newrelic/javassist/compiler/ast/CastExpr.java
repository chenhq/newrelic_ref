/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ import com.newrelic.javassist.compiler.TokenId;
/*    */ 
/*    */ public class CastExpr extends ASTList
/*    */   implements TokenId
/*    */ {
/*    */   protected int castType;
/*    */   protected int arrayDim;
/*    */ 
/*    */   public CastExpr(ASTList className, int dim, ASTree expr)
/*    */   {
/* 29 */     super(className, new ASTList(expr));
/* 30 */     this.castType = 307;
/* 31 */     this.arrayDim = dim;
/*    */   }
/*    */ 
/*    */   public CastExpr(int type, int dim, ASTree expr) {
/* 35 */     super(null, new ASTList(expr));
/* 36 */     this.castType = type;
/* 37 */     this.arrayDim = dim;
/*    */   }
/*    */ 
/*    */   public int getType()
/*    */   {
/* 42 */     return this.castType;
/*    */   }
/* 44 */   public int getArrayDim() { return this.arrayDim; } 
/*    */   public ASTList getClassName() {
/* 46 */     return (ASTList)getLeft();
/*    */   }
/* 48 */   public ASTree getOprand() { return getRight().getLeft(); } 
/*    */   public void setOprand(ASTree t) {
/* 50 */     getRight().setLeft(t);
/*    */   }
/* 52 */   public String getTag() { return "cast:" + this.castType + ":" + this.arrayDim; } 
/*    */   public void accept(Visitor v) throws CompileError {
/* 54 */     v.atCastExpr(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.CastExpr
 * JD-Core Version:    0.6.2
 */