/*     */ package com.newrelic.agent.android.harvest.crash;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class ThreadInfo extends HarvestableObject
/*     */ {
/*     */   private boolean crashed;
/*     */   private long threadId;
/*     */   private String threadName;
/*     */   private int threadPriority;
/*     */   private StackTraceElement[] stackTrace;
/*     */   private String state;
/*     */ 
/*     */   private ThreadInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ThreadInfo(Throwable throwable)
/*     */   {
/*  34 */     this.crashed = true;
/*  35 */     this.threadId = Thread.currentThread().getId();
/*  36 */     this.threadName = Thread.currentThread().getName();
/*  37 */     this.threadPriority = Thread.currentThread().getPriority();
/*  38 */     this.stackTrace = throwable.getStackTrace();
/*  39 */     this.state = Thread.currentThread().getState().toString();
/*     */   }
/*     */ 
/*     */   public ThreadInfo(Thread thread, StackTraceElement[] stackTrace)
/*     */   {
/*  45 */     this.crashed = false;
/*  46 */     this.threadId = thread.getId();
/*  47 */     this.threadName = thread.getName();
/*  48 */     this.threadPriority = thread.getPriority();
/*  49 */     this.stackTrace = stackTrace;
/*  50 */     this.state = thread.getState().toString();
/*     */   }
/*     */ 
/*     */   public long getThreadId() {
/*  54 */     return this.threadId;
/*     */   }
/*     */ 
/*     */   public static List<ThreadInfo> extractThreads(Throwable throwable) {
/*  58 */     List threads = new ArrayList();
/*  59 */     ThreadInfo crashedThread = new ThreadInfo(throwable);
/*  60 */     long crashedThreadId = crashedThread.getThreadId();
/*     */ 
/*  62 */     threads.add(crashedThread);
/*     */ 
/*  64 */     for (Map.Entry threadEntry : Thread.getAllStackTraces().entrySet()) {
/*  65 */       Thread thread = (Thread)threadEntry.getKey();
/*  66 */       StackTraceElement[] threadStackTrace = (StackTraceElement[])threadEntry.getValue();
/*     */ 
/*  68 */       if (thread.getId() != crashedThreadId) {
/*  69 */         threads.add(new ThreadInfo(thread, threadStackTrace));
/*     */       }
/*     */     }
/*     */ 
/*  73 */     return threads;
/*     */   }
/*     */ 
/*     */   public JsonObject asJsonObject()
/*     */   {
/*  78 */     JsonObject data = new JsonObject();
/*     */ 
/*  80 */     data.add("crashed", new JsonPrimitive(Boolean.valueOf(this.crashed)));
/*  81 */     data.add("state", new JsonPrimitive(this.state));
/*  82 */     data.add("threadNumber", new JsonPrimitive(Long.valueOf(this.threadId)));
/*  83 */     data.add("threadId", new JsonPrimitive(this.threadName));
/*  84 */     data.add("priority", new JsonPrimitive(Integer.valueOf(this.threadPriority)));
/*  85 */     data.add("stack", getStackAsJson());
/*     */ 
/*  87 */     return data;
/*     */   }
/*     */ 
/*     */   public static ThreadInfo newFromJson(JsonObject jsonObject) {
/*  91 */     ThreadInfo info = new ThreadInfo();
/*     */ 
/*  93 */     info.crashed = jsonObject.get("crashed").getAsBoolean();
/*  94 */     info.state = jsonObject.get("state").getAsString();
/*  95 */     info.threadId = jsonObject.get("threadNumber").getAsLong();
/*  96 */     info.threadName = jsonObject.get("threadId").getAsString();
/*  97 */     info.threadPriority = jsonObject.get("priority").getAsInt();
/*  98 */     info.stackTrace = stackTraceFromJson(jsonObject.get("stack").getAsJsonArray());
/*     */ 
/* 100 */     return info;
/*     */   }
/*     */ 
/*     */   public static StackTraceElement[] stackTraceFromJson(JsonArray jsonArray) {
/* 104 */     StackTraceElement[] stack = new StackTraceElement[jsonArray.size()];
/* 105 */     int i = 0;
/*     */ 
/* 107 */     for (JsonElement jsonElement : jsonArray) {
/* 108 */       String fileName = "unknown";
/*     */ 
/* 110 */       if (jsonElement.getAsJsonObject().get("fileName") != null) {
/* 111 */         fileName = jsonElement.getAsJsonObject().get("fileName").getAsString();
/*     */       }
/*     */ 
/* 114 */       String className = jsonElement.getAsJsonObject().get("className").getAsString();
/* 115 */       String methodName = jsonElement.getAsJsonObject().get("methodName").getAsString();
/* 116 */       int lineNumber = jsonElement.getAsJsonObject().get("lineNumber").getAsInt();
/*     */ 
/* 118 */       StackTraceElement stackTraceElement = new StackTraceElement(className, methodName, fileName, lineNumber);
/*     */ 
/* 120 */       stack[(i++)] = stackTraceElement;
/*     */     }
/*     */ 
/* 123 */     return stack;
/*     */   }
/*     */ 
/*     */   public static List<ThreadInfo> newListFromJson(JsonArray jsonArray)
/*     */   {
/* 128 */     List list = new ArrayList();
/*     */ 
/* 130 */     for (JsonElement jsonElement : jsonArray) {
/* 131 */       list.add(newFromJson(jsonElement.getAsJsonObject()));
/*     */     }
/*     */ 
/* 134 */     return list;
/*     */   }
/*     */ 
/*     */   private JsonArray getStackAsJson() {
/* 138 */     JsonArray data = new JsonArray();
/*     */ 
/* 140 */     for (StackTraceElement element : this.stackTrace) {
/* 141 */       JsonObject elementJson = new JsonObject();
/*     */ 
/* 143 */       if (element.getFileName() != null) {
/* 144 */         elementJson.add("fileName", new JsonPrimitive(element.getFileName()));
/*     */       }
/*     */ 
/* 147 */       elementJson.add("className", new JsonPrimitive(element.getClassName()));
/* 148 */       elementJson.add("methodName", new JsonPrimitive(element.getMethodName()));
/* 149 */       elementJson.add("lineNumber", new JsonPrimitive(Integer.valueOf(element.getLineNumber())));
/*     */ 
/* 151 */       data.add(elementJson);
/*     */     }
/*     */ 
/* 154 */     return data;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.crash.ThreadInfo
 * JD-Core Version:    0.6.2
 */