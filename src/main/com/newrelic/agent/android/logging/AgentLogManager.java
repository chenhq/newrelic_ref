/*    */ package com.newrelic.agent.android.logging;
/*    */ 
/*    */ public class AgentLogManager
/*    */ {
/*  4 */   private static DefaultAgentLog instance = new DefaultAgentLog();
/*    */ 
/*    */   public static AgentLog getAgentLog() {
/*  7 */     return instance;
/*    */   }
/*    */ 
/*    */   public static void setAgentLog(AgentLog instance) {
/* 11 */     instance.setImpl(instance);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.logging.AgentLogManager
 * JD-Core Version:    0.6.2
 */