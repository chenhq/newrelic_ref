/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import com.newrelic.agent.android.activity.MeasuredActivity;
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.harvest.Harvest;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.measurement.ThreadInfo;
/*     */ import com.newrelic.agent.android.measurement.consumer.ActivityMeasurementConsumer;
/*     */ import com.newrelic.agent.android.measurement.consumer.CustomMetricConsumer;
/*     */ import com.newrelic.agent.android.measurement.consumer.HttpErrorHarvestingConsumer;
/*     */ import com.newrelic.agent.android.measurement.consumer.HttpTransactionHarvestingConsumer;
/*     */ import com.newrelic.agent.android.measurement.consumer.MeasurementConsumer;
/*     */ import com.newrelic.agent.android.measurement.consumer.MethodMeasurementConsumer;
/*     */ import com.newrelic.agent.android.measurement.consumer.SummaryMetricMeasurementConsumer;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import com.newrelic.agent.android.measurement.producer.ActivityMeasurementProducer;
/*     */ import com.newrelic.agent.android.measurement.producer.CustomMetricProducer;
/*     */ import com.newrelic.agent.android.measurement.producer.HttpErrorMeasurementProducer;
/*     */ import com.newrelic.agent.android.measurement.producer.MeasurementProducer;
/*     */ import com.newrelic.agent.android.measurement.producer.MethodMeasurementProducer;
/*     */ import com.newrelic.agent.android.measurement.producer.NetworkMeasurementProducer;
/*     */ import com.newrelic.agent.android.metric.MetricUnit;
/*     */ import com.newrelic.agent.android.tracing.Trace;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Measurements
/*     */ {
/*  21 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*  23 */   private static final MeasurementEngine measurementEngine = new MeasurementEngine();
/*     */ 
/*  26 */   private static final HttpErrorMeasurementProducer httpErrorMeasurementProducer = new HttpErrorMeasurementProducer();
/*  27 */   private static final NetworkMeasurementProducer networkMeasurementProducer = new NetworkMeasurementProducer();
/*  28 */   private static final ActivityMeasurementProducer activityMeasurementProducer = new ActivityMeasurementProducer();
/*  29 */   private static final MethodMeasurementProducer methodMeasurementProducer = new MethodMeasurementProducer();
/*  30 */   private static final CustomMetricProducer customMetricProducer = new CustomMetricProducer();
/*     */ 
/*  33 */   private static final HttpErrorHarvestingConsumer httpErrorHarvester = new HttpErrorHarvestingConsumer();
/*  34 */   private static final HttpTransactionHarvestingConsumer httpTransactionHarvester = new HttpTransactionHarvestingConsumer();
/*  35 */   private static final ActivityMeasurementConsumer activityConsumer = new ActivityMeasurementConsumer();
/*  36 */   private static final MethodMeasurementConsumer methodMeasurementConsumer = new MethodMeasurementConsumer();
/*  37 */   private static final SummaryMetricMeasurementConsumer summaryMetricMeasurementConsumer = new SummaryMetricMeasurementConsumer();
/*  38 */   private static final CustomMetricConsumer customMetricConsumer = new CustomMetricConsumer();
/*     */ 
/*  40 */   private static boolean broadcastNewMeasurements = true;
/*     */ 
/*     */   public static void initialize()
/*     */   {
/*  46 */     log.info("Measurement Engine initialized.");
/*  47 */     TaskQueue.start();
/*     */ 
/*  50 */     addMeasurementProducer(httpErrorMeasurementProducer);
/*  51 */     addMeasurementProducer(networkMeasurementProducer);
/*     */ 
/*  53 */     addMeasurementProducer(activityMeasurementProducer);
/*  54 */     addMeasurementProducer(methodMeasurementProducer);
/*  55 */     addMeasurementProducer(customMetricProducer);
/*     */ 
/*  58 */     addMeasurementConsumer(httpErrorHarvester);
/*  59 */     addMeasurementConsumer(httpTransactionHarvester);
/*  60 */     addMeasurementConsumer(activityConsumer);
/*  61 */     addMeasurementConsumer(methodMeasurementConsumer);
/*  62 */     addMeasurementConsumer(summaryMetricMeasurementConsumer);
/*  63 */     addMeasurementConsumer(customMetricConsumer);
/*     */   }
/*     */ 
/*     */   public static void shutdown()
/*     */   {
/*  70 */     TaskQueue.stop();
/*  71 */     measurementEngine.clear();
/*     */ 
/*  73 */     log.info("Measurement Engine shutting down.");
/*  74 */     removeMeasurementProducer(httpErrorMeasurementProducer);
/*  75 */     removeMeasurementProducer(networkMeasurementProducer);
/*  76 */     removeMeasurementProducer(activityMeasurementProducer);
/*  77 */     removeMeasurementProducer(methodMeasurementProducer);
/*  78 */     removeMeasurementProducer(customMetricProducer);
/*     */ 
/*  80 */     removeMeasurementConsumer(httpErrorHarvester);
/*  81 */     removeMeasurementConsumer(httpTransactionHarvester);
/*  82 */     removeMeasurementConsumer(activityConsumer);
/*  83 */     removeMeasurementConsumer(methodMeasurementConsumer);
/*  84 */     removeMeasurementConsumer(summaryMetricMeasurementConsumer);
/*  85 */     removeMeasurementConsumer(customMetricConsumer);
/*     */   }
/*     */ 
/*     */   public static void addHttpError(String url, String httpMethod, int statusCode)
/*     */   {
/*  99 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 101 */     httpErrorMeasurementProducer.produceMeasurement(url, httpMethod, statusCode);
/* 102 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void addHttpError(String url, String httpMethod, int statusCode, String responseBody)
/*     */   {
/* 113 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 115 */     httpErrorMeasurementProducer.produceMeasurement(url, httpMethod, statusCode, responseBody);
/* 116 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void addHttpError(String url, String httpMethod, int statusCode, String responseBody, Map<String, String> params)
/*     */   {
/* 128 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 130 */     httpErrorMeasurementProducer.produceMeasurement(url, httpMethod, statusCode, responseBody, params);
/* 131 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void addHttpError(String url, String httpMethod, int statusCode, String responseBody, Map<String, String> params, ThreadInfo threadInfo)
/*     */   {
/* 144 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 146 */     httpErrorMeasurementProducer.produceMeasurement(url, httpMethod, statusCode, responseBody, params, threadInfo);
/* 147 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void addHttpTransaction(HttpTransactionMeasurement transactionMeasurement)
/*     */   {
/* 153 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 155 */     networkMeasurementProducer.produceMeasurement(transactionMeasurement);
/* 156 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void addHttpError(TransactionData transactionData, String responseBody, Map<String, String> params) {
/* 160 */     addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), responseBody, params);
/*     */   }
/*     */ 
/*     */   public static void addCustomMetric(String name, String category, int count, double totalValue, double exclusiveValue)
/*     */   {
/* 166 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 168 */     customMetricProducer.produceMeasurement(name, category, count, totalValue, exclusiveValue);
/* 169 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void addCustomMetric(String name, String category, int count, double totalValue, double exclusiveValue, MetricUnit countUnit, MetricUnit valueUnit) {
/* 173 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 175 */     customMetricProducer.produceMeasurement(name, category, count, totalValue, exclusiveValue, countUnit, valueUnit);
/* 176 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void setBroadcastNewMeasurements(boolean broadcast) {
/* 180 */     broadcastNewMeasurements = broadcast;
/*     */   }
/*     */ 
/*     */   private static void newMeasurementBroadcast() {
/* 184 */     if (broadcastNewMeasurements)
/* 185 */       broadcast();
/*     */   }
/*     */ 
/*     */   public static void broadcast() {
/* 189 */     measurementEngine.broadcastMeasurements();
/*     */   }
/*     */ 
/*     */   public static MeasuredActivity startActivity(String activityName)
/*     */   {
/* 201 */     if (Harvest.isDisabled()) return null;
/*     */ 
/* 203 */     return measurementEngine.startActivity(activityName);
/*     */   }
/*     */ 
/*     */   public static void renameActivity(String oldName, String newName)
/*     */   {
/* 213 */     measurementEngine.renameActivity(oldName, newName);
/*     */   }
/*     */ 
/*     */   public static void endActivity(String activityName)
/*     */   {
/* 222 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 224 */     MeasuredActivity measuredActivity = measurementEngine.endActivity(activityName);
/* 225 */     activityMeasurementProducer.produceMeasurement(measuredActivity);
/* 226 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void endActivity(MeasuredActivity activity)
/*     */   {
/* 235 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 237 */     measurementEngine.endActivity(activity);
/* 238 */     activityMeasurementProducer.produceMeasurement(activity);
/* 239 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void endActivityWithoutMeasurement(MeasuredActivity activity)
/*     */   {
/* 248 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 250 */     measurementEngine.endActivity(activity);
/*     */   }
/*     */ 
/*     */   public static void addTracedMethod(Trace trace) {
/* 254 */     if (Harvest.isDisabled()) return;
/*     */ 
/* 256 */     methodMeasurementProducer.produceMeasurement(trace);
/* 257 */     newMeasurementBroadcast();
/*     */   }
/*     */ 
/*     */   public static void addMeasurementProducer(MeasurementProducer measurementProducer)
/*     */   {
/* 266 */     measurementEngine.addMeasurementProducer(measurementProducer);
/*     */   }
/*     */ 
/*     */   public static void removeMeasurementProducer(MeasurementProducer measurementProducer)
/*     */   {
/* 275 */     measurementEngine.removeMeasurementProducer(measurementProducer);
/*     */   }
/*     */ 
/*     */   public static void addMeasurementConsumer(MeasurementConsumer measurementConsumer)
/*     */   {
/* 284 */     measurementEngine.addMeasurementConsumer(measurementConsumer);
/*     */   }
/*     */ 
/*     */   public static void removeMeasurementConsumer(MeasurementConsumer measurementConsumer)
/*     */   {
/* 293 */     measurementEngine.removeMeasurementConsumer(measurementConsumer);
/*     */   }
/*     */ 
/*     */   public static void process()
/*     */   {
/* 301 */     measurementEngine.broadcastMeasurements();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.Measurements
 * JD-Core Version:    0.6.2
 */