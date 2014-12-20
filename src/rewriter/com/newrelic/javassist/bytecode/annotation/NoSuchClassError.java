/*    */ package com.newrelic.javassist.bytecode.annotation;
/*    */ 
/*    */ public class NoSuchClassError extends Error
/*    */ {
/*    */   private String className;
/*    */ 
/*    */   public NoSuchClassError(String className, Error cause)
/*    */   {
/* 29 */     super(cause.toString(), cause);
/* 30 */     this.className = className;
/*    */   }
/*    */ 
/*    */   public String getClassName()
/*    */   {
/* 37 */     return this.className;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.NoSuchClassError
 * JD-Core Version:    0.6.2
 */