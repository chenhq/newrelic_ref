/*    */ package com.newrelic.agent.android.logging;
/*    */ 
/*    */ public class NullAgentLog
/*    */   implements AgentLog
/*    */ {
/*    */   public void debug(String message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void info(String message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void verbose(String message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void error(String message, Throwable cause)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void warning(String message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public int getLevel()
/*    */   {
/* 30 */     return 5;
/*    */   }
/*    */ 
/*    */   public void setLevel(int level)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.logging.NullAgentLog
 * JD-Core Version:    0.6.2
 */