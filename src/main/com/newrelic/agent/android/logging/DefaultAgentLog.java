/*    */ package com.newrelic.agent.android.logging;
/*    */ 
/*    */ public class DefaultAgentLog
/*    */   implements AgentLog
/*    */ {
/*  5 */   private AgentLog impl = new NullAgentLog();
/*    */ 
/*    */   public void setImpl(AgentLog impl) {
/*  8 */     synchronized (this) {
/*  9 */       this.impl = impl;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void debug(String message)
/*    */   {
/* 15 */     synchronized (this) {
/* 16 */       this.impl.debug(message);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void info(String message)
/*    */   {
/* 22 */     synchronized (this) {
/* 23 */       this.impl.info(message);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void verbose(String message)
/*    */   {
/* 29 */     synchronized (this) {
/* 30 */       this.impl.verbose(message);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void warning(String message)
/*    */   {
/* 36 */     synchronized (this) {
/* 37 */       this.impl.warning(message);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/* 43 */     synchronized (this) {
/* 44 */       this.impl.error(message);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void error(String message, Throwable cause)
/*    */   {
/* 50 */     synchronized (this) {
/* 51 */       this.impl.error(message, cause);
/*    */     }
/*    */   }
/*    */ 
/*    */   public int getLevel()
/*    */   {
/* 57 */     synchronized (this) {
/* 58 */       return this.impl.getLevel();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setLevel(int level)
/*    */   {
/* 64 */     synchronized (this) {
/* 65 */       this.impl.setLevel(level);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.logging.DefaultAgentLog
 * JD-Core Version:    0.6.2
 */