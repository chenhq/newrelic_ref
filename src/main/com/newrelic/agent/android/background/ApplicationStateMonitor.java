/*     */ package com.newrelic.agent.android.background;
/*     */ 
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.util.NamedThreadFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class ApplicationStateMonitor
/*     */   implements Runnable
/*     */ {
/*  13 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*  19 */   private long count = 0L;
/*     */ 
/*  21 */   private long snoozeStartTime = 0L;
/*  22 */   private final Object snoozeLock = new Object();
/*     */   private final int activitySnoozeTimeInMilliseconds;
/*  25 */   private final ArrayList<ApplicationStateListener> applicationStateListeners = new ArrayList();
/*     */ 
/*  27 */   private boolean foregrounded = true;
/*  28 */   private final Object foregroundLock = new Object();
/*     */   private static ApplicationStateMonitor instance;
/*     */ 
/*     */   private ApplicationStateMonitor()
/*     */   {
/*  33 */     this(5, 5, TimeUnit.SECONDS, 5000);
/*     */   }
/*     */ 
/*     */   ApplicationStateMonitor(int initialDelay, int period, TimeUnit timeUnit, int snoozeTimeInMilliseconds) {
/*  37 */     ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("AppStateMon"));
/*  38 */     this.activitySnoozeTimeInMilliseconds = snoozeTimeInMilliseconds;
/*  39 */     executor.scheduleAtFixedRate(this, initialDelay, period, timeUnit);
/*  40 */     log.info("Application state monitor has started");
/*     */   }
/*     */ 
/*     */   public static ApplicationStateMonitor getInstance() {
/*  44 */     if (instance == null) {
/*  45 */       instance = new ApplicationStateMonitor();
/*     */     }
/*  47 */     return instance;
/*     */   }
/*     */ 
/*     */   public void addApplicationStateListener(ApplicationStateListener listener) {
/*  51 */     synchronized (this.applicationStateListeners) {
/*  52 */       this.applicationStateListeners.add(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeApplicationStateListener(ApplicationStateListener listener) {
/*  57 */     synchronized (this.applicationStateListeners) {
/*  58 */       this.applicationStateListeners.remove(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  64 */     synchronized (this.foregroundLock)
/*     */     {
/*  68 */       if ((this.foregrounded) && (getSnoozeTime() >= this.activitySnoozeTimeInMilliseconds)) {
/*  69 */         notifyApplicationInBackground();
/*  70 */         this.foregrounded = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void uiHidden() {
/*  76 */     synchronized (this.foregroundLock) {
/*  77 */       if (this.foregrounded) {
/*  78 */         log.info("UI has become hidden (app backgrounded)");
/*  79 */         notifyApplicationInBackground();
/*  80 */         this.foregrounded = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void activityStopped()
/*     */   {
/*  89 */     synchronized (this.foregroundLock) {
/*  90 */       synchronized (this.snoozeLock) {
/*  91 */         this.count -= 1L;
/*  92 */         if (this.count == 0L)
/*  93 */           this.snoozeStartTime = System.currentTimeMillis();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void activityStarted()
/*     */   {
/* 103 */     synchronized (this.foregroundLock) {
/* 104 */       synchronized (this.snoozeLock) {
/* 105 */         this.count += 1L;
/* 106 */         if (this.count == 1L)
/*     */         {
/* 110 */           this.snoozeStartTime = 0L;
/*     */         }
/*     */       }
/*     */ 
/* 114 */       if (!this.foregrounded) {
/* 115 */         log.verbose("Application appears to be in the foreground");
/* 116 */         notifyApplicationInForeground();
/* 117 */         this.foregrounded = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notifyApplicationInBackground() {
/* 123 */     log.verbose("Application appears to have gone to the background");
/*     */     ArrayList listeners;
/* 125 */     synchronized (this.applicationStateListeners) {
/* 126 */       listeners = new ArrayList(this.applicationStateListeners);
/*     */     }
/* 128 */     ApplicationStateEvent e = new ApplicationStateEvent(this);
/* 129 */     for (ApplicationStateListener listener : listeners)
/* 130 */       listener.applicationBackgrounded(e);
/*     */   }
/*     */ 
/*     */   private void notifyApplicationInForeground()
/*     */   {
/*     */     ArrayList listeners;
/* 136 */     synchronized (this.applicationStateListeners) {
/* 137 */       listeners = new ArrayList(this.applicationStateListeners);
/*     */     }
/* 139 */     ApplicationStateEvent e = new ApplicationStateEvent(this);
/* 140 */     for (ApplicationStateListener listener : listeners)
/* 141 */       listener.applicationForegrounded(e);
/*     */   }
/*     */ 
/*     */   private long getSnoozeTime()
/*     */   {
/* 153 */     synchronized (this.foregroundLock) {
/* 154 */       synchronized (this.snoozeLock) {
/* 155 */         if (this.snoozeStartTime == 0L) return 0L;
/* 156 */         return System.currentTimeMillis() - this.snoozeStartTime;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.background.ApplicationStateMonitor
 * JD-Core Version:    0.6.2
 */