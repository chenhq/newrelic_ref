/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import android.content.Context;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.logging.AndroidAgentLog;
/*     */ import com.newrelic.agent.android.logging.NullAgentLog;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import com.newrelic.agent.android.metric.MetricUnit;
/*     */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import com.newrelic.agent.android.tracing.TracingInactiveException;
/*     */ import com.newrelic.agent.android.util.NetworkFailure;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Map;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpResponse;
/*     */ 
/*     */ public class NewRelic
/*     */ {
/*     */   private static final String DEFAULT_COLLECTOR_ADDR = "mobile-collector.newrelic.com";
/*     */   private static final String UNKNOWN_HTTP_REQUEST_TYPE = "unknown";
/*  38 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*  39 */   private static final AgentConfiguration agentConfiguration = new AgentConfiguration();
/*  40 */   private static boolean started = false;
/*     */ 
/*  42 */   private boolean loggingEnabled = true;
/*  43 */   private int logLevel = 3;
/*     */ 
/*     */   private NewRelic(String token) {
/*  46 */     agentConfiguration.setApplicationToken(token);
/*     */   }
/*     */ 
/*     */   public static NewRelic withApplicationToken(String token)
/*     */   {
/*  53 */     return new NewRelic(token);
/*     */   }
/*     */ 
/*     */   public NewRelic usingSsl(boolean useSsl)
/*     */   {
/*  61 */     agentConfiguration.setUseSsl(useSsl);
/*  62 */     return this;
/*     */   }
/*     */ 
/*     */   public NewRelic usingCollectorAddress(String address)
/*     */   {
/*  69 */     agentConfiguration.setCollectorHost(address);
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */   public NewRelic usingCrashCollectorAddress(String address) {
/*  74 */     agentConfiguration.setCrashCollectorHost(address);
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   public NewRelic withLocationServiceEnabled(boolean enabled)
/*     */   {
/*  84 */     agentConfiguration.setUseLocationService(enabled);
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   public NewRelic withLoggingEnabled(boolean enabled)
/*     */   {
/*  92 */     this.loggingEnabled = enabled;
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   public NewRelic withLogLevel(int level)
/*     */   {
/* 106 */     this.logLevel = level;
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */   public NewRelic withCrashReportingEnabled(boolean enabled)
/*     */   {
/* 115 */     agentConfiguration.setReportCrashes(enabled);
/* 116 */     if (enabled)
/* 117 */       enableFeature(FeatureFlag.CrashReporting);
/*     */     else
/* 119 */       disableFeature(FeatureFlag.CrashReporting);
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   public NewRelic withHttpResponseBodyCaptureEnabled(boolean enabled)
/*     */   {
/* 129 */     if (enabled)
/* 130 */       enableFeature(FeatureFlag.HttpResponseBodyCapture);
/*     */     else
/* 132 */       disableFeature(FeatureFlag.HttpResponseBodyCapture);
/* 133 */     return this;
/*     */   }
/*     */ 
/*     */   public static void enableFeature(FeatureFlag featureFlag)
/*     */   {
/* 144 */     FeatureFlag.enableFeature(featureFlag);
/*     */   }
/*     */ 
/*     */   public static void disableFeature(FeatureFlag featureFlag)
/*     */   {
/* 155 */     FeatureFlag.disableFeature(featureFlag);
/*     */   }
/*     */ 
/*     */   public void start(Context context)
/*     */   {
/* 164 */     if (started) {
/* 165 */       log.debug("NewRelic is already running.");
/* 166 */       return;
/*     */     }
/*     */     try {
/* 169 */       AgentLogManager.setAgentLog(this.loggingEnabled ? new AndroidAgentLog() : new NullAgentLog());
/* 170 */       log.setLevel(this.logLevel);
/*     */ 
/* 172 */       if (isInstrumented()) {
/* 173 */         AndroidAgentImpl.init(context, agentConfiguration);
/* 174 */         started = true;
/*     */       } else {
/* 176 */         log.error("Failed to detect New Relic instrumentation.  Something likely went wrong during your build process and you should contact support@newrelic.com.");
/* 177 */         return;
/*     */       }
/*     */     } catch (Throwable e) {
/* 180 */       log.error("Error occurred while starting the New Relic agent!", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isStarted()
/*     */   {
/* 189 */     return started;
/*     */   }
/*     */ 
/*     */   public static void shutdown()
/*     */   {
/* 196 */     if (started)
/*     */       try {
/* 198 */         Agent.getImpl().stop();
/*     */       } finally {
/* 200 */         Agent.setImpl(NullAgentImpl.instance);
/* 201 */         started = false;
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean isInstrumented()
/*     */   {
/* 212 */     return false;
/*     */   }
/*     */ 
/*     */   public static String startInteraction(String actionName)
/*     */   {
/* 226 */     checkNull(actionName, "startInteraction: actionName must be an action/method name.");
/*     */ 
/* 228 */     TraceMachine.startTracing(actionName.replace("/", "."), true);
/*     */     try
/*     */     {
/* 231 */       return TraceMachine.getActivityTrace().getId(); } catch (TracingInactiveException e) {
/*     */     }
/* 233 */     return null;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static String startInteraction(Context activityContext, String actionName)
/*     */   {
/* 247 */     checkNull(activityContext, "startInteraction: context must be an Activity instance.");
/*     */ 
/* 249 */     checkNull(actionName, "startInteraction: actionName must be an action/method name.");
/*     */ 
/* 251 */     TraceMachine.startTracing(activityContext.getClass().getSimpleName() + "#" + actionName.replace("/", "."));
/*     */     try
/*     */     {
/* 254 */       return TraceMachine.getActivityTrace().getId(); } catch (TracingInactiveException e) {
/*     */     }
/* 256 */     return null;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static String startInteraction(Context context, String actionName, boolean cancelRunningTrace)
/*     */   {
/* 272 */     if ((TraceMachine.isTracingActive()) && (!cancelRunningTrace)) {
/* 273 */       log.warning("startInteraction: An interaction is already being traced, and invalidateActiveTrace is false. This interaction will not be traced.");
/* 274 */       return null;
/*     */     }
/* 276 */     return startInteraction(context, actionName);
/*     */   }
/*     */ 
/*     */   public static void endInteraction(String id)
/*     */   {
/* 285 */     TraceMachine.endTrace(id);
/*     */   }
/*     */ 
/*     */   public static void setInteractionName(String name)
/*     */   {
/* 295 */     TraceMachine.setRootDisplayName(name);
/*     */   }
/*     */ 
/*     */   public static void recordMetric(String name, String category, int count, double totalValue, double exclusiveValue)
/*     */   {
/* 304 */     recordMetric(name, category, count, totalValue, exclusiveValue, null, null);
/*     */   }
/*     */ 
/*     */   public static void recordMetric(String name, String category, int count, double totalValue, double exclusiveValue, MetricUnit countUnit, MetricUnit valueUnit) {
/* 308 */     checkNull(category, "recordMetric: category must not be null. If no MetricCategory is applicable, use MetricCategory.NONE.");
/*     */ 
/* 310 */     checkEmpty(name, "recordMetric: name must not be empty.");
/*     */ 
/* 312 */     checkNegative(count, "recordMetric: count must not be negative.");
/*     */ 
/* 314 */     Measurements.addCustomMetric(name, category, count, totalValue, exclusiveValue, countUnit, valueUnit);
/*     */   }
/*     */ 
/*     */   public static void recordMetric(String name, String category, double value) {
/* 318 */     recordMetric(name, category, 1, value, value, null, null);
/*     */   }
/*     */ 
/*     */   public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived)
/*     */   {
/* 326 */     _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, null, null, null);
/*     */   }
/*     */ 
/*     */   public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody) {
/* 330 */     _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, null, null);
/*     */   }
/*     */ 
/*     */   public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params) {
/* 334 */     _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, null);
/*     */   }
/*     */ 
/*     */   public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, String appData) {
/* 338 */     _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, appData);
/*     */   }
/*     */ 
/*     */   public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, HttpResponse httpResponse) {
/* 342 */     if (httpResponse != null) {
/* 343 */       Header header = httpResponse.getFirstHeader("X-NewRelic-ID");
/*     */ 
/* 345 */       if ((header != null) && (header.getValue() != null) && (header.getValue().length() > 0)) {
/* 346 */         _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, header.getValue());
/* 347 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 351 */     _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, null);
/*     */   }
/*     */ 
/*     */   public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, URLConnection urlConnection) {
/* 355 */     if (urlConnection != null) {
/* 356 */       String header = urlConnection.getHeaderField("X-NewRelic-ID");
/*     */ 
/* 358 */       if ((header != null) && (header.length() > 0)) {
/* 359 */         _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, header);
/* 360 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 364 */     _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, null);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, HttpResponse httpResponse)
/*     */   {
/* 373 */     noticeHttpTransaction(url, "unknown", statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, httpResponse);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, URLConnection urlConnection) {
/* 378 */     noticeHttpTransaction(url, "unknown", statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, urlConnection);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived) {
/* 383 */     _noticeHttpTransaction(url, "unknown", statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, null, null, null);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody) {
/* 388 */     _noticeHttpTransaction(url, "unknown", statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, null, null);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params) {
/* 393 */     _noticeHttpTransaction(url, "unknown", statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, null);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, String appData) {
/* 398 */     _noticeHttpTransaction(url, "unknown", statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, appData);
/*     */   }
/*     */ 
/*     */   private static void _noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, String appData) {
/* 402 */     checkEmpty(url, "noticeHttpTransaction: url must not be empty.");
/* 403 */     checkEmpty(httpMethod, "noticeHttpTransaction: httpMethod must not be empty.");
/*     */     try
/*     */     {
/* 406 */       new URL(url);
/*     */     } catch (MalformedURLException e) {
/* 408 */       throw new IllegalArgumentException("noticeHttpTransaction: URL is malformed: " + url);
/*     */     }
/*     */ 
/* 411 */     double totalTime = endTimeMs - startTimeMs;
/*     */ 
/* 413 */     checkNegative((int)totalTime, "noticeHttpTransaction: the startTimeMs is later than the endTimeMs, resulting in a negative total time.");
/*     */ 
/* 416 */     totalTime /= 1000.0D;
/*     */ 
/* 418 */     TaskQueue.queue(new HttpTransactionMeasurement(url, httpMethod, statusCode, 0, startTimeMs, totalTime, bytesSent, bytesReceived, appData));
/*     */ 
/* 421 */     if (statusCode >= 400L)
/* 422 */       Measurements.addHttpError(url, httpMethod, statusCode, responseBody, params);
/*     */   }
/*     */ 
/*     */   public static void noticeNetworkFailure(String url, String httpMethod, long startTime, long endTime, NetworkFailure failure)
/*     */   {
/* 431 */     TaskQueue.queue(new HttpTransactionMeasurement(url, httpMethod, 0, failure.getErrorCode(), startTime, endTime, 0L, 0L, null));
/*     */   }
/*     */ 
/*     */   public static void noticeNetworkFailure(String url, String httpMethod, long startTime, long endTime, Exception e) {
/* 435 */     checkEmpty(url, "noticeHttpException: url must not be empty.");
/*     */ 
/* 437 */     NetworkFailure failure = NetworkFailure.exceptionToNetworkFailure(e);
/* 438 */     noticeNetworkFailure(url, httpMethod, startTime, endTime, failure);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeNetworkFailure(String url, long startTime, long endTime, NetworkFailure failure)
/*     */   {
/* 447 */     noticeNetworkFailure(url, "unknown", startTime, endTime, failure);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static void noticeNetworkFailure(String url, long startTime, long endTime, Exception e) {
/* 452 */     noticeNetworkFailure(url, "unknown", startTime, endTime, e);
/*     */   }
/*     */ 
/*     */   private static void checkNull(Object object, String message)
/*     */   {
/* 458 */     if (object == null)
/* 459 */       throw new IllegalArgumentException(message);
/*     */   }
/*     */ 
/*     */   private static void checkEmpty(String string, String message)
/*     */   {
/* 464 */     checkNull(string, message);
/*     */ 
/* 466 */     if (string.length() == 0)
/* 467 */       throw new IllegalArgumentException(message);
/*     */   }
/*     */ 
/*     */   private static void checkNegative(int number, String message)
/*     */   {
/* 472 */     if (number < 0)
/* 473 */       throw new IllegalArgumentException(message);
/*     */   }
/*     */ 
/*     */   public static void crashNow()
/*     */   {
/* 485 */     crashNow("This is a demonstration crash courtesy of New Relic");
/*     */   }
/*     */ 
/*     */   public static void crashNow(String message)
/*     */   {
/* 494 */     throw new RuntimeException(message);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.NewRelic
 * JD-Core Version:    0.6.2
 */