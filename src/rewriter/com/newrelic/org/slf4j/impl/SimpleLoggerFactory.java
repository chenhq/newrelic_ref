/*    */ package com.newrelic.org.slf4j.impl;
/*    */ 
/*    */ import com.newrelic.org.slf4j.ILoggerFactory;
/*    */ import com.newrelic.org.slf4j.Logger;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SimpleLoggerFactory
/*    */   implements ILoggerFactory
/*    */ {
/* 50 */   static final SimpleLoggerFactory INSTANCE = new SimpleLoggerFactory();
/*    */   Map loggerMap;
/*    */ 
/*    */   public SimpleLoggerFactory()
/*    */   {
/* 55 */     this.loggerMap = new HashMap();
/*    */   }
/*    */ 
/*    */   public Logger getLogger(String name)
/*    */   {
/* 62 */     Logger slogger = null;
/*    */ 
/* 64 */     synchronized (this) {
/* 65 */       slogger = (Logger)this.loggerMap.get(name);
/* 66 */       if (slogger == null) {
/* 67 */         slogger = new SimpleLogger(name);
/* 68 */         this.loggerMap.put(name, slogger);
/*    */       }
/*    */     }
/* 71 */     return slogger;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.impl.SimpleLoggerFactory
 * JD-Core Version:    0.6.2
 */