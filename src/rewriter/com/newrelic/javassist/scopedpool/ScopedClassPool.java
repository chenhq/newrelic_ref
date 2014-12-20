/*     */ package com.newrelic.javassist.scopedpool;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.LoaderClassPath;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.Collection;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ScopedClassPool extends ClassPool
/*     */ {
/*     */   protected ScopedClassPoolRepository repository;
/*     */   protected WeakReference classLoader;
/*     */   protected LoaderClassPath classPath;
/*  43 */   protected SoftValueHashMap softcache = new SoftValueHashMap();
/*     */ 
/*  45 */   boolean isBootstrapCl = true;
/*     */ 
/*     */   /** @deprecated */
/*     */   protected ScopedClassPool(ClassLoader cl, ClassPool src, ScopedClassPoolRepository repository)
/*     */   {
/*  65 */     this(cl, src, repository, false);
/*     */   }
/*     */ 
/*     */   protected ScopedClassPool(ClassLoader cl, ClassPool src, ScopedClassPoolRepository repository, boolean isTemp)
/*     */   {
/*  82 */     super(src);
/*  83 */     this.repository = repository;
/*  84 */     this.classLoader = new WeakReference(cl);
/*  85 */     if (cl != null) {
/*  86 */       this.classPath = new LoaderClassPath(cl);
/*  87 */       insertClassPath(this.classPath);
/*     */     }
/*  89 */     this.childFirstLookup = true;
/*  90 */     if ((!isTemp) && (cl == null))
/*     */     {
/*  92 */       this.isBootstrapCl = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ClassLoader getClassLoader()
/*     */   {
/* 102 */     ClassLoader cl = getClassLoader0();
/* 103 */     if ((cl == null) && (!this.isBootstrapCl))
/*     */     {
/* 105 */       throw new IllegalStateException("ClassLoader has been garbage collected");
/*     */     }
/*     */ 
/* 108 */     return cl;
/*     */   }
/*     */ 
/*     */   protected ClassLoader getClassLoader0() {
/* 112 */     return (ClassLoader)this.classLoader.get();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 119 */     removeClassPath(this.classPath);
/* 120 */     this.classPath.close();
/* 121 */     this.classes.clear();
/* 122 */     this.softcache.clear();
/*     */   }
/*     */ 
/*     */   public synchronized void flushClass(String classname)
/*     */   {
/* 132 */     this.classes.remove(classname);
/* 133 */     this.softcache.remove(classname);
/*     */   }
/*     */ 
/*     */   public synchronized void soften(CtClass clazz)
/*     */   {
/* 143 */     if (this.repository.isPrune())
/* 144 */       clazz.prune();
/* 145 */     this.classes.remove(clazz.getName());
/* 146 */     this.softcache.put(clazz.getName(), clazz);
/*     */   }
/*     */ 
/*     */   public boolean isUnloadedClassLoader()
/*     */   {
/* 155 */     return false;
/*     */   }
/*     */ 
/*     */   protected CtClass getCached(String classname)
/*     */   {
/* 166 */     CtClass clazz = getCachedLocally(classname);
/* 167 */     if (clazz == null) {
/* 168 */       boolean isLocal = false;
/*     */ 
/* 170 */       ClassLoader dcl = getClassLoader0();
/* 171 */       if (dcl != null) {
/* 172 */         int lastIndex = classname.lastIndexOf('$');
/* 173 */         String classResourceName = null;
/* 174 */         if (lastIndex < 0) {
/* 175 */           classResourceName = classname.replaceAll("[\\.]", "/") + ".class";
/*     */         }
/*     */         else
/*     */         {
/* 179 */           classResourceName = classname.substring(0, lastIndex).replaceAll("[\\.]", "/") + classname.substring(lastIndex) + ".class";
/*     */         }
/*     */ 
/* 184 */         isLocal = dcl.getResource(classResourceName) != null;
/*     */       }
/*     */ 
/* 187 */       if (!isLocal) {
/* 188 */         Map registeredCLs = this.repository.getRegisteredCLs();
/* 189 */         synchronized (registeredCLs) {
/* 190 */           Iterator it = registeredCLs.values().iterator();
/* 191 */           while (it.hasNext()) {
/* 192 */             ScopedClassPool pool = (ScopedClassPool)it.next();
/* 193 */             if (pool.isUnloadedClassLoader()) {
/* 194 */               this.repository.unregisterClassLoader(pool.getClassLoader());
/*     */             }
/*     */             else
/*     */             {
/* 199 */               clazz = pool.getCachedLocally(classname);
/* 200 */               if (clazz != null) {
/* 201 */                 return clazz;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 208 */     return clazz;
/*     */   }
/*     */ 
/*     */   protected void cacheCtClass(String classname, CtClass c, boolean dynamic)
/*     */   {
/* 222 */     if (dynamic) {
/* 223 */       super.cacheCtClass(classname, c, dynamic);
/*     */     }
/*     */     else {
/* 226 */       if (this.repository.isPrune())
/* 227 */         c.prune();
/* 228 */       this.softcache.put(classname, c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void lockInCache(CtClass c)
/*     */   {
/* 239 */     super.cacheCtClass(c.getName(), c, false);
/*     */   }
/*     */ 
/*     */   protected CtClass getCachedLocally(String classname)
/*     */   {
/* 250 */     CtClass cached = (CtClass)this.classes.get(classname);
/* 251 */     if (cached != null)
/* 252 */       return cached;
/* 253 */     synchronized (this.softcache) {
/* 254 */       return (CtClass)this.softcache.get(classname);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized CtClass getLocally(String classname)
/*     */     throws NotFoundException
/*     */   {
/* 269 */     this.softcache.remove(classname);
/* 270 */     CtClass clazz = (CtClass)this.classes.get(classname);
/* 271 */     if (clazz == null) {
/* 272 */       clazz = createCtClass(classname, true);
/* 273 */       if (clazz == null)
/* 274 */         throw new NotFoundException(classname);
/* 275 */       super.cacheCtClass(classname, clazz, false);
/*     */     }
/*     */ 
/* 278 */     return clazz;
/*     */   }
/*     */ 
/*     */   public Class toClass(CtClass ct, ClassLoader loader, ProtectionDomain domain)
/*     */     throws CannotCompileException
/*     */   {
/* 305 */     lockInCache(ct);
/* 306 */     return super.toClass(ct, getClassLoader0(), domain);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  48 */     ClassPool.doPruning = false;
/*  49 */     ClassPool.releaseUnmodifiedClassFile = false;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.scopedpool.ScopedClassPool
 * JD-Core Version:    0.6.2
 */