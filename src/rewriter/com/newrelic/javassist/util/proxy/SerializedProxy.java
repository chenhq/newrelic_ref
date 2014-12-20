/*    */ package com.newrelic.javassist.util.proxy;
/*    */ 
/*    */ import java.io.InvalidClassException;
/*    */ import java.io.InvalidObjectException;
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedActionException;
/*    */ import java.security.PrivilegedExceptionAction;
/*    */ 
/*    */ class SerializedProxy
/*    */   implements Serializable
/*    */ {
/*    */   private String superClass;
/*    */   private String[] interfaces;
/*    */   private byte[] filterSignature;
/*    */   private MethodHandler handler;
/*    */ 
/*    */   SerializedProxy(Class proxy, byte[] sig, MethodHandler h)
/*    */   {
/* 38 */     this.filterSignature = sig;
/* 39 */     this.handler = h;
/* 40 */     this.superClass = proxy.getSuperclass().getName();
/* 41 */     Class[] infs = proxy.getInterfaces();
/* 42 */     int n = infs.length;
/* 43 */     this.interfaces = new String[n - 1];
/* 44 */     String setterInf = ProxyObject.class.getName();
/* 45 */     for (int i = 0; i < n; i++) {
/* 46 */       String name = infs[i].getName();
/* 47 */       if (!name.equals(setterInf))
/* 48 */         this.interfaces[i] = name;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected Class loadClass(final String className)
/*    */     throws ClassNotFoundException
/*    */   {
/*    */     try
/*    */     {
/* 61 */       return (Class)AccessController.doPrivileged(new PrivilegedExceptionAction() { private final String val$className;
/*    */ 
/* 63 */         public Object run() throws Exception { ClassLoader cl = Thread.currentThread().getContextClassLoader();
/* 64 */           return Class.forName(className, true, cl); }
/*    */       });
/*    */     }
/*    */     catch (PrivilegedActionException pae)
/*    */     {
/* 69 */       throw new RuntimeException("cannot load the class: " + className, pae.getException());
/*    */     }
/*    */   }
/*    */ 
/*    */   Object readResolve() throws ObjectStreamException {
/*    */     try {
/* 75 */       int n = this.interfaces.length;
/* 76 */       Class[] infs = new Class[n];
/* 77 */       for (int i = 0; i < n; i++) {
/* 78 */         infs[i] = loadClass(this.interfaces[i]);
/*    */       }
/* 80 */       ProxyFactory f = new ProxyFactory();
/* 81 */       f.setSuperclass(loadClass(this.superClass));
/* 82 */       f.setInterfaces(infs);
/* 83 */       ProxyObject proxy = (ProxyObject)f.createClass(this.filterSignature).newInstance();
/* 84 */       proxy.setHandler(this.handler);
/* 85 */       return proxy;
/*    */     }
/*    */     catch (ClassNotFoundException e) {
/* 88 */       throw new InvalidClassException(e.getMessage());
/*    */     }
/*    */     catch (InstantiationException e2) {
/* 91 */       throw new InvalidObjectException(e2.getMessage());
/*    */     }
/*    */     catch (IllegalAccessException e3) {
/* 94 */       throw new InvalidClassException(e3.getMessage());
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.SerializedProxy
 * JD-Core Version:    0.6.2
 */