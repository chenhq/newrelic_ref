/*     */ package com.newrelic.agent.android.tracing;
/*     */ 
/*     */ import com.newrelic.agent.android.Measurements;
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.api.v2.TraceFieldInterface;
/*     */ import com.newrelic.agent.android.api.v2.TraceMachineInterface;
/*     */ import com.newrelic.agent.android.harvest.ActivityHistory;
/*     */ import com.newrelic.agent.android.harvest.ActivitySighting;
/*     */ import com.newrelic.agent.android.harvest.AgentHealth;
/*     */ import com.newrelic.agent.android.harvest.Harvest;
/*     */ import com.newrelic.agent.android.harvest.HarvestAdapter;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.stats.StatsEngine;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ public class TraceMachine extends HarvestAdapter
/*     */ {
/*     */   public static final String NR_TRACE_FIELD = "_nr_trace";
/*     */   public static final String NR_TRACE_TYPE = "Lcom/newrelic/agent/android/tracing/Trace;";
/*     */   public static final String ACTIVITY_METRIC_PREFIX = "Mobile/Activity/Name/";
/*     */   public static final String ACTIVITY_BACKGROUND_METRIC_PREFIX = "Mobile/Activity/Background/Name/";
/*     */   public static final String ACTIVTY_DISPLAY_NAME_PREFIX = "Display ";
/*  26 */   public static final AtomicBoolean disabled = new AtomicBoolean(false);
/*     */ 
/*  28 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   public static final int HEALTHY_TRACE_TIMEOUT = 500;
/*     */   public static final int UNHEALTHY_TRACE_TIMEOUT = 60000;
/*  35 */   private static final Collection<TraceLifecycleAware> traceListeners = new CopyOnWriteArrayList();
/*     */ 
/*  37 */   private static final ThreadLocal<Trace> threadLocalTrace = new ThreadLocal();
/*  38 */   private static final ThreadLocal<TraceStack> threadLocalTraceStack = new ThreadLocal();
/*     */ 
/*  40 */   private static final List<ActivitySighting> activityHistory = new CopyOnWriteArrayList();
/*     */ 
/*  42 */   private static TraceMachine traceMachine = null;
/*     */   private static TraceMachineInterface traceMachineInterface;
/*     */   private ActivityTrace activityTrace;
/*     */ 
/*     */   protected TraceMachine(Trace rootTrace)
/*     */   {
/*  50 */     this.activityTrace = new ActivityTrace(rootTrace);
/*     */ 
/*  53 */     Harvest.addHarvestListener(this);
/*     */   }
/*     */ 
/*     */   public static TraceMachine getTraceMachine() {
/*  57 */     return traceMachine;
/*     */   }
/*     */ 
/*     */   public static void addTraceListener(TraceLifecycleAware listener) {
/*  61 */     traceListeners.add(listener);
/*     */   }
/*     */ 
/*     */   public static void removeTraceListener(TraceLifecycleAware listener) {
/*  65 */     traceListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public static void setTraceMachineInterface(TraceMachineInterface traceMachineInterface) {
/*  69 */     traceMachineInterface = traceMachineInterface;
/*     */   }
/*     */ 
/*     */   public static void startTracing(String name) {
/*  73 */     startTracing(name, false);
/*     */   }
/*     */ 
/*     */   public static void startTracing(String name, boolean customName) {
/*     */     try {
/*  78 */       if ((disabled.get()) || (!Harvest.shouldCollectActivityTraces())) {
/*  79 */         return;
/*     */       }
/*     */ 
/*  82 */       if (isTracingActive()) {
/*  83 */         traceMachine.completeActivityTrace();
/*     */       }
/*     */ 
/*  87 */       threadLocalTrace.remove();
/*  88 */       threadLocalTraceStack.set(new TraceStack(null));
/*     */ 
/*  91 */       Trace rootTrace = new Trace();
/*  92 */       if (customName)
/*  93 */         rootTrace.displayName = name;
/*     */       else {
/*  95 */         rootTrace.displayName = formatActivityDisplayName(name);
/*     */       }
/*     */ 
/*  99 */       rootTrace.metricName = formatActivityMetricName(rootTrace.displayName);
/* 100 */       rootTrace.metricBackgroundName = formatActivityBackgroundMetricName(rootTrace.displayName);
/*     */ 
/* 102 */       rootTrace.entryTimestamp = System.currentTimeMillis();
/*     */ 
/* 104 */       if (log.getLevel() == 5) {
/* 105 */         log.debug("Started trace of " + name + ":" + rootTrace.myUUID.toString());
/*     */       }
/* 107 */       traceMachine = new TraceMachine(rootTrace);
/* 108 */       rootTrace.traceMachine = traceMachine;
/*     */ 
/* 111 */       pushTraceContext(rootTrace);
/*     */ 
/* 114 */       traceMachine.activityTrace.previousActivity = getLastActivitySighting();
/* 115 */       activityHistory.add(new ActivitySighting(rootTrace.entryTimestamp, rootTrace.displayName));
/*     */ 
/* 117 */       for (TraceLifecycleAware listener : traceListeners)
/* 118 */         listener.onTraceStart(traceMachine.activityTrace);
/*     */     }
/*     */     catch (Exception e) {
/* 121 */       log.error("Caught error while initializing TraceMachine, shutting it down", e);
/*     */ 
/* 123 */       AgentHealth.noticeException(e);
/*     */ 
/* 125 */       traceMachine = null;
/* 126 */       threadLocalTrace.remove();
/* 127 */       threadLocalTraceStack.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void haltTracing()
/*     */   {
/* 133 */     if (isTracingInactive()) {
/* 134 */       return;
/*     */     }
/* 136 */     TraceMachine finishedMachine = traceMachine;
/* 137 */     traceMachine = null;
/*     */ 
/* 139 */     finishedMachine.activityTrace.discard();
/* 140 */     endLastActivitySighting();
/*     */ 
/* 143 */     Harvest.removeHarvestListener(finishedMachine);
/*     */ 
/* 145 */     threadLocalTrace.remove();
/* 146 */     threadLocalTraceStack.remove();
/*     */   }
/*     */ 
/*     */   public static void endTrace() {
/* 150 */     traceMachine.completeActivityTrace();
/*     */   }
/*     */ 
/*     */   public static void endTrace(String id) {
/*     */     try {
/* 155 */       if (getActivityTrace().rootTrace.myUUID.toString().equals(id))
/* 156 */         traceMachine.completeActivityTrace();
/*     */     } catch (TracingInactiveException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String formatActivityMetricName(String name) {
/* 162 */     return "Mobile/Activity/Name/" + name;
/*     */   }
/*     */ 
/*     */   public static String formatActivityBackgroundMetricName(String name) {
/* 166 */     return "Mobile/Activity/Background/Name/" + name;
/*     */   }
/*     */ 
/*     */   public static String formatActivityDisplayName(String name) {
/* 170 */     return "Display " + name;
/*     */   }
/*     */ 
/*     */   private static Trace registerNewTrace(String name) throws TracingInactiveException {
/* 174 */     if (isTracingInactive()) {
/* 175 */       log.debug("Tried to register a new trace but tracing is inactive!");
/* 176 */       throw new TracingInactiveException();
/*     */     }
/*     */ 
/* 179 */     Trace parentTrace = getCurrentTrace();
/*     */ 
/* 182 */     Trace childTrace = new Trace(name, parentTrace.myUUID, traceMachine);
/*     */     try {
/* 184 */       traceMachine.activityTrace.addTrace(childTrace);
/*     */     } catch (Exception e) {
/* 186 */       throw new TracingInactiveException();
/*     */     }
/*     */ 
/* 189 */     if (log.getLevel() == 5) {
/* 190 */       log.debug("Registering trace of " + name + " with parent " + parentTrace.displayName);
/*     */     }
/* 192 */     parentTrace.addChild(childTrace);
/*     */ 
/* 197 */     return childTrace;
/*     */   }
/*     */ 
/*     */   public void completeActivityTrace() {
/* 201 */     if (isTracingInactive()) {
/* 202 */       return;
/*     */     }
/* 204 */     TraceMachine finishedMachine = traceMachine;
/* 205 */     traceMachine = null;
/*     */ 
/* 207 */     finishedMachine.activityTrace.complete();
/* 208 */     endLastActivitySighting();
/*     */ 
/* 210 */     for (TraceLifecycleAware listener : traceListeners) {
/* 211 */       listener.onTraceComplete(finishedMachine.activityTrace);
/*     */     }
/*     */ 
/* 215 */     Harvest.removeHarvestListener(finishedMachine);
/*     */   }
/*     */ 
/*     */   public static void enterNetworkSegment(String name) {
/*     */     try {
/* 220 */       if (isTracingInactive()) {
/* 221 */         return;
/*     */       }
/*     */ 
/* 225 */       Trace currentTrace = getCurrentTrace();
/* 226 */       if (currentTrace.getType() == TraceType.NETWORK) {
/* 227 */         exitMethod();
/*     */       }
/*     */ 
/* 230 */       enterMethod(null, name, null);
/*     */ 
/* 232 */       Trace networkTrace = getCurrentTrace();
/* 233 */       networkTrace.setType(TraceType.NETWORK);
/*     */     } catch (TracingInactiveException e) {
/*     */     }
/*     */     catch (Exception e) {
/* 237 */       log.error("Caught error while calling enterNetworkSegment()", e);
/* 238 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void enterMethod(String name)
/*     */   {
/* 244 */     enterMethod(null, name, null);
/*     */   }
/*     */ 
/*     */   public static void enterMethod(String name, ArrayList<String> annotationParams) {
/* 248 */     enterMethod(null, name, annotationParams);
/*     */   }
/*     */ 
/*     */   public static void enterMethod(Trace trace, String name, ArrayList<String> annotationParams)
/*     */   {
/*     */     try {
/* 254 */       if (isTracingInactive()) {
/* 255 */         return;
/*     */       }
/* 257 */       long currentTime = System.currentTimeMillis();
/* 258 */       long lastUpdatedAt = traceMachine.activityTrace.lastUpdatedAt;
/* 259 */       long inception = traceMachine.activityTrace.startedAt;
/*     */ 
/* 261 */       if ((lastUpdatedAt + 500L < currentTime) && (!traceMachine.activityTrace.hasMissingChildren())) {
/* 262 */         log.debug("Completing activity trace after hitting healthy timeout (500ms)");
/* 263 */         traceMachine.completeActivityTrace();
/* 264 */         return;
/*     */       }
/*     */ 
/* 267 */       if (inception + 60000L < currentTime) {
/* 268 */         log.debug("Completing activity trace after hitting unhealthy timeout (60000ms)");
/* 269 */         traceMachine.completeActivityTrace();
/* 270 */         return;
/*     */       }
/*     */ 
/* 273 */       loadTraceContext(trace);
/*     */ 
/* 275 */       Trace childTrace = registerNewTrace(name);
/*     */ 
/* 277 */       pushTraceContext(childTrace);
/*     */ 
/* 279 */       childTrace.scope = getCurrentScope();
/*     */ 
/* 281 */       childTrace.setAnnotationParams(annotationParams);
/*     */ 
/* 284 */       for (TraceLifecycleAware listener : traceListeners) {
/* 285 */         listener.onEnterMethod();
/*     */       }
/*     */ 
/* 288 */       childTrace.entryTimestamp = System.currentTimeMillis();
/*     */     } catch (TracingInactiveException e) {
/*     */     }
/*     */     catch (Exception e) {
/* 292 */       log.error("Caught error while calling enterMethod()", e);
/* 293 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void exitMethod()
/*     */   {
/*     */     try {
/* 300 */       if (isTracingInactive()) {
/* 301 */         return;
/*     */       }
/* 303 */       Trace trace = (Trace)threadLocalTrace.get();
/*     */ 
/* 305 */       if (trace == null) {
/* 306 */         log.debug("threadLocalTrace is null");
/* 307 */         return;
/*     */       }
/*     */ 
/* 311 */       trace.exitTimestamp = System.currentTimeMillis();
/*     */ 
/* 314 */       if ((trace.threadId == 0L) && (traceMachineInterface != null)) {
/* 315 */         trace.threadId = traceMachineInterface.getCurrentThreadId();
/* 316 */         trace.threadName = traceMachineInterface.getCurrentThreadName();
/*     */       }
/*     */ 
/* 320 */       for (TraceLifecycleAware listener : traceListeners) {
/* 321 */         listener.onExitMethod();
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 326 */         trace.complete();
/*     */       } catch (TracingInactiveException e) {
/* 328 */         threadLocalTrace.remove();
/* 329 */         threadLocalTraceStack.remove();
/* 330 */         if (trace.getType() == TraceType.TRACE) {
/* 331 */           TaskQueue.queue(trace);
/*     */         }
/* 333 */         return;
/*     */       }
/*     */ 
/* 337 */       ((TraceStack)threadLocalTraceStack.get()).pop();
/*     */ 
/* 341 */       if (((TraceStack)threadLocalTraceStack.get()).empty()) {
/* 342 */         threadLocalTrace.set(null);
/*     */       } else {
/* 344 */         Trace parentTrace = (Trace)((TraceStack)threadLocalTraceStack.get()).peek();
/* 345 */         threadLocalTrace.set(parentTrace);
/*     */ 
/* 348 */         parentTrace.childExclusiveTime += trace.getDuration();
/*     */       }
/*     */ 
/* 351 */       if (trace.getType() == TraceType.TRACE)
/* 352 */         TaskQueue.queue(trace);
/*     */     } catch (Exception e) {
/* 354 */       log.error("Caught error while calling exitMethod()", e);
/* 355 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void pushTraceContext(Trace trace) {
/* 360 */     if ((isTracingInactive()) || (trace == null)) {
/* 361 */       return;
/*     */     }
/* 363 */     TraceStack traceStack = (TraceStack)threadLocalTraceStack.get();
/*     */ 
/* 365 */     if (traceStack.empty())
/* 366 */       traceStack.push(trace);
/* 367 */     else if (traceStack.peek() != trace) {
/* 368 */       traceStack.push(trace);
/*     */     }
/*     */ 
/* 371 */     threadLocalTrace.set(trace);
/*     */   }
/*     */ 
/*     */   private static void loadTraceContext(Trace trace) {
/* 375 */     if (isTracingInactive()) {
/* 376 */       return;
/*     */     }
/*     */ 
/* 379 */     if (threadLocalTrace.get() == null) {
/* 380 */       threadLocalTrace.set(trace);
/*     */ 
/* 382 */       threadLocalTraceStack.set(new TraceStack(null));
/*     */ 
/* 385 */       if (trace == null) {
/* 386 */         return;
/*     */       }
/* 388 */       ((TraceStack)threadLocalTraceStack.get()).push(trace);
/*     */     }
/* 392 */     else if (trace == null)
/*     */     {
/* 396 */       if (((TraceStack)threadLocalTraceStack.get()).isEmpty()) {
/* 397 */         if (log.getLevel() == 5)
/* 398 */           log.debug("No context to load!");
/* 399 */         threadLocalTrace.set(null);
/* 400 */         return;
/*     */       }
/*     */ 
/* 403 */       trace = (Trace)((TraceStack)threadLocalTraceStack.get()).peek();
/* 404 */       threadLocalTrace.set(trace);
/*     */     }
/*     */ 
/* 410 */     if (log.getLevel() == 5)
/* 411 */       log.debug("Trace " + trace.myUUID.toString() + " is now active");
/*     */   }
/*     */ 
/*     */   public static void unloadTraceContext(Object object)
/*     */   {
/*     */     try
/*     */     {
/* 419 */       if (isTracingInactive()) {
/* 420 */         return;
/*     */       }
/* 422 */       if ((traceMachineInterface != null) && (traceMachineInterface.isUIThread())) {
/* 423 */         return;
/*     */       }
/* 425 */       if ((threadLocalTrace.get() != null) && 
/* 426 */         (log.getLevel() == 5)) {
/* 427 */         log.debug("Trace " + ((Trace)threadLocalTrace.get()).myUUID.toString() + " is now inactive");
/*     */       }
/* 429 */       threadLocalTrace.remove();
/* 430 */       threadLocalTraceStack.remove();
/*     */ 
/* 433 */       ((TraceFieldInterface)object)._nr_setTrace(null);
/*     */     } catch (Exception e) {
/* 435 */       log.error("Caught error while calling unloadTraceContext()", e);
/* 436 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Trace getCurrentTrace() throws TracingInactiveException {
/* 441 */     if (isTracingInactive()) {
/* 442 */       throw new TracingInactiveException();
/*     */     }
/*     */ 
/* 445 */     Trace trace = (Trace)threadLocalTrace.get();
/* 446 */     if (trace != null) {
/* 447 */       return trace;
/*     */     }
/* 449 */     return getRootTrace();
/*     */   }
/*     */ 
/*     */   public static Map<String, Object> getCurrentTraceParams() throws TracingInactiveException
/*     */   {
/* 454 */     return getCurrentTrace().getParams();
/*     */   }
/*     */ 
/*     */   public static void setCurrentTraceParam(String key, Object value) {
/* 458 */     if (isTracingInactive())
/* 459 */       return;
/*     */     try
/*     */     {
/* 462 */       getCurrentTrace().getParams().put(key, value);
/*     */     }
/*     */     catch (TracingInactiveException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setCurrentDisplayName(String name) {
/* 469 */     if (isTracingInactive())
/* 470 */       return;
/*     */     try
/*     */     {
/* 473 */       getCurrentTrace().displayName = name;
/*     */     }
/*     */     catch (TracingInactiveException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setRootDisplayName(String name)
/*     */   {
/* 482 */     if (isTracingInactive())
/* 483 */       return;
/*     */     try
/*     */     {
/* 486 */       Trace rootTrace = getRootTrace();
/* 487 */       Measurements.renameActivity(rootTrace.displayName, name);
/* 488 */       renameActivityHistory(rootTrace.displayName, name);
/* 489 */       rootTrace.metricName = formatActivityMetricName(name);
/* 490 */       rootTrace.metricBackgroundName = formatActivityBackgroundMetricName(name);
/* 491 */       rootTrace.displayName = name;
/*     */ 
/* 493 */       Trace currentTrace = getCurrentTrace();
/* 494 */       currentTrace.scope = getCurrentScope();
/*     */     }
/*     */     catch (TracingInactiveException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void renameActivityHistory(String oldName, String newName) {
/* 501 */     for (ActivitySighting activitySighting : activityHistory)
/* 502 */       if (activitySighting.getName().equals(oldName))
/* 503 */         activitySighting.setName(newName);
/*     */   }
/*     */ 
/*     */   public static String getCurrentScope()
/*     */   {
/*     */     try {
/* 509 */       if (isTracingInactive()) {
/* 510 */         return null;
/*     */       }
/*     */ 
/* 513 */       if ((traceMachineInterface == null) || (traceMachineInterface.isUIThread())) {
/* 514 */         return traceMachine.activityTrace.rootTrace.metricName;
/*     */       }
/*     */ 
/* 517 */       return traceMachine.activityTrace.rootTrace.metricBackgroundName;
/*     */     } catch (Exception e) {
/* 519 */       log.error("Caught error while calling getCurrentScope()", e);
/* 520 */       AgentHealth.noticeException(e);
/* 521 */     }return null;
/*     */   }
/*     */ 
/*     */   public static boolean isTracingActive()
/*     */   {
/* 526 */     return traceMachine != null;
/*     */   }
/*     */ 
/*     */   public static boolean isTracingInactive() {
/* 530 */     return traceMachine == null;
/*     */   }
/*     */ 
/*     */   public void storeCompletedTrace(Trace trace) {
/*     */     try {
/* 535 */       if (isTracingInactive()) {
/* 536 */         log.debug("Attempted to store a completed trace with no trace machine!");
/* 537 */         return;
/*     */       }
/*     */ 
/* 540 */       this.activityTrace.addCompletedTrace(trace);
/*     */     } catch (Exception e) {
/* 542 */       log.error("Caught error while calling storeCompletedTrace()", e);
/* 543 */       AgentHealth.noticeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Trace getRootTrace() throws TracingInactiveException {
/*     */     try {
/* 549 */       return traceMachine.activityTrace.rootTrace; } catch (NullPointerException e) {
/*     */     }
/* 551 */     throw new TracingInactiveException();
/*     */   }
/*     */ 
/*     */   public static ActivityTrace getActivityTrace() throws TracingInactiveException
/*     */   {
/*     */     try {
/* 557 */       return traceMachine.activityTrace; } catch (NullPointerException e) {
/*     */     }
/* 559 */     throw new TracingInactiveException();
/*     */   }
/*     */ 
/*     */   public static ActivityHistory getActivityHistory()
/*     */   {
/* 564 */     return new ActivityHistory(activityHistory);
/*     */   }
/*     */ 
/*     */   public static ActivitySighting getLastActivitySighting() {
/* 568 */     if (activityHistory.isEmpty()) {
/* 569 */       return null;
/*     */     }
/* 571 */     return (ActivitySighting)activityHistory.get(activityHistory.size() - 1);
/*     */   }
/*     */ 
/*     */   public static void endLastActivitySighting() {
/* 575 */     ActivitySighting activitySighting = getLastActivitySighting();
/*     */ 
/* 577 */     if (activitySighting != null)
/* 578 */       activitySighting.end(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   public static void clearActivityHistory() {
/* 582 */     activityHistory.clear();
/*     */   }
/*     */ 
/*     */   public void onHarvestBefore()
/*     */   {
/* 587 */     if (isTracingActive()) {
/* 588 */       long currentTime = System.currentTimeMillis();
/* 589 */       long lastUpdatedAt = traceMachine.activityTrace.lastUpdatedAt;
/* 590 */       long inception = traceMachine.activityTrace.startedAt;
/*     */ 
/* 592 */       if ((lastUpdatedAt + 500L < currentTime) && (!traceMachine.activityTrace.hasMissingChildren())) {
/* 593 */         log.debug("Completing activity trace after hitting healthy timeout (500ms)");
/* 594 */         completeActivityTrace();
/* 595 */         StatsEngine.get().inc("Supportability/AgentHealth/HealthyActivityTraces");
/* 596 */         return;
/*     */       }
/*     */ 
/* 599 */       if (inception + 60000L < currentTime) {
/* 600 */         log.debug("Completing activity trace after hitting unhealthy timeout (60000ms)");
/* 601 */         completeActivityTrace();
/* 602 */         StatsEngine.get().inc("Supportability/AgentHealth/UnhealthyActivityTraces");
/* 603 */         return;
/*     */       }
/*     */     } else {
/* 606 */       log.debug("TraceMachine is inactive");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onHarvestSendFailed()
/*     */   {
/*     */     try {
/* 613 */       traceMachine.activityTrace.incrementReportAttemptCount();
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class TraceStack extends Stack<Trace>
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.tracing.TraceMachine
 * JD-Core Version:    0.6.2
 */