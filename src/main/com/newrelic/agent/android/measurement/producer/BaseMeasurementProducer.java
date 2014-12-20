/*    */ package com.newrelic.agent.android.measurement.producer;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.Measurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ 
/*    */ public class BaseMeasurementProducer
/*    */   implements MeasurementProducer
/*    */ {
/*    */   private final MeasurementType producedMeasurementType;
/* 16 */   private final ArrayList<Measurement> producedMeasurements = new ArrayList();
/*    */ 
/*    */   public BaseMeasurementProducer(MeasurementType measurementType) {
/* 19 */     this.producedMeasurementType = measurementType;
/*    */   }
/*    */ 
/*    */   public MeasurementType getMeasurementType()
/*    */   {
/* 24 */     return this.producedMeasurementType;
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(Measurement measurement)
/*    */   {
/* 29 */     synchronized (this.producedMeasurements) {
/* 30 */       this.producedMeasurements.add(measurement);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void produceMeasurements(Collection<Measurement> measurements) {
/* 35 */     synchronized (this.producedMeasurements) {
/* 36 */       this.producedMeasurements.addAll(measurements);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Collection<Measurement> drainMeasurements()
/*    */   {
/* 42 */     if (this.producedMeasurements.size() == 0)
/* 43 */       return Collections.emptyList();
/* 44 */     synchronized (this) {
/* 45 */       Collection measurements = new ArrayList(this.producedMeasurements);
/* 46 */       this.producedMeasurements.clear();
/* 47 */       return measurements;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.producer.BaseMeasurementProducer
 * JD-Core Version:    0.6.2
 */