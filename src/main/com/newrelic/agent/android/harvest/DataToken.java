/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class DataToken extends HarvestableArray
/*    */ {
/*    */   private int accountId;
/*    */   private int agentId;
/*    */ 
/*    */   public DataToken()
/*    */   {
/*    */   }
/*    */ 
/*    */   public DataToken(int accountId, int agentId)
/*    */   {
/* 20 */     this.accountId = accountId;
/* 21 */     this.agentId = agentId;
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 26 */     JsonArray array = new JsonArray();
/* 27 */     array.add(new JsonPrimitive(Integer.valueOf(this.accountId)));
/* 28 */     array.add(new JsonPrimitive(Integer.valueOf(this.agentId)));
/* 29 */     return array;
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 33 */     this.accountId = 0;
/* 34 */     this.agentId = 0;
/*    */   }
/*    */ 
/*    */   public int getAccountId() {
/* 38 */     return this.accountId;
/*    */   }
/*    */ 
/*    */   public void setAccountId(int accountId) {
/* 42 */     this.accountId = accountId;
/*    */   }
/*    */ 
/*    */   public int getAgentId() {
/* 46 */     return this.agentId;
/*    */   }
/*    */ 
/*    */   public void setAgentId(int agentId) {
/* 50 */     this.agentId = agentId;
/*    */   }
/*    */ 
/*    */   public boolean isValid() {
/* 54 */     return (this.accountId > 0) && (this.agentId > 0);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 59 */     return "DataToken{accountId=" + this.accountId + ", agentId=" + this.agentId + '}';
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.DataToken
 * JD-Core Version:    0.6.2
 */