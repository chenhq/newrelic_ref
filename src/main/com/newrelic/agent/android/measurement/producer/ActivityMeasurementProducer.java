/*    */ package com.newrelic.agent.android.measurement.producer;
/*    */ 
/*    */ import com.newrelic.agent.android.activity.MeasuredActivity;
/*    */ import com.newrelic.agent.android.measurement.ActivityMeasurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ 
/*    */ public class ActivityMeasurementProducer extends BaseMeasurementProducer
/*    */ {
/*    */   public ActivityMeasurementProducer()
/*    */   {
/* 12 */     super(MeasurementType.Activity);
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(MeasuredActivity measuredActivity)
/*    */   {
/* 17 */     super.produceMeasurement(new ActivityMeasurement(measuredActivity.getMetricName(), measuredActivity.getStartTime(), measuredActivity.getEndTime()));
/* 18 */     super.produceMeasurement(new ActivityMeasurement(measuredActivity.getBackgroundMetricName(), measuredActivity.getStartTime(), measuredActivity.getEndTime()));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.producer.ActivityMeasurementProducer
 * JD-Core Version:    0.6.2
 */