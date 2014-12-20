/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*    */ import java.io.PrintStream;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public final class UncaughtExceptionHandlers
/*    */ {
/*    */   public static Thread.UncaughtExceptionHandler systemExit()
/*    */   {
/* 50 */     return new Exiter(Runtime.getRuntime());
/*    */   }
/*    */   @VisibleForTesting
/*    */   static final class Exiter implements Thread.UncaughtExceptionHandler {
/* 54 */     private static final Logger logger = Logger.getLogger(Exiter.class.getName());
/*    */     private final Runtime runtime;
/*    */ 
/*    */     Exiter(Runtime runtime) {
/* 59 */       this.runtime = runtime;
/*    */     }
/*    */ 
/*    */     public void uncaughtException(Thread t, Throwable e)
/*    */     {
/*    */       try {
/* 65 */         logger.log(Level.SEVERE, String.format("Caught an exception in %s.  Shutting down.", new Object[] { t }), e);
/*    */       }
/*    */       catch (Throwable errorInLogging)
/*    */       {
/* 69 */         System.err.println(e.getMessage());
/* 70 */         System.err.println(errorInLogging.getMessage());
/*    */       } finally {
/* 72 */         this.runtime.exit(1);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.UncaughtExceptionHandlers
 * JD-Core Version:    0.6.2
 */