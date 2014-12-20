/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.harvest.ApplicationInformation;
/*     */ import com.newrelic.agent.android.harvest.DeviceInformation;
/*     */ import com.newrelic.agent.android.harvest.EnvironmentInformation;
/*     */ import com.newrelic.agent.android.util.Encoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ public class NullAgentImpl
/*     */   implements AgentImpl
/*     */ {
/*  16 */   public static final NullAgentImpl instance = new NullAgentImpl();
/*  17 */   private final ReentrantLock lock = new ReentrantLock();
/*     */   private int responseBodyLimit;
/*  19 */   private final AgentConfiguration agentConfiguration = new AgentConfiguration();
/*     */ 
/*     */   public void addTransactionData(TransactionData transactionData)
/*     */   {
/*     */   }
/*     */ 
/*     */   public List<TransactionData> getAndClearTransactionData()
/*     */   {
/*  30 */     return new ArrayList();
/*     */   }
/*     */ 
/*     */   public void mergeTransactionData(List<TransactionData> transactionDataList)
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getCrossProcessId()
/*     */   {
/*  39 */     return null;
/*     */   }
/*     */ 
/*     */   public int getStackTraceLimit()
/*     */   {
/*  44 */     return 0;
/*     */   }
/*     */ 
/*     */   public int getResponseBodyLimit()
/*     */   {
/*  49 */     return this.responseBodyLimit;
/*     */   }
/*     */   public void setResponseBodyLimit(int responseBodyLimit) {
/*  52 */     this.responseBodyLimit = responseBodyLimit;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void disable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isDisabled() {
/*  68 */     return true;
/*     */   }
/*     */ 
/*     */   public String getNetworkCarrier()
/*     */   {
/*  73 */     return "unknown";
/*     */   }
/*     */ 
/*     */   public String getNetworkWanType()
/*     */   {
/*  78 */     return "unknown";
/*     */   }
/*     */ 
/*     */   public void setLocation(String countryCode, String adminRegion)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Encoder getEncoder()
/*     */   {
/*  87 */     return new Encoder() {
/*     */       public String encode(byte[] bytes) {
/*  89 */         return new String(bytes);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public DeviceInformation getDeviceInformation()
/*     */   {
/*  96 */     DeviceInformation devInfo = new DeviceInformation();
/*  97 */     devInfo.setOsName("Android");
/*  98 */     devInfo.setOsVersion("2.3");
/*  99 */     devInfo.setOsBuild("a.b.c");
/* 100 */     devInfo.setManufacturer("Fake");
/* 101 */     devInfo.setModel("NullAgent");
/* 102 */     devInfo.setAgentName("AndroidAgent");
/* 103 */     devInfo.setAgentVersion("2.123");
/* 104 */     devInfo.setDeviceId("389C9738-A761-44DE-8A66-1668CFD67DA1");
/* 105 */     devInfo.setArchitecture("Fake Arch");
/* 106 */     devInfo.setRunTime("1.7.0");
/* 107 */     devInfo.setSize("Fake Size");
/*     */ 
/* 109 */     return devInfo;
/*     */   }
/*     */ 
/*     */   public ApplicationInformation getApplicationInformation()
/*     */   {
/* 114 */     return new ApplicationInformation("null", "0.0", "null");
/*     */   }
/*     */ 
/*     */   public EnvironmentInformation getEnvironmentInformation()
/*     */   {
/* 119 */     return new EnvironmentInformation(0L, 1, "none", "none", new long[] { 0L, 0L });
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.NullAgentImpl
 * JD-Core Version:    0.6.2
 */