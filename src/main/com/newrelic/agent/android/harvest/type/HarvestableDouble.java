/*    */ package com.newrelic.agent.android.harvest.type;
/*    */ 
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class HarvestableDouble extends HarvestableValue
/*    */ {
/*    */   private double value;
/*    */ 
/*    */   public HarvestableDouble()
/*    */   {
/*    */   }
/*    */ 
/*    */   public HarvestableDouble(double value)
/*    */   {
/* 17 */     this();
/* 18 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public JsonPrimitive asJsonPrimitive()
/*    */   {
/* 27 */     return new JsonPrimitive(Double.valueOf(this.value));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.type.HarvestableDouble
 * JD-Core Version:    0.6.2
 */