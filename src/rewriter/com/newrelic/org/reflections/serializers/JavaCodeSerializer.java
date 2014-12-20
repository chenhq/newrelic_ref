/*     */ package com.newrelic.org.reflections.serializers;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Joiner;
/*     */ import com.newrelic.com.google.common.base.Supplier;
/*     */ import com.newrelic.com.google.common.collect.Lists;
/*     */ import com.newrelic.com.google.common.collect.Multimap;
/*     */ import com.newrelic.com.google.common.collect.Multimaps;
/*     */ import com.newrelic.com.google.common.collect.Sets;
/*     */ import com.newrelic.com.google.common.io.Files;
/*     */ import com.newrelic.org.reflections.ReflectionUtils;
/*     */ import com.newrelic.org.reflections.Reflections;
/*     */ import com.newrelic.org.reflections.ReflectionsException;
/*     */ import com.newrelic.org.reflections.Store;
/*     */ import com.newrelic.org.reflections.scanners.TypeElementsScanner;
/*     */ import com.newrelic.org.reflections.scanners.TypesScanner;
/*     */ import com.newrelic.org.reflections.util.Utils;
/*     */ import com.newrelic.org.slf4j.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class JavaCodeSerializer
/*     */   implements Serializer
/*     */ {
/*     */   private static final char pathSeparator = '$';
/*     */   private static final String arrayDescriptor = "$$";
/*     */   private static final String tokenSeparator = "_";
/*     */ 
/*     */   public Reflections read(InputStream inputStream)
/*     */   {
/*  64 */     throw new UnsupportedOperationException("read is not implemented on JavaCodeSerializer");
/*     */   }
/*     */ 
/*     */   public File save(Reflections reflections, String name)
/*     */   {
/*  73 */     if (name.endsWith("/")) {
/*  74 */       name = name.substring(0, name.length() - 1);
/*     */     }
/*     */ 
/*  78 */     String filename = name.replace('.', '/').concat(".java");
/*  79 */     File file = Utils.prepareFile(filename);
/*     */ 
/*  84 */     int lastDot = name.lastIndexOf('.');
/*     */     String className;
/*     */     String packageName;
/*     */     String className;
/*  85 */     if (lastDot == -1) {
/*  86 */       String packageName = "";
/*  87 */       className = name.substring(name.lastIndexOf('/') + 1);
/*     */     } else {
/*  89 */       packageName = name.substring(name.lastIndexOf('/') + 1, lastDot);
/*  90 */       className = name.substring(lastDot + 1);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  95 */       StringBuilder sb = new StringBuilder();
/*  96 */       sb.append("//generated using Reflections JavaCodeSerializer").append(" [").append(new Date()).append("]").append("\n");
/*     */ 
/*  99 */       if (packageName.length() != 0) {
/* 100 */         sb.append("package ").append(packageName).append(";\n");
/* 101 */         sb.append("\n");
/*     */       }
/* 103 */       sb.append("import static org.reflections.serializers.JavaCodeSerializer.*;\n");
/* 104 */       sb.append("\n");
/* 105 */       sb.append("public interface ").append(className).append(" extends IElement").append(" {\n\n");
/* 106 */       sb.append(toString(reflections));
/* 107 */       sb.append("}\n");
/*     */ 
/* 109 */       Files.write(sb.toString(), new File(filename), Charset.defaultCharset());
/*     */     }
/*     */     catch (IOException e) {
/* 112 */       throw new RuntimeException();
/*     */     }
/*     */ 
/* 115 */     return file;
/*     */   }
/*     */ 
/*     */   public String toString(Reflections reflections) {
/* 119 */     if ((reflections.getStore().get(TypesScanner.class).isEmpty()) || (reflections.getStore().get(TypeElementsScanner.class).isEmpty()))
/*     */     {
/* 121 */       if (Reflections.log != null) Reflections.log.warn("JavaCodeSerializer needs TypeScanner and TypeElemenetsScanner configured");
/*     */     }
/*     */ 
/* 124 */     StringBuilder sb = new StringBuilder();
/*     */ 
/* 126 */     List prevPaths = Lists.newArrayList();
/* 127 */     int indent = 1;
/*     */ 
/* 129 */     List keys = Lists.newArrayList(reflections.getStore().get(TypesScanner.class).keySet());
/* 130 */     Collections.sort(keys);
/* 131 */     for (String fqn : keys) {
/* 132 */       List typePaths = Lists.newArrayList(fqn.split("\\."));
/*     */ 
/* 135 */       int i = 0;
/* 136 */       while ((i < Math.min(typePaths.size(), prevPaths.size())) && (((String)typePaths.get(i)).equals(prevPaths.get(i)))) {
/* 137 */         i++;
/*     */       }
/*     */ 
/* 141 */       for (int j = prevPaths.size(); j > i; j--) {
/* 142 */         sb.append(Utils.repeat("\t", --indent)).append("}\n");
/*     */       }
/*     */ 
/* 146 */       for (int j = i; j < typePaths.size() - 1; j++) {
/* 147 */         sb.append(Utils.repeat("\t", indent++)).append("public interface ").append(getNonDuplicateName((String)typePaths.get(j), typePaths, j)).append(" extends IPackage").append(" {\n");
/*     */       }
/*     */ 
/* 151 */       String className = (String)typePaths.get(typePaths.size() - 1);
/*     */ 
/* 154 */       List fields = Lists.newArrayList();
/* 155 */       Multimap methods = Multimaps.newSetMultimap(new HashMap(), new Supplier() {
/*     */         public Set<String> get() {
/* 157 */           return Sets.newHashSet();
/*     */         }
/*     */       });
/* 161 */       for (String element : reflections.getStore().get(TypeElementsScanner.class, new String[] { fqn })) {
/* 162 */         if (element.contains("("))
/*     */         {
/* 164 */           if (!element.startsWith("<")) {
/* 165 */             int i1 = element.indexOf('(');
/* 166 */             String name = element.substring(0, i1);
/* 167 */             String params = element.substring(i1 + 1, element.indexOf(")"));
/*     */ 
/* 169 */             String paramsDescriptor = "";
/* 170 */             if (params.length() != 0) {
/* 171 */               paramsDescriptor = "_" + params.replace('.', '$').replace(", ", "_").replace("[]", "$$");
/*     */             }
/* 173 */             String normalized = name + paramsDescriptor;
/* 174 */             methods.put(name, normalized);
/*     */           }
/*     */         }
/*     */         else {
/* 178 */           fields.add(element);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 183 */       sb.append(Utils.repeat("\t", indent++)).append("public interface ").append(getNonDuplicateName(className, typePaths, typePaths.size() - 1)).append(" extends IClass").append(" {\n");
/*     */ 
/* 186 */       if (!fields.isEmpty()) {
/* 187 */         for (String field : fields) {
/* 188 */           sb.append(Utils.repeat("\t", indent)).append("public interface ").append(getNonDuplicateName(field, typePaths)).append(" extends IField").append(" {}\n");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 193 */       if (!methods.isEmpty()) {
/* 194 */         for (Map.Entry entry : methods.entries()) {
/* 195 */           String simpleName = (String)entry.getKey();
/* 196 */           String normalized = (String)entry.getValue();
/*     */ 
/* 198 */           String methodName = methods.get(simpleName).size() == 1 ? simpleName : normalized;
/*     */ 
/* 200 */           methodName = getNonDuplicateName(methodName, fields);
/*     */ 
/* 202 */           sb.append(Utils.repeat("\t", indent)).append("public interface ").append(getNonDuplicateName(methodName, typePaths)).append(" extends IMethod").append(" {}\n");
/*     */         }
/*     */       }
/*     */ 
/* 206 */       prevPaths = typePaths;
/*     */     }
/*     */ 
/* 210 */     for (int j = prevPaths.size(); j >= 1; j--) {
/* 211 */       sb.append(Utils.repeat("\t", j)).append("}\n");
/*     */     }
/*     */ 
/* 214 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String getNonDuplicateName(String candidate, List<String> prev, int offset)
/*     */   {
/* 219 */     for (int i = 0; i < offset; i++) {
/* 220 */       if (candidate.equals(prev.get(i))) {
/* 221 */         return getNonDuplicateName(candidate + "_", prev, offset);
/*     */       }
/*     */     }
/*     */ 
/* 225 */     return candidate;
/*     */   }
/*     */ 
/*     */   private String getNonDuplicateName(String candidate, List<String> prev) {
/* 229 */     return getNonDuplicateName(candidate, prev, prev.size());
/*     */   }
/*     */ 
/*     */   public static Class<?> resolveClassOf(Class<? extends IElement> element) throws ClassNotFoundException
/*     */   {
/* 234 */     Class cursor = element;
/* 235 */     List path = Lists.newArrayList();
/*     */ 
/* 237 */     while ((cursor != null) && (IElement.class.isAssignableFrom(cursor)))
/*     */     {
/* 239 */       path.add(cursor);
/* 240 */       cursor = cursor.getDeclaringClass();
/*     */     }
/*     */ 
/* 243 */     Collections.reverse(path);
/*     */ 
/* 245 */     int i = 1;
/*     */ 
/* 247 */     List ognl = Lists.newArrayList();
/* 248 */     while ((i < path.size()) && ((IPackage.class.isAssignableFrom((Class)path.get(i))) || (IClass.class.isAssignableFrom((Class)path.get(i)))))
/*     */     {
/* 250 */       ognl.add(((Class)path.get(i)).getSimpleName());
/* 251 */       i++;
/*     */     }
/*     */ 
/* 254 */     String classOgnl = Joiner.on(".").join(ognl).replace(".$", "$");
/* 255 */     return Class.forName(classOgnl);
/*     */   }
/*     */ 
/*     */   public static Class<?> resolveClass(Class<? extends IClass> aClass) {
/*     */     try {
/* 260 */       return resolveClassOf(aClass);
/*     */     } catch (Exception e) {
/* 262 */       throw new ReflectionsException("could not resolve to class " + aClass.getName(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Field resolveField(Class<? extends IField> aField) {
/*     */     try {
/* 268 */       String name = aField.getSimpleName();
/* 269 */       return resolveClassOf(aField).getDeclaredField(name);
/*     */     } catch (Exception e) {
/* 271 */       throw new ReflectionsException("could not resolve to field " + aField.getName(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Method resolveMethod(Class<? extends IMethod> aMethod) {
/* 276 */     String methodOgnl = aMethod.getSimpleName();
/*     */     try
/*     */     {
/*     */       String methodName;
/*     */       Class[] paramTypes;
/* 281 */       if (methodOgnl.contains("_")) {
/* 282 */         String methodName = methodOgnl.substring(0, methodOgnl.indexOf("_"));
/* 283 */         String[] params = methodOgnl.substring(methodOgnl.indexOf("_") + 1).split("_");
/* 284 */         Class[] paramTypes = new Class[params.length];
/* 285 */         for (int i = 0; i < params.length; i++) {
/* 286 */           String typeName = params[i].replace("$$", "[]").replace('$', '.');
/* 287 */           paramTypes[i] = ReflectionUtils.forName(typeName, new ClassLoader[0]);
/*     */         }
/*     */       } else {
/* 290 */         methodName = methodOgnl;
/* 291 */         paramTypes = null;
/*     */       }
/*     */ 
/* 294 */       return resolveClassOf(aMethod).getDeclaredMethod(methodName, paramTypes);
/*     */     } catch (Exception e) {
/* 296 */       throw new ReflectionsException("could not resolve to method " + aMethod.getName(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface IMethod extends JavaCodeSerializer.IElement
/*     */   {
/*     */   }
/*     */ 
/*     */   public static abstract interface IField extends JavaCodeSerializer.IElement
/*     */   {
/*     */   }
/*     */ 
/*     */   public static abstract interface IClass extends JavaCodeSerializer.IElement
/*     */   {
/*     */   }
/*     */ 
/*     */   public static abstract interface IPackage extends JavaCodeSerializer.IElement
/*     */   {
/*     */   }
/*     */ 
/*     */   public static abstract interface IElement
/*     */   {
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.serializers.JavaCodeSerializer
 * JD-Core Version:    0.6.2
 */