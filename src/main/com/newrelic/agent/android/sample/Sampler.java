/*     */ package com.newrelic.agent.android.sample;
/*     */ 
/*     */ import android.app.ActivityManager;
/*     */ import android.content.Context;
/*     */ import android.os.Debug.MemoryInfo;
/*     */ import android.os.Process;
/*     */ import com.newrelic.agent.android.harvest.AgentHealth;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*     */ import com.newrelic.agent.android.tracing.Sample;
/*     */ import com.newrelic.agent.android.tracing.Sample.SampleType;
/*     */ import com.newrelic.agent.android.tracing.TraceLifecycleAware;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import com.newrelic.agent.android.util.NamedThreadFactory;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ public class Sampler
/*     */   implements TraceLifecycleAware, Runnable
/*     */ {
/*     */   private static final long SAMPLE_FREQ_MS = 100L;
/*  23 */   private static final int[] PID = { Process.myPid() };
/*     */   private static final int KB_IN_MB = 1024;
/*  25 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*  26 */   private static final ReentrantLock samplerLock = new ReentrantLock();
/*     */   private static Sampler sampler;
/*  29 */   private static boolean cpuSamplingDisabled = false;
/*     */   private final ActivityManager activityManager;
/*  32 */   private final EnumMap<Sample.SampleType, Collection<Sample>> samples = new EnumMap(Sample.SampleType.class);
/*  33 */   private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Sampler"));
/*  34 */   private final AtomicBoolean isRunning = new AtomicBoolean(false);
/*     */   private ScheduledFuture sampleFuture;
/*     */   private Long lastCpuTime;
/*     */   private Long lastAppCpuTime;
/*     */   private RandomAccessFile procStatFile;
/*     */   private RandomAccessFile appStatFile;
/*     */ 
/*     */   private Sampler(Context context)
/*     */   {
/*  44 */     this.activityManager = ((ActivityManager)context.getSystemService("activity"));
/*     */ 
/*  46 */     this.samples.put(Sample.SampleType.MEMORY, new ArrayList());
/*  47 */     this.samples.put(Sample.SampleType.CPU, new ArrayList());
/*     */   }
/*     */ 
/*     */   public static void init(Context context) {
/*  51 */     samplerLock.lock();
/*  52 */     sampler = new Sampler(context);
/*  53 */     samplerLock.unlock();
/*     */ 
/*  55 */     TraceMachine.addTraceListener(sampler);
/*     */ 
/*  57 */     log.debug("Sampler Initialized");
/*     */   }
/*     */ 
/*     */   public static void start() {
/*  61 */     samplerLock.lock();
/*     */ 
/*  63 */     if (sampler == null) {
/*  64 */       samplerLock.unlock();
/*  65 */       return;
/*     */     }
/*     */ 
/*  68 */     sampler.schedule();
/*     */ 
/*  70 */     samplerLock.unlock();
/*     */ 
/*  72 */     log.debug("Sampler started");
/*     */   }
/*     */ 
/*     */   public static void stop() {
/*  76 */     samplerLock.lock();
/*     */ 
/*  78 */     if (sampler == null) {
/*  79 */       samplerLock.unlock();
/*  80 */       return;
/*     */     }
/*     */ 
/*  83 */     sampler.stop(false);
/*     */ 
/*  85 */     samplerLock.unlock();
/*     */   }
/*     */ 
/*     */   public static void stopNow() {
/*  89 */     samplerLock.lock();
/*     */ 
/*  91 */     if (sampler == null) {
/*  92 */       samplerLock.unlock();
/*  93 */       return;
/*     */     }
/*     */ 
/*  96 */     sampler.stop(true);
/*     */ 
/*  98 */     samplerLock.unlock();
/*     */   }
/*     */ 
/*     */   public static void shutdown() {
/* 102 */     samplerLock.lock();
/*     */ 
/* 104 */     if (sampler == null) {
/* 105 */       samplerLock.unlock();
/* 106 */       return;
/*     */     }
/*     */ 
/* 109 */     TraceMachine.removeTraceListener(sampler);
/*     */ 
/* 111 */     stop();
/*     */ 
/* 113 */     sampler = null;
/*     */ 
/* 115 */     samplerLock.unlock();
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/* 120 */       if (this.isRunning.get())
/* 121 */         sample();
/*     */     }
/*     */     catch (Exception e) {
/* 124 */       log.error("Caught exception while running the sampler", e);
/* 125 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void schedule() {
/* 130 */     if (this.isRunning.get()) {
/* 131 */       return;
/*     */     }
/* 133 */     clear();
/* 134 */     this.isRunning.set(true);
/* 135 */     this.sampleFuture = this.scheduler.scheduleAtFixedRate(this, 0L, 100L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   private void stop(boolean immediate)
/*     */   {
/* 140 */     samplerLock.lock();
/*     */ 
/* 142 */     if (!this.isRunning.get()) {
/* 143 */       samplerLock.unlock();
/* 144 */       return;
/*     */     }
/*     */ 
/* 147 */     this.isRunning.set(false);
/* 148 */     this.sampleFuture.cancel(immediate);
/*     */ 
/* 150 */     resetCpuSampler();
/* 151 */     samplerLock.unlock();
/*     */ 
/* 153 */     log.debug("Sampler stopped");
/*     */   }
/*     */ 
/*     */   public static boolean isRunning() {
/* 157 */     if (sampler == null) {
/* 158 */       return false;
/*     */     }
/*     */ 
/* 161 */     return !sampler.sampleFuture.isDone();
/*     */   }
/*     */ 
/*     */   private void sample() {
/* 165 */     samplerLock.lock();
/*     */     try
/*     */     {
/* 168 */       Sample memorySample = sampleMemory();
/*     */ 
/* 170 */       if (memorySample != null) {
/* 171 */         getSampleCollection(Sample.SampleType.MEMORY).add(memorySample);
/*     */       }
/*     */ 
/* 174 */       Sample cpuSample = sampleCpu();
/* 175 */       if (cpuSample != null)
/* 176 */         getSampleCollection(Sample.SampleType.CPU).add(cpuSample);
/*     */     }
/*     */     finally {
/* 179 */       samplerLock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void clear() {
/* 184 */     for (Collection sampleCollection : this.samples.values())
/* 185 */       sampleCollection.clear();
/*     */   }
/*     */ 
/*     */   public static Sample sampleMemory()
/*     */   {
/* 190 */     if (sampler == null) {
/* 191 */       return null;
/*     */     }
/*     */ 
/* 194 */     return sampleMemory(sampler.activityManager);
/*     */   }
/*     */ 
/*     */   public static Sample sampleMemory(ActivityManager activityManager) {
/* 198 */     Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(PID);
/* 199 */     int totalPss = memInfo[0].getTotalPss();
/*     */ 
/* 201 */     if (totalPss >= 0) {
/* 202 */       Sample sample = new Sample(Sample.SampleType.MEMORY);
/* 203 */       sample.setSampleValue(totalPss / 1024.0D);
/* 204 */       return sample;
/*     */     }
/*     */ 
/* 207 */     return null;
/*     */   }
/*     */ 
/*     */   public Sample sampleCpu() {
/* 211 */     if (cpuSamplingDisabled) {
/* 212 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 216 */       if ((this.procStatFile == null) || (this.appStatFile == null))
/*     */       {
/* 218 */         this.procStatFile = new RandomAccessFile("/proc/stat", "r");
/* 219 */         this.appStatFile = new RandomAccessFile("/proc/" + PID[0] + "/stat", "r");
/*     */       }
/*     */       else {
/* 222 */         this.procStatFile.seek(0L);
/* 223 */         this.appStatFile.seek(0L);
/*     */       }
/*     */ 
/* 226 */       String procStatString = this.procStatFile.readLine();
/* 227 */       String appStatString = this.appStatFile.readLine();
/*     */ 
/* 229 */       String[] procStats = procStatString.split(" ");
/* 230 */       String[] appStats = appStatString.split(" ");
/*     */ 
/* 232 */       long cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3]) + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5]) + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7]) + Long.parseLong(procStats[8]);
/*     */ 
/* 240 */       long appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
/*     */ 
/* 243 */       if ((this.lastCpuTime == null) && (this.lastAppCpuTime == null))
/*     */       {
/* 245 */         this.lastCpuTime = Long.valueOf(cpuTime);
/* 246 */         this.lastAppCpuTime = Long.valueOf(appTime);
/*     */ 
/* 248 */         return null;
/*     */       }
/*     */ 
/* 251 */       Sample sample = new Sample(Sample.SampleType.CPU);
/*     */ 
/* 253 */       sample.setSampleValue((appTime - this.lastAppCpuTime.longValue()) / (cpuTime - this.lastCpuTime.longValue()) * 100.0D);
/*     */ 
/* 255 */       this.lastCpuTime = Long.valueOf(cpuTime);
/* 256 */       this.lastAppCpuTime = Long.valueOf(appTime);
/*     */ 
/* 258 */       return sample;
/*     */     }
/*     */     catch (Exception e) {
/* 261 */       cpuSamplingDisabled = true;
/* 262 */       log.debug("Exception hit while CPU sampling: " + e.getMessage());
/* 263 */       AgentHealth.noticeException(e);
/* 264 */     }return null;
/*     */   }
/*     */ 
/*     */   private void resetCpuSampler()
/*     */   {
/* 269 */     this.lastCpuTime = null;
/* 270 */     this.lastAppCpuTime = null;
/*     */ 
/* 272 */     if ((this.appStatFile != null) && (this.procStatFile != null))
/*     */       try {
/* 274 */         this.appStatFile.close();
/* 275 */         this.procStatFile.close();
/* 276 */         this.appStatFile = null;
/* 277 */         this.procStatFile = null;
/*     */       } catch (IOException e) {
/* 279 */         log.debug("Exception hit while resetting CPU sampler: " + e.getMessage());
/* 280 */         AgentHealth.noticeException(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static Map<Sample.SampleType, Collection<Sample>> copySamples()
/*     */   {
/* 286 */     samplerLock.lock();
/*     */ 
/* 288 */     if (sampler == null) {
/* 289 */       samplerLock.unlock();
/* 290 */       return new HashMap();
/*     */     }
/*     */ 
/* 293 */     EnumMap copy = new EnumMap(sampler.samples);
/*     */ 
/* 295 */     for (Sample.SampleType key : sampler.samples.keySet()) {
/* 296 */       copy.put(key, new ArrayList((Collection)sampler.samples.get(key)));
/*     */     }
/*     */ 
/* 299 */     samplerLock.unlock();
/*     */ 
/* 301 */     return Collections.unmodifiableMap(copy);
/*     */   }
/*     */ 
/*     */   private Collection<Sample> getSampleCollection(Sample.SampleType type) {
/* 305 */     return (Collection)this.samples.get(type);
/*     */   }
/*     */ 
/*     */   public void onEnterMethod()
/*     */   {
/* 313 */     if (this.isRunning.get()) {
/* 314 */       return;
/*     */     }
/* 316 */     start();
/*     */   }
/*     */ 
/*     */   public void onExitMethod()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onTraceStart(ActivityTrace activityTrace) {
/* 324 */     start();
/*     */   }
/*     */ 
/*     */   public void onTraceComplete(ActivityTrace activityTrace)
/*     */   {
/* 329 */     stop();
/* 330 */     activityTrace.setVitals(copySamples());
/* 331 */     clear();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.sample.Sampler
 * JD-Core Version:    0.6.2
 */