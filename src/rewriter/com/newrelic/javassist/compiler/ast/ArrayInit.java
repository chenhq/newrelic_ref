/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class ArrayInit extends ASTList
/*    */ {
/*    */   public ArrayInit(ASTree firstElement)
/*    */   {
/* 25 */     super(firstElement);
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 28 */     v.atArrayInit(this);
/*    */   }
/* 30 */   public String getTag() { return "array"; }
/*    */ 
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.ArrayInit
 * JD-Core Version:    0.6.2
 */