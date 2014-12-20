/*    */ package com.newrelic.agent.android.measurement;
/*    */ 
/*    */ import com.newrelic.agent.android.instrumentation.MetricCategory;
/*    */ 
/*    */ public class CategorizedMeasurement extends BaseMeasurement
/*    */ {
/*    */   private MetricCategory category;
/*    */ 
/*    */   public CategorizedMeasurement(MeasurementType measurementType)
/*    */   {
/*  9 */     super(measurementType);
/*    */   }
/*    */ 
/*    */   public MetricCategory getCategory() {
/* 13 */     return this.category;
/*    */   }
/*    */ 
/*    */   public void setCategory(MetricCategory category) {
/* 17 */     this.category = category;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.CategorizedMeasurement
 * JD-Core Version:    0.6.2
 */