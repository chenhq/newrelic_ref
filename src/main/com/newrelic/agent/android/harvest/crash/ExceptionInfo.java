/*    */ package com.newrelic.agent.android.harvest.crash;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableObject;
/*    */ import com.newrelic.com.google.gson.JsonElement;
/*    */ import com.newrelic.com.google.gson.JsonObject;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class ExceptionInfo extends HarvestableObject
/*    */ {
/*    */   private String className;
/*    */   private String message;
/*    */ 
/*    */   public ExceptionInfo()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ExceptionInfo(Throwable throwable)
/*    */   {
/* 21 */     this.className = throwable.getClass().getName();
/* 22 */     if (throwable.getMessage() != null)
/* 23 */       this.message = throwable.getMessage();
/*    */     else
/* 25 */       this.message = "";
/*    */   }
/*    */ 
/*    */   public String getClassName()
/*    */   {
/* 30 */     return this.className;
/*    */   }
/*    */ 
/*    */   public String getMessage() {
/* 34 */     return this.message;
/*    */   }
/*    */ 
/*    */   public JsonObject asJsonObject()
/*    */   {
/* 39 */     JsonObject data = new JsonObject();
/*    */ 
/* 41 */     data.add("name", new JsonPrimitive(this.className));
/* 42 */     data.add("cause", new JsonPrimitive(this.message));
/*    */ 
/* 44 */     return data;
/*    */   }
/*    */ 
/*    */   public static ExceptionInfo newFromJson(JsonObject jsonObject) {
/* 48 */     ExceptionInfo info = new ExceptionInfo();
/*    */ 
/* 50 */     info.className = jsonObject.get("name").getAsString();
/* 51 */     info.message = jsonObject.get("cause").getAsString();
/*    */ 
/* 53 */     return info;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.crash.ExceptionInfo
 * JD-Core Version:    0.6.2
 */