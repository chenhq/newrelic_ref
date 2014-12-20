/*    */ package com.newrelic.agent.android.measurement;
/*    */ 
/*    */ import com.newrelic.agent.android.instrumentation.MetricCategory;
/*    */ 
/*    */ public class MethodMeasurement extends CategorizedMeasurement
/*    */ {
/*    */   public MethodMeasurement(String name, String scope, long startTime, long endTime, long exclusiveTime, MetricCategory category)
/*    */   {
/* 10 */     super(MeasurementType.Method);
/* 11 */     setName(name);
/* 12 */     setScope(scope);
/* 13 */     setStartTime(startTime);
/* 14 */     setEndTime(endTime);
/* 15 */     setExclusiveTime(exclusiveTime);
/* 16 */     setCategory(category);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.MethodMeasurement
 * JD-Core Version:    0.6.2
 */