/*    */ package com.newrelic.com.google.common.reflect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Proxy;
/*    */ 
/*    */ @Beta
/*    */ public final class Reflection
/*    */ {
/*    */   public static String getPackageName(Class<?> clazz)
/*    */   {
/* 41 */     return getPackageName(clazz.getName());
/*    */   }
/*    */ 
/*    */   public static String getPackageName(String classFullName)
/*    */   {
/* 50 */     int lastDot = classFullName.lastIndexOf('.');
/* 51 */     return lastDot < 0 ? "" : classFullName.substring(0, lastDot);
/*    */   }
/*    */ 
/*    */   public static void initialize(Class<?>[] classes)
/*    */   {
/* 67 */     for (Class clazz : classes)
/*    */       try {
/* 69 */         Class.forName(clazz.getName(), true, clazz.getClassLoader());
/*    */       } catch (ClassNotFoundException e) {
/* 71 */         throw new AssertionError(e);
/*    */       }
/*    */   }
/*    */ 
/*    */   public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler)
/*    */   {
/* 88 */     Preconditions.checkNotNull(handler);
/* 89 */     Preconditions.checkArgument(interfaceType.isInterface(), "%s is not an interface", new Object[] { interfaceType });
/* 90 */     Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[] { interfaceType }, handler);
/*    */ 
/* 94 */     return interfaceType.cast(object);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.reflect.Reflection
 * JD-Core Version:    0.6.2
 */