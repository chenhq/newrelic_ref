/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import com.newrelic.agent.android.activity.MeasuredActivity;
/*     */ import com.newrelic.agent.android.activity.NamedActivity;
/*     */ import com.newrelic.agent.android.measurement.MeasurementException;
/*     */ import com.newrelic.agent.android.measurement.MeasurementPool;
/*     */ import com.newrelic.agent.android.measurement.consumer.MeasurementConsumer;
/*     */ import com.newrelic.agent.android.measurement.producer.MeasurementProducer;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class MeasurementEngine
/*     */ {
/*  18 */   private final Map<String, MeasuredActivity> activities = new ConcurrentHashMap();
/*  19 */   private final MeasurementPool rootMeasurementPool = new MeasurementPool();
/*     */ 
/*     */   public MeasuredActivity startActivity(String activityName)
/*     */   {
/*  28 */     if (this.activities.containsKey(activityName)) {
/*  29 */       throw new MeasurementException("An activity with the name '" + activityName + "' has already started.");
/*     */     }
/*  31 */     NamedActivity activity = new NamedActivity(activityName);
/*     */ 
/*  33 */     this.activities.put(activityName, activity);
/*     */ 
/*  35 */     MeasurementPool measurementPool = new MeasurementPool();
/*  36 */     activity.setMeasurementPool(measurementPool);
/*  37 */     this.rootMeasurementPool.addMeasurementConsumer(measurementPool);
/*  38 */     return activity;
/*     */   }
/*     */ 
/*     */   public void renameActivity(String oldName, String newName)
/*     */   {
/*  48 */     MeasuredActivity namedActivity = (MeasuredActivity)this.activities.remove(oldName);
/*     */ 
/*  50 */     if ((namedActivity != null) && ((namedActivity instanceof NamedActivity))) {
/*  51 */       this.activities.put(newName, namedActivity);
/*  52 */       ((NamedActivity)namedActivity).rename(newName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MeasuredActivity endActivity(String activityName)
/*     */   {
/*  62 */     MeasuredActivity measuredActivity = (MeasuredActivity)this.activities.get(activityName);
/*  63 */     if (measuredActivity == null) {
/*  64 */       throw new MeasurementException("Activity '" + activityName + "' has not been started.");
/*     */     }
/*  66 */     endActivity(measuredActivity);
/*     */ 
/*  68 */     return measuredActivity;
/*     */   }
/*     */ 
/*     */   public void endActivity(MeasuredActivity activity)
/*     */   {
/*  76 */     this.rootMeasurementPool.removeMeasurementConsumer(activity.getMeasurementPool());
/*  77 */     this.activities.remove(activity.getName());
/*  78 */     activity.finish();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  85 */     this.activities.clear();
/*     */   }
/*     */ 
/*     */   public void addMeasurementProducer(MeasurementProducer measurementProducer)
/*     */   {
/*  93 */     this.rootMeasurementPool.addMeasurementProducer(measurementProducer);
/*     */   }
/*     */ 
/*     */   public void removeMeasurementProducer(MeasurementProducer measurementProducer)
/*     */   {
/* 101 */     this.rootMeasurementPool.removeMeasurementProducer(measurementProducer);
/*     */   }
/*     */ 
/*     */   public void addMeasurementConsumer(MeasurementConsumer measurementConsumer)
/*     */   {
/* 109 */     this.rootMeasurementPool.addMeasurementConsumer(measurementConsumer);
/*     */   }
/*     */ 
/*     */   public void removeMeasurementConsumer(MeasurementConsumer measurementConsumer)
/*     */   {
/* 117 */     this.rootMeasurementPool.removeMeasurementConsumer(measurementConsumer);
/*     */   }
/*     */ 
/*     */   public void broadcastMeasurements()
/*     */   {
/* 124 */     this.rootMeasurementPool.broadcastMeasurements();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.MeasurementEngine
 * JD-Core Version:    0.6.2
 */