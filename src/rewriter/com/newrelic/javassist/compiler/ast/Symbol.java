/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class Symbol extends ASTree
/*    */ {
/*    */   protected String identifier;
/*    */ 
/*    */   public Symbol(String sym)
/*    */   {
/* 27 */     this.identifier = sym;
/*    */   }
/*    */   public String get() {
/* 30 */     return this.identifier;
/*    */   }
/* 32 */   public String toString() { return this.identifier; } 
/*    */   public void accept(Visitor v) throws CompileError {
/* 34 */     v.atSymbol(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.Symbol
 * JD-Core Version:    0.6.2
 */