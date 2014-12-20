/*     */ package com.newrelic.com.google.common.net;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.escape.Escaper;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class UrlEscapers
/*     */ {
/*     */   static final String URL_FORM_PARAMETER_OTHER_SAFE_CHARS = "-_.*";
/*     */   static final String URL_PATH_OTHER_SAFE_CHARS_LACKING_PLUS = "-._~!$'()*,;&=@:";
/*  88 */   private static final Escaper URL_FORM_PARAMETER_ESCAPER = new PercentEscaper("-_.*", true);
/*     */ 
/* 129 */   private static final Escaper URL_PATH_SEGMENT_ESCAPER = new PercentEscaper("-._~!$'()*,;&=@:+", false);
/*     */ 
/* 167 */   private static final Escaper URL_FRAGMENT_ESCAPER = new PercentEscaper("-._~!$'()*,;&=@:+/?", false);
/*     */ 
/*     */   public static Escaper urlFormParameterEscaper()
/*     */   {
/*  85 */     return URL_FORM_PARAMETER_ESCAPER;
/*     */   }
/*     */ 
/*     */   public static Escaper urlPathSegmentEscaper()
/*     */   {
/* 126 */     return URL_PATH_SEGMENT_ESCAPER;
/*     */   }
/*     */ 
/*     */   public static Escaper urlFragmentEscaper()
/*     */   {
/* 164 */     return URL_FRAGMENT_ESCAPER;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.net.UrlEscapers
 * JD-Core Version:    0.6.2
 */