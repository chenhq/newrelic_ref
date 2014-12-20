/*    */ package com.newrelic.agent.android.harvest;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class Events extends HarvestableArray
/*    */ {
/* 10 */   private final Collection<Event> events = new ArrayList();
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 14 */     JsonArray array = new JsonArray();
/* 15 */     for (Event event : this.events) {
/* 16 */       array.add(event.asJson());
/*    */     }
/* 18 */     return array;
/*    */   }
/*    */ 
/*    */   public void addEvent(Event event) {
/* 22 */     this.events.add(event);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.Events
 * JD-Core Version:    0.6.2
 */