/*    */ package com.newrelic.com.google.common.io;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.io.File;
/*    */ import java.io.FilenameFilter;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public final class PatternFilenameFilter
/*    */   implements FilenameFilter
/*    */ {
/*    */   private final Pattern pattern;
/*    */ 
/*    */   public PatternFilenameFilter(String patternStr)
/*    */   {
/* 48 */     this(Pattern.compile(patternStr));
/*    */   }
/*    */ 
/*    */   public PatternFilenameFilter(Pattern pattern)
/*    */   {
/* 56 */     this.pattern = ((Pattern)Preconditions.checkNotNull(pattern));
/*    */   }
/*    */ 
/*    */   public boolean accept(@Nullable File dir, String fileName) {
/* 60 */     return this.pattern.matcher(fileName).matches();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.io.PatternFilenameFilter
 * JD-Core Version:    0.6.2
 */