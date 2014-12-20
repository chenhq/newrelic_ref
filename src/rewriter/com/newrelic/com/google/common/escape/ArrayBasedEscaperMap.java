/*    */ package com.newrelic.com.google.common.escape;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.util.Collections;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class ArrayBasedEscaperMap
/*    */ {
/*    */   private final char[][] replacementArray;
/* 90 */   private static final char[][] EMPTY_REPLACEMENT_ARRAY = new char[0][0];
/*    */ 
/*    */   public static ArrayBasedEscaperMap create(Map<Character, String> replacements)
/*    */   {
/* 56 */     return new ArrayBasedEscaperMap(createReplacementArray(replacements));
/*    */   }
/*    */ 
/*    */   private ArrayBasedEscaperMap(char[][] replacementArray)
/*    */   {
/* 64 */     this.replacementArray = replacementArray;
/*    */   }
/*    */ 
/*    */   char[][] getReplacementArray()
/*    */   {
/* 69 */     return this.replacementArray;
/*    */   }
/*    */ 
/*    */   @VisibleForTesting
/*    */   static char[][] createReplacementArray(Map<Character, String> map)
/*    */   {
/* 77 */     Preconditions.checkNotNull(map);
/* 78 */     if (map.isEmpty()) {
/* 79 */       return EMPTY_REPLACEMENT_ARRAY;
/*    */     }
/* 81 */     char max = ((Character)Collections.max(map.keySet())).charValue();
/* 82 */     char[][] replacements = new char[max + '\001'][];
/* 83 */     for (Iterator i$ = map.keySet().iterator(); i$.hasNext(); ) { char c = ((Character)i$.next()).charValue();
/* 84 */       replacements[c] = ((String)map.get(Character.valueOf(c))).toCharArray();
/*    */     }
/* 86 */     return replacements;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.escape.ArrayBasedEscaperMap
 * JD-Core Version:    0.6.2
 */