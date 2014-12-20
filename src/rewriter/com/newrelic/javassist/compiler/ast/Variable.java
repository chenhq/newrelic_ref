/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class Variable extends Symbol
/*    */ {
/*    */   protected Declarator declarator;
/*    */ 
/*    */   public Variable(String sym, Declarator d)
/*    */   {
/* 27 */     super(sym);
/* 28 */     this.declarator = d;
/*    */   }
/*    */   public Declarator getDeclarator() {
/* 31 */     return this.declarator;
/*    */   }
/*    */   public String toString() {
/* 34 */     return this.identifier + ":" + this.declarator.getType();
/*    */   }
/*    */   public void accept(Visitor v) throws CompileError {
/* 37 */     v.atVariable(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Variable
 * JD-Core Version:    0.6.2
 */