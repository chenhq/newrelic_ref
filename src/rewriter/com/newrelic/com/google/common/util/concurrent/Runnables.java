/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class Runnables
/*    */ {
/* 31 */   private static final Runnable EMPTY_RUNNABLE = new Runnable() {
/* 31 */     public void run() {  }  } ;
/*    */ 
/*    */   public static Runnable doNothing()
/*    */   {
/* 41 */     return EMPTY_RUNNABLE;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.Runnables
 * JD-Core Version:    0.6.2
 */