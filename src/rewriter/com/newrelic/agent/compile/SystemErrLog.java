/*    */ package com.newrelic.agent.compile;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Map;
/*    */ 
/*    */ final class SystemErrLog
/*    */   implements Log
/*    */ {
/*    */   private final Map<String, String> agentOptions;
/*    */ 
/*    */   public SystemErrLog(Map<String, String> agentOptions)
/*    */   {
/* 10 */     this.agentOptions = agentOptions;
/*    */   }
/*    */ 
/*    */   public void info(String message)
/*    */   {
/* 15 */     System.out.println("[newrelic.info] " + message);
/*    */   }
/*    */ 
/*    */   public void debug(String message)
/*    */   {
/* 20 */     if (this.agentOptions.get("debug") != null)
/* 21 */       System.out.println("[newrelic.debug] " + message);
/*    */   }
/*    */ 
/*    */   public void warning(String message)
/*    */   {
/* 27 */     System.err.println("[newrelic.warn] " + message);
/*    */   }
/*    */ 
/*    */   public void warning(String message, Throwable cause)
/*    */   {
/* 32 */     System.err.println("[newrelic.warn] " + message);
/* 33 */     cause.printStackTrace(System.err);
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/* 38 */     System.err.println("[newrelic.error] " + message);
/*    */   }
/*    */ 
/*    */   public void error(String message, Throwable cause)
/*    */   {
/* 43 */     System.err.println("[newrelic.error] " + message);
/* 44 */     cause.printStackTrace(System.err);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.SystemErrLog
 * JD-Core Version:    0.6.2
 */