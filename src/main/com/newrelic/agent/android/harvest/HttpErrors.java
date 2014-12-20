/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class HttpErrors extends HarvestableArray
/*    */ {
/* 13 */   private final Collection<HttpError> httpErrors = new ArrayList();
/*    */ 
/*    */   public void addHttpError(HttpError httpError)
/*    */   {
/* 23 */     synchronized (httpError) {
/* 24 */       for (HttpError error : this.httpErrors) {
/* 25 */         if (httpError.getHash().equals(error.getHash())) {
/* 26 */           error.incrementCount();
/* 27 */           return;
/*    */         }
/*    */       }
/*    */ 
/* 31 */       this.httpErrors.add(httpError);
/*    */     }
/*    */   }
/*    */ 
/*    */   public synchronized void removeHttpError(HttpError error) {
/* 36 */     this.httpErrors.remove(error);
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 40 */     this.httpErrors.clear();
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 45 */     JsonArray array = new JsonArray();
/* 46 */     for (HttpError httpError : this.httpErrors) {
/* 47 */       array.add(httpError.asJson());
/*    */     }
/* 49 */     return array;
/*    */   }
/*    */ 
/*    */   public Collection<HttpError> getHttpErrors() {
/* 53 */     return this.httpErrors;
/*    */   }
/*    */ 
/*    */   public int count() {
/* 57 */     return this.httpErrors.size();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HttpErrors
 * JD-Core Version:    0.6.2
 */