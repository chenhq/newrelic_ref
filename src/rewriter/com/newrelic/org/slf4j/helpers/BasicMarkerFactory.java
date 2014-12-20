/*    */ package com.newrelic.org.slf4j.helpers;
/*    */ 
/*    */ import com.newrelic.org.slf4j.IMarkerFactory;
/*    */ import com.newrelic.org.slf4j.Marker;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class BasicMarkerFactory
/*    */   implements IMarkerFactory
/*    */ {
/* 44 */   Map markerMap = new HashMap();
/*    */ 
/*    */   public synchronized Marker getMarker(String name)
/*    */   {
/* 63 */     if (name == null) {
/* 64 */       throw new IllegalArgumentException("Marker name cannot be null");
/*    */     }
/*    */ 
/* 67 */     Marker marker = (Marker)this.markerMap.get(name);
/* 68 */     if (marker == null) {
/* 69 */       marker = new BasicMarker(name);
/* 70 */       this.markerMap.put(name, marker);
/*    */     }
/* 72 */     return marker;
/*    */   }
/*    */ 
/*    */   public synchronized boolean exists(String name)
/*    */   {
/* 79 */     if (name == null) {
/* 80 */       return false;
/*    */     }
/* 82 */     return this.markerMap.containsKey(name);
/*    */   }
/*    */ 
/*    */   public boolean detachMarker(String name) {
/* 86 */     if (name == null) {
/* 87 */       return false;
/*    */     }
/* 89 */     return this.markerMap.remove(name) != null;
/*    */   }
/*    */ 
/*    */   public Marker getDetachedMarker(String name)
/*    */   {
/* 94 */     return new BasicMarker(name);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.slf4j.helpers.BasicMarkerFactory
 * JD-Core Version:    0.6.2
 */