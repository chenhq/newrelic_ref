/*    */ package com.newrelic.agent.android.harvest.crash;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.ApplicationInformation;
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*    */ import com.newrelic.com.google.gson.JsonElement;
/*    */ import com.newrelic.com.google.gson.JsonObject;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class ApplicationInfo extends HarvestableObject
/*    */ {
/* 13 */   private String applicationName = "";
/* 14 */   private String applicationVersion = "";
/* 15 */   private String bundleId = "";
/* 16 */   private int processId = 0;
/*    */ 
/*    */   public ApplicationInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ApplicationInfo(ApplicationInformation applicationInformation) {
/* 23 */     this.applicationName = applicationInformation.getAppName();
/* 24 */     this.applicationVersion = applicationInformation.getAppVersion();
/* 25 */     this.bundleId = applicationInformation.getPackageId();
/*    */   }
/*    */ 
/*    */   public JsonObject asJsonObject()
/*    */   {
/* 30 */     JsonObject data = new JsonObject();
/*    */ 
/* 32 */     data.add("appName", new JsonPrimitive(this.applicationName));
/* 33 */     data.add("appVersion", new JsonPrimitive(this.applicationVersion));
/* 34 */     data.add("bundleId", new JsonPrimitive(this.bundleId));
/* 35 */     data.add("processId", new JsonPrimitive(Integer.valueOf(this.processId)));
/*    */ 
/* 37 */     return data;
/*    */   }
/*    */ 
/*    */   public static ApplicationInfo newFromJson(JsonObject jsonObject) {
/* 41 */     ApplicationInfo info = new ApplicationInfo();
/*    */ 
/* 43 */     info.applicationName = jsonObject.get("appName").getAsString();
/* 44 */     info.applicationVersion = jsonObject.get("appVersion").getAsString();
/* 45 */     info.bundleId = jsonObject.get("bundleId").getAsString();
/* 46 */     info.processId = jsonObject.get("processId").getAsInt();
/*    */ 
/* 48 */     return info;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.crash.ApplicationInfo
 * JD-Core Version:    0.6.2
 */