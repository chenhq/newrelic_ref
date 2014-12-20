/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.metric.Metric;
/*    */ import com.newrelic.agent.android.metric.MetricStore;
/*    */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*    */ import com.newrelic.agent.android.tracing.Trace;
/*    */ import java.util.List;
/*    */ 
/*    */ public class HarvestDataValidator extends HarvestAdapter
/*    */ {
/*    */   public void onHarvestFinalize()
/*    */   {
/* 12 */     if (!Harvest.isInitialized()) {
/* 13 */       return;
/*    */     }
/* 15 */     ensureActivityNameMetricsExist();
/*    */   }
/*    */ 
/*    */   public void ensureActivityNameMetricsExist() {
/* 19 */     HarvestData harvestData = Harvest.getInstance().getHarvestData();
/*    */ 
/* 21 */     ActivityTraces activityTraces = harvestData.getActivityTraces();
/* 22 */     if ((activityTraces == null) || (activityTraces.count() == 0)) {
/* 23 */       return;
/*    */     }
/* 25 */     MachineMeasurements metrics = harvestData.getMetrics();
/* 26 */     if ((metrics == null) || (metrics.isEmpty())) {
/* 27 */       return;
/*    */     }
/*    */ 
/* 30 */     for (ActivityTrace activityTrace : activityTraces.getActivityTraces()) {
/* 31 */       String activityName = activityTrace.rootTrace.displayName;
/* 32 */       int hashIndex = activityName.indexOf("#");
/* 33 */       if (hashIndex > 0) {
/* 34 */         activityName = activityName.substring(0, hashIndex);
/*    */       }
/*    */ 
/* 37 */       String activityMetricRoot = "Mobile/Activity/Name/" + activityName;
/*    */ 
/* 40 */       boolean foundMetricForActivity = false;
/* 41 */       List unScopedMetrics = metrics.getMetrics().getAllUnscoped();
/*    */ 
/* 43 */       if ((unScopedMetrics != null) && (unScopedMetrics.size() > 0)) {
/* 44 */         for (Metric metric : unScopedMetrics) {
/* 45 */           if (metric.getName().startsWith(activityMetricRoot)) {
/* 46 */             foundMetricForActivity = true;
/* 47 */             break;
/*    */           }
/*    */         }
/*    */       }
/*    */ 
/* 52 */       if (!foundMetricForActivity)
/*    */       {
/* 56 */         Metric activityMetric = new Metric(activityMetricRoot);
/* 57 */         metrics.addMetric(activityMetric);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestDataValidator
 * JD-Core Version:    0.6.2
 */