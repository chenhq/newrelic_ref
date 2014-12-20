/*      */ package com.newrelic.javassist;
/*      */ 
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.URL;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ 
/*      */ public class ClassPool
/*      */ {
/*      */   private static Method defineClass1;
/*      */   private static Method defineClass2;
/*  105 */   public boolean childFirstLookup = false;
/*      */ 
/*  121 */   public static boolean doPruning = false;
/*      */   private int compressCount;
/*      */   private static final int COMPRESS_THRESHOLD = 100;
/*  136 */   public static boolean releaseUnmodifiedClassFile = true;
/*      */   protected ClassPoolTail source;
/*      */   protected ClassPool parent;
/*      */   protected Hashtable classes;
/*  145 */   private Hashtable cflow = null;
/*      */   private static final int INIT_HASH_SIZE = 191;
/*      */   private ArrayList importedPackages;
/*  228 */   private static ClassPool defaultPool = null;
/*      */ 
/*      */   public ClassPool()
/*      */   {
/*  155 */     this(null);
/*      */   }
/*      */ 
/*      */   public ClassPool(boolean useDefaultPath)
/*      */   {
/*  168 */     this(null);
/*  169 */     if (useDefaultPath)
/*  170 */       appendSystemPath();
/*      */   }
/*      */ 
/*      */   public ClassPool(ClassPool parent)
/*      */   {
/*  181 */     this.classes = new Hashtable(191);
/*  182 */     this.source = new ClassPoolTail();
/*  183 */     this.parent = parent;
/*  184 */     if (parent == null) {
/*  185 */       CtClass[] pt = CtClass.primitiveTypes;
/*  186 */       for (int i = 0; i < pt.length; i++) {
/*  187 */         this.classes.put(pt[i].getName(), pt[i]);
/*      */       }
/*      */     }
/*  190 */     this.cflow = null;
/*  191 */     this.compressCount = 0;
/*  192 */     clearImportedPackages();
/*      */   }
/*      */ 
/*      */   public static synchronized ClassPool getDefault()
/*      */   {
/*  220 */     if (defaultPool == null) {
/*  221 */       defaultPool = new ClassPool(null);
/*  222 */       defaultPool.appendSystemPath();
/*      */     }
/*      */ 
/*  225 */     return defaultPool;
/*      */   }
/*      */ 
/*      */   protected CtClass getCached(String classname)
/*      */   {
/*  238 */     return (CtClass)this.classes.get(classname);
/*      */   }
/*      */ 
/*      */   protected void cacheCtClass(String classname, CtClass c, boolean dynamic)
/*      */   {
/*  249 */     this.classes.put(classname, c);
/*      */   }
/*      */ 
/*      */   protected CtClass removeCached(String classname)
/*      */   {
/*  260 */     return (CtClass)this.classes.remove(classname);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  267 */     return this.source.toString();
/*      */   }
/*      */ 
/*      */   void compress()
/*      */   {
/*  275 */     if (this.compressCount++ > 100) {
/*  276 */       this.compressCount = 0;
/*  277 */       Enumeration e = this.classes.elements();
/*  278 */       while (e.hasMoreElements())
/*  279 */         ((CtClass)e.nextElement()).compress();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void importPackage(String packageName)
/*      */   {
/*  298 */     this.importedPackages.add(packageName);
/*      */   }
/*      */ 
/*      */   public void clearImportedPackages()
/*      */   {
/*  309 */     this.importedPackages = new ArrayList();
/*  310 */     this.importedPackages.add("java.lang");
/*      */   }
/*      */ 
/*      */   public Iterator getImportedPackages()
/*      */   {
/*  320 */     return this.importedPackages.iterator();
/*      */   }
/*      */ 
/*      */   public void recordInvalidClassName(String name)
/*      */   {
/*  334 */     this.source.recordInvalidClassName(name);
/*      */   }
/*      */ 
/*      */   void recordCflow(String name, String cname, String fname)
/*      */   {
/*  346 */     if (this.cflow == null) {
/*  347 */       this.cflow = new Hashtable();
/*      */     }
/*  349 */     this.cflow.put(name, new Object[] { cname, fname });
/*      */   }
/*      */ 
/*      */   public Object[] lookupCflow(String name)
/*      */   {
/*  358 */     if (this.cflow == null) {
/*  359 */       this.cflow = new Hashtable();
/*      */     }
/*  361 */     return (Object[])this.cflow.get(name);
/*      */   }
/*      */ 
/*      */   public CtClass getAndRename(String orgName, String newName)
/*      */     throws NotFoundException
/*      */   {
/*  385 */     CtClass clazz = get0(orgName, false);
/*  386 */     if (clazz == null) {
/*  387 */       throw new NotFoundException(orgName);
/*      */     }
/*  389 */     if ((clazz instanceof CtClassType)) {
/*  390 */       ((CtClassType)clazz).setClassPool(this);
/*      */     }
/*  392 */     clazz.setName(newName);
/*      */ 
/*  394 */     return clazz;
/*      */   }
/*      */ 
/*      */   synchronized void classNameChanged(String oldname, CtClass clazz)
/*      */   {
/*  403 */     CtClass c = getCached(oldname);
/*  404 */     if (c == clazz) {
/*  405 */       removeCached(oldname);
/*      */     }
/*  407 */     String newName = clazz.getName();
/*  408 */     checkNotFrozen(newName);
/*  409 */     cacheCtClass(newName, clazz, false);
/*      */   }
/*      */ 
/*      */   public CtClass get(String classname)
/*      */     throws NotFoundException
/*      */   {
/*      */     CtClass clazz;
/*      */     CtClass clazz;
/*  430 */     if (classname == null)
/*  431 */       clazz = null;
/*      */     else {
/*  433 */       clazz = get0(classname, true);
/*      */     }
/*  435 */     if (clazz == null) {
/*  436 */       throw new NotFoundException(classname);
/*      */     }
/*  438 */     clazz.incGetCounter();
/*  439 */     return clazz;
/*      */   }
/*      */ 
/*      */   public CtClass getOrNull(String classname)
/*      */   {
/*  458 */     CtClass clazz = null;
/*  459 */     if (classname == null) {
/*  460 */       clazz = null;
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/*  467 */         clazz = get0(classname, true);
/*      */       } catch (NotFoundException e) {
/*      */       }
/*      */     }
/*  471 */     if (clazz != null) {
/*  472 */       clazz.incGetCounter();
/*      */     }
/*  474 */     return clazz;
/*      */   }
/*      */ 
/*      */   public CtClass getCtClass(String classname)
/*      */     throws NotFoundException
/*      */   {
/*  498 */     if (classname.charAt(0) == '[') {
/*  499 */       return Descriptor.toCtClass(classname, this);
/*      */     }
/*  501 */     return get(classname);
/*      */   }
/*      */ 
/*      */   protected synchronized CtClass get0(String classname, boolean useCache)
/*      */     throws NotFoundException
/*      */   {
/*  512 */     CtClass clazz = null;
/*  513 */     if (useCache) {
/*  514 */       clazz = getCached(classname);
/*  515 */       if (clazz != null) {
/*  516 */         return clazz;
/*      */       }
/*      */     }
/*  519 */     if ((!this.childFirstLookup) && (this.parent != null)) {
/*  520 */       clazz = this.parent.get0(classname, useCache);
/*  521 */       if (clazz != null) {
/*  522 */         return clazz;
/*      */       }
/*      */     }
/*  525 */     clazz = createCtClass(classname, useCache);
/*  526 */     if (clazz != null)
/*      */     {
/*  528 */       if (useCache) {
/*  529 */         cacheCtClass(clazz.getName(), clazz, false);
/*      */       }
/*  531 */       return clazz;
/*      */     }
/*      */ 
/*  534 */     if ((this.childFirstLookup) && (this.parent != null)) {
/*  535 */       clazz = this.parent.get0(classname, useCache);
/*      */     }
/*  537 */     return clazz;
/*      */   }
/*      */ 
/*      */   protected CtClass createCtClass(String classname, boolean useCache)
/*      */   {
/*  549 */     if (classname.charAt(0) == '[') {
/*  550 */       classname = Descriptor.toClassName(classname);
/*      */     }
/*  552 */     if (classname.endsWith("[]")) {
/*  553 */       String base = classname.substring(0, classname.indexOf('['));
/*  554 */       if (((!useCache) || (getCached(base) == null)) && (find(base) == null)) {
/*  555 */         return null;
/*      */       }
/*  557 */       return new CtArray(classname, this);
/*      */     }
/*      */ 
/*  560 */     if (find(classname) == null) {
/*  561 */       return null;
/*      */     }
/*  563 */     return new CtClassType(classname, this);
/*      */   }
/*      */ 
/*      */   public URL find(String classname)
/*      */   {
/*  576 */     return this.source.find(classname);
/*      */   }
/*      */ 
/*      */   void checkNotFrozen(String classname)
/*      */     throws RuntimeException
/*      */   {
/*  588 */     CtClass clazz = getCached(classname);
/*  589 */     if (clazz == null) {
/*  590 */       if ((!this.childFirstLookup) && (this.parent != null)) {
/*      */         try {
/*  592 */           clazz = this.parent.get0(classname, true);
/*      */         } catch (NotFoundException e) {
/*      */         }
/*  595 */         if (clazz != null) {
/*  596 */           throw new RuntimeException(classname + " is in a parent ClassPool.  Use the parent.");
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*  601 */     else if (clazz.isFrozen())
/*  602 */       throw new RuntimeException(classname + ": frozen class (cannot edit)");
/*      */   }
/*      */ 
/*      */   CtClass checkNotExists(String classname)
/*      */   {
/*  613 */     CtClass clazz = getCached(classname);
/*  614 */     if ((clazz == null) && 
/*  615 */       (!this.childFirstLookup) && (this.parent != null))
/*      */       try {
/*  617 */         clazz = this.parent.get0(classname, true);
/*      */       }
/*      */       catch (NotFoundException e)
/*      */       {
/*      */       }
/*  622 */     return clazz;
/*      */   }
/*      */ 
/*      */   InputStream openClassfile(String classname)
/*      */     throws NotFoundException
/*      */   {
/*  628 */     return this.source.openClassfile(classname);
/*      */   }
/*      */ 
/*      */   void writeClassfile(String classname, OutputStream out)
/*      */     throws NotFoundException, IOException, CannotCompileException
/*      */   {
/*  634 */     this.source.writeClassfile(classname, out);
/*      */   }
/*      */ 
/*      */   public CtClass[] get(String[] classnames)
/*      */     throws NotFoundException
/*      */   {
/*  649 */     if (classnames == null) {
/*  650 */       return new CtClass[0];
/*      */     }
/*  652 */     int num = classnames.length;
/*  653 */     CtClass[] result = new CtClass[num];
/*  654 */     for (int i = 0; i < num; i++) {
/*  655 */       result[i] = get(classnames[i]);
/*      */     }
/*  657 */     return result;
/*      */   }
/*      */ 
/*      */   public CtMethod getMethod(String classname, String methodname)
/*      */     throws NotFoundException
/*      */   {
/*  670 */     CtClass c = get(classname);
/*  671 */     return c.getDeclaredMethod(methodname);
/*      */   }
/*      */ 
/*      */   public CtClass makeClass(InputStream classfile)
/*      */     throws IOException, RuntimeException
/*      */   {
/*  692 */     return makeClass(classfile, true);
/*      */   }
/*      */ 
/*      */   public CtClass makeClass(InputStream classfile, boolean ifNotFrozen)
/*      */     throws IOException, RuntimeException
/*      */   {
/*  712 */     compress();
/*  713 */     classfile = new BufferedInputStream(classfile);
/*  714 */     CtClass clazz = new CtClassType(classfile, this);
/*  715 */     clazz.checkModify();
/*  716 */     String classname = clazz.getName();
/*  717 */     if (ifNotFrozen) {
/*  718 */       checkNotFrozen(classname);
/*      */     }
/*  720 */     cacheCtClass(classname, clazz, true);
/*  721 */     return clazz;
/*      */   }
/*      */ 
/*      */   public CtClass makeClassIfNew(InputStream classfile)
/*      */     throws IOException, RuntimeException
/*      */   {
/*  742 */     compress();
/*  743 */     classfile = new BufferedInputStream(classfile);
/*  744 */     CtClass clazz = new CtClassType(classfile, this);
/*  745 */     clazz.checkModify();
/*  746 */     String classname = clazz.getName();
/*  747 */     CtClass found = checkNotExists(classname);
/*  748 */     if (found != null) {
/*  749 */       return found;
/*      */     }
/*  751 */     cacheCtClass(classname, clazz, true);
/*  752 */     return clazz;
/*      */   }
/*      */ 
/*      */   public CtClass makeClass(String classname)
/*      */     throws RuntimeException
/*      */   {
/*  773 */     return makeClass(classname, null);
/*      */   }
/*      */ 
/*      */   public synchronized CtClass makeClass(String classname, CtClass superclass)
/*      */     throws RuntimeException
/*      */   {
/*  796 */     checkNotFrozen(classname);
/*  797 */     CtClass clazz = new CtNewClass(classname, this, false, superclass);
/*  798 */     cacheCtClass(classname, clazz, true);
/*  799 */     return clazz;
/*      */   }
/*      */ 
/*      */   synchronized CtClass makeNestedClass(String classname)
/*      */   {
/*  810 */     checkNotFrozen(classname);
/*  811 */     CtClass clazz = new CtNewNestedClass(classname, this, false, null);
/*  812 */     cacheCtClass(classname, clazz, true);
/*  813 */     return clazz;
/*      */   }
/*      */ 
/*      */   public CtClass makeInterface(String name)
/*      */     throws RuntimeException
/*      */   {
/*  825 */     return makeInterface(name, null);
/*      */   }
/*      */ 
/*      */   public synchronized CtClass makeInterface(String name, CtClass superclass)
/*      */     throws RuntimeException
/*      */   {
/*  840 */     checkNotFrozen(name);
/*  841 */     CtClass clazz = new CtNewClass(name, this, true, superclass);
/*  842 */     cacheCtClass(name, clazz, true);
/*  843 */     return clazz;
/*      */   }
/*      */ 
/*      */   public ClassPath appendSystemPath()
/*      */   {
/*  857 */     return this.source.appendSystemPath();
/*      */   }
/*      */ 
/*      */   public ClassPath insertClassPath(ClassPath cp)
/*      */   {
/*  870 */     return this.source.insertClassPath(cp);
/*      */   }
/*      */ 
/*      */   public ClassPath appendClassPath(ClassPath cp)
/*      */   {
/*  883 */     return this.source.appendClassPath(cp);
/*      */   }
/*      */ 
/*      */   public ClassPath insertClassPath(String pathname)
/*      */     throws NotFoundException
/*      */   {
/*  901 */     return this.source.insertClassPath(pathname);
/*      */   }
/*      */ 
/*      */   public ClassPath appendClassPath(String pathname)
/*      */     throws NotFoundException
/*      */   {
/*  919 */     return this.source.appendClassPath(pathname);
/*      */   }
/*      */ 
/*      */   public void removeClassPath(ClassPath cp)
/*      */   {
/*  928 */     this.source.removeClassPath(cp);
/*      */   }
/*      */ 
/*      */   public void appendPathList(String pathlist)
/*      */     throws NotFoundException
/*      */   {
/*  944 */     char sep = File.pathSeparatorChar;
/*  945 */     int i = 0;
/*      */     while (true) {
/*  947 */       int j = pathlist.indexOf(sep, i);
/*  948 */       if (j < 0) {
/*  949 */         appendClassPath(pathlist.substring(i));
/*  950 */         break;
/*      */       }
/*      */ 
/*  953 */       appendClassPath(pathlist.substring(i, j));
/*  954 */       i = j + 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Class toClass(CtClass clazz)
/*      */     throws CannotCompileException
/*      */   {
/*  986 */     return toClass(clazz, getClassLoader());
/*      */   }
/*      */ 
/*      */   public ClassLoader getClassLoader()
/*      */   {
/* 1000 */     return getContextClassLoader();
/*      */   }
/*      */ 
/*      */   static ClassLoader getContextClassLoader()
/*      */   {
/* 1008 */     return Thread.currentThread().getContextClassLoader();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Class toClass(CtClass ct, ClassLoader loader)
/*      */     throws CannotCompileException
/*      */   {
/* 1028 */     return toClass(ct, loader, null);
/*      */   }
/*      */ 
/*      */   public Class toClass(CtClass ct, ClassLoader loader, ProtectionDomain domain)
/*      */     throws CannotCompileException
/*      */   {
/*      */     try
/*      */     {
/* 1065 */       byte[] b = ct.toBytecode();
/*      */       Object[] args;
/*      */       Method method;
/*      */       Object[] args;
/* 1068 */       if (domain == null) {
/* 1069 */         Method method = defineClass1;
/* 1070 */         args = new Object[] { ct.getName(), b, new Integer(0), new Integer(b.length) };
/*      */       }
/*      */       else
/*      */       {
/* 1074 */         method = defineClass2;
/* 1075 */         args = new Object[] { ct.getName(), b, new Integer(0), new Integer(b.length), domain };
/*      */       }
/*      */ 
/* 1079 */       return toClass2(method, loader, args);
/*      */     }
/*      */     catch (RuntimeException e) {
/* 1082 */       throw e;
/*      */     }
/*      */     catch (InvocationTargetException e) {
/* 1085 */       throw new CannotCompileException(e.getTargetException());
/*      */     }
/*      */     catch (Exception e) {
/* 1088 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static synchronized Class toClass2(Method method, ClassLoader loader, Object[] args)
/*      */     throws Exception
/*      */   {
/* 1096 */     method.setAccessible(true);
/*      */     try {
/* 1098 */       return (Class)method.invoke(loader, args);
/*      */     }
/*      */     finally {
/* 1101 */       method.setAccessible(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*   75 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*      */         public Object run() throws Exception {
/*   77 */           Class cl = Class.forName("java.lang.ClassLoader");
/*   78 */           ClassPool.access$002(cl.getDeclaredMethod("defineClass", new Class[] { String.class, new byte[0].getClass(), Integer.TYPE, Integer.TYPE }));
/*      */ 
/*   82 */           ClassPool.access$102(cl.getDeclaredMethod("defineClass", new Class[] { String.class, new byte[0].getClass(), Integer.TYPE, Integer.TYPE, ProtectionDomain.class }));
/*      */ 
/*   85 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (PrivilegedActionException pae) {
/*   90 */       throw new RuntimeException("cannot initialize ClassPool", pae.getException());
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.ClassPool
 * JD-Core Version:    0.6.2
 */