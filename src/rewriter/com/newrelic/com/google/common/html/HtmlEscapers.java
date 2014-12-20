/*    */ package com.newrelic.com.google.common.html;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.escape.Escaper;
/*    */ import com.newrelic.com.google.common.escape.Escapers;
/*    */ import com.newrelic.com.google.common.escape.Escapers.Builder;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class HtmlEscapers
/*    */ {
/* 65 */   private static final Escaper HTML_ESCAPER = Escapers.builder().addEscape('"', "&quot;").addEscape('\'', "&#39;").addEscape('&', "&amp;").addEscape('<', "&lt;").addEscape('>', "&gt;").build();
/*    */ 
/*    */   public static Escaper htmlEscaper()
/*    */   {
/* 59 */     return HTML_ESCAPER;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.html.HtmlEscapers
 * JD-Core Version:    0.6.2
 */