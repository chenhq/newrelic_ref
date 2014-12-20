/*     */ package com.newrelic.com.google.common.escape;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class Escapers
/*     */ {
/*  51 */   private static final Escaper NULL_ESCAPER = new CharEscaper() {
/*     */     public String escape(String string) {
/*  53 */       return (String)Preconditions.checkNotNull(string);
/*     */     }
/*     */ 
/*     */     protected char[] escape(char c)
/*     */     {
/*  58 */       return null;
/*     */     }
/*  51 */   };
/*     */ 
/*     */   public static Escaper nullEscaper()
/*     */   {
/*  46 */     return NULL_ESCAPER;
/*     */   }
/*     */ 
/*     */   public static Builder builder()
/*     */   {
/*  78 */     return new Builder(null);
/*     */   }
/*     */ 
/*     */   static UnicodeEscaper asUnicodeEscaper(Escaper escaper)
/*     */   {
/* 183 */     Preconditions.checkNotNull(escaper);
/* 184 */     if ((escaper instanceof UnicodeEscaper))
/* 185 */       return (UnicodeEscaper)escaper;
/* 186 */     if ((escaper instanceof CharEscaper)) {
/* 187 */       return wrap((CharEscaper)escaper);
/*     */     }
/*     */ 
/* 191 */     throw new IllegalArgumentException("Cannot create a UnicodeEscaper from: " + escaper.getClass().getName());
/*     */   }
/*     */ 
/*     */   public static String computeReplacement(CharEscaper escaper, char c)
/*     */   {
/* 206 */     return stringOrNull(escaper.escape(c));
/*     */   }
/*     */ 
/*     */   public static String computeReplacement(UnicodeEscaper escaper, int cp)
/*     */   {
/* 220 */     return stringOrNull(escaper.escape(cp));
/*     */   }
/*     */ 
/*     */   private static String stringOrNull(char[] in) {
/* 224 */     return in == null ? null : new String(in);
/*     */   }
/*     */ 
/*     */   private static UnicodeEscaper wrap(CharEscaper escaper)
/*     */   {
/* 229 */     return new UnicodeEscaper()
/*     */     {
/*     */       protected char[] escape(int cp) {
/* 232 */         if (cp < 65536) {
/* 233 */           return this.val$escaper.escape((char)cp);
/*     */         }
/*     */ 
/* 239 */         char[] surrogateChars = new char[2];
/* 240 */         Character.toChars(cp, surrogateChars, 0);
/* 241 */         char[] hiChars = this.val$escaper.escape(surrogateChars[0]);
/* 242 */         char[] loChars = this.val$escaper.escape(surrogateChars[1]);
/*     */ 
/* 248 */         if ((hiChars == null) && (loChars == null))
/*     */         {
/* 250 */           return null;
/*     */         }
/*     */ 
/* 253 */         int hiCount = hiChars != null ? hiChars.length : 1;
/* 254 */         int loCount = loChars != null ? loChars.length : 1;
/* 255 */         char[] output = new char[hiCount + loCount];
/* 256 */         if (hiChars != null)
/*     */         {
/* 258 */           for (int n = 0; n < hiChars.length; n++)
/* 259 */             output[n] = hiChars[n];
/*     */         }
/*     */         else {
/* 262 */           output[0] = surrogateChars[0];
/*     */         }
/* 264 */         if (loChars != null) {
/* 265 */           for (int n = 0; n < loChars.length; n++)
/* 266 */             output[(hiCount + n)] = loChars[n];
/*     */         }
/*     */         else {
/* 269 */           output[hiCount] = surrogateChars[1];
/*     */         }
/* 271 */         return output;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static final class Builder
/*     */   {
/*  95 */     private final Map<Character, String> replacementMap = new HashMap();
/*     */ 
/*  97 */     private char safeMin = '\000';
/*  98 */     private char safeMax = 65535;
/*  99 */     private String unsafeReplacement = null;
/*     */ 
/*     */     public Builder setSafeRange(char safeMin, char safeMax)
/*     */     {
/* 115 */       this.safeMin = safeMin;
/* 116 */       this.safeMax = safeMax;
/* 117 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder setUnsafeReplacement(@Nullable String unsafeReplacement)
/*     */     {
/* 130 */       this.unsafeReplacement = unsafeReplacement;
/* 131 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder addEscape(char c, String replacement)
/*     */     {
/* 146 */       Preconditions.checkNotNull(replacement);
/*     */ 
/* 148 */       this.replacementMap.put(Character.valueOf(c), replacement);
/* 149 */       return this;
/*     */     }
/*     */ 
/*     */     public Escaper build()
/*     */     {
/* 156 */       return new ArrayBasedCharEscaper(this.replacementMap, this.safeMin, this.safeMax) {
/* 157 */         private final char[] replacementChars = Escapers.Builder.this.unsafeReplacement != null ? Escapers.Builder.this.unsafeReplacement.toCharArray() : null;
/*     */ 
/*     */         protected char[] escapeUnsafe(char c) {
/* 160 */           return this.replacementChars;
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.escape.Escapers
 * JD-Core Version:    0.6.2
 */