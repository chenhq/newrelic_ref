/*    */ package com.newrelic.agent.android.harvest.type;
/*    */ 
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ 
/*    */ public abstract class HarvestableArray extends BaseHarvestable
/*    */ {
/*    */   public HarvestableArray()
/*    */   {
/* 14 */     super(Harvestable.Type.ARRAY);
/*    */   }
/*    */ 
/*    */   public abstract JsonArray asJsonArray();
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.type.HarvestableArray
 * JD-Core Version:    0.6.2
 */