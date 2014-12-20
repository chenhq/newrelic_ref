/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonObject;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ public class AgentHealthExceptions extends HarvestableObject
/*    */ {
/* 15 */   private static final JsonArray keyArray = new JsonArray();
/*    */ 
/* 17 */   private final Map<String, AgentHealthException> agentHealthExceptions = new ConcurrentHashMap();
/*    */ 
/*    */   public AgentHealthExceptions()
/*    */   {
/* 22 */     keyArray.add(new JsonPrimitive("ExceptionClass"));
/* 23 */     keyArray.add(new JsonPrimitive("Message"));
/* 24 */     keyArray.add(new JsonPrimitive("ThreadName"));
/* 25 */     keyArray.add(new JsonPrimitive("CallStack"));
/* 26 */     keyArray.add(new JsonPrimitive("Count"));
/* 27 */     keyArray.add(new JsonPrimitive("Extras"));
/*    */   }
/*    */ 
/*    */   public void add(AgentHealthException exception)
/*    */   {
/* 32 */     String aggregationKey = exception.getExceptionClass() + exception.getStackTrace()[0].toString();
/* 33 */     synchronized (this.agentHealthExceptions) {
/* 34 */       AgentHealthException healthException = (AgentHealthException)this.agentHealthExceptions.get(aggregationKey);
/*    */ 
/* 36 */       if (healthException == null)
/* 37 */         this.agentHealthExceptions.put(aggregationKey, exception);
/*    */       else
/* 39 */         healthException.increment();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 45 */     synchronized (this.agentHealthExceptions) {
/* 46 */       this.agentHealthExceptions.clear();
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean isEmpty() {
/* 51 */     return this.agentHealthExceptions.isEmpty();
/*    */   }
/*    */ 
/*    */   public Map<String, AgentHealthException> getAgentHealthExceptions() {
/* 55 */     return this.agentHealthExceptions;
/*    */   }
/*    */ 
/*    */   public JsonObject asJsonObject()
/*    */   {
/* 60 */     JsonObject exceptions = new JsonObject();
/*    */ 
/* 62 */     JsonArray data = new JsonArray();
/*    */ 
/* 64 */     for (AgentHealthException exception : this.agentHealthExceptions.values()) {
/* 65 */       data.add(exception.asJsonArray());
/*    */     }
/*    */ 
/* 68 */     exceptions.add("Type", new JsonPrimitive("AgentErrors"));
/* 69 */     exceptions.add("Keys", keyArray);
/* 70 */     exceptions.add("Data", data);
/*    */ 
/* 72 */     return exceptions;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.AgentHealthExceptions
 * JD-Core Version:    0.6.2
 */