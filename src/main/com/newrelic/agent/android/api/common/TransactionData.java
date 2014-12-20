/*     */ package com.newrelic.agent.android.api.common;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TransactionData
/*     */ {
/*     */   private final long timestamp;
/*     */   private final String url;
/*     */   private final String httpMethod;
/*     */   private final String carrier;
/*     */   private final float time;
/*     */   private final int statusCode;
/*     */   private int errorCode;
/*  14 */   private final Object errorCodeLock = new Object();
/*     */   private final long bytesSent;
/*     */   private final long bytesReceived;
/*     */   private final String appData;
/*     */   private final String wanType;
/*     */ 
/*     */   public TransactionData(String url, String httpMethod, String carrier, float time, int statusCode, int errorCode, long bytesSent, long bytesReceived, String appData, String wanType)
/*     */   {
/*  21 */     int endPos = url.indexOf('?');
/*  22 */     if (endPos < 0) {
/*  23 */       endPos = url.indexOf(';');
/*  24 */       if (endPos < 0) {
/*  25 */         endPos = url.length();
/*     */       }
/*     */     }
/*  28 */     String trimmedUrl = url.substring(0, endPos);
/*     */ 
/*  30 */     this.url = trimmedUrl;
/*  31 */     this.httpMethod = httpMethod;
/*  32 */     this.carrier = carrier;
/*  33 */     this.time = time;
/*  34 */     this.statusCode = statusCode;
/*  35 */     this.errorCode = errorCode;
/*  36 */     this.bytesSent = bytesSent;
/*  37 */     this.bytesReceived = bytesReceived;
/*  38 */     this.appData = appData;
/*  39 */     this.wanType = wanType;
/*     */ 
/*  41 */     this.timestamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public String getUrl() {
/*  45 */     return this.url;
/*     */   }
/*     */ 
/*     */   public String getHttpMethod() {
/*  49 */     return this.httpMethod;
/*     */   }
/*     */ 
/*     */   public String getCarrier() {
/*  53 */     return this.carrier;
/*     */   }
/*     */ 
/*     */   public int getStatusCode() {
/*  57 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   public int getErrorCode() {
/*  61 */     synchronized (this.errorCodeLock) {
/*  62 */       return this.errorCode;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setErrorCode(int errorCode) {
/*  67 */     synchronized (this.errorCodeLock) {
/*  68 */       this.errorCode = errorCode;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getBytesSent() {
/*  73 */     return this.bytesSent;
/*     */   }
/*     */ 
/*     */   public long getBytesReceived() {
/*  77 */     return this.bytesReceived;
/*     */   }
/*     */ 
/*     */   public String getAppData() {
/*  81 */     return this.appData;
/*     */   }
/*     */ 
/*     */   public String getWanType() {
/*  85 */     return this.wanType;
/*     */   }
/*     */ 
/*     */   public List<Object> asList() {
/*  89 */     ArrayList r = new ArrayList();
/*  90 */     r.add(this.url);
/*  91 */     r.add(this.carrier);
/*  92 */     r.add(Float.valueOf(this.time));
/*  93 */     r.add(Integer.valueOf(this.statusCode));
/*  94 */     r.add(Integer.valueOf(this.errorCode));
/*  95 */     r.add(Long.valueOf(this.bytesSent));
/*  96 */     r.add(Long.valueOf(this.bytesReceived));
/*  97 */     r.add(this.appData);
/*  98 */     return r;
/*     */   }
/*     */ 
/*     */   public long getTimestamp() {
/* 102 */     return this.timestamp;
/*     */   }
/*     */ 
/*     */   public float getTime() {
/* 106 */     return this.time;
/*     */   }
/*     */ 
/*     */   public TransactionData clone() {
/* 110 */     return new TransactionData(this.url, this.httpMethod, this.carrier, this.time, this.statusCode, this.errorCode, this.bytesSent, this.bytesReceived, this.appData, this.wanType);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 115 */     return "TransactionData{timestamp=" + this.timestamp + ", url='" + this.url + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", carrier='" + this.carrier + '\'' + ", time=" + this.time + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", errorCodeLock=" + this.errorCodeLock + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", appData='" + this.appData + '\'' + ", wanType='" + this.wanType + '\'' + '}';
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.api.common.TransactionData
 * JD-Core Version:    0.6.2
 */