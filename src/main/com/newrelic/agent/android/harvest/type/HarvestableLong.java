/*    */ package com.newrelic.agent.android.harvest.type;
/*    */ 
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class HarvestableLong extends HarvestableValue
/*    */ {
/*    */   private long value;
/*    */ 
/*    */   public HarvestableLong()
/*    */   {
/*    */   }
/*    */ 
/*    */   public HarvestableLong(long value)
/*    */   {
/* 17 */     this();
/* 18 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public JsonPrimitive asJsonPrimitive()
/*    */   {
/* 27 */     return new JsonPrimitive(Long.valueOf(this.value));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.type.HarvestableLong
 * JD-Core Version:    0.6.2
 */