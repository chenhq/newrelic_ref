/*    */ package com.newrelic.agent.android.harvest.type;
/*    */ 
/*    */ import com.newrelic.com.google.gson.Gson;
/*    */ import com.newrelic.com.google.gson.JsonObject;
/*    */ import java.util.Map;
/*    */ 
/*    */ public abstract class HarvestableObject extends BaseHarvestable
/*    */ {
/*    */   public static HarvestableObject fromMap(Map<String, String> map)
/*    */   {
/* 19 */     return new HarvestableObject()
/*    */     {
/*    */       public JsonObject asJsonObject() {
/* 22 */         return (JsonObject)new Gson().toJsonTree(this.val$map, GSON_STRING_MAP_TYPE);
/*    */       }
/*    */     };
/*    */   }
/*    */ 
/*    */   public HarvestableObject() {
/* 28 */     super(Harvestable.Type.OBJECT);
/*    */   }
/*    */ 
/*    */   public abstract JsonObject asJsonObject();
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.type.HarvestableObject
 * JD-Core Version:    0.6.2
 */