/*     */ package com.newrelic.com.google.common.base;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public enum CaseFormat
/*     */ {
/*  40 */   LOWER_HYPHEN(CharMatcher.is('-'), "-"), 
/*     */ 
/*  58 */   LOWER_UNDERSCORE(CharMatcher.is('_'), "_"), 
/*     */ 
/*  76 */   LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), ""), 
/*     */ 
/*  85 */   UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), ""), 
/*     */ 
/*  94 */   UPPER_UNDERSCORE(CharMatcher.is('_'), "_");
/*     */ 
/*     */   private final CharMatcher wordBoundary;
/*     */   private final String wordSeparator;
/*     */ 
/*     */   private CaseFormat(CharMatcher wordBoundary, String wordSeparator)
/*     */   {
/* 113 */     this.wordBoundary = wordBoundary;
/* 114 */     this.wordSeparator = wordSeparator;
/*     */   }
/*     */ 
/*     */   public final String to(CaseFormat format, String str)
/*     */   {
/* 123 */     Preconditions.checkNotNull(format);
/* 124 */     Preconditions.checkNotNull(str);
/* 125 */     return format == this ? str : convert(format, str);
/*     */   }
/*     */ 
/*     */   String convert(CaseFormat format, String s)
/*     */   {
/* 133 */     StringBuilder out = null;
/* 134 */     int i = 0;
/* 135 */     int j = -1;
/* 136 */     while ((j = this.wordBoundary.indexIn(s, ++j)) != -1) {
/* 137 */       if (i == 0)
/*     */       {
/* 139 */         out = new StringBuilder(s.length() + 4 * this.wordSeparator.length());
/* 140 */         out.append(format.normalizeFirstWord(s.substring(i, j)));
/*     */       } else {
/* 142 */         out.append(format.normalizeWord(s.substring(i, j)));
/*     */       }
/* 144 */       out.append(format.wordSeparator);
/* 145 */       i = j + this.wordSeparator.length();
/*     */     }
/* 147 */     return format.normalizeWord(s.substring(i));
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public Converter<String, String> converterTo(CaseFormat targetFormat)
/*     */   {
/* 159 */     return new StringConverter(this, targetFormat);
/*     */   }
/*     */ 
/*     */   abstract String normalizeWord(String paramString);
/*     */ 
/*     */   private String normalizeFirstWord(String word)
/*     */   {
/* 206 */     return this == LOWER_CAMEL ? Ascii.toLowerCase(word) : normalizeWord(word);
/*     */   }
/*     */ 
/*     */   private static String firstCharOnlyToUpper(String word) {
/* 210 */     return word.length() + Ascii.toUpperCase(word.charAt(0)) + Ascii.toLowerCase(word.substring(1));
/*     */   }
/*     */ 
/*     */   private static final class StringConverter extends Converter<String, String>
/*     */     implements Serializable
/*     */   {
/*     */     private final CaseFormat sourceFormat;
/*     */     private final CaseFormat targetFormat;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     StringConverter(CaseFormat sourceFormat, CaseFormat targetFormat)
/*     */     {
/* 169 */       this.sourceFormat = ((CaseFormat)Preconditions.checkNotNull(sourceFormat));
/* 170 */       this.targetFormat = ((CaseFormat)Preconditions.checkNotNull(targetFormat));
/*     */     }
/*     */ 
/*     */     protected String doForward(String s)
/*     */     {
/* 175 */       return s == null ? null : this.sourceFormat.to(this.targetFormat, s);
/*     */     }
/*     */ 
/*     */     protected String doBackward(String s)
/*     */     {
/* 180 */       return s == null ? null : this.targetFormat.to(this.sourceFormat, s);
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object object) {
/* 184 */       if ((object instanceof StringConverter)) {
/* 185 */         StringConverter that = (StringConverter)object;
/* 186 */         return (this.sourceFormat.equals(that.sourceFormat)) && (this.targetFormat.equals(that.targetFormat));
/*     */       }
/*     */ 
/* 189 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 193 */       return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 197 */       return this.sourceFormat + ".converterTo(" + this.targetFormat + ")";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.CaseFormat
 * JD-Core Version:    0.6.2
 */