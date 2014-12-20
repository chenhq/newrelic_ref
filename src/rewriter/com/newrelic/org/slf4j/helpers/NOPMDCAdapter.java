/*    */ package com.newrelic.org.slf4j.helpers;
/*    */ 
/*    */ import com.newrelic.org.slf4j.spi.MDCAdapter;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class NOPMDCAdapter
/*    */   implements MDCAdapter
/*    */ {
/*    */   public void clear()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String get(String key)
/*    */   {
/* 22 */     return null;
/*    */   }
/*    */ 
/*    */   public void put(String key, String val) {
/*    */   }
/*    */ 
/*    */   public void remove(String key) {
/*    */   }
/*    */ 
/*    */   public Map getCopyOfContextMap() {
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   public void setContextMap(Map contextMap)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.helpers.NOPMDCAdapter
 * JD-Core Version:    0.6.2
 */