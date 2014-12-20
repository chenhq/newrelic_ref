/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.Gson;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class Event extends HarvestableArray
/*    */ {
/*    */   private long timestamp;
/*    */   private long eventName;
/* 14 */   private Map<String, String> params = new HashMap();
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 20 */     JsonArray array = new JsonArray();
/* 21 */     array.add(new JsonPrimitive(Long.valueOf(this.timestamp)));
/* 22 */     array.add(new JsonPrimitive(Long.valueOf(this.eventName)));
/* 23 */     array.add(new Gson().toJsonTree(this.params, GSON_STRING_MAP_TYPE));
/* 24 */     return array;
/*    */   }
/*    */ 
/*    */   public long getTimestamp() {
/* 28 */     return this.timestamp;
/*    */   }
/*    */ 
/*    */   public void setTimestamp(long timestamp) {
/* 32 */     this.timestamp = timestamp;
/*    */   }
/*    */ 
/*    */   public long getEventName() {
/* 36 */     return this.eventName;
/*    */   }
/*    */ 
/*    */   public void setEventName(long eventName) {
/* 40 */     this.eventName = eventName;
/*    */   }
/*    */ 
/*    */   public Map<String, String> getParams() {
/* 44 */     return this.params;
/*    */   }
/*    */ 
/*    */   public void setParams(Map<String, String> params) {
/* 48 */     this.params = params;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.Event
 * JD-Core Version:    0.6.2
 */