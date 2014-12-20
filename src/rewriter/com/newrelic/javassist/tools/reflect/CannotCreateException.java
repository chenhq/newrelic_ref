/*    */ package com.newrelic.javassist.tools.reflect;
/*    */ 
/*    */ public class CannotCreateException extends Exception
/*    */ {
/*    */   public CannotCreateException(String s)
/*    */   {
/* 23 */     super(s);
/*    */   }
/*    */ 
/*    */   public CannotCreateException(Exception e) {
/* 27 */     super("by " + e.toString());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.CannotCreateException
 * JD-Core Version:    0.6.2
 */