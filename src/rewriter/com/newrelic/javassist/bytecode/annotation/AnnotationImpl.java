/*     */ package com.newrelic.javassist.bytecode.annotation;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.AnnotationDefaultAttribute;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ 
/*     */ public class AnnotationImpl
/*     */   implements InvocationHandler
/*     */ {
/*     */   private static final String JDK_ANNOTATION_CLASS_NAME = "java.lang.annotation.Annotation";
/*  39 */   private static Method JDK_ANNOTATION_TYPE_METHOD = null;
/*     */   private Annotation annotation;
/*     */   private ClassPool pool;
/*     */   private ClassLoader classLoader;
/*     */   private transient Class annotationType;
/*  45 */   private transient int cachedHashCode = -2147483648;
/*     */ 
/*     */   public static Object make(ClassLoader cl, Class clazz, ClassPool cp, Annotation anon)
/*     */   {
/*  70 */     AnnotationImpl handler = new AnnotationImpl(anon, cp, cl);
/*  71 */     return Proxy.newProxyInstance(cl, new Class[] { clazz }, handler);
/*     */   }
/*     */ 
/*     */   private AnnotationImpl(Annotation a, ClassPool cp, ClassLoader loader) {
/*  75 */     this.annotation = a;
/*  76 */     this.pool = cp;
/*  77 */     this.classLoader = loader;
/*     */   }
/*     */ 
/*     */   public String getTypeName()
/*     */   {
/*  86 */     return this.annotation.getTypeName();
/*     */   }
/*     */ 
/*     */   private Class getAnnotationType()
/*     */   {
/*  96 */     if (this.annotationType == null) {
/*  97 */       String typeName = this.annotation.getTypeName();
/*     */       try {
/*  99 */         this.annotationType = this.classLoader.loadClass(typeName);
/*     */       }
/*     */       catch (ClassNotFoundException e) {
/* 102 */         NoClassDefFoundError error = new NoClassDefFoundError("Error loading annotation class: " + typeName);
/* 103 */         error.setStackTrace(e.getStackTrace());
/* 104 */         throw error;
/*     */       }
/*     */     }
/* 107 */     return this.annotationType;
/*     */   }
/*     */ 
/*     */   public Annotation getAnnotation()
/*     */   {
/* 116 */     return this.annotation;
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 129 */     String name = method.getName();
/* 130 */     if (Object.class == method.getDeclaringClass()) {
/* 131 */       if ("equals".equals(name)) {
/* 132 */         Object obj = args[0];
/* 133 */         return new Boolean(checkEquals(obj));
/*     */       }
/* 135 */       if ("toString".equals(name))
/* 136 */         return this.annotation.toString();
/* 137 */       if ("hashCode".equals(name))
/* 138 */         return new Integer(hashCode());
/*     */     }
/* 140 */     else if (("annotationType".equals(name)) && (method.getParameterTypes().length == 0))
/*     */     {
/* 142 */       return getAnnotationType();
/*     */     }
/* 144 */     MemberValue mv = this.annotation.getMemberValue(name);
/* 145 */     if (mv == null) {
/* 146 */       return getDefault(name, method);
/*     */     }
/* 148 */     return mv.getValue(this.classLoader, this.pool, method);
/*     */   }
/*     */ 
/*     */   private Object getDefault(String name, Method method)
/*     */     throws ClassNotFoundException, RuntimeException
/*     */   {
/* 154 */     String classname = this.annotation.getTypeName();
/* 155 */     if (this.pool != null) {
/*     */       try {
/* 157 */         CtClass cc = this.pool.get(classname);
/* 158 */         ClassFile cf = cc.getClassFile2();
/* 159 */         MethodInfo minfo = cf.getMethod(name);
/* 160 */         if (minfo != null) {
/* 161 */           AnnotationDefaultAttribute ainfo = (AnnotationDefaultAttribute)minfo.getAttribute("AnnotationDefault");
/*     */ 
/* 164 */           if (ainfo != null) {
/* 165 */             MemberValue mv = ainfo.getDefaultValue();
/* 166 */             return mv.getValue(this.classLoader, this.pool, method);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NotFoundException e) {
/* 171 */         throw new RuntimeException("cannot find a class file: " + classname);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 176 */     throw new RuntimeException("no default value: " + classname + "." + name + "()");
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 184 */     if (this.cachedHashCode == -2147483648) {
/* 185 */       int hashCode = 0;
/*     */ 
/* 188 */       getAnnotationType();
/*     */ 
/* 190 */       Method[] methods = this.annotationType.getDeclaredMethods();
/* 191 */       for (int i = 0; i < methods.length; i++) {
/* 192 */         String name = methods[i].getName();
/* 193 */         int valueHashCode = 0;
/*     */ 
/* 196 */         MemberValue mv = this.annotation.getMemberValue(name);
/* 197 */         Object value = null;
/*     */         try {
/* 199 */           if (mv != null)
/* 200 */             value = mv.getValue(this.classLoader, this.pool, methods[i]);
/* 201 */           if (value == null)
/* 202 */             value = getDefault(name, methods[i]);
/*     */         }
/*     */         catch (RuntimeException e) {
/* 205 */           throw e;
/*     */         }
/*     */         catch (Exception e) {
/* 208 */           throw new RuntimeException("Error retrieving value " + name + " for annotation " + this.annotation.getTypeName(), e);
/*     */         }
/*     */ 
/* 212 */         if (value != null) {
/* 213 */           if (value.getClass().isArray())
/* 214 */             valueHashCode = arrayHashCode(value);
/*     */           else
/* 216 */             valueHashCode = value.hashCode();
/*     */         }
/* 218 */         hashCode += (127 * name.hashCode() ^ valueHashCode);
/*     */       }
/*     */ 
/* 221 */       this.cachedHashCode = hashCode;
/*     */     }
/* 223 */     return this.cachedHashCode;
/*     */   }
/*     */ 
/*     */   private boolean checkEquals(Object obj)
/*     */     throws Exception
/*     */   {
/* 234 */     if (obj == null) {
/* 235 */       return false;
/*     */     }
/*     */ 
/* 238 */     if ((obj instanceof Proxy)) {
/* 239 */       InvocationHandler ih = Proxy.getInvocationHandler(obj);
/* 240 */       if ((ih instanceof AnnotationImpl)) {
/* 241 */         AnnotationImpl other = (AnnotationImpl)ih;
/* 242 */         return this.annotation.equals(other.annotation);
/*     */       }
/*     */     }
/*     */ 
/* 246 */     Class otherAnnotationType = (Class)JDK_ANNOTATION_TYPE_METHOD.invoke(obj, (Object[])null);
/* 247 */     if (!getAnnotationType().equals(otherAnnotationType)) {
/* 248 */       return false;
/*     */     }
/* 250 */     Method[] methods = this.annotationType.getDeclaredMethods();
/* 251 */     for (int i = 0; i < methods.length; i++) {
/* 252 */       String name = methods[i].getName();
/*     */ 
/* 255 */       MemberValue mv = this.annotation.getMemberValue(name);
/* 256 */       Object value = null;
/* 257 */       Object otherValue = null;
/*     */       try {
/* 259 */         if (mv != null)
/* 260 */           value = mv.getValue(this.classLoader, this.pool, methods[i]);
/* 261 */         if (value == null)
/* 262 */           value = getDefault(name, methods[i]);
/* 263 */         otherValue = methods[i].invoke(obj, (Object[])null);
/*     */       }
/*     */       catch (RuntimeException e) {
/* 266 */         throw e;
/*     */       }
/*     */       catch (Exception e) {
/* 269 */         throw new RuntimeException("Error retrieving value " + name + " for annotation " + this.annotation.getTypeName(), e);
/*     */       }
/*     */ 
/* 272 */       if ((value == null) && (otherValue != null))
/* 273 */         return false;
/* 274 */       if ((value != null) && (!value.equals(otherValue))) {
/* 275 */         return false;
/*     */       }
/*     */     }
/* 278 */     return true;
/*     */   }
/*     */ 
/*     */   private static int arrayHashCode(Object object)
/*     */   {
/* 290 */     if (object == null) {
/* 291 */       return 0;
/*     */     }
/* 293 */     int result = 1;
/*     */ 
/* 295 */     Object[] array = (Object[])object;
/* 296 */     for (int i = 0; i < array.length; i++) {
/* 297 */       int elementHashCode = 0;
/* 298 */       if (array[i] != null)
/* 299 */         elementHashCode = array[i].hashCode();
/* 300 */       result = 31 * result + elementHashCode;
/*     */     }
/* 302 */     return result;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  50 */       Class clazz = Class.forName("java.lang.annotation.Annotation");
/*  51 */       JDK_ANNOTATION_TYPE_METHOD = clazz.getMethod("annotationType", (Class[])null);
/*     */     }
/*     */     catch (Exception ignored)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.annotation.AnnotationImpl
 * JD-Core Version:    0.6.2
 */