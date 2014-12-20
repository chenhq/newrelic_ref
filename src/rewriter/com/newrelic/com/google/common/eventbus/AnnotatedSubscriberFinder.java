/*     */ package com.newrelic.com.google.common.eventbus;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Objects;
/*     */ import com.newrelic.com.google.common.base.Throwables;
/*     */ import com.newrelic.com.google.common.cache.CacheBuilder;
/*     */ import com.newrelic.com.google.common.cache.CacheLoader;
/*     */ import com.newrelic.com.google.common.cache.LoadingCache;
/*     */ import com.newrelic.com.google.common.collect.HashMultimap;
/*     */ import com.newrelic.com.google.common.collect.ImmutableList;
/*     */ import com.newrelic.com.google.common.collect.Maps;
/*     */ import com.newrelic.com.google.common.collect.Multimap;
/*     */ import com.newrelic.com.google.common.reflect.TypeToken;
/*     */ import com.newrelic.com.google.common.reflect.TypeToken.TypeSet;
/*     */ import com.newrelic.com.google.common.util.concurrent.UncheckedExecutionException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ class AnnotatedSubscriberFinder
/*     */   implements SubscriberFindingStrategy
/*     */ {
/*  53 */   private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader()
/*     */   {
/*     */     public ImmutableList<Method> load(Class<?> concreteClass)
/*     */       throws Exception
/*     */     {
/*  59 */       return AnnotatedSubscriberFinder.getAnnotatedMethodsInternal(concreteClass);
/*     */     }
/*     */   });
/*     */ 
/*     */   public Multimap<Class<?>, EventSubscriber> findAllSubscribers(Object listener)
/*     */   {
/*  70 */     Multimap methodsInListener = HashMultimap.create();
/*  71 */     Class clazz = listener.getClass();
/*  72 */     for (Method method : getAnnotatedMethods(clazz)) {
/*  73 */       Class[] parameterTypes = method.getParameterTypes();
/*  74 */       Class eventType = parameterTypes[0];
/*  75 */       EventSubscriber subscriber = makeSubscriber(listener, method);
/*  76 */       methodsInListener.put(eventType, subscriber);
/*     */     }
/*  78 */     return methodsInListener;
/*     */   }
/*     */ 
/*     */   private static ImmutableList<Method> getAnnotatedMethods(Class<?> clazz) {
/*     */     try {
/*  83 */       return (ImmutableList)subscriberMethodsCache.getUnchecked(clazz);
/*     */     } catch (UncheckedExecutionException e) {
/*  85 */       throw Throwables.propagate(e.getCause());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static ImmutableList<Method> getAnnotatedMethodsInternal(Class<?> clazz)
/*     */   {
/* 114 */     Set supers = TypeToken.of(clazz).getTypes().rawTypes();
/* 115 */     Map identifiers = Maps.newHashMap();
/* 116 */     for (Class superClazz : supers) {
/* 117 */       for (Method superClazzMethod : superClazz.getMethods()) {
/* 118 */         if (superClazzMethod.isAnnotationPresent(Subscribe.class)) {
/* 119 */           Class[] parameterTypes = superClazzMethod.getParameterTypes();
/* 120 */           if (parameterTypes.length != 1) {
/* 121 */             throw new IllegalArgumentException("Method " + superClazzMethod + " has @Subscribe annotation, but requires " + parameterTypes.length + " arguments.  Event subscriber methods must require a single argument.");
/*     */           }
/*     */ 
/* 126 */           MethodIdentifier ident = new MethodIdentifier(superClazzMethod);
/* 127 */           if (!identifiers.containsKey(ident)) {
/* 128 */             identifiers.put(ident, superClazzMethod);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 133 */     return ImmutableList.copyOf(identifiers.values());
/*     */   }
/*     */ 
/*     */   private static EventSubscriber makeSubscriber(Object listener, Method method)
/*     */   {
/*     */     EventSubscriber wrapper;
/*     */     EventSubscriber wrapper;
/* 149 */     if (methodIsDeclaredThreadSafe(method))
/* 150 */       wrapper = new EventSubscriber(listener, method);
/*     */     else {
/* 152 */       wrapper = new SynchronizedEventSubscriber(listener, method);
/*     */     }
/* 154 */     return wrapper;
/*     */   }
/*     */ 
/*     */   private static boolean methodIsDeclaredThreadSafe(Method method)
/*     */   {
/* 166 */     return method.getAnnotation(AllowConcurrentEvents.class) != null;
/*     */   }
/*     */ 
/*     */   private static final class MethodIdentifier
/*     */   {
/*     */     private final String name;
/*     */     private final List<Class<?>> parameterTypes;
/*     */ 
/*     */     MethodIdentifier(Method method)
/*     */     {
/*  94 */       this.name = method.getName();
/*  95 */       this.parameterTypes = Arrays.asList(method.getParameterTypes());
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 100 */       return Objects.hashCode(new Object[] { this.name, this.parameterTypes });
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object o)
/*     */     {
/* 105 */       if ((o instanceof MethodIdentifier)) {
/* 106 */         MethodIdentifier ident = (MethodIdentifier)o;
/* 107 */         return (this.name.equals(ident.name)) && (this.parameterTypes.equals(ident.parameterTypes));
/*     */       }
/* 109 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.AnnotatedSubscriberFinder
 * JD-Core Version:    0.6.2
 */