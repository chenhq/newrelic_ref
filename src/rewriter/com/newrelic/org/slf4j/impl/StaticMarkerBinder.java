/*    */ package com.newrelic.org.slf4j.impl;
/*    */ 
/*    */ import com.newrelic.org.slf4j.IMarkerFactory;
/*    */ import com.newrelic.org.slf4j.helpers.BasicMarkerFactory;
/*    */ import com.newrelic.org.slf4j.spi.MarkerFactoryBinder;
/*    */ 
/*    */ public class StaticMarkerBinder
/*    */   implements MarkerFactoryBinder
/*    */ {
/* 45 */   public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
/*    */ 
/* 47 */   final IMarkerFactory markerFactory = new BasicMarkerFactory();
/*    */ 
/*    */   public IMarkerFactory getMarkerFactory()
/*    */   {
/* 57 */     return this.markerFactory;
/*    */   }
/*    */ 
/*    */   public String getMarkerFactoryClassStr()
/*    */   {
/* 65 */     return BasicMarkerFactory.class.getName();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.impl.StaticMarkerBinder
 * JD-Core Version:    0.6.2
 */