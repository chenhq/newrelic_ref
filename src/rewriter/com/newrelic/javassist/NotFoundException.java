/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ public class NotFoundException extends Exception
/*    */ {
/*    */   public NotFoundException(String msg)
/*    */   {
/* 23 */     super(msg);
/*    */   }
/*    */ 
/*    */   public NotFoundException(String msg, Exception e) {
/* 27 */     super(msg + " because of " + e.toString());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.NotFoundException
 * JD-Core Version:    0.6.2
 */