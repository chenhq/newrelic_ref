/*     */ package com.newrelic.com.google.common.util.concurrent;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ 
/*     */ final class SerializingExecutor
/*     */   implements Executor
/*     */ {
/*  47 */   private static final Logger log = Logger.getLogger(SerializingExecutor.class.getName());
/*     */   private final Executor executor;
/*     */ 
/*     */   @GuardedBy("internalLock")
/*  54 */   private final Queue<Runnable> waitQueue = new ArrayDeque();
/*     */ 
/*     */   @GuardedBy("internalLock")
/*  65 */   private boolean isThreadScheduled = false;
/*     */ 
/*  69 */   private final TaskRunner taskRunner = new TaskRunner(null);
/*     */ 
/*  81 */   private final Object internalLock = new Object() {
/*     */     public String toString() {
/*  83 */       return "SerializingExecutor lock: " + super.toString();
/*     */     }
/*  81 */   };
/*     */ 
/*     */   public SerializingExecutor(Executor executor)
/*     */   {
/*  77 */     Preconditions.checkNotNull(executor, "'executor' must not be null.");
/*  78 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */   public void execute(Runnable r)
/*     */   {
/*  93 */     Preconditions.checkNotNull(r, "'r' must not be null.");
/*  94 */     boolean scheduleTaskRunner = false;
/*  95 */     synchronized (this.internalLock) {
/*  96 */       this.waitQueue.add(r);
/*     */ 
/*  98 */       if (!this.isThreadScheduled) {
/*  99 */         this.isThreadScheduled = true;
/* 100 */         scheduleTaskRunner = true;
/*     */       }
/*     */     }
/* 103 */     if (scheduleTaskRunner) {
/* 104 */       boolean threw = true;
/*     */       try {
/* 106 */         this.executor.execute(this.taskRunner);
/* 107 */         threw = false;
/*     */       } finally {
/* 109 */         if (threw)
/* 110 */           synchronized (this.internalLock)
/*     */           {
/* 115 */             this.isThreadScheduled = false;
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class TaskRunner
/*     */     implements Runnable
/*     */   {
/*     */     private TaskRunner()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 132 */       boolean stillRunning = true;
/*     */       try {
/*     */         while (true) {
/* 135 */           Preconditions.checkState(SerializingExecutor.this.isThreadScheduled);
/*     */           Runnable nextToRun;
/* 137 */           synchronized (SerializingExecutor.this.internalLock) {
/* 138 */             nextToRun = (Runnable)SerializingExecutor.this.waitQueue.poll();
/* 139 */             if (nextToRun == null) {
/* 140 */               SerializingExecutor.this.isThreadScheduled = false;
/* 141 */               stillRunning = false;
/* 142 */               break;
/*     */             }
/*     */           }
/*     */ 
/*     */           try
/*     */           {
/* 148 */             nextToRun.run();
/*     */           }
/*     */           catch (RuntimeException e) {
/* 151 */             SerializingExecutor.log.log(Level.SEVERE, "Exception while executing runnable " + nextToRun, e);
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 156 */         if (stillRunning)
/*     */         {
/* 160 */           synchronized (SerializingExecutor.this.internalLock) {
/* 161 */             SerializingExecutor.this.isThreadScheduled = false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.SerializingExecutor
 * JD-Core Version:    0.6.2
 */