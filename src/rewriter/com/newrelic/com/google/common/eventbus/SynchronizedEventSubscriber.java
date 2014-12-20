/*    */ package com.newrelic.com.google.common.eventbus;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ final class SynchronizedEventSubscriber extends EventSubscriber
/*    */ {
/*    */   public SynchronizedEventSubscriber(Object target, Method method)
/*    */   {
/* 40 */     super(target, method);
/*    */   }
/*    */ 
/*    */   public void handleEvent(Object event)
/*    */     throws InvocationTargetException
/*    */   {
/* 46 */     synchronized (this) {
/* 47 */       super.handleEvent(event);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.SynchronizedEventSubscriber
 * JD-Core Version:    0.6.2
 */