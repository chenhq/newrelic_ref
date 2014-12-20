/*     */ package com.newrelic.agent.android.api.common;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public final class ConnectionState
/*     */ {
/*     */   private final Object dataToken;
/*     */   private final String crossProcessId;
/*     */   private final long serverTimestamp;
/*     */   private final long harvestInterval;
/*     */   private final TimeUnit harvestIntervalTimeUnit;
/*     */   private final long maxTransactionAge;
/*     */   private final TimeUnit maxTransactionAgeTimeUnit;
/*     */   private final long maxTransactionCount;
/*     */   private final int stackTraceLimit;
/*     */   private final int responseBodyLimit;
/*     */   private final boolean collectingNetworkErrors;
/*     */   private final int errorLimit;
/*  22 */   public static final ConnectionState NULL = new ConnectionState();
/*     */ 
/*     */   private ConnectionState() {
/*  25 */     this.dataToken = null;
/*  26 */     this.crossProcessId = null;
/*  27 */     this.serverTimestamp = 0L;
/*  28 */     this.harvestInterval = 60L;
/*  29 */     this.harvestIntervalTimeUnit = TimeUnit.SECONDS;
/*  30 */     this.maxTransactionAge = 600L;
/*  31 */     this.maxTransactionAgeTimeUnit = TimeUnit.SECONDS;
/*  32 */     this.maxTransactionCount = 1000L;
/*  33 */     this.stackTraceLimit = 50;
/*  34 */     this.responseBodyLimit = 1024;
/*  35 */     this.collectingNetworkErrors = true;
/*  36 */     this.errorLimit = 10;
/*     */   }
/*     */ 
/*     */   public ConnectionState(Object dataToken, String crossProcessId, long serverTimestamp, long harvestInterval, TimeUnit harvestIntervalTimeUnit, long maxTransactionAge, TimeUnit maxTransactionAgeTimeUnit, long maxTransactionCount, int stackTraceLimit, int responseBodyLimit, boolean collectingNetworkerrors, int errorLimit)
/*     */   {
/*  53 */     this.dataToken = dataToken;
/*  54 */     this.crossProcessId = crossProcessId;
/*  55 */     this.serverTimestamp = serverTimestamp;
/*  56 */     this.harvestInterval = harvestInterval;
/*  57 */     this.harvestIntervalTimeUnit = harvestIntervalTimeUnit;
/*  58 */     this.maxTransactionAge = maxTransactionAge;
/*  59 */     this.maxTransactionAgeTimeUnit = maxTransactionAgeTimeUnit;
/*  60 */     this.maxTransactionCount = maxTransactionCount;
/*  61 */     this.stackTraceLimit = stackTraceLimit;
/*  62 */     this.responseBodyLimit = responseBodyLimit;
/*  63 */     this.collectingNetworkErrors = collectingNetworkerrors;
/*  64 */     this.errorLimit = errorLimit;
/*     */   }
/*     */ 
/*     */   public Object getDataToken() {
/*  68 */     return this.dataToken;
/*     */   }
/*     */ 
/*     */   public String getCrossProcessId() {
/*  72 */     return this.crossProcessId;
/*     */   }
/*     */ 
/*     */   public long getServerTimestamp() {
/*  76 */     return this.serverTimestamp;
/*     */   }
/*     */ 
/*     */   public long getHarvestIntervalInSeconds() {
/*  80 */     return TimeUnit.SECONDS.convert(this.harvestInterval, this.harvestIntervalTimeUnit);
/*     */   }
/*     */ 
/*     */   public long getHarvestIntervalInMilliseconds() {
/*  84 */     return TimeUnit.MILLISECONDS.convert(this.harvestInterval, this.harvestIntervalTimeUnit);
/*     */   }
/*     */ 
/*     */   public long getMaxTransactionAgeInSeconds() {
/*  88 */     return TimeUnit.SECONDS.convert(this.maxTransactionAge, this.maxTransactionAgeTimeUnit);
/*     */   }
/*     */ 
/*     */   public long getMaxTransactionAgeInMilliseconds() {
/*  92 */     return TimeUnit.MILLISECONDS.convert(this.maxTransactionAge, this.maxTransactionAgeTimeUnit);
/*     */   }
/*     */ 
/*     */   public long getMaxTransactionCount() {
/*  96 */     return this.maxTransactionCount;
/*     */   }
/*     */ 
/*     */   public int getStackTraceLimit() {
/* 100 */     return this.stackTraceLimit;
/*     */   }
/*     */ 
/*     */   public int getResponseBodyLimit() {
/* 104 */     return this.responseBodyLimit;
/*     */   }
/*     */ 
/*     */   public boolean isCollectingNetworkErrors() {
/* 108 */     return this.collectingNetworkErrors;
/*     */   }
/*     */ 
/*     */   public int getErrorLimit() {
/* 112 */     return this.errorLimit;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 116 */     StringBuilder sb = new StringBuilder();
/* 117 */     sb.append(this.dataToken);
/* 118 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.api.common.ConnectionState
 * JD-Core Version:    0.6.2
 */