/*    */ package com.newrelic.javassist.tools.reflect;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ 
/*    */ public class CannotInvokeException extends RuntimeException
/*    */ {
/* 31 */   private Throwable err = null;
/*    */ 
/*    */   public Throwable getReason()
/*    */   {
/* 36 */     return this.err;
/*    */   }
/*    */ 
/*    */   public CannotInvokeException(String reason)
/*    */   {
/* 42 */     super(reason);
/*    */   }
/*    */ 
/*    */   public CannotInvokeException(InvocationTargetException e)
/*    */   {
/* 49 */     super("by " + e.getTargetException().toString());
/* 50 */     this.err = e.getTargetException();
/*    */   }
/*    */ 
/*    */   public CannotInvokeException(IllegalAccessException e)
/*    */   {
/* 57 */     super("by " + e.toString());
/* 58 */     this.err = e;
/*    */   }
/*    */ 
/*    */   public CannotInvokeException(ClassNotFoundException e)
/*    */   {
/* 65 */     super("by " + e.toString());
/* 66 */     this.err = e;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.CannotInvokeException
 * JD-Core Version:    0.6.2
 */