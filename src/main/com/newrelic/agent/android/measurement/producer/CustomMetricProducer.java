/*    */ package com.newrelic.agent.android.measurement.producer;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.CustomMetricMeasurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.metric.MetricUnit;
/*    */ 
/*    */ public class CustomMetricProducer extends BaseMeasurementProducer
/*    */ {
/*    */   private static final String FILTER_REGEX = "[/\\[\\]|*]";
/*    */ 
/*    */   public CustomMetricProducer()
/*    */   {
/* 11 */     super(MeasurementType.Custom);
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(String name, String category, int count, double totalValue, double exclusiveValue) {
/* 15 */     produceMeasurement(category, name, count, totalValue, exclusiveValue, null, null);
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(String name, String category, int count, double totalValue, double exclusiveValue, MetricUnit countUnit, MetricUnit valueUnit) {
/* 19 */     String metricName = createMetricName(name, category, countUnit, valueUnit);
/* 20 */     CustomMetricMeasurement custom = new CustomMetricMeasurement(metricName, count, totalValue, exclusiveValue);
/* 21 */     produceMeasurement(custom);
/*    */   }
/*    */ 
/*    */   private String createMetricName(String name, String category, MetricUnit countUnit, MetricUnit valueUnit) {
/* 25 */     StringBuffer metricName = new StringBuffer();
/*    */ 
/* 27 */     metricName.append(category.replaceAll("[/\\[\\]|*]", ""));
/* 28 */     metricName.append("/");
/* 29 */     metricName.append(name.replaceAll("[/\\[\\]|*]", ""));
/*    */ 
/* 32 */     if ((countUnit != null) || (valueUnit != null)) {
/* 33 */       metricName.append("[");
/* 34 */       if (valueUnit != null) {
/* 35 */         metricName.append(valueUnit.getLabel());
/*    */       }
/* 37 */       if (countUnit != null) {
/* 38 */         metricName.append("|");
/* 39 */         metricName.append(countUnit.getLabel());
/*    */       }
/* 41 */       metricName.append("]");
/*    */     }
/* 43 */     return metricName.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.producer.CustomMetricProducer
 * JD-Core Version:    0.6.2
 */