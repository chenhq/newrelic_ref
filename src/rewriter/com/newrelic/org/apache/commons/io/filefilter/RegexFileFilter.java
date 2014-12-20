/*     */ package com.newrelic.org.apache.commons.io.filefilter;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.IOCase;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class RegexFileFilter extends AbstractFileFilter
/*     */   implements Serializable
/*     */ {
/*     */   private final Pattern pattern;
/*     */ 
/*     */   public RegexFileFilter(String pattern)
/*     */   {
/*  57 */     if (pattern == null) {
/*  58 */       throw new IllegalArgumentException("Pattern is missing");
/*     */     }
/*     */ 
/*  61 */     this.pattern = Pattern.compile(pattern);
/*     */   }
/*     */ 
/*     */   public RegexFileFilter(String pattern, IOCase caseSensitivity)
/*     */   {
/*  72 */     if (pattern == null) {
/*  73 */       throw new IllegalArgumentException("Pattern is missing");
/*     */     }
/*  75 */     int flags = 0;
/*  76 */     if ((caseSensitivity != null) && (!caseSensitivity.isCaseSensitive())) {
/*  77 */       flags = 2;
/*     */     }
/*  79 */     this.pattern = Pattern.compile(pattern, flags);
/*     */   }
/*     */ 
/*     */   public RegexFileFilter(String pattern, int flags)
/*     */   {
/*  90 */     if (pattern == null) {
/*  91 */       throw new IllegalArgumentException("Pattern is missing");
/*     */     }
/*  93 */     this.pattern = Pattern.compile(pattern, flags);
/*     */   }
/*     */ 
/*     */   public RegexFileFilter(Pattern pattern)
/*     */   {
/* 103 */     if (pattern == null) {
/* 104 */       throw new IllegalArgumentException("Pattern is missing");
/*     */     }
/*     */ 
/* 107 */     this.pattern = pattern;
/*     */   }
/*     */ 
/*     */   public boolean accept(File dir, String name)
/*     */   {
/* 119 */     return this.pattern.matcher(name).matches();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.RegexFileFilter
 * JD-Core Version:    0.6.2
 */