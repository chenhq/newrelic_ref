/*    */ package com.newrelic.agent.android.measurement;
/*    */ 
/*    */ public class ThreadInfo
/*    */ {
/*    */   private long id;
/*    */   private String name;
/*    */ 
/*    */   public ThreadInfo()
/*    */   {
/* 11 */     this(Thread.currentThread());
/*    */   }
/*    */ 
/*    */   public ThreadInfo(long id, String name) {
/* 15 */     this.id = id;
/* 16 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public ThreadInfo(Thread thread) {
/* 20 */     this(thread.getId(), thread.getName());
/*    */   }
/*    */ 
/*    */   public static ThreadInfo fromThread(Thread thread) {
/* 24 */     return new ThreadInfo(thread);
/*    */   }
/*    */ 
/*    */   public long getId() {
/* 28 */     return this.id;
/*    */   }
/*    */ 
/*    */   public String getName() {
/* 32 */     return this.name;
/*    */   }
/*    */ 
/*    */   public void setId(long id) {
/* 36 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public void setName(String name) {
/* 40 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 45 */     return "ThreadInfo{id=" + this.id + ", name='" + this.name + '\'' + '}';
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.measurement.ThreadInfo
 * JD-Core Version:    0.6.2
 */