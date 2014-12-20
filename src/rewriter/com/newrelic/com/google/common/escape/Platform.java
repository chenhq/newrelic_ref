/*    */ package com.newrelic.com.google.common.escape;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/* 40 */   private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal()
/*    */   {
/*    */     protected char[] initialValue() {
/* 43 */       return new char[1024];
/*    */     }
/* 40 */   };
/*    */ 
/*    */   static char[] charBufferFromThreadLocal()
/*    */   {
/* 32 */     return (char[])DEST_TL.get();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.escape.Platform
 * JD-Core Version:    0.6.2
 */