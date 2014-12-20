/*    */ package com.newrelic.com.google.common.cache;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public enum RemovalCause
/*    */ {
/* 40 */   EXPLICIT, 
/*    */ 
/* 53 */   REPLACED, 
/*    */ 
/* 65 */   COLLECTED, 
/*    */ 
/* 76 */   EXPIRED, 
/*    */ 
/* 87 */   SIZE;
/*    */ 
/*    */   abstract boolean wasEvicted();
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.RemovalCause
 * JD-Core Version:    0.6.2
 */