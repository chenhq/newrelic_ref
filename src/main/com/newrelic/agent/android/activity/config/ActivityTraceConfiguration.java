/*    */ package com.newrelic.agent.android.activity.config;
/*    */ 
/*    */ public class ActivityTraceConfiguration
/*    */ {
/*    */   private int maxTotalTraceCount;
/*    */ 
/*    */   public static ActivityTraceConfiguration defaultActivityTraceConfiguration()
/*    */   {
/*  7 */     ActivityTraceConfiguration configuration = new ActivityTraceConfiguration();
/*  8 */     configuration.setMaxTotalTraceCount(1);
/*  9 */     return configuration;
/*    */   }
/*    */ 
/*    */   public int getMaxTotalTraceCount() {
/* 13 */     return this.maxTotalTraceCount;
/*    */   }
/*    */ 
/*    */   public void setMaxTotalTraceCount(int maxTotalTraceCount) {
/* 17 */     this.maxTotalTraceCount = maxTotalTraceCount;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 22 */     return "ActivityTraceConfiguration{maxTotalTraceCount=" + this.maxTotalTraceCount + '}';
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.activity.config.ActivityTraceConfiguration
 * JD-Core Version:    0.6.2
 */