/*    */ package com.newrelic.agent.android.instrumentation;
/*    */ 
/*    */ public class Location
/*    */ {
/*    */   private final String countryCode;
/*    */   private final String region;
/*    */ 
/*    */   public Location(String countryCode, String region)
/*    */   {
/*  8 */     if ((countryCode == null) || (region == null)) {
/*  9 */       throw new IllegalArgumentException("Country code and region must not be null.");
/*    */     }
/* 11 */     this.countryCode = countryCode;
/* 12 */     this.region = region;
/*    */   }
/*    */ 
/*    */   public String getCountryCode() {
/* 16 */     return this.countryCode;
/*    */   }
/*    */ 
/*    */   public String getRegion() {
/* 20 */     return this.region;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.Location
 * JD-Core Version:    0.6.2
 */