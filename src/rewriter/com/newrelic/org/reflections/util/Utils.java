/*     */ package com.newrelic.org.reflections.util;
/*     */ 
/*     */ import com.newrelic.org.reflections.ReflectionUtils;
/*     */ import com.newrelic.org.reflections.ReflectionsException;
/*     */ import com.newrelic.org.slf4j.Logger;
/*     */ import com.newrelic.org.slf4j.LoggerFactory;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public abstract class Utils
/*     */ {
/*     */   public static String repeat(String string, int times)
/*     */   {
/*  23 */     StringBuilder sb = new StringBuilder();
/*     */ 
/*  25 */     for (int i = 0; i < times; i++) {
/*  26 */       sb.append(string);
/*     */     }
/*     */ 
/*  29 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(String s)
/*     */   {
/*  36 */     return (s == null) || (s.length() == 0);
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(Object[] objects) {
/*  40 */     return (objects == null) || (objects.length == 0);
/*     */   }
/*     */ 
/*     */   public static File prepareFile(String filename) {
/*  44 */     File file = new File(filename);
/*  45 */     File parent = file.getAbsoluteFile().getParentFile();
/*  46 */     if (!parent.exists())
/*     */     {
/*  48 */       parent.mkdirs();
/*     */     }
/*  50 */     return file;
/*     */   }
/*     */ 
/*     */   public static Method getMethodFromDescriptor(String descriptor, ClassLoader[] classLoaders)
/*     */     throws ReflectionsException
/*     */   {
/*  56 */     int p0 = descriptor.indexOf('(');
/*  57 */     String methodKey = descriptor.substring(0, p0);
/*  58 */     String methodParameters = descriptor.substring(p0 + 1, descriptor.length() - 1);
/*     */ 
/*  60 */     int p1 = methodKey.lastIndexOf('.');
/*  61 */     String className = methodKey.substring(methodKey.lastIndexOf(' ') + 1, p1);
/*  62 */     String methodName = methodKey.substring(p1 + 1);
/*     */ 
/*  64 */     Class[] parameterTypes = null;
/*  65 */     if (!isEmpty(methodParameters)) {
/*  66 */       String[] parameterNames = methodParameters.split(", ");
/*  67 */       List result = new ArrayList(parameterNames.length);
/*  68 */       for (String className1 : parameterNames)
/*     */       {
/*  70 */         result.add(ReflectionUtils.forName(className1, new ClassLoader[0]));
/*     */       }
/*  72 */       parameterTypes = (Class[])result.toArray(new Class[result.size()]);
/*     */     }
/*     */ 
/*  75 */     Class aClass = ReflectionUtils.forName(className, classLoaders);
/*     */     try {
/*  77 */       if (descriptor.contains("<init>"))
/*     */       {
/*  79 */         return null;
/*     */       }
/*  81 */       return aClass.getDeclaredMethod(methodName, parameterTypes);
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/*  84 */       throw new ReflectionsException("Can't resolve method named " + methodName, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Field getFieldFromString(String field, ClassLoader[] classLoaders)
/*     */   {
/*  90 */     String className = field.substring(0, field.lastIndexOf('.'));
/*  91 */     String fieldName = field.substring(field.lastIndexOf('.') + 1);
/*     */     try
/*     */     {
/*  94 */       return ReflectionUtils.forName(className, classLoaders).getDeclaredField(fieldName);
/*     */     } catch (NoSuchFieldException e) {
/*  96 */       throw new ReflectionsException("Can't resolve field named " + fieldName, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void close(InputStream closeable) {
/*     */     try { if (closeable != null) closeable.close();  } catch (IOException e) {
/* 102 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   @Nullable
/*     */   public static Logger findLogger(Class<?> aClass) {
/*     */     try { Class.forName("com.newrelic.org.slf4j.impl.StaticLoggerBinder");
/* 108 */       return LoggerFactory.getLogger(aClass); } catch (Throwable e) {
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.util.Utils
 * JD-Core Version:    0.6.2
 */