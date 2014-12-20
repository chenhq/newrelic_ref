/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class HttpTransactions extends HarvestableArray
/*    */ {
/* 13 */   private final Collection<HttpTransaction> httpTransactions = new ArrayList();
/*    */ 
/*    */   public synchronized void add(HttpTransaction httpTransaction) {
/* 16 */     this.httpTransactions.add(httpTransaction);
/*    */   }
/*    */ 
/*    */   public synchronized void remove(HttpTransaction transaction) {
/* 20 */     this.httpTransactions.remove(transaction);
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 24 */     this.httpTransactions.clear();
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 29 */     JsonArray array = new JsonArray();
/* 30 */     for (HttpTransaction transaction : this.httpTransactions) {
/* 31 */       array.add(transaction.asJson());
/*    */     }
/* 33 */     return array;
/*    */   }
/*    */ 
/*    */   public Collection<HttpTransaction> getHttpTransactions() {
/* 37 */     return this.httpTransactions;
/*    */   }
/*    */ 
/*    */   public int count() {
/* 41 */     return this.httpTransactions.size();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 46 */     return "HttpTransactions{httpTransactions=" + this.httpTransactions + '}';
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HttpTransactions
 * JD-Core Version:    0.6.2
 */