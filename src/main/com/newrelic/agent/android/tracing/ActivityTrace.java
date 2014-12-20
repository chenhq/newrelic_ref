/*     */ package com.newrelic.agent.android.tracing;
/*     */ 
/*     */ import com.newrelic.agent.android.Measurements;
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.activity.NamedActivity;
/*     */ import com.newrelic.agent.android.harvest.ActivitySighting;
/*     */ import com.newrelic.agent.android.harvest.ConnectInformation;
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.com.google.gson.Gson;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class ActivityTrace extends HarvestableArray
/*     */ {
/*     */   public static final String TRACE_VERSION = "1.0";
/*     */   public static final int MAX_TRACES = 2000;
/*     */   public Trace rootTrace;
/*  27 */   private final ConcurrentHashMap<UUID, Trace> traces = new ConcurrentHashMap();
/*  28 */   private int traceCount = 0;
/*  29 */   private final Set<UUID> missingChildren = Collections.synchronizedSet(new HashSet());
/*     */   private NamedActivity measuredActivity;
/*  32 */   private long reportAttemptCount = 0L;
/*     */   public long lastUpdatedAt;
/*     */   public long startedAt;
/*     */   public ActivitySighting previousActivity;
/*  37 */   private boolean complete = false;
/*     */ 
/*  39 */   private final HashMap<String, String> params = new HashMap();
/*     */   private Map<Sample.SampleType, Collection<Sample>> vitals;
/*  41 */   private final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private static final String SIZE_NORMAL = "NORMAL";
/*  47 */   private static final HashMap<String, String> ENVIRONMENT_TYPE = new HashMap() { } ;
/*     */ 
/*  50 */   private static final HashMap<String, String> VITALS_TYPE = new HashMap() { } ;
/*     */ 
/*  54 */   private static final HashMap<String, String> ACTIVITY_HISTORY_TYPE = new HashMap() { } ;
/*     */ 
/*     */   public ActivityTrace()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ActivityTrace(Trace rootTrace)
/*     */   {
/*  62 */     this.rootTrace = rootTrace;
/*     */ 
/*  64 */     this.lastUpdatedAt = rootTrace.entryTimestamp;
/*  65 */     this.startedAt = this.lastUpdatedAt;
/*     */ 
/*  67 */     this.params.put("traceVersion", "1.0");
/*     */ 
/*  69 */     this.params.put("type", "ACTIVITY");
/*     */ 
/*  72 */     this.measuredActivity = ((NamedActivity)Measurements.startActivity(rootTrace.displayName));
/*  73 */     this.measuredActivity.setStartTime(rootTrace.entryTimestamp);
/*     */   }
/*     */ 
/*     */   public String getId() {
/*  77 */     if (this.rootTrace == null) {
/*  78 */       return null;
/*     */     }
/*  80 */     return this.rootTrace.myUUID.toString();
/*     */   }
/*     */ 
/*     */   public void addTrace(Trace trace) {
/*  84 */     this.missingChildren.add(trace.myUUID);
/*     */ 
/*  86 */     this.lastUpdatedAt = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public void addCompletedTrace(Trace trace)
/*     */   {
/*  91 */     trace.traceMachine = null;
/*     */ 
/*  93 */     this.missingChildren.remove(trace.myUUID);
/*     */ 
/*  95 */     if (this.traceCount > 2000) {
/*  96 */       this.log.debug("Maximum trace limit reached, discarding trace " + trace.myUUID);
/*  97 */       return;
/*     */     }
/*     */ 
/* 100 */     this.traces.put(trace.myUUID, trace);
/* 101 */     this.traceCount += 1;
/*     */ 
/* 105 */     if (trace.exitTimestamp > this.rootTrace.exitTimestamp) {
/* 106 */       this.rootTrace.exitTimestamp = trace.exitTimestamp;
/*     */     }
/* 108 */     if (this.log.getLevel() == 5) {
/* 109 */       this.log.debug("Added trace " + trace.myUUID.toString() + " missing children: " + this.missingChildren.size());
/*     */     }
/* 111 */     this.lastUpdatedAt = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public boolean hasMissingChildren() {
/* 115 */     return !this.missingChildren.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean isComplete() {
/* 119 */     return this.complete;
/*     */   }
/*     */ 
/*     */   public void discard() {
/* 123 */     if (this.log.getLevel() == 5) {
/* 124 */       this.log.debug("Discarding trace of " + this.rootTrace.displayName + ":" + this.rootTrace.myUUID.toString() + "(" + this.traces.size() + " traces)");
/*     */     }
/* 126 */     this.rootTrace.traceMachine = null;
/* 127 */     this.complete = true;
/* 128 */     Measurements.endActivityWithoutMeasurement(this.measuredActivity);
/*     */   }
/*     */ 
/*     */   public void complete() {
/* 132 */     if (this.log.getLevel() == 5) {
/* 133 */       this.log.debug("Completing trace of " + this.rootTrace.displayName + ":" + this.rootTrace.myUUID.toString() + "(" + this.traces.size() + " traces)");
/*     */     }
/*     */ 
/* 136 */     if (this.rootTrace.exitTimestamp == 0L) {
/* 137 */       this.rootTrace.exitTimestamp = System.currentTimeMillis();
/*     */     }
/*     */ 
/* 140 */     if (this.traces.isEmpty()) {
/* 141 */       this.rootTrace.traceMachine = null;
/* 142 */       this.complete = true;
/* 143 */       Measurements.endActivityWithoutMeasurement(this.measuredActivity);
/*     */ 
/* 145 */       return;
/*     */     }
/*     */ 
/* 149 */     this.measuredActivity.setEndTime(this.rootTrace.exitTimestamp);
/* 150 */     Measurements.endActivity(this.measuredActivity);
/*     */ 
/* 153 */     this.rootTrace.traceMachine = null;
/* 154 */     this.complete = true;
/*     */ 
/* 156 */     TaskQueue.queue(this);
/*     */   }
/*     */ 
/*     */   public Map<UUID, Trace> getTraces() {
/* 160 */     return this.traces;
/*     */   }
/*     */ 
/*     */   public JsonArray asJsonArray()
/*     */   {
/* 165 */     JsonArray tree = new JsonArray();
/*     */ 
/* 167 */     if (!this.complete) {
/* 168 */       this.log.debug("Attempted to serialize trace " + this.rootTrace.myUUID.toString() + " but it has yet to be finalized");
/* 169 */       return null;
/*     */     }
/*     */ 
/* 172 */     tree.add(new Gson().toJsonTree(this.params, GSON_STRING_MAP_TYPE));
/* 173 */     tree.add(new JsonPrimitive(Long.valueOf(this.rootTrace.entryTimestamp)));
/* 174 */     tree.add(new JsonPrimitive(Long.valueOf(this.rootTrace.exitTimestamp)));
/* 175 */     tree.add(new JsonPrimitive(this.rootTrace.displayName));
/*     */ 
/* 177 */     JsonArray segments = new JsonArray();
/* 178 */     segments.add(getEnvironment());
/* 179 */     segments.add(traceToTree(this.rootTrace));
/* 180 */     segments.add(getVitalsAsJson());
/*     */ 
/* 183 */     if (this.previousActivity != null) {
/* 184 */       segments.add(getPreviousActivityAsJson());
/*     */     }
/*     */ 
/* 187 */     tree.add(segments);
/*     */ 
/* 189 */     return tree;
/*     */   }
/*     */ 
/*     */   private JsonArray traceToTree(Trace trace) {
/* 193 */     JsonArray segment = new JsonArray();
/*     */ 
/* 195 */     trace.prepareForSerialization();
/*     */ 
/* 197 */     segment.add(new Gson().toJsonTree(trace.getParams(), GSON_STRING_MAP_TYPE));
/* 198 */     segment.add(new JsonPrimitive(Long.valueOf(trace.entryTimestamp)));
/* 199 */     segment.add(new JsonPrimitive(Long.valueOf(trace.exitTimestamp)));
/* 200 */     segment.add(new JsonPrimitive(trace.displayName));
/*     */ 
/* 202 */     JsonArray threadData = new JsonArray();
/* 203 */     threadData.add(new JsonPrimitive(Long.valueOf(trace.threadId)));
/* 204 */     threadData.add(new JsonPrimitive(trace.threadName));
/*     */ 
/* 206 */     segment.add(threadData);
/*     */ 
/* 211 */     if (trace.getChildren().isEmpty()) {
/* 212 */       segment.add(new JsonArray());
/*     */     } else {
/* 214 */       JsonArray children = new JsonArray();
/*     */ 
/* 216 */       for (UUID traceUUID : trace.getChildren())
/*     */       {
/* 218 */         Trace childTrace = (Trace)this.traces.get(traceUUID);
/* 219 */         if (childTrace != null) {
/* 220 */           children.add(traceToTree(childTrace));
/*     */         }
/*     */       }
/*     */ 
/* 224 */       segment.add(children);
/*     */     }
/*     */ 
/* 227 */     return segment;
/*     */   }
/*     */ 
/*     */   private JsonArray getEnvironment() {
/* 231 */     JsonArray environment = new JsonArray();
/*     */ 
/* 233 */     environment.add(new Gson().toJsonTree(ENVIRONMENT_TYPE, GSON_STRING_MAP_TYPE));
/*     */ 
/* 236 */     ConnectInformation connectInformation = new ConnectInformation();
/* 237 */     environment.addAll(connectInformation.asJsonArray());
/*     */ 
/* 240 */     HashMap environmentParams = new HashMap();
/* 241 */     environmentParams.put("size", "NORMAL");
/* 242 */     environment.add(new Gson().toJsonTree(environmentParams, GSON_STRING_MAP_TYPE));
/*     */ 
/* 244 */     return environment;
/*     */   }
/*     */ 
/*     */   public void setVitals(Map<Sample.SampleType, Collection<Sample>> vitals) {
/* 248 */     this.vitals = vitals;
/*     */   }
/*     */ 
/*     */   private JsonArray getVitalsAsJson() {
/* 252 */     JsonArray vitalsJson = new JsonArray();
/*     */ 
/* 254 */     vitalsJson.add(new Gson().toJsonTree(VITALS_TYPE, GSON_STRING_MAP_TYPE));
/*     */ 
/* 256 */     JsonObject vitalsMap = new JsonObject();
/*     */ 
/* 271 */     if (this.vitals != null) {
/* 272 */       for (Map.Entry entry : this.vitals.entrySet()) {
/* 273 */         JsonArray samplesJsonArray = new JsonArray();
/*     */ 
/* 275 */         for (Sample sample : (Collection)entry.getValue())
/*     */         {
/* 277 */           if (sample.getTimestamp() <= this.lastUpdatedAt) {
/* 278 */             samplesJsonArray.add(sample.asJsonArray());
/*     */           }
/*     */         }
/* 281 */         vitalsMap.add(((Sample.SampleType)entry.getKey()).toString(), samplesJsonArray);
/*     */       }
/*     */     }
/*     */ 
/* 285 */     vitalsJson.add(vitalsMap);
/*     */ 
/* 287 */     return vitalsJson;
/*     */   }
/*     */ 
/*     */   private JsonArray getPreviousActivityAsJson() {
/* 291 */     JsonArray historyJson = new JsonArray();
/*     */ 
/* 293 */     historyJson.add(new Gson().toJsonTree(ACTIVITY_HISTORY_TYPE, GSON_STRING_MAP_TYPE));
/* 294 */     historyJson.addAll(this.previousActivity.asJsonArray());
/*     */ 
/* 296 */     return historyJson;
/*     */   }
/*     */ 
/*     */   public void setLastUpdatedAt(long lastUpdatedAt) {
/* 300 */     this.lastUpdatedAt = lastUpdatedAt;
/*     */   }
/*     */ 
/*     */   public long getLastUpdatedAt() {
/* 304 */     return this.lastUpdatedAt;
/*     */   }
/*     */ 
/*     */   public long getReportAttemptCount() {
/* 308 */     return this.reportAttemptCount;
/*     */   }
/*     */ 
/*     */   public void incrementReportAttemptCount() {
/* 312 */     this.reportAttemptCount += 1L;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.tracing.ActivityTrace
 * JD-Core Version:    0.6.2
 */