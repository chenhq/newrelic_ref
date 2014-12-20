/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ 
/*     */ public class HttpTransaction extends HarvestableArray
/*     */ {
/*     */   private String url;
/*     */   private String httpMethod;
/*     */   private String carrier;
/*     */   private String wanType;
/*     */   private double totalTime;
/*     */   private int statusCode;
/*     */   private int errorCode;
/*     */   private long bytesSent;
/*     */   private long bytesReceived;
/*     */   private String appData;
/*     */   private Long timestamp;
/*     */ 
/*     */   public JsonArray asJsonArray()
/*     */   {
/*  27 */     JsonArray array = new JsonArray();
/*  28 */     array.add(new JsonPrimitive(this.url));
/*  29 */     array.add(new JsonPrimitive(this.carrier));
/*  30 */     array.add(new JsonPrimitive(Double.valueOf(this.totalTime)));
/*  31 */     array.add(new JsonPrimitive(Integer.valueOf(this.statusCode)));
/*  32 */     array.add(new JsonPrimitive(Integer.valueOf(this.errorCode)));
/*  33 */     array.add(new JsonPrimitive(Long.valueOf(this.bytesSent)));
/*  34 */     array.add(new JsonPrimitive(Long.valueOf(this.bytesReceived)));
/*  35 */     array.add(this.appData == null ? null : new JsonPrimitive(this.appData));
/*  36 */     array.add(new JsonPrimitive(this.wanType));
/*  37 */     array.add(new JsonPrimitive(this.httpMethod));
/*  38 */     return array;
/*     */   }
/*     */ 
/*     */   public void setUrl(String url) {
/*  42 */     this.url = url;
/*     */   }
/*     */ 
/*     */   public void setHttpMethod(String httpMethod) {
/*  46 */     this.httpMethod = httpMethod;
/*     */   }
/*     */ 
/*     */   public void setCarrier(String carrier) {
/*  50 */     this.carrier = carrier;
/*     */   }
/*     */ 
/*     */   public void setWanType(String wanType) {
/*  54 */     this.wanType = wanType;
/*     */   }
/*     */ 
/*     */   public void setTotalTime(double totalTime) {
/*  58 */     this.totalTime = totalTime;
/*     */   }
/*     */ 
/*     */   public void setStatusCode(int statusCode) {
/*  62 */     this.statusCode = statusCode;
/*     */   }
/*     */ 
/*     */   public void setErrorCode(int errorCode) {
/*  66 */     this.errorCode = errorCode;
/*     */   }
/*     */ 
/*     */   public void setBytesSent(long bytesSent) {
/*  70 */     this.bytesSent = bytesSent;
/*     */   }
/*     */ 
/*     */   public void setBytesReceived(long bytesReceived) {
/*  74 */     this.bytesReceived = bytesReceived;
/*     */   }
/*     */ 
/*     */   public void setAppData(String appData) {
/*  78 */     this.appData = appData;
/*     */   }
/*     */ 
/*     */   public Long getTimestamp() {
/*  82 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   public void setTimestamp(Long timestamp) {
/*  86 */     this.timestamp = timestamp;
/*     */   }
/*     */ 
/*     */   public String getUrl() {
/*  90 */     return this.url;
/*     */   }
/*     */ 
/*     */   public String getHttpMethod() {
/*  94 */     return this.httpMethod;
/*     */   }
/*     */ 
/*     */   public String getCarrier() {
/*  98 */     return this.carrier;
/*     */   }
/*     */ 
/*     */   public double getTotalTime() {
/* 102 */     return this.totalTime;
/*     */   }
/*     */ 
/*     */   public int getStatusCode() {
/* 106 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   public int getErrorCode() {
/* 110 */     return this.errorCode;
/*     */   }
/*     */ 
/*     */   public long getBytesSent() {
/* 114 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   public long getBytesReceived() {
/* 118 */     return this.bytesReceived;
/*     */   }
/*     */ 
/*     */   public String getAppData() {
/* 122 */     return this.appData;
/*     */   }
/*     */ 
/*     */   public String getWanType() {
/* 126 */     return this.wanType;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 131 */     return "HttpTransaction{url='" + this.url + '\'' + ", carrier='" + this.carrier + '\'' + ", wanType='" + this.wanType + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", totalTime=" + this.totalTime + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", appData='" + this.appData + '\'' + ", timestamp=" + this.timestamp + '}';
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HttpTransaction
 * JD-Core Version:    0.6.2
 */