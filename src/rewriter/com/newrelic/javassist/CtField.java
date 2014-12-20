/*      */ package com.newrelic.javassist;
/*      */ 
/*      */ import com.newrelic.javassist.bytecode.AccessFlag;
/*      */ import com.newrelic.javassist.bytecode.AnnotationsAttribute;
/*      */ import com.newrelic.javassist.bytecode.AttributeInfo;
/*      */ import com.newrelic.javassist.bytecode.Bytecode;
/*      */ import com.newrelic.javassist.bytecode.ClassFile;
/*      */ import com.newrelic.javassist.bytecode.ConstPool;
/*      */ import com.newrelic.javassist.bytecode.Descriptor;
/*      */ import com.newrelic.javassist.bytecode.FieldInfo;
/*      */ import com.newrelic.javassist.compiler.CompileError;
/*      */ import com.newrelic.javassist.compiler.Javac;
/*      */ import com.newrelic.javassist.compiler.SymbolTable;
/*      */ import com.newrelic.javassist.compiler.ast.ASTree;
/*      */ import com.newrelic.javassist.compiler.ast.DoubleConst;
/*      */ import com.newrelic.javassist.compiler.ast.IntConst;
/*      */ import com.newrelic.javassist.compiler.ast.StringL;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ 
/*      */ public class CtField extends CtMember
/*      */ {
/*      */   static final String javaLangString = "java.lang.String";
/*      */   protected FieldInfo fieldInfo;
/*      */ 
/*      */   public CtField(CtClass type, String name, CtClass declaring)
/*      */     throws CannotCompileException
/*      */   {
/*   60 */     this(Descriptor.of(type), name, declaring);
/*      */   }
/*      */ 
/*      */   public CtField(CtField src, CtClass declaring)
/*      */     throws CannotCompileException
/*      */   {
/*   83 */     this(src.fieldInfo.getDescriptor(), src.fieldInfo.getName(), declaring);
/*      */ 
/*   85 */     ListIterator iterator = src.fieldInfo.getAttributes().listIterator();
/*      */ 
/*   87 */     FieldInfo fi = this.fieldInfo;
/*   88 */     fi.setAccessFlags(src.fieldInfo.getAccessFlags());
/*   89 */     ConstPool cp = fi.getConstPool();
/*   90 */     while (iterator.hasNext()) {
/*   91 */       AttributeInfo ainfo = (AttributeInfo)iterator.next();
/*   92 */       fi.addAttribute(ainfo.copy(cp, null));
/*      */     }
/*      */   }
/*      */ 
/*      */   private CtField(String typeDesc, String name, CtClass clazz)
/*      */     throws CannotCompileException
/*      */   {
/*   99 */     super(clazz);
/*  100 */     ClassFile cf = clazz.getClassFile2();
/*  101 */     if (cf == null) {
/*  102 */       throw new CannotCompileException("bad declaring class: " + clazz.getName());
/*      */     }
/*      */ 
/*  105 */     this.fieldInfo = new FieldInfo(cf.getConstPool(), name, typeDesc);
/*      */   }
/*      */ 
/*      */   CtField(FieldInfo fi, CtClass clazz) {
/*  109 */     super(clazz);
/*  110 */     this.fieldInfo = fi;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  117 */     return getDeclaringClass().getName() + "." + getName() + ":" + this.fieldInfo.getDescriptor();
/*      */   }
/*      */ 
/*      */   protected void extendToString(StringBuffer buffer)
/*      */   {
/*  122 */     buffer.append(' ');
/*  123 */     buffer.append(getName());
/*  124 */     buffer.append(' ');
/*  125 */     buffer.append(this.fieldInfo.getDescriptor());
/*      */   }
/*      */ 
/*      */   protected ASTree getInitAST()
/*      */   {
/*  130 */     return null;
/*      */   }
/*      */ 
/*      */   Initializer getInit()
/*      */   {
/*  135 */     ASTree tree = getInitAST();
/*  136 */     if (tree == null) {
/*  137 */       return null;
/*      */     }
/*  139 */     return Initializer.byExpr(tree);
/*      */   }
/*      */ 
/*      */   public static CtField make(String src, CtClass declaring)
/*      */     throws CannotCompileException
/*      */   {
/*  159 */     Javac compiler = new Javac(declaring);
/*      */     try {
/*  161 */       CtMember obj = compiler.compile(src);
/*  162 */       if ((obj instanceof CtField))
/*  163 */         return (CtField)obj;
/*      */     }
/*      */     catch (CompileError e) {
/*  166 */       throw new CannotCompileException(e);
/*      */     }
/*      */ 
/*  169 */     throw new CannotCompileException("not a field");
/*      */   }
/*      */ 
/*      */   public FieldInfo getFieldInfo()
/*      */   {
/*  176 */     this.declaringClass.checkModify();
/*  177 */     return this.fieldInfo;
/*      */   }
/*      */ 
/*      */   public FieldInfo getFieldInfo2()
/*      */   {
/*  199 */     return this.fieldInfo;
/*      */   }
/*      */ 
/*      */   public CtClass getDeclaringClass()
/*      */   {
/*  206 */     return super.getDeclaringClass();
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  213 */     return this.fieldInfo.getName();
/*      */   }
/*      */ 
/*      */   public void setName(String newName)
/*      */   {
/*  220 */     this.declaringClass.checkModify();
/*  221 */     this.fieldInfo.setName(newName);
/*      */   }
/*      */ 
/*      */   public int getModifiers()
/*      */   {
/*  230 */     return AccessFlag.toModifier(this.fieldInfo.getAccessFlags());
/*      */   }
/*      */ 
/*      */   public void setModifiers(int mod)
/*      */   {
/*  239 */     this.declaringClass.checkModify();
/*  240 */     this.fieldInfo.setAccessFlags(AccessFlag.of(mod));
/*      */   }
/*      */ 
/*      */   public boolean hasAnnotation(Class clz)
/*      */   {
/*  251 */     FieldInfo fi = getFieldInfo2();
/*  252 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)fi.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  254 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)fi.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  256 */     return CtClassType.hasAnnotationType(clz, getDeclaringClass().getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   public Object getAnnotation(Class clz)
/*      */     throws ClassNotFoundException
/*      */   {
/*  272 */     FieldInfo fi = getFieldInfo2();
/*  273 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)fi.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  275 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)fi.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  277 */     return CtClassType.getAnnotationType(clz, getDeclaringClass().getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   public Object[] getAnnotations()
/*      */     throws ClassNotFoundException
/*      */   {
/*  289 */     return getAnnotations(false);
/*      */   }
/*      */ 
/*      */   public Object[] getAvailableAnnotations()
/*      */   {
/*      */     try
/*      */     {
/*  303 */       return getAnnotations(true);
/*      */     }
/*      */     catch (ClassNotFoundException e) {
/*  306 */       throw new RuntimeException("Unexpected exception", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object[] getAnnotations(boolean ignoreNotFound) throws ClassNotFoundException {
/*  311 */     FieldInfo fi = getFieldInfo2();
/*  312 */     AnnotationsAttribute ainfo = (AnnotationsAttribute)fi.getAttribute("RuntimeInvisibleAnnotations");
/*      */ 
/*  314 */     AnnotationsAttribute ainfo2 = (AnnotationsAttribute)fi.getAttribute("RuntimeVisibleAnnotations");
/*      */ 
/*  316 */     return CtClassType.toAnnotationType(ignoreNotFound, getDeclaringClass().getClassPool(), ainfo, ainfo2);
/*      */   }
/*      */ 
/*      */   public String getSignature()
/*      */   {
/*  337 */     return this.fieldInfo.getDescriptor();
/*      */   }
/*      */ 
/*      */   public CtClass getType()
/*      */     throws NotFoundException
/*      */   {
/*  344 */     return Descriptor.toCtClass(this.fieldInfo.getDescriptor(), this.declaringClass.getClassPool());
/*      */   }
/*      */ 
/*      */   public void setType(CtClass clazz)
/*      */   {
/*  352 */     this.declaringClass.checkModify();
/*  353 */     this.fieldInfo.setDescriptor(Descriptor.of(clazz));
/*      */   }
/*      */ 
/*      */   public Object getConstantValue()
/*      */   {
/*  374 */     int index = this.fieldInfo.getConstantValue();
/*  375 */     if (index == 0) {
/*  376 */       return null;
/*      */     }
/*  378 */     ConstPool cp = this.fieldInfo.getConstPool();
/*  379 */     switch (cp.getTag(index)) {
/*      */     case 5:
/*  381 */       return new Long(cp.getLongInfo(index));
/*      */     case 4:
/*  383 */       return new Float(cp.getFloatInfo(index));
/*      */     case 6:
/*  385 */       return new Double(cp.getDoubleInfo(index));
/*      */     case 3:
/*  387 */       int value = cp.getIntegerInfo(index);
/*      */ 
/*  389 */       if ("Z".equals(this.fieldInfo.getDescriptor())) {
/*  390 */         return new Boolean(value != 0);
/*      */       }
/*  392 */       return new Integer(value);
/*      */     case 8:
/*  394 */       return cp.getStringInfo(index);
/*      */     case 7:
/*  396 */     }throw new RuntimeException("bad tag: " + cp.getTag(index) + " at " + index);
/*      */   }
/*      */ 
/*      */   public byte[] getAttribute(String name)
/*      */   {
/*  413 */     AttributeInfo ai = this.fieldInfo.getAttribute(name);
/*  414 */     if (ai == null) {
/*  415 */       return null;
/*      */     }
/*  417 */     return ai.get();
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, byte[] data)
/*      */   {
/*  431 */     this.declaringClass.checkModify();
/*  432 */     this.fieldInfo.addAttribute(new AttributeInfo(this.fieldInfo.getConstPool(), name, data));
/*      */   }
/*      */ 
/*      */   static class MultiArrayInitializer extends CtField.Initializer
/*      */   {
/*      */     CtClass type;
/*      */     int[] dim;
/*      */ 
/*      */     MultiArrayInitializer(CtClass t, int[] d)
/*      */     {
/* 1362 */       this.type = t; this.dim = d;
/*      */     }
/*      */     void check(String desc) throws CannotCompileException {
/* 1365 */       if (desc.charAt(0) != '[')
/* 1366 */         throw new CannotCompileException("type mismatch");
/*      */     }
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1373 */       code.addAload(0);
/* 1374 */       int s = code.addMultiNewarray(type, this.dim);
/* 1375 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1376 */       return s + 1;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1382 */       int s = code.addMultiNewarray(type, this.dim);
/* 1383 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1384 */       return s;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ArrayInitializer extends CtField.Initializer
/*      */   {
/*      */     CtClass type;
/*      */     int size;
/*      */ 
/*      */     ArrayInitializer(CtClass t, int s)
/*      */     {
/* 1329 */       this.type = t; this.size = s;
/*      */     }
/*      */     private void addNewarray(Bytecode code) {
/* 1332 */       if (this.type.isPrimitive()) {
/* 1333 */         code.addNewarray(((CtPrimitiveType)this.type).getArrayType(), this.size);
/*      */       }
/*      */       else
/* 1336 */         code.addAnewarray(this.type, this.size);
/*      */     }
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1343 */       code.addAload(0);
/* 1344 */       addNewarray(code);
/* 1345 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1346 */       return 2;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1352 */       addNewarray(code);
/* 1353 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1354 */       return 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class StringInitializer extends CtField.Initializer
/*      */   {
/*      */     String value;
/*      */ 
/*      */     StringInitializer(String v)
/*      */     {
/* 1297 */       this.value = v;
/*      */     }
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1303 */       code.addAload(0);
/* 1304 */       code.addLdc(this.value);
/* 1305 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1306 */       return 2;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1312 */       code.addLdc(this.value);
/* 1313 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1314 */       return 1;
/*      */     }
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type) {
/* 1318 */       if (type.getName().equals("java.lang.String")) {
/* 1319 */         return cp.addStringInfo(this.value);
/*      */       }
/* 1321 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class DoubleInitializer extends CtField.Initializer
/*      */   {
/*      */     double value;
/*      */ 
/*      */     DoubleInitializer(double v)
/*      */     {
/* 1261 */       this.value = v;
/*      */     }
/*      */     void check(String desc) throws CannotCompileException {
/* 1264 */       if (!desc.equals("D"))
/* 1265 */         throw new CannotCompileException("type mismatch");
/*      */     }
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1272 */       code.addAload(0);
/* 1273 */       code.addLdc2w(this.value);
/* 1274 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1275 */       return 3;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1281 */       code.addLdc2w(this.value);
/* 1282 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1283 */       return 2;
/*      */     }
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type) {
/* 1287 */       if (type == CtClass.doubleType) {
/* 1288 */         return cp.addDoubleInfo(this.value);
/*      */       }
/* 1290 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class FloatInitializer extends CtField.Initializer
/*      */   {
/*      */     float value;
/*      */ 
/*      */     FloatInitializer(float v)
/*      */     {
/* 1225 */       this.value = v;
/*      */     }
/*      */     void check(String desc) throws CannotCompileException {
/* 1228 */       if (!desc.equals("F"))
/* 1229 */         throw new CannotCompileException("type mismatch");
/*      */     }
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1236 */       code.addAload(0);
/* 1237 */       code.addFconst(this.value);
/* 1238 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1239 */       return 3;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1245 */       code.addFconst(this.value);
/* 1246 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1247 */       return 2;
/*      */     }
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type) {
/* 1251 */       if (type == CtClass.floatType) {
/* 1252 */         return cp.addFloatInfo(this.value);
/*      */       }
/* 1254 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LongInitializer extends CtField.Initializer
/*      */   {
/*      */     long value;
/*      */ 
/*      */     LongInitializer(long v)
/*      */     {
/* 1189 */       this.value = v;
/*      */     }
/*      */     void check(String desc) throws CannotCompileException {
/* 1192 */       if (!desc.equals("J"))
/* 1193 */         throw new CannotCompileException("type mismatch");
/*      */     }
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1200 */       code.addAload(0);
/* 1201 */       code.addLdc2w(this.value);
/* 1202 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1203 */       return 3;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1209 */       code.addLdc2w(this.value);
/* 1210 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1211 */       return 2;
/*      */     }
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type) {
/* 1215 */       if (type == CtClass.longType) {
/* 1216 */         return cp.addLongInfo(this.value);
/*      */       }
/* 1218 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class IntInitializer extends CtField.Initializer
/*      */   {
/*      */     int value;
/*      */ 
/*      */     IntInitializer(int v)
/*      */     {
/* 1155 */       this.value = v;
/*      */     }
/*      */     void check(String desc) throws CannotCompileException {
/* 1158 */       char c = desc.charAt(0);
/* 1159 */       if ((c != 'I') && (c != 'S') && (c != 'B') && (c != 'C') && (c != 'Z'))
/* 1160 */         throw new CannotCompileException("type mismatch");
/*      */     }
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1167 */       code.addAload(0);
/* 1168 */       code.addIconst(this.value);
/* 1169 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1170 */       return 2;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1176 */       code.addIconst(this.value);
/* 1177 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1178 */       return 1;
/*      */     }
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type) {
/* 1182 */       return cp.addIntegerInfo(this.value);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class MethodInitializer extends CtField.NewInitializer
/*      */   {
/*      */     String methodName;
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1094 */       code.addAload(0);
/* 1095 */       code.addAload(0);
/*      */       int stacksize;
/*      */       int stacksize;
/* 1097 */       if (this.stringParams == null)
/* 1098 */         stacksize = 2;
/*      */       else {
/* 1100 */         stacksize = compileStringParameter(code) + 2;
/*      */       }
/* 1102 */       if (this.withConstructorParams) {
/* 1103 */         stacksize += CtNewWrappedMethod.compileParameterList(code, parameters, 1);
/*      */       }
/*      */ 
/* 1106 */       String typeDesc = Descriptor.of(type);
/* 1107 */       String mDesc = getDescriptor() + typeDesc;
/* 1108 */       code.addInvokestatic(this.objectType, this.methodName, mDesc);
/* 1109 */       code.addPutfield(Bytecode.THIS, name, typeDesc);
/* 1110 */       return stacksize;
/*      */     }
/*      */ 
/*      */     private String getDescriptor() {
/* 1114 */       String desc3 = "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)";
/*      */ 
/* 1117 */       if (this.stringParams == null) {
/* 1118 */         if (this.withConstructorParams) {
/* 1119 */           return "(Ljava/lang/Object;[Ljava/lang/Object;)";
/*      */         }
/* 1121 */         return "(Ljava/lang/Object;)";
/*      */       }
/* 1123 */       if (this.withConstructorParams) {
/* 1124 */         return "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)";
/*      */       }
/* 1126 */       return "(Ljava/lang/Object;[Ljava/lang/String;)";
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1137 */       int stacksize = 1;
/*      */       String desc;
/*      */       String desc;
/* 1138 */       if (this.stringParams == null) {
/* 1139 */         desc = "()";
/*      */       } else {
/* 1141 */         desc = "([Ljava/lang/String;)";
/* 1142 */         stacksize += compileStringParameter(code);
/*      */       }
/*      */ 
/* 1145 */       String typeDesc = Descriptor.of(type);
/* 1146 */       code.addInvokestatic(this.objectType, this.methodName, desc + typeDesc);
/* 1147 */       code.addPutstatic(Bytecode.THIS, name, typeDesc);
/* 1148 */       return stacksize;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class NewInitializer extends CtField.Initializer
/*      */   {
/*      */     CtClass objectType;
/*      */     String[] stringParams;
/*      */     boolean withConstructorParams;
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/*  998 */       code.addAload(0);
/*  999 */       code.addNew(this.objectType);
/* 1000 */       code.add(89);
/* 1001 */       code.addAload(0);
/*      */       int stacksize;
/*      */       int stacksize;
/* 1003 */       if (this.stringParams == null)
/* 1004 */         stacksize = 4;
/*      */       else {
/* 1006 */         stacksize = compileStringParameter(code) + 4;
/*      */       }
/* 1008 */       if (this.withConstructorParams) {
/* 1009 */         stacksize += CtNewWrappedMethod.compileParameterList(code, parameters, 1);
/*      */       }
/*      */ 
/* 1012 */       code.addInvokespecial(this.objectType, "<init>", getDescriptor());
/* 1013 */       code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/* 1014 */       return stacksize;
/*      */     }
/*      */ 
/*      */     private String getDescriptor() {
/* 1018 */       String desc3 = "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)V";
/*      */ 
/* 1021 */       if (this.stringParams == null) {
/* 1022 */         if (this.withConstructorParams) {
/* 1023 */           return "(Ljava/lang/Object;[Ljava/lang/Object;)V";
/*      */         }
/* 1025 */         return "(Ljava/lang/Object;)V";
/*      */       }
/* 1027 */       if (this.withConstructorParams) {
/* 1028 */         return "(Ljava/lang/Object;[Ljava/lang/String;[Ljava/lang/Object;)V";
/*      */       }
/* 1030 */       return "(Ljava/lang/Object;[Ljava/lang/String;)V";
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/* 1041 */       code.addNew(this.objectType);
/* 1042 */       code.add(89);
/*      */ 
/* 1044 */       int stacksize = 2;
/*      */       String desc;
/*      */       String desc;
/* 1045 */       if (this.stringParams == null) {
/* 1046 */         desc = "()V";
/*      */       } else {
/* 1048 */         desc = "([Ljava/lang/String;)V";
/* 1049 */         stacksize += compileStringParameter(code);
/*      */       }
/*      */ 
/* 1052 */       code.addInvokespecial(this.objectType, "<init>", desc);
/* 1053 */       code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/* 1054 */       return stacksize;
/*      */     }
/*      */ 
/*      */     protected final int compileStringParameter(Bytecode code)
/*      */       throws CannotCompileException
/*      */     {
/* 1060 */       int nparam = this.stringParams.length;
/* 1061 */       code.addIconst(nparam);
/* 1062 */       code.addAnewarray("java.lang.String");
/* 1063 */       for (int j = 0; j < nparam; j++) {
/* 1064 */         code.add(89);
/* 1065 */         code.addIconst(j);
/* 1066 */         code.addLdc(this.stringParams[j]);
/* 1067 */         code.add(83);
/*      */       }
/*      */ 
/* 1070 */       return 4;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ParamInitializer extends CtField.Initializer
/*      */   {
/*      */     int nthParam;
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/*  931 */       if ((parameters != null) && (this.nthParam < parameters.length)) {
/*  932 */         code.addAload(0);
/*  933 */         int nth = nthParamToLocal(this.nthParam, parameters, false);
/*  934 */         int s = code.addLoad(nth, type) + 1;
/*  935 */         code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/*  936 */         return s;
/*      */       }
/*      */ 
/*  939 */       return 0;
/*      */     }
/*      */ 
/*      */     static int nthParamToLocal(int nth, CtClass[] params, boolean isStatic)
/*      */     {
/*  952 */       CtClass longType = CtClass.longType;
/*  953 */       CtClass doubleType = CtClass.doubleType;
/*      */       int k;
/*      */       int k;
/*  955 */       if (isStatic)
/*  956 */         k = 0;
/*      */       else {
/*  958 */         k = 1;
/*      */       }
/*  960 */       for (int i = 0; i < nth; i++) {
/*  961 */         CtClass type = params[i];
/*  962 */         if ((type == longType) || (type == doubleType))
/*  963 */           k += 2;
/*      */         else {
/*  965 */           k++;
/*      */         }
/*      */       }
/*  968 */       return k;
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/*  974 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class PtreeInitializer extends CtField.CodeInitializer0
/*      */   {
/*      */     private ASTree expression;
/*      */ 
/*      */     PtreeInitializer(ASTree expr)
/*      */     {
/*  907 */       this.expression = expr;
/*      */     }
/*      */     void compileExpr(Javac drv) throws CompileError {
/*  910 */       drv.compileExpr(this.expression);
/*      */     }
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type) {
/*  914 */       return getConstantValue2(cp, type, this.expression);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class CodeInitializer extends CtField.CodeInitializer0
/*      */   {
/*      */     private String expression;
/*      */ 
/*      */     CodeInitializer(String expr)
/*      */     {
/*  887 */       this.expression = expr;
/*      */     }
/*      */     void compileExpr(Javac drv) throws CompileError {
/*  890 */       drv.compileExpr(this.expression);
/*      */     }
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type) {
/*      */       try {
/*  895 */         ASTree t = Javac.parseExpr(this.expression, new SymbolTable());
/*  896 */         return getConstantValue2(cp, type, t);
/*      */       } catch (CompileError e) {
/*      */       }
/*  899 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class CodeInitializer0 extends CtField.Initializer
/*      */   {
/*      */     abstract void compileExpr(Javac paramJavac)
/*      */       throws CompileError;
/*      */ 
/*      */     int compile(CtClass type, String name, Bytecode code, CtClass[] parameters, Javac drv)
/*      */       throws CannotCompileException
/*      */     {
/*      */       try
/*      */       {
/*  832 */         code.addAload(0);
/*  833 */         compileExpr(drv);
/*  834 */         code.addPutfield(Bytecode.THIS, name, Descriptor.of(type));
/*  835 */         return code.getMaxStack();
/*      */       }
/*      */       catch (CompileError e) {
/*  838 */         throw new CannotCompileException(e);
/*      */       }
/*      */     }
/*      */ 
/*      */     int compileIfStatic(CtClass type, String name, Bytecode code, Javac drv) throws CannotCompileException
/*      */     {
/*      */       try
/*      */       {
/*  846 */         compileExpr(drv);
/*  847 */         code.addPutstatic(Bytecode.THIS, name, Descriptor.of(type));
/*  848 */         return code.getMaxStack();
/*      */       }
/*      */       catch (CompileError e) {
/*  851 */         throw new CannotCompileException(e);
/*      */       }
/*      */     }
/*      */ 
/*      */     int getConstantValue2(ConstPool cp, CtClass type, ASTree tree) {
/*  856 */       if (type.isPrimitive()) {
/*  857 */         if ((tree instanceof IntConst)) {
/*  858 */           long value = ((IntConst)tree).get();
/*  859 */           if (type == CtClass.doubleType)
/*  860 */             return cp.addDoubleInfo(value);
/*  861 */           if (type == CtClass.floatType)
/*  862 */             return cp.addFloatInfo((float)value);
/*  863 */           if (type == CtClass.longType)
/*  864 */             return cp.addLongInfo(value);
/*  865 */           if (type != CtClass.voidType)
/*  866 */             return cp.addIntegerInfo((int)value);
/*      */         }
/*  868 */         else if ((tree instanceof DoubleConst)) {
/*  869 */           double value = ((DoubleConst)tree).get();
/*  870 */           if (type == CtClass.floatType)
/*  871 */             return cp.addFloatInfo((float)value);
/*  872 */           if (type == CtClass.doubleType)
/*  873 */             return cp.addDoubleInfo(value);
/*      */         }
/*      */       }
/*  876 */       else if (((tree instanceof StringL)) && (type.getName().equals("java.lang.String")))
/*      */       {
/*  878 */         return cp.addStringInfo(((StringL)tree).get());
/*      */       }
/*  880 */       return 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class Initializer
/*      */   {
/*      */     public static Initializer constant(int i)
/*      */     {
/*  457 */       return new CtField.IntInitializer(i);
/*      */     }
/*      */ 
/*      */     public static Initializer constant(boolean b)
/*      */     {
/*  465 */       return new CtField.IntInitializer(b ? 1 : 0);
/*      */     }
/*      */ 
/*      */     public static Initializer constant(long l)
/*      */     {
/*  473 */       return new CtField.LongInitializer(l);
/*      */     }
/*      */ 
/*      */     public static Initializer constant(float l)
/*      */     {
/*  481 */       return new CtField.FloatInitializer(l);
/*      */     }
/*      */ 
/*      */     public static Initializer constant(double d)
/*      */     {
/*  489 */       return new CtField.DoubleInitializer(d);
/*      */     }
/*      */ 
/*      */     public static Initializer constant(String s)
/*      */     {
/*  497 */       return new CtField.StringInitializer(s);
/*      */     }
/*      */ 
/*      */     public static Initializer byParameter(int nth)
/*      */     {
/*  515 */       CtField.ParamInitializer i = new CtField.ParamInitializer();
/*  516 */       i.nthParam = nth;
/*  517 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byNew(CtClass objectType)
/*      */     {
/*  536 */       CtField.NewInitializer i = new CtField.NewInitializer();
/*  537 */       i.objectType = objectType;
/*  538 */       i.stringParams = null;
/*  539 */       i.withConstructorParams = false;
/*  540 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byNew(CtClass objectType, String[] stringParams)
/*      */     {
/*  564 */       CtField.NewInitializer i = new CtField.NewInitializer();
/*  565 */       i.objectType = objectType;
/*  566 */       i.stringParams = stringParams;
/*  567 */       i.withConstructorParams = false;
/*  568 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byNewWithParams(CtClass objectType)
/*      */     {
/*  593 */       CtField.NewInitializer i = new CtField.NewInitializer();
/*  594 */       i.objectType = objectType;
/*  595 */       i.stringParams = null;
/*  596 */       i.withConstructorParams = true;
/*  597 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byNewWithParams(CtClass objectType, String[] stringParams)
/*      */     {
/*  624 */       CtField.NewInitializer i = new CtField.NewInitializer();
/*  625 */       i.objectType = objectType;
/*  626 */       i.stringParams = stringParams;
/*  627 */       i.withConstructorParams = true;
/*  628 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byCall(CtClass methodClass, String methodName)
/*      */     {
/*  653 */       CtField.MethodInitializer i = new CtField.MethodInitializer();
/*  654 */       i.objectType = methodClass;
/*  655 */       i.methodName = methodName;
/*  656 */       i.stringParams = null;
/*  657 */       i.withConstructorParams = false;
/*  658 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byCall(CtClass methodClass, String methodName, String[] stringParams)
/*      */     {
/*  688 */       CtField.MethodInitializer i = new CtField.MethodInitializer();
/*  689 */       i.objectType = methodClass;
/*  690 */       i.methodName = methodName;
/*  691 */       i.stringParams = stringParams;
/*  692 */       i.withConstructorParams = false;
/*  693 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byCallWithParams(CtClass methodClass, String methodName)
/*      */     {
/*  721 */       CtField.MethodInitializer i = new CtField.MethodInitializer();
/*  722 */       i.objectType = methodClass;
/*  723 */       i.methodName = methodName;
/*  724 */       i.stringParams = null;
/*  725 */       i.withConstructorParams = true;
/*  726 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byCallWithParams(CtClass methodClass, String methodName, String[] stringParams)
/*      */     {
/*  758 */       CtField.MethodInitializer i = new CtField.MethodInitializer();
/*  759 */       i.objectType = methodClass;
/*  760 */       i.methodName = methodName;
/*  761 */       i.stringParams = stringParams;
/*  762 */       i.withConstructorParams = true;
/*  763 */       return i;
/*      */     }
/*      */ 
/*      */     public static Initializer byNewArray(CtClass type, int size)
/*      */       throws NotFoundException
/*      */     {
/*  777 */       return new CtField.ArrayInitializer(type.getComponentType(), size);
/*      */     }
/*      */ 
/*      */     public static Initializer byNewArray(CtClass type, int[] sizes)
/*      */     {
/*  790 */       return new CtField.MultiArrayInitializer(type, sizes);
/*      */     }
/*      */ 
/*      */     public static Initializer byExpr(String source)
/*      */     {
/*  799 */       return new CtField.CodeInitializer(source);
/*      */     }
/*      */ 
/*      */     static Initializer byExpr(ASTree source) {
/*  803 */       return new CtField.PtreeInitializer(source);
/*      */     }
/*      */ 
/*      */     void check(String desc)
/*      */       throws CannotCompileException
/*      */     {
/*      */     }
/*      */ 
/*      */     abstract int compile(CtClass paramCtClass, String paramString, Bytecode paramBytecode, CtClass[] paramArrayOfCtClass, Javac paramJavac)
/*      */       throws CannotCompileException;
/*      */ 
/*      */     abstract int compileIfStatic(CtClass paramCtClass, String paramString, Bytecode paramBytecode, Javac paramJavac)
/*      */       throws CannotCompileException;
/*      */ 
/*      */     int getConstantValue(ConstPool cp, CtClass type)
/*      */     {
/*  821 */       return 0;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.CtField
 * JD-Core Version:    0.6.2
 */