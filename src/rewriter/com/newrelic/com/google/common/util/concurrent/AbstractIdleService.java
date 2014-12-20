/*     */ package com.newrelic.com.google.common.util.concurrent;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Supplier;
/*     */ import com.newrelic.com.google.common.base.Throwables;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractIdleService
/*     */   implements Service
/*     */ {
/*  41 */   private final Supplier<String> threadNameSupplier = new Supplier() {
/*     */     public String get() {
/*  43 */       return AbstractIdleService.this.serviceName() + " " + AbstractIdleService.this.state();
/*     */     }
/*  41 */   };
/*     */ 
/*  48 */   private final Service delegate = new AbstractService() {
/*     */     protected final void doStart() {
/*  50 */       MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/*  54 */             AbstractIdleService.this.startUp();
/*  55 */             AbstractIdleService.2.this.notifyStarted();
/*     */           } catch (Throwable t) {
/*  57 */             AbstractIdleService.2.this.notifyFailed(t);
/*  58 */             throw Throwables.propagate(t);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     protected final void doStop() {
/*  65 */       MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/*  69 */             AbstractIdleService.this.shutDown();
/*  70 */             AbstractIdleService.2.this.notifyStopped();
/*     */           } catch (Throwable t) {
/*  72 */             AbstractIdleService.2.this.notifyFailed(t);
/*  73 */             throw Throwables.propagate(t);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*  48 */   };
/*     */ 
/*     */   protected abstract void startUp()
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void shutDown()
/*     */     throws Exception;
/*     */ 
/*     */   protected Executor executor()
/*     */   {
/*  98 */     return new Executor() {
/*     */       public void execute(Runnable command) {
/* 100 */         MoreExecutors.newThread((String)AbstractIdleService.this.threadNameSupplier.get(), command).start();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 106 */     return serviceName() + " [" + state() + "]";
/*     */   }
/*     */ 
/*     */   public final boolean isRunning() {
/* 110 */     return this.delegate.isRunning();
/*     */   }
/*     */ 
/*     */   public final Service.State state() {
/* 114 */     return this.delegate.state();
/*     */   }
/*     */ 
/*     */   public final void addListener(Service.Listener listener, Executor executor)
/*     */   {
/* 121 */     this.delegate.addListener(listener, executor);
/*     */   }
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 128 */     return this.delegate.failureCause();
/*     */   }
/*     */ 
/*     */   public final Service startAsync()
/*     */   {
/* 135 */     this.delegate.startAsync();
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   public final Service stopAsync()
/*     */   {
/* 143 */     this.delegate.stopAsync();
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */   public final void awaitRunning()
/*     */   {
/* 151 */     this.delegate.awaitRunning();
/*     */   }
/*     */ 
/*     */   public final void awaitRunning(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 158 */     this.delegate.awaitRunning(timeout, unit);
/*     */   }
/*     */ 
/*     */   public final void awaitTerminated()
/*     */   {
/* 165 */     this.delegate.awaitTerminated();
/*     */   }
/*     */ 
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 172 */     this.delegate.awaitTerminated(timeout, unit);
/*     */   }
/*     */ 
/*     */   protected String serviceName()
/*     */   {
/* 182 */     return getClass().getSimpleName();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.AbstractIdleService
 * JD-Core Version:    0.6.2
 */