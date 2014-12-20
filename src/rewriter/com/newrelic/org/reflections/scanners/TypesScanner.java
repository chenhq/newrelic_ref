/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Lists;
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.org.reflections.ReflectionsException;
/*    */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*    */ import com.newrelic.org.reflections.serializers.JavaCodeSerializer.IClass;
/*    */ import com.newrelic.org.reflections.serializers.JavaCodeSerializer.IElement;
/*    */ import com.newrelic.org.reflections.serializers.JavaCodeSerializer.IField;
/*    */ import com.newrelic.org.reflections.serializers.JavaCodeSerializer.IMethod;
/*    */ import com.newrelic.org.reflections.serializers.JavaCodeSerializer.IPackage;
/*    */ import com.newrelic.org.reflections.vfs.Vfs.File;
/*    */ import java.util.List;
/*    */ 
/*    */ public class TypesScanner extends AbstractScanner
/*    */ {
/* 47 */   private static final List<String> javaCodeSerializerInterfaces = Lists.newArrayList(new String[] { JavaCodeSerializer.IElement.class.getName(), JavaCodeSerializer.IPackage.class.getName(), JavaCodeSerializer.IClass.class.getName(), JavaCodeSerializer.IField.class.getName(), JavaCodeSerializer.IMethod.class.getName() });
/*    */ 
/*    */   public boolean acceptsInput(String file)
/*    */   {
/* 13 */     return (file.endsWith(".class")) && (!file.endsWith("package-info.class"));
/*    */   }
/*    */ 
/*    */   public void scan(Vfs.File file)
/*    */   {
/*    */     try {
/* 19 */       Object cls = getMetadataAdapter().getOfCreateClassObject(file);
/* 20 */       scan(cls, file);
/*    */     } catch (Exception e) {
/* 22 */       throw new ReflectionsException("could not create class file from " + file.getName(), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   private void scan(Object cls, Vfs.File file)
/*    */   {
/* 29 */     if (isJavaCodeSerializer(getMetadataAdapter().getInterfacesNames(cls))) return;
/*    */ 
/* 32 */     String className = getMetadataAdapter().getClassName(cls);
/*    */ 
/* 34 */     getStore().put(className, className);
/*    */   }
/*    */ 
/*    */   public void scan(Object cls)
/*    */   {
/* 39 */     throw new UnsupportedOperationException("should not get here");
/*    */   }
/*    */ 
/*    */   public static boolean isJavaCodeSerializer(List<String> interfacesNames)
/*    */   {
/* 44 */     return (interfacesNames.size() == 1) && (javaCodeSerializerInterfaces.contains(interfacesNames.get(0)));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.TypesScanner
 * JD-Core Version:    0.6.2
 */