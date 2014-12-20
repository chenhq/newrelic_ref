/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*    */ import com.newrelic.org.reflections.util.FilterBuilder;
/*    */ 
/*    */ public class SubTypesScanner extends AbstractScanner
/*    */ {
/*    */   public SubTypesScanner()
/*    */   {
/* 12 */     this(true);
/*    */   }
/*    */ 
/*    */   public SubTypesScanner(boolean excludeObjectClass)
/*    */   {
/* 18 */     if (excludeObjectClass)
/* 19 */       filterResultsBy(new FilterBuilder().exclude(Object.class.getName()));
/*    */   }
/*    */ 
/*    */   public void scan(Object cls)
/*    */   {
/* 25 */     String className = getMetadataAdapter().getClassName(cls);
/* 26 */     String superclass = getMetadataAdapter().getSuperclassName(cls);
/*    */ 
/* 28 */     if (acceptResult(superclass)) {
/* 29 */       getStore().put(superclass, className);
/*    */     }
/*    */ 
/* 32 */     for (String anInterface : getMetadataAdapter().getInterfacesNames(cls))
/* 33 */       if (acceptResult(anInterface))
/* 34 */         getStore().put(anInterface, className);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.SubTypesScanner
 * JD-Core Version:    0.6.2
 */