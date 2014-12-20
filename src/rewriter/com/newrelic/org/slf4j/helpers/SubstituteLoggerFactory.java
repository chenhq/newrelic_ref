/*    */ package com.newrelic.org.slf4j.helpers;
/*    */ 
/*    */ import com.newrelic.org.slf4j.ILoggerFactory;
/*    */ import com.newrelic.org.slf4j.Logger;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class SubstituteLoggerFactory
/*    */   implements ILoggerFactory
/*    */ {
/* 56 */   final List loggerNameList = new ArrayList();
/*    */ 
/*    */   public Logger getLogger(String name) {
/* 59 */     synchronized (this.loggerNameList) {
/* 60 */       this.loggerNameList.add(name);
/*    */     }
/* 62 */     return NOPLogger.NOP_LOGGER;
/*    */   }
/*    */ 
/*    */   public List getLoggerNameList() {
/* 66 */     List copy = new ArrayList();
/* 67 */     synchronized (this.loggerNameList) {
/* 68 */       copy.addAll(this.loggerNameList);
/*    */     }
/* 70 */     return copy;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.helpers.SubstituteLoggerFactory
 * JD-Core Version:    0.6.2
 */