/*     */ package com.newrelic.org.apache.commons.io.filefilter;
/*     */ 
/*     */ import com.newrelic.org.apache.commons.io.FilenameUtils;
/*     */ import java.io.File;
/*     */ import java.io.Serializable;
/*     */ import java.util.List;
/*     */ 
/*     */ @Deprecated
/*     */ public class WildcardFilter extends AbstractFileFilter
/*     */   implements Serializable
/*     */ {
/*     */   private final String[] wildcards;
/*     */ 
/*     */   public WildcardFilter(String wildcard)
/*     */   {
/*  65 */     if (wildcard == null) {
/*  66 */       throw new IllegalArgumentException("The wildcard must not be null");
/*     */     }
/*  68 */     this.wildcards = new String[] { wildcard };
/*     */   }
/*     */ 
/*     */   public WildcardFilter(String[] wildcards)
/*     */   {
/*  78 */     if (wildcards == null) {
/*  79 */       throw new IllegalArgumentException("The wildcard array must not be null");
/*     */     }
/*  81 */     this.wildcards = new String[wildcards.length];
/*  82 */     System.arraycopy(wildcards, 0, this.wildcards, 0, wildcards.length);
/*     */   }
/*     */ 
/*     */   public WildcardFilter(List<String> wildcards)
/*     */   {
/*  93 */     if (wildcards == null) {
/*  94 */       throw new IllegalArgumentException("The wildcard list must not be null");
/*     */     }
/*  96 */     this.wildcards = ((String[])wildcards.toArray(new String[wildcards.size()]));
/*     */   }
/*     */ 
/*     */   public boolean accept(File dir, String name)
/*     */   {
/* 109 */     if ((dir != null) && (new File(dir, name).isDirectory())) {
/* 110 */       return false;
/*     */     }
/*     */ 
/* 113 */     for (String wildcard : this.wildcards) {
/* 114 */       if (FilenameUtils.wildcardMatch(name, wildcard)) {
/* 115 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 119 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean accept(File file)
/*     */   {
/* 130 */     if (file.isDirectory()) {
/* 131 */       return false;
/*     */     }
/*     */ 
/* 134 */     for (String wildcard : this.wildcards) {
/* 135 */       if (FilenameUtils.wildcardMatch(file.getName(), wildcard)) {
/* 136 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 140 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.filefilter.WildcardFilter
 * JD-Core Version:    0.6.2
 */