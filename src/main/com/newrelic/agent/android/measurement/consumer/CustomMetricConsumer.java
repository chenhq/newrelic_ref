/*    */ package com.newrelic.agent.android.measurement.consumer;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.CustomMetricMeasurement;
/*    */ import com.newrelic.agent.android.measurement.Measurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.metric.Metric;
/*    */ 
/*    */ public class CustomMetricConsumer extends MetricMeasurementConsumer
/*    */ {
/*    */   private static final String METRIC_PREFIX = "Custom/";
/*    */ 
/*    */   public CustomMetricConsumer()
/*    */   {
/* 13 */     super(MeasurementType.Custom);
/*    */   }
/*    */ 
/*    */   protected String formatMetricName(String name)
/*    */   {
/* 18 */     return "Custom/" + name;
/*    */   }
/*    */ 
/*    */   public void consumeMeasurement(Measurement measurement)
/*    */   {
/* 23 */     CustomMetricMeasurement custom = (CustomMetricMeasurement)measurement;
/* 24 */     Metric metric = custom.getCustomMetric();
/* 25 */     metric.setName(formatMetricName(metric.getName()));
/* 26 */     addMetric(metric);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.CustomMetricConsumer
 * JD-Core Version:    0.6.2
 */