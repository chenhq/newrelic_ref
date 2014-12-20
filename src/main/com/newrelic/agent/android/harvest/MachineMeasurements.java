/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.agent.android.metric.Metric;
/*    */ import com.newrelic.agent.android.metric.MetricStore;
/*    */ import com.newrelic.com.google.gson.Gson;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class MachineMeasurements extends HarvestableArray
/*    */ {
/* 15 */   private final MetricStore metrics = new MetricStore();
/*    */ 
/*    */   public void addMetric(String name, double value)
/*    */   {
/* 24 */     Metric metric = new Metric(name);
/* 25 */     metric.sample(value);
/* 26 */     addMetric(metric);
/*    */   }
/*    */ 
/*    */   public void addMetric(Metric metric) {
/* 30 */     this.metrics.add(metric);
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 34 */     this.metrics.clear();
/*    */   }
/*    */ 
/*    */   public boolean isEmpty() {
/* 38 */     return this.metrics.isEmpty();
/*    */   }
/*    */ 
/*    */   public MetricStore getMetrics() {
/* 42 */     return this.metrics;
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 47 */     JsonArray metricArray = new JsonArray();
/*    */ 
/* 49 */     for (Metric metric : this.metrics.getAll()) {
/* 50 */       JsonArray metricJson = new JsonArray();
/*    */ 
/* 52 */       HashMap header = new HashMap();
/* 53 */       header.put("name", metric.getName());
/* 54 */       header.put("scope", metric.getStringScope());
/*    */ 
/* 56 */       metricJson.add(new Gson().toJsonTree(header, GSON_STRING_MAP_TYPE));
/* 57 */       metricJson.add(metric.asJsonObject());
/* 58 */       metricArray.add(metricJson);
/*    */     }
/*    */ 
/* 61 */     return metricArray;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.MachineMeasurements
 * JD-Core Version:    0.6.2
 */