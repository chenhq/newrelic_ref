/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*     */ import com.newrelic.com.google.gson.Gson;
/*     */ import com.newrelic.com.google.gson.JsonArray;
/*     */ import com.newrelic.com.google.gson.JsonElement;
/*     */ import com.newrelic.com.google.gson.JsonPrimitive;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DeviceInformation extends HarvestableArray
/*     */ {
/*     */   private String osName;
/*     */   private String osVersion;
/*     */   private String osBuild;
/*     */   private String model;
/*     */   private String agentName;
/*     */   private String agentVersion;
/*     */   private String deviceId;
/*     */   private String countryCode;
/*     */   private String regionCode;
/*     */   private String manufacturer;
/*     */   private String architecture;
/*     */   private String runTime;
/*     */   private String size;
/*  27 */   private Map<String, String> misc = new HashMap();
/*     */ 
/*     */   public JsonArray asJsonArray()
/*     */   {
/*  31 */     JsonArray array = new JsonArray();
/*     */ 
/*  33 */     notEmpty(this.osName);
/*  34 */     array.add(new JsonPrimitive(this.osName));
/*     */ 
/*  36 */     notEmpty(this.osVersion);
/*  37 */     array.add(new JsonPrimitive(this.osVersion));
/*     */ 
/*  40 */     notEmpty(this.manufacturer);
/*  41 */     notEmpty(this.model);
/*  42 */     array.add(new JsonPrimitive(this.manufacturer + " " + this.model));
/*     */ 
/*  44 */     notEmpty(this.agentName);
/*  45 */     array.add(new JsonPrimitive(this.agentName));
/*     */ 
/*  47 */     notEmpty(this.agentVersion);
/*  48 */     array.add(new JsonPrimitive(this.agentVersion));
/*     */ 
/*  50 */     notEmpty(this.deviceId);
/*  51 */     array.add(new JsonPrimitive(this.deviceId));
/*     */ 
/*  54 */     array.add(new JsonPrimitive(optional(this.countryCode)));
/*  55 */     array.add(new JsonPrimitive(optional(this.regionCode)));
/*     */ 
/*  58 */     array.add(new JsonPrimitive(this.manufacturer));
/*     */ 
/*  60 */     if ((this.misc == null) || (this.misc.isEmpty())) {
/*  61 */       this.misc = Collections.emptyMap();
/*     */     }
/*  63 */     JsonElement map = new Gson().toJsonTree(this.misc, GSON_STRING_MAP_TYPE);
/*  64 */     array.add(map);
/*     */ 
/*  66 */     return array;
/*     */   }
/*     */ 
/*     */   public void setOsName(String osName) {
/*  70 */     this.osName = osName;
/*     */   }
/*     */ 
/*     */   public void setOsVersion(String osVersion) {
/*  74 */     this.osVersion = osVersion;
/*     */   }
/*     */ 
/*     */   public void setOsBuild(String osBuild) {
/*  78 */     this.osBuild = osBuild;
/*     */   }
/*     */ 
/*     */   public void setManufacturer(String manufacturer) {
/*  82 */     this.manufacturer = manufacturer;
/*     */   }
/*     */ 
/*     */   public void setModel(String model) {
/*  86 */     this.model = model;
/*     */   }
/*     */ 
/*     */   public void setCountryCode(String countryCode) {
/*  90 */     this.countryCode = countryCode;
/*     */   }
/*     */ 
/*     */   public void setRegionCode(String regionCode) {
/*  94 */     this.regionCode = regionCode;
/*     */   }
/*     */ 
/*     */   public void setAgentName(String agentName) {
/*  98 */     this.agentName = agentName;
/*     */   }
/*     */ 
/*     */   public void setAgentVersion(String agentVersion) {
/* 102 */     this.agentVersion = agentVersion;
/*     */   }
/*     */ 
/*     */   public void setDeviceId(String deviceId) {
/* 106 */     this.deviceId = deviceId;
/*     */   }
/*     */ 
/*     */   public void setArchitecture(String architecture) {
/* 110 */     this.architecture = architecture;
/*     */   }
/*     */ 
/*     */   public void setRunTime(String runTime) {
/* 114 */     this.runTime = runTime;
/*     */   }
/*     */ 
/*     */   public void setSize(String size) {
/* 118 */     this.size = size;
/* 119 */     addMisc("size", size);
/*     */   }
/*     */ 
/*     */   public void setMisc(Map<String, String> misc) {
/* 123 */     this.misc = new HashMap(misc);
/*     */   }
/*     */ 
/*     */   public void addMisc(String key, String value) {
/* 127 */     this.misc.put(key, value);
/*     */   }
/*     */ 
/*     */   public String getOsName() {
/* 131 */     return this.osName;
/*     */   }
/*     */ 
/*     */   public String getOsVersion() {
/* 135 */     return this.osVersion;
/*     */   }
/*     */ 
/*     */   public String getOsBuild() {
/* 139 */     return this.osBuild;
/*     */   }
/*     */ 
/*     */   public String getModel() {
/* 143 */     return this.model;
/*     */   }
/*     */ 
/*     */   public String getAgentName() {
/* 147 */     return this.agentName;
/*     */   }
/*     */ 
/*     */   public String getAgentVersion() {
/* 151 */     return this.agentVersion;
/*     */   }
/*     */ 
/*     */   public String getDeviceId() {
/* 155 */     return this.deviceId;
/*     */   }
/*     */ 
/*     */   public String getCountryCode() {
/* 159 */     return this.countryCode;
/*     */   }
/*     */ 
/*     */   public String getRegionCode() {
/* 163 */     return this.regionCode;
/*     */   }
/*     */ 
/*     */   public String getManufacturer() {
/* 167 */     return this.manufacturer;
/*     */   }
/*     */ 
/*     */   public String getArchitecture() {
/* 171 */     return this.architecture;
/*     */   }
/*     */ 
/*     */   public String getRunTime() {
/* 175 */     return this.runTime;
/*     */   }
/*     */ 
/*     */   public String getSize() {
/* 179 */     return this.size;
/*     */   }
/*     */ 
/*     */   public String toJsonString()
/*     */   {
/* 184 */     return "DeviceInformation{manufacturer='" + this.manufacturer + '\'' + ", osName='" + this.osName + '\'' + ", osVersion='" + this.osVersion + '\'' + ", model='" + this.model + '\'' + ", agentName='" + this.agentName + '\'' + ", agentVersion='" + this.agentVersion + '\'' + ", deviceId='" + this.deviceId + '\'' + ", countryCode='" + this.countryCode + '\'' + ", regionCode='" + this.regionCode + '\'' + '}';
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.DeviceInformation
 * JD-Core Version:    0.6.2
 */