/*     */ package com.newrelic.org.slf4j.impl;
/*     */ 
/*     */ import com.newrelic.org.slf4j.helpers.FormattingTuple;
/*     */ import com.newrelic.org.slf4j.helpers.MarkerIgnoringBase;
/*     */ import com.newrelic.org.slf4j.helpers.MessageFormatter;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class SimpleLogger extends MarkerIgnoringBase
/*     */ {
/*     */   private static final long serialVersionUID = -6560244151660620173L;
/*  72 */   private static long startTime = System.currentTimeMillis();
/*  73 */   public static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*  75 */   private static String INFO_STR = "INFO";
/*  76 */   private static String WARN_STR = "WARN";
/*  77 */   private static String ERROR_STR = "ERROR";
/*     */ 
/*     */   SimpleLogger(String name)
/*     */   {
/*  84 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */   public void trace(String msg)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String format, Object param1)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String format, Object param1, Object param2)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String format, Object[] argArray)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String msg, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */   public void debug(String msg)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String format, Object param1)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String format, Object param1, Object param2)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String format, Object[] argArray)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String msg, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void log(String level, String message, Throwable t)
/*     */   {
/* 186 */     StringBuffer buf = new StringBuffer();
/*     */ 
/* 188 */     long millis = System.currentTimeMillis();
/* 189 */     buf.append(millis - startTime);
/*     */ 
/* 191 */     buf.append(" [");
/* 192 */     buf.append(Thread.currentThread().getName());
/* 193 */     buf.append("] ");
/*     */ 
/* 195 */     buf.append(level);
/* 196 */     buf.append(" ");
/*     */ 
/* 198 */     buf.append(this.name);
/* 199 */     buf.append(" - ");
/*     */ 
/* 201 */     buf.append(message);
/*     */ 
/* 203 */     buf.append(LINE_SEPARATOR);
/*     */ 
/* 205 */     System.err.print(buf.toString());
/* 206 */     if (t != null) {
/* 207 */       t.printStackTrace(System.err);
/*     */     }
/* 209 */     System.err.flush();
/*     */   }
/*     */ 
/*     */   private void formatAndLog(String level, String format, Object arg1, Object arg2)
/*     */   {
/* 222 */     FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
/* 223 */     log(level, tp.getMessage(), tp.getThrowable());
/*     */   }
/*     */ 
/*     */   private void formatAndLog(String level, String format, Object[] argArray)
/*     */   {
/* 234 */     FormattingTuple tp = MessageFormatter.arrayFormat(format, argArray);
/* 235 */     log(level, tp.getMessage(), tp.getThrowable());
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 242 */     return true;
/*     */   }
/*     */ 
/*     */   public void info(String msg)
/*     */   {
/* 250 */     log(INFO_STR, msg, null);
/*     */   }
/*     */ 
/*     */   public void info(String format, Object arg)
/*     */   {
/* 258 */     formatAndLog(INFO_STR, format, arg, null);
/*     */   }
/*     */ 
/*     */   public void info(String format, Object arg1, Object arg2)
/*     */   {
/* 266 */     formatAndLog(INFO_STR, format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void info(String format, Object[] argArray)
/*     */   {
/* 274 */     formatAndLog(INFO_STR, format, argArray);
/*     */   }
/*     */ 
/*     */   public void info(String msg, Throwable t)
/*     */   {
/* 281 */     log(INFO_STR, msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 288 */     return true;
/*     */   }
/*     */ 
/*     */   public void warn(String msg)
/*     */   {
/* 296 */     log(WARN_STR, msg, null);
/*     */   }
/*     */ 
/*     */   public void warn(String format, Object arg)
/*     */   {
/* 304 */     formatAndLog(WARN_STR, format, arg, null);
/*     */   }
/*     */ 
/*     */   public void warn(String format, Object arg1, Object arg2)
/*     */   {
/* 312 */     formatAndLog(WARN_STR, format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void warn(String format, Object[] argArray)
/*     */   {
/* 320 */     formatAndLog(WARN_STR, format, argArray);
/*     */   }
/*     */ 
/*     */   public void warn(String msg, Throwable t)
/*     */   {
/* 327 */     log(WARN_STR, msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 334 */     return true;
/*     */   }
/*     */ 
/*     */   public void error(String msg)
/*     */   {
/* 342 */     log(ERROR_STR, msg, null);
/*     */   }
/*     */ 
/*     */   public void error(String format, Object arg)
/*     */   {
/* 350 */     formatAndLog(ERROR_STR, format, arg, null);
/*     */   }
/*     */ 
/*     */   public void error(String format, Object arg1, Object arg2)
/*     */   {
/* 358 */     formatAndLog(ERROR_STR, format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void error(String format, Object[] argArray)
/*     */   {
/* 366 */     formatAndLog(ERROR_STR, format, argArray);
/*     */   }
/*     */ 
/*     */   public void error(String msg, Throwable t)
/*     */   {
/* 373 */     log(ERROR_STR, msg, t);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.impl.SimpleLogger
 * JD-Core Version:    0.6.2
 */