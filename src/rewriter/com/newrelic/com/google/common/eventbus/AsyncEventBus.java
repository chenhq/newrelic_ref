/*     */ package com.newrelic.com.google.common.eventbus;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ @Beta
/*     */ public class AsyncEventBus extends EventBus
/*     */ {
/*     */   private final Executor executor;
/*  38 */   private final ConcurrentLinkedQueue<EventBus.EventWithSubscriber> eventsToDispatch = new ConcurrentLinkedQueue();
/*     */ 
/*     */   public AsyncEventBus(String identifier, Executor executor)
/*     */   {
/*  51 */     super(identifier);
/*  52 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */   }
/*     */ 
/*     */   public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler)
/*     */   {
/*  67 */     super(subscriberExceptionHandler);
/*  68 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */   }
/*     */ 
/*     */   public AsyncEventBus(Executor executor)
/*     */   {
/*  80 */     super("default");
/*  81 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */   }
/*     */ 
/*     */   void enqueueEvent(Object event, EventSubscriber subscriber)
/*     */   {
/*  86 */     this.eventsToDispatch.offer(new EventBus.EventWithSubscriber(event, subscriber));
/*     */   }
/*     */ 
/*     */   protected void dispatchQueuedEvents()
/*     */   {
/*     */     while (true)
/*     */     {
/*  97 */       EventBus.EventWithSubscriber eventWithSubscriber = (EventBus.EventWithSubscriber)this.eventsToDispatch.poll();
/*  98 */       if (eventWithSubscriber == null)
/*     */       {
/*     */         break;
/*     */       }
/* 102 */       dispatch(eventWithSubscriber.event, eventWithSubscriber.subscriber);
/*     */     }
/*     */   }
/*     */ 
/*     */   void dispatch(final Object event, final EventSubscriber subscriber)
/*     */   {
/* 111 */     Preconditions.checkNotNull(event);
/* 112 */     Preconditions.checkNotNull(subscriber);
/* 113 */     this.executor.execute(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 117 */         AsyncEventBus.this.dispatch(event, subscriber);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.AsyncEventBus
 * JD-Core Version:    0.6.2
 */