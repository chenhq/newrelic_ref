/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class Keyword extends ASTree
/*    */ {
/*    */   protected int tokenId;
/*    */ 
/*    */   public Keyword(int token)
/*    */   {
/* 27 */     this.tokenId = token;
/*    */   }
/*    */   public int get() {
/* 30 */     return this.tokenId;
/*    */   }
/* 32 */   public String toString() { return "id:" + this.tokenId; } 
/*    */   public void accept(Visitor v) throws CompileError {
/* 34 */     v.atKeyword(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Keyword
 * JD-Core Version:    0.6.2
 */