/*      */ package com.newrelic.javassist.util.proxy;
/*      */ 
/*      */ import com.newrelic.javassist.CannotCompileException;
/*      */ import com.newrelic.javassist.bytecode.Bytecode;
/*      */ import com.newrelic.javassist.bytecode.ClassFile;
/*      */ import com.newrelic.javassist.bytecode.CodeAttribute;
/*      */ import com.newrelic.javassist.bytecode.ConstPool;
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import com.newrelic.javassist.bytecode.DuplicateMemberException;
/*      */ import com.newrelic.javassist.bytecode.ExceptionsAttribute;
/*      */ import com.newrelic.javassist.bytecode.FieldInfo;
/*      */ import com.newrelic.javassist.bytecode.MethodInfo;
/*      */ import com.newrelic.javassist.bytecode.StackMapTable.Writer;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Member;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.WeakHashMap;
/*      */ 
/*      */ public class ProxyFactory
/*      */ {
/*      */   private Class superClass;
/*      */   private Class[] interfaces;
/*      */   private MethodFilter methodFilter;
/*      */   private MethodHandler handler;
/*      */   private List signatureMethods;
/*      */   private byte[] signature;
/*      */   private String classname;
/*      */   private String basename;
/*      */   private String superName;
/*      */   private Class thisClass;
/*      */   private boolean factoryUseCache;
/*      */   private boolean factoryWriteReplace;
/*      */   public String writeDirectory;
/*  184 */   private static final Class OBJECT_TYPE = Object.class;
/*      */   private static final String HOLDER = "_methods_";
/*      */   private static final String HOLDER_TYPE = "[Ljava/lang/reflect/Method;";
/*      */   private static final String FILTER_SIGNATURE_FIELD = "_filter_signature";
/*      */   private static final String FILTER_SIGNATURE_TYPE = "[B";
/*      */   private static final String HANDLER = "handler";
/*      */   private static final String NULL_INTERCEPTOR_HOLDER = "com.newrelic.javassist.util.proxy.RuntimeSupport";
/*      */   private static final String DEFAULT_INTERCEPTOR = "default_interceptor";
/*  193 */   private static final String HANDLER_TYPE = 'L' + MethodHandler.class.getName().replace('.', '/') + ';';
/*      */   private static final String HANDLER_SETTER = "setHandler";
/*  196 */   private static final String HANDLER_SETTER_TYPE = "(" + HANDLER_TYPE + ")V";
/*      */   private static final String HANDLER_GETTER = "getHandler";
/*  199 */   private static final String HANDLER_GETTER_TYPE = "()" + HANDLER_TYPE;
/*      */   private static final String SERIAL_VERSION_UID_FIELD = "serialVersionUID";
/*      */   private static final String SERIAL_VERSION_UID_TYPE = "J";
/*      */   private static final int SERIAL_VERSION_UID_VALUE = -1;
/*  216 */   public static volatile boolean useCache = true;
/*      */ 
/*  237 */   public static volatile boolean useWriteReplace = true;
/*      */ 
/*  286 */   private static WeakHashMap proxyCache = new WeakHashMap();
/*      */ 
/*  435 */   private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*      */ 
/*  579 */   public static ClassLoaderProvider classLoaderProvider = new ClassLoaderProvider()
/*      */   {
/*      */     public ClassLoader get(ProxyFactory pf) {
/*  582 */       return pf.getClassLoader0();
/*      */     }
/*  579 */   };
/*      */ 
/*  677 */   private static int counter = 0;
/*      */ 
/*  759 */   private static Comparator sorter = new Comparator()
/*      */   {
/*      */     public int compare(Object o1, Object o2) {
/*  762 */       Map.Entry e1 = (Map.Entry)o1;
/*  763 */       Map.Entry e2 = (Map.Entry)o2;
/*  764 */       String key1 = (String)e1.getKey();
/*  765 */       String key2 = (String)e2.getKey();
/*  766 */       return key1.compareTo(key2);
/*      */     }
/*  759 */   };
/*      */ 
/*      */   public boolean isUseCache()
/*      */   {
/*  249 */     return this.factoryUseCache;
/*      */   }
/*      */ 
/*      */   public void setUseCache(boolean useCache)
/*      */   {
/*  261 */     if ((this.handler != null) && (useCache)) {
/*  262 */       throw new RuntimeException("caching cannot be enabled if the factory default interceptor has been set");
/*      */     }
/*  264 */     this.factoryUseCache = useCache;
/*      */   }
/*      */ 
/*      */   public boolean isUseWriteReplace()
/*      */   {
/*  273 */     return this.factoryWriteReplace;
/*      */   }
/*      */ 
/*      */   public void setUseWriteReplace(boolean useWriteReplace)
/*      */   {
/*  283 */     this.factoryWriteReplace = useWriteReplace;
/*      */   }
/*      */ 
/*      */   public static boolean isProxyClass(Class cl)
/*      */   {
/*  296 */     return ProxyObject.class.isAssignableFrom(cl);
/*      */   }
/*      */ 
/*      */   public ProxyFactory()
/*      */   {
/*  334 */     this.superClass = null;
/*  335 */     this.interfaces = null;
/*  336 */     this.methodFilter = null;
/*  337 */     this.handler = null;
/*  338 */     this.signature = null;
/*  339 */     this.signatureMethods = null;
/*  340 */     this.thisClass = null;
/*  341 */     this.writeDirectory = null;
/*  342 */     this.factoryUseCache = useCache;
/*  343 */     this.factoryWriteReplace = useWriteReplace;
/*      */   }
/*      */ 
/*      */   public void setSuperclass(Class clazz)
/*      */   {
/*  350 */     this.superClass = clazz;
/*      */ 
/*  352 */     this.signature = null;
/*      */   }
/*      */ 
/*      */   public Class getSuperclass()
/*      */   {
/*  360 */     return this.superClass;
/*      */   }
/*      */ 
/*      */   public void setInterfaces(Class[] ifs)
/*      */   {
/*  366 */     this.interfaces = ifs;
/*      */ 
/*  368 */     this.signature = null;
/*      */   }
/*      */ 
/*      */   public Class[] getInterfaces()
/*      */   {
/*  376 */     return this.interfaces;
/*      */   }
/*      */ 
/*      */   public void setFilter(MethodFilter mf)
/*      */   {
/*  382 */     this.methodFilter = mf;
/*      */ 
/*  384 */     this.signature = null;
/*      */   }
/*      */ 
/*      */   public Class createClass()
/*      */   {
/*  391 */     if (this.signature == null) {
/*  392 */       computeSignature(this.methodFilter);
/*      */     }
/*  394 */     return createClass1();
/*      */   }
/*      */ 
/*      */   public Class createClass(MethodFilter filter)
/*      */   {
/*  401 */     computeSignature(filter);
/*  402 */     return createClass1();
/*      */   }
/*      */ 
/*      */   Class createClass(byte[] signature)
/*      */   {
/*  413 */     installSignature(signature);
/*  414 */     return createClass1();
/*      */   }
/*      */ 
/*      */   private Class createClass1() {
/*  418 */     if (this.thisClass == null) {
/*  419 */       ClassLoader cl = getClassLoader();
/*  420 */       synchronized (proxyCache) {
/*  421 */         if (this.factoryUseCache)
/*  422 */           createClass2(cl);
/*      */         else {
/*  424 */           createClass3(cl);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  429 */     Class result = this.thisClass;
/*  430 */     this.thisClass = null;
/*      */ 
/*  432 */     return result;
/*      */   }
/*      */ 
/*      */   public String getKey(Class superClass, Class[] interfaces, byte[] signature, boolean useWriteReplace)
/*      */   {
/*  441 */     StringBuffer sbuf = new StringBuffer();
/*  442 */     if (superClass != null) {
/*  443 */       sbuf.append(superClass.getName());
/*      */     }
/*  445 */     sbuf.append(":");
/*  446 */     for (int i = 0; i < interfaces.length; i++) {
/*  447 */       sbuf.append(interfaces[i].getName());
/*  448 */       sbuf.append(":");
/*      */     }
/*  450 */     for (int i = 0; i < signature.length; i++) {
/*  451 */       byte b = signature[i];
/*  452 */       int lo = b & 0xF;
/*  453 */       int hi = b >> 4 & 0xF;
/*  454 */       sbuf.append(hexDigits[lo]);
/*  455 */       sbuf.append(hexDigits[hi]);
/*      */     }
/*  457 */     if (useWriteReplace) {
/*  458 */       sbuf.append(":w");
/*      */     }
/*      */ 
/*  461 */     return sbuf.toString();
/*      */   }
/*      */ 
/*      */   private void createClass2(ClassLoader cl) {
/*  465 */     String key = getKey(this.superClass, this.interfaces, this.signature, this.factoryWriteReplace);
/*      */ 
/*  472 */     HashMap cacheForTheLoader = (HashMap)proxyCache.get(cl);
/*      */ 
/*  474 */     if (cacheForTheLoader == null) {
/*  475 */       cacheForTheLoader = new HashMap();
/*  476 */       proxyCache.put(cl, cacheForTheLoader);
/*      */     }
/*  478 */     ProxyDetails details = (ProxyDetails)cacheForTheLoader.get(key);
/*  479 */     if (details != null) {
/*  480 */       WeakReference reference = details.proxyClass;
/*  481 */       this.thisClass = ((Class)reference.get());
/*  482 */       if (this.thisClass != null) {
/*  483 */         return;
/*      */       }
/*      */     }
/*  486 */     createClass3(cl);
/*  487 */     details = new ProxyDetails(this.signature, this.thisClass, this.factoryWriteReplace);
/*  488 */     cacheForTheLoader.put(key, details);
/*      */   }
/*      */ 
/*      */   private void createClass3(ClassLoader cl)
/*      */   {
/*  494 */     allocateClassName();
/*      */     try
/*      */     {
/*  497 */       ClassFile cf = make();
/*  498 */       if (this.writeDirectory != null) {
/*  499 */         FactoryHelper.writeFile(cf, this.writeDirectory);
/*      */       }
/*  501 */       this.thisClass = FactoryHelper.toClass(cf, cl, getDomain());
/*  502 */       setField("_filter_signature", this.signature);
/*      */ 
/*  504 */       if (!this.factoryUseCache)
/*  505 */         setField("default_interceptor", this.handler);
/*      */     }
/*      */     catch (CannotCompileException e)
/*      */     {
/*  509 */       throw new RuntimeException(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setField(String fieldName, Object value)
/*      */   {
/*  515 */     if ((this.thisClass != null) && (value != null))
/*      */       try {
/*  517 */         Field f = this.thisClass.getField(fieldName);
/*  518 */         SecurityActions.setAccessible(f, true);
/*  519 */         f.set(null, value);
/*  520 */         SecurityActions.setAccessible(f, false);
/*      */       }
/*      */       catch (Exception e) {
/*  523 */         throw new RuntimeException(e);
/*      */       }
/*      */   }
/*      */ 
/*      */   static byte[] getFilterSignature(Class clazz) {
/*  528 */     return (byte[])getField(clazz, "_filter_signature");
/*      */   }
/*      */ 
/*      */   private static Object getField(Class clazz, String fieldName) {
/*      */     try {
/*  533 */       Field f = clazz.getField(fieldName);
/*  534 */       f.setAccessible(true);
/*  535 */       Object value = f.get(null);
/*  536 */       f.setAccessible(false);
/*  537 */       return value;
/*      */     }
/*      */     catch (Exception e) {
/*  540 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ClassLoader getClassLoader()
/*      */   {
/*  587 */     return classLoaderProvider.get(this);
/*      */   }
/*      */ 
/*      */   protected ClassLoader getClassLoader0() {
/*  591 */     ClassLoader loader = null;
/*  592 */     if ((this.superClass != null) && (!this.superClass.getName().equals("java.lang.Object")))
/*  593 */       loader = this.superClass.getClassLoader();
/*  594 */     else if ((this.interfaces != null) && (this.interfaces.length > 0)) {
/*  595 */       loader = this.interfaces[0].getClassLoader();
/*      */     }
/*  597 */     if (loader == null) {
/*  598 */       loader = getClass().getClassLoader();
/*      */ 
/*  600 */       if (loader == null) {
/*  601 */         loader = Thread.currentThread().getContextClassLoader();
/*  602 */         if (loader == null) {
/*  603 */           loader = ClassLoader.getSystemClassLoader();
/*      */         }
/*      */       }
/*      */     }
/*  607 */     return loader;
/*      */   }
/*      */ 
/*      */   protected ProtectionDomain getDomain()
/*      */   {
/*      */     Class clazz;
/*      */     Class clazz;
/*  612 */     if ((this.superClass != null) && (!this.superClass.getName().equals("java.lang.Object"))) {
/*  613 */       clazz = this.superClass;
/*      */     }
/*      */     else
/*      */     {
/*      */       Class clazz;
/*  614 */       if ((this.interfaces != null) && (this.interfaces.length > 0))
/*  615 */         clazz = this.interfaces[0];
/*      */       else
/*  617 */         clazz = getClass();
/*      */     }
/*  619 */     return clazz.getProtectionDomain();
/*      */   }
/*      */ 
/*      */   public Object create(Class[] paramTypes, Object[] args, MethodHandler mh)
/*      */     throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  634 */     Object obj = create(paramTypes, args);
/*  635 */     ((ProxyObject)obj).setHandler(mh);
/*  636 */     return obj;
/*      */   }
/*      */ 
/*      */   public Object create(Class[] paramTypes, Object[] args)
/*      */     throws NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
/*      */   {
/*  649 */     Class c = createClass();
/*  650 */     Constructor cons = c.getConstructor(paramTypes);
/*  651 */     return cons.newInstance(args);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setHandler(MethodHandler mi)
/*      */   {
/*  666 */     if ((this.factoryUseCache) && (mi != null)) {
/*  667 */       this.factoryUseCache = false;
/*      */ 
/*  669 */       this.thisClass = null;
/*      */     }
/*  671 */     this.handler = mi;
/*      */ 
/*  674 */     setField("default_interceptor", this.handler);
/*      */   }
/*      */ 
/*      */   private static synchronized String makeProxyName(String classname)
/*      */   {
/*  680 */     return classname + "_$$_javassist_" + counter++;
/*      */   }
/*      */ 
/*      */   private ClassFile make() throws CannotCompileException {
/*  684 */     ClassFile cf = new ClassFile(false, this.classname, this.superName);
/*  685 */     cf.setAccessFlags(1);
/*  686 */     setInterfaces(cf, this.interfaces);
/*  687 */     ConstPool pool = cf.getConstPool();
/*      */ 
/*  690 */     if (!this.factoryUseCache) {
/*  691 */       FieldInfo finfo = new FieldInfo(pool, "default_interceptor", HANDLER_TYPE);
/*  692 */       finfo.setAccessFlags(9);
/*  693 */       cf.addField(finfo);
/*      */     }
/*      */ 
/*  697 */     FieldInfo finfo2 = new FieldInfo(pool, "handler", HANDLER_TYPE);
/*  698 */     finfo2.setAccessFlags(2);
/*  699 */     cf.addField(finfo2);
/*      */ 
/*  702 */     FieldInfo finfo3 = new FieldInfo(pool, "_filter_signature", "[B");
/*  703 */     finfo3.setAccessFlags(9);
/*  704 */     cf.addField(finfo3);
/*      */ 
/*  707 */     FieldInfo finfo4 = new FieldInfo(pool, "serialVersionUID", "J");
/*  708 */     finfo4.setAccessFlags(25);
/*  709 */     cf.addField(finfo4);
/*      */ 
/*  713 */     makeConstructors(this.classname, cf, pool, this.classname);
/*  714 */     int s = overrideMethods(cf, pool, this.classname);
/*  715 */     addMethodsHolder(cf, pool, this.classname, s);
/*  716 */     addSetter(this.classname, cf, pool);
/*  717 */     addGetter(this.classname, cf, pool);
/*      */ 
/*  719 */     if (this.factoryWriteReplace) {
/*      */       try {
/*  721 */         cf.addMethod(makeWriteReplace(pool));
/*      */       }
/*      */       catch (DuplicateMemberException e)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/*  728 */     this.thisClass = null;
/*  729 */     return cf;
/*      */   }
/*      */ 
/*      */   private void checkClassAndSuperName()
/*      */   {
/*  734 */     if (this.interfaces == null) {
/*  735 */       this.interfaces = new Class[0];
/*      */     }
/*  737 */     if (this.superClass == null) {
/*  738 */       this.superClass = OBJECT_TYPE;
/*  739 */       this.superName = this.superClass.getName();
/*  740 */       this.basename = (this.interfaces.length == 0 ? this.superName : this.interfaces[0].getName());
/*      */     }
/*      */     else {
/*  743 */       this.superName = this.superClass.getName();
/*  744 */       this.basename = this.superName;
/*      */     }
/*      */ 
/*  747 */     if (Modifier.isFinal(this.superClass.getModifiers())) {
/*  748 */       throw new RuntimeException(this.superName + " is final");
/*      */     }
/*  750 */     if (this.basename.startsWith("java."))
/*  751 */       this.basename = ("org.javassist.tmp." + this.basename);
/*      */   }
/*      */ 
/*      */   private void allocateClassName()
/*      */   {
/*  756 */     this.classname = makeProxyName(this.basename);
/*      */   }
/*      */ 
/*      */   private void makeSortedMethodList()
/*      */   {
/*  772 */     checkClassAndSuperName();
/*      */ 
/*  774 */     HashMap allMethods = getMethods(this.superClass, this.interfaces);
/*  775 */     this.signatureMethods = new ArrayList(allMethods.entrySet());
/*  776 */     Collections.sort(this.signatureMethods, sorter);
/*      */   }
/*      */ 
/*      */   private void computeSignature(MethodFilter filter)
/*      */   {
/*  781 */     makeSortedMethodList();
/*      */ 
/*  783 */     int l = this.signatureMethods.size();
/*  784 */     int maxBytes = l + 7 >> 3;
/*  785 */     this.signature = new byte[maxBytes];
/*  786 */     for (int idx = 0; idx < l; idx++)
/*      */     {
/*  788 */       Map.Entry e = (Map.Entry)this.signatureMethods.get(idx);
/*  789 */       Method m = (Method)e.getValue();
/*  790 */       int mod = m.getModifiers();
/*  791 */       if ((!Modifier.isFinal(mod)) && (!Modifier.isStatic(mod)) && (isVisible(mod, this.basename, m)) && ((filter == null) || (filter.isHandled(m))))
/*      */       {
/*  793 */         setBit(this.signature, idx);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void installSignature(byte[] signature)
/*      */   {
/*  800 */     makeSortedMethodList();
/*      */ 
/*  802 */     int l = this.signatureMethods.size();
/*  803 */     int maxBytes = l + 7 >> 3;
/*  804 */     if (signature.length != maxBytes) {
/*  805 */       throw new RuntimeException("invalid filter signature length for deserialized proxy class");
/*      */     }
/*      */ 
/*  808 */     this.signature = signature;
/*      */   }
/*      */ 
/*      */   private boolean testBit(byte[] signature, int idx)
/*      */   {
/*  813 */     int byteIdx = idx >> 3;
/*  814 */     if (byteIdx > signature.length) {
/*  815 */       return false;
/*      */     }
/*  817 */     int bitIdx = idx & 0x7;
/*  818 */     int mask = 1 << bitIdx;
/*  819 */     int sigByte = signature[byteIdx];
/*  820 */     return (sigByte & mask) != 0;
/*      */   }
/*      */ 
/*      */   private void setBit(byte[] signature, int idx)
/*      */   {
/*  826 */     int byteIdx = idx >> 3;
/*  827 */     if (byteIdx < signature.length) {
/*  828 */       int bitIdx = idx & 0x7;
/*  829 */       int mask = 1 << bitIdx;
/*  830 */       int sigByte = signature[byteIdx];
/*  831 */       signature[byteIdx] = ((byte)(sigByte | mask));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setInterfaces(ClassFile cf, Class[] interfaces) {
/*  836 */     String setterIntf = ProxyObject.class.getName();
/*      */     String[] list;
/*      */     String[] list;
/*  838 */     if ((interfaces == null) || (interfaces.length == 0)) {
/*  839 */       list = new String[] { setterIntf };
/*      */     } else {
/*  841 */       list = new String[interfaces.length + 1];
/*  842 */       for (int i = 0; i < interfaces.length; i++) {
/*  843 */         list[i] = interfaces[i].getName();
/*      */       }
/*  845 */       list[interfaces.length] = setterIntf;
/*      */     }
/*      */ 
/*  848 */     cf.setInterfaces(list);
/*      */   }
/*      */ 
/*      */   private static void addMethodsHolder(ClassFile cf, ConstPool cp, String classname, int size)
/*      */     throws CannotCompileException
/*      */   {
/*  855 */     FieldInfo finfo = new FieldInfo(cp, "_methods_", "[Ljava/lang/reflect/Method;");
/*  856 */     finfo.setAccessFlags(10);
/*  857 */     cf.addField(finfo);
/*  858 */     MethodInfo minfo = new MethodInfo(cp, "<clinit>", "()V");
/*  859 */     minfo.setAccessFlags(8);
/*  860 */     Bytecode code = new Bytecode(cp, 0, 0);
/*  861 */     code.addIconst(size * 2);
/*  862 */     code.addAnewarray("java.lang.reflect.Method");
/*  863 */     code.addPutstatic(classname, "_methods_", "[Ljava/lang/reflect/Method;");
/*      */ 
/*  865 */     code.addLconst(-1L);
/*  866 */     code.addPutstatic(classname, "serialVersionUID", "J");
/*  867 */     code.addOpcode(177);
/*  868 */     minfo.setCodeAttribute(code.toCodeAttribute());
/*  869 */     cf.addMethod(minfo);
/*      */   }
/*      */ 
/*      */   private static void addSetter(String classname, ClassFile cf, ConstPool cp)
/*      */     throws CannotCompileException
/*      */   {
/*  875 */     MethodInfo minfo = new MethodInfo(cp, "setHandler", HANDLER_SETTER_TYPE);
/*      */ 
/*  877 */     minfo.setAccessFlags(1);
/*  878 */     Bytecode code = new Bytecode(cp, 2, 2);
/*  879 */     code.addAload(0);
/*  880 */     code.addAload(1);
/*  881 */     code.addPutfield(classname, "handler", HANDLER_TYPE);
/*  882 */     code.addOpcode(177);
/*  883 */     minfo.setCodeAttribute(code.toCodeAttribute());
/*  884 */     cf.addMethod(minfo);
/*      */   }
/*      */ 
/*      */   private static void addGetter(String classname, ClassFile cf, ConstPool cp)
/*      */     throws CannotCompileException
/*      */   {
/*  890 */     MethodInfo minfo = new MethodInfo(cp, "getHandler", HANDLER_GETTER_TYPE);
/*      */ 
/*  892 */     minfo.setAccessFlags(1);
/*  893 */     Bytecode code = new Bytecode(cp, 1, 1);
/*  894 */     code.addAload(0);
/*  895 */     code.addGetfield(classname, "handler", HANDLER_TYPE);
/*  896 */     code.addOpcode(176);
/*  897 */     minfo.setCodeAttribute(code.toCodeAttribute());
/*  898 */     cf.addMethod(minfo);
/*      */   }
/*      */ 
/*      */   private int overrideMethods(ClassFile cf, ConstPool cp, String className)
/*      */     throws CannotCompileException
/*      */   {
/*  904 */     String prefix = makeUniqueName("_d", this.signatureMethods);
/*  905 */     Iterator it = this.signatureMethods.iterator();
/*  906 */     int index = 0;
/*  907 */     while (it.hasNext()) {
/*  908 */       Map.Entry e = (Map.Entry)it.next();
/*  909 */       String key = (String)e.getKey();
/*  910 */       Method meth = (Method)e.getValue();
/*  911 */       int mod = meth.getModifiers();
/*  912 */       if (testBit(this.signature, index)) {
/*  913 */         override(className, meth, prefix, index, keyToDesc(key), cf, cp);
/*      */       }
/*      */ 
/*  916 */       index++;
/*      */     }
/*      */ 
/*  919 */     return index;
/*      */   }
/*      */ 
/*      */   private void override(String thisClassname, Method meth, String prefix, int index, String desc, ClassFile cf, ConstPool cp)
/*      */     throws CannotCompileException
/*      */   {
/*  926 */     Class declClass = meth.getDeclaringClass();
/*  927 */     String delegatorName = prefix + index + meth.getName();
/*  928 */     if (Modifier.isAbstract(meth.getModifiers())) {
/*  929 */       delegatorName = null;
/*      */     } else {
/*  931 */       MethodInfo delegator = makeDelegator(meth, desc, cp, declClass, delegatorName);
/*      */ 
/*  934 */       delegator.setAccessFlags(delegator.getAccessFlags() & 0xFFFFFFBF);
/*  935 */       cf.addMethod(delegator);
/*      */     }
/*      */ 
/*  938 */     MethodInfo forwarder = makeForwarder(thisClassname, meth, desc, cp, declClass, delegatorName, index);
/*      */ 
/*  941 */     cf.addMethod(forwarder);
/*      */   }
/*      */ 
/*      */   private void makeConstructors(String thisClassName, ClassFile cf, ConstPool cp, String classname)
/*      */     throws CannotCompileException
/*      */   {
/*  947 */     Constructor[] cons = SecurityActions.getDeclaredConstructors(this.superClass);
/*      */ 
/*  949 */     boolean doHandlerInit = !this.factoryUseCache;
/*  950 */     for (int i = 0; i < cons.length; i++) {
/*  951 */       Constructor c = cons[i];
/*  952 */       int mod = c.getModifiers();
/*  953 */       if ((!Modifier.isFinal(mod)) && (!Modifier.isPrivate(mod)) && (isVisible(mod, this.basename, c)))
/*      */       {
/*  955 */         MethodInfo m = makeConstructor(thisClassName, c, cp, this.superClass, doHandlerInit);
/*  956 */         cf.addMethod(m);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static String makeUniqueName(String name, List sortedMethods) {
/*  962 */     if (makeUniqueName0(name, sortedMethods.iterator())) {
/*  963 */       return name;
/*      */     }
/*  965 */     for (int i = 100; i < 999; i++) {
/*  966 */       String s = name + i;
/*  967 */       if (makeUniqueName0(s, sortedMethods.iterator())) {
/*  968 */         return s;
/*      */       }
/*      */     }
/*  971 */     throw new RuntimeException("cannot make a unique method name");
/*      */   }
/*      */ 
/*      */   private static boolean makeUniqueName0(String name, Iterator it) {
/*  975 */     while (it.hasNext()) {
/*  976 */       Map.Entry e = (Map.Entry)it.next();
/*  977 */       String key = (String)e.getKey();
/*  978 */       if (key.startsWith(name)) {
/*  979 */         return false;
/*      */       }
/*      */     }
/*  982 */     return true;
/*      */   }
/*      */ 
/*      */   private static boolean isVisible(int mod, String from, Member meth)
/*      */   {
/*  991 */     if ((mod & 0x2) != 0)
/*  992 */       return false;
/*  993 */     if ((mod & 0x5) != 0) {
/*  994 */       return true;
/*      */     }
/*  996 */     String p = getPackageName(from);
/*  997 */     String q = getPackageName(meth.getDeclaringClass().getName());
/*  998 */     if (p == null) {
/*  999 */       return q == null;
/*      */     }
/* 1001 */     return p.equals(q);
/*      */   }
/*      */ 
/*      */   private static String getPackageName(String name)
/*      */   {
/* 1006 */     int i = name.lastIndexOf('.');
/* 1007 */     if (i < 0) {
/* 1008 */       return null;
/*      */     }
/* 1010 */     return name.substring(0, i);
/*      */   }
/*      */ 
/*      */   private static HashMap getMethods(Class superClass, Class[] interfaceTypes) {
/* 1014 */     HashMap hash = new HashMap();
/* 1015 */     for (int i = 0; i < interfaceTypes.length; i++) {
/* 1016 */       getMethods(hash, interfaceTypes[i]);
/*      */     }
/* 1018 */     getMethods(hash, superClass);
/* 1019 */     return hash;
/*      */   }
/*      */ 
/*      */   private static void getMethods(HashMap hash, Class clazz) {
/* 1023 */     Class[] ifs = clazz.getInterfaces();
/* 1024 */     for (int i = 0; i < ifs.length; i++) {
/* 1025 */       getMethods(hash, ifs[i]);
/*      */     }
/* 1027 */     Class parent = clazz.getSuperclass();
/* 1028 */     if (parent != null) {
/* 1029 */       getMethods(hash, parent);
/*      */     }
/* 1031 */     Method[] methods = SecurityActions.getDeclaredMethods(clazz);
/* 1032 */     for (int i = 0; i < methods.length; i++)
/* 1033 */       if (!Modifier.isPrivate(methods[i].getModifiers())) {
/* 1034 */         Method m = methods[i];
/* 1035 */         String key = m.getName() + ':' + RuntimeSupport.makeDescriptor(m);
/*      */ 
/* 1038 */         Method oldMethod = (Method)hash.put(key, methods[i]);
/*      */ 
/* 1041 */         if ((null != oldMethod) && (Modifier.isPublic(oldMethod.getModifiers())) && (!Modifier.isPublic(methods[i].getModifiers())))
/*      */         {
/* 1045 */           hash.put(key, oldMethod);
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   private static String keyToDesc(String key) {
/* 1051 */     return key.substring(key.indexOf(':') + 1);
/*      */   }
/*      */ 
/*      */   private static MethodInfo makeConstructor(String thisClassName, Constructor cons, ConstPool cp, Class superClass, boolean doHandlerInit)
/*      */   {
/* 1056 */     String desc = RuntimeSupport.makeDescriptor(cons.getParameterTypes(), Void.TYPE);
/*      */ 
/* 1058 */     MethodInfo minfo = new MethodInfo(cp, "<init>", desc);
/* 1059 */     minfo.setAccessFlags(1);
/* 1060 */     setThrows(minfo, cp, cons.getExceptionTypes());
/* 1061 */     Bytecode code = new Bytecode(cp, 0, 0);
/*      */ 
/* 1066 */     if (doHandlerInit) {
/* 1067 */       code.addAload(0);
/* 1068 */       code.addGetstatic(thisClassName, "default_interceptor", HANDLER_TYPE);
/* 1069 */       code.addPutfield(thisClassName, "handler", HANDLER_TYPE);
/* 1070 */       code.addGetstatic(thisClassName, "default_interceptor", HANDLER_TYPE);
/* 1071 */       code.addOpcode(199);
/* 1072 */       code.addIndex(10);
/*      */     }
/*      */ 
/* 1076 */     code.addAload(0);
/* 1077 */     code.addGetstatic("com.newrelic.javassist.util.proxy.RuntimeSupport", "default_interceptor", HANDLER_TYPE);
/* 1078 */     code.addPutfield(thisClassName, "handler", HANDLER_TYPE);
/* 1079 */     int pc = code.currentPc();
/*      */ 
/* 1081 */     code.addAload(0);
/* 1082 */     int s = addLoadParameters(code, cons.getParameterTypes(), 1);
/* 1083 */     code.addInvokespecial(superClass.getName(), "<init>", desc);
/* 1084 */     code.addOpcode(177);
/* 1085 */     code.setMaxLocals(s + 1);
/* 1086 */     CodeAttribute ca = code.toCodeAttribute();
/* 1087 */     minfo.setCodeAttribute(ca);
/*      */ 
/* 1089 */     StackMapTable.Writer writer = new StackMapTable.Writer(32);
/* 1090 */     writer.sameFrame(pc);
/* 1091 */     ca.setAttribute(writer.toStackMapTable(cp));
/* 1092 */     return minfo;
/*      */   }
/*      */ 
/*      */   private static MethodInfo makeDelegator(Method meth, String desc, ConstPool cp, Class declClass, String delegatorName)
/*      */   {
/* 1097 */     MethodInfo delegator = new MethodInfo(cp, delegatorName, desc);
/* 1098 */     delegator.setAccessFlags(0x11 | meth.getModifiers() & 0xFFFFFAD9);
/*      */ 
/* 1104 */     setThrows(delegator, cp, meth);
/* 1105 */     Bytecode code = new Bytecode(cp, 0, 0);
/* 1106 */     code.addAload(0);
/* 1107 */     int s = addLoadParameters(code, meth.getParameterTypes(), 1);
/* 1108 */     code.addInvokespecial(declClass.getName(), meth.getName(), desc);
/* 1109 */     addReturn(code, meth.getReturnType());
/* 1110 */     code.setMaxLocals(++s);
/* 1111 */     delegator.setCodeAttribute(code.toCodeAttribute());
/* 1112 */     return delegator;
/*      */   }
/*      */ 
/*      */   private static MethodInfo makeForwarder(String thisClassName, Method meth, String desc, ConstPool cp, Class declClass, String delegatorName, int index)
/*      */   {
/* 1121 */     MethodInfo forwarder = new MethodInfo(cp, meth.getName(), desc);
/* 1122 */     forwarder.setAccessFlags(0x10 | meth.getModifiers() & 0xFFFFFADF);
/*      */ 
/* 1126 */     setThrows(forwarder, cp, meth);
/* 1127 */     int args = Descriptor.paramSize(desc);
/* 1128 */     Bytecode code = new Bytecode(cp, 0, args + 2);
/*      */ 
/* 1140 */     int origIndex = index * 2;
/* 1141 */     int delIndex = index * 2 + 1;
/* 1142 */     int arrayVar = args + 1;
/* 1143 */     code.addGetstatic(thisClassName, "_methods_", "[Ljava/lang/reflect/Method;");
/* 1144 */     code.addAstore(arrayVar);
/*      */ 
/* 1146 */     callFind2Methods(code, meth.getName(), delegatorName, origIndex, desc, arrayVar);
/*      */ 
/* 1148 */     code.addAload(0);
/* 1149 */     code.addGetfield(thisClassName, "handler", HANDLER_TYPE);
/* 1150 */     code.addAload(0);
/*      */ 
/* 1152 */     code.addAload(arrayVar);
/* 1153 */     code.addIconst(origIndex);
/* 1154 */     code.addOpcode(50);
/*      */ 
/* 1156 */     code.addAload(arrayVar);
/* 1157 */     code.addIconst(delIndex);
/* 1158 */     code.addOpcode(50);
/*      */ 
/* 1160 */     makeParameterList(code, meth.getParameterTypes());
/* 1161 */     code.addInvokeinterface(MethodHandler.class.getName(), "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", 5);
/*      */ 
/* 1164 */     Class retType = meth.getReturnType();
/* 1165 */     addUnwrapper(code, retType);
/* 1166 */     addReturn(code, retType);
/*      */ 
/* 1168 */     CodeAttribute ca = code.toCodeAttribute();
/* 1169 */     forwarder.setCodeAttribute(ca);
/* 1170 */     return forwarder;
/*      */   }
/*      */ 
/*      */   private static void setThrows(MethodInfo minfo, ConstPool cp, Method orig) {
/* 1174 */     Class[] exceptions = orig.getExceptionTypes();
/* 1175 */     setThrows(minfo, cp, exceptions);
/*      */   }
/*      */ 
/*      */   private static void setThrows(MethodInfo minfo, ConstPool cp, Class[] exceptions)
/*      */   {
/* 1180 */     if (exceptions.length == 0) {
/* 1181 */       return;
/*      */     }
/* 1183 */     String[] list = new String[exceptions.length];
/* 1184 */     for (int i = 0; i < exceptions.length; i++) {
/* 1185 */       list[i] = exceptions[i].getName();
/*      */     }
/* 1187 */     ExceptionsAttribute ea = new ExceptionsAttribute(cp);
/* 1188 */     ea.setExceptions(list);
/* 1189 */     minfo.setExceptionsAttribute(ea);
/*      */   }
/*      */ 
/*      */   private static int addLoadParameters(Bytecode code, Class[] params, int offset)
/*      */   {
/* 1194 */     int stacksize = 0;
/* 1195 */     int n = params.length;
/* 1196 */     for (int i = 0; i < n; i++) {
/* 1197 */       stacksize += addLoad(code, stacksize + offset, params[i]);
/*      */     }
/* 1199 */     return stacksize;
/*      */   }
/*      */ 
/*      */   private static int addLoad(Bytecode code, int n, Class type) {
/* 1203 */     if (type.isPrimitive()) {
/* 1204 */       if (type == Long.TYPE) {
/* 1205 */         code.addLload(n);
/* 1206 */         return 2;
/*      */       }
/* 1208 */       if (type == Float.TYPE) {
/* 1209 */         code.addFload(n); } else {
/* 1210 */         if (type == Double.TYPE) {
/* 1211 */           code.addDload(n);
/* 1212 */           return 2;
/*      */         }
/*      */ 
/* 1215 */         code.addIload(n);
/*      */       }
/*      */     } else {
/* 1218 */       code.addAload(n);
/*      */     }
/* 1220 */     return 1;
/*      */   }
/*      */ 
/*      */   private static int addReturn(Bytecode code, Class type) {
/* 1224 */     if (type.isPrimitive()) {
/* 1225 */       if (type == Long.TYPE) {
/* 1226 */         code.addOpcode(173);
/* 1227 */         return 2;
/*      */       }
/* 1229 */       if (type == Float.TYPE) {
/* 1230 */         code.addOpcode(174); } else {
/* 1231 */         if (type == Double.TYPE) {
/* 1232 */           code.addOpcode(175);
/* 1233 */           return 2;
/*      */         }
/* 1235 */         if (type == Void.TYPE) {
/* 1236 */           code.addOpcode(177);
/* 1237 */           return 0;
/*      */         }
/*      */ 
/* 1240 */         code.addOpcode(172);
/*      */       }
/*      */     } else {
/* 1243 */       code.addOpcode(176);
/*      */     }
/* 1245 */     return 1;
/*      */   }
/*      */ 
/*      */   private static void makeParameterList(Bytecode code, Class[] params) {
/* 1249 */     int regno = 1;
/* 1250 */     int n = params.length;
/* 1251 */     code.addIconst(n);
/* 1252 */     code.addAnewarray("java/lang/Object");
/* 1253 */     for (int i = 0; i < n; i++) {
/* 1254 */       code.addOpcode(89);
/* 1255 */       code.addIconst(i);
/* 1256 */       Class type = params[i];
/* 1257 */       if (type.isPrimitive()) {
/* 1258 */         regno = makeWrapper(code, type, regno);
/*      */       } else {
/* 1260 */         code.addAload(regno);
/* 1261 */         regno++;
/*      */       }
/*      */ 
/* 1264 */       code.addOpcode(83);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int makeWrapper(Bytecode code, Class type, int regno) {
/* 1269 */     int index = FactoryHelper.typeIndex(type);
/* 1270 */     String wrapper = FactoryHelper.wrapperTypes[index];
/* 1271 */     code.addNew(wrapper);
/* 1272 */     code.addOpcode(89);
/* 1273 */     addLoad(code, regno, type);
/* 1274 */     code.addInvokespecial(wrapper, "<init>", FactoryHelper.wrapperDesc[index]);
/*      */ 
/* 1276 */     return regno + FactoryHelper.dataSize[index];
/*      */   }
/*      */ 
/*      */   private static void callFind2Methods(Bytecode code, String superMethod, String thisMethod, int index, String desc, int arrayVar)
/*      */   {
/* 1284 */     String findClass = RuntimeSupport.class.getName();
/* 1285 */     String findDesc = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;[Ljava/lang/reflect/Method;)V";
/*      */ 
/* 1288 */     code.addAload(0);
/* 1289 */     code.addLdc(superMethod);
/* 1290 */     if (thisMethod == null)
/* 1291 */       code.addOpcode(1);
/*      */     else {
/* 1293 */       code.addLdc(thisMethod);
/*      */     }
/* 1295 */     code.addIconst(index);
/* 1296 */     code.addLdc(desc);
/* 1297 */     code.addAload(arrayVar);
/* 1298 */     code.addInvokestatic(findClass, "find2Methods", findDesc);
/*      */   }
/*      */ 
/*      */   private static void addUnwrapper(Bytecode code, Class type) {
/* 1302 */     if (type.isPrimitive()) {
/* 1303 */       if (type == Void.TYPE) {
/* 1304 */         code.addOpcode(87);
/*      */       } else {
/* 1306 */         int index = FactoryHelper.typeIndex(type);
/* 1307 */         String wrapper = FactoryHelper.wrapperTypes[index];
/* 1308 */         code.addCheckcast(wrapper);
/* 1309 */         code.addInvokevirtual(wrapper, FactoryHelper.unwarpMethods[index], FactoryHelper.unwrapDesc[index]);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1315 */       code.addCheckcast(type.getName());
/*      */     }
/*      */   }
/*      */ 
/* 1319 */   private static MethodInfo makeWriteReplace(ConstPool cp) { MethodInfo minfo = new MethodInfo(cp, "writeReplace", "()Ljava/lang/Object;");
/* 1320 */     String[] list = new String[1];
/* 1321 */     list[0] = "java.io.ObjectStreamException";
/* 1322 */     ExceptionsAttribute ea = new ExceptionsAttribute(cp);
/* 1323 */     ea.setExceptions(list);
/* 1324 */     minfo.setExceptionsAttribute(ea);
/* 1325 */     Bytecode code = new Bytecode(cp, 0, 1);
/* 1326 */     code.addAload(0);
/* 1327 */     code.addInvokestatic("com.newrelic.javassist.util.proxy.RuntimeSupport", "makeSerializedProxy", "(Ljava/lang/Object;)Ljavassist/util/proxy/SerializedProxy;");
/*      */ 
/* 1330 */     code.addOpcode(176);
/* 1331 */     minfo.setCodeAttribute(code.toCodeAttribute());
/* 1332 */     return minfo;
/*      */   }
/*      */ 
/*      */   public static abstract interface ClassLoaderProvider
/*      */   {
/*      */     public abstract ClassLoader get(ProxyFactory paramProxyFactory);
/*      */   }
/*      */ 
/*      */   static class ProxyDetails
/*      */   {
/*      */     byte[] signature;
/*      */     WeakReference proxyClass;
/*      */     boolean isUseWriteReplace;
/*      */ 
/*      */     ProxyDetails(byte[] signature, Class proxyClass, boolean isUseWriteReplace)
/*      */     {
/*  324 */       this.signature = signature;
/*  325 */       this.proxyClass = new WeakReference(proxyClass);
/*  326 */       this.isUseWriteReplace = isUseWriteReplace;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.util.proxy.ProxyFactory
 * JD-Core Version:    0.6.2
 */