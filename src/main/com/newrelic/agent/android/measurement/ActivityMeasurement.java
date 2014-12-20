/*    */ package com.newrelic.agent.android.measurement;
/*    */ 
/*    */ public class ActivityMeasurement extends BaseMeasurement
/*    */ {
/*    */   public ActivityMeasurement(String name, long startTime, long endTime)
/*    */   {
/*  8 */     super(MeasurementType.Activity);
/*    */ 
/* 10 */     setName(name);
/* 11 */     setStartTime(startTime);
/* 12 */     setEndTime(endTime);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.ActivityMeasurement
 * JD-Core Version:    0.6.2
 */