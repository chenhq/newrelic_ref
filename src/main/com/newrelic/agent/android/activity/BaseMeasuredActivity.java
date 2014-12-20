/*     */ package com.newrelic.agent.android.activity;
/*     */ 
/*     */ import com.newrelic.agent.android.measurement.Measurement;
/*     */ import com.newrelic.agent.android.measurement.MeasurementException;
/*     */ import com.newrelic.agent.android.measurement.MeasurementPool;
/*     */ import com.newrelic.agent.android.measurement.ThreadInfo;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ 
/*     */ public class BaseMeasuredActivity
/*     */   implements MeasuredActivity
/*     */ {
/*     */   private String name;
/*     */   private long startTime;
/*     */   private long endTime;
/*     */   private ThreadInfo startingThread;
/*     */   private ThreadInfo endingThread;
/*     */   private boolean autoInstrumented;
/*     */   private Measurement startingMeasurement;
/*     */   private Measurement endingMeasurement;
/*     */   private MeasurementPool measurementPool;
/*     */   private boolean finished;
/*     */ 
/*     */   public String getName()
/*     */   {
/*  28 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getMetricName()
/*     */   {
/*  33 */     return TraceMachine.formatActivityMetricName(this.name);
/*     */   }
/*     */ 
/*     */   public String getBackgroundMetricName()
/*     */   {
/*  38 */     return TraceMachine.formatActivityBackgroundMetricName(this.name);
/*     */   }
/*     */ 
/*     */   public long getStartTime()
/*     */   {
/*  43 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public long getEndTime()
/*     */   {
/*  48 */     return this.endTime;
/*     */   }
/*     */ 
/*     */   public ThreadInfo getStartingThread()
/*     */   {
/*  53 */     return this.startingThread;
/*     */   }
/*     */ 
/*     */   public ThreadInfo getEndingThread()
/*     */   {
/*  58 */     return this.endingThread;
/*     */   }
/*     */ 
/*     */   public boolean isAutoInstrumented()
/*     */   {
/*  63 */     return this.autoInstrumented;
/*     */   }
/*     */ 
/*     */   public Measurement getStartingMeasurement()
/*     */   {
/*  68 */     return this.startingMeasurement;
/*     */   }
/*     */ 
/*     */   public Measurement getEndingMeasurement()
/*     */   {
/*  73 */     return this.endingMeasurement;
/*     */   }
/*     */ 
/*     */   public MeasurementPool getMeasurementPool()
/*     */   {
/*  78 */     return this.measurementPool;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/*  87 */     throwIfFinished();
/*  88 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public void setStartTime(long startTime)
/*     */   {
/*  96 */     throwIfFinished();
/*  97 */     this.startTime = startTime;
/*     */   }
/*     */ 
/*     */   public void setEndTime(long endTime)
/*     */   {
/* 105 */     throwIfFinished();
/* 106 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public void setStartingThread(ThreadInfo startingThread)
/*     */   {
/* 114 */     throwIfFinished();
/* 115 */     this.startingThread = startingThread;
/*     */   }
/*     */ 
/*     */   public void setEndingThread(ThreadInfo endingThread)
/*     */   {
/* 123 */     throwIfFinished();
/* 124 */     this.endingThread = endingThread;
/*     */   }
/*     */ 
/*     */   public void setAutoInstrumented(boolean autoInstrumented)
/*     */   {
/* 132 */     throwIfFinished();
/* 133 */     this.autoInstrumented = autoInstrumented;
/*     */   }
/*     */ 
/*     */   public void setStartingMeasurement(Measurement startingMeasurement)
/*     */   {
/* 141 */     throwIfFinished();
/* 142 */     this.startingMeasurement = startingMeasurement;
/*     */   }
/*     */ 
/*     */   public void setEndingMeasurement(Measurement endingMeasurement)
/*     */   {
/* 150 */     throwIfFinished();
/* 151 */     this.endingMeasurement = endingMeasurement;
/*     */   }
/*     */ 
/*     */   public void setMeasurementPool(MeasurementPool measurementPool)
/*     */   {
/* 159 */     throwIfFinished();
/* 160 */     this.measurementPool = measurementPool;
/*     */   }
/*     */ 
/*     */   public void finish()
/*     */   {
/* 168 */     this.finished = true;
/*     */   }
/*     */ 
/*     */   public boolean isFinished()
/*     */   {
/* 173 */     return this.finished;
/*     */   }
/*     */ 
/*     */   private void throwIfFinished() {
/* 177 */     if (this.finished) throw new MeasurementException("Cannot modify finished Activity");
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.activity.BaseMeasuredActivity
 * JD-Core Version:    0.6.2
 */