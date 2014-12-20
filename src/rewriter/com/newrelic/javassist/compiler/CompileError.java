/*    */ package com.newrelic.javassist.compiler;
/*    */ 
/*    */ import com.newrelic.javassist.CannotCompileException;
/*    */ import com.newrelic.javassist.NotFoundException;
/*    */ 
/*    */ public class CompileError extends Exception
/*    */ {
/*    */   private Lex lex;
/*    */   private String reason;
/*    */ 
/*    */   public CompileError(String s, Lex l)
/*    */   {
/* 26 */     this.reason = s;
/* 27 */     this.lex = l;
/*    */   }
/*    */ 
/*    */   public CompileError(String s) {
/* 31 */     this.reason = s;
/* 32 */     this.lex = null;
/*    */   }
/*    */ 
/*    */   public CompileError(CannotCompileException e) {
/* 36 */     this(e.getReason());
/*    */   }
/*    */ 
/*    */   public CompileError(NotFoundException e) {
/* 40 */     this("cannot find " + e.getMessage());
/*    */   }
/*    */   public Lex getLex() {
/* 43 */     return this.lex;
/*    */   }
/*    */   public String getMessage() {
/* 46 */     return this.reason;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 50 */     return "compile error: " + this.reason;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.CompileError
 * JD-Core Version:    0.6.2
 */