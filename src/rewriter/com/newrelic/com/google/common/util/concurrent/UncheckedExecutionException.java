/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public class UncheckedExecutionException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   protected UncheckedExecutionException()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected UncheckedExecutionException(@Nullable String message)
/*    */   {
/* 51 */     super(message);
/*    */   }
/*    */ 
/*    */   public UncheckedExecutionException(@Nullable String message, @Nullable Throwable cause)
/*    */   {
/* 58 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public UncheckedExecutionException(@Nullable Throwable cause)
/*    */   {
/* 65 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.UncheckedExecutionException
 * JD-Core Version:    0.6.2
 */