/*    */ package com.newrelic.com.google.gson;
/*    */ 
/*    */ public final class JsonNull extends JsonElement
/*    */ {
/* 32 */   public static final JsonNull INSTANCE = new JsonNull();
/*    */ 
/*    */   JsonNull deepCopy()
/*    */   {
/* 45 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 53 */     return JsonNull.class.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 61 */     return (this == other) || ((other instanceof JsonNull));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.JsonNull
 * JD-Core Version:    0.6.2
 */