/*    */ package com.newrelic.agent.android.measurement.consumer;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ 
/*    */ public class MethodMeasurementConsumer extends MetricMeasurementConsumer
/*    */ {
/*    */   private static final String METRIC_PREFIX = "Method/";
/*    */ 
/*    */   public MethodMeasurementConsumer()
/*    */   {
/* 12 */     super(MeasurementType.Method);
/*    */   }
/*    */ 
/*    */   protected String formatMetricName(String name)
/*    */   {
/* 17 */     return "Method/" + name.replace("#", "/");
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.MethodMeasurementConsumer
 * JD-Core Version:    0.6.2
 */