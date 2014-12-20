/*     */ package com.newrelic.javassist.tools.reflect;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class ClassMetaobject
/*     */   implements Serializable
/*     */ {
/*     */   static final String methodPrefix = "_m_";
/*     */   static final int methodPrefixLen = 3;
/*     */   private Class javaClass;
/*     */   private Constructor[] constructors;
/*     */   private Method[] methods;
/*  62 */   public static boolean useContextClassLoader = false;
/*     */ 
/*     */   public ClassMetaobject(String[] params)
/*     */   {
/*     */     try
/*     */     {
/*  73 */       this.javaClass = getClassObject(params[0]);
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/*  76 */       throw new RuntimeException("not found: " + params[0] + ", useContextClassLoader: " + Boolean.toString(useContextClassLoader), e);
/*     */     }
/*     */ 
/*  81 */     this.constructors = this.javaClass.getConstructors();
/*  82 */     this.methods = null;
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/*  86 */     out.writeUTF(this.javaClass.getName());
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  92 */     this.javaClass = getClassObject(in.readUTF());
/*  93 */     this.constructors = this.javaClass.getConstructors();
/*  94 */     this.methods = null;
/*     */   }
/*     */ 
/*     */   private Class getClassObject(String name) throws ClassNotFoundException {
/*  98 */     if (useContextClassLoader) {
/*  99 */       return Thread.currentThread().getContextClassLoader().loadClass(name);
/*     */     }
/*     */ 
/* 102 */     return Class.forName(name);
/*     */   }
/*     */ 
/*     */   public final Class getJavaClass()
/*     */   {
/* 109 */     return this.javaClass;
/*     */   }
/*     */ 
/*     */   public final String getName()
/*     */   {
/* 116 */     return this.javaClass.getName();
/*     */   }
/*     */ 
/*     */   public final boolean isInstance(Object obj)
/*     */   {
/* 123 */     return this.javaClass.isInstance(obj);
/*     */   }
/*     */ 
/*     */   public final Object newInstance(Object[] args)
/*     */     throws CannotCreateException
/*     */   {
/* 134 */     int n = this.constructors.length;
/* 135 */     for (int i = 0; i < n; i++) {
/*     */       try {
/* 137 */         return this.constructors[i].newInstance(args);
/*     */       }
/*     */       catch (IllegalArgumentException e)
/*     */       {
/*     */       }
/*     */       catch (InstantiationException e) {
/* 143 */         throw new CannotCreateException(e);
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 146 */         throw new CannotCreateException(e);
/*     */       }
/*     */       catch (InvocationTargetException e) {
/* 149 */         throw new CannotCreateException(e);
/*     */       }
/*     */     }
/*     */ 
/* 153 */     throw new CannotCreateException("no constructor matches");
/*     */   }
/*     */ 
/*     */   public Object trapFieldRead(String name)
/*     */   {
/* 164 */     Class jc = getJavaClass();
/*     */     try {
/* 166 */       return jc.getField(name).get(null);
/*     */     }
/*     */     catch (NoSuchFieldException e) {
/* 169 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 172 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void trapFieldWrite(String name, Object value)
/*     */   {
/* 184 */     Class jc = getJavaClass();
/*     */     try {
/* 186 */       jc.getField(name).set(null, value);
/*     */     }
/*     */     catch (NoSuchFieldException e) {
/* 189 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 192 */       throw new RuntimeException(e.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Object invoke(Object target, int identifier, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 205 */     Method[] allmethods = target.getClass().getMethods();
/* 206 */     int n = allmethods.length;
/* 207 */     String head = "_m_" + identifier;
/* 208 */     for (int i = 0; i < n; i++) {
/* 209 */       if (allmethods[i].getName().startsWith(head)) {
/*     */         try {
/* 211 */           return allmethods[i].invoke(target, args);
/*     */         } catch (InvocationTargetException e) {
/* 213 */           throw e.getTargetException();
/*     */         } catch (IllegalAccessException e) {
/* 215 */           throw new CannotInvokeException(e);
/*     */         }
/*     */       }
/*     */     }
/* 219 */     throw new CannotInvokeException("cannot find a method");
/*     */   }
/*     */ 
/*     */   public Object trapMethodcall(int identifier, Object[] args)
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 234 */       Method[] m = getReflectiveMethods();
/* 235 */       return m[identifier].invoke(null, args);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 238 */       throw e.getTargetException();
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 241 */       throw new CannotInvokeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final Method[] getReflectiveMethods()
/*     */   {
/* 250 */     if (this.methods != null) {
/* 251 */       return this.methods;
/*     */     }
/* 253 */     Class baseclass = getJavaClass();
/* 254 */     Method[] allmethods = baseclass.getDeclaredMethods();
/* 255 */     int n = allmethods.length;
/* 256 */     int[] index = new int[n];
/* 257 */     int max = 0;
/* 258 */     for (int i = 0; i < n; i++) {
/* 259 */       Method m = allmethods[i];
/* 260 */       String mname = m.getName();
/* 261 */       if (mname.startsWith("_m_")) {
/* 262 */         int k = 0;
/* 263 */         for (int j = 3; ; j++) {
/* 264 */           char c = mname.charAt(j);
/* 265 */           if (('0' > c) || (c > '9')) break;
/* 266 */           k = k * 10 + c - 48;
/*     */         }
/*     */ 
/* 271 */         index[i] = (++k);
/* 272 */         if (k > max) {
/* 273 */           max = k;
/*     */         }
/*     */       }
/*     */     }
/* 277 */     this.methods = new Method[max];
/* 278 */     for (int i = 0; i < n; i++) {
/* 279 */       if (index[i] > 0)
/* 280 */         this.methods[(index[i] - 1)] = allmethods[i];
/*     */     }
/* 282 */     return this.methods;
/*     */   }
/*     */ 
/*     */   public final Method getMethod(int identifier)
/*     */   {
/* 298 */     return getReflectiveMethods()[identifier];
/*     */   }
/*     */ 
/*     */   public final String getMethodName(int identifier)
/*     */   {
/* 306 */     String mname = getReflectiveMethods()[identifier].getName();
/* 307 */     int j = 3;
/*     */     while (true) {
/* 309 */       char c = mname.charAt(j++);
/* 310 */       if ((c < '0') || ('9' < c)) {
/*     */         break;
/*     */       }
/*     */     }
/* 314 */     return mname.substring(j);
/*     */   }
/*     */ 
/*     */   public final Class[] getParameterTypes(int identifier)
/*     */   {
/* 323 */     return getReflectiveMethods()[identifier].getParameterTypes();
/*     */   }
/*     */ 
/*     */   public final Class getReturnType(int identifier)
/*     */   {
/* 331 */     return getReflectiveMethods()[identifier].getReturnType();
/*     */   }
/*     */ 
/*     */   public final int getMethodIndex(String originalName, Class[] argTypes)
/*     */     throws NoSuchMethodException
/*     */   {
/* 355 */     Method[] mthds = getReflectiveMethods();
/* 356 */     for (int i = 0; i < mthds.length; i++)
/* 357 */       if (mthds[i] != null)
/*     */       {
/* 361 */         if ((getMethodName(i).equals(originalName)) && (Arrays.equals(argTypes, mthds[i].getParameterTypes())))
/*     */         {
/* 363 */           return i;
/*     */         }
/*     */       }
/* 366 */     throw new NoSuchMethodException("Method " + originalName + " not found");
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.reflect.ClassMetaobject
 * JD-Core Version:    0.6.2
 */