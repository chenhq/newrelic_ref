/*    */ package com.newrelic.javassist.compiler;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public final class KeywordTable extends HashMap
/*    */ {
/*    */   public int lookup(String name)
/*    */   {
/* 22 */     Object found = get(name);
/* 23 */     if (found == null) {
/* 24 */       return -1;
/*    */     }
/* 26 */     return ((Integer)found).intValue();
/*    */   }
/*    */ 
/*    */   public void append(String name, int t) {
/* 30 */     put(name, new Integer(t));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.KeywordTable
 * JD-Core Version:    0.6.2
 */