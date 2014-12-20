/*     */ package com.newrelic.javassist.util.proxy;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.ProtectionDomain;
/*     */ 
/*     */ public class FactoryHelper
/*     */ {
/*     */   private static Method defineClass1;
/*     */   private static Method defineClass2;
/*  77 */   public static final Class[] primitiveTypes = { Boolean.TYPE, Byte.TYPE, Character.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE };
/*     */ 
/*  85 */   public static final String[] wrapperTypes = { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Void" };
/*     */ 
/*  94 */   public static final String[] wrapperDesc = { "(Z)V", "(B)V", "(C)V", "(S)V", "(I)V", "(J)V", "(F)V", "(D)V" };
/*     */ 
/* 105 */   public static final String[] unwarpMethods = { "booleanValue", "byteValue", "charValue", "shortValue", "intValue", "longValue", "floatValue", "doubleValue" };
/*     */ 
/* 114 */   public static final String[] unwrapDesc = { "()Z", "()B", "()C", "()S", "()I", "()J", "()F", "()D" };
/*     */ 
/* 122 */   public static final int[] dataSize = { 1, 1, 1, 1, 1, 2, 1, 2 };
/*     */ 
/*     */   public static final int typeIndex(Class type)
/*     */   {
/*  65 */     Class[] list = primitiveTypes;
/*  66 */     int n = list.length;
/*  67 */     for (int i = 0; i < n; i++) {
/*  68 */       if (list[i] == type)
/*  69 */         return i;
/*     */     }
/*  71 */     throw new RuntimeException("bad type:" + type.getName());
/*     */   }
/*     */ 
/*     */   public static Class toClass(ClassFile cf, ClassLoader loader)
/*     */     throws CannotCompileException
/*     */   {
/* 136 */     return toClass(cf, loader, null);
/*     */   }
/*     */ 
/*     */   public static Class toClass(ClassFile cf, ClassLoader loader, ProtectionDomain domain)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 149 */       byte[] b = toBytecode(cf);
/*     */       Object[] args;
/*     */       Method method;
/*     */       Object[] args;
/* 152 */       if (domain == null) {
/* 153 */         Method method = defineClass1;
/* 154 */         args = new Object[] { cf.getName(), b, new Integer(0), new Integer(b.length) };
/*     */       }
/*     */       else
/*     */       {
/* 158 */         method = defineClass2;
/* 159 */         args = new Object[] { cf.getName(), b, new Integer(0), new Integer(b.length), domain };
/*     */       }
/*     */ 
/* 163 */       return toClass2(method, loader, args);
/*     */     }
/*     */     catch (RuntimeException e) {
/* 166 */       throw e;
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 169 */       throw new CannotCompileException(e.getTargetException());
/*     */     }
/*     */     catch (Exception e) {
/* 172 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized Class toClass2(Method method, ClassLoader loader, Object[] args)
/*     */     throws Exception
/*     */   {
/* 180 */     SecurityActions.setAccessible(method, true);
/* 181 */     Class clazz = (Class)method.invoke(loader, args);
/* 182 */     SecurityActions.setAccessible(method, false);
/* 183 */     return clazz;
/*     */   }
/*     */ 
/*     */   private static byte[] toBytecode(ClassFile cf) throws IOException {
/* 187 */     ByteArrayOutputStream barray = new ByteArrayOutputStream();
/* 188 */     DataOutputStream out = new DataOutputStream(barray);
/*     */     try {
/* 190 */       cf.write(out);
/*     */     }
/*     */     finally {
/* 193 */       out.close();
/*     */     }
/*     */ 
/* 196 */     return barray.toByteArray();
/*     */   }
/*     */ 
/*     */   public static void writeFile(ClassFile cf, String directoryName)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 205 */       writeFile0(cf, directoryName);
/*     */     }
/*     */     catch (IOException e) {
/* 208 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void writeFile0(ClassFile cf, String directoryName) throws CannotCompileException, IOException
/*     */   {
/* 214 */     String classname = cf.getName();
/* 215 */     String filename = directoryName + File.separatorChar + classname.replace('.', File.separatorChar) + ".class";
/*     */ 
/* 217 */     int pos = filename.lastIndexOf(File.separatorChar);
/* 218 */     if (pos > 0) {
/* 219 */       String dir = filename.substring(0, pos);
/* 220 */       if (!dir.equals(".")) {
/* 221 */         new File(dir).mkdirs();
/*     */       }
/*     */     }
/* 224 */     DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
/*     */     try
/*     */     {
/* 227 */       cf.write(out);
/*     */     }
/*     */     catch (IOException e) {
/* 230 */       throw e;
/*     */     }
/*     */     finally {
/* 233 */       out.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  41 */       Class cl = Class.forName("java.lang.ClassLoader");
/*  42 */       defineClass1 = SecurityActions.getDeclaredMethod(cl, "defineClass", new Class[] { String.class, new byte[0].getClass(), Integer.TYPE, Integer.TYPE });
/*     */ 
/*  48 */       defineClass2 = SecurityActions.getDeclaredMethod(cl, "defineClass", new Class[] { String.class, new byte[0].getClass(), Integer.TYPE, Integer.TYPE, ProtectionDomain.class });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  55 */       throw new RuntimeException("cannot initialize");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.FactoryHelper
 * JD-Core Version:    0.6.2
 */