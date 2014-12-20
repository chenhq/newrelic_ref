/*    */ package com.newrelic.agent.android.util;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import java.util.Random;
/*    */ 
/*    */ public class Util
/*    */ {
/* 12 */   private static final Random random = new Random();
/*    */ 
/*    */   public static String slurp(InputStream stream) throws IOException {
/* 15 */     char[] buf = new char[8192];
/* 16 */     StringBuilder sb = new StringBuilder();
/* 17 */     BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
/*    */     while (true) {
/* 19 */       int n = reader.read(buf);
/* 20 */       if (n < 0) break;
/* 21 */       sb.append(buf, 0, n);
/*    */     }
/* 23 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   public static String sanitizeUrl(String urlString) {
/* 27 */     if (urlString == null) {
/* 28 */       return null;
/*    */     }
/*    */     URL url;
/*    */     try
/*    */     {
/* 33 */       url = new URL(urlString);
/*    */     } catch (MalformedURLException e) {
/* 35 */       return null;
/*    */     }
/*    */ 
/* 38 */     StringBuffer sanitizedUrl = new StringBuffer();
/*    */ 
/* 40 */     sanitizedUrl.append(url.getProtocol());
/* 41 */     sanitizedUrl.append("://");
/* 42 */     sanitizedUrl.append(url.getHost());
/* 43 */     if (url.getPort() != -1) {
/* 44 */       sanitizedUrl.append(":");
/* 45 */       sanitizedUrl.append(url.getPort());
/*    */     }
/* 47 */     sanitizedUrl.append(url.getPath());
/*    */ 
/* 49 */     return sanitizedUrl.toString();
/*    */   }
/*    */ 
/*    */   public static Random getRandom() {
/* 53 */     return random;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.util.Util
 * JD-Core Version:    0.6.2
 */