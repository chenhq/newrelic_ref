/*    */ package com.newrelic.agent.android.instrumentation;
/*    */ 
/*    */ import java.net.HttpURLConnection;
/*    */ import javax.net.ssl.HttpsURLConnection;
/*    */ 
/*    */ public class OkHttpInstrumentation
/*    */ {
/*    */   @WrapReturn(className="com/squareup/okhttp/OkHttpClient", methodName="open", methodDesc="(Ljava/net/URL;)Ljava/net/HttpURLConnection;")
/*    */   public static HttpURLConnection open(HttpURLConnection connection)
/*    */   {
/* 14 */     if ((connection instanceof HttpsURLConnection))
/* 15 */       return new HttpsURLConnectionExtension((HttpsURLConnection)connection);
/* 16 */     if (connection != null) {
/* 17 */       return new HttpURLConnectionExtension(connection);
/*    */     }
/* 19 */     return null;
/*    */   }
/*    */ 
/*    */   @WrapReturn(className="com/squareup/okhttp/OkHttpClient", methodName="open", methodDesc="(Ljava/net/URL;Ljava/net/Proxy)Ljava/net/HttpURLConnection;")
/*    */   public static HttpURLConnection openWithProxy(HttpURLConnection connection)
/*    */   {
/* 25 */     if ((connection instanceof HttpsURLConnection))
/* 26 */       return new HttpsURLConnectionExtension((HttpsURLConnection)connection);
/* 27 */     if (connection != null) {
/* 28 */       return new HttpURLConnectionExtension(connection);
/*    */     }
/* 30 */     return null;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.OkHttpInstrumentation
 * JD-Core Version:    0.6.2
 */