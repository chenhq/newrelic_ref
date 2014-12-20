/*    */ package com.newrelic.agent.android.util;
/*    */ 
/*    */ import android.content.Context;
/*    */ import android.content.SharedPreferences;
/*    */ import android.content.SharedPreferences.Editor;
/*    */ import com.newrelic.agent.android.crashes.CrashStore;
/*    */ import com.newrelic.agent.android.harvest.crash.Crash;
/*    */ import com.newrelic.agent.android.logging.AgentLog;
/*    */ import com.newrelic.agent.android.logging.AgentLogManager;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class JsonCrashStore
/*    */   implements CrashStore
/*    */ {
/*    */   private static final String STORE_FILE = "NRCrashStore";
/* 19 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*    */   private final Context context;
/*    */ 
/*    */   public JsonCrashStore(Context context)
/*    */   {
/* 23 */     this.context = context;
/*    */   }
/*    */ 
/*    */   public void store(Crash crash)
/*    */   {
/* 28 */     synchronized (this) {
/* 29 */       SharedPreferences store = this.context.getSharedPreferences("NRCrashStore", 0);
/* 30 */       SharedPreferences.Editor editor = store.edit();
/*    */ 
/* 32 */       editor.putString(crash.getUuid().toString(), crash.toJsonString());
/* 33 */       editor.commit();
/*    */     }
/*    */   }
/*    */ 
/*    */   public List<Crash> fetchAll()
/*    */   {
/* 39 */     SharedPreferences store = this.context.getSharedPreferences("NRCrashStore", 0);
/* 40 */     List crashes = new ArrayList();
/*    */     Map crashStrings;
/* 43 */     synchronized (this) {
/* 44 */       crashStrings = store.getAll();
/*    */     }
/*    */ 
/* 47 */     for (Iterator i$ = crashStrings.values().iterator(); i$.hasNext(); ) { Object string = i$.next();
/* 48 */       if ((string instanceof String)) {
/*    */         try {
/* 50 */           crashes.add(Crash.crashFromJsonString((String)string));
/*    */         } catch (Exception e) {
/* 52 */           log.error("Exception encountered while deserializing crash", e);
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 57 */     return crashes;
/*    */   }
/*    */ 
/*    */   public int count()
/*    */   {
/* 62 */     SharedPreferences store = this.context.getSharedPreferences("NRCrashStore", 0);
/* 63 */     return store.getAll().size();
/*    */   }
/*    */ 
/*    */   public void clear()
/*    */   {
/* 68 */     synchronized (this) {
/* 69 */       SharedPreferences store = this.context.getSharedPreferences("NRCrashStore", 0);
/* 70 */       SharedPreferences.Editor editor = store.edit();
/*    */ 
/* 72 */       editor.clear();
/* 73 */       editor.commit();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void delete(Crash crash)
/*    */   {
/* 79 */     synchronized (this) {
/* 80 */       SharedPreferences store = this.context.getSharedPreferences("NRCrashStore", 0);
/* 81 */       SharedPreferences.Editor editor = store.edit();
/*    */ 
/* 83 */       editor.remove(crash.getUuid().toString());
/*    */ 
/* 85 */       editor.commit();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.util.JsonCrashStore
 * JD-Core Version:    0.6.2
 */