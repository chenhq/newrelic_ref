/*     */ package com.newrelic.javassist.tools.rmi;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtConstructor;
/*     */ import com.newrelic.javassist.CtField;
/*     */ import com.newrelic.javassist.CtField.Initializer;
/*     */ import com.newrelic.javassist.CtMethod;
/*     */ import com.newrelic.javassist.CtMethod.ConstParameter;
/*     */ import com.newrelic.javassist.CtNewConstructor;
/*     */ import com.newrelic.javassist.CtNewMethod;
/*     */ import com.newrelic.javassist.Modifier;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.Translator;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class StubGenerator
/*     */   implements Translator
/*     */ {
/*     */   private static final String fieldImporter = "importer";
/*     */   private static final String fieldObjectId = "objectId";
/*     */   private static final String accessorObjectId = "_getObjectId";
/*     */   private static final String sampleClass = "com.newrelic.javassist.tools.rmi.Sample";
/*     */   private ClassPool classPool;
/*     */   private Hashtable proxyClasses;
/*     */   private CtMethod forwardMethod;
/*     */   private CtMethod forwardStaticMethod;
/*     */   private CtClass[] proxyConstructorParamTypes;
/*     */   private CtClass[] interfacesForProxy;
/*     */   private CtClass[] exceptionForProxy;
/*     */ 
/*     */   public StubGenerator()
/*     */   {
/*  61 */     this.proxyClasses = new Hashtable();
/*     */   }
/*     */ 
/*     */   public void start(ClassPool pool)
/*     */     throws NotFoundException
/*     */   {
/*  71 */     this.classPool = pool;
/*  72 */     CtClass c = pool.get("com.newrelic.javassist.tools.rmi.Sample");
/*  73 */     this.forwardMethod = c.getDeclaredMethod("forward");
/*  74 */     this.forwardStaticMethod = c.getDeclaredMethod("forwardStatic");
/*     */ 
/*  76 */     this.proxyConstructorParamTypes = pool.get(new String[] { "com.newrelic.javassist.tools.rmi.ObjectImporter", "int" });
/*     */ 
/*  79 */     this.interfacesForProxy = pool.get(new String[] { "java.io.Serializable", "com.newrelic.javassist.tools.rmi.Proxy" });
/*     */ 
/*  82 */     this.exceptionForProxy = new CtClass[] { pool.get("com.newrelic.javassist.tools.rmi.RemoteException") };
/*     */   }
/*     */ 
/*     */   public void onLoad(ClassPool pool, String classname)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isProxyClass(String name)
/*     */   {
/* 100 */     return this.proxyClasses.get(name) != null;
/*     */   }
/*     */ 
/*     */   public synchronized boolean makeProxyClass(Class clazz)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 115 */     String classname = clazz.getName();
/* 116 */     if (this.proxyClasses.get(classname) != null) {
/* 117 */       return false;
/*     */     }
/* 119 */     CtClass ctclazz = produceProxyClass(this.classPool.get(classname), clazz);
/*     */ 
/* 121 */     this.proxyClasses.put(classname, ctclazz);
/* 122 */     modifySuperclass(ctclazz);
/* 123 */     return true;
/*     */   }
/*     */ 
/*     */   private CtClass produceProxyClass(CtClass orgclass, Class orgRtClass)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 130 */     int modify = orgclass.getModifiers();
/* 131 */     if ((Modifier.isAbstract(modify)) || (Modifier.isNative(modify)) || (!Modifier.isPublic(modify)))
/*     */     {
/* 133 */       throw new CannotCompileException(orgclass.getName() + " must be public, non-native, and non-abstract.");
/*     */     }
/*     */ 
/* 136 */     CtClass proxy = this.classPool.makeClass(orgclass.getName(), orgclass.getSuperclass());
/*     */ 
/* 139 */     proxy.setInterfaces(this.interfacesForProxy);
/*     */ 
/* 141 */     CtField f = new CtField(this.classPool.get("com.newrelic.javassist.tools.rmi.ObjectImporter"), "importer", proxy);
/*     */ 
/* 144 */     f.setModifiers(2);
/* 145 */     proxy.addField(f, CtField.Initializer.byParameter(0));
/*     */ 
/* 147 */     f = new CtField(CtClass.intType, "objectId", proxy);
/* 148 */     f.setModifiers(2);
/* 149 */     proxy.addField(f, CtField.Initializer.byParameter(1));
/*     */ 
/* 151 */     proxy.addMethod(CtNewMethod.getter("_getObjectId", f));
/*     */ 
/* 153 */     proxy.addConstructor(CtNewConstructor.defaultConstructor(proxy));
/* 154 */     CtConstructor cons = CtNewConstructor.skeleton(this.proxyConstructorParamTypes, null, proxy);
/*     */ 
/* 157 */     proxy.addConstructor(cons);
/*     */     try
/*     */     {
/* 160 */       addMethods(proxy, orgRtClass.getMethods());
/* 161 */       return proxy;
/*     */     }
/*     */     catch (SecurityException e) {
/* 164 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private CtClass toCtClass(Class rtclass)
/*     */     throws NotFoundException
/*     */   {
/*     */     String name;
/*     */     String name;
/* 170 */     if (!rtclass.isArray()) {
/* 171 */       name = rtclass.getName();
/*     */     } else {
/* 173 */       StringBuffer sbuf = new StringBuffer();
/*     */       do {
/* 175 */         sbuf.append("[]");
/* 176 */         rtclass = rtclass.getComponentType();
/* 177 */       }while (rtclass.isArray());
/* 178 */       sbuf.insert(0, rtclass.getName());
/* 179 */       name = sbuf.toString();
/*     */     }
/*     */ 
/* 182 */     return this.classPool.get(name);
/*     */   }
/*     */ 
/*     */   private CtClass[] toCtClass(Class[] rtclasses) throws NotFoundException {
/* 186 */     int n = rtclasses.length;
/* 187 */     CtClass[] ctclasses = new CtClass[n];
/* 188 */     for (int i = 0; i < n; i++) {
/* 189 */       ctclasses[i] = toCtClass(rtclasses[i]);
/*     */     }
/* 191 */     return ctclasses;
/*     */   }
/*     */ 
/*     */   private void addMethods(CtClass proxy, Method[] ms)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/* 201 */     for (int i = 0; i < ms.length; i++) {
/* 202 */       Method m = ms[i];
/* 203 */       int mod = m.getModifiers();
/* 204 */       if ((m.getDeclaringClass() != Object.class) && (!Modifier.isFinal(mod)))
/*     */       {
/* 206 */         if (Modifier.isPublic(mod))
/*     */         {
/*     */           CtMethod body;
/*     */           CtMethod body;
/* 208 */           if (Modifier.isStatic(mod))
/* 209 */             body = this.forwardStaticMethod;
/*     */           else {
/* 211 */             body = this.forwardMethod;
/*     */           }
/* 213 */           CtMethod wmethod = CtNewMethod.wrapped(toCtClass(m.getReturnType()), m.getName(), toCtClass(m.getParameterTypes()), this.exceptionForProxy, body, CtMethod.ConstParameter.integer(i), proxy);
/*     */ 
/* 221 */           wmethod.setModifiers(mod);
/* 222 */           proxy.addMethod(wmethod);
/*     */         }
/* 224 */         else if ((!Modifier.isProtected(mod)) && (!Modifier.isPrivate(mod)))
/*     */         {
/* 227 */           throw new CannotCompileException("the methods must be public, protected, or private.");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void modifySuperclass(CtClass orgclass)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/*     */     CtClass superclazz;
/* 239 */     for (; ; orgclass = superclazz) {
/* 240 */       superclazz = orgclass.getSuperclass();
/* 241 */       if (superclazz == null)
/*     */         break;
/*     */       try
/*     */       {
/* 245 */         superclazz.getDeclaredConstructor(null);
/*     */       }
/*     */       catch (NotFoundException e)
/*     */       {
/* 251 */         superclazz.addConstructor(CtNewConstructor.defaultConstructor(superclazz));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.rmi.StubGenerator
 * JD-Core Version:    0.6.2
 */