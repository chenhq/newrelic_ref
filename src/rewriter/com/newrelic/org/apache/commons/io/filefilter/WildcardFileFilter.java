/*     */ package com.newrelic.org.apache.commons.io.filefilter;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.FilenameUtils;
/*     */ import com.newrelic.org.apache.commons.io.IOCase;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.List;
/*     */ 
/*     */ public class WildcardFileFilter extends AbstractFileFilter
/*     */   implements Serializable
/*     */ {
/*     */   private final String[] wildcards;
/*     */   private final IOCase caseSensitivity;
/*     */ 
/*     */   public WildcardFileFilter(String wildcard)
/*     */   {
/*  65 */     this(wildcard, null);
/*     */   }
/*     */ 
/*     */   public WildcardFileFilter(String wildcard, IOCase caseSensitivity)
/*     */   {
/*  76 */     if (wildcard == null) {
/*  77 */       throw new IllegalArgumentException("The wildcard must not be null");
/*     */     }
/*  79 */     this.wildcards = new String[] { wildcard };
/*  80 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*     */   }
/*     */ 
/*     */   public WildcardFileFilter(String[] wildcards)
/*     */   {
/*  93 */     this(wildcards, null);
/*     */   }
/*     */ 
/*     */   public WildcardFileFilter(String[] wildcards, IOCase caseSensitivity)
/*     */   {
/* 107 */     if (wildcards == null) {
/* 108 */       throw new IllegalArgumentException("The wildcard array must not be null");
/*     */     }
/* 110 */     this.wildcards = new String[wildcards.length];
/* 111 */     System.arraycopy(wildcards, 0, this.wildcards, 0, wildcards.length);
/* 112 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*     */   }
/*     */ 
/*     */   public WildcardFileFilter(List<String> wildcards)
/*     */   {
/* 123 */     this(wildcards, null);
/*     */   }
/*     */ 
/*     */   public WildcardFileFilter(List<String> wildcards, IOCase caseSensitivity)
/*     */   {
/* 135 */     if (wildcards == null) {
/* 136 */       throw new IllegalArgumentException("The wildcard list must not be null");
/*     */     }
/* 138 */     this.wildcards = ((String[])wildcards.toArray(new String[wildcards.size()]));
/* 139 */     this.caseSensitivity = (caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity);
/*     */   }
/*     */ 
/*     */   public boolean accept(File dir, String name)
/*     */   {
/* 152 */     for (String wildcard : this.wildcards) {
/* 153 */       if (FilenameUtils.wildcardMatch(name, wildcard, this.caseSensitivity)) {
/* 154 */         return true;
/*     */       }
/*     */     }
/* 157 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean accept(File file)
/*     */   {
/* 168 */     String name = file.getName();
/* 169 */     for (String wildcard : this.wildcards) {
/* 170 */       if (FilenameUtils.wildcardMatch(name, wildcard, this.caseSensitivity)) {
/* 171 */         return true;
/*     */       }
/*     */     }
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 184 */     StringBuilder buffer = new StringBuilder();
/* 185 */     buffer.append(super.toString());
/* 186 */     buffer.append("(");
/* 187 */     if (this.wildcards != null) {
/* 188 */       for (int i = 0; i < this.wildcards.length; i++) {
/* 189 */         if (i > 0) {
/* 190 */           buffer.append(",");
/*     */         }
/* 192 */         buffer.append(this.wildcards[i]);
/*     */       }
/*     */     }
/* 195 */     buffer.append(")");
/* 196 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.WildcardFileFilter
 * JD-Core Version:    0.6.2
 */