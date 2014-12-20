/*    */ package com.newrelic.org.slf4j.impl;
/*    */ 
/*    */ import com.newrelic.org.slf4j.helpers.NOPMDCAdapter;
/*    */ import com.newrelic.org.slf4j.spi.MDCAdapter;
/*    */ 
/*    */ public class StaticMDCBinder
/*    */ {
/* 42 */   public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();
/*    */ 
/*    */   public MDCAdapter getMDCA()
/*    */   {
/* 52 */     return new NOPMDCAdapter();
/*    */   }
/*    */ 
/*    */   public String getMDCAdapterClassStr() {
/* 56 */     return NOPMDCAdapter.class.getName();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.impl.StaticMDCBinder
 * JD-Core Version:    0.6.2
 */