/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.Agent;
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*     */ import com.newrelic.agent.android.stats.StatsEngine;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ 
/*     */ public class HarvestData extends HarvestableArray
/*     */ {
/*     */   private DataToken dataToken;
/*     */   private DeviceInformation deviceInformation;
/*     */   private double harvestTimeDelta;
/*     */   private HttpTransactions httpTransactions;
/*     */   private MachineMeasurements machineMeasurements;
/*     */   private HttpErrors httpErrors;
/*     */   private ActivityTraces activityTraces;
/*     */   private AgentHealth agentHealth;
/*     */ 
/*     */   public HarvestData()
/*     */   {
/*  34 */     this.dataToken = new DataToken();
/*  35 */     this.httpTransactions = new HttpTransactions();
/*  36 */     this.httpErrors = new HttpErrors();
/*  37 */     this.activityTraces = new ActivityTraces();
/*  38 */     this.machineMeasurements = new MachineMeasurements();
/*  39 */     this.deviceInformation = Agent.getDeviceInformation();
/*  40 */     this.agentHealth = new AgentHealth();
/*     */   }
/*     */ 
/*     */   public JsonArray asJsonArray()
/*     */   {
/*  49 */     JsonArray array = new JsonArray();
/*  50 */     array.add(this.dataToken.asJson());
/*  51 */     array.add(this.deviceInformation.asJson());
/*  52 */     array.add(new JsonPrimitive(Double.valueOf(this.harvestTimeDelta)));
/*  53 */     array.add(this.httpTransactions.asJson());
/*  54 */     array.add(this.machineMeasurements.asJson());
/*  55 */     array.add(this.httpErrors.asJson());
/*     */ 
/*  57 */     JsonElement activityTracesElement = this.activityTraces.asJson();
/*     */ 
/*  60 */     String activityTraceJson = activityTracesElement.toString();
/*  61 */     if (activityTraceJson.length() < Harvest.getHarvestConfiguration().getActivity_trace_max_size())
/*  62 */       array.add(activityTracesElement);
/*     */     else {
/*  64 */       StatsEngine.get().sample("Supportability/AgentHealth/BigActivityTracesDropped", activityTraceJson.length());
/*     */     }
/*     */ 
/*  67 */     array.add(this.agentHealth.asJson());
/*     */ 
/*  69 */     return array;
/*     */   }
/*     */ 
/*     */   public void reset() {
/*  73 */     this.httpErrors.clear();
/*  74 */     this.httpTransactions.clear();
/*  75 */     this.activityTraces.clear();
/*  76 */     this.machineMeasurements.clear();
/*  77 */     this.agentHealth.clear();
/*     */   }
/*     */ 
/*     */   public void setDataToken(DataToken dataToken) {
/*  81 */     if (dataToken == null)
/*  82 */       return;
/*  83 */     this.dataToken = dataToken;
/*     */   }
/*     */ 
/*     */   public void setDeviceInformation(DeviceInformation deviceInformation) {
/*  87 */     this.deviceInformation = deviceInformation;
/*     */   }
/*     */ 
/*     */   public void setHarvestTimeDelta(double harvestTimeDelta) {
/*  91 */     this.harvestTimeDelta = harvestTimeDelta;
/*     */   }
/*     */ 
/*     */   public void setHttpTransactions(HttpTransactions httpTransactions) {
/*  95 */     this.httpTransactions = httpTransactions;
/*     */   }
/*     */ 
/*     */   public void setMachineMeasurements(MachineMeasurements machineMeasurements) {
/*  99 */     this.machineMeasurements = machineMeasurements;
/*     */   }
/*     */ 
/*     */   public void setActivityTraces(ActivityTraces activityTraces) {
/* 103 */     this.activityTraces = activityTraces;
/*     */   }
/*     */ 
/*     */   public void setHttpErrors(HttpErrors httpErrors) {
/* 107 */     this.httpErrors = httpErrors;
/*     */   }
/*     */ 
/*     */   public DeviceInformation getDeviceInformation() {
/* 111 */     return this.deviceInformation;
/*     */   }
/*     */ 
/*     */   public HttpErrors getHttpErrors() {
/* 115 */     return this.httpErrors;
/*     */   }
/*     */ 
/*     */   public HttpTransactions getHttpTransactions() {
/* 119 */     return this.httpTransactions;
/*     */   }
/*     */ 
/*     */   public MachineMeasurements getMetrics() {
/* 123 */     return this.machineMeasurements;
/*     */   }
/*     */ 
/*     */   public ActivityTraces getActivityTraces() {
/* 127 */     return this.activityTraces;
/*     */   }
/*     */ 
/*     */   public AgentHealth getAgentHealth() {
/* 131 */     return this.agentHealth;
/*     */   }
/*     */ 
/*     */   public DataToken getDataToken() {
/* 135 */     return this.dataToken;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 140 */     return "HarvestData{dataToken=" + this.dataToken + ", deviceInformation=" + this.deviceInformation + ", harvestTimeDelta=" + this.harvestTimeDelta + ", httpTransactions=" + this.httpTransactions + ", machineMeasurements=" + this.machineMeasurements + ", httpErrors=" + this.httpErrors + ", activityTraces=" + this.activityTraces + '}';
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestData
 * JD-Core Version:    0.6.2
 */