/*    */ package com.newrelic.agent.android.activity;
/*    */ 
/*    */ public class NamedActivity extends BaseMeasuredActivity
/*    */ {
/*    */   public NamedActivity(String activityName)
/*    */   {
/* 13 */     setName(activityName);
/* 14 */     setAutoInstrumented(false);
/*    */   }
/*    */ 
/*    */   public void rename(String activityName) {
/* 18 */     setName(activityName);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.activity.NamedActivity
 * JD-Core Version:    0.6.2
 */