/*      */ package com.newrelic.javassist;
/*      */ 
/*      */ import com.newrelic.javassist.bytecode.ClassFile;
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import com.newrelic.javassist.compiler.AccessorMaker;
/*      */ import com.newrelic.javassist.expr.ExprEditor;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URL;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.Collection;
/*      */ 
/*      */ public abstract class CtClass
/*      */ {
/*      */   protected String qualifiedName;
/*      */   public static final String version = "3.12.0.GA";
/*      */   static final String javaLangObject = "java.lang.Object";
/*      */   public static CtClass booleanType;
/*      */   public static CtClass charType;
/*      */   public static CtClass byteType;
/*      */   public static CtClass shortType;
/*      */   public static CtClass intType;
/*      */   public static CtClass longType;
/*      */   public static CtClass floatType;
/*      */   public static CtClass doubleType;
/*      */   public static CtClass voidType;
/*  129 */   static CtClass[] primitiveTypes = new CtClass[9];
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*   65 */     System.out.println("Javassist version 3.12.0.GA");
/*   66 */     System.out.println("Copyright (C) 1999-2010 Shigeru Chiba. All Rights Reserved.");
/*      */   }
/*      */ 
/*      */   protected CtClass(String name)
/*      */   {
/*  178 */     this.qualifiedName = name;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  185 */     StringBuffer buf = new StringBuffer(getClass().getName());
/*  186 */     buf.append("@");
/*  187 */     buf.append(Integer.toHexString(hashCode()));
/*  188 */     buf.append("[");
/*  189 */     extendToString(buf);
/*  190 */     buf.append("]");
/*  191 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   protected void extendToString(StringBuffer buffer)
/*      */   {
/*  199 */     buffer.append(getName());
/*      */   }
/*      */ 
/*      */   public ClassPool getClassPool()
/*      */   {
/*  205 */     return null;
/*      */   }
/*      */ 
/*      */   public ClassFile getClassFile()
/*      */   {
/*  214 */     checkModify();
/*  215 */     return getClassFile2();
/*      */   }
/*      */ 
/*      */   public ClassFile getClassFile2()
/*      */   {
/*  236 */     return null;
/*      */   }
/*      */ 
/*      */   public AccessorMaker getAccessorMaker()
/*      */   {
/*  242 */     return null;
/*      */   }
/*      */ 
/*      */   public URL getURL()
/*      */     throws NotFoundException
/*      */   {
/*  249 */     throw new NotFoundException(getName());
/*      */   }
/*      */ 
/*      */   public boolean isModified()
/*      */   {
/*  255 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isFrozen()
/*      */   {
/*  264 */     return true;
/*      */   }
/*      */ 
/*      */   public void freeze()
/*      */   {
/*      */   }
/*      */ 
/*      */   void checkModify()
/*      */     throws RuntimeException
/*      */   {
/*  278 */     if (isFrozen())
/*  279 */       throw new RuntimeException(getName() + " class is frozen");
/*      */   }
/*      */ 
/*      */   public void defrost()
/*      */   {
/*  300 */     throw new RuntimeException("cannot defrost " + getName());
/*      */   }
/*      */ 
/*      */   public boolean isPrimitive()
/*      */   {
/*  308 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isArray()
/*      */   {
/*  314 */     return false;
/*      */   }
/*      */ 
/*      */   public CtClass getComponentType()
/*      */     throws NotFoundException
/*      */   {
/*  322 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean subtypeOf(CtClass clazz)
/*      */     throws NotFoundException
/*      */   {
/*  331 */     return (this == clazz) || (getName().equals(clazz.getName()));
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  337 */     return this.qualifiedName;
/*      */   }
/*      */ 
/*      */   public final String getSimpleName()
/*      */   {
/*  343 */     String qname = this.qualifiedName;
/*  344 */     int index = qname.lastIndexOf('.');
/*  345 */     if (index < 0) {
/*  346 */       return qname;
/*      */     }
/*  348 */     return qname.substring(index + 1);
/*      */   }
/*      */ 
/*      */   public final String getPackageName()
/*      */   {
/*  355 */     String qname = this.qualifiedName;
/*  356 */     int index = qname.lastIndexOf('.');
/*  357 */     if (index < 0) {
/*  358 */       return null;
/*      */     }
/*  360 */     return qname.substring(0, index);
/*      */   }
/*      */ 
/*      */   public void setName(String name)
/*      */   {
/*  369 */     checkModify();
/*  370 */     if (name != null)
/*  371 */       this.qualifiedName = name;
/*      */   }
/*      */ 
/*      */   public void replaceClassName(String oldName, String newName)
/*      */   {
/*  382 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void replaceClassName(ClassMap map)
/*      */   {
/*  403 */     checkModify();
/*      */   }
/*      */ 
/*      */   public synchronized Collection getRefClasses()
/*      */   {
/*  414 */     ClassFile cf = getClassFile2();
/*  415 */     if (cf != null) {
/*  416 */       ClassMap cm = new ClassMap() {
/*      */         public void put(String oldname, String newname) {
/*  418 */           put0(oldname, newname);
/*      */         }
/*      */ 
/*      */         public Object get(Object jvmClassName) {
/*  422 */           String n = toJavaName((String)jvmClassName);
/*  423 */           put0(n, n);
/*  424 */           return null;
/*      */         }
/*      */ 
/*      */         public void fix(String name)
/*      */         {
/*      */         }
/*      */       };
/*  429 */       cf.renameClass(cm);
/*  430 */       return cm.values();
/*      */     }
/*      */ 
/*  433 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean isInterface()
/*      */   {
/*  441 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isAnnotation()
/*      */   {
/*  451 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isEnum()
/*      */   {
/*  461 */     return false;
/*      */   }
/*      */ 
/*      */   public int getModifiers()
/*      */   {
/*  474 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean hasAnnotation(Class clz)
/*      */   {
/*  485 */     return false;
/*      */   }
/*      */ 
/*      */   public Object getAnnotation(Class clz)
/*      */     throws ClassNotFoundException
/*      */   {
/*  500 */     return null;
/*      */   }
/*      */ 
/*      */   public Object[] getAnnotations()
/*      */     throws ClassNotFoundException
/*      */   {
/*  515 */     return new Object[0];
/*      */   }
/*      */ 
/*      */   public Object[] getAvailableAnnotations()
/*      */   {
/*  530 */     return new Object[0];
/*      */   }
/*      */ 
/*      */   public CtClass[] getNestedClasses()
/*      */     throws NotFoundException
/*      */   {
/*  541 */     return new CtClass[0];
/*      */   }
/*      */ 
/*      */   public void setModifiers(int mod)
/*      */   {
/*  556 */     checkModify();
/*      */   }
/*      */ 
/*      */   public boolean subclassOf(CtClass superclass)
/*      */   {
/*  568 */     return false;
/*      */   }
/*      */ 
/*      */   public CtClass getSuperclass()
/*      */     throws NotFoundException
/*      */   {
/*  584 */     return null;
/*      */   }
/*      */ 
/*      */   public void setSuperclass(CtClass clazz)
/*      */     throws CannotCompileException
/*      */   {
/*  601 */     checkModify();
/*      */   }
/*      */ 
/*      */   public CtClass[] getInterfaces()
/*      */     throws NotFoundException
/*      */   {
/*  610 */     return new CtClass[0];
/*      */   }
/*      */ 
/*      */   public void setInterfaces(CtClass[] list)
/*      */   {
/*  623 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void addInterface(CtClass anInterface)
/*      */   {
/*  632 */     checkModify();
/*      */   }
/*      */ 
/*      */   public CtClass getDeclaringClass()
/*      */     throws NotFoundException
/*      */   {
/*  642 */     return null;
/*      */   }
/*      */ 
/*      */   public CtMethod getEnclosingMethod()
/*      */     throws NotFoundException
/*      */   {
/*  653 */     return null;
/*      */   }
/*      */ 
/*      */   public CtClass makeNestedClass(String name, boolean isStatic)
/*      */   {
/*  668 */     throw new RuntimeException(getName() + " is not a class");
/*      */   }
/*      */ 
/*      */   public CtField[] getFields()
/*      */   {
/*  677 */     return new CtField[0];
/*      */   }
/*      */ 
/*      */   public CtField getField(String name)
/*      */     throws NotFoundException
/*      */   {
/*  684 */     throw new NotFoundException(name);
/*      */   }
/*      */ 
/*      */   CtField getField2(String name)
/*      */   {
/*  690 */     return null;
/*      */   }
/*      */ 
/*      */   public CtField[] getDeclaredFields()
/*      */   {
/*  698 */     return new CtField[0];
/*      */   }
/*      */ 
/*      */   public CtField getDeclaredField(String name)
/*      */     throws NotFoundException
/*      */   {
/*  707 */     throw new NotFoundException(name);
/*      */   }
/*      */ 
/*      */   public CtBehavior[] getDeclaredBehaviors()
/*      */   {
/*  714 */     return new CtBehavior[0];
/*      */   }
/*      */ 
/*      */   public CtConstructor[] getConstructors()
/*      */   {
/*  722 */     return new CtConstructor[0];
/*      */   }
/*      */ 
/*      */   public CtConstructor getConstructor(String desc)
/*      */     throws NotFoundException
/*      */   {
/*  738 */     throw new NotFoundException("no such a constructor");
/*      */   }
/*      */ 
/*      */   public CtConstructor[] getDeclaredConstructors()
/*      */   {
/*  747 */     return new CtConstructor[0];
/*      */   }
/*      */ 
/*      */   public CtConstructor getDeclaredConstructor(CtClass[] params)
/*      */     throws NotFoundException
/*      */   {
/*  758 */     String desc = Descriptor.ofConstructor(params);
/*  759 */     return getConstructor(desc);
/*      */   }
/*      */ 
/*      */   public CtConstructor getClassInitializer()
/*      */   {
/*  772 */     return null;
/*      */   }
/*      */ 
/*      */   public CtMethod[] getMethods()
/*      */   {
/*  782 */     return new CtMethod[0];
/*      */   }
/*      */ 
/*      */   public CtMethod getMethod(String name, String desc)
/*      */     throws NotFoundException
/*      */   {
/*  800 */     throw new NotFoundException(name);
/*      */   }
/*      */ 
/*      */   public CtMethod[] getDeclaredMethods()
/*      */   {
/*  810 */     return new CtMethod[0];
/*      */   }
/*      */ 
/*      */   public CtMethod getDeclaredMethod(String name, CtClass[] params)
/*      */     throws NotFoundException
/*      */   {
/*  826 */     throw new NotFoundException(name);
/*      */   }
/*      */ 
/*      */   public CtMethod getDeclaredMethod(String name)
/*      */     throws NotFoundException
/*      */   {
/*  839 */     throw new NotFoundException(name);
/*      */   }
/*      */ 
/*      */   public CtConstructor makeClassInitializer()
/*      */     throws CannotCompileException
/*      */   {
/*  852 */     throw new CannotCompileException("not a class");
/*      */   }
/*      */ 
/*      */   public void addConstructor(CtConstructor c)
/*      */     throws CannotCompileException
/*      */   {
/*  864 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void removeConstructor(CtConstructor c)
/*      */     throws NotFoundException
/*      */   {
/*  874 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void addMethod(CtMethod m)
/*      */     throws CannotCompileException
/*      */   {
/*  881 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void removeMethod(CtMethod m)
/*      */     throws NotFoundException
/*      */   {
/*  891 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void addField(CtField f)
/*      */     throws CannotCompileException
/*      */   {
/*  904 */     addField(f, (CtField.Initializer)null);
/*      */   }
/*      */ 
/*      */   public void addField(CtField f, String init)
/*      */     throws CannotCompileException
/*      */   {
/*  940 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void addField(CtField f, CtField.Initializer init)
/*      */     throws CannotCompileException
/*      */   {
/*  968 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void removeField(CtField f)
/*      */     throws NotFoundException
/*      */   {
/*  978 */     checkModify();
/*      */   }
/*      */ 
/*      */   public byte[] getAttribute(String name)
/*      */   {
/*  999 */     return null;
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, byte[] data)
/*      */   {
/* 1025 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void instrument(CodeConverter converter)
/*      */     throws CannotCompileException
/*      */   {
/* 1039 */     checkModify();
/*      */   }
/*      */ 
/*      */   public void instrument(ExprEditor editor)
/*      */     throws CannotCompileException
/*      */   {
/* 1053 */     checkModify();
/*      */   }
/*      */ 
/*      */   public Class toClass()
/*      */     throws CannotCompileException
/*      */   {
/* 1079 */     return getClassPool().toClass(this);
/*      */   }
/*      */ 
/*      */   public Class toClass(ClassLoader loader, ProtectionDomain domain)
/*      */     throws CannotCompileException
/*      */   {
/* 1116 */     ClassPool cp = getClassPool();
/* 1117 */     if (loader == null) {
/* 1118 */       loader = cp.getClassLoader();
/*      */     }
/* 1120 */     return cp.toClass(this, loader, domain);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final Class toClass(ClassLoader loader)
/*      */     throws CannotCompileException
/*      */   {
/* 1135 */     return getClassPool().toClass(this, loader);
/*      */   }
/*      */ 
/*      */   public void detach()
/*      */   {
/* 1151 */     ClassPool cp = getClassPool();
/* 1152 */     CtClass obj = cp.removeCached(getName());
/* 1153 */     if (obj != this)
/* 1154 */       cp.cacheCtClass(getName(), obj, false);
/*      */   }
/*      */ 
/*      */   public boolean stopPruning(boolean stop)
/*      */   {
/* 1181 */     return true;
/*      */   }
/*      */ 
/*      */   public void prune()
/*      */   {
/*      */   }
/*      */ 
/*      */   void incGetCounter()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void rebuildClassFile()
/*      */   {
/*      */   }
/*      */ 
/*      */   public byte[] toBytecode()
/*      */     throws IOException, CannotCompileException
/*      */   {
/* 1243 */     ByteArrayOutputStream barray = new ByteArrayOutputStream();
/* 1244 */     DataOutputStream out = new DataOutputStream(barray);
/*      */     try {
/* 1246 */       toBytecode(out);
/*      */     }
/*      */     finally {
/* 1249 */       out.close();
/*      */     }
/*      */ 
/* 1252 */     return barray.toByteArray();
/*      */   }
/*      */ 
/*      */   public void writeFile()
/*      */     throws NotFoundException, IOException, CannotCompileException
/*      */   {
/* 1266 */     writeFile(".");
/*      */   }
/*      */ 
/*      */   public void writeFile(String directoryName)
/*      */     throws CannotCompileException, IOException
/*      */   {
/* 1281 */     String classname = getName();
/* 1282 */     String filename = directoryName + File.separatorChar + classname.replace('.', File.separatorChar) + ".class";
/*      */ 
/* 1284 */     int pos = filename.lastIndexOf(File.separatorChar);
/* 1285 */     if (pos > 0) {
/* 1286 */       String dir = filename.substring(0, pos);
/* 1287 */       if (!dir.equals(".")) {
/* 1288 */         new File(dir).mkdirs();
/*      */       }
/*      */     }
/* 1291 */     DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new DelayedFileOutputStream(filename)));
/*      */     try
/*      */     {
/* 1295 */       toBytecode(out);
/*      */     }
/*      */     finally {
/* 1298 */       out.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void debugWriteFile()
/*      */   {
/* 1310 */     debugWriteFile(".");
/*      */   }
/*      */ 
/*      */   public void debugWriteFile(String directoryName)
/*      */   {
/*      */     try
/*      */     {
/* 1324 */       boolean p = stopPruning(true);
/* 1325 */       writeFile(directoryName);
/* 1326 */       defrost();
/* 1327 */       stopPruning(p);
/*      */     }
/*      */     catch (Exception e) {
/* 1330 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void toBytecode(DataOutputStream out)
/*      */     throws CannotCompileException, IOException
/*      */   {
/* 1387 */     throw new CannotCompileException("not a class");
/*      */   }
/*      */ 
/*      */   public String makeUniqueName(String prefix)
/*      */   {
/* 1400 */     throw new RuntimeException("not available in " + getName());
/*      */   }
/*      */ 
/*      */   void compress()
/*      */   {
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  131 */     booleanType = new CtPrimitiveType("boolean", 'Z', "java.lang.Boolean", "booleanValue", "()Z", 172, 4, 1);
/*      */ 
/*  135 */     primitiveTypes[0] = booleanType;
/*      */ 
/*  137 */     charType = new CtPrimitiveType("char", 'C', "java.lang.Character", "charValue", "()C", 172, 5, 1);
/*      */ 
/*  140 */     primitiveTypes[1] = charType;
/*      */ 
/*  142 */     byteType = new CtPrimitiveType("byte", 'B', "java.lang.Byte", "byteValue", "()B", 172, 8, 1);
/*      */ 
/*  145 */     primitiveTypes[2] = byteType;
/*      */ 
/*  147 */     shortType = new CtPrimitiveType("short", 'S', "java.lang.Short", "shortValue", "()S", 172, 9, 1);
/*      */ 
/*  150 */     primitiveTypes[3] = shortType;
/*      */ 
/*  152 */     intType = new CtPrimitiveType("int", 'I', "java.lang.Integer", "intValue", "()I", 172, 10, 1);
/*      */ 
/*  155 */     primitiveTypes[4] = intType;
/*      */ 
/*  157 */     longType = new CtPrimitiveType("long", 'J', "java.lang.Long", "longValue", "()J", 173, 11, 2);
/*      */ 
/*  160 */     primitiveTypes[5] = longType;
/*      */ 
/*  162 */     floatType = new CtPrimitiveType("float", 'F', "java.lang.Float", "floatValue", "()F", 174, 6, 1);
/*      */ 
/*  165 */     primitiveTypes[6] = floatType;
/*      */ 
/*  167 */     doubleType = new CtPrimitiveType("double", 'D', "java.lang.Double", "doubleValue", "()D", 175, 7, 2);
/*      */ 
/*  170 */     primitiveTypes[7] = doubleType;
/*      */ 
/*  172 */     voidType = new CtPrimitiveType("void", 'V', "java.lang.Void", null, null, 177, 0, 0);
/*      */ 
/*  174 */     primitiveTypes[8] = voidType;
/*      */   }
/*      */ 
/*      */   static class DelayedFileOutputStream extends OutputStream
/*      */   {
/*      */     private FileOutputStream file;
/*      */     private String filename;
/*      */ 
/*      */     DelayedFileOutputStream(String name)
/*      */     {
/* 1339 */       this.file = null;
/* 1340 */       this.filename = name;
/*      */     }
/*      */ 
/*      */     private void init() throws IOException {
/* 1344 */       if (this.file == null)
/* 1345 */         this.file = new FileOutputStream(this.filename);
/*      */     }
/*      */ 
/*      */     public void write(int b) throws IOException {
/* 1349 */       init();
/* 1350 */       this.file.write(b);
/*      */     }
/*      */ 
/*      */     public void write(byte[] b) throws IOException {
/* 1354 */       init();
/* 1355 */       this.file.write(b);
/*      */     }
/*      */ 
/*      */     public void write(byte[] b, int off, int len) throws IOException {
/* 1359 */       init();
/* 1360 */       this.file.write(b, off, len);
/*      */     }
/*      */ 
/*      */     public void flush() throws IOException
/*      */     {
/* 1365 */       init();
/* 1366 */       this.file.flush();
/*      */     }
/*      */ 
/*      */     public void close() throws IOException {
/* 1370 */       init();
/* 1371 */       this.file.close();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtClass
 * JD-Core Version:    0.6.2
 */