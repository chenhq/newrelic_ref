/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public class VerifyException extends RuntimeException
/*    */ {
/*    */   public VerifyException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public VerifyException(@Nullable String message)
/*    */   {
/* 37 */     super(message);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.VerifyException
 * JD-Core Version:    0.6.2
 */