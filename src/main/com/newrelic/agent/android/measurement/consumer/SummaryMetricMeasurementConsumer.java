/*     */ package com.newrelic.agent.android.measurement.consumer;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.Harvest;
/*     */ import com.newrelic.agent.android.instrumentation.MetricCategory;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.measurement.BaseMeasurement;
/*     */ import com.newrelic.agent.android.measurement.CustomMetricMeasurement;
/*     */ import com.newrelic.agent.android.measurement.Measurement;
/*     */ import com.newrelic.agent.android.measurement.MeasurementType;
/*     */ import com.newrelic.agent.android.measurement.MethodMeasurement;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import com.newrelic.agent.android.metric.Metric;
/*     */ import com.newrelic.agent.android.metric.MetricStore;
/*     */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*     */ import com.newrelic.agent.android.tracing.Trace;
/*     */ import com.newrelic.agent.android.tracing.TraceLifecycleAware;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ 
/*     */ public class SummaryMetricMeasurementConsumer extends MetricMeasurementConsumer
/*     */   implements TraceLifecycleAware
/*     */ {
/*     */   private static final String METRIC_PREFIX = "Mobile/Summary/";
/*     */   private static final String ACTIVITY_METRIC_PREFIX = "Mobile/Activity/Summary/Name/";
/*  24 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*  26 */   private final List<ActivityTrace> completedTraces = new CopyOnWriteArrayList();
/*     */ 
/*     */   public SummaryMetricMeasurementConsumer() {
/*  29 */     super(MeasurementType.Any);
/*  30 */     this.recordUnscopedMetrics = false;
/*     */ 
/*  32 */     TraceMachine.addTraceListener(this);
/*     */   }
/*     */ 
/*     */   public void consumeMeasurement(Measurement measurement)
/*     */   {
/*  37 */     if (measurement == null) {
/*  38 */       return;
/*     */     }
/*  40 */     switch (1.$SwitchMap$com$newrelic$agent$android$measurement$MeasurementType[measurement.getType().ordinal()]) {
/*     */     case 1:
/*  42 */       consumeMethodMeasurement((MethodMeasurement)measurement);
/*  43 */       break;
/*     */     case 2:
/*  45 */       consumeNetworkMeasurement((HttpTransactionMeasurement)measurement);
/*  46 */       break;
/*     */     case 3:
/*  48 */       consumeCustomMeasurement((CustomMetricMeasurement)measurement);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void consumeMethodMeasurement(MethodMeasurement methodMeasurement)
/*     */   {
/*  55 */     if ((methodMeasurement.getCategory() == null) || (methodMeasurement.getCategory() == MetricCategory.NONE)) {
/*  56 */       methodMeasurement.setCategory(MetricCategory.categoryForMethod(methodMeasurement.getName()));
/*  57 */       if (methodMeasurement.getCategory() == MetricCategory.NONE) {
/*  58 */         return;
/*     */       }
/*     */     }
/*     */ 
/*  62 */     BaseMeasurement summary = new BaseMeasurement(methodMeasurement);
/*  63 */     summary.setName(methodMeasurement.getCategory().getCategoryName());
/*     */ 
/*  65 */     super.consumeMeasurement(summary);
/*     */   }
/*     */ 
/*     */   private void consumeCustomMeasurement(CustomMetricMeasurement customMetricMeasurement) {
/*  69 */     if ((customMetricMeasurement.getCategory() == null) || (customMetricMeasurement.getCategory() == MetricCategory.NONE)) {
/*  70 */       return;
/*     */     }
/*  72 */     BaseMeasurement summary = new BaseMeasurement(customMetricMeasurement);
/*  73 */     summary.setName(customMetricMeasurement.getCategory().getCategoryName());
/*  74 */     super.consumeMeasurement(summary);
/*     */   }
/*     */ 
/*     */   private void consumeNetworkMeasurement(HttpTransactionMeasurement networkMeasurement) {
/*  78 */     BaseMeasurement summary = new BaseMeasurement(networkMeasurement);
/*  79 */     summary.setName(MetricCategory.NETWORK.getCategoryName());
/*  80 */     super.consumeMeasurement(summary);
/*     */   }
/*     */ 
/*     */   protected String formatMetricName(String name)
/*     */   {
/*  85 */     return "Mobile/Summary/" + name.replace("#", "/");
/*     */   }
/*     */ 
/*     */   public void onHarvest()
/*     */   {
/*  90 */     if (this.metrics.getAll().size() == 0) {
/*  91 */       return;
/*     */     }
/*     */ 
/*  94 */     if (this.completedTraces.size() == 0)
/*     */     {
/*  96 */       return;
/*     */     }
/*     */ 
/*  99 */     for (ActivityTrace trace : this.completedTraces) {
/* 100 */       summarizeActivityMetrics(trace);
/*     */     }
/*     */ 
/* 103 */     if (this.metrics.getAll().size() != 0) {
/* 104 */       log.debug("Not all metrics were summarized!");
/*     */     }
/*     */ 
/* 107 */     this.completedTraces.clear();
/*     */   }
/*     */ 
/*     */   private void summarizeActivityMetrics(ActivityTrace activityTrace) {
/* 111 */     Trace trace = activityTrace.rootTrace;
/*     */ 
/* 114 */     List activityMetrics = this.metrics.removeAllWithScope(trace.metricName);
/* 115 */     List backgroundMetrics = this.metrics.removeAllWithScope(trace.metricBackgroundName);
/* 116 */     Map summaryMetrics = new HashMap();
/*     */ 
/* 122 */     for (Metric activityMetric : activityMetrics) {
/* 123 */       summaryMetrics.put(activityMetric.getName(), activityMetric);
/*     */     }
/*     */ 
/* 128 */     for (Metric backgroundMetric : backgroundMetrics) {
/* 129 */       if (summaryMetrics.containsKey(backgroundMetric.getName()))
/* 130 */         ((Metric)summaryMetrics.get(backgroundMetric.getName())).aggregate(backgroundMetric);
/*     */       else {
/* 132 */         summaryMetrics.put(backgroundMetric.getName(), backgroundMetric);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 137 */     double totalExclusiveTime = 0.0D;
/* 138 */     for (Metric metric : summaryMetrics.values()) {
/* 139 */       totalExclusiveTime += metric.getExclusive();
/*     */     }
/*     */ 
/* 142 */     double traceTime = (trace.exitTimestamp - trace.entryTimestamp) / 1000.0D;
/*     */ 
/* 145 */     for (Metric metric : summaryMetrics.values()) {
/* 146 */       double normalizedTime = 0.0D;
/*     */ 
/* 148 */       if ((metric.getExclusive() != 0.0D) && (totalExclusiveTime != 0.0D)) {
/* 149 */         normalizedTime = metric.getExclusive() / totalExclusiveTime;
/*     */       }
/*     */ 
/* 152 */       double scaledTime = normalizedTime * traceTime;
/*     */ 
/* 154 */       metric.setTotal(Double.valueOf(scaledTime));
/* 155 */       metric.setExclusive(Double.valueOf(scaledTime));
/* 156 */       metric.setMinFieldValue(Double.valueOf(0.0D));
/* 157 */       metric.setMaxFieldValue(Double.valueOf(0.0D));
/* 158 */       metric.setSumOfSquares(Double.valueOf(0.0D));
/* 159 */       metric.setScope("Mobile/Activity/Summary/Name/" + trace.displayName);
/*     */ 
/* 163 */       Harvest.addMetric(metric);
/*     */ 
/* 165 */       Metric unScoped = new Metric(metric);
/* 166 */       unScoped.setScope(null);
/* 167 */       Harvest.addMetric(unScoped);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onHarvestError()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onHarvestComplete()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onTraceStart(ActivityTrace activityTrace)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onTraceComplete(ActivityTrace activityTrace)
/*     */   {
/* 186 */     if (!this.completedTraces.contains(activityTrace))
/* 187 */       this.completedTraces.add(activityTrace);
/*     */   }
/*     */ 
/*     */   public void onEnterMethod()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onExitMethod()
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.consumer.SummaryMetricMeasurementConsumer
 * JD-Core Version:    0.6.2
 */