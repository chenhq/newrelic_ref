/*    */ package com.newrelic.javassist.tools.web;
/*    */ 
/*    */ public class BadHttpRequest extends Exception
/*    */ {
/*    */   private Exception e;
/*    */ 
/*    */   public BadHttpRequest()
/*    */   {
/* 24 */     this.e = null;
/*    */   }
/* 26 */   public BadHttpRequest(Exception _e) { this.e = _e; }
/*    */ 
/*    */   public String toString() {
/* 29 */     if (this.e == null) {
/* 30 */       return super.toString();
/*    */     }
/* 32 */     return this.e.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.web.BadHttpRequest
 * JD-Core Version:    0.6.2
 */