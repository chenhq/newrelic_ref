/*     */ package com.newrelic.org.reflections.util;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Joiner;
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import com.newrelic.org.reflections.ReflectionsException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class FilterBuilder
/*     */   implements Predicate<String>
/*     */ {
/*     */   private final List<Predicate<String>> chain;
/*     */ 
/*     */   public FilterBuilder()
/*     */   {
/*  23 */     this.chain = Lists.newArrayList(); } 
/*  24 */   private FilterBuilder(Iterable<Predicate<String>> filters) { this.chain = Lists.newArrayList(filters); }
/*     */ 
/*     */   public FilterBuilder include(String regex) {
/*  27 */     return add(new Include(regex));
/*     */   }
/*  29 */   public FilterBuilder exclude(String regex) { add(new Exclude(regex)); return this; } 
/*     */   public FilterBuilder add(Predicate<String> filter) {
/*  31 */     this.chain.add(filter); return this;
/*     */   }
/*  33 */   public FilterBuilder includePackage(Class<?> aClass) { return add(new Include(packageNameRegex(aClass))); } 
/*     */   public FilterBuilder excludePackage(Class<?> aClass) {
/*  35 */     return add(new Exclude(packageNameRegex(aClass)));
/*     */   }
/*  37 */   private static String packageNameRegex(Class<?> aClass) { return prefix(aClass.getPackage().getName() + "."); } 
/*     */   public static String prefix(String qualifiedName) {
/*  39 */     return qualifiedName.replace(".", "\\.") + ".*";
/*     */   }
/*  41 */   public String toString() { return Joiner.on(", ").join(this.chain); }
/*     */ 
/*     */   public boolean apply(String regex) {
/*  44 */     boolean accept = (this.chain == null) || (this.chain.isEmpty()) || ((this.chain.get(0) instanceof Exclude));
/*     */ 
/*  46 */     if (this.chain != null) {
/*  47 */       for (Predicate filter : this.chain) {
/*  48 */         if (((!accept) || (!(filter instanceof Include))) && (
/*  49 */           (accept) || (!(filter instanceof Exclude))))
/*  50 */           accept = filter.apply(regex);
/*     */       }
/*     */     }
/*  53 */     return accept;
/*     */   }
/*     */ 
/*     */   public static FilterBuilder parse(String includeExcludeString)
/*     */   {
/*  82 */     List filters = new ArrayList();
/*     */ 
/*  84 */     if (!Utils.isEmpty(includeExcludeString)) {
/*  85 */       for (String string : includeExcludeString.split(",")) {
/*  86 */         String trimmed = string.trim();
/*  87 */         char prefix = trimmed.charAt(0);
/*  88 */         String pattern = trimmed.substring(1);
/*     */         Predicate filter;
/*  91 */         switch (prefix) {
/*     */         case '+':
/*  93 */           filter = new Include(pattern);
/*  94 */           break;
/*     */         case '-':
/*  96 */           filter = new Exclude(pattern);
/*  97 */           break;
/*     */         default:
/*  99 */           throw new ReflectionsException("includeExclude should start with either + or -");
/*     */         }
/*     */ 
/* 102 */         filters.add(filter);
/*     */       }
/*     */ 
/* 105 */       return new FilterBuilder(filters);
/*     */     }
/* 107 */     return new FilterBuilder();
/*     */   }
/*     */ 
/*     */   public static class Exclude extends FilterBuilder.Matcher
/*     */   {
/*     */     public Exclude(String patternString)
/*     */     {
/*  70 */       super(); } 
/*  71 */     public boolean apply(String regex) { return !this.pattern.matcher(regex).matches(); } 
/*  72 */     public String toString() { return "-" + this.pattern.pattern(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class Include extends FilterBuilder.Matcher
/*     */   {
/*     */     public Include(String patternString)
/*     */     {
/*  64 */       super(); } 
/*  65 */     public boolean apply(String regex) { return this.pattern.matcher(regex).matches(); } 
/*  66 */     public String toString() { return "+" + super.toString(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static abstract class Matcher extends FilterBuilder
/*     */   {
/*     */     final Pattern pattern;
/*     */ 
/*     */     public Matcher(String regex)
/*     */     {
/*  58 */       this.pattern = Pattern.compile(regex); } 
/*     */     public abstract boolean apply(String paramString);
/*     */ 
/*  60 */     public String toString() { return this.pattern.pattern(); }
/*     */ 
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.util.FilterBuilder
 * JD-Core Version:    0.6.2
 */