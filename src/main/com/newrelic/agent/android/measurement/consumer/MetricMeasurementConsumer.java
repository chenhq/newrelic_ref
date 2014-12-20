/*    */ package com.newrelic.agent.android.measurement.consumer;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.Harvest;
/*    */ import com.newrelic.agent.android.harvest.HarvestLifecycleAware;
/*    */ import com.newrelic.agent.android.measurement.Measurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.metric.Metric;
/*    */ import com.newrelic.agent.android.metric.MetricStore;
/*    */ 
/*    */ public abstract class MetricMeasurementConsumer extends BaseMeasurementConsumer
/*    */   implements HarvestLifecycleAware
/*    */ {
/*    */   protected MetricStore metrics;
/* 15 */   protected boolean recordUnscopedMetrics = true;
/*    */ 
/*    */   public MetricMeasurementConsumer(MeasurementType measurementType) {
/* 18 */     super(measurementType);
/*    */ 
/* 20 */     this.metrics = new MetricStore();
/* 21 */     Harvest.addHarvestListener(this);
/*    */   }
/*    */ 
/*    */   protected abstract String formatMetricName(String paramString);
/*    */ 
/*    */   public void consumeMeasurement(Measurement measurement)
/*    */   {
/* 28 */     String name = formatMetricName(measurement.getName());
/* 29 */     String scope = measurement.getScope();
/* 30 */     double delta = measurement.getEndTimeInSeconds() - measurement.getStartTimeInSeconds();
/*    */ 
/* 33 */     if (scope != null) {
/* 34 */       Metric scopedMetric = this.metrics.get(name, scope);
/* 35 */       if (scopedMetric == null) {
/* 36 */         scopedMetric = new Metric(name, scope);
/* 37 */         this.metrics.add(scopedMetric);
/*    */       }
/*    */ 
/* 40 */       scopedMetric.sample(delta);
/* 41 */       scopedMetric.addExclusive(measurement.getExclusiveTimeInSeconds());
/*    */     }
/*    */ 
/* 45 */     if (!this.recordUnscopedMetrics) {
/* 46 */       return;
/*    */     }
/* 48 */     Metric unscopedMetric = this.metrics.get(name);
/* 49 */     if (unscopedMetric == null) {
/* 50 */       unscopedMetric = new Metric(name);
/* 51 */       this.metrics.add(unscopedMetric);
/*    */     }
/*    */ 
/* 54 */     unscopedMetric.sample(delta);
/* 55 */     unscopedMetric.addExclusive(measurement.getExclusiveTimeInSeconds());
/*    */   }
/*    */ 
/*    */   protected void addMetric(Metric newMetric)
/*    */   {
/*    */     Metric metric;
/*    */     Metric metric;
/* 61 */     if (newMetric.getScope() != null)
/* 62 */       metric = this.metrics.get(newMetric.getName(), newMetric.getScope());
/*    */     else {
/* 64 */       metric = this.metrics.get(newMetric.getName());
/*    */     }
/*    */ 
/* 67 */     if (metric != null)
/* 68 */       metric.aggregate(newMetric);
/*    */     else
/* 70 */       this.metrics.add(newMetric);
/*    */   }
/*    */ 
/*    */   public void onHarvest()
/*    */   {
/* 75 */     for (Metric metric : this.metrics.getAll())
/* 76 */       Harvest.addMetric(metric);
/*    */   }
/*    */ 
/*    */   public void onHarvestComplete()
/*    */   {
/* 82 */     this.metrics.clear();
/*    */   }
/*    */ 
/*    */   public void onHarvestError()
/*    */   {
/* 87 */     this.metrics.clear();
/*    */   }
/*    */ 
/*    */   public void onHarvestSendFailed()
/*    */   {
/* 92 */     this.metrics.clear();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.MetricMeasurementConsumer
 * JD-Core Version:    0.6.2
 */