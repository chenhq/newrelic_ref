/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.content.SharedPreferences;
/*     */ import android.content.SharedPreferences.Editor;
/*     */ import com.newrelic.agent.android.harvest.DataToken;
/*     */ import com.newrelic.agent.android.harvest.DeviceInformation;
/*     */ import com.newrelic.agent.android.harvest.Harvest;
/*     */ import com.newrelic.agent.android.harvest.HarvestAdapter;
/*     */ import com.newrelic.agent.android.harvest.HarvestConfiguration;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONException;
/*     */ import org.json.JSONTokener;
/*     */ 
/*     */ public class SavedState extends HarvestAdapter
/*     */ {
/*  18 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*  20 */   private final String PREFERENCE_FILE_PREFIX = "com.newrelic.android.agent.v1_";
/*     */ 
/*  22 */   private final String PREF_MAX_TRANSACTION_COUNT = "maxTransactionCount";
/*  23 */   private final String PREF_MAX_TRANSACTION_AGE = "maxTransactionAgeInSeconds";
/*  24 */   private final String PREF_HARVEST_INTERVAL = "harvestIntervalInSeconds";
/*  25 */   private final String PREF_SERVER_TIMESTAMP = "serverTimestamp";
/*  26 */   private final String PREF_CROSS_PROCESS_ID = "crossProcessId";
/*  27 */   private final String PREF_AGENT_VERSION = "agentVersion";
/*  28 */   private final String PREF_DATA_TOKEN = "dataToken";
/*  29 */   private final String PREF_APP_TOKEN = "appToken";
/*  30 */   private final String PREF_ANDROID_ID_BUG_WORK_AROUND = "androidIdBugWorkAround";
/*  31 */   private final String PREF_STACK_TRACE_LIMIT = "stackTraceLimit";
/*  32 */   private final String PREF_RESPONSE_BODY_LIMIT = "responseBodyLimit";
/*  33 */   private final String PREF_COLLECT_NETWORK_ERRORS = "collectNetworkErrors";
/*  34 */   private final String PREF_ERROR_LIMIT = "errorLimit";
/*  35 */   private final String NEW_RELIC_AGENT_DISABLED_VERSION_KEY = "NewRelicAgentDisabledVersion";
/*  36 */   private final String PREF_ACTIVITY_TRACE_MIN_UTILIZATION = "activityTraceMinUtilization";
/*  37 */   private final HarvestConfiguration configuration = new HarvestConfiguration();
/*     */   private Float activityTraceMinUtilization;
/*     */   private final SharedPreferences prefs;
/*     */   private final SharedPreferences.Editor editor;
/*  43 */   private final Lock lock = new ReentrantLock();
/*     */ 
/*     */   public SavedState(Context context) {
/*  46 */     this.prefs = context.getSharedPreferences(getPreferenceFileName(context.getPackageName()), 0);
/*  47 */     this.editor = this.prefs.edit();
/*  48 */     loadHarvestConfiguration();
/*     */   }
/*     */ 
/*     */   public void saveHarvestConfiguration(HarvestConfiguration newConfiguration)
/*     */   {
/*  53 */     if (this.configuration.equals(newConfiguration)) {
/*  54 */       return;
/*     */     }
/*  56 */     if (!newConfiguration.getDataToken().isValid()) {
/*  57 */       newConfiguration.setData_token(this.configuration.getData_token());
/*     */     }
/*  59 */     log.info("Saving configuration: " + newConfiguration);
/*  60 */     saveDataToken(newConfiguration.getDataToken().toJsonString());
/*  61 */     saveCrossProcessId(newConfiguration.getCross_process_id());
/*  62 */     saveServerTimestamp(newConfiguration.getServer_timestamp());
/*  63 */     saveHarvestInterval(newConfiguration.getData_report_period());
/*  64 */     saveMaxTransactionAge(newConfiguration.getReport_max_transaction_age());
/*  65 */     saveMaxTransactionCount(newConfiguration.getReport_max_transaction_count());
/*  66 */     saveStackTraceLimit(newConfiguration.getStack_trace_limit());
/*  67 */     saveResponseBodyLimit(newConfiguration.getResponse_body_limit());
/*  68 */     saveCollectNetworkErrors(newConfiguration.isCollect_network_errors());
/*  69 */     saveErrorLimit(newConfiguration.getError_limit());
/*  70 */     saveActivityTraceMinUtilization((float)newConfiguration.getActivity_trace_min_utilization());
/*     */ 
/*  73 */     loadHarvestConfiguration();
/*     */   }
/*     */ 
/*     */   public void loadHarvestConfiguration() {
/*  77 */     if (has("dataToken")) {
/*  78 */       this.configuration.setData_token(getDataToken());
/*     */     }
/*  80 */     if (has("crossProcessId")) {
/*  81 */       this.configuration.setCross_process_id(getCrossProcessId());
/*     */     }
/*  83 */     if (has("serverTimestamp")) {
/*  84 */       this.configuration.setServer_timestamp(getServerTimestamp());
/*     */     }
/*  86 */     if (has("harvestIntervalInSeconds")) {
/*  87 */       this.configuration.setData_report_period((int)getHarvestIntervalInSeconds());
/*     */     }
/*  89 */     if (has("maxTransactionAgeInSeconds")) {
/*  90 */       this.configuration.setReport_max_transaction_age((int)getMaxTransactionAgeInSeconds());
/*     */     }
/*  92 */     if (has("maxTransactionCount")) {
/*  93 */       this.configuration.setReport_max_transaction_count((int)getMaxTransactionCount());
/*     */     }
/*  95 */     if (has("stackTraceLimit")) {
/*  96 */       this.configuration.setStack_trace_limit(getStackTraceLimit());
/*     */     }
/*  98 */     if (has("responseBodyLimit")) {
/*  99 */       this.configuration.setResponse_body_limit(getResponseBodyLimit());
/*     */     }
/* 101 */     if (has("collectNetworkErrors")) {
/* 102 */       this.configuration.setCollect_network_errors(isCollectingNetworkErrors());
/*     */     }
/* 104 */     if (has("errorLimit")) {
/* 105 */       this.configuration.setError_limit(getErrorLimit());
/*     */     }
/* 107 */     if (has("activityTraceMinUtilization")) {
/* 108 */       this.configuration.setActivity_trace_min_utilization(getActivityTraceMinUtilization());
/*     */     }
/* 110 */     log.info("Loaded configuration: " + this.configuration);
/*     */   }
/*     */ 
/*     */   public HarvestConfiguration getHarvestConfiguration() {
/* 114 */     return this.configuration;
/*     */   }
/*     */ 
/*     */   private boolean has(String key) {
/* 118 */     return this.prefs.contains(key);
/*     */   }
/*     */ 
/*     */   public void onHarvestConnected()
/*     */   {
/* 123 */     saveHarvestConfiguration(Harvest.getHarvestConfiguration());
/*     */   }
/*     */ 
/*     */   public void onHarvestComplete()
/*     */   {
/* 128 */     saveHarvestConfiguration(Harvest.getHarvestConfiguration());
/*     */   }
/*     */ 
/*     */   public void onHarvestDisconnected()
/*     */   {
/* 133 */     log.info("Clearing harvest configuration.");
/* 134 */     clear();
/*     */   }
/*     */ 
/*     */   public void onHarvestDisabled()
/*     */   {
/* 139 */     String agentVersion = Agent.getDeviceInformation().getAgentVersion();
/* 140 */     log.info("Disabling agent version " + agentVersion);
/* 141 */     saveDisabledVersion(agentVersion);
/*     */   }
/*     */ 
/*     */   public void save(String key, String value) {
/* 145 */     this.lock.lock();
/*     */     try {
/* 147 */       this.editor.putString(key, value);
/* 148 */       this.editor.commit();
/*     */     } finally {
/* 150 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save(String key, boolean value) {
/* 155 */     this.lock.lock();
/*     */     try {
/* 157 */       this.editor.putBoolean(key, value);
/* 158 */       this.editor.commit();
/*     */     } finally {
/* 160 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save(String key, int value) {
/* 165 */     this.lock.lock();
/*     */     try {
/* 167 */       this.editor.putInt(key, value);
/* 168 */       this.editor.commit();
/*     */     } finally {
/* 170 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save(String key, long value) {
/* 175 */     this.lock.lock();
/*     */     try {
/* 177 */       this.editor.putLong(key, value);
/* 178 */       this.editor.commit();
/*     */     } finally {
/* 180 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save(String key, float value) {
/* 185 */     this.lock.lock();
/*     */     try {
/* 187 */       this.editor.putFloat(key, value);
/* 188 */       this.editor.commit();
/*     */     } finally {
/* 190 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getString(String key) {
/* 195 */     if (!this.prefs.contains(key)) {
/* 196 */       return null;
/*     */     }
/* 198 */     return this.prefs.getString(key, null);
/*     */   }
/*     */ 
/*     */   public boolean getBoolean(String key) {
/* 202 */     return this.prefs.getBoolean(key, false);
/*     */   }
/*     */ 
/*     */   public long getLong(String key) {
/* 206 */     return this.prefs.getLong(key, 0L);
/*     */   }
/*     */ 
/*     */   public int getInt(String key) {
/* 210 */     return this.prefs.getInt(key, 0);
/*     */   }
/*     */ 
/*     */   public Float getFloat(String key) {
/* 214 */     if (!this.prefs.contains(key)) {
/* 215 */       return null;
/*     */     }
/* 217 */     float f = this.prefs.getFloat(key, 0.0F);
/*     */ 
/* 220 */     return Float.valueOf((int)(f * 100.0F) / 100.0F);
/*     */   }
/*     */ 
/*     */   public String getDisabledVersion() {
/* 224 */     return getString("NewRelicAgentDisabledVersion");
/*     */   }
/*     */ 
/*     */   public void saveDisabledVersion(String version) {
/* 228 */     save("NewRelicAgentDisabledVersion", version);
/*     */   }
/*     */ 
/*     */   public int[] getDataToken() {
/* 232 */     int[] dataToken = new int[2];
/* 233 */     String dataTokenString = getString("dataToken");
/*     */ 
/* 235 */     if (dataTokenString == null)
/* 236 */       return null;
/*     */     try
/*     */     {
/* 239 */       JSONTokener tokener = new JSONTokener(dataTokenString);
/* 240 */       if (tokener == null) {
/* 241 */         return null;
/*     */       }
/* 243 */       JSONArray array = (JSONArray)tokener.nextValue();
/*     */ 
/* 245 */       if (array == null) {
/* 246 */         return null;
/*     */       }
/* 248 */       dataToken[0] = array.getInt(0);
/* 249 */       dataToken[1] = array.getInt(1);
/*     */     } catch (JSONException e) {
/* 251 */       e.printStackTrace();
/*     */     }
/* 253 */     return dataToken;
/*     */   }
/*     */ 
/*     */   public void saveDataToken(String dataToken) {
/* 257 */     log.debug("!! saving data token: " + dataToken);
/* 258 */     save("dataToken", dataToken);
/*     */   }
/*     */ 
/*     */   public String getAgentVersion() {
/* 262 */     return getString("agentVersion");
/*     */   }
/*     */ 
/*     */   public void saveAgentVersion(String agentVersion) {
/* 266 */     save("agentVersion", agentVersion);
/*     */   }
/*     */ 
/*     */   public String getCrossProcessId() {
/* 270 */     return getString("crossProcessId");
/*     */   }
/*     */ 
/*     */   public void saveCrossProcessId(String crossProcessId) {
/* 274 */     save("crossProcessId", crossProcessId);
/*     */   }
/*     */ 
/*     */   public String getAppToken() {
/* 278 */     return getString("appToken");
/*     */   }
/*     */ 
/*     */   public void saveAppToken(String appToken) {
/* 282 */     save("appToken", appToken);
/*     */   }
/*     */ 
/*     */   public String getAndroidIdBugWorkAround() {
/* 286 */     return getString("androidIdBugWorkAround");
/*     */   }
/*     */ 
/*     */   public void saveAndroidIdBugWorkAround(String androidIdBugWorkAround) {
/* 290 */     save("androidIdBugWorkAround", androidIdBugWorkAround);
/*     */   }
/*     */ 
/*     */   public boolean isCollectingNetworkErrors() {
/* 294 */     return getBoolean("collectNetworkErrors");
/*     */   }
/*     */ 
/*     */   public void saveCollectNetworkErrors(boolean collectNetworkErrors) {
/* 298 */     save("collectNetworkErrors", collectNetworkErrors);
/*     */   }
/*     */ 
/*     */   public long getServerTimestamp() {
/* 302 */     return getLong("serverTimestamp");
/*     */   }
/*     */ 
/*     */   public void saveServerTimestamp(long serverTimestamp) {
/* 306 */     save("serverTimestamp", serverTimestamp);
/*     */   }
/*     */ 
/*     */   public long getHarvestInterval() {
/* 310 */     return getLong("harvestIntervalInSeconds");
/*     */   }
/*     */ 
/*     */   public void saveHarvestInterval(long harvestInterval) {
/* 314 */     save("harvestIntervalInSeconds", harvestInterval);
/*     */   }
/*     */ 
/*     */   public long getMaxTransactionAge() {
/* 318 */     return getLong("maxTransactionAgeInSeconds");
/*     */   }
/*     */ 
/*     */   public void saveMaxTransactionAge(long maxTransactionAge) {
/* 322 */     save("maxTransactionAgeInSeconds", maxTransactionAge);
/*     */   }
/*     */ 
/*     */   public long getMaxTransactionCount() {
/* 326 */     return getLong("maxTransactionCount");
/*     */   }
/*     */ 
/*     */   public void saveMaxTransactionCount(long maxTransactionCount) {
/* 330 */     save("maxTransactionCount", maxTransactionCount);
/*     */   }
/*     */ 
/*     */   public int getStackTraceLimit() {
/* 334 */     return getInt("stackTraceLimit");
/*     */   }
/*     */ 
/*     */   public void saveStackTraceLimit(int stackTraceLimit) {
/* 338 */     save("stackTraceLimit", stackTraceLimit);
/*     */   }
/*     */ 
/*     */   public int getResponseBodyLimit() {
/* 342 */     return getInt("responseBodyLimit");
/*     */   }
/*     */ 
/*     */   public void saveResponseBodyLimit(int responseBodyLimit) {
/* 346 */     save("responseBodyLimit", responseBodyLimit);
/*     */   }
/*     */ 
/*     */   public int getErrorLimit() {
/* 350 */     return getInt("errorLimit");
/*     */   }
/*     */ 
/*     */   public void saveErrorLimit(int errorLimit) {
/* 354 */     save("errorLimit", errorLimit);
/*     */   }
/*     */ 
/*     */   public void saveActivityTraceMinUtilization(float activityTraceMinUtilization) {
/* 358 */     this.activityTraceMinUtilization = Float.valueOf(activityTraceMinUtilization);
/* 359 */     save("activityTraceMinUtilization", activityTraceMinUtilization);
/*     */   }
/*     */ 
/*     */   public float getActivityTraceMinUtilization() {
/* 363 */     if (this.activityTraceMinUtilization == null)
/* 364 */       this.activityTraceMinUtilization = getFloat("activityTraceMinUtilization");
/* 365 */     return this.activityTraceMinUtilization.floatValue();
/*     */   }
/*     */ 
/*     */   public long getHarvestIntervalInSeconds() {
/* 369 */     return getHarvestInterval();
/*     */   }
/*     */ 
/*     */   public long getMaxTransactionAgeInSeconds()
/*     */   {
/* 374 */     return getMaxTransactionAge();
/*     */   }
/*     */ 
/*     */   private String getPreferenceFileName(String packageName) {
/* 378 */     return "com.newrelic.android.agent.v1_" + packageName;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 382 */     this.lock.lock();
/*     */     try {
/* 384 */       this.editor.clear();
/* 385 */       this.editor.commit();
/* 386 */       this.configuration.setDefaultValues();
/*     */     } finally {
/* 388 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.SavedState
 * JD-Core Version:    0.6.2
 */