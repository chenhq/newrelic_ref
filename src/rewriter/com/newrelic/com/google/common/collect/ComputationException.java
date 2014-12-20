/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public class ComputationException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public ComputationException(@Nullable Throwable cause)
/*    */   {
/* 35 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ComputationException
 * JD-Core Version:    0.6.2
 */