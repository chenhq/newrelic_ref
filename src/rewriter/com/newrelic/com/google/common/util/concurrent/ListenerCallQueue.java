/*     */ package com.newrelic.com.google.common.util.concurrent;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.collect.Queues;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ 
/*     */ final class ListenerCallQueue<L>
/*     */   implements Runnable
/*     */ {
/*  40 */   private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());
/*     */   private final L listener;
/*     */   private final Executor executor;
/*     */ 
/*     */   @GuardedBy("this")
/*  62 */   private final Queue<Callback<L>> waitQueue = Queues.newArrayDeque();
/*     */ 
/*     */   @GuardedBy("this")
/*     */   private boolean isThreadScheduled;
/*     */ 
/*  66 */   ListenerCallQueue(L listener, Executor executor) { this.listener = Preconditions.checkNotNull(listener);
/*  67 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */   }
/*     */ 
/*     */   synchronized void add(Callback<L> callback)
/*     */   {
/*  72 */     this.waitQueue.add(callback);
/*     */   }
/*     */ 
/*     */   void execute()
/*     */   {
/*  77 */     boolean scheduleTaskRunner = false;
/*  78 */     synchronized (this) {
/*  79 */       if (!this.isThreadScheduled) {
/*  80 */         this.isThreadScheduled = true;
/*  81 */         scheduleTaskRunner = true;
/*     */       }
/*     */     }
/*  84 */     if (scheduleTaskRunner)
/*     */       try {
/*  86 */         this.executor.execute(this);
/*     */       }
/*     */       catch (RuntimeException e) {
/*  89 */         synchronized (this) {
/*  90 */           this.isThreadScheduled = false;
/*     */         }
/*     */ 
/*  93 */         logger.log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, e);
/*     */ 
/*  96 */         throw e;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 102 */     boolean stillRunning = true;
/*     */     try
/*     */     {
/*     */       while (true)
/*     */       {
/*     */         Callback nextToRun;
/* 106 */         synchronized (this) {
/* 107 */           Preconditions.checkState(this.isThreadScheduled);
/* 108 */           nextToRun = (Callback)this.waitQueue.poll();
/* 109 */           if (nextToRun == null) {
/* 110 */             this.isThreadScheduled = false;
/* 111 */             stillRunning = false;
/* 112 */             break;
/*     */           }
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 118 */           nextToRun.call(this.listener);
/*     */         }
/*     */         catch (RuntimeException e) {
/* 121 */           logger.log(Level.SEVERE, "Exception while executing callback: " + this.listener + "." + nextToRun.methodCall, e);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 127 */       if (stillRunning)
/*     */       {
/* 131 */         synchronized (this) {
/* 132 */           this.isThreadScheduled = false;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class Callback<L>
/*     */   {
/*     */     private final String methodCall;
/*     */ 
/*     */     Callback(String methodCall)
/*     */     {
/*  46 */       this.methodCall = methodCall;
/*     */     }
/*     */ 
/*     */     abstract void call(L paramL);
/*     */ 
/*     */     void enqueueOn(Iterable<ListenerCallQueue<L>> queues)
/*     */     {
/*  53 */       for (ListenerCallQueue queue : queues)
/*  54 */         queue.add(this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.ListenerCallQueue
 * JD-Core Version:    0.6.2
 */