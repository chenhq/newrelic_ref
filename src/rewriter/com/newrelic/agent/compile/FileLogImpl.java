/*    */ package com.newrelic.agent.compile;
/*    */ 
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.util.Map;
/*    */ 
/*    */ final class FileLogImpl
/*    */   implements Log
/*    */ {
/*    */   private final PrintWriter writer;
/*    */   private final Map<String, String> agentOptions;
/*    */ 
/*    */   public FileLogImpl(Map<String, String> agentOptions, String logFileName)
/*    */   {
/* 14 */     this.agentOptions = agentOptions;
/*    */     try {
/* 16 */       this.writer = new PrintWriter(new FileOutputStream(logFileName));
/*    */     } catch (FileNotFoundException e) {
/* 18 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void writeln(String level, String message) {
/* 23 */     this.writer.write("[" + level + "] " + message + "\n");
/* 24 */     this.writer.flush();
/*    */   }
/*    */ 
/*    */   public void info(String message)
/*    */   {
/* 29 */     writeln("info", message);
/*    */   }
/*    */ 
/*    */   public void debug(String message)
/*    */   {
/* 34 */     if (this.agentOptions.get("debug") != null)
/* 35 */       writeln("debug", message);
/*    */   }
/*    */ 
/*    */   public void warning(String message)
/*    */   {
/* 41 */     writeln("warn", message);
/*    */   }
/*    */ 
/*    */   public void warning(String message, Throwable cause)
/*    */   {
/* 46 */     writeln("warn", message);
/* 47 */     cause.printStackTrace(this.writer);
/* 48 */     this.writer.flush();
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/* 53 */     writeln("error", message);
/*    */   }
/*    */ 
/*    */   public void error(String message, Throwable cause)
/*    */   {
/* 58 */     writeln("error", message);
/* 59 */     cause.printStackTrace(this.writer);
/* 60 */     this.writer.flush();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.FileLogImpl
 * JD-Core Version:    0.6.2
 */