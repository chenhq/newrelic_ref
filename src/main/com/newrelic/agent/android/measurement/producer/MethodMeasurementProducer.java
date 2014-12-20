/*    */ package com.newrelic.agent.android.measurement.producer;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.measurement.MethodMeasurement;
/*    */ import com.newrelic.agent.android.tracing.Trace;
/*    */ 
/*    */ public class MethodMeasurementProducer extends BaseMeasurementProducer
/*    */ {
/*    */   public MethodMeasurementProducer()
/*    */   {
/* 12 */     super(MeasurementType.Method);
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(Trace trace)
/*    */   {
/* 17 */     MethodMeasurement methodMeasurement = new MethodMeasurement(trace.displayName, trace.scope, trace.entryTimestamp, trace.exitTimestamp, trace.exclusiveTime, trace.getCategory());
/*    */ 
/* 25 */     produceMeasurement(methodMeasurement);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.producer.MethodMeasurementProducer
 * JD-Core Version:    0.6.2
 */