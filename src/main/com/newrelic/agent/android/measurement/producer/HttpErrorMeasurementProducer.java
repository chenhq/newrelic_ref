/*    */ package com.newrelic.agent.android.measurement.producer;
/*    */ 
/*    */ import com.newrelic.agent.android.Agent;
/*    */ import com.newrelic.agent.android.measurement.MeasurementType;
/*    */ import com.newrelic.agent.android.measurement.ThreadInfo;
/*    */ import com.newrelic.agent.android.measurement.http.HttpErrorMeasurement;
/*    */ import com.newrelic.agent.android.util.Util;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class HttpErrorMeasurementProducer extends BaseMeasurementProducer
/*    */ {
/*    */   public static final String HTTP_METHOD_PARAMS_KEY = "http_method";
/*    */   public static final String WAN_TYPE_PARAMS_KEY = "wan_type";
/*    */ 
/*    */   public HttpErrorMeasurementProducer()
/*    */   {
/* 17 */     super(MeasurementType.HttpError);
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(String url, String httpMethod, int statusCode) {
/* 21 */     produceMeasurement(url, httpMethod, statusCode, "");
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(String url, String httpMethod, int statusCode, String responseBody) {
/* 25 */     produceMeasurement(url, httpMethod, statusCode, responseBody, null);
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(String url, String httpMethod, int statusCode, String responseBody, Map<String, String> params) {
/* 29 */     produceMeasurement(url, httpMethod, statusCode, responseBody, params, new ThreadInfo());
/*    */   }
/*    */ 
/*    */   public void produceMeasurement(String urlString, String httpMethod, int statusCode, String responseBody, Map<String, String> params, ThreadInfo threadInfo) {
/* 33 */     String url = Util.sanitizeUrl(urlString);
/* 34 */     if (url == null) {
/* 35 */       return;
/*    */     }
/* 37 */     HttpErrorMeasurement measurement = new HttpErrorMeasurement(url, statusCode);
/* 38 */     if (params == null) {
/* 39 */       params = new HashMap();
/*    */     }
/* 41 */     params.put("http_method", httpMethod);
/* 42 */     params.put("wan_type", Agent.getActiveNetworkWanType());
/* 43 */     measurement.setThreadInfo(threadInfo);
/* 44 */     measurement.setStackTrace(getSanitizedStackTrace());
/* 45 */     measurement.setResponseBody(responseBody);
/* 46 */     measurement.setParams(params);
/* 47 */     produceMeasurement(measurement);
/*    */   }
/*    */ 
/*    */   private String getSanitizedStackTrace() {
/* 51 */     StringBuilder stackTrace = new StringBuilder();
/*    */ 
/* 53 */     StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
/*    */ 
/* 55 */     int numErrors = 0;
/* 56 */     for (int i = 0; i < stackTraceElements.length; i++) {
/* 57 */       StackTraceElement frame = stackTraceElements[i];
/* 58 */       if (!shouldFilterStackTraceElement(frame)) {
/* 59 */         stackTrace.append(frame.toString());
/* 60 */         if (i <= stackTraceElements.length - 1) {
/* 61 */           stackTrace.append("\n");
/*    */         }
/* 63 */         numErrors++;
/* 64 */         if (numErrors >= Agent.getStackTraceLimit()) {
/*    */           break;
/*    */         }
/*    */       }
/*    */     }
/* 69 */     return stackTrace.toString();
/*    */   }
/*    */ 
/*    */   private boolean shouldFilterStackTraceElement(StackTraceElement element) {
/* 73 */     String className = element.getClassName();
/* 74 */     String method = element.getMethodName();
/*    */ 
/* 76 */     if (className.startsWith("com.newrelic")) {
/* 77 */       return true;
/*    */     }
/* 79 */     if ((className.startsWith("dalvik.system.VMStack")) && (method.startsWith("getThreadStackTrace"))) {
/* 80 */       return true;
/*    */     }
/* 82 */     if ((className.startsWith("java.lang.Thread")) && (method.startsWith("getStackTrace"))) {
/* 83 */       return true;
/*    */     }
/* 85 */     return false;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.producer.HttpErrorMeasurementProducer
 * JD-Core Version:    0.6.2
 */