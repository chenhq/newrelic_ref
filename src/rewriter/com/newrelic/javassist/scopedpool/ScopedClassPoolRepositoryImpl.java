/*     */ package com.newrelic.javassist.scopedpool;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.LoaderClassPath;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ public class ScopedClassPoolRepositoryImpl
/*     */   implements ScopedClassPoolRepository
/*     */ {
/*  36 */   private static final ScopedClassPoolRepositoryImpl instance = new ScopedClassPoolRepositoryImpl();
/*     */ 
/*  39 */   private boolean prune = true;
/*     */   boolean pruneWhenCached;
/*  45 */   protected Map registeredCLs = Collections.synchronizedMap(new WeakHashMap());
/*     */   protected ClassPool classpool;
/*  52 */   protected ScopedClassPoolFactory factory = new ScopedClassPoolFactoryImpl();
/*     */ 
/*     */   public static ScopedClassPoolRepository getInstance()
/*     */   {
/*  60 */     return instance;
/*     */   }
/*     */ 
/*     */   private ScopedClassPoolRepositoryImpl()
/*     */   {
/*  67 */     this.classpool = ClassPool.getDefault();
/*     */ 
/*  69 */     ClassLoader cl = Thread.currentThread().getContextClassLoader();
/*  70 */     this.classpool.insertClassPath(new LoaderClassPath(cl));
/*     */   }
/*     */ 
/*     */   public boolean isPrune()
/*     */   {
/*  79 */     return this.prune;
/*     */   }
/*     */ 
/*     */   public void setPrune(boolean prune)
/*     */   {
/*  88 */     this.prune = prune;
/*     */   }
/*     */ 
/*     */   public ScopedClassPool createScopedClassPool(ClassLoader cl, ClassPool src)
/*     */   {
/*  99 */     return this.factory.create(cl, src, this);
/*     */   }
/*     */ 
/*     */   public ClassPool findClassPool(ClassLoader cl) {
/* 103 */     if (cl == null) {
/* 104 */       return registerClassLoader(ClassLoader.getSystemClassLoader());
/*     */     }
/* 106 */     return registerClassLoader(cl);
/*     */   }
/*     */ 
/*     */   public ClassPool registerClassLoader(ClassLoader ucl)
/*     */   {
/* 116 */     synchronized (this.registeredCLs)
/*     */     {
/* 122 */       if (this.registeredCLs.containsKey(ucl)) {
/* 123 */         return (ClassPool)this.registeredCLs.get(ucl);
/*     */       }
/* 125 */       ScopedClassPool pool = createScopedClassPool(ucl, this.classpool);
/* 126 */       this.registeredCLs.put(ucl, pool);
/* 127 */       return pool;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map getRegisteredCLs()
/*     */   {
/* 135 */     clearUnregisteredClassLoaders();
/* 136 */     return this.registeredCLs;
/*     */   }
/*     */ 
/*     */   public void clearUnregisteredClassLoaders()
/*     */   {
/* 144 */     ArrayList toUnregister = null;
/* 145 */     synchronized (this.registeredCLs) {
/* 146 */       Iterator it = this.registeredCLs.values().iterator();
/* 147 */       while (it.hasNext()) {
/* 148 */         ScopedClassPool pool = (ScopedClassPool)it.next();
/* 149 */         if (pool.isUnloadedClassLoader()) {
/* 150 */           it.remove();
/* 151 */           ClassLoader cl = pool.getClassLoader();
/* 152 */           if (cl != null) {
/* 153 */             if (toUnregister == null) {
/* 154 */               toUnregister = new ArrayList();
/*     */             }
/* 156 */             toUnregister.add(cl);
/*     */           }
/*     */         }
/*     */       }
/* 160 */       if (toUnregister != null)
/* 161 */         for (int i = 0; i < toUnregister.size(); i++)
/* 162 */           unregisterClassLoader((ClassLoader)toUnregister.get(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregisterClassLoader(ClassLoader cl)
/*     */   {
/* 169 */     synchronized (this.registeredCLs) {
/* 170 */       ScopedClassPool pool = (ScopedClassPool)this.registeredCLs.remove(cl);
/* 171 */       if (pool != null)
/* 172 */         pool.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void insertDelegate(ScopedClassPoolRepository delegate)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setClassPoolFactory(ScopedClassPoolFactory factory) {
/* 181 */     this.factory = factory;
/*     */   }
/*     */ 
/*     */   public ScopedClassPoolFactory getClassPoolFactory() {
/* 185 */     return this.factory;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.scopedpool.ScopedClassPoolRepositoryImpl
 * JD-Core Version:    0.6.2
 */