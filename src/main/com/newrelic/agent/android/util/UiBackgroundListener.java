/*    */ package com.newrelic.agent.android.util;
/*    */ 
/*    */ import android.content.ComponentCallbacks2;
/*    */ import android.content.res.Configuration;
/*    */ import com.newrelic.agent.android.background.ApplicationStateMonitor;
/*    */ 
/*    */ public class UiBackgroundListener
/*    */   implements ComponentCallbacks2
/*    */ {
/*    */   public void onConfigurationChanged(Configuration newConfig)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onLowMemory()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onTrimMemory(int level)
/*    */   {
/* 21 */     if (level == 20)
/* 22 */       ApplicationStateMonitor.getInstance().uiHidden();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.util.UiBackgroundListener
 * JD-Core Version:    0.6.2
 */