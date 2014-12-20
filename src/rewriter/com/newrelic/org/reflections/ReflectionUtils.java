/*     */ package com.newrelic.org.reflections;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Predicate;
/*     */ import com.newrelic.com.google.common.base.Predicates;
/*     */ import com.newrelic.com.google.common.collect.Collections2;
/*     */ import com.newrelic.com.google.common.collect.ImmutableSet;
/*     */ import com.newrelic.com.google.common.collect.Iterables;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import com.newrelic.com.google.common.collect.Sets;
/*     */ import com.newrelic.org.reflections.util.ClasspathHelper;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.AnnotatedElement;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public abstract class ReflectionUtils
/*     */ {
/*     */   private static List<String> primitiveNames;
/*     */   private static List<Class> primitiveTypes;
/*     */   private static List<String> primitiveDescriptors;
/*     */ 
/*     */   public static Set<Class<?>> getAllSuperTypes(Class<?> type, Predicate<? super Class<?>> predicate)
/*     */   {
/*  67 */     Set result = Sets.newHashSet();
/*  68 */     if (type != null) {
/*  69 */       result.add(type);
/*  70 */       result.addAll(getAllSuperTypes(type.getSuperclass(), Predicates.alwaysTrue()));
/*  71 */       for (Class inter : type.getInterfaces()) {
/*  72 */         result.addAll(getAllSuperTypes(inter, Predicates.alwaysTrue()));
/*     */       }
/*     */     }
/*     */ 
/*  76 */     return ImmutableSet.copyOf(Collections2.filter(result, predicate));
/*     */   }
/*     */ 
/*     */   public static Set<Class<?>> getAllSuperTypes(Iterable<? extends Class<?>> types, Predicate<? super Class<?>> predicate) {
/*  80 */     Set result = Sets.newHashSet();
/*     */     Class type;
/*  80 */     for (Iterator i$ = types.iterator(); i$.hasNext(); result.addAll(getAllSuperTypes(type, predicate))) type = (Class)i$.next();
/*  81 */     return result;
/*     */   }
/*     */ 
/*     */   public static Set<Field> getAllFields(Class<?> type, Predicate<? super Field> predicate)
/*     */   {
/*  92 */     Set result = Sets.newHashSet();
/*     */     Class t;
/*  93 */     for (Iterator i$ = getAllSuperTypes(type, Predicates.alwaysTrue()).iterator(); i$.hasNext(); Collections.addAll(result, t.getDeclaredFields())) t = (Class)i$.next();
/*     */ 
/*  95 */     return ImmutableSet.copyOf(Collections2.filter(result, predicate));
/*     */   }
/*     */ 
/*     */   public static Set<Field> getAllFields(Iterable<? extends Class<?>> types, Predicate<? super Field> predicate) {
/*  99 */     Set result = Sets.newHashSet();
/*     */     Class type;
/*  99 */     for (Iterator i$ = types.iterator(); i$.hasNext(); result.addAll(getAllFields(type, predicate))) type = (Class)i$.next();
/* 100 */     return result;
/*     */   }
/*     */ 
/*     */   public static Set<Method> getAllMethods(Class<?> type, Predicate<? super Method> predicate)
/*     */   {
/* 114 */     Set result = Sets.newHashSet();
/* 115 */     for (Class t : getAllSuperTypes(type, Predicates.alwaysTrue())) {
/* 116 */       Collections.addAll(result, t.isInterface() ? t.getMethods() : t.getDeclaredMethods());
/*     */     }
/*     */ 
/* 119 */     return ImmutableSet.copyOf(Collections2.filter(result, predicate));
/*     */   }
/*     */ 
/*     */   public static Set<Method> getAllMethods(Iterable<? extends Class<?>> types, Predicate<? super Method> predicate)
/*     */   {
/* 124 */     Set result = Sets.newHashSet();
/*     */     Class type;
/* 124 */     for (Iterator i$ = types.iterator(); i$.hasNext(); result.addAll(getAllMethods(type, predicate))) type = (Class)i$.next();
/*     */ 
/* 126 */     return ImmutableSet.copyOf(result);
/*     */   }
/*     */ 
/*     */   public static <T extends AnnotatedElement> Set<T> getAll(Iterable<? extends T> elements, Predicate<? super T> predicate)
/*     */   {
/* 131 */     return ImmutableSet.copyOf(Iterables.filter(elements, predicate));
/*     */   }
/*     */ 
/*     */   public static <T extends Member> Predicate<T> withName(String name)
/*     */   {
/* 136 */     return new Predicate() {
/*     */       public boolean apply(@Nullable T input) {
/* 138 */         return (input != null) && (input.getName().equals(this.val$name));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T extends Member> Predicate<T> withPrefix(String prefix)
/*     */   {
/* 145 */     return new Predicate() {
/*     */       public boolean apply(@Nullable T input) {
/* 147 */         return (input != null) && (input.getName().startsWith(this.val$prefix));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T extends AnnotatedElement> Predicate<T> withAnnotation(Class<? extends Annotation> annotation)
/*     */   {
/* 154 */     return new Predicate() {
/*     */       public boolean apply(@Nullable T input) {
/* 156 */         return (input != null) && (input.isAnnotationPresent(this.val$annotation));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T extends AnnotatedElement> Predicate<T> withAnnotations(Class<? extends Annotation>[] annotations)
/*     */   {
/* 163 */     return new Predicate() {
/*     */       public boolean apply(@Nullable T input) {
/* 165 */         return (input != null) && (Arrays.equals(this.val$annotations, input.getAnnotations()));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T extends AnnotatedElement> Predicate<T> withAnnotation(Annotation annotation)
/*     */   {
/* 172 */     return new Predicate() {
/*     */       public boolean apply(@Nullable T input) {
/* 174 */         return (input != null) && (input.isAnnotationPresent(this.val$annotation.annotationType())) && (ReflectionUtils.areAnnotationMembersMatching(input.getAnnotation(this.val$annotation.annotationType()), this.val$annotation));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T extends AnnotatedElement> Predicate<T> withAnnotations(Annotation[] annotations)
/*     */   {
/* 182 */     return new Predicate() {
/*     */       public boolean apply(@Nullable T input) {
/* 184 */         if (input != null) {
/* 185 */           Annotation[] inputAnnotations = input.getAnnotations();
/* 186 */           if (inputAnnotations.length == this.val$annotations.length) {
/* 187 */             for (int i = 0; i < inputAnnotations.length; i++) {
/* 188 */               if (!ReflectionUtils.areAnnotationMembersMatching(inputAnnotations[i], this.val$annotations[i])) return false;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 193 */         return true;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Predicate<Method> withParameters(Class<?>[] types)
/*     */   {
/* 200 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Method input) {
/* 202 */         return (input != null) && (Arrays.equals(input.getParameterTypes(), this.val$types));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Predicate<Method> withParametersAssignableTo(Class[] types)
/*     */   {
/* 209 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Method input) {
/* 211 */         if (input != null) {
/* 212 */           Class[] parameterTypes = input.getParameterTypes();
/* 213 */           if (parameterTypes.length == this.val$types.length) {
/* 214 */             for (int i = 0; i < parameterTypes.length; i++) {
/* 215 */               if (!this.val$types[i].isAssignableFrom(parameterTypes[i])) {
/* 216 */                 return false;
/*     */               }
/*     */             }
/* 219 */             return true;
/*     */           }
/*     */         }
/* 222 */         return false;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Predicate<Method> withParametersCount(int count)
/*     */   {
/* 229 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Method input) {
/* 231 */         return (input != null) && (input.getParameterTypes().length == this.val$count);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Predicate<Method> withParameterAnnotations(Annotation[] annotations)
/*     */   {
/* 238 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Method input) {
/* 240 */         if ((input != null) && (this.val$annotations != null)) {
/* 241 */           Annotation[][] parameterAnnotations = input.getParameterAnnotations();
/* 242 */           if (this.val$annotations.length == parameterAnnotations.length)
/* 243 */             for (int i = 0; i < parameterAnnotations.length; i++) {
/* 244 */               boolean any = false;
/* 245 */               for (Annotation annotation : parameterAnnotations[i])
/* 246 */                 if (ReflectionUtils.areAnnotationMembersMatching(this.val$annotations[i], annotation)) { any = true; break;
/*     */                 }
/* 248 */               if (!any) return false;
/*     */             }
/*     */           else {
/* 251 */             return false;
/*     */           }
/*     */         }
/* 254 */         return true;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static Predicate<Method> withParameterAnnotations(Class<? extends Annotation>[] annotationClasses)
/*     */   {
/* 261 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Method input) {
/* 263 */         if ((input != null) && (this.val$annotationClasses != null)) {
/* 264 */           Annotation[][] parameterAnnotations = input.getParameterAnnotations();
/* 265 */           if (this.val$annotationClasses.length == parameterAnnotations.length)
/* 266 */             for (int i = 0; i < parameterAnnotations.length; i++) {
/* 267 */               boolean any = false;
/* 268 */               for (Annotation annotation : parameterAnnotations[i])
/* 269 */                 if (this.val$annotationClasses[i].equals(annotation.annotationType())) { any = true; break;
/*     */                 }
/* 271 */               if (!any) return false;
/*     */             }
/*     */           else {
/* 274 */             return false;
/*     */           }
/*     */         }
/* 277 */         return true;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<Field> withType(Class<T> type)
/*     */   {
/* 284 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Field input) {
/* 286 */         return (input != null) && (input.getType().equals(this.val$type));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<Field> withTypeAssignableTo(Class<T> type)
/*     */   {
/* 293 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Field input) {
/* 295 */         return (input != null) && (this.val$type.isAssignableFrom(input.getType()));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<Method> withReturnType(Class<T> type)
/*     */   {
/* 302 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Method input) {
/* 304 */         return (input != null) && (input.getReturnType().equals(this.val$type));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<Method> withReturnTypeAssignableTo(Class<T> type)
/*     */   {
/* 311 */     return new Predicate() {
/*     */       public boolean apply(@Nullable Method input) {
/* 313 */         return (input != null) && (this.val$type.isAssignableFrom(input.getReturnType()));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T extends Member> Predicate<T> withModifier(int mod)
/*     */   {
/* 326 */     return new Predicate() {
/*     */       public boolean apply(@Nullable T input) {
/* 328 */         return (input != null) && ((input.getModifiers() & this.val$mod) != 0);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static boolean areAnnotationMembersMatching(Annotation annotation1, Annotation annotation2)
/*     */   {
/* 335 */     if ((annotation2 != null) && (annotation1.annotationType() == annotation2.annotationType())) {
/* 336 */       for (Method method : annotation1.annotationType().getDeclaredMethods()) {
/*     */         try {
/* 338 */           if (!method.invoke(annotation1, new Object[0]).equals(method.invoke(annotation2, new Object[0])))
/* 339 */             return false;
/*     */         }
/*     */         catch (Exception e) {
/* 342 */           throw new ReflectionsException(String.format("could not invoke method %s on annotation %s", new Object[] { method.getName(), annotation1.annotationType() }), e);
/*     */         }
/*     */       }
/* 345 */       return true;
/*     */     }
/*     */ 
/* 348 */     return false;
/*     */   }
/*     */ 
/*     */   public static <T extends AnnotatedElement> Set<T> getMatchingAnnotations(Set<T> annotatedElements, Annotation annotation)
/*     */   {
/* 355 */     Set result = Sets.newHashSet();
/*     */ 
/* 357 */     for (AnnotatedElement annotatedElement : annotatedElements) {
/* 358 */       Annotation annotation1 = annotatedElement.getAnnotation(annotation.annotationType());
/* 359 */       if (areAnnotationMembersMatching(annotation, annotation1)) {
/* 360 */         result.add(annotatedElement);
/*     */       }
/*     */     }
/*     */ 
/* 364 */     return result;
/*     */   }
/*     */ 
/*     */   public static Class<?> forName(String typeName, ClassLoader[] classLoaders)
/*     */   {
/* 371 */     if (getPrimitiveNames().contains(typeName))
/* 372 */       return (Class)getPrimitiveTypes().get(getPrimitiveNames().indexOf(typeName));
/*     */     String type;
/* 375 */     if (typeName.contains("[")) {
/* 376 */       int i = typeName.indexOf("[");
/* 377 */       String type = typeName.substring(0, i);
/* 378 */       String array = typeName.substring(i).replace("]", "");
/*     */ 
/* 380 */       if (getPrimitiveNames().contains(type))
/* 381 */         type = (String)getPrimitiveDescriptors().get(getPrimitiveNames().indexOf(type));
/*     */       else {
/* 383 */         type = "L" + type + ";";
/*     */       }
/*     */ 
/* 386 */       type = array + type;
/*     */     } else {
/* 388 */       type = typeName;
/*     */     }
/*     */ 
/* 391 */     for (ClassLoader classLoader : ClasspathHelper.classLoaders(classLoaders)) try {
/* 392 */         return Class.forName(type, false, classLoader);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */       } return null;
/*     */   }
/*     */ 
/*     */   public static <T> List<Class<? extends T>> forNames(Iterable<String> classes, ClassLoader[] classLoaders)
/*     */   {
/* 402 */     List result = new ArrayList();
/* 403 */     for (String className : classes)
/*     */     {
/* 405 */       result.add(forName(className, classLoaders));
/*     */     }
/* 407 */     return result;
/*     */   }
/*     */ 
/*     */   public static List<String> names(Iterable<Class<?>> types) {
/* 411 */     List result = Lists.newArrayList();
/* 412 */     for (Class type : types) {
/* 413 */       result.add(type.getName());
/*     */     }
/* 415 */     return result;
/*     */   }
/*     */ 
/*     */   public static List<String> getPrimitiveNames() {
/* 419 */     return primitiveNames == null ? (ReflectionUtils.primitiveNames = Lists.newArrayList(new String[] { "boolean", "char", "byte", "short", "int", "long", "float", "double", "void" })) : primitiveNames;
/*     */   }
/*     */ 
/*     */   public static List<Class> getPrimitiveTypes() {
/* 423 */     return primitiveTypes == null ? (ReflectionUtils.primitiveTypes = Lists.newArrayList(new Class[] { Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE })) : primitiveTypes;
/*     */   }
/*     */ 
/*     */   public static List<String> getPrimitiveDescriptors() {
/* 427 */     return primitiveDescriptors == null ? (ReflectionUtils.primitiveDescriptors = Lists.newArrayList(new String[] { "Z", "C", "B", "S", "I", "J", "F", "D", "V" })) : primitiveDescriptors;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.ReflectionUtils
 * JD-Core Version:    0.6.2
 */