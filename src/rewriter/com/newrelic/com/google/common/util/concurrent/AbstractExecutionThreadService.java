/*     */ package com.newrelic.com.google.common.util.concurrent;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Supplier;
/*     */ import com.newrelic.com.google.common.base.Throwables;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractExecutionThreadService
/*     */   implements Service
/*     */ {
/*  40 */   private static final Logger logger = Logger.getLogger(AbstractExecutionThreadService.class.getName());
/*     */ 
/*  44 */   private final Service delegate = new AbstractService() {
/*     */     protected final void doStart() {
/*  46 */       Executor executor = MoreExecutors.renamingDecorator(AbstractExecutionThreadService.this.executor(), new Supplier() {
/*     */         public String get() {
/*  48 */           return AbstractExecutionThreadService.this.serviceName();
/*     */         }
/*     */       });
/*  51 */       executor.execute(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/*  55 */             AbstractExecutionThreadService.this.startUp();
/*  56 */             AbstractExecutionThreadService.1.this.notifyStarted();
/*     */ 
/*  58 */             if (AbstractExecutionThreadService.1.this.isRunning()) {
/*     */               try {
/*  60 */                 AbstractExecutionThreadService.this.run();
/*     */               } catch (Throwable t) {
/*     */                 try {
/*  63 */                   AbstractExecutionThreadService.this.shutDown();
/*     */                 } catch (Exception ignored) {
/*  65 */                   AbstractExecutionThreadService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
/*     */                 }
/*     */ 
/*  69 */                 throw t;
/*     */               }
/*     */             }
/*     */ 
/*  73 */             AbstractExecutionThreadService.this.shutDown();
/*  74 */             AbstractExecutionThreadService.1.this.notifyStopped();
/*     */           } catch (Throwable t) {
/*  76 */             AbstractExecutionThreadService.1.this.notifyFailed(t);
/*  77 */             throw Throwables.propagate(t);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     protected void doStop() {
/*  84 */       AbstractExecutionThreadService.this.triggerShutdown();
/*     */     }
/*  44 */   };
/*     */ 
/*     */   protected void startUp()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract void run()
/*     */     throws Exception;
/*     */ 
/*     */   protected void shutDown()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void triggerShutdown()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected Executor executor()
/*     */   {
/* 143 */     return new Executor()
/*     */     {
/*     */       public void execute(Runnable command) {
/* 146 */         MoreExecutors.newThread(AbstractExecutionThreadService.this.serviceName(), command).start();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 152 */     return serviceName() + " [" + state() + "]";
/*     */   }
/*     */ 
/*     */   public final boolean isRunning() {
/* 156 */     return this.delegate.isRunning();
/*     */   }
/*     */ 
/*     */   public final Service.State state() {
/* 160 */     return this.delegate.state();
/*     */   }
/*     */ 
/*     */   public final void addListener(Service.Listener listener, Executor executor)
/*     */   {
/* 167 */     this.delegate.addListener(listener, executor);
/*     */   }
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 174 */     return this.delegate.failureCause();
/*     */   }
/*     */ 
/*     */   public final Service startAsync()
/*     */   {
/* 181 */     this.delegate.startAsync();
/* 182 */     return this;
/*     */   }
/*     */ 
/*     */   public final Service stopAsync()
/*     */   {
/* 189 */     this.delegate.stopAsync();
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */   public final void awaitRunning()
/*     */   {
/* 197 */     this.delegate.awaitRunning();
/*     */   }
/*     */ 
/*     */   public final void awaitRunning(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 204 */     this.delegate.awaitRunning(timeout, unit);
/*     */   }
/*     */ 
/*     */   public final void awaitTerminated()
/*     */   {
/* 211 */     this.delegate.awaitTerminated();
/*     */   }
/*     */ 
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 218 */     this.delegate.awaitTerminated(timeout, unit);
/*     */   }
/*     */ 
/*     */   protected String serviceName()
/*     */   {
/* 230 */     return getClass().getSimpleName();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.AbstractExecutionThreadService
 * JD-Core Version:    0.6.2
 */