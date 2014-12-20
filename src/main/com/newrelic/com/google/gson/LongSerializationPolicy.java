/*    */ package com.newrelic.com.google.gson;
/*    */ 
/*    */ public enum LongSerializationPolicy
/*    */ {
/* 34 */   DEFAULT, 
/*    */ 
/* 45 */   STRING;
/*    */ 
/*    */   public abstract JsonElement serialize(Long paramLong);
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.LongSerializationPolicy
 * JD-Core Version:    0.6.2
 */