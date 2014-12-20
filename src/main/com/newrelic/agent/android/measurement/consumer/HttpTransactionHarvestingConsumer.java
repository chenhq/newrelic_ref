/*    */ package com.newrelic.agent.android.measurement.consumer;
/*    */ 
/*    */ import com.newrelic.agent.android.Agent;
/*    */ import com.newrelic.agent.android.harvest.Harvest;
/*    */ import com.newrelic.agent.android.harvest.HttpTransaction;
/*    */ import com.newrelic.agent.android.measurement.Measurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*    */ 
/*    */ public class HttpTransactionHarvestingConsumer extends BaseMeasurementConsumer
/*    */ {
/*    */   public HttpTransactionHarvestingConsumer()
/*    */   {
/* 12 */     super(MeasurementType.Network);
/*    */   }
/*    */ 
/*    */   public void consumeMeasurement(Measurement measurement)
/*    */   {
/* 17 */     HttpTransactionMeasurement m = (HttpTransactionMeasurement)measurement;
/* 18 */     HttpTransaction txn = new HttpTransaction();
/*    */ 
/* 20 */     txn.setUrl(m.getUrl());
/* 21 */     txn.setHttpMethod(m.getHttpMethod());
/* 22 */     txn.setStatusCode(m.getStatusCode());
/* 23 */     txn.setErrorCode(m.getErrorCode());
/* 24 */     txn.setTotalTime(m.getTotalTime());
/* 25 */     txn.setCarrier(Agent.getActiveNetworkCarrier());
/* 26 */     txn.setWanType(Agent.getActiveNetworkCarrier());
/* 27 */     txn.setBytesReceived(m.getBytesReceived());
/* 28 */     txn.setBytesSent(m.getBytesSent());
/* 29 */     txn.setAppData(m.getAppData());
/* 30 */     txn.setTimestamp(Long.valueOf(m.getStartTime()));
/*    */ 
/* 32 */     Harvest.addHttpTransaction(txn);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.HttpTransactionHarvestingConsumer
 * JD-Core Version:    0.6.2
 */