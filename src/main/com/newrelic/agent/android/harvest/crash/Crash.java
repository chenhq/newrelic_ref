/*     */ package com.newrelic.agent.android.harvest.crash;
/*     */ 
/*     */ import com.newrelic.agent.android.Agent;
/*     */ import com.newrelic.agent.android.AgentConfiguration;
/*     */ import com.newrelic.agent.android.AgentImpl;
/*     */ import com.newrelic.agent.android.crashes.CrashReporter;
/*     */ import com.newrelic.agent.android.harvest.ActivityHistory;
/*     */ import com.newrelic.agent.android.harvest.DataToken;
/*     */ import com.newrelic.agent.android.harvest.Harvest;
/*     */ import com.newrelic.agent.android.harvest.HarvestConfiguration;
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import com.newrelic.agent.android.util.Util;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonParser;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class Crash extends HarvestableObject
/*     */ {
/*     */   public static final int PROTOCOL_VERSION = 1;
/*     */   private final UUID uuid;
/*     */   private final String buildId;
/*     */   private final long timestamp;
/*     */   private final String appToken;
/*     */   private DeviceInfo deviceInfo;
/*     */   private ApplicationInfo applicationInfo;
/*     */   private ExceptionInfo exceptionInfo;
/*     */   private List<ThreadInfo> threads;
/*     */   private ActivityHistory activityHistory;
/*     */ 
/*     */   public Crash(UUID uuid, String buildId, long timestamp)
/*     */   {
/*  35 */     this.uuid = uuid;
/*  36 */     this.buildId = buildId;
/*  37 */     this.timestamp = timestamp;
/*  38 */     this.appToken = CrashReporter.getAgentConfiguration().getApplicationToken();
/*     */   }
/*     */ 
/*     */   public Crash(Throwable throwable) {
/*  42 */     AgentImpl agentImpl = Agent.getImpl();
/*     */ 
/*  44 */     Throwable cause = getRootCause(throwable);
/*     */ 
/*  46 */     this.uuid = new UUID(Util.getRandom().nextLong(), Util.getRandom().nextLong());
/*  47 */     this.buildId = getBuildId();
/*  48 */     this.timestamp = (System.currentTimeMillis() / 1000L);
/*  49 */     this.appToken = CrashReporter.getAgentConfiguration().getApplicationToken();
/*  50 */     this.deviceInfo = new DeviceInfo(agentImpl.getDeviceInformation(), agentImpl.getEnvironmentInformation());
/*  51 */     this.applicationInfo = new ApplicationInfo(agentImpl.getApplicationInformation());
/*  52 */     this.exceptionInfo = new ExceptionInfo(cause);
/*  53 */     this.threads = ThreadInfo.extractThreads(cause);
/*  54 */     this.activityHistory = TraceMachine.getActivityHistory();
/*     */   }
/*     */ 
/*     */   public static String getBuildId()
/*     */   {
/*  59 */     return "";
/*     */   }
/*     */ 
/*     */   public UUID getUuid() {
/*  63 */     return this.uuid;
/*     */   }
/*     */ 
/*     */   public ExceptionInfo getExceptionInfo() {
/*  67 */     return this.exceptionInfo;
/*     */   }
/*     */ 
/*     */   public JsonObject asJsonObject()
/*     */   {
/*  72 */     JsonObject data = new JsonObject();
/*     */ 
/*  74 */     data.add("protocolVersion", new JsonPrimitive(Integer.valueOf(1)));
/*  75 */     data.add("platform", new JsonPrimitive("Android"));
/*  76 */     data.add("uuid", new JsonPrimitive(this.uuid.toString()));
/*  77 */     data.add("buildId", new JsonPrimitive(this.buildId));
/*  78 */     data.add("timestamp", new JsonPrimitive(Long.valueOf(this.timestamp)));
/*  79 */     data.add("appToken", new JsonPrimitive(this.appToken));
/*  80 */     data.add("deviceInfo", this.deviceInfo.asJsonObject());
/*  81 */     data.add("appInfo", this.applicationInfo.asJsonObject());
/*  82 */     data.add("exception", this.exceptionInfo.asJsonObject());
/*  83 */     data.add("threads", getThreadsAsJson());
/*  84 */     data.add("activityHistory", this.activityHistory.asJsonArrayWithoutDuration());
/*     */ 
/*  86 */     DataToken dataToken = Harvest.getHarvestConfiguration().getDataToken();
/*  87 */     if (dataToken != null) {
/*  88 */       data.add("dataToken", dataToken.asJsonArray());
/*     */     }
/*     */ 
/*  91 */     return data;
/*     */   }
/*     */ 
/*     */   public static Crash crashFromJsonString(String json) {
/*  95 */     JsonElement element = new JsonParser().parse(json);
/*  96 */     JsonObject crashObject = element.getAsJsonObject();
/*     */ 
/*  98 */     String uuid = crashObject.get("uuid").getAsString();
/*  99 */     String buildIdentifier = crashObject.get("buildId").getAsString();
/* 100 */     long timestamp = crashObject.get("timestamp").getAsLong();
/*     */ 
/* 102 */     Crash crash = new Crash(UUID.fromString(uuid), buildIdentifier, timestamp);
/*     */ 
/* 104 */     crash.deviceInfo = DeviceInfo.newFromJson(crashObject.get("deviceInfo").getAsJsonObject());
/* 105 */     crash.applicationInfo = ApplicationInfo.newFromJson(crashObject.get("appInfo").getAsJsonObject());
/* 106 */     crash.exceptionInfo = ExceptionInfo.newFromJson(crashObject.get("exception").getAsJsonObject());
/* 107 */     crash.threads = ThreadInfo.newListFromJson(crashObject.get("threads").getAsJsonArray());
/* 108 */     crash.activityHistory = ActivityHistory.newFromJson(crashObject.get("activityHistory").getAsJsonArray());
/*     */ 
/* 110 */     return crash;
/*     */   }
/*     */ 
/*     */   private static Throwable getRootCause(Throwable throwable) {
/* 114 */     Throwable cause = throwable.getCause();
/*     */ 
/* 116 */     if (cause == null) {
/* 117 */       return throwable;
/*     */     }
/* 119 */     return getRootCause(cause);
/*     */   }
/*     */ 
/*     */   private JsonArray getThreadsAsJson()
/*     */   {
/* 124 */     JsonArray data = new JsonArray();
/*     */ 
/* 126 */     for (ThreadInfo thread : this.threads) {
/* 127 */       data.add(thread.asJsonObject());
/*     */     }
/*     */ 
/* 130 */     return data;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.crash.Crash
 * JD-Core Version:    0.6.2
 */