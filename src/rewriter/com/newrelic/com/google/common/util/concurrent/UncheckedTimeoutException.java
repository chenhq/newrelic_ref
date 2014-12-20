/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class UncheckedTimeoutException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public UncheckedTimeoutException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UncheckedTimeoutException(@Nullable String message)
/*    */   {
/* 31 */     super(message);
/*    */   }
/*    */ 
/*    */   public UncheckedTimeoutException(@Nullable Throwable cause) {
/* 35 */     super(cause);
/*    */   }
/*    */ 
/*    */   public UncheckedTimeoutException(@Nullable String message, @Nullable Throwable cause) {
/* 39 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.UncheckedTimeoutException
 * JD-Core Version:    0.6.2
 */