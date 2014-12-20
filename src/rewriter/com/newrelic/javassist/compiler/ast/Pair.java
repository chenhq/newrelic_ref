/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class Pair extends ASTree
/*    */ {
/*    */   protected ASTree left;
/*    */   protected ASTree right;
/*    */ 
/*    */   public Pair(ASTree _left, ASTree _right)
/*    */   {
/* 28 */     this.left = _left;
/* 29 */     this.right = _right;
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 32 */     v.atPair(this);
/*    */   }
/*    */   public String toString() {
/* 35 */     StringBuffer sbuf = new StringBuffer();
/* 36 */     sbuf.append("(<Pair> ");
/* 37 */     sbuf.append(this.left == null ? "<null>" : this.left.toString());
/* 38 */     sbuf.append(" . ");
/* 39 */     sbuf.append(this.right == null ? "<null>" : this.right.toString());
/* 40 */     sbuf.append(')');
/* 41 */     return sbuf.toString();
/*    */   }
/*    */   public ASTree getLeft() {
/* 44 */     return this.left;
/*    */   }
/* 46 */   public ASTree getRight() { return this.right; } 
/*    */   public void setLeft(ASTree _left) {
/* 48 */     this.left = _left;
/*    */   }
/* 50 */   public void setRight(ASTree _right) { this.right = _right; }
/*    */ 
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Pair
 * JD-Core Version:    0.6.2
 */