/*    */ package com.newrelic.agent.android.logging;
/*    */ 
/*    */ import android.util.Log;
/*    */ 
/*    */ public class AndroidAgentLog
/*    */   implements AgentLog
/*    */ {
/*    */   private static final String TAG = "com.newrelic.agent.android";
/*  9 */   private int level = 3;
/*    */ 
/*    */   public void debug(String message) {
/* 12 */     if (this.level == 5)
/* 13 */       Log.d("com.newrelic.agent.android", message);
/*    */   }
/*    */ 
/*    */   public void verbose(String message) {
/* 17 */     if (this.level >= 4)
/* 18 */       Log.v("com.newrelic.agent.android", message);
/*    */   }
/*    */ 
/*    */   public void info(String message) {
/* 22 */     if (this.level >= 3)
/* 23 */       Log.i("com.newrelic.agent.android", message);
/*    */   }
/*    */ 
/*    */   public void warning(String message) {
/* 27 */     if (this.level >= 2)
/* 28 */       Log.w("com.newrelic.agent.android", message);
/*    */   }
/*    */ 
/*    */   public void error(String message) {
/* 32 */     if (this.level >= 1)
/* 33 */       Log.e("com.newrelic.agent.android", message);
/*    */   }
/*    */ 
/*    */   public void error(String message, Throwable cause) {
/* 37 */     if (this.level >= 1)
/* 38 */       Log.e("com.newrelic.agent.android", message, cause);
/*    */   }
/*    */ 
/*    */   public int getLevel() {
/* 42 */     return this.level;
/*    */   }
/*    */ 
/*    */   public void setLevel(int level) {
/* 46 */     if ((level <= 5) && (level >= 1))
/* 47 */       this.level = level;
/*    */     else
/* 49 */       throw new IllegalArgumentException("Log level is not between ERROR and DEBUG");
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.logging.AndroidAgentLog
 * JD-Core Version:    0.6.2
 */