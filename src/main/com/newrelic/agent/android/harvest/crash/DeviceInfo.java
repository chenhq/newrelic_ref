/*     */ package com.newrelic.agent.android.harvest.crash;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.DeviceInformation;
/*     */ import com.newrelic.agent.android.harvest.EnvironmentInformation;
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonObject;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ 
/*     */ public class DeviceInfo extends HarvestableObject
/*     */ {
/*     */   private long memoryUsage;
/*     */   private int orientation;
/*     */   private String networkStatus;
/*     */   private long[] diskAvailable;
/*     */   private String OSVersion;
/*     */   private String deviceName;
/*     */   private String OSBuild;
/*     */   private String architecture;
/*     */   private String modelNumber;
/*     */   private String screenResolution;
/*     */   private String deviceUuid;
/*     */   private String runTime;
/*     */ 
/*     */   public DeviceInfo()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DeviceInfo(DeviceInformation devInfo, EnvironmentInformation envInfo)
/*     */   {
/*  33 */     this.memoryUsage = envInfo.getMemoryUsage();
/*  34 */     this.orientation = envInfo.getOrientation();
/*  35 */     this.networkStatus = envInfo.getNetworkStatus();
/*  36 */     this.diskAvailable = envInfo.getDiskAvailable();
/*  37 */     this.OSVersion = devInfo.getOsVersion();
/*  38 */     this.deviceName = devInfo.getManufacturer();
/*  39 */     this.OSBuild = devInfo.getOsBuild();
/*  40 */     this.architecture = devInfo.getArchitecture();
/*  41 */     this.modelNumber = devInfo.getModel();
/*  42 */     this.screenResolution = devInfo.getSize();
/*  43 */     this.deviceUuid = devInfo.getDeviceId();
/*  44 */     this.runTime = devInfo.getRunTime();
/*     */   }
/*     */ 
/*     */   public JsonObject asJsonObject()
/*     */   {
/*  49 */     JsonObject data = new JsonObject();
/*     */ 
/*  51 */     data.add("memoryUsage", new JsonPrimitive(Long.valueOf(this.memoryUsage)));
/*  52 */     data.add("orientation", new JsonPrimitive(Integer.valueOf(this.orientation)));
/*  53 */     data.add("networkStatus", new JsonPrimitive(this.networkStatus));
/*  54 */     data.add("diskAvailable", getDiskAvailableAsJson());
/*  55 */     data.add("osVersion", new JsonPrimitive(this.OSVersion));
/*  56 */     data.add("deviceName", new JsonPrimitive(this.deviceName));
/*  57 */     data.add("osBuild", new JsonPrimitive(this.OSBuild));
/*  58 */     data.add("architecture", new JsonPrimitive(this.architecture));
/*  59 */     data.add("runTime", new JsonPrimitive(this.runTime));
/*  60 */     data.add("modelNumber", new JsonPrimitive(this.modelNumber));
/*  61 */     data.add("screenResolution", new JsonPrimitive(this.screenResolution));
/*  62 */     data.add("deviceUuid", new JsonPrimitive(this.deviceUuid));
/*     */ 
/*  64 */     return data;
/*     */   }
/*     */ 
/*     */   public static DeviceInfo newFromJson(JsonObject jsonObject) {
/*  68 */     DeviceInfo info = new DeviceInfo();
/*     */ 
/*  70 */     info.memoryUsage = jsonObject.get("memoryUsage").getAsLong();
/*  71 */     info.orientation = jsonObject.get("orientation").getAsInt();
/*  72 */     info.networkStatus = jsonObject.get("networkStatus").getAsString();
/*  73 */     info.diskAvailable = longArrayFromJsonArray(jsonObject.get("diskAvailable").getAsJsonArray());
/*  74 */     info.OSVersion = jsonObject.get("osVersion").getAsString();
/*  75 */     info.deviceName = jsonObject.get("deviceName").getAsString();
/*  76 */     info.OSBuild = jsonObject.get("osBuild").getAsString();
/*  77 */     info.architecture = jsonObject.get("architecture").getAsString();
/*  78 */     info.runTime = jsonObject.get("runTime").getAsString();
/*  79 */     info.modelNumber = jsonObject.get("modelNumber").getAsString();
/*  80 */     info.screenResolution = jsonObject.get("screenResolution").getAsString();
/*  81 */     info.deviceUuid = jsonObject.get("deviceUuid").getAsString();
/*     */ 
/*  83 */     return info;
/*     */   }
/*     */ 
/*     */   private static long[] longArrayFromJsonArray(JsonArray jsonArray)
/*     */   {
/*  88 */     long[] array = new long[jsonArray.size()];
/*  89 */     int i = 0;
/*     */ 
/*  91 */     for (JsonElement jsonElement : jsonArray) {
/*  92 */       array[(i++)] = jsonElement.getAsLong();
/*     */     }
/*     */ 
/*  95 */     return array;
/*     */   }
/*     */ 
/*     */   private JsonArray getDiskAvailableAsJson() {
/*  99 */     JsonArray data = new JsonArray();
/*     */ 
/* 101 */     for (long value : this.diskAvailable) {
/* 102 */       data.add(new JsonPrimitive(Long.valueOf(value)));
/*     */     }
/*     */ 
/* 105 */     return data;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.crash.DeviceInfo
 * JD-Core Version:    0.6.2
 */