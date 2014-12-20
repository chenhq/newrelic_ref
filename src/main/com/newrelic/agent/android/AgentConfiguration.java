/*    */ package com.newrelic.agent.android;
/*    */ 
/*    */ import com.newrelic.agent.android.crashes.CrashStore;
/*    */ 
/*    */ public class AgentConfiguration
/*    */ {
/*    */   private static final String DEFAULT_COLLECTOR_HOST = "mobile-collector.newrelic.com";
/*    */   private static final String DEFAULT_CRASH_COLLECTOR_HOST = "mobile-crash.newrelic.com";
/* 12 */   private String collectorHost = "mobile-collector.newrelic.com";
/* 13 */   private String crashCollectorHost = "mobile-crash.newrelic.com";
/*    */   private String applicationToken;
/*    */   private String appName;
/* 16 */   private boolean useSsl = true;
/*    */   private boolean useLocationService;
/* 18 */   private boolean reportCrashes = true;
/*    */   private CrashStore crashStore;
/*    */ 
/*    */   public String getApplicationToken()
/*    */   {
/* 23 */     return this.applicationToken;
/*    */   }
/*    */ 
/*    */   public void setApplicationToken(String applicationToken) {
/* 27 */     this.applicationToken = applicationToken;
/*    */   }
/*    */ 
/*    */   public String getAppName() {
/* 31 */     return this.appName;
/*    */   }
/*    */ 
/*    */   public void setAppName(String appName) {
/* 35 */     this.appName = appName;
/*    */   }
/*    */   public String getCollectorHost() {
/* 38 */     return this.collectorHost;
/*    */   }
/*    */ 
/*    */   public void setCollectorHost(String collectorHost) {
/* 42 */     this.collectorHost = collectorHost;
/*    */   }
/*    */ 
/*    */   public String getCrashCollectorHost() {
/* 46 */     return this.crashCollectorHost;
/*    */   }
/*    */ 
/*    */   public void setCrashCollectorHost(String crashCollectorHost) {
/* 50 */     this.crashCollectorHost = crashCollectorHost;
/*    */   }
/*    */ 
/*    */   public boolean useSsl() {
/* 54 */     return this.useSsl;
/*    */   }
/*    */ 
/*    */   public void setUseSsl(boolean useSsl) {
/* 58 */     this.useSsl = useSsl;
/*    */   }
/*    */ 
/*    */   public boolean useLocationService() {
/* 62 */     return this.useLocationService;
/*    */   }
/*    */ 
/*    */   public void setUseLocationService(boolean useLocationService) {
/* 66 */     this.useLocationService = useLocationService;
/*    */   }
/*    */ 
/*    */   public boolean getReportCrashes() {
/* 70 */     return this.reportCrashes;
/*    */   }
/*    */ 
/*    */   public void setReportCrashes(boolean reportCrashes) {
/* 74 */     this.reportCrashes = reportCrashes;
/*    */   }
/*    */ 
/*    */   public CrashStore getCrashStore() {
/* 78 */     return this.crashStore;
/*    */   }
/*    */ 
/*    */   public void setCrashStore(CrashStore crashStore) {
/* 82 */     this.crashStore = crashStore;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.AgentConfiguration
 * JD-Core Version:    0.6.2
 */