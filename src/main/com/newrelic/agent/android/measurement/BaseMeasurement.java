/*     */ package com.newrelic.agent.android.measurement;
/*     */ 
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ 
/*     */ public class BaseMeasurement
/*     */   implements Measurement
/*     */ {
/*  11 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private MeasurementType type;
/*     */   private String name;
/*     */   private String scope;
/*     */   private long startTime;
/*     */   private long endTime;
/*     */   private long exclusiveTime;
/*     */   private ThreadInfo threadInfo;
/*     */   private boolean finished;
/*     */ 
/*     */   public BaseMeasurement(MeasurementType measurementType)
/*     */   {
/*  23 */     setType(measurementType);
/*     */   }
/*     */ 
/*     */   public BaseMeasurement(Measurement measurement) {
/*  27 */     setType(measurement.getType());
/*  28 */     setName(measurement.getName());
/*  29 */     setScope(measurement.getScope());
/*  30 */     setStartTime(measurement.getStartTime());
/*  31 */     setEndTime(measurement.getEndTime());
/*  32 */     setExclusiveTime(measurement.getExclusiveTime());
/*  33 */     setThreadInfo(measurement.getThreadInfo());
/*  34 */     this.finished = measurement.isFinished();
/*     */   }
/*     */ 
/*     */   void setType(MeasurementType type) {
/*  38 */     throwIfFinished();
/*  39 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  43 */     throwIfFinished();
/*  44 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public void setScope(String scope) {
/*  48 */     throwIfFinished();
/*  49 */     this.scope = scope;
/*     */   }
/*     */ 
/*     */   public void setStartTime(long startTime) {
/*  53 */     throwIfFinished();
/*  54 */     this.startTime = startTime;
/*     */   }
/*     */ 
/*     */   public void setEndTime(long endTime) {
/*  58 */     throwIfFinished();
/*  59 */     if (endTime < this.startTime) {
/*  60 */       log.error("Measurement end time must not precede start time - startTime: " + this.startTime + " endTime: " + endTime);
/*  61 */       return;
/*     */     }
/*  63 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public void setExclusiveTime(long exclusiveTime) {
/*  67 */     throwIfFinished();
/*  68 */     this.exclusiveTime = exclusiveTime;
/*     */   }
/*     */ 
/*     */   public MeasurementType getType()
/*     */   {
/*  73 */     return this.type;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  78 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getScope()
/*     */   {
/*  83 */     return this.scope;
/*     */   }
/*     */ 
/*     */   public long getStartTime()
/*     */   {
/*  88 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public double getStartTimeInSeconds()
/*     */   {
/*  93 */     return this.startTime / 1000.0D;
/*     */   }
/*     */ 
/*     */   public long getEndTime()
/*     */   {
/*  98 */     return this.endTime;
/*     */   }
/*     */ 
/*     */   public double getEndTimeInSeconds()
/*     */   {
/* 103 */     return this.endTime / 1000.0D;
/*     */   }
/*     */ 
/*     */   public long getExclusiveTime()
/*     */   {
/* 108 */     return this.exclusiveTime;
/*     */   }
/*     */ 
/*     */   public double getExclusiveTimeInSeconds()
/*     */   {
/* 113 */     return this.exclusiveTime / 1000.0D;
/*     */   }
/*     */ 
/*     */   public double asDouble()
/*     */   {
/* 118 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ThreadInfo getThreadInfo()
/*     */   {
/* 123 */     return this.threadInfo;
/*     */   }
/*     */ 
/*     */   public void setThreadInfo(ThreadInfo threadInfo) {
/* 127 */     this.threadInfo = threadInfo;
/*     */   }
/*     */ 
/*     */   public boolean isInstantaneous()
/*     */   {
/* 132 */     return this.endTime == 0L;
/*     */   }
/*     */ 
/*     */   public void finish()
/*     */   {
/* 137 */     if (this.finished)
/* 138 */       throw new MeasurementException("Finish called on already finished Measurement");
/* 139 */     this.finished = true;
/*     */   }
/*     */ 
/*     */   public boolean isFinished()
/*     */   {
/* 144 */     return this.finished;
/*     */   }
/*     */ 
/*     */   private void throwIfFinished() {
/* 148 */     if (this.finished)
/* 149 */       throw new MeasurementException("Attempted to modify finished Measurement");
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 154 */     return "BaseMeasurement{type=" + this.type + ", name='" + this.name + '\'' + ", scope='" + this.scope + '\'' + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", exclusiveTime=" + this.exclusiveTime + ", threadInfo=" + this.threadInfo + ", finished=" + this.finished + '}';
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.BaseMeasurement
 * JD-Core Version:    0.6.2
 */