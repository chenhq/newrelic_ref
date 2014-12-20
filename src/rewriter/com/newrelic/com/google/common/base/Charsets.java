/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ public final class Charsets
/*    */ {
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 46 */   public static final Charset US_ASCII = Charset.forName("US-ASCII");
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 53 */   public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
/*    */ 
/* 59 */   public static final Charset UTF_8 = Charset.forName("UTF-8");
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 66 */   public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 73 */   public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
/*    */ 
/*    */   @GwtIncompatible("Non-UTF-8 Charset")
/* 81 */   public static final Charset UTF_16 = Charset.forName("UTF-16");
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Charsets
 * JD-Core Version:    0.6.2
 */