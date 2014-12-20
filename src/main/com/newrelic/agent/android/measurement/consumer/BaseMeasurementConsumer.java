/*    */ package com.newrelic.agent.android.measurement.consumer;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.HarvestAdapter;
/*    */ import com.newrelic.agent.android.measurement.Measurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class BaseMeasurementConsumer extends HarvestAdapter
/*    */   implements MeasurementConsumer
/*    */ {
/*    */   private final MeasurementType measurementType;
/*    */ 
/*    */   public BaseMeasurementConsumer(MeasurementType measurementType)
/*    */   {
/* 18 */     this.measurementType = measurementType;
/*    */   }
/*    */ 
/*    */   public MeasurementType getMeasurementType()
/*    */   {
/* 23 */     return this.measurementType;
/*    */   }
/*    */ 
/*    */   public void consumeMeasurement(Measurement measurement)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void consumeMeasurements(Collection<Measurement> measurements)
/*    */   {
/* 32 */     for (Measurement measurement : measurements)
/* 33 */       consumeMeasurement(measurement);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.BaseMeasurementConsumer
 * JD-Core Version:    0.6.2
 */