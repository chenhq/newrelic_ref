/*     */ package com.newrelic.javassist.util.proxy;
/*     */ 
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ 
/*     */ class SecurityActions
/*     */ {
/*     */   static Method[] getDeclaredMethods(Class clazz)
/*     */   {
/*  28 */     if (System.getSecurityManager() == null) {
/*  29 */       return clazz.getDeclaredMethods();
/*     */     }
/*  31 */     return (Method[])AccessController.doPrivileged(new PrivilegedAction() {
/*     */       private final Class val$clazz;
/*     */ 
/*  34 */       public Object run() { return this.val$clazz.getDeclaredMethods(); }
/*     */ 
/*     */     });
/*     */   }
/*     */ 
/*     */   static Constructor[] getDeclaredConstructors(Class clazz)
/*     */   {
/*  41 */     if (System.getSecurityManager() == null) {
/*  42 */       return clazz.getDeclaredConstructors();
/*     */     }
/*  44 */     return (Constructor[])AccessController.doPrivileged(new PrivilegedAction() {
/*     */       private final Class val$clazz;
/*     */ 
/*  47 */       public Object run() { return this.val$clazz.getDeclaredConstructors(); }
/*     */ 
/*     */     });
/*     */   }
/*     */ 
/*     */   static Method getDeclaredMethod(Class clazz, final String name, final Class[] types)
/*     */     throws NoSuchMethodException
/*     */   {
/*  55 */     if (System.getSecurityManager() == null)
/*  56 */       return clazz.getDeclaredMethod(name, types);
/*     */     try
/*     */     {
/*  59 */       return (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() { private final Class val$clazz;
/*     */         private final String val$name;
/*     */         private final Class[] val$types;
/*     */ 
/*  62 */         public Object run() throws Exception { return this.val$clazz.getDeclaredMethod(name, types); }
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException e)
/*     */     {
/*  67 */       if ((e.getCause() instanceof NoSuchMethodException)) {
/*  68 */         throw ((NoSuchMethodException)e.getCause());
/*     */       }
/*  70 */       throw new RuntimeException(e.getCause());
/*     */     }
/*     */   }
/*     */ 
/*     */   static Constructor getDeclaredConstructor(Class clazz, final Class[] types)
/*     */     throws NoSuchMethodException
/*     */   {
/*  79 */     if (System.getSecurityManager() == null)
/*  80 */       return clazz.getDeclaredConstructor(types);
/*     */     try
/*     */     {
/*  83 */       return (Constructor)AccessController.doPrivileged(new PrivilegedExceptionAction() { private final Class val$clazz;
/*     */         private final Class[] val$types;
/*     */ 
/*  86 */         public Object run() throws Exception { return this.val$clazz.getDeclaredConstructor(types); }
/*     */       });
/*     */     }
/*     */     catch (PrivilegedActionException e)
/*     */     {
/*  91 */       if ((e.getCause() instanceof NoSuchMethodException)) {
/*  92 */         throw ((NoSuchMethodException)e.getCause());
/*     */       }
/*  94 */       throw new RuntimeException(e.getCause());
/*     */     }
/*     */   }
/*     */ 
/*     */   static void setAccessible(AccessibleObject ao, final boolean accessible)
/*     */   {
/* 101 */     if (System.getSecurityManager() == null)
/* 102 */       ao.setAccessible(accessible);
/*     */     else
/* 104 */       AccessController.doPrivileged(new PrivilegedAction() { private final AccessibleObject val$ao;
/*     */         private final boolean val$accessible;
/*     */ 
/* 106 */         public Object run() { this.val$ao.setAccessible(accessible);
/* 107 */           return null;
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */   static void set(Field fld, final Object target, final Object value)
/*     */     throws IllegalAccessException
/*     */   {
/* 116 */     if (System.getSecurityManager() == null)
/* 117 */       fld.set(target, value);
/*     */     else
/*     */       try {
/* 120 */         AccessController.doPrivileged(new PrivilegedExceptionAction() { private final Field val$fld;
/*     */           private final Object val$target;
/*     */           private final Object val$value;
/*     */ 
/* 122 */           public Object run() throws Exception { this.val$fld.set(target, value);
/* 123 */             return null; }
/*     */         });
/*     */       }
/*     */       catch (PrivilegedActionException e)
/*     */       {
/* 128 */         if ((e.getCause() instanceof NoSuchMethodException)) {
/* 129 */           throw ((IllegalAccessException)e.getCause());
/*     */         }
/* 131 */         throw new RuntimeException(e.getCause());
/*     */       }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.SecurityActions
 * JD-Core Version:    0.6.2
 */