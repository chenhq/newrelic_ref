/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.Agent;
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ 
/*    */ public class ConnectInformation extends HarvestableArray
/*    */ {
/*    */   private ApplicationInformation applicationInformation;
/*    */   private DeviceInformation deviceInformation;
/*    */ 
/*    */   public ConnectInformation()
/*    */   {
/* 15 */     setApplicationInformation(Agent.getApplicationInformation());
/* 16 */     setDeviceInformation(Agent.getDeviceInformation());
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 21 */     JsonArray array = new JsonArray();
/*    */ 
/* 23 */     notNull(this.applicationInformation);
/* 24 */     array.add(this.applicationInformation.asJsonArray());
/*    */ 
/* 26 */     notNull(this.deviceInformation);
/* 27 */     array.add(this.deviceInformation.asJsonArray());
/*    */ 
/* 29 */     return array;
/*    */   }
/*    */ 
/*    */   public void setApplicationInformation(ApplicationInformation applicationInformation) {
/* 33 */     this.applicationInformation = applicationInformation;
/*    */   }
/*    */ 
/*    */   public void setDeviceInformation(DeviceInformation deviceInformation) {
/* 37 */     this.deviceInformation = deviceInformation;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.ConnectInformation
 * JD-Core Version:    0.6.2
 */