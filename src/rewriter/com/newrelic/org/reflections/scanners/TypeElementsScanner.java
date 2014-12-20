/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ public class TypeElementsScanner extends AbstractScanner
/*    */ {
/*  6 */   private boolean includeFields = true;
/*  7 */   private boolean includeMethods = true;
/*  8 */   private boolean publicOnly = true;
/*    */ 
/*    */   public void scan(Object cls)
/*    */   {
/* 12 */     if (TypesScanner.isJavaCodeSerializer(getMetadataAdapter().getInterfacesNames(cls))) return;
/*    */ 
/* 14 */     String className = getMetadataAdapter().getClassName(cls);
/*    */     Iterator i$;
/* 16 */     if (this.includeFields)
/* 17 */       for (i$ = getMetadataAdapter().getFields(cls).iterator(); i$.hasNext(); ) { Object field = i$.next();
/* 18 */         String fieldName = getMetadataAdapter().getFieldName(field);
/* 19 */         getStore().put(className, fieldName);
/*    */       }
/*    */     Iterator i$;
/* 23 */     if (this.includeMethods)
/* 24 */       for (i$ = getMetadataAdapter().getMethods(cls).iterator(); i$.hasNext(); ) { Object method = i$.next();
/* 25 */         if ((!this.publicOnly) || (getMetadataAdapter().isPublic(method)))
/* 26 */           getStore().put(className, getMetadataAdapter().getMethodKey(cls, method));
/*    */       }
/*    */   }
/*    */ 
/*    */   public TypeElementsScanner includeFields()
/*    */   {
/* 33 */     return includeFields(true); } 
/* 34 */   public TypeElementsScanner includeFields(boolean include) { this.includeFields = include; return this; } 
/* 35 */   public TypeElementsScanner includeMethods() { return includeMethods(true); } 
/* 36 */   public TypeElementsScanner includeMethods(boolean include) { this.includeMethods = include; return this; } 
/* 37 */   public TypeElementsScanner publicOnly(boolean only) { this.publicOnly = only; return this; } 
/* 38 */   public TypeElementsScanner publicOnly() { return publicOnly(true); }
/*    */ 
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.TypeElementsScanner
 * JD-Core Version:    0.6.2
 */