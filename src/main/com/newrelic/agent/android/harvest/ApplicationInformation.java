/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class ApplicationInformation extends HarvestableArray
/*    */ {
/*    */   private String appName;
/*    */   private String appVersion;
/*    */   private String packageId;
/*    */ 
/*    */   public ApplicationInformation()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ApplicationInformation(String appName, String appVersion, String packageId)
/*    */   {
/* 21 */     this();
/* 22 */     this.appName = appName;
/* 23 */     this.appVersion = appVersion;
/* 24 */     this.packageId = packageId;
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 29 */     JsonArray array = new JsonArray();
/*    */ 
/* 31 */     notEmpty(this.appName);
/* 32 */     array.add(new JsonPrimitive(this.appName));
/* 33 */     notEmpty(this.appVersion);
/* 34 */     array.add(new JsonPrimitive(this.appVersion));
/* 35 */     notEmpty(this.packageId);
/* 36 */     array.add(new JsonPrimitive(this.packageId));
/*    */ 
/* 38 */     return array;
/*    */   }
/*    */ 
/*    */   public void setAppName(String appName) {
/* 42 */     this.appName = appName;
/*    */   }
/*    */ 
/*    */   public String getAppName() {
/* 46 */     return this.appName;
/*    */   }
/*    */ 
/*    */   public void setAppVersion(String appVersion) {
/* 50 */     this.appVersion = appVersion;
/*    */   }
/*    */ 
/*    */   public String getAppVersion() {
/* 54 */     return this.appVersion;
/*    */   }
/*    */ 
/*    */   public void setPackageId(String packageId) {
/* 58 */     this.packageId = packageId;
/*    */   }
/*    */ 
/*    */   public String getPackageId() {
/* 62 */     return this.packageId;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.ApplicationInformation
 * JD-Core Version:    0.6.2
 */