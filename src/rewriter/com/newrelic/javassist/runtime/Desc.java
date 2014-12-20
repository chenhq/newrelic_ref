/*     */ package com.newrelic.javassist.runtime;
/*     */ 
/*     */ public class Desc
/*     */ {
/*  34 */   public static boolean useContextClassLoader = false;
/*     */ 
/*     */   private static Class getClassObject(String name)
/*     */     throws ClassNotFoundException
/*     */   {
/*  39 */     if (useContextClassLoader) {
/*  40 */       return Thread.currentThread().getContextClassLoader().loadClass(name);
/*     */     }
/*     */ 
/*  43 */     return Class.forName(name);
/*     */   }
/*     */ 
/*     */   public static Class getClazz(String name)
/*     */   {
/*     */     try
/*     */     {
/*  52 */       return getClassObject(name);
/*     */     }
/*     */     catch (ClassNotFoundException e) {
/*  55 */       throw new RuntimeException("$class: internal error, could not find class '" + name + "' (Desc.useContextClassLoader: " + Boolean.toString(useContextClassLoader) + ")", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Class[] getParams(String desc)
/*     */   {
/*  67 */     if (desc.charAt(0) != '(') {
/*  68 */       throw new RuntimeException("$sig: internal error");
/*     */     }
/*  70 */     return getType(desc, desc.length(), 1, 0);
/*     */   }
/*     */ 
/*     */   public static Class getType(String desc)
/*     */   {
/*  78 */     Class[] result = getType(desc, desc.length(), 0, 0);
/*  79 */     if ((result == null) || (result.length != 1)) {
/*  80 */       throw new RuntimeException("$type: internal error");
/*     */     }
/*  82 */     return result[0];
/*     */   }
/*     */ 
/*     */   private static Class[] getType(String desc, int descLen, int start, int num)
/*     */   {
/*  88 */     if (start >= descLen) {
/*  89 */       return new Class[num];
/*     */     }
/*  91 */     char c = desc.charAt(start);
/*     */     Class clazz;
/*  92 */     switch (c) {
/*     */     case 'Z':
/*  94 */       clazz = Boolean.TYPE;
/*  95 */       break;
/*     */     case 'C':
/*  97 */       clazz = Character.TYPE;
/*  98 */       break;
/*     */     case 'B':
/* 100 */       clazz = Byte.TYPE;
/* 101 */       break;
/*     */     case 'S':
/* 103 */       clazz = Short.TYPE;
/* 104 */       break;
/*     */     case 'I':
/* 106 */       clazz = Integer.TYPE;
/* 107 */       break;
/*     */     case 'J':
/* 109 */       clazz = Long.TYPE;
/* 110 */       break;
/*     */     case 'F':
/* 112 */       clazz = Float.TYPE;
/* 113 */       break;
/*     */     case 'D':
/* 115 */       clazz = Double.TYPE;
/* 116 */       break;
/*     */     case 'V':
/* 118 */       clazz = Void.TYPE;
/* 119 */       break;
/*     */     case 'L':
/*     */     case '[':
/* 122 */       return getClassType(desc, descLen, start, num);
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'W':
/*     */     case 'X':
/*     */     case 'Y':
/*     */     default:
/* 124 */       return new Class[num];
/*     */     }
/*     */ 
/* 127 */     Class[] result = getType(desc, descLen, start + 1, num + 1);
/* 128 */     result[num] = clazz;
/* 129 */     return result;
/*     */   }
/*     */ 
/*     */   private static Class[] getClassType(String desc, int descLen, int start, int num)
/*     */   {
/* 134 */     int end = start;
/* 135 */     while (desc.charAt(end) == '[') {
/* 136 */       end++;
/*     */     }
/* 138 */     if (desc.charAt(end) == 'L') {
/* 139 */       end = desc.indexOf(';', end);
/* 140 */       if (end < 0)
/* 141 */         throw new IndexOutOfBoundsException("bad descriptor");
/*     */     }
/*     */     String cname;
/*     */     String cname;
/* 145 */     if (desc.charAt(start) == 'L')
/* 146 */       cname = desc.substring(start + 1, end);
/*     */     else {
/* 148 */       cname = desc.substring(start, end + 1);
/*     */     }
/* 150 */     Class[] result = getType(desc, descLen, end + 1, num + 1);
/*     */     try {
/* 152 */       result[num] = getClassObject(cname.replace('/', '.'));
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 156 */       throw new RuntimeException(e.getMessage());
/*     */     }
/*     */ 
/* 159 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.runtime.Desc
 * JD-Core Version:    0.6.2
 */