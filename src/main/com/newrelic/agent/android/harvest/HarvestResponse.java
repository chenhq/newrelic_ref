/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ public class HarvestResponse
/*     */ {
/*     */   private static final String DISABLE_STRING = "DISABLE_NEW_RELIC";
/*     */   private int statusCode;
/*     */   private String responseBody;
/*     */   private long responseTime;
/*     */ 
/*     */   public Code getResponseCode()
/*     */   {
/*  50 */     if (isOK()) {
/*  51 */       return Code.OK;
/*     */     }
/*  53 */     for (Code code : Code.values()) {
/*  54 */       if (code.getStatusCode() == this.statusCode) {
/*  55 */         return code;
/*     */       }
/*     */     }
/*  58 */     return Code.UNKNOWN;
/*     */   }
/*     */ 
/*     */   public boolean isDisableCommand()
/*     */   {
/*  68 */     return (Code.FORBIDDEN == getResponseCode()) && ("DISABLE_NEW_RELIC".equals(getResponseBody()));
/*     */   }
/*     */ 
/*     */   public boolean isError()
/*     */   {
/*  77 */     return this.statusCode >= 400;
/*     */   }
/*     */ 
/*     */   public boolean isUnknown() {
/*  81 */     return getResponseCode() == Code.UNKNOWN;
/*     */   }
/*     */ 
/*     */   public boolean isOK()
/*     */   {
/*  90 */     return !isError();
/*     */   }
/*     */ 
/*     */   public int getStatusCode() {
/*  94 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   public void setStatusCode(int statusCode) {
/*  98 */     this.statusCode = statusCode;
/*     */   }
/*     */ 
/*     */   public String getResponseBody() {
/* 102 */     return this.responseBody;
/*     */   }
/*     */ 
/*     */   public void setResponseBody(String responseBody) {
/* 106 */     this.responseBody = responseBody;
/*     */   }
/*     */ 
/*     */   public long getResponseTime() {
/* 110 */     return this.responseTime;
/*     */   }
/*     */ 
/*     */   public void setResponseTime(long responseTime) {
/* 114 */     this.responseTime = responseTime;
/*     */   }
/*     */ 
/*     */   public static enum Code
/*     */   {
/*  13 */     OK(200), 
/*  14 */     UNAUTHORIZED(401), 
/*  15 */     FORBIDDEN(403), 
/*  16 */     ENTITY_TOO_LARGE(413), 
/*  17 */     INVALID_AGENT_ID(450), 
/*  18 */     UNSUPPORTED_MEDIA_TYPE(415), 
/*  19 */     INTERNAL_SERVER_ERROR(500), 
/*  20 */     UNKNOWN(-1);
/*     */ 
/*     */     int statusCode;
/*     */ 
/*     */     private Code(int statusCode) {
/*  25 */       this.statusCode = statusCode;
/*     */     }
/*     */ 
/*     */     public int getStatusCode() {
/*  29 */       return this.statusCode;
/*     */     }
/*     */ 
/*     */     public boolean isError() {
/*  33 */       return this != OK;
/*     */     }
/*     */ 
/*     */     public boolean isOK() {
/*  37 */       return !isError();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestResponse
 * JD-Core Version:    0.6.2
 */