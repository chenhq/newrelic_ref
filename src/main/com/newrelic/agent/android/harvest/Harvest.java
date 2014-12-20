/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.AgentConfiguration;
/*     */ import com.newrelic.agent.android.activity.config.ActivityTraceConfiguration;
/*     */ import com.newrelic.agent.android.harvest.type.Harvestable;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.metric.Metric;
/*     */ import com.newrelic.agent.android.stats.StatsEngine;
/*     */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*     */ import com.newrelic.agent.android.tracing.Trace;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class Harvest
/*     */ {
/*  17 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private static final boolean DISABLE_ACTIVITY_TRACE_LIMITS_FOR_DEBUGGING = false;
/*  20 */   protected static Harvest instance = new Harvest();
/*     */   private Harvester harvester;
/*     */   private HarvestConnection harvestConnection;
/*     */   private HarvestTimer harvestTimer;
/*     */   protected HarvestData harvestData;
/*     */   private HarvestDataValidator harvestDataValidator;
/*  29 */   private static final Collection<HarvestLifecycleAware> unregisteredLifecycleListeners = new ArrayList();
/*     */ 
/*  32 */   private static final HarvestableCache activityTraceCache = new HarvestableCache();
/*     */ 
/*  34 */   private HarvestConfiguration configuration = HarvestConfiguration.getDefaultHarvestConfiguration();
/*     */ 
/*     */   public static void initialize(AgentConfiguration agentConfiguration)
/*     */   {
/*  38 */     instance.initializeHarvester(agentConfiguration);
/*  39 */     registerUnregisteredListeners();
/*  40 */     addHarvestListener(StatsEngine.get());
/*     */   }
/*     */ 
/*     */   public void initializeHarvester(AgentConfiguration agentConfiguration) {
/*  44 */     createHarvester();
/*  45 */     this.harvester.setAgentConfiguration(agentConfiguration);
/*  46 */     this.harvester.setConfiguration(instance.getConfiguration());
/*  47 */     flushHarvestableCaches();
/*     */   }
/*     */ 
/*     */   public static void setPeriod(long period) {
/*  51 */     instance.getHarvestTimer().setPeriod(period);
/*     */   }
/*     */ 
/*     */   public static void start() {
/*  55 */     instance.getHarvestTimer().start();
/*     */   }
/*     */ 
/*     */   public static void stop() {
/*  59 */     instance.getHarvestTimer().stop();
/*     */   }
/*     */ 
/*     */   public static void harvestNow() {
/*  63 */     if (!isInitialized()) {
/*  64 */       return;
/*     */     }
/*  66 */     StatsEngine.get().sampleTimeMs("Session/Duration", instance.getHarvestTimer().timeSinceStart());
/*  67 */     instance.getHarvestTimer().tickNow();
/*     */   }
/*     */ 
/*     */   public static void setInstance(Harvest harvestInstance) {
/*  71 */     instance = harvestInstance;
/*     */   }
/*     */ 
/*     */   public void createHarvester()
/*     */   {
/*  76 */     this.harvestConnection = new HarvestConnection();
/*  77 */     this.harvestData = new HarvestData();
/*  78 */     this.harvester = new Harvester();
/*  79 */     this.harvester.setHarvestConnection(this.harvestConnection);
/*  80 */     this.harvester.setHarvestData(this.harvestData);
/*  81 */     this.harvestTimer = new HarvestTimer(this.harvester);
/*  82 */     this.harvestDataValidator = new HarvestDataValidator();
/*  83 */     addHarvestListener(this.harvestDataValidator);
/*     */   }
/*     */ 
/*     */   public void shutdownHarvester() {
/*  87 */     this.harvestTimer.shutdown();
/*  88 */     this.harvestTimer = null;
/*  89 */     this.harvester = null;
/*  90 */     this.harvestConnection = null;
/*  91 */     this.harvestData = null;
/*     */   }
/*     */ 
/*     */   public static void shutdown() {
/*  95 */     if (!isInitialized())
/*  96 */       return;
/*  97 */     stop();
/*  98 */     instance.shutdownHarvester();
/*     */   }
/*     */ 
/*     */   public static void addHttpError(HttpError error) {
/* 102 */     if ((!instance.shouldCollectNetworkErrors()) || (isDisabled())) {
/* 103 */       return;
/*     */     }
/* 105 */     HttpErrors errors = instance.getHarvestData().getHttpErrors();
/* 106 */     instance.getHarvester().expireHttpErrors();
/*     */ 
/* 108 */     int errorLimit = instance.getConfiguration().getError_limit();
/* 109 */     if (errors.count() >= errorLimit) {
/* 110 */       StatsEngine.get().inc("Supportability/AgentHealth/ErrorsDropped");
/* 111 */       log.debug("Maximum number of HTTP errors (" + errorLimit + ") reached. HTTP Error dropped.");
/* 112 */       return;
/*     */     }
/* 114 */     errors.addHttpError(error);
/*     */   }
/*     */ 
/*     */   public static void addHttpTransaction(HttpTransaction txn) {
/* 118 */     if (isDisabled()) return;
/*     */ 
/* 120 */     HttpTransactions transactions = instance.getHarvestData().getHttpTransactions();
/* 121 */     instance.getHarvester().expireHttpTransactions();
/*     */ 
/* 123 */     int transactionLimit = instance.getConfiguration().getReport_max_transaction_count();
/* 124 */     if (transactions.count() >= transactionLimit) {
/* 125 */       StatsEngine.get().inc("Supportability/AgentHealth/TransactionsDropped");
/* 126 */       log.debug("Maximum number of transactions (" + transactionLimit + ") reached. HTTP Transaction dropped.");
/* 127 */       return;
/*     */     }
/* 129 */     transactions.add(txn);
/*     */   }
/*     */ 
/*     */   public static void addActivityTrace(ActivityTrace activityTrace) {
/* 133 */     if (isDisabled()) return;
/*     */ 
/* 135 */     if (!isInitialized()) {
/* 136 */       activityTraceCache.add(activityTrace);
/* 137 */       return;
/*     */     }
/*     */ 
/* 140 */     if (activityTrace.rootTrace == null) {
/* 141 */       log.error("Activity trace is lacking a root trace!");
/* 142 */       return;
/*     */     }
/*     */ 
/* 145 */     if (activityTrace.rootTrace.childExclusiveTime == 0L) {
/* 146 */       log.error("Total trace exclusive time is zero. Ignoring trace " + activityTrace.rootTrace.displayName);
/* 147 */       return;
/*     */     }
/*     */ 
/* 150 */     double traceExclusiveTime = activityTrace.rootTrace.childExclusiveTime / 1000.0D;
/* 151 */     if (traceExclusiveTime < instance.getConfiguration().getActivity_trace_min_utilization()) {
/* 152 */       StatsEngine.get().inc("Supportability/AgentHealth/IgnoredTraces");
/* 153 */       log.debug("Total trace exclusive time is too low (" + traceExclusiveTime + "). Ignoring trace " + activityTrace.rootTrace.displayName);
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     ActivityTraces activityTraces = instance.getHarvestData().getActivityTraces();
/* 158 */     ActivityTraceConfiguration configurations = instance.getActivityTraceConfiguration();
/*     */ 
/* 160 */     instance.getHarvester().expireActivityTraces();
/*     */ 
/* 165 */     if (activityTraces.count() >= configurations.getMaxTotalTraceCount()) {
/* 166 */       log.debug("Activity trace limit of " + configurations.getMaxTotalTraceCount() + " exceeded. Ignoring trace: " + activityTrace.toJsonString());
/* 167 */       return;
/*     */     }
/*     */ 
/* 171 */     log.debug("Adding activity trace: " + activityTrace.toJsonString());
/* 172 */     activityTraces.add(activityTrace);
/*     */   }
/*     */ 
/*     */   public static void addMetric(Metric metric) {
/* 176 */     if ((isDisabled()) || (!isInitialized())) return;
/* 177 */     instance.getHarvestData().getMetrics().addMetric(metric);
/*     */   }
/*     */ 
/*     */   public static void addAgentHealthException(AgentHealthException exception) {
/* 181 */     if ((isDisabled()) || (!isInitialized())) return;
/*     */ 
/* 183 */     instance.getHarvestData().getAgentHealth().addException(exception);
/*     */   }
/*     */ 
/*     */   public static void addHarvestListener(HarvestLifecycleAware harvestAware) {
/* 187 */     if (harvestAware == null) {
/* 188 */       log.error("Harvest: Argument to addHarvestListener cannot be null.");
/* 189 */       return;
/*     */     }
/*     */ 
/* 192 */     if (!isInitialized()) {
/* 193 */       if (!isUnregisteredListener(harvestAware)) {
/* 194 */         addUnregisteredListener(harvestAware);
/*     */       }
/* 196 */       return;
/*     */     }
/* 198 */     instance.getHarvester().addHarvestListener(harvestAware);
/*     */   }
/*     */ 
/*     */   public static void removeHarvestListener(HarvestLifecycleAware harvestAware) {
/* 202 */     if (harvestAware == null) {
/* 203 */       log.error("Harvest: Argument to removeHarvestListener cannot be null.");
/* 204 */       return;
/*     */     }
/*     */ 
/* 207 */     if (!isInitialized()) {
/* 208 */       if (isUnregisteredListener(harvestAware)) {
/* 209 */         removeUnregisteredListener(harvestAware);
/*     */       }
/* 211 */       return;
/*     */     }
/* 213 */     instance.getHarvester().removeHarvestListener(harvestAware);
/*     */   }
/*     */ 
/*     */   public static boolean isInitialized() {
/* 217 */     return instance.getHarvester() != null;
/*     */   }
/*     */ 
/*     */   public static int getActivityTraceCacheSize()
/*     */   {
/* 222 */     return activityTraceCache.getSize();
/*     */   }
/*     */ 
/*     */   public static boolean shouldCollectActivityTraces() {
/* 226 */     if (isDisabled()) {
/* 227 */       return false;
/*     */     }
/*     */ 
/* 230 */     if (!isInitialized()) {
/* 231 */       return true;
/*     */     }
/*     */ 
/* 234 */     ActivityTraceConfiguration configurations = instance.getActivityTraceConfiguration();
/* 235 */     if (configurations == null) {
/* 236 */       return true;
/*     */     }
/* 238 */     return configurations.getMaxTotalTraceCount() > 0;
/*     */   }
/*     */ 
/*     */   private void flushHarvestableCaches() {
/*     */     try {
/* 243 */       flushActivityTraceCache();
/*     */     } catch (Exception e) {
/* 245 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void flushActivityTraceCache() {
/* 250 */     Collection activityTraces = activityTraceCache.flush();
/* 251 */     for (Harvestable activityTrace : activityTraces)
/* 252 */       addActivityTrace((ActivityTrace)activityTrace);
/*     */   }
/*     */ 
/*     */   private static void addUnregisteredListener(HarvestLifecycleAware harvestAware)
/*     */   {
/* 257 */     if (harvestAware == null) {
/* 258 */       return;
/*     */     }
/* 260 */     synchronized (unregisteredLifecycleListeners) {
/* 261 */       unregisteredLifecycleListeners.add(harvestAware);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void removeUnregisteredListener(HarvestLifecycleAware harvestAware) {
/* 266 */     if (harvestAware == null) {
/* 267 */       return;
/*     */     }
/* 269 */     synchronized (unregisteredLifecycleListeners) {
/* 270 */       unregisteredLifecycleListeners.remove(harvestAware);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void registerUnregisteredListeners() {
/* 275 */     for (HarvestLifecycleAware harvestAware : unregisteredLifecycleListeners) {
/* 276 */       addHarvestListener(harvestAware);
/*     */     }
/* 278 */     unregisteredLifecycleListeners.clear();
/*     */   }
/*     */ 
/*     */   private static boolean isUnregisteredListener(HarvestLifecycleAware harvestAware) {
/* 282 */     if (harvestAware == null)
/* 283 */       return false;
/* 284 */     return unregisteredLifecycleListeners.contains(harvestAware);
/*     */   }
/*     */ 
/*     */   private HarvestTimer getHarvestTimer() {
/* 288 */     return this.harvestTimer;
/*     */   }
/*     */ 
/*     */   public static Harvest getInstance() {
/* 292 */     return instance;
/*     */   }
/*     */ 
/*     */   protected Harvester getHarvester() {
/* 296 */     return this.harvester;
/*     */   }
/*     */ 
/*     */   public HarvestData getHarvestData() {
/* 300 */     return this.harvestData;
/*     */   }
/*     */ 
/*     */   public HarvestConfiguration getConfiguration() {
/* 304 */     return this.configuration;
/*     */   }
/*     */ 
/*     */   public HarvestConnection getHarvestConnection() {
/* 308 */     return this.harvestConnection;
/*     */   }
/*     */ 
/*     */   public void setHarvestConnection(HarvestConnection connection) {
/* 312 */     this.harvestConnection = connection;
/*     */   }
/*     */ 
/*     */   public boolean shouldCollectNetworkErrors() {
/* 316 */     return this.configuration.isCollect_network_errors();
/*     */   }
/*     */ 
/*     */   public void setConfiguration(HarvestConfiguration newConfiguration) {
/* 320 */     this.configuration.reconfigure(newConfiguration);
/*     */ 
/* 322 */     this.harvestTimer.setPeriod(TimeUnit.MILLISECONDS.convert(this.configuration.getData_report_period(), TimeUnit.SECONDS));
/* 323 */     this.harvestConnection.setServerTimestamp(this.configuration.getServer_timestamp());
/* 324 */     this.harvestData.setDataToken(this.configuration.getDataToken());
/* 325 */     this.harvester.setConfiguration(this.configuration);
/*     */   }
/*     */ 
/*     */   public static void setHarvestConfiguration(HarvestConfiguration configuration) {
/* 329 */     if (!isInitialized()) {
/* 330 */       log.error("Cannot configure Harvester before initialization.");
/* 331 */       new Exception().printStackTrace();
/* 332 */       return;
/*     */     }
/* 334 */     log.debug("Harvest Configuration: " + configuration);
/* 335 */     instance.setConfiguration(configuration);
/*     */   }
/*     */ 
/*     */   public static HarvestConfiguration getHarvestConfiguration() {
/* 339 */     if (!isInitialized()) {
/* 340 */       return HarvestConfiguration.getDefaultHarvestConfiguration();
/*     */     }
/* 342 */     return instance.getConfiguration();
/*     */   }
/*     */ 
/*     */   public static boolean isDisabled() {
/* 346 */     if (!isInitialized())
/* 347 */       return false;
/* 348 */     return instance.getHarvester().isDisabled();
/*     */   }
/*     */ 
/*     */   protected ActivityTraceConfiguration getActivityTraceConfiguration() {
/* 352 */     return this.configuration.getAt_capture();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.Harvest
 * JD-Core Version:    0.6.2
 */