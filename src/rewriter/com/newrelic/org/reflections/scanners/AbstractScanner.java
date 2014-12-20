/*    */ package com.newrelic.org.reflections.scanners;
/*    */ 
/*    */ import com.newrelic.com.google.common.base.Predicate;
/*    */ import com.newrelic.com.google.common.base.Predicates;
/*    */ import com.newrelic.com.google.common.collect.Multimap;
/*    */ import com.newrelic.org.reflections.Configuration;
/*    */ import com.newrelic.org.reflections.ReflectionsException;
/*    */ import com.newrelic.org.reflections.adapters.MetadataAdapter;
/*    */ import com.newrelic.org.reflections.vfs.Vfs.File;
/*    */ 
/*    */ public abstract class AbstractScanner
/*    */   implements Scanner
/*    */ {
/*    */   private Configuration configuration;
/*    */   private Multimap<String, String> store;
/* 19 */   private Predicate<String> resultFilter = Predicates.alwaysTrue();
/*    */ 
/*    */   public boolean acceptsInput(String file) {
/* 22 */     return file.endsWith(".class");
/*    */   }
/*    */ 
/*    */   public void scan(Vfs.File file) {
/*    */     try {
/* 27 */       Object classObject = getMetadataAdapter().getOfCreateClassObject(file);
/* 28 */       scan(classObject);
/*    */     } catch (Exception e) {
/* 30 */       throw new ReflectionsException("could not create class file from " + file.getName(), e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public abstract void scan(Object paramObject);
/*    */ 
/*    */   public Configuration getConfiguration()
/*    */   {
/* 38 */     return this.configuration;
/*    */   }
/*    */ 
/*    */   public void setConfiguration(Configuration configuration) {
/* 42 */     this.configuration = configuration;
/*    */   }
/*    */ 
/*    */   public Multimap<String, String> getStore() {
/* 46 */     return this.store;
/*    */   }
/*    */ 
/*    */   public void setStore(Multimap<String, String> store) {
/* 50 */     this.store = store;
/*    */   }
/*    */ 
/*    */   public Predicate<String> getResultFilter() {
/* 54 */     return this.resultFilter;
/*    */   }
/*    */ 
/*    */   public void setResultFilter(Predicate<String> resultFilter) {
/* 58 */     this.resultFilter = resultFilter;
/*    */   }
/*    */ 
/*    */   public Scanner filterResultsBy(Predicate<String> filter) {
/* 62 */     setResultFilter(filter); return this;
/*    */   }
/*    */ 
/*    */   public boolean acceptResult(String fqn)
/*    */   {
/* 67 */     return (fqn != null) && (this.resultFilter.apply(fqn));
/*    */   }
/*    */ 
/*    */   protected MetadataAdapter getMetadataAdapter() {
/* 71 */     return this.configuration.getMetadataAdapter();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 76 */     return (this == o) || ((o != null) && (getClass() == o.getClass()));
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 80 */     return getClass().hashCode();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.reflections.scanners.AbstractScanner
 * JD-Core Version:    0.6.2
 */