/*     */ package com.newrelic.agent.android;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.AgentHealth;
/*     */ import com.newrelic.agent.android.harvest.AgentHealthException;
/*     */ import com.newrelic.agent.android.harvest.Harvest;
/*     */ import com.newrelic.agent.android.harvest.HarvestAdapter;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import com.newrelic.agent.android.metric.Metric;
/*     */ import com.newrelic.agent.android.tracing.ActivityTrace;
/*     */ import com.newrelic.agent.android.tracing.Trace;
/*     */ import com.newrelic.agent.android.util.NamedThreadFactory;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class TaskQueue extends HarvestAdapter
/*     */ {
/*     */   private static final long DEQUEUE_PERIOD_MS = 1000L;
/*  27 */   private static final ScheduledExecutorService queueExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("TaskQueue"));
/*  28 */   private static final ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue();
/*  29 */   private static final Runnable dequeueTask = new Runnable()
/*     */   {
/*     */     public void run() {
/*  32 */       TaskQueue.access$000();
/*     */     }
/*  29 */   };
/*     */   private static Future dequeueFuture;
/*     */ 
/*     */   public static void queue(Object object)
/*     */   {
/*  43 */     queue.add(object);
/*     */   }
/*     */ 
/*     */   public static void backgroundDequeue()
/*     */   {
/*  50 */     queueExecutor.execute(dequeueTask);
/*     */   }
/*     */ 
/*     */   public static void synchronousDequeue()
/*     */   {
/*  57 */     Future future = queueExecutor.submit(dequeueTask);
/*     */     try {
/*  59 */       future.get();
/*     */     } catch (InterruptedException e) {
/*  61 */       e.printStackTrace();
/*     */     } catch (ExecutionException e) {
/*  63 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void start()
/*     */   {
/*  71 */     if (dequeueFuture != null) {
/*  72 */       return;
/*     */     }
/*  74 */     dequeueFuture = queueExecutor.scheduleAtFixedRate(dequeueTask, 0L, 1000L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   public static void stop()
/*     */   {
/*  81 */     if (dequeueFuture == null) {
/*  82 */       return;
/*     */     }
/*  84 */     dequeueFuture.cancel(true);
/*  85 */     dequeueFuture = null;
/*     */   }
/*     */ 
/*     */   private static void dequeue()
/*     */   {
/*  94 */     if (queue.size() == 0) {
/*  95 */       return;
/*     */     }
/*     */ 
/* 102 */     Measurements.setBroadcastNewMeasurements(false);
/* 103 */     while (!queue.isEmpty()) {
/*     */       try {
/* 105 */         Object object = queue.remove();
/*     */ 
/* 108 */         if ((object instanceof ActivityTrace)) {
/* 109 */           Harvest.addActivityTrace((ActivityTrace)object);
/*     */         }
/* 114 */         else if ((object instanceof Metric)) {
/* 115 */           Harvest.addMetric((Metric)object);
/*     */         }
/* 120 */         else if ((object instanceof AgentHealthException)) {
/* 121 */           Harvest.addAgentHealthException((AgentHealthException)object);
/*     */         }
/* 126 */         else if ((object instanceof Trace)) {
/* 127 */           Measurements.addTracedMethod((Trace)object);
/*     */         }
/* 132 */         else if ((object instanceof HttpTransactionMeasurement))
/* 133 */           Measurements.addHttpTransaction((HttpTransactionMeasurement)object);
/*     */       }
/*     */       catch (Exception e) {
/* 136 */         e.printStackTrace();
/* 137 */         AgentHealth.noticeException(e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 142 */     Measurements.broadcast();
/* 143 */     Measurements.setBroadcastNewMeasurements(true);
/*     */   }
/*     */ 
/*     */   public static int size()
/*     */   {
/* 152 */     return queue.size();
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 159 */     queue.clear();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.TaskQueue
 * JD-Core Version:    0.6.2
 */