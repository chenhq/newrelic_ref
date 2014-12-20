/*    */ package com.newrelic.agent.android.tracing;
/*    */ 
/*    */ public class SampleValue
/*    */ {
/*  4 */   private Double value = Double.valueOf(0.0D);
/*    */   private boolean isDouble;
/*    */ 
/*    */   public SampleValue(double value)
/*    */   {
/*  8 */     setValue(value);
/*    */   }
/*    */ 
/*    */   public SampleValue(long value) {
/* 12 */     setValue(value);
/*    */   }
/*    */ 
/*    */   public Number getValue() {
/* 16 */     if (this.isDouble)
/* 17 */       return asDouble();
/* 18 */     return asLong();
/*    */   }
/*    */ 
/*    */   public Double asDouble() {
/* 22 */     return this.value;
/*    */   }
/*    */ 
/*    */   public Long asLong() {
/* 26 */     return Long.valueOf(this.value.longValue());
/*    */   }
/*    */ 
/*    */   public void setValue(double value) {
/* 30 */     this.value = Double.valueOf(value);
/* 31 */     this.isDouble = true;
/*    */   }
/*    */ 
/*    */   public void setValue(long value) {
/* 35 */     this.value = Double.valueOf(value);
/* 36 */     this.isDouble = false;
/*    */   }
/*    */ 
/*    */   public boolean isDouble() {
/* 40 */     return this.isDouble;
/*    */   }
/*    */ 
/*    */   public void setDouble(boolean aDouble) {
/* 44 */     this.isDouble = aDouble;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.tracing.SampleValue
 * JD-Core Version:    0.6.2
 */