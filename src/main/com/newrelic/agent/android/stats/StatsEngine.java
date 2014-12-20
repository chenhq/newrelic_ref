/*     */ package com.newrelic.agent.android.stats;
/*     */ 
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.harvest.HarvestAdapter;
/*     */ import com.newrelic.agent.android.metric.Metric;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class StatsEngine extends HarvestAdapter
/*     */ {
/*  17 */   public static final StatsEngine INSTANCE = new StatsEngine();
/*  18 */   public boolean enabled = true;
/*     */ 
/*  20 */   private ConcurrentHashMap<String, Metric> statsMap = new ConcurrentHashMap();
/*     */ 
/*     */   public static StatsEngine get()
/*     */   {
/*  27 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   public void inc(String name)
/*     */   {
/*  36 */     Metric m = lazyGet(name);
/*     */ 
/*  38 */     synchronized (m) {
/*  39 */       m.increment();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void inc(String name, long count)
/*     */   {
/*  50 */     Metric m = lazyGet(name);
/*     */ 
/*  52 */     synchronized (m) {
/*  53 */       m.increment(count);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sample(String name, float value)
/*     */   {
/*  64 */     Metric m = lazyGet(name);
/*     */ 
/*  66 */     synchronized (m)
/*     */     {
/*  68 */       m.sample(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sampleTimeMs(String name, long time)
/*     */   {
/*  79 */     sample(name, (float)time / 1000.0F);
/*     */   }
/*     */ 
/*     */   public static void populateMetrics()
/*     */   {
/*  86 */     for (Map.Entry entry : INSTANCE.getStatsMap().entrySet()) {
/*  87 */       Metric metric = (Metric)entry.getValue();
/*  88 */       TaskQueue.queue(metric);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onHarvest()
/*     */   {
/*  94 */     populateMetrics();
/*  95 */     reset();
/*     */   }
/*     */ 
/*     */   public static void reset()
/*     */   {
/* 103 */     INSTANCE.getStatsMap().clear();
/*     */   }
/*     */ 
/*     */   public static synchronized void disable()
/*     */   {
/* 111 */     INSTANCE.enabled = false;
/*     */   }
/*     */ 
/*     */   public static synchronized void enable()
/*     */   {
/* 118 */     INSTANCE.enabled = true;
/*     */   }
/*     */ 
/*     */   private ConcurrentHashMap<String, Metric> getStatsMap() {
/* 122 */     return this.statsMap;
/*     */   }
/*     */ 
/*     */   private Metric lazyGet(String name) {
/* 126 */     Metric m = (Metric)this.statsMap.get(name);
/*     */ 
/* 128 */     if (m == null) {
/* 129 */       m = new Metric(name);
/*     */ 
/* 131 */       if (this.enabled) {
/* 132 */         this.statsMap.put(name, m);
/*     */       }
/*     */     }
/*     */ 
/* 136 */     return m;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.stats.StatsEngine
 * JD-Core Version:    0.6.2
 */