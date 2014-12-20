/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.Agent;
/*     */ import com.newrelic.agent.android.FeatureFlag;
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.measurement.http.HttpErrorMeasurement;
/*     */ import com.newrelic.agent.android.util.Encoder;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class HttpError extends HarvestableArray
/*     */ {
/*  26 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private String url;
/*     */   private int httpStatusCode;
/*     */   private long count;
/*     */   private String responseBody;
/*     */   private String stackTrace;
/*     */   private Map<String, String> params;
/*     */   private String appData;
/*     */   private String digest;
/*     */   private Long timestamp;
/*     */ 
/*     */   public HttpError()
/*     */   {
/*     */   }
/*     */ 
/*     */   public HttpError(String url, int httpStatusCode, String responseBody, String stackTrace, Map<String, String> params)
/*     */   {
/*  43 */     this.url = url;
/*  44 */     this.httpStatusCode = httpStatusCode;
/*  45 */     this.responseBody = responseBody;
/*  46 */     this.stackTrace = stackTrace;
/*  47 */     this.params = params;
/*  48 */     this.count = 1L;
/*  49 */     this.digest = computeHash();
/*     */   }
/*     */ 
/*     */   public HttpError(HttpErrorMeasurement m) {
/*  53 */     this(m.getUrl(), m.getHttpStatusCode(), m.getResponseBody(), m.getStackTrace(), m.getParams());
/*  54 */     setTimestamp(Long.valueOf(m.getStartTime()));
/*     */   }
/*     */ 
/*     */   public JsonArray asJsonArray()
/*     */   {
/*  59 */     int bodyLimit = Harvest.getHarvestConfiguration().getResponse_body_limit();
/*  60 */     JsonArray array = new JsonArray();
/*     */ 
/*  62 */     array.add(new JsonPrimitive(this.url));
/*  63 */     array.add(new JsonPrimitive(Integer.valueOf(this.httpStatusCode)));
/*  64 */     array.add(new JsonPrimitive(Long.valueOf(this.count)));
/*     */ 
/*  66 */     String body = "";
/*  67 */     if (FeatureFlag.featureEnabled(FeatureFlag.HttpResponseBodyCapture)) {
/*  68 */       body = optional(this.responseBody);
/*  69 */       if (body.length() > bodyLimit) {
/*  70 */         log.warning("HTTP Error response body is too large. Truncating to " + bodyLimit + " bytes.");
/*  71 */         body = body.substring(0, bodyLimit);
/*     */       }
/*     */     } else {
/*  74 */       log.warning("not enabled");
/*     */     }
/*     */ 
/*  77 */     array.add(new JsonPrimitive(Agent.getEncoder().encode(body.getBytes())));
/*  78 */     array.add(new JsonPrimitive(optional(this.stackTrace)));
/*     */ 
/*  80 */     JsonObject customParams = new JsonObject();
/*  81 */     if (this.params == null) {
/*  82 */       this.params = Collections.emptyMap();
/*     */     }
/*     */ 
/*  85 */     customParams.add("custom_params", HarvestableObject.fromMap(this.params).asJson());
/*     */ 
/*  87 */     array.add(customParams);
/*  88 */     array.add(new JsonPrimitive(optional(this.appData)));
/*     */ 
/*  90 */     return array;
/*     */   }
/*     */ 
/*     */   public void incrementCount()
/*     */   {
/*  97 */     this.count += 1L;
/*     */   }
/*     */ 
/*     */   public String getHash()
/*     */   {
/* 106 */     return this.digest;
/*     */   }
/*     */ 
/*     */   public void digest() {
/* 110 */     this.digest = computeHash();
/*     */   }
/*     */ 
/*     */   private String computeHash()
/*     */   {
/*     */     MessageDigest digester;
/*     */     try
/*     */     {
/* 118 */       digester = MessageDigest.getInstance("SHA-1");
/*     */     } catch (NoSuchAlgorithmException e) {
/* 120 */       log.error("Unable to initialize SHA-1 hash algorithm");
/* 121 */       return null;
/*     */     }
/*     */ 
/* 124 */     digester.update(this.url.getBytes());
/* 125 */     digester.update(ByteBuffer.allocate(8).putInt(this.httpStatusCode).array());
/*     */ 
/* 127 */     if ((this.stackTrace != null) && (this.stackTrace.length() > 0)) {
/* 128 */       digester.update(this.stackTrace.getBytes());
/*     */     }
/* 130 */     return new String(digester.digest());
/*     */   }
/*     */ 
/*     */   public void setUrl(String url) {
/* 134 */     this.url = url;
/*     */   }
/*     */ 
/*     */   public void setHttpStatusCode(int httpStatusCode) {
/* 138 */     this.httpStatusCode = httpStatusCode;
/*     */   }
/*     */ 
/*     */   public void setCount(long count) {
/* 142 */     this.count = count;
/*     */   }
/*     */ 
/*     */   public void setResponseBody(String responseBody) {
/* 146 */     this.responseBody = responseBody;
/*     */   }
/*     */ 
/*     */   public void setStackTrace(String stackTrace) {
/* 150 */     this.stackTrace = stackTrace;
/*     */   }
/*     */ 
/*     */   public void setParams(Map<String, String> params) {
/* 154 */     this.params = params;
/*     */   }
/*     */ 
/*     */   public void setAppData(String appData) {
/* 158 */     this.appData = appData;
/*     */   }
/*     */ 
/*     */   public Long getTimestamp() {
/* 162 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getParams() {
/* 166 */     return this.params;
/*     */   }
/*     */ 
/*     */   public void setTimestamp(Long timestamp) {
/* 170 */     this.timestamp = timestamp;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HttpError
 * JD-Core Version:    0.6.2
 */