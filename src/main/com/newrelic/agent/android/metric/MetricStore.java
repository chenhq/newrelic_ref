/*     */ package com.newrelic.agent.android.metric;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class MetricStore
/*     */ {
/*     */   private final Map<String, Map<String, Metric>> metricStore;
/*     */ 
/*     */   public MetricStore()
/*     */   {
/*  16 */     this.metricStore = new ConcurrentHashMap();
/*     */   }
/*     */ 
/*     */   public void add(Metric metric) {
/*  20 */     String scope = metric.getStringScope();
/*  21 */     String name = metric.getName();
/*     */ 
/*  24 */     if (!this.metricStore.containsKey(scope)) {
/*  25 */       this.metricStore.put(scope, new HashMap());
/*     */     }
/*     */ 
/*  28 */     if (((Map)this.metricStore.get(scope)).containsKey(name))
/*  29 */       ((Metric)((Map)this.metricStore.get(scope)).get(name)).aggregate(metric);
/*     */     else
/*  31 */       ((Map)this.metricStore.get(scope)).put(name, metric);
/*     */   }
/*     */ 
/*     */   public Metric get(String name)
/*     */   {
/*  36 */     return get(name, "");
/*     */   }
/*     */ 
/*     */   public Metric get(String name, String scope) {
/*     */     try {
/*  41 */       return (Metric)((Map)this.metricStore.get(scope == null ? "" : scope)).get(name); } catch (NullPointerException e) {
/*     */     }
/*  43 */     return null;
/*     */   }
/*     */ 
/*     */   public List<Metric> getAll()
/*     */   {
/*  48 */     List metrics = new ArrayList();
/*     */ 
/*  50 */     for (Map.Entry entry : this.metricStore.entrySet()) {
/*  51 */       for (Map.Entry metricEntry : ((Map)entry.getValue()).entrySet()) {
/*  52 */         metrics.add(metricEntry.getValue());
/*     */       }
/*     */     }
/*     */ 
/*  56 */     return metrics;
/*     */   }
/*     */ 
/*     */   public List<Metric> getAllByScope(String scope) {
/*  60 */     List metrics = new ArrayList();
/*     */     try
/*     */     {
/*  63 */       for (Map.Entry metricEntry : ((Map)this.metricStore.get(scope)).entrySet()) {
/*  64 */         metrics.add(metricEntry.getValue());
/*     */       }
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*     */     }
/*  70 */     return metrics;
/*     */   }
/*     */ 
/*     */   public List<Metric> getAllUnscoped() {
/*  74 */     return getAllByScope("");
/*     */   }
/*     */ 
/*     */   public void remove(Metric metric) {
/*  78 */     String scope = metric.getStringScope();
/*  79 */     String name = metric.getName();
/*     */ 
/*  81 */     if (!this.metricStore.containsKey(scope)) {
/*  82 */       return;
/*     */     }
/*  84 */     if (!((Map)this.metricStore.get(scope)).containsKey(name)) {
/*  85 */       return;
/*     */     }
/*  87 */     ((Map)this.metricStore.get(scope)).remove(name);
/*     */   }
/*     */ 
/*     */   public void removeAll(List<Metric> metrics) {
/*  91 */     synchronized (this.metricStore) {
/*  92 */       for (Metric metric : metrics)
/*  93 */         remove(metric);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Metric> removeAllWithScope(String scope)
/*     */   {
/*  99 */     List metrics = getAllByScope(scope);
/* 100 */     if (!metrics.isEmpty()) {
/* 101 */       removeAll(metrics);
/*     */     }
/* 103 */     return metrics;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 107 */     this.metricStore.clear();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 111 */     return this.metricStore.isEmpty();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.metric.MetricStore
 * JD-Core Version:    0.6.2
 */