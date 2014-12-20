/*     */ package com.newrelic.org.reflections.adapters;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Joiner;
/*     */ import com.newrelic.com.google.common.cache.Cache;
/*     */ import com.newrelic.com.google.common.cache.CacheBuilder;
/*     */ import com.newrelic.com.google.common.cache.CacheLoader;
/*     */ import com.newrelic.com.google.common.cache.LoadingCache;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import com.newrelic.javassist.bytecode.AccessFlag;
/*     */ import com.newrelic.javassist.bytecode.AnnotationsAttribute;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.Descriptor.Iterator;
/*     */ import com.newrelic.javassist.bytecode.FieldInfo;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.bytecode.ParameterAnnotationsAttribute;
/*     */ import com.newrelic.javassist.bytecode.annotation.Annotation;
/*     */ import com.newrelic.org.reflections.ReflectionsException;
/*     */ import com.newrelic.org.reflections.util.Utils;
/*     */ import com.newrelic.org.reflections.vfs.Vfs.File;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class JavassistAdapter
/*     */   implements MetadataAdapter<ClassFile, FieldInfo, MethodInfo>
/*     */ {
/*  29 */   public static boolean includeInvisibleTag = true;
/*     */ 
/*     */   @Nullable
/*     */   private Cache<Vfs.File, ClassFile> classFileCache;
/*     */ 
/*     */   public JavassistAdapter() { try { this.classFileCache = CacheBuilder.newBuilder().softValues().weakKeys().maximumSize(16L).expireAfterWrite(500L, TimeUnit.MILLISECONDS).build(new CacheLoader()
/*     */       {
/*     */         public ClassFile load(Vfs.File key) throws Exception {
/*  38 */           return JavassistAdapter.this.createClassObject(key);
/*     */         }
/*     */       });
/*     */     } catch (Error e) {
/*  42 */       this.classFileCache = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<FieldInfo> getFields(ClassFile cls)
/*     */   {
/*  48 */     return cls.getFields();
/*     */   }
/*     */ 
/*     */   public List<MethodInfo> getMethods(ClassFile cls)
/*     */   {
/*  53 */     return cls.getMethods();
/*     */   }
/*     */ 
/*     */   public String getMethodName(MethodInfo method) {
/*  57 */     return method.getName();
/*     */   }
/*     */ 
/*     */   public List<String> getParameterNames(MethodInfo method) {
/*  61 */     String descriptor = method.getDescriptor();
/*  62 */     descriptor = descriptor.substring(descriptor.indexOf("(") + 1, descriptor.lastIndexOf(")"));
/*  63 */     return splitDescriptorToTypeNames(descriptor);
/*     */   }
/*     */ 
/*     */   public List<String> getClassAnnotationNames(ClassFile aClass) {
/*  67 */     return getAnnotationNames(new AnnotationsAttribute[] { (AnnotationsAttribute)aClass.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)aClass.getAttribute("RuntimeInvisibleAnnotations") : null });
/*     */   }
/*     */ 
/*     */   public List<String> getFieldAnnotationNames(FieldInfo field)
/*     */   {
/*  72 */     return getAnnotationNames(new AnnotationsAttribute[] { (AnnotationsAttribute)field.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)field.getAttribute("RuntimeInvisibleAnnotations") : null });
/*     */   }
/*     */ 
/*     */   public List<String> getMethodAnnotationNames(MethodInfo method)
/*     */   {
/*  77 */     return getAnnotationNames(new AnnotationsAttribute[] { (AnnotationsAttribute)method.getAttribute("RuntimeVisibleAnnotations"), includeInvisibleTag ? (AnnotationsAttribute)method.getAttribute("RuntimeInvisibleAnnotations") : null });
/*     */   }
/*     */ 
/*     */   public List<String> getParameterAnnotationNames(MethodInfo method, int parameterIndex)
/*     */   {
/*  82 */     List result = Lists.newArrayList();
/*     */ 
/*  84 */     List parameterAnnotationsAttributes = Lists.newArrayList(new ParameterAnnotationsAttribute[] { (ParameterAnnotationsAttribute)method.getAttribute("RuntimeVisibleParameterAnnotations"), (ParameterAnnotationsAttribute)method.getAttribute("RuntimeInvisibleParameterAnnotations") });
/*     */ 
/*  87 */     if (parameterAnnotationsAttributes != null) {
/*  88 */       for (ParameterAnnotationsAttribute parameterAnnotationsAttribute : parameterAnnotationsAttributes) {
/*  89 */         Annotation[][] annotations = parameterAnnotationsAttribute.getAnnotations();
/*  90 */         if (parameterIndex < annotations.length) {
/*  91 */           Annotation[] annotation = annotations[parameterIndex];
/*  92 */           result.addAll(getAnnotationNames(annotation));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  97 */     return result;
/*     */   }
/*     */ 
/*     */   public String getReturnTypeName(MethodInfo method) {
/* 101 */     String descriptor = method.getDescriptor();
/* 102 */     descriptor = descriptor.substring(descriptor.lastIndexOf(")") + 1);
/* 103 */     return (String)splitDescriptorToTypeNames(descriptor).get(0);
/*     */   }
/*     */ 
/*     */   public String getFieldName(FieldInfo field) {
/* 107 */     return field.getName();
/*     */   }
/*     */ 
/*     */   public ClassFile getOfCreateClassObject(Vfs.File file) {
/*     */     try {
/* 112 */       if (this.classFileCache != null)
/* 113 */         return (ClassFile)((LoadingCache)this.classFileCache).get(file);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/* 118 */     return createClassObject(file);
/*     */   }
/*     */ 
/*     */   protected ClassFile createClassObject(Vfs.File file) {
/* 122 */     InputStream inputStream = null;
/*     */     try {
/* 124 */       inputStream = file.openInputStream();
/* 125 */       DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
/* 126 */       return new ClassFile(dis);
/*     */     } catch (IOException e) {
/* 128 */       throw new ReflectionsException("could not create class file from " + file.getName(), e);
/*     */     } finally {
/* 130 */       Utils.close(inputStream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getMethodModifier(MethodInfo method) {
/* 135 */     int accessFlags = method.getAccessFlags();
/* 136 */     return isPublic(Integer.valueOf(accessFlags)) ? "public" : AccessFlag.isProtected(accessFlags) ? "protected" : AccessFlag.isPrivate(accessFlags) ? "private" : "";
/*     */   }
/*     */ 
/*     */   public String getMethodKey(ClassFile cls, MethodInfo method)
/*     */   {
/* 142 */     return getMethodName(method) + "(" + Joiner.on(", ").join(getParameterNames(method)) + ")";
/*     */   }
/*     */ 
/*     */   public String getMethodFullKey(ClassFile cls, MethodInfo method) {
/* 146 */     return getClassName(cls) + "." + getMethodKey(cls, method);
/*     */   }
/*     */ 
/*     */   public boolean isPublic(Object o) {
/* 150 */     Integer accessFlags = Integer.valueOf((o instanceof FieldInfo) ? ((FieldInfo)o).getAccessFlags() : (o instanceof ClassFile) ? ((ClassFile)o).getAccessFlags() : ((o instanceof MethodInfo) ? Integer.valueOf(((MethodInfo)o).getAccessFlags()) : null).intValue());
/*     */ 
/* 155 */     return (accessFlags != null) && (AccessFlag.isPublic(accessFlags.intValue()));
/*     */   }
/*     */ 
/*     */   public String getClassName(ClassFile cls)
/*     */   {
/* 160 */     return cls.getName();
/*     */   }
/*     */ 
/*     */   public String getSuperclassName(ClassFile cls) {
/* 164 */     return cls.getSuperclass();
/*     */   }
/*     */ 
/*     */   public List<String> getInterfacesNames(ClassFile cls) {
/* 168 */     return Arrays.asList(cls.getInterfaces());
/*     */   }
/*     */ 
/*     */   private List<String> getAnnotationNames(AnnotationsAttribute[] annotationsAttributes)
/*     */   {
/* 173 */     List result = Lists.newArrayList();
/*     */ 
/* 175 */     if (annotationsAttributes != null) {
/* 176 */       for (AnnotationsAttribute annotationsAttribute : annotationsAttributes) {
/* 177 */         if (annotationsAttribute != null) {
/* 178 */           for (Annotation annotation : annotationsAttribute.getAnnotations()) {
/* 179 */             result.add(annotation.getTypeName());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 185 */     return result;
/*     */   }
/*     */ 
/*     */   private List<String> getAnnotationNames(Annotation[] annotations) {
/* 189 */     List result = Lists.newArrayList();
/*     */ 
/* 191 */     for (Annotation annotation : annotations) {
/* 192 */       result.add(annotation.getTypeName());
/*     */     }
/*     */ 
/* 195 */     return result;
/*     */   }
/*     */ 
/*     */   private List<String> splitDescriptorToTypeNames(String descriptors) {
/* 199 */     List result = Lists.newArrayList();
/*     */ 
/* 201 */     if ((descriptors != null) && (descriptors.length() != 0))
/*     */     {
/* 203 */       List indices = Lists.newArrayList();
/* 204 */       Descriptor.Iterator iterator = new Descriptor.Iterator(descriptors);
/* 205 */       while (iterator.hasNext()) {
/* 206 */         indices.add(Integer.valueOf(iterator.next()));
/*     */       }
/* 208 */       indices.add(Integer.valueOf(descriptors.length()));
/*     */ 
/* 210 */       for (int i = 0; i < indices.size() - 1; i++) {
/* 211 */         String s1 = Descriptor.toString(descriptors.substring(((Integer)indices.get(i)).intValue(), ((Integer)indices.get(i + 1)).intValue()));
/* 212 */         result.add(s1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 217 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.adapters.JavassistAdapter
 * JD-Core Version:    0.6.2
 */