/*     */ package com.newrelic.org.slf4j;
/*     */ 
/*     */ import com.newrelic.org.slf4j.helpers.NOPMDCAdapter;
/*     */ import com.newrelic.org.slf4j.helpers.Util;
/*     */ import com.newrelic.org.slf4j.impl.StaticMDCBinder;
/*     */ import com.newrelic.org.slf4j.spi.MDCAdapter;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MDC
/*     */ {
/*     */   static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
/*     */   static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
/*     */   static MDCAdapter mdcAdapter;
/*     */ 
/*     */   public static void put(String key, String val)
/*     */     throws IllegalArgumentException
/*     */   {
/* 105 */     if (key == null) {
/* 106 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/* 108 */     if (mdcAdapter == null) {
/* 109 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 112 */     mdcAdapter.put(key, val);
/*     */   }
/*     */ 
/*     */   public static String get(String key)
/*     */     throws IllegalArgumentException
/*     */   {
/* 127 */     if (key == null) {
/* 128 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/*     */ 
/* 131 */     if (mdcAdapter == null) {
/* 132 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 135 */     return mdcAdapter.get(key);
/*     */   }
/*     */ 
/*     */   public static void remove(String key)
/*     */     throws IllegalArgumentException
/*     */   {
/* 148 */     if (key == null) {
/* 149 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/*     */ 
/* 152 */     if (mdcAdapter == null) {
/* 153 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 156 */     mdcAdapter.remove(key);
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 163 */     if (mdcAdapter == null) {
/* 164 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 167 */     mdcAdapter.clear();
/*     */   }
/*     */ 
/*     */   public static Map getCopyOfContextMap()
/*     */   {
/* 178 */     if (mdcAdapter == null) {
/* 179 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 182 */     return mdcAdapter.getCopyOfContextMap();
/*     */   }
/*     */ 
/*     */   public static void setContextMap(Map contextMap)
/*     */   {
/* 195 */     if (mdcAdapter == null) {
/* 196 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 199 */     mdcAdapter.setContextMap(contextMap);
/*     */   }
/*     */ 
/*     */   public static MDCAdapter getMDCAdapter()
/*     */   {
/* 209 */     return mdcAdapter;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  73 */       mdcAdapter = StaticMDCBinder.SINGLETON.getMDCA();
/*     */     } catch (NoClassDefFoundError ncde) {
/*  75 */       mdcAdapter = new NOPMDCAdapter();
/*  76 */       String msg = ncde.getMessage();
/*  77 */       if ((msg != null) && (msg.indexOf("com/newrelic/org/slf4j/impl/StaticMDCBinder") != -1)) {
/*  78 */         Util.report("Failed to load class \"org.slf4j.impl.StaticMDCBinder\".");
/*  79 */         Util.report("Defaulting to no-operation MDCAdapter implementation.");
/*  80 */         Util.report("See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.");
/*     */       }
/*     */       else {
/*  83 */         throw ncde;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  87 */       Util.report("MDC binding unsuccessful.", e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.MDC
 * JD-Core Version:    0.6.2
 */