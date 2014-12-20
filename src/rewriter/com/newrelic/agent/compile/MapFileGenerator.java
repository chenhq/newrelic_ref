/*     */ package com.newrelic.agent.compile;
/*     */ 
/*     */ import com.newrelic.agent.android.instrumentation.ReplaceCallSite;
/*     */ import com.newrelic.agent.android.instrumentation.TraceConstructor;
/*     */ import com.newrelic.agent.android.instrumentation.WrapReturn;
/*     */ import com.newrelic.agent.util.Annotations;
/*     */ import com.newrelic.agent.util.MethodAnnotation;
/*     */ import com.newrelic.objectweb.asm.Type;
/*     */ import com.newrelic.org.reflections.util.ClasspathHelper;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MapFileGenerator
/*     */ {
/*     */   public static void main(String[] args)
/*     */   {
/*  23 */     if (args.length != 1) {
/*  24 */       System.err.println("Usage:   MapFileGenerator class_dir");
/*  25 */       System.exit(1);
/*     */     }
/*     */     try {
/*  28 */       Class.forName("com.newrelic.agent.android.Agent");
/*     */     } catch (Exception ex) {
/*  30 */       System.err.println("Unable to load agent classes");
/*  31 */       System.exit(1);
/*     */     }
/*  33 */     Map remapperProperties = getRemapperProperties();
/*  34 */     if (remapperProperties.size() == 0) {
/*  35 */       System.err.println("No class mappings were found");
/*  36 */       System.exit(1);
/*     */     }
/*  38 */     for (Map.Entry entry : remapperProperties.entrySet()) {
/*  39 */       System.out.println((String)entry.getKey() + " = " + (String)entry.getValue());
/*     */     }
/*  41 */     Properties props = new Properties();
/*  42 */     props.putAll(remapperProperties);
/*     */     try {
/*  44 */       FileOutputStream out = new FileOutputStream(args[0]);
/*  45 */       props.store(out, "");
/*  46 */       out.close();
/*     */     } catch (Exception ex) {
/*  48 */       ex.printStackTrace();
/*  49 */       System.exit(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   static Map<String, String> getRemapperProperties()
/*     */   {
/*  59 */     Map classMap = new HashMap();
/*     */ 
/*  61 */     Set urls = ClasspathHelper.forPackage("com.newrelic.agent", new ClassLoader[0]);
/*     */ 
/*  63 */     Collection wrapReturnAnnotations = Annotations.getMethodAnnotations(WrapReturn.class, "com/newrelic/agent", urls);
/*  64 */     for (MethodAnnotation annotation : wrapReturnAnnotations) {
/*  65 */       String originalClassName = (String)annotation.getAttributes().get("className");
/*  66 */       String originalMethodName = (String)annotation.getAttributes().get("methodName");
/*  67 */       String originalMethodDesc = (String)annotation.getAttributes().get("methodDesc");
/*     */ 
/*  69 */       String newClassName = annotation.getClassName();
/*  70 */       String newMethodName = annotation.getMethodName();
/*     */ 
/*  72 */       classMap.put("WRAP_METHOD:" + originalClassName.replace('.', '/') + '.' + originalMethodName + originalMethodDesc, newClassName + '.' + newMethodName + annotation.getMethodDesc());
/*     */     }
/*     */ 
/*  76 */     Collection callSiteAnnotations = Annotations.getMethodAnnotations(ReplaceCallSite.class, "com/newrelic/agent", urls);
/*  77 */     for (MethodAnnotation annotation : callSiteAnnotations) {
/*  78 */       Boolean isStatic = (Boolean)annotation.getAttributes().get("isStatic");
/*  79 */       String scope = (String)annotation.getAttributes().get("scope");
/*     */ 
/*  81 */       if (isStatic == null) isStatic = new Boolean(false);
/*     */ 
/*  83 */       String originalMethodName = annotation.getMethodName();
/*  84 */       String originalMethodDesc = annotation.getMethodDesc();
/*     */ 
/*  89 */       if (!isStatic.booleanValue()) {
/*  90 */         Type[] argTypes = Type.getArgumentTypes(originalMethodDesc);
/*  91 */         Type[] newArgTypes = new Type[argTypes.length - 1];
/*  92 */         for (int i = 0; i < newArgTypes.length; i++) {
/*  93 */           newArgTypes[i] = argTypes[(i + 1)];
/*     */         }
/*  95 */         Type returnType = Type.getReturnType(originalMethodDesc);
/*  96 */         originalMethodDesc = Type.getMethodDescriptor(returnType, newArgTypes);
/*     */       }
/*     */ 
/*  99 */       String newClassName = annotation.getClassName();
/* 100 */       String newMethodName = annotation.getMethodName();
/*     */ 
/* 108 */       if (scope == null) {
/* 109 */         classMap.put("REPLACE_CALL_SITE:" + originalMethodName + originalMethodDesc, newClassName + '.' + newMethodName + annotation.getMethodDesc());
/*     */       }
/*     */       else {
/* 112 */         classMap.put("REPLACE_CALL_SITE:" + scope.replace('.', '/') + "." + originalMethodName + originalMethodDesc, newClassName + '.' + newMethodName + annotation.getMethodDesc());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 117 */     Collection constructorAnnotations = Annotations.getMethodAnnotations(TraceConstructor.class, "com/newrelic/agent", urls);
/* 118 */     for (MethodAnnotation annotation : constructorAnnotations)
/*     */     {
/* 122 */       int typeStart = annotation.getMethodDesc().indexOf(")L");
/* 123 */       int typeEnd = annotation.getMethodDesc().lastIndexOf(";");
/* 124 */       System.out.print("Start: " + typeStart + " end: " + typeEnd + " for " + annotation.getMethodDesc());
/* 125 */       String originalClassName = annotation.getMethodDesc().substring(typeStart + 2, typeEnd);
/*     */ 
/* 127 */       String originalMethodDesc = annotation.getMethodDesc().substring(0, typeStart + 1) + "V";
/* 128 */       String newClassName = annotation.getClassName();
/* 129 */       String newMethodName = annotation.getMethodName();
/*     */ 
/* 131 */       classMap.put("REPLACE_CALL_SITE:" + originalClassName.replace('.', '/') + "." + "<init>" + originalMethodDesc, newClassName + '.' + newMethodName + annotation.getMethodDesc());
/*     */     }
/*     */ 
/* 135 */     return classMap;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.MapFileGenerator
 * JD-Core Version:    0.6.2
 */