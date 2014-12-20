/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class ActivityTraces extends HarvestableArray
/*    */ {
/* 14 */   private final Collection<ActivityTrace> activityTraces = new ArrayList();
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 18 */     JsonArray array = new JsonArray();
/*    */ 
/* 20 */     for (ActivityTrace activityTrace : this.activityTraces) {
/* 21 */       array.add(activityTrace.asJson());
/*    */     }
/*    */ 
/* 24 */     return array;
/*    */   }
/*    */ 
/*    */   public synchronized void add(ActivityTrace activityTrace) {
/* 28 */     this.activityTraces.add(activityTrace);
/*    */   }
/*    */ 
/*    */   public synchronized void remove(ActivityTrace activityTrace) {
/* 32 */     this.activityTraces.remove(activityTrace);
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 36 */     this.activityTraces.clear();
/*    */   }
/*    */ 
/*    */   public int count() {
/* 40 */     return this.activityTraces.size();
/*    */   }
/*    */ 
/*    */   public Collection<ActivityTrace> getActivityTraces() {
/* 44 */     return this.activityTraces;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.ActivityTraces
 * JD-Core Version:    0.6.2
 */