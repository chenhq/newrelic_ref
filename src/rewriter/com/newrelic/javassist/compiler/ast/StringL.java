/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class StringL extends ASTree
/*    */ {
/*    */   protected String text;
/*    */ 
/*    */   public StringL(String t)
/*    */   {
/* 27 */     this.text = t;
/*    */   }
/*    */   public String get() {
/* 30 */     return this.text;
/*    */   }
/* 32 */   public String toString() { return "\"" + this.text + "\""; } 
/*    */   public void accept(Visitor v) throws CompileError {
/* 34 */     v.atStringL(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.StringL
 * JD-Core Version:    0.6.2
 */