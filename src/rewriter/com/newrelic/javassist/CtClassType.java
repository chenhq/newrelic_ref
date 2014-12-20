/*      */ package com.newrelic.javassist;
/*      */ 
/*      */ import com.newrelic.javassist.bytecode.AccessFlag;
/*      */ import com.newrelic.javassist.bytecode.AnnotationsAttribute;
/*      */ import com.newrelic.javassist.bytecode.AttributeInfo;
/*      */ import com.newrelic.javassist.bytecode.BadBytecode;
/*      */ import com.newrelic.javassist.bytecode.Bytecode;
/*      */ import com.newrelic.javassist.bytecode.ClassFile;
/*      */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*      */ import com.newrelic.javassist.bytecode.CodeIterator;
/*      */ import com.newrelic.javassist.bytecode.ConstPool;
/*      */ import com.newrelic.javassist.bytecode.ConstantAttribute;
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import com.newrelic.javassist.bytecode.EnclosingMethodAttribute;
/*      */ import com.newrelic.javassist.bytecode.FieldInfo;
/*      */ import com.newrelic.javassist.bytecode.InnerClassesAttribute;
/*      */ import com.newrelic.javassist.bytecode.MethodInfo;
/*      */ import com.newrelic.javassist.bytecode.ParameterAnnotationsAttribute;
/*      */ import com.newrelic.javassist.bytecode.annotation.Annotation;
/*      */ import com.newrelic.javassist.compiler.AccessorMaker;
/*      */ import com.newrelic.javassist.compiler.CompileError;
/*      */ import com.newrelic.javassist.compiler.Javac;
/*      */ import com.newrelic.javassist.expr.ExprEditor;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ 
/*      */ class CtClassType extends CtClass
/*      */ {
/*      */   ClassPool classPool;
/*      */   boolean wasChanged;
/*      */   private boolean wasFrozen;
/*      */   boolean wasPruned;
/*      */   boolean gcConstPool;
/*      */   ClassFile classfile;
/*      */   byte[] rawClassfile;
/*      */   private WeakReference memberCache;
/*      */   private AccessorMaker accessors;
/*      */   private FieldInitLink fieldInitializers;
/*      */   private Hashtable hiddenMethods;
/*      */   private int uniqueNumberSeed;
/*   74 */   private boolean doPruning = ClassPool.doPruning;
/*      */   private int getCount;
/*      */   private static final int GET_THRESHOLD = 2;
/*      */ 
/*      */   CtClassType(String name, ClassPool cp)
/*      */   {
/*   79 */     super(name);
/*   80 */     this.classPool = cp;
/*   81 */     this.wasChanged = (this.wasFrozen = this.wasPruned = this.gcConstPool = 0);
/*   82 */     this.classfile = null;
/*   83 */     this.rawClassfile = null;
/*   84 */     this.memberCache = null;
/*   85 */     this.accessors = null;
/*   86 */     this.fieldInitializers = null;
/*   87 */     this.hiddenMethods = null;
/*   88 */     this.uniqueNumberSeed = 0;
/*   89 */     this.getCount = 0;
/*      */   }
/*      */ 
/*      */   CtClassType(InputStream ins, ClassPool cp) throws IOException {
/*   93 */     this((String)null, cp);
/*   94 */     this.classfile = new ClassFile(new DataInputStream(ins));
/*   95 */     this.qualifiedName = this.classfile.getName();
/*      */   }
/*      */ 
/*      */   protected void extendToString(StringBuffer buffer) {
/*   99 */     if (this.wasChanged) {
/*  100 */       buffer.append("changed ");
/*      */     }
/*  102 */     if (this.wasFrozen) {
/*  103 */       buffer.append("frozen ");
/*      */     }
/*  105 */     if (this.wasPruned) {
/*  106 */       buffer.append("pruned ");
/*      */     }
/*  108 */     buffer.append(Modifier.toString(getModifiers()));
/*  109 */     buffer.append(" class ");
/*  110 */     buffer.append(getName());
/*      */     try
/*      */     {
/*  113 */       CtClass ext = getSuperclass();
/*  114 */       if (ext != null) {
/*  115 */         String name = ext.getName();
/*  116 */         if (!name.equals("java.lang.Object"))
/*  117 */           buffer.append(" extends " + ext.getName());
/*      */       }
/*      */     }
/*      */     catch (NotFoundException e) {
/*  121 */       buffer.append(" extends ??");
/*      */     }
/*      */     try
/*      */     {
/*  125 */       CtClass[] intf = getInterfaces();
/*  126 */       if (intf.length > 0) {
/*  127 */         buffer.append(" implements ");
/*      */       }
/*  129 */       for (int i = 0; i < intf.length; i++) {
/*  130 */         buffer.append(intf[i].getName());
/*  131 */         buffer.append(", ");
/*      */       }
/*      */     }
/*      */     catch (NotFoundException e) {
/*  135 */       buffer.append(" extends ??");
/*      */     }
/*      */ 
/*  138 */     CtMember.Cache memCache = getMembers();
/*  139 */     exToString(buffer, " fields=", memCache.fieldHead(), memCache.lastField());
/*      */ 
/*  141 */     exToString(buffer, " constructors=", memCache.consHead(), memCache.lastCons());
/*      */ 
/*  143 */     exToString(buffer, " methods=", memCache.methodHead(), memCache.lastMethod());
/*      */   }
/*      */ 
/*      */   private void exToString(StringBuffer buffer, String msg, CtMember head, CtMember tail)
/*      */   {
/*  149 */     buffer.append(msg);
/*  150 */     while (head != tail) {
/*  151 */       head = head.next();
/*  152 */       buffer.append(head);
/*  153 */       buffer.append(", ");
/*      */     }
/*      */   }
/*      */ 
/*      */   public AccessorMaker getAccessorMaker() {
/*  158 */     if (this.accessors == null) {
/*  159 */       this.accessors = new AccessorMaker(this);
/*      */     }
/*  161 */     return this.accessors;
/*      */   }
/*      */ 
/*      */   public ClassFile getClassFile2() {
/*  165 */     ClassFile cfile = this.classfile;
/*  166 */     if (cfile != null) {
/*  167 */       return cfile;
/*      */     }
/*  169 */     this.classPool.compress();
/*  170 */     if (this.rawClassfile != null) {
/*      */       try {
/*  172 */         this.classfile = new ClassFile(new DataInputStream(new ByteArrayInputStream(this.rawClassfile)));
/*      */ 
/*  174 */         this.rawClassfile = null;
/*  175 */         this.getCount = 2;
/*  176 */         return this.classfile;
/*      */       }
/*      */       catch (IOException e) {
/*  179 */         throw new RuntimeException(e.toString(), e);
/*      */       }
/*      */     }
/*      */ 
/*  183 */     InputStream fin = null;
/*      */     try {
/*  185 */       fin = this.classPool.openClassfile(getName());
/*  186 */       if (fin == null) {
/*  187 */         throw new NotFoundException(getName());
/*      */       }
/*  189 */       fin = new BufferedInputStream(fin);
/*  190 */       ClassFile cf = new ClassFile(new DataInputStream(fin));
/*  191 */       if (!cf.getName().equals(this.qualifiedName)) {
/*  192 */         throw new RuntimeException("cannot find " + this.qualifiedName + ": " + cf.getName() + " found in " + this.qualifiedName.replace('.', '/') + ".class");
/*      */       }
/*      */ 
/*  196 */       this.classfile = cf;
/*  197 */       return cf;
/*      */     }
/*      */     catch (NotFoundException e) {
/*  200 */       throw new RuntimeException(e.toString(), e);
/*      */     }
/*      */     catch (IOException e) {
/*  203 */       throw new RuntimeException(e.toString(), e);
/*      */     }
/*      */     finally {
/*  206 */       if (fin != null)
/*      */         try {
/*  208 */           fin.close();
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   final void incGetCounter()
/*      */   {
/*  219 */     this.getCount += 1;
/*      */   }
/*      */ 
/*      */   void compress()
/*      */   {
/*  227 */     if (this.getCount < 2) {
/*  228 */       if ((!isModified()) && (ClassPool.releaseUnmodifiedClassFile))
/*  229 */         removeClassFile();
/*  230 */       else if ((isFrozen()) && (!this.wasPruned))
/*  231 */         saveClassFile();
/*      */     }
/*  233 */     this.getCount = 0;
/*      */   }
/*      */ 
/*      */   private synchronized void saveClassFile()
/*      */   {
/*  243 */     if ((this.classfile == null) || (hasMemberCache() != null)) {
/*  244 */       return;
/*      */     }
/*  246 */     ByteArrayOutputStream barray = new ByteArrayOutputStream();
/*  247 */     DataOutputStream out = new DataOutputStream(barray);
/*      */     try {
/*  249 */       this.classfile.write(out);
/*  250 */       barray.close();
/*  251 */       this.rawClassfile = barray.toByteArray();
/*  252 */       this.classfile = null;
/*      */     } catch (IOException e) {
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void removeClassFile() {
/*  258 */     if ((this.classfile != null) && (!isModified()) && (hasMemberCache() == null))
/*  259 */       this.classfile = null; 
/*      */   }
/*      */ 
/*  262 */   public ClassPool getClassPool() { return this.classPool; } 
/*      */   void setClassPool(ClassPool cp) {
/*  264 */     this.classPool = cp;
/*      */   }
/*      */   public URL getURL() throws NotFoundException {
/*  267 */     URL url = this.classPool.find(getName());
/*  268 */     if (url == null) {
/*  269 */       throw new NotFoundException(getName());
/*      */     }
/*  271 */     return url;
/*      */   }
/*      */   public boolean isModified() {
/*  274 */     return this.wasChanged;
/*      */   }
/*  276 */   public boolean isFrozen() { return this.wasFrozen; } 
/*      */   public void freeze() {
/*  278 */     this.wasFrozen = true;
/*      */   }
/*      */   void checkModify() throws RuntimeException {
/*  281 */     if (isFrozen()) {
/*  282 */       String msg = getName() + " class is frozen";
/*  283 */       if (this.wasPruned) {
/*  284 */         msg = msg + " and pruned";
/*      */       }
/*  286 */       throw new RuntimeException(msg);
/*      */     }
/*      */ 
/*  289 */     this.wasChanged = true;
/*      */   }
/*      */ 
/*      */   public void defrost() {
/*  293 */     checkPruned("defrost");
/*  294 */     this.wasFrozen = false;
/*      */   }
/*      */ 
/*      */   public boolean subtypeOf(CtClass clazz) throws NotFoundException
/*      */   {
/*  299 */     String cname = clazz.getName();
/*  300 */     if ((this == clazz) || (getName().equals(cname))) {
/*  301 */       return true;
/*      */     }
/*  303 */     ClassFile file = getClassFile2();
/*  304 */     String supername = file.getSuperclass();
/*  305 */     if ((supername != null) && (supername.equals(cname))) {
/*  306 */       return true;
/*      */     }
/*  308 */     String[] ifs = file.getInterfaces();
/*  309 */     int num = ifs.length;
/*  310 */     for (int i = 0; i < num; i++) {
/*  311 */       if (ifs[i].equals(cname))
/*  312 */         return true;
/*      */     }
/*  314 */     if ((supername != null) && (this.classPool.get(supername).subtypeOf(clazz))) {
/*  315 */       return true;
/*      */     }
/*  317 */     for (i = 0; i < num; i++) {
/*  318 */       if (this.classPool.get(ifs[i]).subtypeOf(clazz))
/*  319 */         return true;
/*      */     }
/*  321 */     return false;
/*      */   }
/*      */ 
/*      */   public void setName(String name) throws RuntimeException {
/*  325 */     String oldname = getName();
/*  326 */     if (name.equals(oldname)) {
/*  327 */       return;
/*      */     }
/*      */ 
/*  330 */     this.classPool.checkNotFrozen(name);
/*  331 */     ClassFile cf = getClassFile2();
/*  332 */     super.setName(name);
/*  333 */     cf.setName(name);
/*  334 */     nameReplaced();
/*  335 */     this.classPool.classNameChanged(oldname, this);
/*      */   }
/*      */ 
/*      */   public void replaceClassName(ClassMap classnames)
/*      */     throws RuntimeException
/*      */   {
/*  341 */     String oldClassName = getName();
/*  342 */     String newClassName = (String)classnames.get(Descriptor.toJvmName(oldClassName));
/*      */ 
/*  344 */     if (newClassName != null) {
/*  345 */       newClassName = Descriptor.toJavaName(newClassName);
/*      */ 
/*  347 */       this.classPool.checkNotFrozen(newClassName);
/*      */     }
/*      */ 
/*  350 */     super.replaceClassName(classnames);
/*  351 */     ClassFile cf = getClassFile2();
/*  352 */     cf.renameClass(classnames);
/*  353 */     nameReplaced();
/*      */ 
/*  355 */     if (newClassName != null) {
/*  356 */       super.setName(newClassName);
/*  357 */       this.classPool.classNameChanged(oldClassName, this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void replaceClassName(String oldname, String newname)
/*      */     throws RuntimeException
/*      */   {
/*  364 */     String thisname = getName();
/*  365 */     if (thisname.equals(oldname)) {
/*  366 */       setName(newname);
/*      */     } else {
/*  368 */       super.replaceClassName(oldname, newname);
/*  369 */       getClassFile2().renameClass(oldname, newname);
/*  370 */       nameReplaced();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isInterface() {
/*  375 */     return Modifier.isInterface(getModifiers());
/*      */   }
/*      */ 
/*      */   public boolean isAnnotation() {
/*  379 */     return Modifier.isAnnotation(getModifiers());
/*      */   }
/*      */ 
/*      */   public boolean isEnum() {
/*  383 */     return Modifier.isEnum(getModifiers());
/*      */   }
/*      */ 
/*      */   public int getModifiers() {
/*  387 */     ClassFile cf = getClassFile2();
/*  388 */     int acc = cf.getAccessFlags();
/*  389 */     acc = AccessFlag.clear(acc, 32);
/*  390 */     int inner = cf.getInnerAccessFlags();
/*  391 */     if ((inner != -1) && ((inner & 0x8) != 0)) {
/*  392 */       acc |= 8;
/*      */     }
/*  394 */     return AccessFlag.toModifier(acc);
/*      */   }
/*      */ 
/*      */   public CtClass[] getNestedClasses() throws NotFoundException {
/*  398 */     ClassFile cf = getClassFile2();
/*  399 */     InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
/*      */ 
/*  401 */     if (ica == null) {
/*  402 */       return new CtClass[0];
/*      */     }
/*  404 */     String thisName = cf.getName();
/*  405 */     int n = ica.tableLength();
/*  406 */     ArrayList list = new ArrayList(n);
/*  407 */     for (int i = 0; i < n; i++) {
/*  408 */       String outer = ica.outerClass(i);
/*      */ 
/*  413 */       if ((outer == null) || (outer.equals(thisName))) {
/*  414 */         String inner = ica.innerClass(i);
/*  415 */         if (inner != null) {
/*  416 */           list.add(this.classPool.get(inner));
/*      */         }
/*      */       }
/*      */     }
/*  420 */     return (CtClass[])list.toArray(new CtClass[list.size()]);
/*      */   }
/*      */ 
/*      */   public void setModifiers(int mod) {
/*  424 */     ClassFile cf = getClassFile2();
/*  425 */     if (Modifier.isStatic(mod)) {
/*  426 */       int flags = cf.getInnerAccessFlags();
/*  427 */       if ((flags != -1) && ((flags & 0x8) != 0))
/*  428 */         mod &= -9;
/*      */       else {
/*  430 */         throw new RuntimeException("cannot change " + getName() + " into a static class");
/*      */       }
/*      */     }
/*  433 */     checkModify();
/*  434 */     cf.setAccessFlags(AccessFlag.of(mod));
/*      */   }
/*      */ 
/*      */   public boolean hasAnnotation(Class clz) {
/*  438 */     ClassFile cf = getClassFile2();
/*  439 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)cf.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  441 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)cf.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  443 */     return hasAnnotationType(clz, getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   static boolean hasAnnotationType(Class clz, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2)
/*      */   {
/*      */     Annotation[] anno1;
/*      */     Annotation[] anno1;
/*  451 */     if (a1 == null)
/*  452 */       anno1 = null;
/*      */     else
/*  454 */       anno1 = a1.getAnnotations();
/*      */     Annotation[] anno2;
/*      */     Annotation[] anno2;
/*  456 */     if (a2 == null)
/*  457 */       anno2 = null;
/*      */     else {
/*  459 */       anno2 = a2.getAnnotations();
/*      */     }
/*  461 */     String typeName = clz.getName();
/*  462 */     if (anno1 != null) {
/*  463 */       for (int i = 0; i < anno1.length; i++)
/*  464 */         if (anno1[i].getTypeName().equals(typeName))
/*  465 */           return true;
/*      */     }
/*  467 */     if (anno2 != null) {
/*  468 */       for (int i = 0; i < anno2.length; i++)
/*  469 */         if (anno2[i].getTypeName().equals(typeName))
/*  470 */           return true;
/*      */     }
/*  472 */     return false;
/*      */   }
/*      */ 
/*      */   public Object getAnnotation(Class clz) throws ClassNotFoundException {
/*  476 */     ClassFile cf = getClassFile2();
/*  477 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)cf.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  479 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)cf.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  481 */     return getAnnotationType(clz, getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   static Object getAnnotationType(Class clz, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2)
/*      */     throws ClassNotFoundException
/*      */   {
/*      */     Annotation[] anno1;
/*      */     Annotation[] anno1;
/*  490 */     if (a1 == null)
/*  491 */       anno1 = null;
/*      */     else
/*  493 */       anno1 = a1.getAnnotations();
/*      */     Annotation[] anno2;
/*      */     Annotation[] anno2;
/*  495 */     if (a2 == null)
/*  496 */       anno2 = null;
/*      */     else {
/*  498 */       anno2 = a2.getAnnotations();
/*      */     }
/*  500 */     String typeName = clz.getName();
/*  501 */     if (anno1 != null) {
/*  502 */       for (int i = 0; i < anno1.length; i++)
/*  503 */         if (anno1[i].getTypeName().equals(typeName))
/*  504 */           return toAnnoType(anno1[i], cp);
/*      */     }
/*  506 */     if (anno2 != null) {
/*  507 */       for (int i = 0; i < anno2.length; i++)
/*  508 */         if (anno2[i].getTypeName().equals(typeName))
/*  509 */           return toAnnoType(anno2[i], cp);
/*      */     }
/*  511 */     return null;
/*      */   }
/*      */ 
/*      */   public Object[] getAnnotations() throws ClassNotFoundException {
/*  515 */     return getAnnotations(false);
/*      */   }
/*      */ 
/*      */   public Object[] getAvailableAnnotations() {
/*      */     try {
/*  520 */       return getAnnotations(true);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  523 */       throw new RuntimeException("Unexpected exception ", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object[] getAnnotations(boolean ignoreNotFound)
/*      */     throws ClassNotFoundException
/*      */   {
/*  530 */     ClassFile cf = getClassFile2();
/*  531 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)cf.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  533 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)cf.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  535 */     return toAnnotationType(ignoreNotFound, getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   static Object[] toAnnotationType(boolean ignoreNotFound, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2)
/*      */     throws ClassNotFoundException
/*      */   {
/*      */     int size1;
/*      */     Annotation[] anno1;
/*      */     int size1;
/*  545 */     if (a1 == null) {
/*  546 */       Annotation[] anno1 = null;
/*  547 */       size1 = 0;
/*      */     }
/*      */     else {
/*  550 */       anno1 = a1.getAnnotations();
/*  551 */       size1 = anno1.length;
/*      */     }
/*      */     int size2;
/*      */     Annotation[] anno2;
/*      */     int size2;
/*  554 */     if (a2 == null) {
/*  555 */       Annotation[] anno2 = null;
/*  556 */       size2 = 0;
/*      */     }
/*      */     else {
/*  559 */       anno2 = a2.getAnnotations();
/*  560 */       size2 = anno2.length;
/*      */     }
/*      */ 
/*  563 */     if (!ignoreNotFound) {
/*  564 */       Object[] result = new Object[size1 + size2];
/*  565 */       for (int i = 0; i < size1; i++) {
/*  566 */         result[i] = toAnnoType(anno1[i], cp);
/*      */       }
/*  568 */       for (int j = 0; j < size2; j++) {
/*  569 */         result[(j + size1)] = toAnnoType(anno2[j], cp);
/*      */       }
/*  571 */       return result;
/*      */     }
/*      */ 
/*  574 */     ArrayList annotations = new ArrayList();
/*  575 */     for (int i = 0; i < size1; i++)
/*      */       try {
/*  577 */         annotations.add(toAnnoType(anno1[i], cp));
/*      */       }
/*      */       catch (ClassNotFoundException e) {
/*      */       }
/*  581 */     for (int j = 0; j < size2; j++)
/*      */       try {
/*  583 */         annotations.add(toAnnoType(anno2[j], cp));
/*      */       }
/*      */       catch (ClassNotFoundException e)
/*      */       {
/*      */       }
/*  588 */     return annotations.toArray();
/*      */   }
/*      */ 
/*      */   static Object[][] toAnnotationType(boolean ignoreNotFound, ClassPool cp, ParameterAnnotationsAttribute a1, ParameterAnnotationsAttribute a2, MethodInfo minfo)
/*      */     throws ClassNotFoundException
/*      */   {
/*  598 */     int numParameters = 0;
/*  599 */     if (a1 != null)
/*  600 */       numParameters = a1.numParameters();
/*  601 */     else if (a2 != null)
/*  602 */       numParameters = a2.numParameters();
/*      */     else {
/*  604 */       numParameters = Descriptor.numOfParameters(minfo.getDescriptor());
/*      */     }
/*  606 */     Object[][] result = new Object[numParameters][];
/*  607 */     for (int i = 0; i < numParameters; i++)
/*      */     {
/*      */       int size1;
/*      */       Annotation[] anno1;
/*      */       int size1;
/*  611 */       if (a1 == null) {
/*  612 */         Annotation[] anno1 = null;
/*  613 */         size1 = 0;
/*      */       }
/*      */       else {
/*  616 */         anno1 = a1.getAnnotations()[i];
/*  617 */         size1 = anno1.length;
/*      */       }
/*      */       int size2;
/*      */       Annotation[] anno2;
/*      */       int size2;
/*  620 */       if (a2 == null) {
/*  621 */         Annotation[] anno2 = null;
/*  622 */         size2 = 0;
/*      */       }
/*      */       else {
/*  625 */         anno2 = a2.getAnnotations()[i];
/*  626 */         size2 = anno2.length;
/*      */       }
/*      */ 
/*  629 */       if (!ignoreNotFound) {
/*  630 */         result[i] = new Object[size1 + size2];
/*  631 */         for (int j = 0; j < size1; j++) {
/*  632 */           result[i][j] = toAnnoType(anno1[j], cp);
/*      */         }
/*  634 */         for (int j = 0; j < size2; j++)
/*  635 */           result[i][(j + size1)] = toAnnoType(anno2[j], cp);
/*      */       }
/*      */       else {
/*  638 */         ArrayList annotations = new ArrayList();
/*  639 */         for (int j = 0; j < size1; j++)
/*      */           try {
/*  641 */             annotations.add(toAnnoType(anno1[j], cp));
/*      */           }
/*      */           catch (ClassNotFoundException e) {
/*      */           }
/*  645 */         for (int j = 0; j < size2; j++)
/*      */           try {
/*  647 */             annotations.add(toAnnoType(anno2[j], cp));
/*      */           }
/*      */           catch (ClassNotFoundException e)
/*      */           {
/*      */           }
/*  652 */         result[i] = annotations.toArray();
/*      */       }
/*      */     }
/*      */ 
/*  656 */     return result;
/*      */   }
/*      */ 
/*      */   private static Object toAnnoType(Annotation anno, ClassPool cp) throws ClassNotFoundException
/*      */   {
/*      */     try
/*      */     {
/*  663 */       ClassLoader cl = cp.getClassLoader();
/*  664 */       return anno.toAnnotationType(cl, cp);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  667 */       ClassLoader cl2 = cp.getClass().getClassLoader();
/*  668 */       return anno.toAnnotationType(cl2, cp);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean subclassOf(CtClass superclass) {
/*  673 */     if (superclass == null) {
/*  674 */       return false;
/*      */     }
/*  676 */     String superName = superclass.getName();
/*  677 */     CtClass curr = this;
/*      */     try {
/*  679 */       while (curr != null) {
/*  680 */         if (curr.getName().equals(superName)) {
/*  681 */           return true;
/*      */         }
/*  683 */         curr = curr.getSuperclass();
/*      */       }
/*      */     } catch (Exception ignored) {
/*      */     }
/*  687 */     return false;
/*      */   }
/*      */ 
/*      */   public CtClass getSuperclass() throws NotFoundException {
/*  691 */     String supername = getClassFile2().getSuperclass();
/*  692 */     if (supername == null) {
/*  693 */       return null;
/*      */     }
/*  695 */     return this.classPool.get(supername);
/*      */   }
/*      */ 
/*      */   public void setSuperclass(CtClass clazz) throws CannotCompileException {
/*  699 */     checkModify();
/*  700 */     if (isInterface())
/*  701 */       addInterface(clazz);
/*      */     else
/*  703 */       getClassFile2().setSuperclass(clazz.getName());
/*      */   }
/*      */ 
/*      */   public CtClass[] getInterfaces() throws NotFoundException {
/*  707 */     String[] ifs = getClassFile2().getInterfaces();
/*  708 */     int num = ifs.length;
/*  709 */     CtClass[] ifc = new CtClass[num];
/*  710 */     for (int i = 0; i < num; i++) {
/*  711 */       ifc[i] = this.classPool.get(ifs[i]);
/*      */     }
/*  713 */     return ifc;
/*      */   }
/*      */ 
/*      */   public void setInterfaces(CtClass[] list) {
/*  717 */     checkModify();
/*      */     String[] ifs;
/*      */     String[] ifs;
/*  719 */     if (list == null) {
/*  720 */       ifs = new String[0];
/*      */     } else {
/*  722 */       int num = list.length;
/*  723 */       ifs = new String[num];
/*  724 */       for (int i = 0; i < num; i++) {
/*  725 */         ifs[i] = list[i].getName();
/*      */       }
/*      */     }
/*  728 */     getClassFile2().setInterfaces(ifs);
/*      */   }
/*      */ 
/*      */   public void addInterface(CtClass anInterface) {
/*  732 */     checkModify();
/*  733 */     if (anInterface != null)
/*  734 */       getClassFile2().addInterface(anInterface.getName());
/*      */   }
/*      */ 
/*      */   public CtClass getDeclaringClass() throws NotFoundException {
/*  738 */     ClassFile cf = getClassFile2();
/*  739 */     InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
/*      */ 
/*  741 */     if (ica == null) {
/*  742 */       return null;
/*      */     }
/*  744 */     String name = getName();
/*  745 */     int n = ica.tableLength();
/*  746 */     for (int i = 0; i < n; i++) {
/*  747 */       if (name.equals(ica.innerClass(i))) {
/*  748 */         String outName = ica.outerClass(i);
/*  749 */         if (outName != null) {
/*  750 */           return this.classPool.get(outName);
/*      */         }
/*      */ 
/*  753 */         EnclosingMethodAttribute ema = (EnclosingMethodAttribute)cf.getAttribute("EnclosingMethod");
/*      */ 
/*  756 */         if (ema != null) {
/*  757 */           return this.classPool.get(ema.className());
/*      */         }
/*      */       }
/*      */     }
/*  761 */     return null;
/*      */   }
/*      */ 
/*      */   public CtMethod getEnclosingMethod() throws NotFoundException {
/*  765 */     ClassFile cf = getClassFile2();
/*  766 */     EnclosingMethodAttribute ema = (EnclosingMethodAttribute)cf.getAttribute("EnclosingMethod");
/*      */ 
/*  769 */     if (ema != null) {
/*  770 */       CtClass enc = this.classPool.get(ema.className());
/*  771 */       return enc.getMethod(ema.methodName(), ema.methodDescriptor());
/*      */     }
/*      */ 
/*  774 */     return null;
/*      */   }
/*      */ 
/*      */   public CtClass makeNestedClass(String name, boolean isStatic) {
/*  778 */     if (!isStatic) {
/*  779 */       throw new RuntimeException("sorry, only nested static class is supported");
/*      */     }
/*      */ 
/*  782 */     checkModify();
/*  783 */     CtClass c = this.classPool.makeNestedClass(getName() + "$" + name);
/*  784 */     ClassFile cf = getClassFile2();
/*  785 */     ClassFile cf2 = c.getClassFile2();
/*  786 */     InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
/*      */ 
/*  788 */     if (ica == null) {
/*  789 */       ica = new InnerClassesAttribute(cf.getConstPool());
/*  790 */       cf.addAttribute(ica);
/*      */     }
/*      */ 
/*  793 */     ica.append(c.getName(), getName(), name, cf2.getAccessFlags() & 0xFFFFFFDF | 0x8);
/*      */ 
/*  795 */     cf2.addAttribute(ica.copy(cf2.getConstPool(), null));
/*  796 */     return c;
/*      */   }
/*      */ 
/*      */   private void nameReplaced()
/*      */   {
/*  802 */     CtMember.Cache cache = hasMemberCache();
/*  803 */     if (cache != null) {
/*  804 */       CtMember mth = cache.methodHead();
/*  805 */       CtMember tail = cache.lastMethod();
/*  806 */       while (mth != tail) {
/*  807 */         mth = mth.next();
/*  808 */         mth.nameReplaced();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected CtMember.Cache hasMemberCache()
/*      */   {
/*  817 */     if (this.memberCache != null) {
/*  818 */       return (CtMember.Cache)this.memberCache.get();
/*      */     }
/*  820 */     return null;
/*      */   }
/*      */ 
/*      */   protected synchronized CtMember.Cache getMembers() {
/*  824 */     CtMember.Cache cache = null;
/*  825 */     if ((this.memberCache == null) || ((cache = (CtMember.Cache)this.memberCache.get()) == null))
/*      */     {
/*  827 */       cache = new CtMember.Cache(this);
/*  828 */       makeFieldCache(cache);
/*  829 */       makeBehaviorCache(cache);
/*  830 */       this.memberCache = new WeakReference(cache);
/*      */     }
/*      */ 
/*  833 */     return cache;
/*      */   }
/*      */ 
/*      */   private void makeFieldCache(CtMember.Cache cache) {
/*  837 */     List list = getClassFile2().getFields();
/*  838 */     int n = list.size();
/*  839 */     for (int i = 0; i < n; i++) {
/*  840 */       FieldInfo finfo = (FieldInfo)list.get(i);
/*  841 */       CtField newField = new CtField(finfo, this);
/*  842 */       cache.addField(newField);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void makeBehaviorCache(CtMember.Cache cache) {
/*  847 */     List list = getClassFile2().getMethods();
/*  848 */     int n = list.size();
/*  849 */     for (int i = 0; i < n; i++) {
/*  850 */       MethodInfo minfo = (MethodInfo)list.get(i);
/*  851 */       if (minfo.isMethod()) {
/*  852 */         CtMethod newMethod = new CtMethod(minfo, this);
/*  853 */         cache.addMethod(newMethod);
/*      */       }
/*      */       else {
/*  856 */         CtConstructor newCons = new CtConstructor(minfo, this);
/*  857 */         cache.addConstructor(newCons);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public CtField[] getFields() {
/*  863 */     ArrayList alist = new ArrayList();
/*  864 */     getFields(alist, this);
/*  865 */     return (CtField[])alist.toArray(new CtField[alist.size()]);
/*      */   }
/*      */ 
/*      */   private static void getFields(ArrayList alist, CtClass cc)
/*      */   {
/*  870 */     if (cc == null)
/*  871 */       return;
/*      */     try
/*      */     {
/*  874 */       getFields(alist, cc.getSuperclass());
/*      */     }
/*      */     catch (NotFoundException e) {
/*      */     }
/*      */     try {
/*  879 */       CtClass[] ifs = cc.getInterfaces();
/*  880 */       int num = ifs.length;
/*  881 */       for (int i = 0; i < num; i++)
/*  882 */         getFields(alist, ifs[i]);
/*      */     }
/*      */     catch (NotFoundException e) {
/*      */     }
/*  886 */     CtMember.Cache memCache = ((CtClassType)cc).getMembers();
/*  887 */     CtMember field = memCache.fieldHead();
/*  888 */     CtMember tail = memCache.lastField();
/*  889 */     while (field != tail) {
/*  890 */       field = field.next();
/*  891 */       if (!Modifier.isPrivate(field.getModifiers()))
/*  892 */         alist.add(field);
/*      */     }
/*      */   }
/*      */ 
/*      */   public CtField getField(String name) throws NotFoundException {
/*  897 */     CtField f = getField2(name);
/*  898 */     if (f == null) {
/*  899 */       throw new NotFoundException("field: " + name + " in " + getName());
/*      */     }
/*  901 */     return f;
/*      */   }
/*      */ 
/*      */   CtField getField2(String name) {
/*  905 */     CtField df = getDeclaredField2(name);
/*  906 */     if (df != null)
/*  907 */       return df;
/*      */     try
/*      */     {
/*  910 */       CtClass[] ifs = getInterfaces();
/*  911 */       int num = ifs.length;
/*  912 */       for (int i = 0; i < num; i++) {
/*  913 */         CtField f = ifs[i].getField2(name);
/*  914 */         if (f != null) {
/*  915 */           return f;
/*      */         }
/*      */       }
/*  918 */       CtClass s = getSuperclass();
/*  919 */       if (s != null)
/*  920 */         return s.getField2(name);
/*      */     } catch (NotFoundException e) {
/*      */     }
/*  923 */     return null;
/*      */   }
/*      */ 
/*      */   public CtField[] getDeclaredFields() {
/*  927 */     CtMember.Cache memCache = getMembers();
/*  928 */     CtMember field = memCache.fieldHead();
/*  929 */     CtMember tail = memCache.lastField();
/*  930 */     int num = CtMember.Cache.count(field, tail);
/*  931 */     CtField[] cfs = new CtField[num];
/*  932 */     int i = 0;
/*  933 */     while (field != tail) {
/*  934 */       field = field.next();
/*  935 */       cfs[(i++)] = ((CtField)field);
/*      */     }
/*      */ 
/*  938 */     return cfs;
/*      */   }
/*      */ 
/*      */   public CtField getDeclaredField(String name) throws NotFoundException {
/*  942 */     CtField f = getDeclaredField2(name);
/*  943 */     if (f == null) {
/*  944 */       throw new NotFoundException("field: " + name + " in " + getName());
/*      */     }
/*  946 */     return f;
/*      */   }
/*      */ 
/*      */   private CtField getDeclaredField2(String name) {
/*  950 */     CtMember.Cache memCache = getMembers();
/*  951 */     CtMember field = memCache.fieldHead();
/*  952 */     CtMember tail = memCache.lastField();
/*  953 */     while (field != tail) {
/*  954 */       field = field.next();
/*  955 */       if (field.getName().equals(name)) {
/*  956 */         return (CtField)field;
/*      */       }
/*      */     }
/*  959 */     return null;
/*      */   }
/*      */ 
/*      */   public CtBehavior[] getDeclaredBehaviors() {
/*  963 */     CtMember.Cache memCache = getMembers();
/*  964 */     CtMember cons = memCache.consHead();
/*  965 */     CtMember consTail = memCache.lastCons();
/*  966 */     int cnum = CtMember.Cache.count(cons, consTail);
/*  967 */     CtMember mth = memCache.methodHead();
/*  968 */     CtMember mthTail = memCache.lastMethod();
/*  969 */     int mnum = CtMember.Cache.count(mth, mthTail);
/*      */ 
/*  971 */     CtBehavior[] cb = new CtBehavior[cnum + mnum];
/*  972 */     int i = 0;
/*  973 */     while (cons != consTail) {
/*  974 */       cons = cons.next();
/*  975 */       cb[(i++)] = ((CtBehavior)cons);
/*      */     }
/*      */ 
/*  978 */     while (mth != mthTail) {
/*  979 */       mth = mth.next();
/*  980 */       cb[(i++)] = ((CtBehavior)mth);
/*      */     }
/*      */ 
/*  983 */     return cb;
/*      */   }
/*      */ 
/*      */   public CtConstructor[] getConstructors() {
/*  987 */     CtMember.Cache memCache = getMembers();
/*  988 */     CtMember cons = memCache.consHead();
/*  989 */     CtMember consTail = memCache.lastCons();
/*      */ 
/*  991 */     int n = 0;
/*  992 */     CtMember mem = cons;
/*  993 */     while (mem != consTail) {
/*  994 */       mem = mem.next();
/*  995 */       if (isPubCons((CtConstructor)mem)) {
/*  996 */         n++;
/*      */       }
/*      */     }
/*  999 */     CtConstructor[] result = new CtConstructor[n];
/* 1000 */     int i = 0;
/* 1001 */     mem = cons;
/* 1002 */     while (mem != consTail) {
/* 1003 */       mem = mem.next();
/* 1004 */       CtConstructor cc = (CtConstructor)mem;
/* 1005 */       if (isPubCons(cc)) {
/* 1006 */         result[(i++)] = cc;
/*      */       }
/*      */     }
/* 1009 */     return result;
/*      */   }
/*      */ 
/*      */   private static boolean isPubCons(CtConstructor cons) {
/* 1013 */     return (!Modifier.isPrivate(cons.getModifiers())) && (cons.isConstructor());
/*      */   }
/*      */ 
/*      */   public CtConstructor getConstructor(String desc)
/*      */     throws NotFoundException
/*      */   {
/* 1020 */     CtMember.Cache memCache = getMembers();
/* 1021 */     CtMember cons = memCache.consHead();
/* 1022 */     CtMember consTail = memCache.lastCons();
/*      */ 
/* 1024 */     while (cons != consTail) {
/* 1025 */       cons = cons.next();
/* 1026 */       CtConstructor cc = (CtConstructor)cons;
/* 1027 */       if ((cc.getMethodInfo2().getDescriptor().equals(desc)) && (cc.isConstructor()))
/*      */       {
/* 1029 */         return cc;
/*      */       }
/*      */     }
/* 1032 */     return super.getConstructor(desc);
/*      */   }
/*      */ 
/*      */   public CtConstructor[] getDeclaredConstructors() {
/* 1036 */     CtMember.Cache memCache = getMembers();
/* 1037 */     CtMember cons = memCache.consHead();
/* 1038 */     CtMember consTail = memCache.lastCons();
/*      */ 
/* 1040 */     int n = 0;
/* 1041 */     CtMember mem = cons;
/* 1042 */     while (mem != consTail) {
/* 1043 */       mem = mem.next();
/* 1044 */       CtConstructor cc = (CtConstructor)mem;
/* 1045 */       if (cc.isConstructor()) {
/* 1046 */         n++;
/*      */       }
/*      */     }
/* 1049 */     CtConstructor[] result = new CtConstructor[n];
/* 1050 */     int i = 0;
/* 1051 */     mem = cons;
/* 1052 */     while (mem != consTail) {
/* 1053 */       mem = mem.next();
/* 1054 */       CtConstructor cc = (CtConstructor)mem;
/* 1055 */       if (cc.isConstructor()) {
/* 1056 */         result[(i++)] = cc;
/*      */       }
/*      */     }
/* 1059 */     return result;
/*      */   }
/*      */ 
/*      */   public CtConstructor getClassInitializer() {
/* 1063 */     CtMember.Cache memCache = getMembers();
/* 1064 */     CtMember cons = memCache.consHead();
/* 1065 */     CtMember consTail = memCache.lastCons();
/*      */ 
/* 1067 */     while (cons != consTail) {
/* 1068 */       cons = cons.next();
/* 1069 */       CtConstructor cc = (CtConstructor)cons;
/* 1070 */       if (cc.isClassInitializer()) {
/* 1071 */         return cc;
/*      */       }
/*      */     }
/* 1074 */     return null;
/*      */   }
/*      */ 
/*      */   public CtMethod[] getMethods() {
/* 1078 */     HashMap h = new HashMap();
/* 1079 */     getMethods0(h, this);
/* 1080 */     return (CtMethod[])h.values().toArray(new CtMethod[h.size()]);
/*      */   }
/*      */ 
/*      */   private static void getMethods0(HashMap h, CtClass cc) {
/*      */     try {
/* 1085 */       CtClass[] ifs = cc.getInterfaces();
/* 1086 */       int size = ifs.length;
/* 1087 */       for (int i = 0; i < size; i++)
/* 1088 */         getMethods0(h, ifs[i]);
/*      */     }
/*      */     catch (NotFoundException e) {
/*      */     }
/*      */     try {
/* 1093 */       CtClass s = cc.getSuperclass();
/* 1094 */       if (s != null)
/* 1095 */         getMethods0(h, s);
/*      */     }
/*      */     catch (NotFoundException e) {
/*      */     }
/* 1099 */     if ((cc instanceof CtClassType)) {
/* 1100 */       CtMember.Cache memCache = ((CtClassType)cc).getMembers();
/* 1101 */       CtMember mth = memCache.methodHead();
/* 1102 */       CtMember mthTail = memCache.lastMethod();
/*      */ 
/* 1104 */       while (mth != mthTail) {
/* 1105 */         mth = mth.next();
/* 1106 */         if (!Modifier.isPrivate(mth.getModifiers()))
/* 1107 */           h.put(((CtMethod)mth).getStringRep(), mth);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public CtMethod getMethod(String name, String desc)
/*      */     throws NotFoundException
/*      */   {
/* 1115 */     CtMethod m = getMethod0(this, name, desc);
/* 1116 */     if (m != null) {
/* 1117 */       return m;
/*      */     }
/* 1119 */     throw new NotFoundException(name + "(..) is not found in " + getName());
/*      */   }
/*      */ 
/*      */   private static CtMethod getMethod0(CtClass cc, String name, String desc)
/*      */   {
/* 1125 */     if ((cc instanceof CtClassType)) {
/* 1126 */       CtMember.Cache memCache = ((CtClassType)cc).getMembers();
/* 1127 */       CtMember mth = memCache.methodHead();
/* 1128 */       CtMember mthTail = memCache.lastMethod();
/*      */ 
/* 1130 */       while (mth != mthTail) {
/* 1131 */         mth = mth.next();
/* 1132 */         if ((mth.getName().equals(name)) && (((CtMethod)mth).getMethodInfo2().getDescriptor().equals(desc)))
/*      */         {
/* 1134 */           return (CtMethod)mth;
/*      */         }
/*      */       }
/*      */     }
/*      */     try {
/* 1139 */       CtClass s = cc.getSuperclass();
/* 1140 */       if (s != null) {
/* 1141 */         CtMethod m = getMethod0(s, name, desc);
/* 1142 */         if (m != null)
/* 1143 */           return m;
/*      */       }
/*      */     }
/*      */     catch (NotFoundException e) {
/*      */     }
/*      */     try {
/* 1149 */       CtClass[] ifs = cc.getInterfaces();
/* 1150 */       int size = ifs.length;
/* 1151 */       for (int i = 0; i < size; i++) {
/* 1152 */         CtMethod m = getMethod0(ifs[i], name, desc);
/* 1153 */         if (m != null)
/* 1154 */           return m;
/*      */       }
/*      */     } catch (NotFoundException e) {
/*      */     }
/* 1158 */     return null;
/*      */   }
/*      */ 
/*      */   public CtMethod[] getDeclaredMethods() {
/* 1162 */     CtMember.Cache memCache = getMembers();
/* 1163 */     CtMember mth = memCache.methodHead();
/* 1164 */     CtMember mthTail = memCache.lastMethod();
/* 1165 */     int num = CtMember.Cache.count(mth, mthTail);
/* 1166 */     CtMethod[] cms = new CtMethod[num];
/* 1167 */     int i = 0;
/* 1168 */     while (mth != mthTail) {
/* 1169 */       mth = mth.next();
/* 1170 */       cms[(i++)] = ((CtMethod)mth);
/*      */     }
/*      */ 
/* 1173 */     return cms;
/*      */   }
/*      */ 
/*      */   public CtMethod getDeclaredMethod(String name) throws NotFoundException {
/* 1177 */     CtMember.Cache memCache = getMembers();
/* 1178 */     CtMember mth = memCache.methodHead();
/* 1179 */     CtMember mthTail = memCache.lastMethod();
/* 1180 */     while (mth != mthTail) {
/* 1181 */       mth = mth.next();
/* 1182 */       if (mth.getName().equals(name)) {
/* 1183 */         return (CtMethod)mth;
/*      */       }
/*      */     }
/* 1186 */     throw new NotFoundException(name + "(..) is not found in " + getName());
/*      */   }
/*      */ 
/*      */   public CtMethod getDeclaredMethod(String name, CtClass[] params)
/*      */     throws NotFoundException
/*      */   {
/* 1193 */     String desc = Descriptor.ofParameters(params);
/* 1194 */     CtMember.Cache memCache = getMembers();
/* 1195 */     CtMember mth = memCache.methodHead();
/* 1196 */     CtMember mthTail = memCache.lastMethod();
/*      */ 
/* 1198 */     while (mth != mthTail) {
/* 1199 */       mth = mth.next();
/* 1200 */       if ((mth.getName().equals(name)) && (((CtMethod)mth).getMethodInfo2().getDescriptor().startsWith(desc)))
/*      */       {
/* 1202 */         return (CtMethod)mth;
/*      */       }
/*      */     }
/* 1205 */     throw new NotFoundException(name + "(..) is not found in " + getName());
/*      */   }
/*      */ 
/*      */   public void addField(CtField f, String init)
/*      */     throws CannotCompileException
/*      */   {
/* 1212 */     addField(f, CtField.Initializer.byExpr(init));
/*      */   }
/*      */ 
/*      */   public void addField(CtField f, CtField.Initializer init)
/*      */     throws CannotCompileException
/*      */   {
/* 1218 */     checkModify();
/* 1219 */     if (f.getDeclaringClass() != this) {
/* 1220 */       throw new CannotCompileException("cannot add");
/*      */     }
/* 1222 */     if (init == null) {
/* 1223 */       init = f.getInit();
/*      */     }
/* 1225 */     if (init != null) {
/* 1226 */       init.check(f.getSignature());
/* 1227 */       int mod = f.getModifiers();
/* 1228 */       if ((Modifier.isStatic(mod)) && (Modifier.isFinal(mod)))
/*      */         try {
/* 1230 */           ConstPool cp = getClassFile2().getConstPool();
/* 1231 */           int index = init.getConstantValue(cp, f.getType());
/* 1232 */           if (index != 0) {
/* 1233 */             f.getFieldInfo2().addAttribute(new ConstantAttribute(cp, index));
/* 1234 */             init = null;
/*      */           }
/*      */         }
/*      */         catch (NotFoundException e) {
/*      */         }
/*      */     }
/* 1240 */     getMembers().addField(f);
/* 1241 */     getClassFile2().addField(f.getFieldInfo2());
/*      */ 
/* 1243 */     if (init != null) {
/* 1244 */       FieldInitLink fil = new FieldInitLink(f, init);
/* 1245 */       FieldInitLink link = this.fieldInitializers;
/* 1246 */       if (link == null) {
/* 1247 */         this.fieldInitializers = fil;
/*      */       } else {
/* 1249 */         while (link.next != null) {
/* 1250 */           link = link.next;
/*      */         }
/* 1252 */         link.next = fil;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeField(CtField f) throws NotFoundException {
/* 1258 */     checkModify();
/* 1259 */     FieldInfo fi = f.getFieldInfo2();
/* 1260 */     ClassFile cf = getClassFile2();
/* 1261 */     if (cf.getFields().remove(fi)) {
/* 1262 */       getMembers().remove(f);
/* 1263 */       this.gcConstPool = true;
/*      */     }
/*      */     else {
/* 1266 */       throw new NotFoundException(f.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public CtConstructor makeClassInitializer() throws CannotCompileException
/*      */   {
/* 1272 */     CtConstructor clinit = getClassInitializer();
/* 1273 */     if (clinit != null) {
/* 1274 */       return clinit;
/*      */     }
/* 1276 */     checkModify();
/* 1277 */     ClassFile cf = getClassFile2();
/* 1278 */     Bytecode code = new Bytecode(cf.getConstPool(), 0, 0);
/* 1279 */     modifyClassConstructor(cf, code, 0, 0);
/* 1280 */     return getClassInitializer();
/*      */   }
/*      */ 
/*      */   public void addConstructor(CtConstructor c)
/*      */     throws CannotCompileException
/*      */   {
/* 1286 */     checkModify();
/* 1287 */     if (c.getDeclaringClass() != this) {
/* 1288 */       throw new CannotCompileException("cannot add");
/*      */     }
/* 1290 */     getMembers().addConstructor(c);
/* 1291 */     getClassFile2().addMethod(c.getMethodInfo2());
/*      */   }
/*      */ 
/*      */   public void removeConstructor(CtConstructor m) throws NotFoundException {
/* 1295 */     checkModify();
/* 1296 */     MethodInfo mi = m.getMethodInfo2();
/* 1297 */     ClassFile cf = getClassFile2();
/* 1298 */     if (cf.getMethods().remove(mi)) {
/* 1299 */       getMembers().remove(m);
/* 1300 */       this.gcConstPool = true;
/*      */     }
/*      */     else {
/* 1303 */       throw new NotFoundException(m.toString());
/*      */     }
/*      */   }
/*      */ 
/* 1307 */   public void addMethod(CtMethod m) throws CannotCompileException { checkModify();
/* 1308 */     if (m.getDeclaringClass() != this) {
/* 1309 */       throw new CannotCompileException("bad declaring class");
/*      */     }
/* 1311 */     int mod = m.getModifiers();
/* 1312 */     if ((getModifiers() & 0x200) != 0) {
/* 1313 */       m.setModifiers(mod | 0x1);
/* 1314 */       if ((mod & 0x400) == 0) {
/* 1315 */         throw new CannotCompileException("an interface method must be abstract: " + m.toString());
/*      */       }
/*      */     }
/*      */ 
/* 1319 */     getMembers().addMethod(m);
/* 1320 */     getClassFile2().addMethod(m.getMethodInfo2());
/* 1321 */     if ((mod & 0x400) != 0)
/* 1322 */       setModifiers(getModifiers() | 0x400); }
/*      */ 
/*      */   public void removeMethod(CtMethod m) throws NotFoundException
/*      */   {
/* 1326 */     checkModify();
/* 1327 */     MethodInfo mi = m.getMethodInfo2();
/* 1328 */     ClassFile cf = getClassFile2();
/* 1329 */     if (cf.getMethods().remove(mi)) {
/* 1330 */       getMembers().remove(m);
/* 1331 */       this.gcConstPool = true;
/*      */     }
/*      */     else {
/* 1334 */       throw new NotFoundException(m.toString());
/*      */     }
/*      */   }
/*      */ 
/* 1338 */   public byte[] getAttribute(String name) { AttributeInfo ai = getClassFile2().getAttribute(name);
/* 1339 */     if (ai == null) {
/* 1340 */       return null;
/*      */     }
/* 1342 */     return ai.get(); }
/*      */ 
/*      */   public void setAttribute(String name, byte[] data)
/*      */   {
/* 1346 */     checkModify();
/* 1347 */     ClassFile cf = getClassFile2();
/* 1348 */     cf.addAttribute(new AttributeInfo(cf.getConstPool(), name, data));
/*      */   }
/*      */ 
/*      */   public void instrument(CodeConverter converter)
/*      */     throws CannotCompileException
/*      */   {
/* 1354 */     checkModify();
/* 1355 */     ClassFile cf = getClassFile2();
/* 1356 */     ConstPool cp = cf.getConstPool();
/* 1357 */     List list = cf.getMethods();
/* 1358 */     int n = list.size();
/* 1359 */     for (int i = 0; i < n; i++) {
/* 1360 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 1361 */       converter.doit(this, minfo, cp);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void instrument(ExprEditor editor)
/*      */     throws CannotCompileException
/*      */   {
/* 1368 */     checkModify();
/* 1369 */     ClassFile cf = getClassFile2();
/* 1370 */     List list = cf.getMethods();
/* 1371 */     int n = list.size();
/* 1372 */     for (int i = 0; i < n; i++) {
/* 1373 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 1374 */       editor.doit(this, minfo);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void prune()
/*      */   {
/* 1383 */     if (this.wasPruned) {
/* 1384 */       return;
/*      */     }
/* 1386 */     this.wasPruned = (this.wasFrozen = 1);
/* 1387 */     getClassFile2().prune();
/*      */   }
/*      */   public void rebuildClassFile() {
/* 1390 */     this.gcConstPool = true;
/*      */   }
/*      */ 
/*      */   public void toBytecode(DataOutputStream out) throws CannotCompileException, IOException
/*      */   {
/*      */     try {
/* 1396 */       if (isModified()) {
/* 1397 */         checkPruned("toBytecode");
/* 1398 */         ClassFile cf = getClassFile2();
/* 1399 */         if (this.gcConstPool) {
/* 1400 */           cf.compact();
/* 1401 */           this.gcConstPool = false;
/*      */         }
/*      */ 
/* 1404 */         modifyClassConstructor(cf);
/* 1405 */         modifyConstructors(cf);
/* 1406 */         cf.write(out);
/* 1407 */         out.flush();
/* 1408 */         this.fieldInitializers = null;
/* 1409 */         if (this.doPruning)
/*      */         {
/* 1411 */           cf.prune();
/* 1412 */           this.wasPruned = true;
/*      */         }
/*      */       }
/*      */       else {
/* 1416 */         this.classPool.writeClassfile(getName(), out);
/*      */       }
/*      */ 
/* 1421 */       this.getCount = 0;
/* 1422 */       this.wasFrozen = true;
/*      */     }
/*      */     catch (NotFoundException e) {
/* 1425 */       throw new CannotCompileException(e);
/*      */     }
/*      */     catch (IOException e) {
/* 1428 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkPruned(String method)
/*      */   {
/* 1435 */     if (this.wasPruned)
/* 1436 */       throw new RuntimeException(method + "(): " + getName() + " was pruned.");
/*      */   }
/*      */ 
/*      */   public boolean stopPruning(boolean stop)
/*      */   {
/* 1441 */     boolean prev = !this.doPruning;
/* 1442 */     this.doPruning = (!stop);
/* 1443 */     return prev;
/*      */   }
/*      */ 
/*      */   private void modifyClassConstructor(ClassFile cf)
/*      */     throws CannotCompileException, NotFoundException
/*      */   {
/* 1449 */     if (this.fieldInitializers == null) {
/* 1450 */       return;
/*      */     }
/* 1452 */     Bytecode code = new Bytecode(cf.getConstPool(), 0, 0);
/* 1453 */     Javac jv = new Javac(code, this);
/* 1454 */     int stacksize = 0;
/* 1455 */     boolean doInit = false;
/* 1456 */     for (FieldInitLink fi = this.fieldInitializers; fi != null; fi = fi.next) {
/* 1457 */       CtField f = fi.field;
/* 1458 */       if (Modifier.isStatic(f.getModifiers())) {
/* 1459 */         doInit = true;
/* 1460 */         int s = fi.init.compileIfStatic(f.getType(), f.getName(), code, jv);
/*      */ 
/* 1462 */         if (stacksize < s) {
/* 1463 */           stacksize = s;
/*      */         }
/*      */       }
/*      */     }
/* 1467 */     if (doInit)
/* 1468 */       modifyClassConstructor(cf, code, stacksize, 0);
/*      */   }
/*      */ 
/*      */   private void modifyClassConstructor(ClassFile cf, Bytecode code, int stacksize, int localsize)
/*      */     throws CannotCompileException
/*      */   {
/* 1475 */     MethodInfo m = cf.getStaticInitializer();
/* 1476 */     if (m == null) {
/* 1477 */       code.add(177);
/* 1478 */       code.setMaxStack(stacksize);
/* 1479 */       code.setMaxLocals(localsize);
/* 1480 */       m = new MethodInfo(cf.getConstPool(), "<clinit>", "()V");
/* 1481 */       m.setAccessFlags(8);
/* 1482 */       m.setCodeAttribute(code.toCodeAttribute());
/* 1483 */       cf.addMethod(m);
/* 1484 */       CtMember.Cache cache = hasMemberCache();
/* 1485 */       if (cache != null)
/* 1486 */         cache.addConstructor(new CtConstructor(m, this));
/*      */     }
/*      */     else {
/* 1489 */       CodeAttribute codeAttr = m.getCodeAttribute();
/* 1490 */       if (codeAttr == null)
/* 1491 */         throw new CannotCompileException("empty <clinit>");
/*      */       try
/*      */       {
/* 1494 */         CodeIterator it = codeAttr.iterator();
/* 1495 */         int pos = it.insertEx(code.get());
/* 1496 */         it.insert(code.getExceptionTable(), pos);
/* 1497 */         int maxstack = codeAttr.getMaxStack();
/* 1498 */         if (maxstack < stacksize) {
/* 1499 */           codeAttr.setMaxStack(stacksize);
/*      */         }
/* 1501 */         int maxlocals = codeAttr.getMaxLocals();
/* 1502 */         if (maxlocals < localsize)
/* 1503 */           codeAttr.setMaxLocals(localsize);
/*      */       }
/*      */       catch (BadBytecode e) {
/* 1506 */         throw new CannotCompileException(e);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1511 */       m.rebuildStackMapIf6(this.classPool, cf);
/*      */     }
/*      */     catch (BadBytecode e) {
/* 1514 */       throw new CannotCompileException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void modifyConstructors(ClassFile cf)
/*      */     throws CannotCompileException, NotFoundException
/*      */   {
/* 1521 */     if (this.fieldInitializers == null) {
/* 1522 */       return;
/*      */     }
/* 1524 */     ConstPool cp = cf.getConstPool();
/* 1525 */     List list = cf.getMethods();
/* 1526 */     int n = list.size();
/* 1527 */     for (int i = 0; i < n; i++) {
/* 1528 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 1529 */       if (minfo.isConstructor()) {
/* 1530 */         CodeAttribute codeAttr = minfo.getCodeAttribute();
/* 1531 */         if (codeAttr != null)
/*      */           try {
/* 1533 */             Bytecode init = new Bytecode(cp, 0, codeAttr.getMaxLocals());
/*      */ 
/* 1535 */             CtClass[] params = Descriptor.getParameterTypes(minfo.getDescriptor(), this.classPool);
/*      */ 
/* 1539 */             int stacksize = makeFieldInitializer(init, params);
/* 1540 */             insertAuxInitializer(codeAttr, init, stacksize);
/* 1541 */             minfo.rebuildStackMapIf6(this.classPool, cf);
/*      */           }
/*      */           catch (BadBytecode e) {
/* 1544 */             throw new CannotCompileException(e);
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void insertAuxInitializer(CodeAttribute codeAttr, Bytecode initializer, int stacksize)
/*      */     throws BadBytecode
/*      */   {
/* 1555 */     CodeIterator it = codeAttr.iterator();
/* 1556 */     int index = it.skipSuperConstructor();
/* 1557 */     if (index < 0) {
/* 1558 */       index = it.skipThisConstructor();
/* 1559 */       if (index >= 0) {
/* 1560 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1565 */     int pos = it.insertEx(initializer.get());
/* 1566 */     it.insert(initializer.getExceptionTable(), pos);
/* 1567 */     int maxstack = codeAttr.getMaxStack();
/* 1568 */     if (maxstack < stacksize)
/* 1569 */       codeAttr.setMaxStack(stacksize);
/*      */   }
/*      */ 
/*      */   private int makeFieldInitializer(Bytecode code, CtClass[] parameters)
/*      */     throws CannotCompileException, NotFoundException
/*      */   {
/* 1575 */     int stacksize = 0;
/* 1576 */     Javac jv = new Javac(code, this);
/*      */     try {
/* 1578 */       jv.recordParams(parameters, false);
/*      */     }
/*      */     catch (CompileError e) {
/* 1581 */       throw new CannotCompileException(e);
/*      */     }
/*      */ 
/* 1584 */     for (FieldInitLink fi = this.fieldInitializers; fi != null; fi = fi.next) {
/* 1585 */       CtField f = fi.field;
/* 1586 */       if (!Modifier.isStatic(f.getModifiers())) {
/* 1587 */         int s = fi.init.compile(f.getType(), f.getName(), code, parameters, jv);
/*      */ 
/* 1589 */         if (stacksize < s) {
/* 1590 */           stacksize = s;
/*      */         }
/*      */       }
/*      */     }
/* 1594 */     return stacksize;
/*      */   }
/*      */ 
/*      */   Hashtable getHiddenMethods()
/*      */   {
/* 1600 */     if (this.hiddenMethods == null) {
/* 1601 */       this.hiddenMethods = new Hashtable();
/*      */     }
/* 1603 */     return this.hiddenMethods;
/*      */   }
/*      */   int getUniqueNumber() {
/* 1606 */     return this.uniqueNumberSeed++;
/*      */   }
/*      */   public String makeUniqueName(String prefix) {
/* 1609 */     HashMap table = new HashMap();
/* 1610 */     makeMemberList(table);
/* 1611 */     Set keys = table.keySet();
/* 1612 */     String[] methods = new String[keys.size()];
/* 1613 */     keys.toArray(methods);
/*      */ 
/* 1615 */     if (notFindInArray(prefix, methods)) {
/* 1616 */       return prefix;
/* 1618 */     }int i = 100;
/*      */     String name;
/*      */     do {
/* 1621 */       if (i > 999) {
/* 1622 */         throw new RuntimeException("too many unique name");
/*      */       }
/* 1624 */       name = prefix + i++;
/* 1625 */     }while (!notFindInArray(name, methods));
/* 1626 */     return name;
/*      */   }
/*      */ 
/*      */   private static boolean notFindInArray(String prefix, String[] values) {
/* 1630 */     int len = values.length;
/* 1631 */     for (int i = 0; i < len; i++) {
/* 1632 */       if (values[i].startsWith(prefix))
/* 1633 */         return false;
/*      */     }
/* 1635 */     return true;
/*      */   }
/*      */ 
/*      */   private void makeMemberList(HashMap table) {
/* 1639 */     int mod = getModifiers();
/* 1640 */     if ((Modifier.isAbstract(mod)) || (Modifier.isInterface(mod)))
/*      */       try {
/* 1642 */         CtClass[] ifs = getInterfaces();
/* 1643 */         int size = ifs.length;
/* 1644 */         for (int i = 0; i < size; i++) {
/* 1645 */           CtClass ic = ifs[i];
/* 1646 */           if ((ic != null) && ((ic instanceof CtClassType)))
/* 1647 */             ((CtClassType)ic).makeMemberList(table);
/*      */         }
/*      */       }
/*      */       catch (NotFoundException e) {
/*      */       }
/*      */     try {
/* 1653 */       CtClass s = getSuperclass();
/* 1654 */       if ((s != null) && ((s instanceof CtClassType)))
/* 1655 */         ((CtClassType)s).makeMemberList(table);
/*      */     }
/*      */     catch (NotFoundException e) {
/*      */     }
/* 1659 */     List list = getClassFile2().getMethods();
/* 1660 */     int n = list.size();
/* 1661 */     for (int i = 0; i < n; i++) {
/* 1662 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 1663 */       table.put(minfo.getName(), this);
/*      */     }
/*      */ 
/* 1666 */     list = getClassFile2().getFields();
/* 1667 */     n = list.size();
/* 1668 */     for (int i = 0; i < n; i++) {
/* 1669 */       FieldInfo finfo = (FieldInfo)list.get(i);
/* 1670 */       table.put(finfo.getName(), this);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtClassType
 * JD-Core Version:    0.6.2
 */