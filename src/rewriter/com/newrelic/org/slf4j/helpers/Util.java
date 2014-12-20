/*    */ package com.newrelic.org.slf4j.helpers;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class Util
/*    */ {
/*    */   public static final void report(String msg, Throwable t)
/*    */   {
/* 37 */     System.err.println(msg);
/* 38 */     System.err.println("Reported exception:");
/* 39 */     t.printStackTrace();
/*    */   }
/*    */ 
/*    */   public static final void report(String msg) {
/* 43 */     System.err.println("SLF4J: " + msg);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.helpers.Util
 * JD-Core Version:    0.6.2
 */