/*     */ package com.newrelic.agent.android.tracing;
/*     */ 
/*     */ import com.newrelic.agent.android.instrumentation.MetricCategory;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.util.Util;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class Trace
/*     */ {
/*     */   private static final String CATEGORY_PARAMETER = "category";
/*  17 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   public final UUID parentUUID;
/*  20 */   public final UUID myUUID = new UUID(Util.getRandom().nextLong(), Util.getRandom().nextLong());
/*     */ 
/*  22 */   public long entryTimestamp = 0L;
/*  23 */   public long exitTimestamp = 0L;
/*  24 */   public long exclusiveTime = 0L;
/*  25 */   public long childExclusiveTime = 0L;
/*     */   public String metricName;
/*     */   public String metricBackgroundName;
/*     */   public String displayName;
/*     */   public String scope;
/*  32 */   public long threadId = 0L;
/*     */ 
/*  35 */   public String threadName = "main";
/*     */   private volatile Map<String, Object> params;
/*     */   private List<String> rawAnnotationParams;
/*     */   private volatile Set<UUID> children;
/*  42 */   private TraceType type = TraceType.TRACE;
/*  43 */   private boolean isComplete = false;
/*     */   public TraceMachine traceMachine;
/*     */ 
/*     */   public Trace()
/*     */   {
/*  48 */     this.parentUUID = null;
/*     */   }
/*     */ 
/*     */   public Trace(String displayName, UUID parentUUID, TraceMachine traceMachine) {
/*  52 */     this.displayName = displayName;
/*  53 */     this.parentUUID = parentUUID;
/*  54 */     this.traceMachine = traceMachine;
/*     */   }
/*     */ 
/*     */   public void addChild(Trace trace)
/*     */   {
/*  59 */     if (this.children == null) {
/*  60 */       synchronized (this) {
/*  61 */         if (this.children == null) {
/*  62 */           this.children = Collections.synchronizedSet(new HashSet());
/*     */         }
/*     */       }
/*     */     }
/*  66 */     this.children.add(trace.myUUID);
/*     */   }
/*     */ 
/*     */   public Set<UUID> getChildren()
/*     */   {
/*  71 */     if (this.children == null) {
/*  72 */       synchronized (this) {
/*  73 */         if (this.children == null)
/*  74 */           this.children = Collections.synchronizedSet(new HashSet());
/*     */       }
/*     */     }
/*  77 */     return this.children;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getParams()
/*     */   {
/*  82 */     if (this.params == null) {
/*  83 */       synchronized (this) {
/*  84 */         if (this.params == null) {
/*  85 */           this.params = new ConcurrentHashMap();
/*     */         }
/*     */       }
/*     */     }
/*  89 */     return this.params;
/*     */   }
/*     */ 
/*     */   public void setAnnotationParams(List<String> rawAnnotationParams) {
/*  93 */     this.rawAnnotationParams = rawAnnotationParams;
/*     */   }
/*     */ 
/*     */   public Map<String, Object> getAnnotationParams() {
/*  97 */     HashMap annotationParams = new HashMap();
/*     */ 
/*  99 */     if ((this.rawAnnotationParams != null) && (this.rawAnnotationParams.size() > 0)) {
/* 100 */       Iterator i = this.rawAnnotationParams.iterator();
/*     */ 
/* 102 */       while (i.hasNext())
/*     */       {
/* 104 */         String paramName = (String)i.next();
/* 105 */         String paramClass = (String)i.next();
/* 106 */         String paramValue = (String)i.next();
/*     */ 
/* 108 */         Object param = createParameter(paramName, paramClass, paramValue);
/* 109 */         if (param != null) {
/* 110 */           annotationParams.put(paramName, param);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 115 */     return annotationParams;
/*     */   }
/*     */ 
/*     */   public boolean isComplete() {
/* 119 */     return this.isComplete;
/*     */   }
/*     */ 
/*     */   public void complete() throws TracingInactiveException {
/* 123 */     if (this.isComplete) {
/* 124 */       log.warning("Attempted to double complete trace " + this.myUUID.toString());
/* 125 */       return;
/*     */     }
/*     */ 
/* 129 */     if (this.exitTimestamp == 0L) {
/* 130 */       this.exitTimestamp = System.currentTimeMillis();
/*     */     }
/* 132 */     this.exclusiveTime = (getDuration() - this.childExclusiveTime);
/*     */ 
/* 134 */     this.isComplete = true;
/*     */     try
/*     */     {
/* 137 */       this.traceMachine.storeCompletedTrace(this);
/*     */     } catch (NullPointerException e) {
/* 139 */       throw new TracingInactiveException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void prepareForSerialization()
/*     */   {
/* 145 */     getParams().put("type", this.type.toString());
/*     */   }
/*     */ 
/*     */   public TraceType getType() {
/* 149 */     return this.type;
/*     */   }
/*     */ 
/*     */   public void setType(TraceType type) {
/* 153 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public long getDuration() {
/* 157 */     return this.exitTimestamp - this.entryTimestamp;
/*     */   }
/*     */ 
/*     */   public MetricCategory getCategory() {
/* 161 */     if (!getAnnotationParams().containsKey("category"))
/* 162 */       return null;
/* 163 */     Object category = getAnnotationParams().get("category");
/* 164 */     if (!(category instanceof MetricCategory)) {
/* 165 */       log.error("Category annotation parameter is not of type MetricCategory");
/* 166 */       return null;
/*     */     }
/* 168 */     return (MetricCategory)category;
/*     */   }
/*     */ 
/*     */   private static Object createParameter(String parameterName, String parameterClass, String parameterValue)
/*     */   {
/*     */     Class clazz;
/*     */     try {
/* 175 */       clazz = Class.forName(parameterClass);
/*     */     } catch (ClassNotFoundException e) {
/* 177 */       log.error("Unable to resolve parameter class in enterMethod: " + e.getMessage(), e);
/* 178 */       return null;
/*     */     }
/*     */ 
/* 182 */     if (MetricCategory.class == clazz) {
/* 183 */       return MetricCategory.valueOf(parameterValue);
/*     */     }
/*     */ 
/* 186 */     if (String.class == clazz) {
/* 187 */       return parameterValue;
/*     */     }
/*     */ 
/* 190 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.tracing.Trace
 * JD-Core Version:    0.6.2
 */