/*     */ package com.newrelic.org.apache.commons.io;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ public class Charsets
/*     */ {
/*  91 */   public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
/*     */ 
/* 103 */   public static final Charset US_ASCII = Charset.forName("US-ASCII");
/*     */ 
/* 116 */   public static final Charset UTF_16 = Charset.forName("UTF-16");
/*     */ 
/* 128 */   public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
/*     */ 
/* 140 */   public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
/*     */ 
/* 152 */   public static final Charset UTF_8 = Charset.forName("UTF-8");
/*     */ 
/*     */   public static Charset toCharset(Charset charset)
/*     */   {
/*  67 */     return charset == null ? Charset.defaultCharset() : charset;
/*     */   }
/*     */ 
/*     */   public static Charset toCharset(String charset)
/*     */   {
/*  80 */     return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.Charsets
 * JD-Core Version:    0.6.2
 */