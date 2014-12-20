/*    */ package com.newrelic.agent.android.harvest.type;
/*    */ 
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonElement;
/*    */ import com.newrelic.com.google.gson.JsonObject;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public abstract interface Harvestable
/*    */ {
/*    */   public abstract Type getType();
/*    */ 
/*    */   public abstract JsonElement asJson();
/*    */ 
/*    */   public abstract JsonObject asJsonObject();
/*    */ 
/*    */   public abstract JsonArray asJsonArray();
/*    */ 
/*    */   public abstract JsonPrimitive asJsonPrimitive();
/*    */ 
/*    */   public abstract String toJsonString();
/*    */ 
/*    */   public static enum Type
/*    */   {
/* 19 */     OBJECT, 
/* 20 */     ARRAY, 
/* 21 */     VALUE;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.type.Harvestable
 * JD-Core Version:    0.6.2
 */