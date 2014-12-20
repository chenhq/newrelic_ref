/*    */ package com.newrelic.agent.android.tracing;
/*    */ 
/*    */ import com.newrelic.agent.android.harvest.type.HarvestableArray;
/*    */ import com.newrelic.com.google.gson.JsonArray;
/*    */ import com.newrelic.com.google.gson.JsonPrimitive;
/*    */ 
/*    */ public class Sample extends HarvestableArray
/*    */ {
/*    */   private long timestamp;
/*    */   private SampleValue sampleValue;
/*    */   private SampleType type;
/*    */ 
/*    */   public Sample(SampleType type)
/*    */   {
/* 18 */     setSampleType(type);
/* 19 */     setTimestamp(System.currentTimeMillis());
/*    */   }
/*    */ 
/*    */   public Sample(long timestamp) {
/* 23 */     setTimestamp(timestamp);
/*    */   }
/*    */ 
/*    */   public Sample(long timestamp, SampleValue sampleValue) {
/* 27 */     setTimestamp(timestamp);
/* 28 */     setSampleValue(sampleValue);
/*    */   }
/*    */ 
/*    */   public long getTimestamp() {
/* 32 */     return this.timestamp;
/*    */   }
/*    */ 
/*    */   public void setTimestamp(long timestamp) {
/* 36 */     this.timestamp = timestamp;
/*    */   }
/*    */ 
/*    */   public SampleValue getSampleValue() {
/* 40 */     return this.sampleValue;
/*    */   }
/*    */ 
/*    */   public void setSampleValue(SampleValue sampleValue) {
/* 44 */     this.sampleValue = sampleValue;
/*    */   }
/*    */ 
/*    */   public void setSampleValue(double value) {
/* 48 */     this.sampleValue = new SampleValue(value);
/*    */   }
/*    */ 
/*    */   public void setSampleValue(long value) {
/* 52 */     this.sampleValue = new SampleValue(value);
/*    */   }
/*    */ 
/*    */   public Number getValue() {
/* 56 */     return this.sampleValue.getValue();
/*    */   }
/*    */ 
/*    */   public SampleType getSampleType() {
/* 60 */     return this.type;
/*    */   }
/*    */ 
/*    */   public void setSampleType(SampleType type) {
/* 64 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public JsonArray asJsonArray()
/*    */   {
/* 69 */     JsonArray jsonArray = new JsonArray();
/*    */ 
/* 71 */     jsonArray.add(new JsonPrimitive(Long.valueOf(this.timestamp)));
/* 72 */     jsonArray.add(new JsonPrimitive(this.sampleValue.getValue()));
/*    */ 
/* 74 */     return jsonArray;
/*    */   }
/*    */ 
/*    */   public static enum SampleType
/*    */   {
/* 13 */     MEMORY, 
/* 14 */     CPU;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.tracing.Sample
 * JD-Core Version:    0.6.2
 */