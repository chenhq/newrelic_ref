/*    */ package com.newrelic.agent.android;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public enum FeatureFlag
/*    */ {
/*  7 */   HttpResponseBodyCapture, 
/*  8 */   CrashReporting;
/*    */ 
/*    */   public static final Set<FeatureFlag> enabledFeatures;
/*    */ 
/*    */   public static void enableFeature(FeatureFlag featureFlag)
/*    */   {
/* 19 */     enabledFeatures.add(featureFlag);
/*    */   }
/*    */ 
/*    */   public static void disableFeature(FeatureFlag featureFlag) {
/* 23 */     enabledFeatures.remove(featureFlag);
/*    */   }
/*    */ 
/*    */   public static boolean featureEnabled(FeatureFlag featureFlag) {
/* 27 */     return enabledFeatures.contains(featureFlag);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 10 */     enabledFeatures = new HashSet();
/*    */ 
/* 14 */     enableFeature(HttpResponseBodyCapture);
/* 15 */     enableFeature(CrashReporting);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.FeatureFlag
 * JD-Core Version:    0.6.2
 */