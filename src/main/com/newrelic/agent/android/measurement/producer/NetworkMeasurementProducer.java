/*    */ package com.newrelic.agent.android.measurement.producer;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*    */ import com.newrelic.agent.android.util.Util;
/*    */ 
/*    */ public class NetworkMeasurementProducer extends BaseMeasurementProducer
/*    */ {
/*    */   public NetworkMeasurementProducer()
/*    */   {
/*  9 */     super(MeasurementType.Network);
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(String urlString, String httpMethod, int statusCode, int errorCode, long startTime, double totalTime, long bytesSent, long bytesReceived, String appData) {
/* 13 */     String url = Util.sanitizeUrl(urlString);
/* 14 */     if (url == null) {
/* 15 */       return;
/*    */     }
/* 17 */     produceMeasurement(new HttpTransactionMeasurement(url, httpMethod, statusCode, errorCode, startTime, totalTime, bytesSent, bytesReceived, appData));
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(HttpTransactionMeasurement transactionMeasurement) {
/* 21 */     String url = Util.sanitizeUrl(transactionMeasurement.getUrl());
/* 22 */     if (url == null) {
/* 23 */       return;
/*    */     }
/* 25 */     transactionMeasurement.setUrl(url);
/* 26 */     super.produceMeasurement(transactionMeasurement);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.producer.NetworkMeasurementProducer
 * JD-Core Version:    0.6.2
 */