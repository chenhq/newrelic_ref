/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ public class AgentHealthException extends HarvestableArray
/*     */ {
/*     */   private String exceptionClass;
/*     */   private String message;
/*     */   private String threadName;
/*     */   private StackTraceElement[] stackTrace;
/*  21 */   private final AtomicLong count = new AtomicLong(1L);
/*     */   private Map<String, String> extras;
/*     */ 
/*     */   public AgentHealthException(Exception e)
/*     */   {
/*  25 */     this(e, Thread.currentThread().getName());
/*     */   }
/*     */ 
/*     */   public AgentHealthException(Exception e, String threadName) {
/*  29 */     this(e.getClass().getName(), e.getMessage(), threadName, e.getStackTrace());
/*     */   }
/*     */ 
/*     */   public AgentHealthException(String exceptionClass, String message, String threadName, StackTraceElement[] stackTrace) {
/*  33 */     this(exceptionClass, message, threadName, stackTrace, null);
/*     */   }
/*     */ 
/*     */   public AgentHealthException(String exceptionClass, String message, String threadName, StackTraceElement[] stackTrace, Map<String, String> extras)
/*     */   {
/*  39 */     this.exceptionClass = exceptionClass;
/*  40 */     this.message = message;
/*  41 */     this.threadName = threadName;
/*  42 */     this.stackTrace = stackTrace;
/*  43 */     this.extras = extras;
/*     */   }
/*     */ 
/*     */   public void increment() {
/*  47 */     this.count.getAndIncrement();
/*     */   }
/*     */ 
/*     */   public void increment(long i) {
/*  51 */     this.count.getAndAdd(i);
/*     */   }
/*     */ 
/*     */   public String getExceptionClass() {
/*  55 */     return this.exceptionClass;
/*     */   }
/*     */ 
/*     */   public String getMessage() {
/*  59 */     return this.message;
/*     */   }
/*     */ 
/*     */   public String getThreadName() {
/*  63 */     return this.threadName;
/*     */   }
/*     */ 
/*     */   public StackTraceElement[] getStackTrace() {
/*  67 */     return this.stackTrace;
/*     */   }
/*     */ 
/*     */   public long getCount() {
/*  71 */     return this.count.get();
/*     */   }
/*     */ 
/*     */   public Map<String, String> getExtras() {
/*  75 */     return this.extras;
/*     */   }
/*     */ 
/*     */   public String getSourceClass() {
/*  79 */     return this.stackTrace[0].getClassName();
/*     */   }
/*     */ 
/*     */   public String getSourceMethod() {
/*  83 */     return this.stackTrace[0].getMethodName();
/*     */   }
/*     */ 
/*     */   public JsonArray asJsonArray()
/*     */   {
/*  88 */     JsonArray data = new JsonArray();
/*     */ 
/*  90 */     data.add(new JsonPrimitive(this.exceptionClass));
/*  91 */     data.add(new JsonPrimitive(this.message == null ? "" : this.message));
/*  92 */     data.add(new JsonPrimitive(this.threadName));
/*  93 */     data.add(stackTraceToJson());
/*  94 */     data.add(new JsonPrimitive(Long.valueOf(this.count.get())));
/*  95 */     data.add(extrasToJson());
/*     */ 
/*  97 */     return data;
/*     */   }
/*     */ 
/*     */   private JsonArray stackTraceToJson() {
/* 101 */     JsonArray stack = new JsonArray();
/*     */ 
/* 103 */     for (StackTraceElement element : this.stackTrace) {
/* 104 */       stack.add(new JsonPrimitive(element.toString()));
/*     */     }
/*     */ 
/* 107 */     return stack;
/*     */   }
/*     */ 
/*     */   private JsonObject extrasToJson() {
/* 111 */     JsonObject data = new JsonObject();
/*     */ 
/* 113 */     if (this.extras != null) {
/* 114 */       for (Map.Entry entry : this.extras.entrySet()) {
/* 115 */         data.add((String)entry.getKey(), new JsonPrimitive((String)entry.getValue()));
/*     */       }
/*     */     }
/*     */ 
/* 119 */     return data;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.AgentHealthException
 * JD-Core Version:    0.6.2
 */