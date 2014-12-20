/*    */ package com.newrelic.agent.android.harvest.type;
/*    */ 
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public abstract class HarvestableValue extends BaseHarvestable
/*    */ {
/*    */   public HarvestableValue()
/*    */   {
/* 11 */     super(Harvestable.Type.VALUE);
/*    */   }
/*    */ 
/*    */   public abstract JsonPrimitive asJsonPrimitive();
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.type.HarvestableValue
 * JD-Core Version:    0.6.2
 */