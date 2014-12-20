/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.TaskQueue;
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.agent.android.stats.StatsEngine;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import java.text.MessageFormat;
/*    */ 
/*    */ public class AgentHealth extends HarvestableArray
/*    */ {
/* 14 */   private final AgentHealthExceptions agentHealthExceptions = new AgentHealthExceptions();
/*    */ 
/*    */   public static void noticeException(Exception exception) {
/* 17 */     noticeException(new AgentHealthException(exception));
/*    */   }
/*    */ 
/*    */   public static void noticeException(AgentHealthException exception) {
/* 21 */     StatsEngine.get().inc(MessageFormat.format("Supportability/AgentHealth/Exception/{0}/{1}/{2}", new Object[] { exception.getSourceClass(), exception.getSourceMethod(), exception.getExceptionClass() }));
/*    */ 
/* 26 */     TaskQueue.queue(exception);
/*    */   }
/*    */ 
/*    */   public void addException(AgentHealthException exception) {
/* 30 */     this.agentHealthExceptions.add(exception);
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 34 */     this.agentHealthExceptions.clear();
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 39 */     JsonArray data = new JsonArray();
/*    */ 
/* 41 */     if (!this.agentHealthExceptions.isEmpty()) {
/* 42 */       data.add(this.agentHealthExceptions.asJsonObject());
/*    */     }
/*    */ 
/* 45 */     return data;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.AgentHealth
 * JD-Core Version:    0.6.2
 */