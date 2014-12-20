/*     */ package com.newrelic.agent.android.crashes;
/*     */ 
/*     */ import com.newrelic.agent.android.AgentConfiguration;
/*     */ import com.newrelic.agent.android.FeatureFlag;
/*     */ import com.newrelic.agent.android.harvest.crash.Crash;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.stats.StatsEngine;
/*     */ import com.newrelic.agent.android.stats.TicToc;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ public class CrashReporter
/*     */ {
/*     */   private static final String CRASH_COLLECTOR_PATH = "/mobile_crash";
/*     */   private static final int CRASH_COLLECTOR_TIMEOUT = 5000;
/*  24 */   private static final CrashReporter instance = new CrashReporter();
/*     */   private static AgentConfiguration agentConfiguration;
/*     */   private final AgentLog log;
/*     */   private boolean isEnabled;
/*     */   private boolean reportCrashes;
/*     */   private Thread.UncaughtExceptionHandler previousExceptionHandler;
/*     */   private CrashStore crashStore;
/*  36 */   private static final AtomicBoolean initialized = new AtomicBoolean(false);
/*     */ 
/*     */   public CrashReporter()
/*     */   {
/*  28 */     this.log = AgentLogManager.getAgentLog();
/*     */ 
/*  31 */     this.isEnabled = false;
/*  32 */     this.reportCrashes = true;
/*     */   }
/*     */ 
/*     */   public static void initialize(AgentConfiguration _agentConfiguration)
/*     */   {
/*  41 */     if (!initialized.compareAndSet(false, true)) {
/*  42 */       return;
/*     */     }
/*  44 */     agentConfiguration = _agentConfiguration;
/*     */ 
/*  46 */     instance.isEnabled = agentConfiguration.getReportCrashes();
/*  47 */     instance.crashStore = agentConfiguration.getCrashStore();
/*     */ 
/*  49 */     instance.reportSavedCrashes();
/*     */ 
/*  51 */     if (instance.isEnabled)
/*  52 */       instance.installCrashHandler();
/*     */   }
/*     */ 
/*     */   public static AgentConfiguration getAgentConfiguration()
/*     */   {
/*  57 */     return agentConfiguration;
/*     */   }
/*     */ 
/*     */   public UncaughtExceptionHandler getHandler() {
/*  61 */     return new UncaughtExceptionHandler(null);
/*     */   }
/*     */ 
/*     */   public static UncaughtExceptionHandler getInstanceHandler() {
/*  65 */     return instance.getHandler();
/*     */   }
/*     */ 
/*     */   public static void setReportCrashes(boolean reportCrashes) {
/*  69 */     instance.reportCrashes = reportCrashes;
/*     */   }
/*     */ 
/*     */   public static int storedCrashes()
/*     */   {
/*  74 */     return instance.crashStore.count();
/*     */   }
/*     */ 
/*     */   public static List<Crash> fetchAllCrashes() {
/*  78 */     return instance.crashStore.fetchAll();
/*     */   }
/*     */ 
/*     */   public static void clear() {
/*  82 */     instance.crashStore.clear();
/*     */   }
/*     */ 
/*     */   private void installCrashHandler()
/*     */   {
/*  87 */     Thread.UncaughtExceptionHandler currentExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
/*     */ 
/*  89 */     if (currentExceptionHandler != null) {
/*  90 */       if ((currentExceptionHandler instanceof UncaughtExceptionHandler)) {
/*  91 */         this.log.debug("New Relic crash handler already installed.");
/*  92 */         return;
/*     */       }
/*     */ 
/*  95 */       this.previousExceptionHandler = currentExceptionHandler;
/*     */ 
/*  97 */       this.log.debug("Installing New Relic crash handler and chaining " + this.previousExceptionHandler.getClass().getName() + ".");
/*     */     } else {
/*  99 */       this.log.debug("Installing New Relic crash handler.");
/*     */     }
/*     */ 
/* 102 */     Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(null));
/*     */   }
/*     */ 
/*     */   public static void resetForTesting()
/*     */   {
/* 107 */     initialized.compareAndSet(true, false);
/*     */   }
/*     */ 
/*     */   private void reportSavedCrashes() {
/* 111 */     for (Crash crash : this.crashStore.fetchAll())
/* 112 */       reportCrash(crash, false);
/*     */   }
/*     */ 
/*     */   private void reportCrash(Crash crash, boolean block)
/*     */   {
/* 118 */     this.crashStore.store(crash);
/*     */ 
/* 120 */     if (this.reportCrashes) {
/* 121 */       CrashSender sender = new CrashSender(crash);
/* 122 */       Thread senderThread = new Thread(sender);
/* 123 */       senderThread.start();
/*     */ 
/* 125 */       if (block)
/*     */         try {
/* 127 */           senderThread.join();
/*     */         } catch (InterruptedException e) {
/* 129 */           this.log.error("Exception caught while waiting to send crash", e);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CrashSender
/*     */     implements Runnable
/*     */   {
/*     */     private final Crash crash;
/*     */ 
/*     */     CrashSender(Crash crash)
/*     */     {
/* 188 */       this.crash = crash;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try {
/* 194 */         String protocol = CrashReporter.agentConfiguration.useSsl() ? "https://" : "http://";
/* 195 */         String urlString = protocol + CrashReporter.agentConfiguration.getCrashCollectorHost() + "/mobile_crash";
/* 196 */         URL url = new URL(urlString);
/* 197 */         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
/*     */ 
/* 199 */         connection.setDoOutput(true);
/* 200 */         connection.setChunkedStreamingMode(0);
/* 201 */         connection.setRequestProperty("Content-Type", "application/json");
/* 202 */         connection.setConnectTimeout(5000);
/*     */         try
/*     */         {
/* 205 */           OutputStream out = new BufferedOutputStream(connection.getOutputStream());
/* 206 */           out.write(this.crash.toJsonString().getBytes());
/* 207 */           out.close();
/*     */ 
/* 209 */           if (connection.getResponseCode() == 200) {
/* 210 */             CrashReporter.this.log.info("Crash " + this.crash.getUuid().toString() + " successfully submitted.");
/* 211 */             CrashReporter.this.crashStore.delete(this.crash);
/*     */           } else {
/* 213 */             CrashReporter.this.log.error("Something went wrong while submitting a crash (will try again later) - Response code " + connection.getResponseCode());
/*     */           }
/*     */         } finally {
/* 216 */           connection.disconnect();
/*     */         }
/*     */       } catch (Exception e) {
/* 219 */         CrashReporter.this.log.error("Unable to report crash to New Relic, will try again later.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class UncaughtExceptionHandler
/*     */     implements Thread.UncaughtExceptionHandler
/*     */   {
/* 136 */     private final AtomicBoolean handledException = new AtomicBoolean(false);
/*     */ 
/*     */     private UncaughtExceptionHandler() {
/*     */     }
/*     */ 
/*     */     public void uncaughtException(Thread thread, Throwable throwable) {
/* 142 */       if (!this.handledException.compareAndSet(false, true)) {
/* 143 */         StatsEngine.get().inc("Supportability/AgentHealth/Recursion/UncaughtExceptionHandler");
/* 144 */         return;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 149 */         if ((!CrashReporter.instance.isEnabled) || (!FeatureFlag.featureEnabled(FeatureFlag.CrashReporting))) {
/* 150 */           CrashReporter.this.log.debug("A crash has been detected but crash reporting is disabled!");
/* 151 */           chainExceptionHandler(thread, throwable);
/* 152 */           return;
/*     */         }
/*     */ 
/* 155 */         TicToc timer = new TicToc();
/* 156 */         timer.tic();
/*     */ 
/* 158 */         CrashReporter.this.log.debug("A crash has been detected in " + thread.getStackTrace()[0].getClassName() + " and will be reported ASAP.");
/*     */ 
/* 160 */         Crash crash = new Crash(throwable);
/*     */ 
/* 162 */         CrashReporter.this.reportCrash(crash, true);
/*     */ 
/* 165 */         CrashReporter.this.log.debug("Crash collection took " + timer.toc() + "ms");
/*     */ 
/* 167 */         chainExceptionHandler(thread, throwable);
/*     */       }
/*     */       catch (Throwable t) {
/* 170 */         CrashReporter.this.log.error("Error encountered while preparing crash for New Relic!", t);
/* 171 */         chainExceptionHandler(thread, throwable);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void chainExceptionHandler(Thread thread, Throwable throwable)
/*     */     {
/* 177 */       if (CrashReporter.this.previousExceptionHandler != null) {
/* 178 */         CrashReporter.this.log.debug("Chaining crash reporting duties to " + CrashReporter.this.previousExceptionHandler.getClass().getSimpleName());
/* 179 */         CrashReporter.this.previousExceptionHandler.uncaughtException(thread, throwable);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.crashes.CrashReporter
 * JD-Core Version:    0.6.2
 */