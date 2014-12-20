/*     */ package com.newrelic.com.google.common.eventbus;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.annotations.VisibleForTesting;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Throwables;
/*     */ import com.newrelic.com.google.common.cache.CacheBuilder;
/*     */ import com.newrelic.com.google.common.cache.CacheLoader;
/*     */ import com.newrelic.com.google.common.cache.LoadingCache;
/*     */ import com.newrelic.com.google.common.collect.HashMultimap;
/*     */ import com.newrelic.com.google.common.collect.Multimap;
/*     */ import com.newrelic.com.google.common.collect.SetMultimap;
/*     */ import com.newrelic.com.google.common.reflect.TypeToken;
/*     */ import com.newrelic.com.google.common.reflect.TypeToken.TypeSet;
/*     */ import com.newrelic.com.google.common.util.concurrent.UncheckedExecutionException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ @Beta
/*     */ public class EventBus
/*     */ {
/* 121 */   private static final LoadingCache<Class<?>, Set<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader()
/*     */   {
/*     */     public Set<Class<?>> load(Class<?> concreteClass)
/*     */     {
/* 128 */       return TypeToken.of(concreteClass).getTypes().rawTypes();
/*     */     }
/*     */   });
/*     */ 
/* 138 */   private final SetMultimap<Class<?>, EventSubscriber> subscribersByType = HashMultimap.create();
/*     */ 
/* 140 */   private final ReadWriteLock subscribersByTypeLock = new ReentrantReadWriteLock();
/*     */ 
/* 147 */   private final SubscriberFindingStrategy finder = new AnnotatedSubscriberFinder();
/*     */ 
/* 150 */   private final ThreadLocal<Queue<EventWithSubscriber>> eventsToDispatch = new ThreadLocal()
/*     */   {
/*     */     protected Queue<EventBus.EventWithSubscriber> initialValue() {
/* 153 */       return new LinkedList();
/*     */     }
/* 150 */   };
/*     */ 
/* 158 */   private final ThreadLocal<Boolean> isDispatching = new ThreadLocal()
/*     */   {
/*     */     protected Boolean initialValue() {
/* 161 */       return Boolean.valueOf(false);
/*     */     }
/* 158 */   };
/*     */   private SubscriberExceptionHandler subscriberExceptionHandler;
/*     */ 
/*     */   public EventBus()
/*     */   {
/* 171 */     this("default");
/*     */   }
/*     */ 
/*     */   public EventBus(String identifier)
/*     */   {
/* 181 */     this(new LoggingSubscriberExceptionHandler(identifier));
/*     */   }
/*     */ 
/*     */   public EventBus(SubscriberExceptionHandler subscriberExceptionHandler)
/*     */   {
/* 191 */     this.subscriberExceptionHandler = ((SubscriberExceptionHandler)Preconditions.checkNotNull(subscriberExceptionHandler));
/*     */   }
/*     */ 
/*     */   public void register(Object object)
/*     */   {
/* 203 */     Multimap methodsInListener = this.finder.findAllSubscribers(object);
/*     */ 
/* 205 */     this.subscribersByTypeLock.writeLock().lock();
/*     */     try {
/* 207 */       this.subscribersByType.putAll(methodsInListener);
/*     */     } finally {
/* 209 */       this.subscribersByTypeLock.writeLock().unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregister(Object object)
/*     */   {
/* 220 */     Multimap methodsInListener = this.finder.findAllSubscribers(object);
/*     */ 
/* 222 */     for (Map.Entry entry : methodsInListener.asMap().entrySet()) {
/* 223 */       Class eventType = (Class)entry.getKey();
/* 224 */       Collection eventMethodsInListener = (Collection)entry.getValue();
/*     */ 
/* 226 */       this.subscribersByTypeLock.writeLock().lock();
/*     */       try {
/* 228 */         Set currentSubscribers = this.subscribersByType.get(eventType);
/* 229 */         if (!currentSubscribers.containsAll(eventMethodsInListener)) {
/* 230 */           throw new IllegalArgumentException("missing event subscriber for an annotated method. Is " + object + " registered?");
/*     */         }
/*     */ 
/* 233 */         currentSubscribers.removeAll(eventMethodsInListener);
/*     */       } finally {
/* 235 */         this.subscribersByTypeLock.writeLock().unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void post(Object event)
/*     */   {
/* 252 */     Set dispatchTypes = flattenHierarchy(event.getClass());
/*     */ 
/* 254 */     boolean dispatched = false;
/* 255 */     for (Class eventType : dispatchTypes) {
/* 256 */       this.subscribersByTypeLock.readLock().lock();
/*     */       try {
/* 258 */         Set wrappers = this.subscribersByType.get(eventType);
/*     */ 
/* 260 */         if (!wrappers.isEmpty()) {
/* 261 */           dispatched = true;
/* 262 */           for (EventSubscriber wrapper : wrappers)
/* 263 */             enqueueEvent(event, wrapper);
/*     */         }
/*     */       }
/*     */       finally {
/* 267 */         this.subscribersByTypeLock.readLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/* 271 */     if ((!dispatched) && (!(event instanceof DeadEvent))) {
/* 272 */       post(new DeadEvent(this, event));
/*     */     }
/*     */ 
/* 275 */     dispatchQueuedEvents();
/*     */   }
/*     */ 
/*     */   void enqueueEvent(Object event, EventSubscriber subscriber)
/*     */   {
/* 284 */     ((Queue)this.eventsToDispatch.get()).offer(new EventWithSubscriber(event, subscriber));
/*     */   }
/*     */ 
/*     */   void dispatchQueuedEvents()
/*     */   {
/* 295 */     if (((Boolean)this.isDispatching.get()).booleanValue()) {
/* 296 */       return;
/*     */     }
/*     */ 
/* 299 */     this.isDispatching.set(Boolean.valueOf(true));
/*     */     try {
/* 301 */       Queue events = (Queue)this.eventsToDispatch.get();
/*     */       EventWithSubscriber eventWithSubscriber;
/* 303 */       while ((eventWithSubscriber = (EventWithSubscriber)events.poll()) != null)
/* 304 */         dispatch(eventWithSubscriber.event, eventWithSubscriber.subscriber);
/*     */     }
/*     */     finally {
/* 307 */       this.isDispatching.remove();
/* 308 */       this.eventsToDispatch.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   void dispatch(Object event, EventSubscriber wrapper)
/*     */   {
/*     */     try
/*     */     {
/* 322 */       wrapper.handleEvent(event);
/*     */     } catch (InvocationTargetException e) {
/*     */       try {
/* 325 */         this.subscriberExceptionHandler.handleException(e.getCause(), new SubscriberExceptionContext(this, event, wrapper.getSubscriber(), wrapper.getMethod()));
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 334 */         Logger.getLogger(EventBus.class.getName()).log(Level.SEVERE, String.format("Exception %s thrown while handling exception: %s", new Object[] { t, e.getCause() }), t);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   Set<Class<?>> flattenHierarchy(Class<?> concreteClass)
/*     */   {
/*     */     try
/*     */     {
/* 354 */       return (Set)flattenHierarchyCache.getUnchecked(concreteClass);
/*     */     } catch (UncheckedExecutionException e) {
/* 356 */       throw Throwables.propagate(e.getCause());
/*     */     }
/*     */   }
/*     */ 
/*     */   static class EventWithSubscriber
/*     */   {
/*     */     final Object event;
/*     */     final EventSubscriber subscriber;
/*     */ 
/*     */     public EventWithSubscriber(Object event, EventSubscriber subscriber)
/*     */     {
/* 395 */       this.event = Preconditions.checkNotNull(event);
/* 396 */       this.subscriber = ((EventSubscriber)Preconditions.checkNotNull(subscriber));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class LoggingSubscriberExceptionHandler
/*     */     implements SubscriberExceptionHandler
/*     */   {
/*     */     private final Logger logger;
/*     */ 
/*     */     public LoggingSubscriberExceptionHandler(String identifier)
/*     */     {
/* 377 */       this.logger = Logger.getLogger(EventBus.class.getName() + "." + (String)Preconditions.checkNotNull(identifier));
/*     */     }
/*     */ 
/*     */     public void handleException(Throwable exception, SubscriberExceptionContext context)
/*     */     {
/* 384 */       this.logger.log(Level.SEVERE, "Could not dispatch event: " + context.getSubscriber() + " to " + context.getSubscriberMethod(), exception.getCause());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.EventBus
 * JD-Core Version:    0.6.2
 */