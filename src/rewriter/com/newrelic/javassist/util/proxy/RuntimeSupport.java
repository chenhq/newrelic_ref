/*     */ package com.newrelic.javassist.util.proxy;
/*     */ 
/*     */ import java.io.InvalidClassException;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public class RuntimeSupport
/*     */ {
/*  30 */   public static MethodHandler default_interceptor = new DefaultMethodHandler();
/*     */ 
/*     */   public static void find2Methods(Object self, String superMethod, String thisMethod, int index, String desc, Method[] methods)
/*     */   {
/*  52 */     synchronized (methods) {
/*  53 */       if (methods[index] == null) {
/*  54 */         methods[(index + 1)] = (thisMethod == null ? null : findMethod(self, thisMethod, desc));
/*     */ 
/*  56 */         methods[index] = findSuperMethod(self, superMethod, desc);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Method findMethod(Object self, String name, String desc)
/*     */   {
/*  68 */     Method m = findMethod2(self.getClass(), name, desc);
/*  69 */     if (m == null) {
/*  70 */       error(self, name, desc);
/*     */     }
/*  72 */     return m;
/*     */   }
/*     */ 
/*     */   public static Method findSuperMethod(Object self, String name, String desc)
/*     */   {
/*  82 */     Class clazz = self.getClass();
/*  83 */     Method m = findSuperMethod2(clazz.getSuperclass(), name, desc);
/*  84 */     if (m == null) {
/*  85 */       m = searchInterfaces(clazz, name, desc);
/*     */     }
/*  87 */     if (m == null) {
/*  88 */       error(self, name, desc);
/*     */     }
/*  90 */     return m;
/*     */   }
/*     */ 
/*     */   private static void error(Object self, String name, String desc) {
/*  94 */     throw new RuntimeException("not found " + name + ":" + desc + " in " + self.getClass().getName());
/*     */   }
/*     */ 
/*     */   private static Method findSuperMethod2(Class clazz, String name, String desc)
/*     */   {
/*  99 */     Method m = findMethod2(clazz, name, desc);
/* 100 */     if (m != null) {
/* 101 */       return m;
/*     */     }
/* 103 */     Class superClass = clazz.getSuperclass();
/* 104 */     if (superClass != null) {
/* 105 */       m = findSuperMethod2(superClass, name, desc);
/* 106 */       if (m != null) {
/* 107 */         return m;
/*     */       }
/*     */     }
/* 110 */     return searchInterfaces(clazz, name, desc);
/*     */   }
/*     */ 
/*     */   private static Method searchInterfaces(Class clazz, String name, String desc) {
/* 114 */     Method m = null;
/* 115 */     Class[] interfaces = clazz.getInterfaces();
/* 116 */     for (int i = 0; i < interfaces.length; i++) {
/* 117 */       m = findSuperMethod2(interfaces[i], name, desc);
/* 118 */       if (m != null) {
/* 119 */         return m;
/*     */       }
/*     */     }
/* 122 */     return m;
/*     */   }
/*     */ 
/*     */   private static Method findMethod2(Class clazz, String name, String desc) {
/* 126 */     Method[] methods = SecurityActions.getDeclaredMethods(clazz);
/* 127 */     int n = methods.length;
/* 128 */     for (int i = 0; i < n; i++)
/* 129 */       if ((methods[i].getName().equals(name)) && (makeDescriptor(methods[i]).equals(desc)))
/*     */       {
/* 131 */         return methods[i];
/*     */       }
/* 133 */     return null;
/*     */   }
/*     */ 
/*     */   public static String makeDescriptor(Method m)
/*     */   {
/* 140 */     Class[] params = m.getParameterTypes();
/* 141 */     return makeDescriptor(params, m.getReturnType());
/*     */   }
/*     */ 
/*     */   public static String makeDescriptor(Class[] params, Class retType)
/*     */   {
/* 151 */     StringBuffer sbuf = new StringBuffer();
/* 152 */     sbuf.append('(');
/* 153 */     for (int i = 0; i < params.length; i++) {
/* 154 */       makeDesc(sbuf, params[i]);
/*     */     }
/* 156 */     sbuf.append(')');
/* 157 */     makeDesc(sbuf, retType);
/* 158 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   private static void makeDesc(StringBuffer sbuf, Class type) {
/* 162 */     if (type.isArray()) {
/* 163 */       sbuf.append('[');
/* 164 */       makeDesc(sbuf, type.getComponentType());
/*     */     }
/* 166 */     else if (type.isPrimitive()) {
/* 167 */       if (type == Void.TYPE)
/* 168 */         sbuf.append('V');
/* 169 */       else if (type == Integer.TYPE)
/* 170 */         sbuf.append('I');
/* 171 */       else if (type == Byte.TYPE)
/* 172 */         sbuf.append('B');
/* 173 */       else if (type == Long.TYPE)
/* 174 */         sbuf.append('J');
/* 175 */       else if (type == Double.TYPE)
/* 176 */         sbuf.append('D');
/* 177 */       else if (type == Float.TYPE)
/* 178 */         sbuf.append('F');
/* 179 */       else if (type == Character.TYPE)
/* 180 */         sbuf.append('C');
/* 181 */       else if (type == Short.TYPE)
/* 182 */         sbuf.append('S');
/* 183 */       else if (type == Boolean.TYPE)
/* 184 */         sbuf.append('Z');
/*     */       else
/* 186 */         throw new RuntimeException("bad type: " + type.getName());
/*     */     }
/*     */     else {
/* 189 */       sbuf.append('L').append(type.getName().replace('.', '/')).append(';');
/*     */     }
/*     */   }
/*     */ 
/*     */   public static SerializedProxy makeSerializedProxy(Object proxy)
/*     */     throws InvalidClassException
/*     */   {
/* 203 */     Class clazz = proxy.getClass();
/*     */ 
/* 205 */     MethodHandler methodHandler = null;
/* 206 */     if ((proxy instanceof ProxyObject)) {
/* 207 */       methodHandler = ((ProxyObject)proxy).getHandler();
/*     */     }
/* 209 */     return new SerializedProxy(clazz, ProxyFactory.getFilterSignature(clazz), methodHandler);
/*     */   }
/*     */ 
/*     */   static class DefaultMethodHandler
/*     */     implements MethodHandler, Serializable
/*     */   {
/*     */     public Object invoke(Object self, Method m, Method proceed, Object[] args)
/*     */       throws Exception
/*     */     {
/*  37 */       return proceed.invoke(self, args);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.RuntimeSupport
 * JD-Core Version:    0.6.2
 */