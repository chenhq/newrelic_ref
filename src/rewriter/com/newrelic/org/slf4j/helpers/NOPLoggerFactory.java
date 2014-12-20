/*    */ package com.newrelic.org.slf4j.helpers;
/*    */ 
/*    */ import com.newrelic.org.slf4j.ILoggerFactory;
/*    */ import com.newrelic.org.slf4j.Logger;
/*    */ 
/*    */ public class NOPLoggerFactory
/*    */   implements ILoggerFactory
/*    */ {
/*    */   public Logger getLogger(String name)
/*    */   {
/* 55 */     return NOPLogger.NOP_LOGGER;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.helpers.NOPLoggerFactory
 * JD-Core Version:    0.6.2
 */