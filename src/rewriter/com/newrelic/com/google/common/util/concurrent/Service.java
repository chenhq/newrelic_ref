/*     */ package com.newrelic.com.google.common.util.concurrent;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ @Beta
/*     */ public abstract interface Service
/*     */ {
/*     */   public abstract Service startAsync();
/*     */ 
/*     */   public abstract boolean isRunning();
/*     */ 
/*     */   public abstract State state();
/*     */ 
/*     */   public abstract Service stopAsync();
/*     */ 
/*     */   public abstract void awaitRunning();
/*     */ 
/*     */   public abstract void awaitRunning(long paramLong, TimeUnit paramTimeUnit)
/*     */     throws TimeoutException;
/*     */ 
/*     */   public abstract void awaitTerminated();
/*     */ 
/*     */   public abstract void awaitTerminated(long paramLong, TimeUnit paramTimeUnit)
/*     */     throws TimeoutException;
/*     */ 
/*     */   public abstract Throwable failureCause();
/*     */ 
/*     */   public abstract void addListener(Listener paramListener, Executor paramExecutor);
/*     */ 
/*     */   @Beta
/*     */   public static abstract class Listener
/*     */   {
/*     */     public void starting()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void running()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void stopping(Service.State from)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void terminated(Service.State from)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void failed(Service.State from, Throwable failure)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static abstract enum State
/*     */   {
/* 189 */     NEW, 
/*     */ 
/* 198 */     STARTING, 
/*     */ 
/* 207 */     RUNNING, 
/*     */ 
/* 216 */     STOPPING, 
/*     */ 
/* 226 */     TERMINATED, 
/*     */ 
/* 236 */     FAILED;
/*     */ 
/*     */     abstract boolean isTerminal();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.Service
 * JD-Core Version:    0.6.2
 */