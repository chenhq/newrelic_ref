/*     */ package com.newrelic.agent.android.util;
/*     */ 
/*     */ import android.content.Context;
/*     */ import android.net.ConnectivityManager;
/*     */ import android.net.NetworkInfo;
/*     */ import android.os.Build;
/*     */ import android.telephony.TelephonyManager;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import java.text.MessageFormat;
/*     */ 
/*     */ public final class Connectivity
/*     */ {
/*     */   private static final String ANDROID = "Android";
/*  19 */   private static AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*     */   public static String carrierNameFromContext(Context context) {
/*     */     NetworkInfo networkInfo;
/*     */     try {
/*  24 */       networkInfo = getNetworkInfo(context);
/*     */     } catch (SecurityException e) {
/*  26 */       return "unknown";
/*     */     }
/*     */ 
/*  29 */     if (!isConnected(networkInfo))
/*  30 */       return "none";
/*  31 */     if (isWan(networkInfo))
/*  32 */       return carrierNameFromTelephonyManager(context);
/*  33 */     if (isWifi(networkInfo)) {
/*  34 */       return "wifi";
/*     */     }
/*  36 */     log.warning(MessageFormat.format("Unknown network type: {0} [{1}]", new Object[] { networkInfo.getTypeName(), Integer.valueOf(networkInfo.getType()) }));
/*  37 */     return "unknown";
/*     */   }
/*     */ 
/*     */   public static String wanType(Context context)
/*     */   {
/*     */     NetworkInfo networkInfo;
/*     */     try {
/*  44 */       networkInfo = getNetworkInfo(context);
/*     */     } catch (SecurityException e) {
/*  46 */       return "unknown";
/*     */     }
/*     */ 
/*  49 */     if (!isConnected(networkInfo))
/*  50 */       return "none";
/*  51 */     if (isWifi(networkInfo))
/*  52 */       return "wifi";
/*  53 */     if (isWan(networkInfo)) {
/*  54 */       return connectionNameFromNetworkSubtype(networkInfo.getSubtype());
/*     */     }
/*  56 */     return "unknown";
/*     */   }
/*     */ 
/*     */   private static boolean isConnected(NetworkInfo networkInfo)
/*     */   {
/*  61 */     return (networkInfo != null) && (networkInfo.isConnected());
/*     */   }
/*     */ 
/*     */   private static boolean isWan(NetworkInfo networkInfo) {
/*  65 */     switch (networkInfo.getType()) {
/*     */     case 0:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*  71 */       return true;
/*     */     case 1:
/*  73 */     }return false;
/*     */   }
/*     */ 
/*     */   private static boolean isWifi(NetworkInfo networkInfo)
/*     */   {
/*  78 */     switch (networkInfo.getType())
/*     */     {
/*     */     case 1:
/*     */     case 6:
/*     */     case 7:
/*     */     case 9:
/*  84 */       return true;
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*  86 */     case 8: } return false;
/*     */   }
/*     */ 
/*     */   private static NetworkInfo getNetworkInfo(Context context) throws SecurityException
/*     */   {
/*  91 */     ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
/*     */     try {
/*  93 */       return connectivityManager.getActiveNetworkInfo();
/*     */     }
/*     */     catch (SecurityException e) {
/*  96 */       log.warning("Cannot determine network state. Enable android.permission.ACCESS_NETWORK_STATE in your manifest.");
/*  97 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String carrierNameFromTelephonyManager(Context context) {
/* 102 */     TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
/* 103 */     String networkOperator = telephonyManager.getNetworkOperatorName();
/*     */ 
/* 105 */     boolean smellsLikeAnEmulator = (Build.PRODUCT.equals("google_sdk")) || (Build.PRODUCT.equals("sdk")) || (Build.PRODUCT.equals("sdk_x86")) || (Build.FINGERPRINT.startsWith("generic"));
/* 106 */     if ((networkOperator.equals("Android")) && (smellsLikeAnEmulator))
/*     */     {
/* 110 */       return "wifi";
/*     */     }
/*     */ 
/* 113 */     return networkOperator;
/*     */   }
/*     */ 
/*     */   private static String connectionNameFromNetworkSubtype(int subType)
/*     */   {
/* 118 */     switch (subType) {
/*     */     case 7:
/* 120 */       return "1xRTT";
/*     */     case 4:
/* 122 */       return "CDMA";
/*     */     case 2:
/* 124 */       return "EDGE";
/*     */     case 5:
/* 126 */       return "EVDO rev 0";
/*     */     case 6:
/* 128 */       return "EVDO rev A";
/*     */     case 1:
/* 130 */       return "GPRS";
/*     */     case 8:
/* 132 */       return "HSDPA";
/*     */     case 10:
/* 134 */       return "HSPA";
/*     */     case 9:
/* 136 */       return "HSUPA";
/*     */     case 3:
/* 138 */       return "UMTS";
/*     */     case 11:
/* 140 */       return "IDEN";
/*     */     case 12:
/* 145 */       return "EVDO rev B";
/*     */     case 15:
/* 147 */       return "HSPAP";
/*     */     case 14:
/* 149 */       return "HRPD";
/*     */     case 13:
/* 151 */       return "LTE";
/*     */     case 0:
/*     */     }
/* 154 */     return "unknown";
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.util.Connectivity
 * JD-Core Version:    0.6.2
 */