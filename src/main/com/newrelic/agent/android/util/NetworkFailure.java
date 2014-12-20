/*    */ package com.newrelic.agent.android.util;
/*    */ 
/*    */ import java.net.ConnectException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.SocketTimeoutException;
/*    */ import java.net.UnknownHostException;
/*    */ import javax.net.ssl.SSLException;
/*    */ import org.apache.http.client.ClientProtocolException;
/*    */ import org.apache.http.client.HttpResponseException;
/*    */ import org.apache.http.conn.ConnectTimeoutException;
/*    */ 
/*    */ public enum NetworkFailure
/*    */ {
/* 15 */   Unknown(-1), 
/* 16 */   BadURL(-1000), 
/* 17 */   TimedOut(-1001), 
/* 18 */   CannotConnectToHost(-1004), 
/* 19 */   DNSLookupFailed(-1006), 
/* 20 */   BadServerResponse(-1011), 
/* 21 */   SecureConnectionFailed(-1200);
/*    */ 
/*    */   private int errorCode;
/*    */ 
/*    */   private NetworkFailure(int errorCode) {
/* 26 */     this.errorCode = errorCode;
/*    */   }
/*    */ 
/*    */   public static NetworkFailure exceptionToNetworkFailure(Exception e) {
/* 30 */     NetworkFailure error = Unknown;
/*    */ 
/* 32 */     if ((e instanceof UnknownHostException))
/* 33 */       error = DNSLookupFailed;
/* 34 */     else if (((e instanceof SocketTimeoutException)) || ((e instanceof ConnectTimeoutException)))
/* 35 */       error = TimedOut;
/* 36 */     else if ((e instanceof ConnectException))
/* 37 */       error = CannotConnectToHost;
/* 38 */     else if ((e instanceof MalformedURLException))
/* 39 */       error = BadURL;
/* 40 */     else if ((e instanceof SSLException))
/* 41 */       error = SecureConnectionFailed;
/* 42 */     else if (((e instanceof HttpResponseException)) || ((e instanceof ClientProtocolException))) {
/* 43 */       error = BadServerResponse;
/*    */     }
/*    */ 
/* 46 */     return error;
/*    */   }
/*    */ 
/*    */   public static int exceptionToErrorCode(Exception e) {
/* 50 */     return exceptionToNetworkFailure(e).getErrorCode();
/*    */   }
/*    */ 
/*    */   public int getErrorCode() {
/* 54 */     return this.errorCode;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.util.NetworkFailure
 * JD-Core Version:    0.6.2
 */