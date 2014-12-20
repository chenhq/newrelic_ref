/*    */ package com.newrelic.org.reflections;
/*    */ 
/*    */ public class ReflectionsException extends RuntimeException
/*    */ {
/*    */   public ReflectionsException(String message)
/*    */   {
/* 11 */     super(message);
/*    */   }
/*    */ 
/*    */   public ReflectionsException(String message, Throwable cause) {
/* 15 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public ReflectionsException(Throwable cause) {
/* 19 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.ReflectionsException
 * JD-Core Version:    0.6.2
 */