/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonElement;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ActivityHistory extends HarvestableArray
/*    */ {
/*    */   private final List<ActivitySighting> activityHistory;
/*    */ 
/*    */   public ActivityHistory(List<ActivitySighting> activityHistory)
/*    */   {
/* 19 */     this.activityHistory = activityHistory;
/*    */   }
/*    */ 
/*    */   public int size() {
/* 23 */     return this.activityHistory.size();
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 28 */     JsonArray data = new JsonArray();
/*    */ 
/* 30 */     for (ActivitySighting sighting : this.activityHistory) {
/* 31 */       data.add(sighting.asJsonArray());
/*    */     }
/*    */ 
/* 34 */     return data;
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArrayWithoutDuration() {
/* 38 */     JsonArray data = new JsonArray();
/*    */ 
/* 40 */     for (ActivitySighting sighting : this.activityHistory) {
/* 41 */       data.add(sighting.asJsonArrayWithoutDuration());
/*    */     }
/*    */ 
/* 44 */     return data;
/*    */   }
/*    */ 
/*    */   public static ActivityHistory newFromJson(JsonArray jsonArray) {
/* 48 */     List sightings = new ArrayList();
/*    */ 
/* 50 */     for (JsonElement element : jsonArray) {
/* 51 */       sightings.add(ActivitySighting.newFromJson(element.getAsJsonArray()));
/*    */     }
/*    */ 
/* 54 */     return new ActivityHistory(sightings);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.ActivityHistory
 * JD-Core Version:    0.6.2
 */