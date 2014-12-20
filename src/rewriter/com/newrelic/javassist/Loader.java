/*     */ package com.newrelic.javassist;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ 
/*     */ public class Loader extends ClassLoader
/*     */ {
/*     */   private Hashtable notDefinedHere;
/*     */   private Vector notDefinedPackages;
/*     */   private ClassPool source;
/*     */   private Translator translator;
/*     */   private ProtectionDomain domain;
/* 153 */   public boolean doDelegation = true;
/*     */ 
/*     */   public Loader()
/*     */   {
/* 159 */     this(null);
/*     */   }
/*     */ 
/*     */   public Loader(ClassPool cp)
/*     */   {
/* 168 */     init(cp);
/*     */   }
/*     */ 
/*     */   public Loader(ClassLoader parent, ClassPool cp)
/*     */   {
/* 179 */     super(parent);
/* 180 */     init(cp);
/*     */   }
/*     */ 
/*     */   private void init(ClassPool cp) {
/* 184 */     this.notDefinedHere = new Hashtable();
/* 185 */     this.notDefinedPackages = new Vector();
/* 186 */     this.source = cp;
/* 187 */     this.translator = null;
/* 188 */     this.domain = null;
/* 189 */     delegateLoadingOf("com.newrelic.javassist.Loader");
/*     */   }
/*     */ 
/*     */   public void delegateLoadingOf(String classname)
/*     */   {
/* 201 */     if (classname.endsWith("."))
/* 202 */       this.notDefinedPackages.addElement(classname);
/*     */     else
/* 204 */       this.notDefinedHere.put(classname, this);
/*     */   }
/*     */ 
/*     */   public void setDomain(ProtectionDomain d)
/*     */   {
/* 214 */     this.domain = d;
/*     */   }
/*     */ 
/*     */   public void setClassPool(ClassPool cp)
/*     */   {
/* 221 */     this.source = cp;
/*     */   }
/*     */ 
/*     */   public void addTranslator(ClassPool cp, Translator t)
/*     */     throws NotFoundException, CannotCompileException
/*     */   {
/* 235 */     this.source = cp;
/* 236 */     this.translator = t;
/* 237 */     t.start(cp);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Throwable
/*     */   {
/* 256 */     Loader cl = new Loader();
/* 257 */     cl.run(args);
/*     */   }
/*     */ 
/*     */   public void run(String[] args)
/*     */     throws Throwable
/*     */   {
/* 271 */     int n = args.length - 1;
/* 272 */     if (n >= 0) {
/* 273 */       String[] args2 = new String[n];
/* 274 */       for (int i = 0; i < n; i++) {
/* 275 */         args2[i] = args[(i + 1)];
/*     */       }
/* 277 */       run(args[0], args2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run(String classname, String[] args)
/*     */     throws Throwable
/*     */   {
/* 288 */     Class c = loadClass(classname);
/*     */     try {
/* 290 */       c.getDeclaredMethod("main", new Class[] { new String[0].getClass() }).invoke(null, new Object[] { args });
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 295 */       throw e.getTargetException();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Class loadClass(String name, boolean resolve)
/*     */     throws ClassFormatError, ClassNotFoundException
/*     */   {
/* 304 */     name = name.intern();
/* 305 */     synchronized (name) {
/* 306 */       Class c = findLoadedClass(name);
/* 307 */       if (c == null) {
/* 308 */         c = loadClassByDelegation(name);
/*     */       }
/* 310 */       if (c == null) {
/* 311 */         c = findClass(name);
/*     */       }
/* 313 */       if (c == null) {
/* 314 */         c = delegateToParent(name);
/*     */       }
/* 316 */       if (resolve) {
/* 317 */         resolveClass(c);
/*     */       }
/* 319 */       return c;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Class findClass(String name)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     byte[] classfile;
/*     */     try
/*     */     {
/* 338 */       if (this.source != null) {
/* 339 */         if (this.translator != null)
/* 340 */           this.translator.onLoad(this.source, name);
/*     */         try
/*     */         {
/* 343 */           classfile = this.source.get(name).toBytecode();
/*     */         }
/*     */         catch (NotFoundException e) {
/* 346 */           return null;
/*     */         }
/*     */       }
/*     */       else {
/* 350 */         String jarname = "/" + name.replace('.', '/') + ".class";
/* 351 */         InputStream in = getClass().getResourceAsStream(jarname);
/* 352 */         if (in == null) {
/* 353 */           return null;
/*     */         }
/* 355 */         classfile = ClassPoolTail.readStream(in);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 359 */       throw new ClassNotFoundException("caught an exception while obtaining a class file for " + name, e);
/*     */     }
/*     */ 
/* 364 */     int i = name.lastIndexOf('.');
/* 365 */     if (i != -1) {
/* 366 */       String pname = name.substring(0, i);
/* 367 */       if (getPackage(pname) == null) {
/*     */         try {
/* 369 */           definePackage(pname, null, null, null, null, null, null, null);
/*     */         }
/*     */         catch (IllegalArgumentException e)
/*     */         {
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 378 */     if (this.domain == null) {
/* 379 */       return defineClass(name, classfile, 0, classfile.length);
/*     */     }
/* 381 */     return defineClass(name, classfile, 0, classfile.length, this.domain);
/*     */   }
/*     */ 
/*     */   protected Class loadClassByDelegation(String name)
/*     */     throws ClassNotFoundException
/*     */   {
/* 397 */     Class c = null;
/* 398 */     if ((this.doDelegation) && (
/* 399 */       (name.startsWith("java.")) || (name.startsWith("javax.")) || (name.startsWith("sun.")) || (name.startsWith("com.sun.")) || (name.startsWith("org.w3c.")) || (name.startsWith("org.xml.")) || (notDelegated(name))))
/*     */     {
/* 406 */       c = delegateToParent(name);
/*     */     }
/* 408 */     return c;
/*     */   }
/*     */ 
/*     */   private boolean notDelegated(String name) {
/* 412 */     if (this.notDefinedHere.get(name) != null) {
/* 413 */       return true;
/*     */     }
/* 415 */     int n = this.notDefinedPackages.size();
/* 416 */     for (int i = 0; i < n; i++) {
/* 417 */       if (name.startsWith((String)this.notDefinedPackages.elementAt(i)))
/* 418 */         return true;
/*     */     }
/* 420 */     return false;
/*     */   }
/*     */ 
/*     */   protected Class delegateToParent(String classname)
/*     */     throws ClassNotFoundException
/*     */   {
/* 426 */     ClassLoader cl = getParent();
/* 427 */     if (cl != null) {
/* 428 */       return cl.loadClass(classname);
/*     */     }
/* 430 */     return findSystemClass(classname);
/*     */   }
/*     */ 
/*     */   protected Package getPackage(String name) {
/* 434 */     return super.getPackage(name);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.Loader
 * JD-Core Version:    0.6.2
 */