/*    */ package com.newrelic.agent.android.activity.config;
/*    */ 
/*    */ import com.newrelic.agent.android.logging.AgentLog;
/*    */ import com.newrelic.agent.android.logging.AgentLogManager;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonDeserializationContext;
/*    */ import com.newrelic.com.google.gson.JsonDeserializer;
/*    */ import com.newrelic.com.google.gson.JsonElement;
/*    */ import com.newrelic.com.google.gson.JsonParseException;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ public class ActivityTraceConfigurationDeserializer
/*    */   implements JsonDeserializer<ActivityTraceConfiguration>
/*    */ {
/* 10 */   private final AgentLog log = AgentLogManager.getAgentLog();
/*    */ 
/*    */   public ActivityTraceConfiguration deserialize(JsonElement root, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
/*    */   {
/* 14 */     ActivityTraceConfiguration configuration = new ActivityTraceConfiguration();
/*    */ 
/* 16 */     if (!root.isJsonArray()) {
/* 17 */       error("Expected root element to be an array.");
/* 18 */       return null;
/*    */     }
/*    */ 
/* 21 */     JsonArray array = root.getAsJsonArray();
/*    */ 
/* 23 */     if (array.size() != 2) {
/* 24 */       error("Root array must contain 2 elements.");
/* 25 */       return null;
/*    */     }
/*    */ 
/* 28 */     Integer maxTotalTraceCount = getInteger(array.get(0));
/* 29 */     if (maxTotalTraceCount == null) {
/* 30 */       return null;
/*    */     }
/* 32 */     if (maxTotalTraceCount.intValue() < 0) {
/* 33 */       error("The first element of the root array must not be negative.");
/* 34 */       return null;
/*    */     }
/*    */ 
/* 37 */     configuration.setMaxTotalTraceCount(maxTotalTraceCount.intValue());
/*    */ 
/* 39 */     return configuration;
/*    */   }
/*    */ 
/*    */   private Integer getInteger(JsonElement element) {
/* 43 */     if (!element.isJsonPrimitive()) {
/* 44 */       error("Expected an integer.");
/* 45 */       return null;
/*    */     }
/*    */ 
/* 48 */     JsonPrimitive primitive = element.getAsJsonPrimitive();
/* 49 */     if (!primitive.isNumber()) {
/* 50 */       error("Expected an integer.");
/* 51 */       return null;
/*    */     }
/*    */ 
/* 54 */     int value = primitive.getAsInt();
/* 55 */     if (value < 0) {
/* 56 */       error("Integer value must not be negative");
/* 57 */       return null;
/*    */     }
/* 59 */     return Integer.valueOf(value);
/*    */   }
/*    */ 
/*    */   private void error(String message) {
/* 63 */     this.log.error("ActivityTraceConfigurationDeserializer: " + message);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.activity.config.ActivityTraceConfigurationDeserializer
 * JD-Core Version:    0.6.2
 */