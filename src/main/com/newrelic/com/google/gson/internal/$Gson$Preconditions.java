/*    */ package com.newrelic.com.google.gson.internal;
/*    */ 
/*    */ public final class $Gson$Preconditions
/*    */ {
/*    */   public static <T> T checkNotNull(T obj)
/*    */   {
/* 34 */     if (obj == null) {
/* 35 */       throw new NullPointerException();
/*    */     }
/* 37 */     return obj;
/*    */   }
/*    */ 
/*    */   public static void checkArgument(boolean condition) {
/* 41 */     if (!condition)
/* 42 */       throw new IllegalArgumentException();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.internal..Gson.Preconditions
 * JD-Core Version:    0.6.2
 */