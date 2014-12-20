/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*    */ import java.lang.annotation.Inherited;
/*    */ 
/*    */ public class TypeAnnotationsScanner extends AbstractScanner
/*    */ {
/*    */   public void scan(Object cls)
/*    */   {
/* 10 */     String className = getMetadataAdapter().getClassName(cls);
/*    */ 
/* 12 */     for (String annotationType : getMetadataAdapter().getClassAnnotationNames(cls))
/*    */     {
/* 14 */       if ((acceptResult(annotationType)) || (annotationType.equals(Inherited.class.getName())))
/*    */       {
/* 16 */         getStore().put(annotationType, className);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.TypeAnnotationsScanner
 * JD-Core Version:    0.6.2
 */