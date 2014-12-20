/*    */ package com.newrelic.agent.android.measurement.http;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.BaseMeasurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.tracing.TraceMachine;
/*    */ import com.newrelic.agent.android.util.Util;
/*    */ 
/*    */ public class HttpTransactionMeasurement extends BaseMeasurement
/*    */ {
/*    */   private String url;
/*    */   private String httpMethod;
/*    */   private double totalTime;
/*    */   private int statusCode;
/*    */   private int errorCode;
/*    */   private long bytesSent;
/*    */   private long bytesReceived;
/*    */   private String appData;
/*    */ 
/*    */   public HttpTransactionMeasurement(String url, String httpMethod, int statusCode, long startTime, double totalTime, long bytesSent, long bytesReceived, String appData)
/*    */   {
/* 24 */     super(MeasurementType.Network);
/*    */ 
/* 26 */     url = Util.sanitizeUrl(url);
/*    */ 
/* 28 */     setName(url);
/* 29 */     setScope(TraceMachine.getCurrentScope());
/* 30 */     setStartTime(startTime);
/* 31 */     setEndTime(startTime + (int)totalTime);
/* 32 */     setExclusiveTime((int)(totalTime * 1000.0D));
/*    */ 
/* 34 */     this.url = url;
/* 35 */     this.httpMethod = httpMethod;
/* 36 */     this.statusCode = statusCode;
/* 37 */     this.bytesSent = bytesSent;
/* 38 */     this.bytesReceived = bytesReceived;
/* 39 */     this.totalTime = totalTime;
/* 40 */     this.appData = appData;
/*    */   }
/*    */ 
/*    */   public HttpTransactionMeasurement(String url, String httpMethod, int statusCode, int errorCode, long startTime, double totalTime, long bytesSent, long bytesReceived, String appData) {
/* 44 */     this(url, httpMethod, statusCode, startTime, totalTime, bytesSent, bytesReceived, appData);
/* 45 */     this.errorCode = errorCode;
/*    */   }
/*    */ 
/*    */   public double asDouble()
/*    */   {
/* 50 */     return this.totalTime;
/*    */   }
/*    */ 
/*    */   public String getUrl() {
/* 54 */     return this.url;
/*    */   }
/*    */ 
/*    */   public String getHttpMethod() {
/* 58 */     return this.httpMethod;
/*    */   }
/*    */ 
/*    */   public double getTotalTime() {
/* 62 */     return this.totalTime;
/*    */   }
/*    */ 
/*    */   public int getStatusCode() {
/* 66 */     return this.statusCode;
/*    */   }
/*    */ 
/*    */   public int getErrorCode() {
/* 70 */     return this.errorCode;
/*    */   }
/*    */ 
/*    */   public long getBytesSent() {
/* 74 */     return this.bytesSent;
/*    */   }
/*    */ 
/*    */   public long getBytesReceived() {
/* 78 */     return this.bytesReceived;
/*    */   }
/*    */ 
/*    */   public String getAppData() {
/* 82 */     return this.appData;
/*    */   }
/*    */ 
/*    */   public void setUrl(String url) {
/* 86 */     this.url = url;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 91 */     return "HttpTransactionMeasurement{url='" + this.url + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", totalTime=" + this.totalTime + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", appData='" + this.appData + '\'' + '}';
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement
 * JD-Core Version:    0.6.2
 */