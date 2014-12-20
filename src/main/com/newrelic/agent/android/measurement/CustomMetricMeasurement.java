/*    */ package com.newrelic.agent.android.measurement;
/*    */ 
/*    */ import com.newrelic.agent.android.metric.Metric;
/*    */ 
/*    */ public class CustomMetricMeasurement extends CategorizedMeasurement
/*    */ {
/*    */   private Metric customMetric;
/*    */ 
/*    */   public CustomMetricMeasurement()
/*    */   {
/*  9 */     super(MeasurementType.Custom);
/*    */   }
/*    */ 
/*    */   public CustomMetricMeasurement(String name, int count, double totalValue, double exclusiveValue) {
/* 13 */     this();
/* 14 */     setName(name);
/* 15 */     this.customMetric = new Metric(name);
/*    */ 
/* 18 */     this.customMetric.sample(totalValue);
/* 19 */     this.customMetric.setCount(count);
/* 20 */     this.customMetric.setExclusive(Double.valueOf(exclusiveValue));
/*    */   }
/*    */ 
/*    */   public Metric getCustomMetric() {
/* 24 */     return this.customMetric;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.CustomMetricMeasurement
 * JD-Core Version:    0.6.2
 */