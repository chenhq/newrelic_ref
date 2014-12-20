/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class CollectPreconditions
/*    */ {
/*    */   static void checkEntryNotNull(Object key, Object value)
/*    */   {
/* 30 */     if (key == null)
/* 31 */       throw new NullPointerException("null key in entry: null=" + value);
/* 32 */     if (value == null)
/* 33 */       throw new NullPointerException("null value in entry: " + key + "=null");
/*    */   }
/*    */ 
/*    */   static int checkNonnegative(int value, String name)
/*    */   {
/* 38 */     if (value < 0) {
/* 39 */       throw new IllegalArgumentException(name + " cannot be negative but was: " + value);
/*    */     }
/* 41 */     return value;
/*    */   }
/*    */ 
/*    */   static void checkRemove(boolean canRemove)
/*    */   {
/* 49 */     Preconditions.checkState(canRemove, "no calls to next() since the last call to remove()");
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.CollectPreconditions
 * JD-Core Version:    0.6.2
 */