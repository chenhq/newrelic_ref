/*    */ package com.newrelic.agent.android.measurement.consumer;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.Harvest;
/*    */ import com.newrelic.agent.android.harvest.HttpError;
/*    */ import com.newrelic.agent.android.measurement.Measurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.measurement.http.HttpErrorMeasurement;
/*    */ 
/*    */ public class HttpErrorHarvestingConsumer extends BaseMeasurementConsumer
/*    */ {
/*    */   public HttpErrorHarvestingConsumer()
/*    */   {
/* 11 */     super(MeasurementType.HttpError);
/*    */   }
/*    */ 
/*    */   public void consumeMeasurement(Measurement measurement)
/*    */   {
/* 16 */     HttpError error = new HttpError((HttpErrorMeasurement)measurement);
/* 17 */     Harvest.addHttpError(error);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.HttpErrorHarvestingConsumer
 * JD-Core Version:    0.6.2
 */