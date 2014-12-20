/*     */ package com.newrelic.agent.android.instrumentation;
/*     */ 
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import com.newrelic.agent.android.util.Util;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ 
/*     */ public final class TransactionState
/*     */ {
/*  22 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private String url;
/*     */   private String httpMethod;
/*     */   private int statusCode;
/*     */   private int errorCode;
/*     */   private long bytesSent;
/*     */   private long bytesReceived;
/*     */   private long startTime;
/*     */   private long endTime;
/*     */   private String appData;
/*     */   private String carrier;
/*     */   private String wanType;
/*     */   private State state;
/*     */   private String contentType;
/*     */   private TransactionData transactionData;
/*     */ 
/*     */   public TransactionState()
/*     */   {
/*  40 */     this.startTime = System.currentTimeMillis();
/*  41 */     this.carrier = "unknown";
/*  42 */     this.wanType = "unknown";
/*  43 */     this.state = State.READY;
/*  44 */     TraceMachine.enterNetworkSegment("External/unknownhost");
/*     */   }
/*     */ 
/*     */   public void setCarrier(String carrier) {
/*  48 */     if (!isSent()) {
/*  49 */       this.carrier = carrier;
/*  50 */       TraceMachine.setCurrentTraceParam("carrier", carrier);
/*     */     }
/*     */     else {
/*  53 */       log.warning("setCarrier(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setWanType(String wanType) {
/*  58 */     if (!isSent()) {
/*  59 */       this.wanType = wanType;
/*  60 */       TraceMachine.setCurrentTraceParam("wan_type", wanType);
/*     */     }
/*     */     else {
/*  63 */       log.warning("setWanType(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setAppData(String appData) {
/*  68 */     if (!isComplete()) {
/*  69 */       this.appData = appData;
/*  70 */       TraceMachine.setCurrentTraceParam("encoded_app_data", appData);
/*     */     }
/*     */     else {
/*  73 */       log.warning("setAppData(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setUrl(String urlString) {
/*  78 */     String url = Util.sanitizeUrl(urlString);
/*  79 */     if (url == null) {
/*  80 */       return;
/*     */     }
/*  82 */     if (!isSent()) {
/*  83 */       this.url = url;
/*     */       try
/*     */       {
/*  86 */         TraceMachine.setCurrentDisplayName("External/" + new URL(url).getHost());
/*     */       } catch (MalformedURLException e) {
/*  88 */         log.error("unable to parse host name from " + url);
/*     */       }
/*  90 */       TraceMachine.setCurrentTraceParam("uri", url);
/*     */     }
/*     */     else {
/*  93 */       log.warning("setUrl(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setHttpMethod(String httpMethod) {
/*  98 */     if (!isSent()) {
/*  99 */       this.httpMethod = httpMethod;
/* 100 */       TraceMachine.setCurrentTraceParam("http_method", httpMethod);
/*     */     }
/*     */     else {
/* 103 */       log.warning("setHttpMethod(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getUrl() {
/* 108 */     return this.url;
/*     */   }
/*     */ 
/*     */   public String getHttpMethod() {
/* 112 */     return this.httpMethod;
/*     */   }
/*     */ 
/*     */   public boolean isSent() {
/* 116 */     return this.state.ordinal() >= State.SENT.ordinal();
/*     */   }
/*     */ 
/*     */   public boolean isComplete() {
/* 120 */     return this.state.ordinal() >= State.COMPLETE.ordinal();
/*     */   }
/*     */ 
/*     */   public void setStatusCode(int statusCode) {
/* 124 */     if (!isComplete()) {
/* 125 */       this.statusCode = statusCode;
/* 126 */       TraceMachine.setCurrentTraceParam("status_code", Integer.valueOf(statusCode));
/*     */     }
/*     */     else {
/* 129 */       log.warning("setStatusCode(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getStatusCode() {
/* 134 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   public void setErrorCode(int errorCode) {
/* 138 */     if (!isComplete()) {
/* 139 */       this.errorCode = errorCode;
/* 140 */       TraceMachine.setCurrentTraceParam("error_code", Integer.valueOf(errorCode));
/*     */     }
/*     */     else {
/* 143 */       if (this.transactionData != null) {
/* 144 */         this.transactionData.setErrorCode(errorCode);
/*     */       }
/* 146 */       log.warning("setErrorCode(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getErrorCode() {
/* 151 */     return this.errorCode;
/*     */   }
/*     */ 
/*     */   public void setBytesSent(long bytesSent) {
/* 155 */     if (!isComplete()) {
/* 156 */       this.bytesSent = bytesSent;
/* 157 */       TraceMachine.setCurrentTraceParam("bytes_sent", Long.valueOf(bytesSent));
/* 158 */       this.state = State.SENT;
/*     */     }
/*     */     else {
/* 161 */       log.warning("setBytesSent(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBytesReceived(long bytesReceived) {
/* 166 */     if (!isComplete()) {
/* 167 */       this.bytesReceived = bytesReceived;
/* 168 */       TraceMachine.setCurrentTraceParam("bytes_received", Long.valueOf(bytesReceived));
/*     */     }
/*     */     else {
/* 171 */       log.warning("setBytesReceived(...) called on TransactionState in " + this.state.toString() + " state");
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getBytesReceived() {
/* 176 */     return this.bytesReceived;
/*     */   }
/*     */ 
/*     */   public TransactionData end() {
/* 180 */     if (!isComplete()) {
/* 181 */       this.state = State.COMPLETE;
/* 182 */       this.endTime = System.currentTimeMillis();
/* 183 */       TraceMachine.exitMethod();
/*     */     }
/* 185 */     return toTransactionData();
/*     */   }
/*     */ 
/*     */   private TransactionData toTransactionData() {
/* 189 */     if (!isComplete()) {
/* 190 */       log.warning("toTransactionData() called on incomplete TransactionState");
/*     */     }
/*     */ 
/* 193 */     if (this.url == null) {
/* 194 */       log.error("Attempted to convert a TransactionState instance with no URL into a TransactionData");
/* 195 */       return null;
/*     */     }
/*     */ 
/* 198 */     if (this.transactionData == null) {
/* 199 */       this.transactionData = new TransactionData(this.url, this.httpMethod, this.carrier, (float)(this.endTime - this.startTime) / 1000.0F, this.statusCode, this.errorCode, this.bytesSent, this.bytesReceived, this.appData, this.wanType);
/*     */     }
/* 201 */     return this.transactionData;
/*     */   }
/*     */ 
/*     */   public String getContentType() {
/* 205 */     return this.contentType;
/*     */   }
/*     */ 
/*     */   public void setContentType(String contentType) {
/* 209 */     this.contentType = contentType;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 214 */     return "TransactionState{url='" + this.url + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", appData='" + this.appData + '\'' + ", carrier='" + this.carrier + '\'' + ", wanType='" + this.wanType + '\'' + ", state=" + this.state + ", contentType='" + this.contentType + '\'' + ", transactionData=" + this.transactionData + '}';
/*     */   }
/*     */ 
/*     */   private static enum State
/*     */   {
/*  17 */     READY, 
/*  18 */     SENT, 
/*  19 */     COMPLETE;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.TransactionState
 * JD-Core Version:    0.6.2
 */