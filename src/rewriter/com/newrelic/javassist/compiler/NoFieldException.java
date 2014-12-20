/*    */ package com.newrelic.javassist.compiler;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.ast.ASTree;
/*    */ 
/*    */ public class NoFieldException extends CompileError
/*    */ {
/*    */   private String fieldName;
/*    */   private ASTree expr;
/*    */ 
/*    */   public NoFieldException(String name, ASTree e)
/*    */   {
/* 27 */     super("no such field: " + name);
/* 28 */     this.fieldName = name;
/* 29 */     this.expr = e;
/*    */   }
/*    */ 
/*    */   public String getField()
/*    */   {
/* 34 */     return this.fieldName;
/*    */   }
/*    */ 
/*    */   public ASTree getExpr() {
/* 38 */     return this.expr;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.NoFieldException
 * JD-Core Version:    0.6.2
 */