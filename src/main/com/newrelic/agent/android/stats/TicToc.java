/*    */ package com.newrelic.agent.android.stats;
/*    */ 
/*    */ public class TicToc
/*    */ {
/*    */   private long startTime;
/*    */   private long endTime;
/*    */   private State state;
/*    */ 
/*    */   public void tic()
/*    */   {
/* 14 */     this.state = State.STARTED;
/*    */ 
/* 16 */     this.startTime = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public long toc() {
/* 20 */     this.endTime = System.currentTimeMillis();
/*    */ 
/* 22 */     if (this.state == State.STARTED) {
/* 23 */       this.state = State.STOPPED;
/* 24 */       return this.endTime - this.startTime;
/*    */     }
/* 26 */     return -1L;
/*    */   }
/*    */ 
/*    */   private static enum State
/*    */   {
/*  5 */     STOPPED, 
/*  6 */     STARTED;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.stats.TicToc
 * JD-Core Version:    0.6.2
 */