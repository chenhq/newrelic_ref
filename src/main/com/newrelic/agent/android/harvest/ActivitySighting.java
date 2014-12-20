/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonElement;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class ActivitySighting extends HarvestableArray
/*    */ {
/*    */   private String name;
/*    */   private final long timestampMs;
/* 14 */   private long durationMs = 0L;
/*    */ 
/*    */   public ActivitySighting(long timestampMs, String name)
/*    */   {
/* 19 */     this.timestampMs = timestampMs;
/* 20 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 24 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 28 */     synchronized (this) {
/* 29 */       this.name = name;
/*    */     }
/*    */   }
/*    */ 
/*    */   public long getTimestampMs() {
/* 34 */     return this.timestampMs;
/*    */   }
/*    */ 
/*    */   public long getDuration() {
/* 38 */     return this.durationMs;
/*    */   }
/*    */ 
/*    */   public void end(long endTimestampMs) {
/* 42 */     synchronized (this) {
/* 43 */       this.durationMs = (endTimestampMs - this.timestampMs);
/*    */     }
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 50 */     JsonArray data = new JsonArray();
/* 51 */     synchronized (this) {
/* 52 */       data.add(new JsonPrimitive(this.name));
/* 53 */       data.add(new JsonPrimitive(Long.valueOf(this.timestampMs)));
/* 54 */       data.add(new JsonPrimitive(Long.valueOf(this.durationMs)));
/*    */     }
/* 56 */     return data;
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArrayWithoutDuration()
/*    */   {
/* 61 */     JsonArray data = new JsonArray();
/* 62 */     synchronized (this) {
/* 63 */       data.add(new JsonPrimitive(Long.valueOf(this.timestampMs)));
/* 64 */       data.add(new JsonPrimitive(this.name));
/*    */     }
/* 66 */     return data;
/*    */   }
/*    */ 
/*    */   public static ActivitySighting newFromJson(JsonArray jsonArray) {
/* 70 */     return new ActivitySighting(jsonArray.get(0).getAsLong(), jsonArray.get(1).getAsString());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.ActivitySighting
 * JD-Core Version:    0.6.2
 */