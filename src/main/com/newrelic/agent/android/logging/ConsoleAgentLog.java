/*    */ package com.newrelic.agent.android.logging;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class ConsoleAgentLog
/*    */   implements AgentLog
/*    */ {
/*  4 */   private int level = 3;
/*    */ 
/*    */   public void debug(String message)
/*    */   {
/*  8 */     if (this.level == 5)
/*  9 */       print("DEBUG", message);
/*    */   }
/*    */ 
/*    */   public void verbose(String message)
/*    */   {
/* 14 */     if (this.level >= 4)
/* 15 */       print("VERBOSE", message);
/*    */   }
/*    */ 
/*    */   public void info(String message)
/*    */   {
/* 20 */     if (this.level >= 3)
/* 21 */       print("INFO", message);
/*    */   }
/*    */ 
/*    */   public void warning(String message)
/*    */   {
/* 26 */     if (this.level >= 2)
/* 27 */       print("WARN", message);
/*    */   }
/*    */ 
/*    */   public void error(String message, Throwable cause)
/*    */   {
/* 32 */     if (this.level >= 1)
/* 33 */       print("ERROR", message + " " + cause.getMessage());
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/* 38 */     if (this.level >= 1)
/* 39 */       print("ERROR", message);
/*    */   }
/*    */ 
/*    */   public int getLevel()
/*    */   {
/* 45 */     return this.level;
/*    */   }
/*    */ 
/*    */   public void setLevel(int level)
/*    */   {
/* 50 */     this.level = level;
/*    */   }
/*    */ 
/*    */   private static void print(String tag, String message) {
/* 54 */     System.out.println("[" + tag + "] " + message);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.logging.ConsoleAgentLog
 * JD-Core Version:    0.6.2
 */