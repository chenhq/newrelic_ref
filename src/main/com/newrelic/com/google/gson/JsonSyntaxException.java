/*    */ package com.newrelic.com.google.gson;
/*    */ 
/*    */ public final class JsonSyntaxException extends JsonParseException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public JsonSyntaxException(String msg)
/*    */   {
/* 30 */     super(msg);
/*    */   }
/*    */ 
/*    */   public JsonSyntaxException(String msg, Throwable cause) {
/* 34 */     super(msg, cause);
/*    */   }
/*    */ 
/*    */   public JsonSyntaxException(Throwable cause)
/*    */   {
/* 45 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.JsonSyntaxException
 * JD-Core Version:    0.6.2
 */