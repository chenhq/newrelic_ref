/*     */ package com.newrelic.com.google.common.escape;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class CharEscaperBuilder
/*     */ {
/*     */   private final Map<Character, String> map;
/*  76 */   private int max = -1;
/*     */ 
/*     */   public CharEscaperBuilder()
/*     */   {
/*  82 */     this.map = new HashMap();
/*     */   }
/*     */ 
/*     */   public CharEscaperBuilder addEscape(char c, String r)
/*     */   {
/*  89 */     this.map.put(Character.valueOf(c), Preconditions.checkNotNull(r));
/*  90 */     if (c > this.max) {
/*  91 */       this.max = c;
/*     */     }
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   public CharEscaperBuilder addEscapes(char[] cs, String r)
/*     */   {
/* 100 */     Preconditions.checkNotNull(r);
/* 101 */     for (char c : cs) {
/* 102 */       addEscape(c, r);
/*     */     }
/* 104 */     return this;
/*     */   }
/*     */ 
/*     */   public char[][] toArray()
/*     */   {
/* 115 */     char[][] result = new char[this.max + 1][];
/* 116 */     for (Map.Entry entry : this.map.entrySet()) {
/* 117 */       result[((Character)entry.getKey()).charValue()] = ((String)entry.getValue()).toCharArray();
/*     */     }
/* 119 */     return result;
/*     */   }
/*     */ 
/*     */   public Escaper toEscaper()
/*     */   {
/* 129 */     return new CharArrayDecorator(toArray());
/*     */   }
/*     */ 
/*     */   private static class CharArrayDecorator extends CharEscaper
/*     */   {
/*     */     private final char[][] replacements;
/*     */     private final int replaceLength;
/*     */ 
/*     */     CharArrayDecorator(char[][] replacements)
/*     */     {
/*  48 */       this.replacements = replacements;
/*  49 */       this.replaceLength = replacements.length;
/*     */     }
/*     */ 
/*     */     public String escape(String s)
/*     */     {
/*  57 */       int slen = s.length();
/*  58 */       for (int index = 0; index < slen; index++) {
/*  59 */         char c = s.charAt(index);
/*  60 */         if ((c < this.replacements.length) && (this.replacements[c] != null)) {
/*  61 */           return escapeSlow(s, index);
/*     */         }
/*     */       }
/*  64 */       return s;
/*     */     }
/*     */ 
/*     */     protected char[] escape(char c) {
/*  68 */       return c < this.replaceLength ? this.replacements[c] : null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.escape.CharEscaperBuilder
 * JD-Core Version:    0.6.2
 */