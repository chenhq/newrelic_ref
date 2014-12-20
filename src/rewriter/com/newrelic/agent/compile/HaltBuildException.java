/*    */ package com.newrelic.agent.compile;
/*    */ 
/*    */ public class HaltBuildException extends RuntimeException
/*    */ {
/*    */   public HaltBuildException(String message)
/*    */   {
/*  8 */     super(message);
/*    */   }
/*    */ 
/*    */   public HaltBuildException(Exception e) {
/* 12 */     super(e);
/*    */   }
/*    */ 
/*    */   public HaltBuildException(String message, Exception e) {
/* 16 */     super(message, e);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.HaltBuildException
 * JD-Core Version:    0.6.2
 */