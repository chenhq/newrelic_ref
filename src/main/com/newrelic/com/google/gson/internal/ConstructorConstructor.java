/*     */ package com.newrelic.com.google.gson.internal;
/*     */ 
/*     */ import com.newrelic.com.google.gson.InstanceCreator;
/*     */ import com.newrelic.com.google.gson.JsonIOException;
/*     */ import com.newrelic.com.google.gson.reflect.TypeToken;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public final class ConstructorConstructor
/*     */ {
/*     */   private final Map<Type, InstanceCreator<?>> instanceCreators;
/*     */ 
/*     */   public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators)
/*     */   {
/*  48 */     this.instanceCreators = instanceCreators;
/*     */   }
/*     */ 
/*     */   public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
/*  52 */     final Type type = typeToken.getType();
/*  53 */     Class rawType = typeToken.getRawType();
/*     */ 
/*  58 */     final InstanceCreator typeCreator = (InstanceCreator)this.instanceCreators.get(type);
/*  59 */     if (typeCreator != null) {
/*  60 */       return new ObjectConstructor() {
/*     */         public T construct() {
/*  62 */           return typeCreator.createInstance(type);
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/*  69 */     final InstanceCreator rawTypeCreator = (InstanceCreator)this.instanceCreators.get(rawType);
/*     */ 
/*  71 */     if (rawTypeCreator != null) {
/*  72 */       return new ObjectConstructor() {
/*     */         public T construct() {
/*  74 */           return rawTypeCreator.createInstance(type);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*  79 */     ObjectConstructor defaultConstructor = newDefaultConstructor(rawType);
/*  80 */     if (defaultConstructor != null) {
/*  81 */       return defaultConstructor;
/*     */     }
/*     */ 
/*  84 */     ObjectConstructor defaultImplementation = newDefaultImplementationConstructor(type, rawType);
/*  85 */     if (defaultImplementation != null) {
/*  86 */       return defaultImplementation;
/*     */     }
/*     */ 
/*  90 */     return newUnsafeAllocator(type, rawType);
/*     */   }
/*     */ 
/*     */   private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
/*     */     try {
/*  95 */       final Constructor constructor = rawType.getDeclaredConstructor(new Class[0]);
/*  96 */       if (!constructor.isAccessible()) {
/*  97 */         constructor.setAccessible(true);
/*     */       }
/*  99 */       return new ObjectConstructor()
/*     */       {
/*     */         public T construct() {
/*     */           try {
/* 103 */             Object[] args = null;
/* 104 */             return constructor.newInstance(args);
/*     */           }
/*     */           catch (InstantiationException e) {
/* 107 */             throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
/*     */           }
/*     */           catch (InvocationTargetException e)
/*     */           {
/* 111 */             throw new RuntimeException("Failed to invoke " + constructor + " with no args", e.getTargetException());
/*     */           }
/*     */           catch (IllegalAccessException e) {
/* 114 */             throw new AssertionError(e);
/*     */           }
/*     */         } } ;
/*     */     } catch (NoSuchMethodException e) {
/*     */     }
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */   private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> rawType)
/*     */   {
/* 130 */     if (Collection.class.isAssignableFrom(rawType)) {
/* 131 */       if (SortedSet.class.isAssignableFrom(rawType))
/* 132 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 134 */             return new TreeSet();
/*     */           }
/*     */         };
/* 137 */       if (EnumSet.class.isAssignableFrom(rawType)) {
/* 138 */         return new ObjectConstructor()
/*     */         {
/*     */           public T construct() {
/* 141 */             if ((type instanceof ParameterizedType)) {
/* 142 */               Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
/* 143 */               if ((elementType instanceof Class)) {
/* 144 */                 return EnumSet.noneOf((Class)elementType);
/*     */               }
/* 146 */               throw new JsonIOException("Invalid EnumSet type: " + type.toString());
/*     */             }
/*     */ 
/* 149 */             throw new JsonIOException("Invalid EnumSet type: " + type.toString());
/*     */           }
/*     */         };
/*     */       }
/* 153 */       if (Set.class.isAssignableFrom(rawType))
/* 154 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 156 */             return new LinkedHashSet();
/*     */           }
/*     */         };
/* 159 */       if (Queue.class.isAssignableFrom(rawType)) {
/* 160 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 162 */             return new LinkedList();
/*     */           }
/*     */         };
/*     */       }
/* 166 */       return new ObjectConstructor() {
/*     */         public T construct() {
/* 168 */           return new ArrayList();
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/* 174 */     if (Map.class.isAssignableFrom(rawType)) {
/* 175 */       if (SortedMap.class.isAssignableFrom(rawType))
/* 176 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 178 */             return new TreeMap();
/*     */           }
/*     */         };
/* 181 */       if (((type instanceof ParameterizedType)) && (!String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType())))
/*     */       {
/* 183 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 185 */             return new LinkedHashMap();
/*     */           }
/*     */         };
/*     */       }
/* 189 */       return new ObjectConstructor() {
/*     */         public T construct() {
/* 191 */           return new LinkedTreeMap();
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/* 197 */     return null;
/*     */   }
/*     */ 
/*     */   private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> rawType)
/*     */   {
/* 202 */     return new ObjectConstructor() {
/* 203 */       private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
/*     */ 
/*     */       public T construct() {
/*     */         try {
/* 207 */           return this.unsafeAllocator.newInstance(rawType);
/*     */         }
/*     */         catch (Exception e) {
/* 210 */           throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", e);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 218 */     return this.instanceCreators.toString();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.com.google.gson.internal.ConstructorConstructor
 * JD-Core Version:    0.6.2
 */