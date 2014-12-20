/*    */ package com.newrelic.agent.android.metric;
/*    */ 
/*    */ public enum MetricUnit
/*    */ {
/*  4 */   PERCENT("%"), 
/*  5 */   BYTES("bytes"), 
/*  6 */   SECONDS("sec"), 
/*  7 */   BYTES_PER_SECOND("bytes/second"), 
/*  8 */   OPERATIONS("op");
/*    */ 
/*    */   private String label;
/*    */ 
/*    */   private MetricUnit(String label) {
/* 13 */     this.label = label;
/*    */   }
/*    */ 
/*    */   public String getLabel() {
/* 17 */     return this.label;
/*    */   }
/*    */ 
/*    */   public void setLabel(String label) {
/* 21 */     this.label = label;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.metric.MetricUnit
 * JD-Core Version:    0.6.2
 */