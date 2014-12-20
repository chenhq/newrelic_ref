/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public class ExecutionError extends Error
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   protected ExecutionError()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected ExecutionError(@Nullable String message)
/*    */   {
/* 46 */     super(message);
/*    */   }
/*    */ 
/*    */   public ExecutionError(@Nullable String message, @Nullable Error cause)
/*    */   {
/* 53 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public ExecutionError(@Nullable Error cause)
/*    */   {
/* 60 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.ExecutionError
 * JD-Core Version:    0.6.2
 */