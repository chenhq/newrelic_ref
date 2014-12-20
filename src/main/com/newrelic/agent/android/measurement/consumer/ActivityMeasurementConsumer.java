/*    */ package com.newrelic.agent.android.measurement.consumer;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ 
/*    */ public class ActivityMeasurementConsumer extends MetricMeasurementConsumer
/*    */ {
/*    */   public ActivityMeasurementConsumer()
/*    */   {
/* 10 */     super(MeasurementType.Activity);
/*    */   }
/*    */ 
/*    */   protected String formatMetricName(String name)
/*    */   {
/* 15 */     return name;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.ActivityMeasurementConsumer
 * JD-Core Version:    0.6.2
 */