/*    */ package com.newrelic.javassist.compiler;
/*    */ 
/*    */ public class SyntaxError extends CompileError
/*    */ {
/*    */   public SyntaxError(Lex lexer)
/*    */   {
/* 20 */     super("syntax error near \"" + lexer.getTextAround() + "\"", lexer);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.SyntaxError
 * JD-Core Version:    0.6.2
 */