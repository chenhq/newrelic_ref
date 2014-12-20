/*    */ package com.newrelic.agent.android.instrumentation;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public enum MetricCategory
/*    */ {
/*  7 */   NONE("None"), 
/*  8 */   VIEW_LOADING("View Loading"), 
/*  9 */   VIEW_LAYOUT("Layout"), 
/* 10 */   DATABASE("Database"), 
/* 11 */   IMAGE("Images"), 
/* 12 */   JSON("JSON"), 
/* 13 */   NETWORK("Network");
/*    */ 
/*    */   private String categoryName;
/* 16 */   private static final Map<String, MetricCategory> methodMap = new HashMap() { } ;
/*    */ 
/*    */   private MetricCategory(String categoryName)
/*    */   {
/* 21 */     this.categoryName = categoryName;
/*    */   }
/*    */ 
/*    */   public String getCategoryName() {
/* 25 */     return this.categoryName;
/*    */   }
/*    */ 
/*    */   public static MetricCategory categoryForMethod(String fullMethodName) {
/* 29 */     if (fullMethodName == null) {
/* 30 */       return NONE;
/*    */     }
/* 32 */     String methodName = null;
/* 33 */     int hashIndex = fullMethodName.indexOf("#");
/* 34 */     if (hashIndex >= 0) {
/* 35 */       methodName = fullMethodName.substring(hashIndex + 1);
/*    */     }
/* 37 */     MetricCategory category = (MetricCategory)methodMap.get(methodName);
/* 38 */     if (category == null)
/* 39 */       category = NONE;
/* 40 */     return category;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.MetricCategory
 * JD-Core Version:    0.6.2
 */