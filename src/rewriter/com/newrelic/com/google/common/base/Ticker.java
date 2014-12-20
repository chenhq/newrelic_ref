/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public abstract class Ticker
/*    */ {
/* 57 */   private static final Ticker SYSTEM_TICKER = new Ticker()
/*    */   {
/*    */     public long read() {
/* 60 */       return Platform.systemNanoTime();
/*    */     }
/* 57 */   };
/*    */ 
/*    */   public abstract long read();
/*    */ 
/*    */   public static Ticker systemTicker()
/*    */   {
/* 54 */     return SYSTEM_TICKER;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Ticker
 * JD-Core Version:    0.6.2
 */