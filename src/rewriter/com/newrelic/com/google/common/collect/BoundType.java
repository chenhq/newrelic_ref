/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible
/*    */ public enum BoundType
/*    */ {
/* 31 */   OPEN, 
/*    */ 
/* 40 */   CLOSED;
/*    */ 
/*    */   static BoundType forBoolean(boolean inclusive)
/*    */   {
/* 51 */     return inclusive ? CLOSED : OPEN;
/*    */   }
/*    */ 
/*    */   abstract BoundType flip();
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.BoundType
 * JD-Core Version:    0.6.2
 */