/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.FieldInfo;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ public class SerialVersionUID
/*     */ {
/*     */   public static void setSerialVersionUID(CtClass clazz)
/*     */     throws CannotCompileException, NotFoundException
/*     */   {
/*     */     try
/*     */     {
/*  42 */       clazz.getDeclaredField("serialVersionUID");
/*  43 */       return;
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/*  48 */       if (!isSerializable(clazz)) {
/*  49 */         return;
/*     */       }
/*     */ 
/*  52 */       CtField field = new CtField(CtClass.longType, "serialVersionUID", clazz);
/*     */ 
/*  54 */       field.setModifiers(26);
/*     */ 
/*  56 */       clazz.addField(field, calculateDefault(clazz) + "L");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isSerializable(CtClass clazz)
/*     */     throws NotFoundException
/*     */   {
/*  65 */     ClassPool pool = clazz.getClassPool();
/*  66 */     return clazz.subtypeOf(pool.get("java.io.Serializable"));
/*     */   }
/*     */ 
/*     */   static long calculateDefault(CtClass clazz)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/*  77 */       ByteArrayOutputStream bout = new ByteArrayOutputStream();
/*  78 */       DataOutputStream out = new DataOutputStream(bout);
/*  79 */       ClassFile classFile = clazz.getClassFile();
/*     */ 
/*  82 */       String javaName = javaName(clazz);
/*  83 */       out.writeUTF(javaName);
/*     */ 
/*  85 */       CtMethod[] methods = clazz.getDeclaredMethods();
/*     */ 
/*  88 */       int classMods = clazz.getModifiers();
/*  89 */       if ((classMods & 0x200) != 0) {
/*  90 */         if (methods.length > 0)
/*  91 */           classMods |= 1024;
/*     */         else
/*  93 */           classMods &= -1025;
/*     */       }
/*  95 */       out.writeInt(classMods);
/*     */ 
/*  98 */       String[] interfaces = classFile.getInterfaces();
/*  99 */       for (int i = 0; i < interfaces.length; i++) {
/* 100 */         interfaces[i] = javaName(interfaces[i]);
/*     */       }
/* 102 */       Arrays.sort(interfaces);
/* 103 */       for (int i = 0; i < interfaces.length; i++) {
/* 104 */         out.writeUTF(interfaces[i]);
/*     */       }
/*     */ 
/* 107 */       CtField[] fields = clazz.getDeclaredFields();
/* 108 */       Arrays.sort(fields, new Comparator() {
/*     */         public int compare(Object o1, Object o2) {
/* 110 */           CtField field1 = (CtField)o1;
/* 111 */           CtField field2 = (CtField)o2;
/* 112 */           return field1.getName().compareTo(field2.getName());
/*     */         }
/*     */       });
/* 116 */       for (int i = 0; i < fields.length; i++) {
/* 117 */         CtField field = fields[i];
/* 118 */         int mods = field.getModifiers();
/* 119 */         if (((mods & 0x2) == 0) || ((mods & 0x88) == 0))
/*     */         {
/* 121 */           out.writeUTF(field.getName());
/* 122 */           out.writeInt(mods);
/* 123 */           out.writeUTF(field.getFieldInfo2().getDescriptor());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 128 */       if (classFile.getStaticInitializer() != null) {
/* 129 */         out.writeUTF("<clinit>");
/* 130 */         out.writeInt(8);
/* 131 */         out.writeUTF("()V");
/*     */       }
/*     */ 
/* 135 */       CtConstructor[] constructors = clazz.getDeclaredConstructors();
/* 136 */       Arrays.sort(constructors, new Comparator() {
/*     */         public int compare(Object o1, Object o2) {
/* 138 */           CtConstructor c1 = (CtConstructor)o1;
/* 139 */           CtConstructor c2 = (CtConstructor)o2;
/* 140 */           return c1.getMethodInfo2().getDescriptor().compareTo(c2.getMethodInfo2().getDescriptor());
/*     */         }
/*     */       });
/* 145 */       for (int i = 0; i < constructors.length; i++) {
/* 146 */         CtConstructor constructor = constructors[i];
/* 147 */         int mods = constructor.getModifiers();
/* 148 */         if ((mods & 0x2) == 0) {
/* 149 */           out.writeUTF("<init>");
/* 150 */           out.writeInt(mods);
/* 151 */           out.writeUTF(constructor.getMethodInfo2().getDescriptor().replace('/', '.'));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 157 */       Arrays.sort(methods, new Comparator() {
/*     */         public int compare(Object o1, Object o2) {
/* 159 */           CtMethod m1 = (CtMethod)o1;
/* 160 */           CtMethod m2 = (CtMethod)o2;
/* 161 */           int value = m1.getName().compareTo(m2.getName());
/* 162 */           if (value == 0) {
/* 163 */             value = m1.getMethodInfo2().getDescriptor().compareTo(m2.getMethodInfo2().getDescriptor());
/*     */           }
/*     */ 
/* 166 */           return value;
/*     */         }
/*     */       });
/* 170 */       for (int i = 0; i < methods.length; i++) {
/* 171 */         CtMethod method = methods[i];
/* 172 */         int mods = method.getModifiers() & 0xD3F;
/*     */ 
/* 177 */         if ((mods & 0x2) == 0) {
/* 178 */           out.writeUTF(method.getName());
/* 179 */           out.writeInt(mods);
/* 180 */           out.writeUTF(method.getMethodInfo2().getDescriptor().replace('/', '.'));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 186 */       out.flush();
/* 187 */       MessageDigest digest = MessageDigest.getInstance("SHA");
/* 188 */       byte[] digested = digest.digest(bout.toByteArray());
/* 189 */       long hash = 0L;
/* 190 */       for (int i = Math.min(digested.length, 8) - 1; i >= 0; i--) {
/* 191 */         hash = hash << 8 | digested[i] & 0xFF;
/*     */       }
/* 193 */       return hash;
/*     */     }
/*     */     catch (IOException e) {
/* 196 */       throw new CannotCompileException(e);
/*     */     }
/*     */     catch (NoSuchAlgorithmException e) {
/* 199 */       throw new CannotCompileException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String javaName(CtClass clazz) {
/* 204 */     return Descriptor.toJavaName(Descriptor.toJvmName(clazz));
/*     */   }
/*     */ 
/*     */   private static String javaName(String name) {
/* 208 */     return Descriptor.toJavaName(Descriptor.toJvmName(name));
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.SerialVersionUID
 * JD-Core Version:    0.6.2
 */