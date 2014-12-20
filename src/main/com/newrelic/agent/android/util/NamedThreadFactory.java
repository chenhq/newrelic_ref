/*    */ package com.newrelic.agent.android.util;
/*    */ 
/*    */ import java.util.concurrent.ThreadFactory;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class NamedThreadFactory
/*    */   implements ThreadFactory
/*    */ {
/*    */   final ThreadGroup group;
/*    */   final String namePrefix;
/* 17 */   final AtomicInteger threadNumber = new AtomicInteger(1);
/*    */ 
/*    */   public NamedThreadFactory(String factoryName) {
/* 20 */     SecurityManager s = System.getSecurityManager();
/* 21 */     this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
/*    */ 
/* 23 */     this.namePrefix = ("NR_" + factoryName + "-");
/*    */   }
/*    */ 
/*    */   public Thread newThread(Runnable r)
/*    */   {
/* 28 */     Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
/*    */ 
/* 31 */     if (t.isDaemon()) {
/* 32 */       t.setDaemon(false);
/*    */     }
/* 34 */     if (t.getPriority() != 5) {
/* 35 */       t.setPriority(5);
/*    */     }
/* 37 */     return t;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.util.NamedThreadFactory
 * JD-Core Version:    0.6.2
 */