/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.com.google.common.collect.Sets;
/*    */ import com.newrelic.org.reflections.Configuration;
/*    */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*    */ import com.newrelic.org.reflections.util.Utils;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ConvertersScanner extends AbstractScanner
/*    */ {
/*    */   public void scan(Object cls)
/*    */   {
/* 16 */     List methods = getMetadataAdapter().getMethods(cls);
/* 17 */     for (Iterator i$ = methods.iterator(); i$.hasNext(); ) { Object method = i$.next();
/* 18 */       List parameterNames = getMetadataAdapter().getParameterNames(method);
/*    */ 
/* 20 */       if (parameterNames.size() == 1) {
/* 21 */         String from = (String)parameterNames.get(0);
/* 22 */         String to = getMetadataAdapter().getReturnTypeName(method);
/*    */ 
/* 24 */         if ((!to.equals("void")) && ((acceptResult(from)) || (acceptResult(to)))) {
/* 25 */           String methodKey = getMetadataAdapter().getMethodFullKey(cls, method);
/* 26 */           getStore().put(getConverterKey(from, to), methodKey);
/*    */         }
/*    */       } }
/*    */   }
/*    */ 
/*    */   public static String getConverterKey(String from, String to)
/*    */   {
/* 33 */     return from + " to " + to;
/*    */   }
/*    */ 
/*    */   public static String getConverterKey(Class<?> from, Class<?> to) {
/* 37 */     return getConverterKey(from.getName(), to.getName());
/*    */   }
/*    */ 
/*    */   public Set<Method> getConverters(Class<?> from, Class<?> to) {
/* 41 */     Set result = Sets.newHashSet();
/*    */ 
/* 43 */     for (String converter : getStore().get(getConverterKey(from, to))) {
/* 44 */       result.add(Utils.getMethodFromDescriptor(converter, getConfiguration().getClassLoaders()));
/*    */     }
/*    */ 
/* 47 */     return result;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.ConvertersScanner
 * JD-Core Version:    0.6.2
 */