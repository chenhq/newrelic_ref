/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.harvest.ApplicationInformation;
/*     */ import com.newrelic.agent.android.harvest.DeviceInformation;
/*     */ import com.newrelic.agent.android.util.Encoder;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Agent
/*     */ {
/*     */   public static final String VERSION = "4.120.0";
/*  13 */   private static final AgentImpl NULL_AGENT_IMPL = new NullAgentImpl();
/*     */ 
/*  15 */   private static Object implLock = new Object();
/*  16 */   private static AgentImpl impl = NULL_AGENT_IMPL;
/*     */ 
/*     */   public static void setImpl(AgentImpl impl) {
/*  19 */     synchronized (implLock) {
/*  20 */       if (impl == null) {
/*  21 */         impl = NULL_AGENT_IMPL;
/*     */       }
/*     */       else
/*  24 */         impl = impl;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static AgentImpl getImpl()
/*     */   {
/*  30 */     synchronized (implLock) {
/*  31 */       return impl;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getVersion() {
/*  36 */     return "4.120.0";
/*     */   }
/*     */ 
/*     */   public static String getCrossProcessId() {
/*  40 */     return getImpl().getCrossProcessId();
/*     */   }
/*     */ 
/*     */   public static int getStackTraceLimit() {
/*  44 */     return getImpl().getStackTraceLimit();
/*     */   }
/*     */ 
/*     */   public static int getResponseBodyLimit() {
/*  48 */     return getImpl().getResponseBodyLimit();
/*     */   }
/*     */ 
/*     */   public static void addTransactionData(TransactionData transactionData)
/*     */   {
/*  57 */     getImpl().addTransactionData(transactionData);
/*     */   }
/*     */ 
/*     */   public static List<TransactionData> getAndClearTransactionData()
/*     */   {
/*  66 */     return getImpl().getAndClearTransactionData();
/*     */   }
/*     */ 
/*     */   public static void mergeTransactionData(List<TransactionData> transactionDataList)
/*     */   {
/*  75 */     getImpl().mergeTransactionData(transactionDataList);
/*     */   }
/*     */ 
/*     */   public static String getActiveNetworkCarrier()
/*     */   {
/*  84 */     return getImpl().getNetworkCarrier();
/*     */   }
/*     */ 
/*     */   public static String getActiveNetworkWanType()
/*     */   {
/*  92 */     return getImpl().getNetworkWanType();
/*     */   }
/*     */ 
/*     */   public static void disable()
/*     */   {
/*  99 */     getImpl().disable();
/*     */   }
/*     */ 
/*     */   public static boolean isDisabled()
/*     */   {
/* 108 */     return getImpl().isDisabled();
/*     */   }
/*     */ 
/*     */   public static void start()
/*     */   {
/* 115 */     getImpl().start();
/*     */   }
/*     */ 
/*     */   public static void stop()
/*     */   {
/* 122 */     getImpl().stop();
/*     */   }
/*     */ 
/*     */   public static void setLocation(String countryCode, String adminRegion)
/*     */   {
/* 132 */     getImpl().setLocation(countryCode, adminRegion);
/*     */   }
/*     */ 
/*     */   public static Encoder getEncoder()
/*     */   {
/* 139 */     return getImpl().getEncoder();
/*     */   }
/*     */ 
/*     */   public static DeviceInformation getDeviceInformation() {
/* 143 */     return getImpl().getDeviceInformation();
/*     */   }
/*     */ 
/*     */   public static ApplicationInformation getApplicationInformation() {
/* 147 */     return getImpl().getApplicationInformation();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.Agent
 * JD-Core Version:    0.6.2
 */