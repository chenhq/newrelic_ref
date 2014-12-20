/*     */ package com.newrelic.javassist.compiler;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.CtField;
/*     */ import com.newrelic.javassist.Modifier;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.ClassFile;
/*     */ import com.newrelic.javassist.bytecode.Descriptor;
/*     */ import com.newrelic.javassist.bytecode.MethodInfo;
/*     */ import com.newrelic.javassist.compiler.ast.ASTList;
/*     */ import com.newrelic.javassist.compiler.ast.ASTree;
/*     */ import com.newrelic.javassist.compiler.ast.Declarator;
/*     */ import com.newrelic.javassist.compiler.ast.Keyword;
/*     */ import com.newrelic.javassist.compiler.ast.Symbol;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class MemberResolver
/*     */   implements TokenId
/*     */ {
/*     */   private ClassPool classPool;
/*     */   private static final int YES = 0;
/*     */   private static final int NO = -1;
/*     */ 
/*     */   public MemberResolver(ClassPool cp)
/*     */   {
/*  30 */     this.classPool = cp;
/*     */   }
/*     */   public ClassPool getClassPool() {
/*  33 */     return this.classPool;
/*     */   }
/*     */   private static void fatal() throws CompileError {
/*  36 */     throw new CompileError("fatal");
/*     */   }
/*     */ 
/*     */   public void recordPackage(String jvmClassName)
/*     */   {
/*  43 */     String classname = jvmToJavaName(jvmClassName);
/*     */     while (true) {
/*  45 */       int i = classname.lastIndexOf('.');
/*  46 */       if (i <= 0) break;
/*  47 */       classname = classname.substring(0, i);
/*  48 */       this.classPool.recordInvalidClassName(classname);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Method lookupMethod(CtClass clazz, CtClass currentClass, MethodInfo current, String methodName, int[] argTypes, int[] argDims, String[] argClassNames)
/*     */     throws CompileError
/*     */   {
/*  81 */     Method maybe = null;
/*     */ 
/*  83 */     if ((current != null) && (clazz == currentClass) && 
/*  84 */       (current.getName().equals(methodName))) {
/*  85 */       int res = compareSignature(current.getDescriptor(), argTypes, argDims, argClassNames);
/*     */ 
/*  87 */       if (res != -1) {
/*  88 */         Method r = new Method(clazz, current, res);
/*  89 */         if (res == 0) {
/*  90 */           return r;
/*     */         }
/*  92 */         maybe = r;
/*     */       }
/*     */     }
/*     */ 
/*  96 */     Method m = lookupMethod(clazz, methodName, argTypes, argDims, argClassNames, maybe != null);
/*     */ 
/*  98 */     if (m != null) {
/*  99 */       return m;
/*     */     }
/* 101 */     return maybe;
/*     */   }
/*     */ 
/*     */   private Method lookupMethod(CtClass clazz, String methodName, int[] argTypes, int[] argDims, String[] argClassNames, boolean onlyExact)
/*     */     throws CompileError
/*     */   {
/* 109 */     Method maybe = null;
/* 110 */     ClassFile cf = clazz.getClassFile2();
/*     */ 
/* 113 */     if (cf != null) {
/* 114 */       List list = cf.getMethods();
/* 115 */       int n = list.size();
/* 116 */       for (int i = 0; i < n; i++) {
/* 117 */         MethodInfo minfo = (MethodInfo)list.get(i);
/* 118 */         if (minfo.getName().equals(methodName)) {
/* 119 */           int res = compareSignature(minfo.getDescriptor(), argTypes, argDims, argClassNames);
/*     */ 
/* 121 */           if (res != -1) {
/* 122 */             Method r = new Method(clazz, minfo, res);
/* 123 */             if (res == 0)
/* 124 */               return r;
/* 125 */             if ((maybe == null) || (maybe.notmatch > res)) {
/* 126 */               maybe = r;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 132 */     if (onlyExact)
/* 133 */       maybe = null;
/*     */     else {
/* 135 */       onlyExact = maybe != null;
/*     */     }
/* 137 */     int mod = clazz.getModifiers();
/* 138 */     boolean isIntf = Modifier.isInterface(mod);
/*     */     try
/*     */     {
/* 141 */       if (!isIntf) {
/* 142 */         CtClass pclazz = clazz.getSuperclass();
/* 143 */         if (pclazz != null) {
/* 144 */           Method r = lookupMethod(pclazz, methodName, argTypes, argDims, argClassNames, onlyExact);
/*     */ 
/* 146 */           if (r != null)
/* 147 */             return r;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NotFoundException e) {
/*     */     }
/* 153 */     if ((isIntf) || (Modifier.isAbstract(mod)))
/*     */       try {
/* 155 */         CtClass[] ifs = clazz.getInterfaces();
/* 156 */         int size = ifs.length;
/* 157 */         for (int i = 0; i < size; i++) {
/* 158 */           Method r = lookupMethod(ifs[i], methodName, argTypes, argDims, argClassNames, onlyExact);
/*     */ 
/* 161 */           if (r != null) {
/* 162 */             return r;
/*     */           }
/*     */         }
/* 165 */         if (isIntf)
/*     */         {
/* 167 */           CtClass pclazz = clazz.getSuperclass();
/* 168 */           if (pclazz != null) {
/* 169 */             Method r = lookupMethod(pclazz, methodName, argTypes, argDims, argClassNames, onlyExact);
/*     */ 
/* 171 */             if (r != null)
/* 172 */               return r;
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NotFoundException e) {
/*     */       }
/* 178 */     return maybe;
/*     */   }
/*     */ 
/*     */   private int compareSignature(String desc, int[] argTypes, int[] argDims, String[] argClassNames)
/*     */     throws CompileError
/*     */   {
/* 200 */     int result = 0;
/* 201 */     int i = 1;
/* 202 */     int nArgs = argTypes.length;
/* 203 */     if (nArgs != Descriptor.numOfParameters(desc)) {
/* 204 */       return -1;
/*     */     }
/* 206 */     int len = desc.length();
/* 207 */     for (int n = 0; i < len; n++) {
/* 208 */       char c = desc.charAt(i++);
/* 209 */       if (c == ')')
/* 210 */         return n == nArgs ? result : -1;
/* 211 */       if (n >= nArgs) {
/* 212 */         return -1;
/*     */       }
/* 214 */       int dim = 0;
/* 215 */       while (c == '[') {
/* 216 */         dim++;
/* 217 */         c = desc.charAt(i++);
/*     */       }
/*     */ 
/* 220 */       if (argTypes[n] == 412) {
/* 221 */         if ((dim == 0) && (c != 'L')) {
/* 222 */           return -1;
/*     */         }
/* 224 */         if (c == 'L')
/* 225 */           i = desc.indexOf(';', i) + 1;
/*     */       }
/* 227 */       else if (argDims[n] != dim) {
/* 228 */         if ((dim != 0) || (c != 'L') || (!desc.startsWith("java/lang/Object;", i)))
/*     */         {
/* 230 */           return -1;
/*     */         }
/*     */ 
/* 233 */         i = desc.indexOf(';', i) + 1;
/* 234 */         result++;
/* 235 */         if (i <= 0)
/* 236 */           return -1;
/*     */       }
/* 238 */       else if (c == 'L') {
/* 239 */         int j = desc.indexOf(';', i);
/* 240 */         if ((j < 0) || (argTypes[n] != 307)) {
/* 241 */           return -1;
/*     */         }
/* 243 */         String cname = desc.substring(i, j);
/* 244 */         if (!cname.equals(argClassNames[n])) {
/* 245 */           CtClass clazz = lookupClassByJvmName(argClassNames[n]);
/*     */           try {
/* 247 */             if (clazz.subtypeOf(lookupClassByJvmName(cname)))
/* 248 */               result++;
/*     */             else
/* 250 */               return -1;
/*     */           }
/*     */           catch (NotFoundException e) {
/* 253 */             result++;
/*     */           }
/*     */         }
/*     */ 
/* 257 */         i = j + 1;
/*     */       }
/*     */       else {
/* 260 */         int t = descToType(c);
/* 261 */         int at = argTypes[n];
/* 262 */         if (t != at) {
/* 263 */           if ((t == 324) && ((at == 334) || (at == 303) || (at == 306)))
/*     */           {
/* 265 */             result++;
/*     */           }
/* 267 */           else return -1;
/*     */         }
/*     */       }
/*     */     }
/* 271 */     return -1;
/*     */   }
/*     */ 
/*     */   public CtField lookupFieldByJvmName2(String jvmClassName, Symbol fieldSym, ASTree expr)
/*     */     throws NoFieldException
/*     */   {
/* 282 */     String field = fieldSym.get();
/* 283 */     CtClass cc = null;
/*     */     try {
/* 285 */       cc = lookupClass(jvmToJavaName(jvmClassName), true);
/*     */     }
/*     */     catch (CompileError e)
/*     */     {
/* 289 */       throw new NoFieldException(jvmClassName + "/" + field, expr);
/*     */     }
/*     */     try
/*     */     {
/* 293 */       return cc.getField(field);
/*     */     }
/*     */     catch (NotFoundException e)
/*     */     {
/* 297 */       jvmClassName = javaToJvmName(cc.getName());
/* 298 */     }throw new NoFieldException(jvmClassName + "$" + field, expr);
/*     */   }
/*     */ 
/*     */   public CtField lookupFieldByJvmName(String jvmClassName, Symbol fieldName)
/*     */     throws CompileError
/*     */   {
/* 308 */     return lookupField(jvmToJavaName(jvmClassName), fieldName);
/*     */   }
/*     */ 
/*     */   public CtField lookupField(String className, Symbol fieldName)
/*     */     throws CompileError
/*     */   {
/* 317 */     CtClass cc = lookupClass(className, false);
/*     */     try {
/* 319 */       return cc.getField(fieldName.get());
/*     */     } catch (NotFoundException e) {
/*     */     }
/* 322 */     throw new CompileError("no such field: " + fieldName.get());
/*     */   }
/*     */ 
/*     */   public CtClass lookupClassByName(ASTList name) throws CompileError {
/* 326 */     return lookupClass(Declarator.astToClassName(name, '.'), false);
/*     */   }
/*     */ 
/*     */   public CtClass lookupClassByJvmName(String jvmName) throws CompileError {
/* 330 */     return lookupClass(jvmToJavaName(jvmName), false);
/*     */   }
/*     */ 
/*     */   public CtClass lookupClass(Declarator decl) throws CompileError {
/* 334 */     return lookupClass(decl.getType(), decl.getArrayDim(), decl.getClassName());
/*     */   }
/*     */ 
/*     */   public CtClass lookupClass(int type, int dim, String classname)
/*     */     throws CompileError
/*     */   {
/* 344 */     String cname = "";
/*     */ 
/* 346 */     if (type == 307) {
/* 347 */       CtClass clazz = lookupClassByJvmName(classname);
/* 348 */       if (dim > 0)
/* 349 */         cname = clazz.getName();
/*     */       else
/* 351 */         return clazz;
/*     */     }
/*     */     else {
/* 354 */       cname = getTypeName(type);
/*     */     }
/* 356 */     while (dim-- > 0) {
/* 357 */       cname = cname + "[]";
/*     */     }
/* 359 */     return lookupClass(cname, false);
/*     */   }
/*     */ 
/*     */   static String getTypeName(int type)
/*     */     throws CompileError
/*     */   {
/* 366 */     String cname = "";
/* 367 */     switch (type) {
/*     */     case 301:
/* 369 */       cname = "boolean";
/* 370 */       break;
/*     */     case 306:
/* 372 */       cname = "char";
/* 373 */       break;
/*     */     case 303:
/* 375 */       cname = "byte";
/* 376 */       break;
/*     */     case 334:
/* 378 */       cname = "short";
/* 379 */       break;
/*     */     case 324:
/* 381 */       cname = "int";
/* 382 */       break;
/*     */     case 326:
/* 384 */       cname = "long";
/* 385 */       break;
/*     */     case 317:
/* 387 */       cname = "float";
/* 388 */       break;
/*     */     case 312:
/* 390 */       cname = "double";
/* 391 */       break;
/*     */     case 344:
/* 393 */       cname = "void";
/* 394 */       break;
/*     */     default:
/* 396 */       fatal();
/*     */     }
/*     */ 
/* 399 */     return cname;
/*     */   }
/*     */ 
/*     */   public CtClass lookupClass(String name, boolean notCheckInner)
/*     */     throws CompileError
/*     */   {
/*     */     try
/*     */     {
/* 409 */       return lookupClass0(name, notCheckInner);
/*     */     } catch (NotFoundException e) {
/*     */     }
/* 412 */     return searchImports(name);
/*     */   }
/*     */ 
/*     */   private CtClass searchImports(String orgName)
/*     */     throws CompileError
/*     */   {
/* 419 */     if (orgName.indexOf('.') < 0) {
/* 420 */       Iterator it = this.classPool.getImportedPackages();
/* 421 */       while (it.hasNext()) {
/* 422 */         String pac = (String)it.next();
/* 423 */         String fqName = pac + '.' + orgName;
/*     */         try {
/* 425 */           CtClass cc = this.classPool.get(fqName);
/*     */ 
/* 427 */           this.classPool.recordInvalidClassName(orgName);
/* 428 */           return cc;
/*     */         }
/*     */         catch (NotFoundException e) {
/* 431 */           this.classPool.recordInvalidClassName(fqName);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 436 */     throw new CompileError("no such class: " + orgName);
/*     */   }
/*     */ 
/*     */   private CtClass lookupClass0(String classname, boolean notCheckInner)
/*     */     throws NotFoundException
/*     */   {
/* 442 */     CtClass cc = null;
/*     */     do {
/*     */       try {
/* 445 */         cc = this.classPool.get(classname);
/*     */       }
/*     */       catch (NotFoundException e) {
/* 448 */         int i = classname.lastIndexOf('.');
/* 449 */         if ((notCheckInner) || (i < 0)) {
/* 450 */           throw e;
/*     */         }
/* 452 */         StringBuffer sbuf = new StringBuffer(classname);
/* 453 */         sbuf.setCharAt(i, '$');
/* 454 */         classname = sbuf.toString();
/*     */       }
/*     */     }
/* 457 */     while (cc == null);
/* 458 */     return cc;
/*     */   }
/*     */ 
/*     */   public String resolveClassName(ASTList name)
/*     */     throws CompileError
/*     */   {
/* 467 */     if (name == null) {
/* 468 */       return null;
/*     */     }
/* 470 */     return javaToJvmName(lookupClassByName(name).getName());
/*     */   }
/*     */ 
/*     */   public String resolveJvmClassName(String jvmName)
/*     */     throws CompileError
/*     */   {
/* 477 */     if (jvmName == null) {
/* 478 */       return null;
/*     */     }
/* 480 */     return javaToJvmName(lookupClassByJvmName(jvmName).getName());
/*     */   }
/*     */ 
/*     */   public static CtClass getSuperclass(CtClass c) throws CompileError {
/*     */     try {
/* 485 */       CtClass sc = c.getSuperclass();
/* 486 */       if (sc != null)
/* 487 */         return sc;
/*     */     } catch (NotFoundException e) {
/*     */     }
/* 490 */     throw new CompileError("cannot find the super class of " + c.getName());
/*     */   }
/*     */ 
/*     */   public static String javaToJvmName(String classname)
/*     */   {
/* 495 */     return classname.replace('.', '/');
/*     */   }
/*     */ 
/*     */   public static String jvmToJavaName(String classname) {
/* 499 */     return classname.replace('/', '.');
/*     */   }
/*     */ 
/*     */   public static int descToType(char c) throws CompileError {
/* 503 */     switch (c) {
/*     */     case 'Z':
/* 505 */       return 301;
/*     */     case 'C':
/* 507 */       return 306;
/*     */     case 'B':
/* 509 */       return 303;
/*     */     case 'S':
/* 511 */       return 334;
/*     */     case 'I':
/* 513 */       return 324;
/*     */     case 'J':
/* 515 */       return 326;
/*     */     case 'F':
/* 517 */       return 317;
/*     */     case 'D':
/* 519 */       return 312;
/*     */     case 'V':
/* 521 */       return 344;
/*     */     case 'L':
/*     */     case '[':
/* 524 */       return 307;
/*     */     case 'E':
/*     */     case 'G':
/*     */     case 'H':
/*     */     case 'K':
/*     */     case 'M':
/*     */     case 'N':
/*     */     case 'O':
/*     */     case 'P':
/*     */     case 'Q':
/*     */     case 'R':
/*     */     case 'T':
/*     */     case 'U':
/*     */     case 'W':
/*     */     case 'X':
/* 526 */     case 'Y': } fatal();
/* 527 */     return 344;
/*     */   }
/*     */ 
/*     */   public static int getModifiers(ASTList mods)
/*     */   {
/* 532 */     int m = 0;
/* 533 */     while (mods != null) {
/* 534 */       Keyword k = (Keyword)mods.head();
/* 535 */       mods = mods.tail();
/* 536 */       switch (k.get()) {
/*     */       case 335:
/* 538 */         m |= 8;
/* 539 */         break;
/*     */       case 315:
/* 541 */         m |= 16;
/* 542 */         break;
/*     */       case 338:
/* 544 */         m |= 32;
/* 545 */         break;
/*     */       case 300:
/* 547 */         m |= 1024;
/* 548 */         break;
/*     */       case 332:
/* 550 */         m |= 1;
/* 551 */         break;
/*     */       case 331:
/* 553 */         m |= 4;
/* 554 */         break;
/*     */       case 330:
/* 556 */         m |= 2;
/* 557 */         break;
/*     */       case 345:
/* 559 */         m |= 64;
/* 560 */         break;
/*     */       case 342:
/* 562 */         m |= 128;
/* 563 */         break;
/*     */       case 347:
/* 565 */         m |= 2048;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 570 */     return m;
/*     */   }
/*     */ 
/*     */   public static class Method
/*     */   {
/*     */     public CtClass declaring;
/*     */     public MethodInfo info;
/*     */     public int notmatch;
/*     */ 
/*     */     public Method(CtClass c, MethodInfo i, int n)
/*     */     {
/*  61 */       this.declaring = c;
/*  62 */       this.info = i;
/*  63 */       this.notmatch = n;
/*     */     }
/*     */ 
/*     */     public boolean isStatic()
/*     */     {
/*  70 */       int acc = this.info.getAccessFlags();
/*  71 */       return (acc & 0x8) != 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.MemberResolver
 * JD-Core Version:    0.6.2
 */