/*     */ package com.newrelic.com.google.common.escape;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Function;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public abstract class Escaper
/*     */ {
/*  89 */   private final Function<String, String> asFunction = new Function()
/*     */   {
/*     */     public String apply(String from)
/*     */     {
/*  93 */       return Escaper.this.escape(from);
/*     */     }
/*  89 */   };
/*     */ 
/*     */   public abstract String escape(String paramString);
/*     */ 
/*     */   public final Function<String, String> asFunction()
/*     */   {
/* 101 */     return this.asFunction;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.escape.Escaper
 * JD-Core Version:    0.6.2
 */