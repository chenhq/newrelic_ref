/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.stats.TicToc;
/*     */ import com.newrelic.agent.android.util.NamedThreadFactory;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class HarvestTimer
/*     */   implements Runnable
/*     */ {
/*     */   private static final long DEFAULT_HARVEST_PERIOD = 60000L;
/*     */   private static final long HARVEST_PERIOD_LEEWAY = 1000L;
/*     */   private static final long NEVER_TICKED = -1L;
/*  18 */   private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Harvester"));
/*  19 */   private final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private ScheduledFuture tickFuture;
/*  21 */   private long period = 60000L;
/*     */   protected final Harvester harvester;
/*     */   protected long lastTickTime;
/*     */   private long startTimeMs;
/*     */ 
/*     */   public HarvestTimer(Harvester harvester)
/*     */   {
/*  27 */     this.harvester = harvester;
/*     */   }
/*     */ 
/*     */   public void run() {
/*  31 */     synchronized (this) {
/*     */       try {
/*  33 */         tickIfReady();
/*     */       } catch (Exception e) {
/*  35 */         this.log.error("HarvestTimer: Exception in timer tick: " + e.getMessage());
/*  36 */         e.printStackTrace();
/*  37 */         AgentHealth.noticeException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void tickIfReady() {
/*  43 */     long lastTickDelta = timeSinceLastTick();
/*     */ 
/*  47 */     if ((lastTickDelta + 1000L < this.period) && (lastTickDelta != -1L)) {
/*  48 */       this.log.debug("HarvestTimer: Tick is too soon (" + lastTickDelta + " delta) Last tick time: " + this.lastTickTime + " . Skipping.");
/*  49 */       return;
/*     */     }
/*     */ 
/*  52 */     this.log.debug("HarvestTimer: time since last tick: " + lastTickDelta);
/*  53 */     long tickStart = now();
/*     */     try
/*     */     {
/*  57 */       tick();
/*     */     } catch (Exception e) {
/*  59 */       this.log.error("HarvestTimer: Exception in timer tick: " + e.getMessage());
/*  60 */       e.printStackTrace();
/*  61 */       AgentHealth.noticeException(e);
/*     */     }
/*     */ 
/*  64 */     this.lastTickTime = tickStart;
/*  65 */     this.log.debug("Set last tick time to: " + this.lastTickTime);
/*     */   }
/*     */ 
/*     */   protected void tick() {
/*  69 */     this.log.debug("Harvest: tick");
/*  70 */     TicToc t = new TicToc();
/*  71 */     t.tic();
/*     */     try
/*     */     {
/*  74 */       this.harvester.execute();
/*     */     } catch (Exception e) {
/*  76 */       this.log.error("HarvestTimer: Exception in harvest execute: " + e.getMessage());
/*  77 */       e.printStackTrace();
/*  78 */       AgentHealth.noticeException(e);
/*     */     }
/*     */ 
/*  81 */     this.log.debug("Harvest: executed");
/*     */ 
/*  84 */     if (this.harvester.isDisabled()) {
/*  85 */       stop();
/*     */     }
/*     */ 
/*  88 */     long tickDelta = t.toc();
/*     */ 
/*  90 */     this.log.debug("HarvestTimer tick took " + tickDelta + "ms");
/*     */   }
/*     */ 
/*     */   public void start() {
/*  94 */     if (isRunning()) {
/*  95 */       this.log.warning("HarvestTimer: Attempting to start while already running");
/*  96 */       return;
/*     */     }
/*     */ 
/*  99 */     if (this.period <= 0L) {
/* 100 */       this.log.error("HarvestTimer: Refusing to start with a period of 0 ms");
/* 101 */       return;
/*     */     }
/*     */ 
/* 104 */     this.log.debug("HarvestTimer: Starting with a period of " + this.period + "ms");
/* 105 */     this.startTimeMs = System.currentTimeMillis();
/* 106 */     this.tickFuture = this.scheduler.scheduleAtFixedRate(this, 0L, this.period, TimeUnit.MILLISECONDS);
/*     */ 
/* 109 */     this.harvester.start();
/*     */   }
/*     */ 
/*     */   public void stop() {
/* 113 */     if (!isRunning()) {
/* 114 */       this.log.warning("HarvestTimer: Attempting to stop when not running");
/* 115 */       return;
/*     */     }
/* 117 */     this.log.debug("HarvestTimer: Stopped.");
/* 118 */     this.startTimeMs = 0L;
/* 119 */     this.harvester.stop();
/* 120 */     this.tickFuture.cancel(true);
/* 121 */     this.tickFuture = null;
/*     */   }
/*     */ 
/*     */   public void shutdown() {
/* 125 */     this.scheduler.shutdownNow();
/*     */   }
/*     */ 
/*     */   public void tickNow()
/*     */   {
/* 130 */     final HarvestTimer timer = this;
/* 131 */     ScheduledFuture future = this.scheduler.schedule(new Runnable()
/*     */     {
/*     */       public void run() {
/* 134 */         timer.tick();
/*     */       }
/*     */     }
/*     */     , 0L, TimeUnit.SECONDS);
/*     */     try
/*     */     {
/* 138 */       future.get();
/*     */     } catch (Exception e) {
/* 140 */       this.log.error("Exception waiting for tickNow to finish: " + e.getMessage());
/* 141 */       e.printStackTrace();
/* 142 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isRunning() {
/* 147 */     return this.tickFuture != null;
/*     */   }
/*     */ 
/*     */   public void setPeriod(long period) {
/* 151 */     this.period = period;
/*     */   }
/*     */ 
/*     */   public long timeSinceLastTick() {
/* 155 */     if (this.lastTickTime == 0L)
/* 156 */       return -1L;
/* 157 */     return now() - this.lastTickTime;
/*     */   }
/*     */ 
/*     */   public long timeSinceStart() {
/* 161 */     if (this.startTimeMs == 0L)
/* 162 */       return 0L;
/* 163 */     return now() - this.startTimeMs;
/*     */   }
/*     */ 
/*     */   private long now() {
/* 167 */     return System.currentTimeMillis();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestTimer
 * JD-Core Version:    0.6.2
 */