/*    */ package com.newrelic.agent.android.sample;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.Measurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.measurement.consumer.MetricMeasurementConsumer;
/*    */ import com.newrelic.agent.android.metric.Metric;
/*    */ import com.newrelic.agent.android.tracing.Sample;
/*    */ 
/*    */ public class MachineMeasurementConsumer extends MetricMeasurementConsumer
/*    */ {
/*    */   public MachineMeasurementConsumer()
/*    */   {
/* 12 */     super(MeasurementType.Machine);
/*    */   }
/*    */ 
/*    */   protected String formatMetricName(String name)
/*    */   {
/* 17 */     return name;
/*    */   }
/*    */ 
/*    */   public void consumeMeasurement(Measurement measurement)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onHarvest() {
/* 25 */     Sample memorySample = Sampler.sampleMemory();
/*    */ 
/* 27 */     if (memorySample != null) {
/* 28 */       Metric memoryMetric = new Metric("Memory/Used");
/* 29 */       memoryMetric.sample(memorySample.getValue().doubleValue());
/* 30 */       addMetric(memoryMetric);
/*    */     }
/*    */ 
/* 33 */     super.onHarvest();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.sample.MachineMeasurementConsumer
 * JD-Core Version:    0.6.2
 */