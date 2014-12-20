/*    */ package com.newrelic.com.google.common.eventbus;
/*    */ 
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class SubscriberExceptionContext
/*    */ {
/*    */   private final EventBus eventBus;
/*    */   private final Object event;
/*    */   private final Object subscriber;
/*    */   private final Method subscriberMethod;
/*    */ 
/*    */   SubscriberExceptionContext(EventBus eventBus, Object event, Object subscriber, Method subscriberMethod)
/*    */   {
/* 42 */     this.eventBus = ((EventBus)Preconditions.checkNotNull(eventBus));
/* 43 */     this.event = Preconditions.checkNotNull(event);
/* 44 */     this.subscriber = Preconditions.checkNotNull(subscriber);
/* 45 */     this.subscriberMethod = ((Method)Preconditions.checkNotNull(subscriberMethod));
/*    */   }
/*    */ 
/*    */   public EventBus getEventBus()
/*    */   {
/* 53 */     return this.eventBus;
/*    */   }
/*    */ 
/*    */   public Object getEvent()
/*    */   {
/* 60 */     return this.event;
/*    */   }
/*    */ 
/*    */   public Object getSubscriber()
/*    */   {
/* 67 */     return this.subscriber;
/*    */   }
/*    */ 
/*    */   public Method getSubscriberMethod()
/*    */   {
/* 74 */     return this.subscriberMethod;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.SubscriberExceptionContext
 * JD-Core Version:    0.6.2
 */