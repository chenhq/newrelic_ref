/*    */ package com.newrelic.agent.android.measurement.http;
/*    */ 
/*    */ import com.newrelic.agent.android.measurement.BaseMeasurement;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class HttpErrorMeasurement extends BaseMeasurement
/*    */ {
/*    */   private String url;
/*    */   private int httpStatusCode;
/*    */   private String responseBody;
/*    */   private String stackTrace;
/*    */   private Map<String, String> params;
/*    */ 
/*    */   public HttpErrorMeasurement(String url, int httpStatusCode)
/*    */   {
/* 19 */     super(MeasurementType.HttpError);
/* 20 */     setUrl(url);
/* 21 */     setName(url);
/* 22 */     setHttpStatusCode(httpStatusCode);
/* 23 */     setStartTime(System.currentTimeMillis());
/*    */   }
/*    */ 
/*    */   public void setUrl(String url) {
/* 27 */     this.url = url;
/*    */   }
/*    */ 
/*    */   public void setHttpStatusCode(int httpStatusCode) {
/* 31 */     this.httpStatusCode = httpStatusCode;
/*    */   }
/*    */ 
/*    */   public void setResponseBody(String responseBody) {
/* 35 */     this.responseBody = responseBody;
/*    */   }
/*    */ 
/*    */   public void setStackTrace(String stackTrace) {
/* 39 */     this.stackTrace = stackTrace;
/*    */   }
/*    */ 
/*    */   public void setParams(Map<String, String> params) {
/* 43 */     this.params = params;
/*    */   }
/*    */ 
/*    */   public String getUrl() {
/* 47 */     return this.url;
/*    */   }
/*    */ 
/*    */   public int getHttpStatusCode() {
/* 51 */     return this.httpStatusCode;
/*    */   }
/*    */ 
/*    */   public String getResponseBody() {
/* 55 */     return this.responseBody;
/*    */   }
/*    */ 
/*    */   public String getStackTrace() {
/* 59 */     return this.stackTrace;
/*    */   }
/*    */ 
/*    */   public Map<String, String> getParams() {
/* 63 */     return this.params;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.http.HttpErrorMeasurement
 * JD-Core Version:    0.6.2
 */