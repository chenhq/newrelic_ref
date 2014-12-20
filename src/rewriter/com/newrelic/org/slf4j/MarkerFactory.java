/*    */ package com.newrelic.org.slf4j;
/*    */ 
/*    */ import com.newrelic.org.slf4j.helpers.BasicMarkerFactory;
/*    */ import com.newrelic.org.slf4j.helpers.Util;
/*    */ import com.newrelic.org.slf4j.impl.StaticMarkerBinder;
/*    */ 
/*    */ public class MarkerFactory
/*    */ {
/*    */   static IMarkerFactory markerFactory;
/*    */ 
/*    */   public static Marker getMarker(String name)
/*    */   {
/* 71 */     return markerFactory.getMarker(name);
/*    */   }
/*    */ 
/*    */   public static Marker getDetachedMarker(String name)
/*    */   {
/* 81 */     return markerFactory.getDetachedMarker(name);
/*    */   }
/*    */ 
/*    */   public static IMarkerFactory getIMarkerFactory()
/*    */   {
/* 93 */     return markerFactory;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 52 */       markerFactory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
/*    */     } catch (NoClassDefFoundError e) {
/* 54 */       markerFactory = new BasicMarkerFactory();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 58 */       Util.report("Unexpected failure while binding MarkerFactory", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.MarkerFactory
 * JD-Core Version:    0.6.2
 */