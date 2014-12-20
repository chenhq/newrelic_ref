/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ public class MethodParametersAnnotationsScanner extends AbstractScanner
/*    */ {
/*    */   public void scan(Object cls)
/*    */   {
/* 11 */     String className = getMetadataAdapter().getClassName(cls);
/*    */ 
/* 13 */     List methods = getMetadataAdapter().getMethods(cls);
/* 14 */     for (Iterator i$ = methods.iterator(); i$.hasNext(); ) { Object method = i$.next();
/* 15 */       List parameters = getMetadataAdapter().getParameterNames(method);
/* 16 */       for (int parameterIndex = 0; parameterIndex < parameters.size(); parameterIndex++) {
/* 17 */         List parameterAnnotations = getMetadataAdapter().getParameterAnnotationNames(method, parameterIndex);
/* 18 */         for (String parameterAnnotation : parameterAnnotations)
/* 19 */           if (acceptResult(parameterAnnotation))
/* 20 */             getStore().put(parameterAnnotation, String.format("%s.%s:%s %s", new Object[] { className, method, parameters.get(parameterIndex), parameterAnnotation }));
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.MethodParametersAnnotationsScanner
 * JD-Core Version:    0.6.2
 */