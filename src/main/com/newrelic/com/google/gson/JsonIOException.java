/*    */ package com.newrelic.com.google.gson;
/*    */ 
/*    */ public final class JsonIOException extends JsonParseException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public JsonIOException(String msg)
/*    */   {
/* 29 */     super(msg);
/*    */   }
/*    */ 
/*    */   public JsonIOException(String msg, Throwable cause) {
/* 33 */     super(msg, cause);
/*    */   }
/*    */ 
/*    */   public JsonIOException(Throwable cause)
/*    */   {
/* 43 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.JsonIOException
 * JD-Core Version:    0.6.2
 */