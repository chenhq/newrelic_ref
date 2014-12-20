/*     */ package com.newrelic.javassist.bytecode.analysis;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Type
/*     */ {
/*     */   private final CtClass clazz;
/*     */   private final boolean special;
/*  46 */   private static final Map prims = new IdentityHashMap();
/*     */ 
/*  48 */   public static final Type DOUBLE = new Type(CtClass.doubleType);
/*     */ 
/*  50 */   public static final Type BOOLEAN = new Type(CtClass.booleanType);
/*     */ 
/*  52 */   public static final Type LONG = new Type(CtClass.longType);
/*     */ 
/*  54 */   public static final Type CHAR = new Type(CtClass.charType);
/*     */ 
/*  56 */   public static final Type BYTE = new Type(CtClass.byteType);
/*     */ 
/*  58 */   public static final Type SHORT = new Type(CtClass.shortType);
/*     */ 
/*  60 */   public static final Type INTEGER = new Type(CtClass.intType);
/*     */ 
/*  62 */   public static final Type FLOAT = new Type(CtClass.floatType);
/*     */ 
/*  64 */   public static final Type VOID = new Type(CtClass.voidType);
/*     */ 
/*  75 */   public static final Type UNINIT = new Type(null);
/*     */ 
/*  81 */   public static final Type RETURN_ADDRESS = new Type(null, true);
/*     */ 
/*  84 */   public static final Type TOP = new Type(null, true);
/*     */ 
/*  93 */   public static final Type BOGUS = new Type(null, true);
/*     */ 
/*  96 */   public static final Type OBJECT = lookupType("java.lang.Object");
/*     */ 
/*  98 */   public static final Type SERIALIZABLE = lookupType("java.io.Serializable");
/*     */ 
/* 100 */   public static final Type CLONEABLE = lookupType("java.lang.Cloneable");
/*     */ 
/* 102 */   public static final Type THROWABLE = lookupType("java.lang.Throwable");
/*     */ 
/*     */   public static Type get(CtClass clazz)
/*     */   {
/* 126 */     Type type = (Type)prims.get(clazz);
/* 127 */     return type != null ? type : new Type(clazz);
/*     */   }
/*     */ 
/*     */   private static Type lookupType(String name) {
/*     */     try {
/* 132 */       return new Type(ClassPool.getDefault().get(name));
/*     */     } catch (NotFoundException e) {
/* 134 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   Type(CtClass clazz) {
/* 139 */     this(clazz, false);
/*     */   }
/*     */ 
/*     */   private Type(CtClass clazz, boolean special) {
/* 143 */     this.clazz = clazz;
/* 144 */     this.special = special;
/*     */   }
/*     */ 
/*     */   boolean popChanged()
/*     */   {
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 159 */     return (this.clazz == CtClass.doubleType) || (this.clazz == CtClass.longType) || (this == TOP) ? 2 : 1;
/*     */   }
/*     */ 
/*     */   public CtClass getCtClass()
/*     */   {
/* 168 */     return this.clazz;
/*     */   }
/*     */ 
/*     */   public boolean isReference()
/*     */   {
/* 177 */     return (!this.special) && ((this.clazz == null) || (!this.clazz.isPrimitive()));
/*     */   }
/*     */ 
/*     */   public boolean isSpecial()
/*     */   {
/* 187 */     return this.special;
/*     */   }
/*     */ 
/*     */   public boolean isArray()
/*     */   {
/* 196 */     return (this.clazz != null) && (this.clazz.isArray());
/*     */   }
/*     */ 
/*     */   public int getDimensions()
/*     */   {
/* 206 */     if (!isArray()) return 0;
/*     */ 
/* 208 */     String name = this.clazz.getName();
/* 209 */     int pos = name.length() - 1;
/* 210 */     int count = 0;
/* 211 */     while (name.charAt(pos) == ']') {
/* 212 */       pos -= 2;
/* 213 */       count++;
/*     */     }
/*     */ 
/* 216 */     return count;
/*     */   }
/*     */ 
/*     */   public Type getComponent()
/*     */   {
/* 226 */     if ((this.clazz == null) || (!this.clazz.isArray()))
/* 227 */       return null;
/*     */     CtClass component;
/*     */     try
/*     */     {
/* 231 */       component = this.clazz.getComponentType();
/*     */     } catch (NotFoundException e) {
/* 233 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 236 */     Type type = (Type)prims.get(component);
/* 237 */     return type != null ? type : new Type(component);
/*     */   }
/*     */ 
/*     */   public boolean isAssignableFrom(Type type)
/*     */   {
/* 249 */     if (this == type) {
/* 250 */       return true;
/*     */     }
/* 252 */     if (((type == UNINIT) && (isReference())) || ((this == UNINIT) && (type.isReference()))) {
/* 253 */       return true;
/*     */     }
/* 255 */     if ((type instanceof MultiType)) {
/* 256 */       return ((MultiType)type).isAssignableTo(this);
/*     */     }
/* 258 */     if ((type instanceof MultiArrayType)) {
/* 259 */       return ((MultiArrayType)type).isAssignableTo(this);
/*     */     }
/*     */ 
/* 263 */     if ((this.clazz == null) || (this.clazz.isPrimitive()))
/* 264 */       return false;
/*     */     try
/*     */     {
/* 267 */       return type.clazz.subtypeOf(this.clazz);
/*     */     } catch (Exception e) {
/* 269 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Type merge(Type type)
/*     */   {
/* 285 */     if (type == this)
/* 286 */       return this;
/* 287 */     if (type == null)
/* 288 */       return this;
/* 289 */     if (type == UNINIT)
/* 290 */       return this;
/* 291 */     if (this == UNINIT) {
/* 292 */       return type;
/*     */     }
/*     */ 
/* 295 */     if ((!type.isReference()) || (!isReference())) {
/* 296 */       return BOGUS;
/*     */     }
/*     */ 
/* 299 */     if ((type instanceof MultiType)) {
/* 300 */       return type.merge(this);
/*     */     }
/* 302 */     if ((type.isArray()) && (isArray()))
/* 303 */       return mergeArray(type);
/*     */     try
/*     */     {
/* 306 */       return mergeClasses(type);
/*     */     } catch (NotFoundException e) {
/* 308 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   Type getRootComponent(Type type) {
/* 313 */     while (type.isArray()) {
/* 314 */       type = type.getComponent();
/*     */     }
/* 316 */     return type;
/*     */   }
/*     */ 
/*     */   private Type createArray(Type rootComponent, int dims) {
/* 320 */     if ((rootComponent instanceof MultiType)) {
/* 321 */       return new MultiArrayType((MultiType)rootComponent, dims);
/*     */     }
/* 323 */     String name = arrayName(rootComponent.clazz.getName(), dims);
/*     */     Type type;
/*     */     try {
/* 327 */       type = get(getClassPool(rootComponent).get(name));
/*     */     } catch (NotFoundException e) {
/* 329 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 332 */     return type;
/*     */   }
/*     */ 
/*     */   String arrayName(String component, int dims)
/*     */   {
/* 338 */     int i = component.length();
/* 339 */     int size = i + dims * 2;
/* 340 */     char[] string = new char[size];
/* 341 */     component.getChars(0, i, string, 0);
/* 342 */     while (i < size) {
/* 343 */       string[(i++)] = '[';
/* 344 */       string[(i++)] = ']';
/*     */     }
/* 346 */     component = new String(string);
/* 347 */     return component;
/*     */   }
/*     */ 
/*     */   private ClassPool getClassPool(Type rootComponent) {
/* 351 */     ClassPool pool = rootComponent.clazz.getClassPool();
/* 352 */     return pool != null ? pool : ClassPool.getDefault();
/*     */   }
/*     */ 
/*     */   private Type mergeArray(Type type) {
/* 356 */     Type typeRoot = getRootComponent(type);
/* 357 */     Type thisRoot = getRootComponent(this);
/* 358 */     int typeDims = type.getDimensions();
/* 359 */     int thisDims = getDimensions();
/*     */ 
/* 362 */     if (typeDims == thisDims) {
/* 363 */       Type mergedComponent = thisRoot.merge(typeRoot);
/*     */ 
/* 367 */       if (mergedComponent == BOGUS) {
/* 368 */         return OBJECT;
/*     */       }
/* 370 */       return createArray(mergedComponent, thisDims);
/*     */     }
/*     */     int targetDims;
/*     */     Type targetRoot;
/*     */     int targetDims;
/* 376 */     if (typeDims < thisDims) {
/* 377 */       Type targetRoot = typeRoot;
/* 378 */       targetDims = typeDims;
/*     */     } else {
/* 380 */       targetRoot = thisRoot;
/* 381 */       targetDims = thisDims;
/*     */     }
/*     */ 
/* 385 */     if ((eq(CLONEABLE.clazz, targetRoot.clazz)) || (eq(SERIALIZABLE.clazz, targetRoot.clazz))) {
/* 386 */       return createArray(targetRoot, targetDims);
/*     */     }
/* 388 */     return createArray(OBJECT, targetDims);
/*     */   }
/*     */ 
/*     */   private static CtClass findCommonSuperClass(CtClass one, CtClass two) throws NotFoundException {
/* 392 */     CtClass deep = one;
/* 393 */     CtClass shallow = two;
/* 394 */     CtClass backupShallow = shallow;
/* 395 */     CtClass backupDeep = deep;
/*     */     while (true)
/*     */     {
/* 400 */       if ((eq(deep, shallow)) && (deep.getSuperclass() != null)) {
/* 401 */         return deep;
/*     */       }
/* 403 */       CtClass deepSuper = deep.getSuperclass();
/* 404 */       CtClass shallowSuper = shallow.getSuperclass();
/*     */ 
/* 406 */       if (shallowSuper == null)
/*     */       {
/* 408 */         shallow = backupShallow;
/* 409 */         break;
/*     */       }
/*     */ 
/* 412 */       if (deepSuper == null)
/*     */       {
/* 414 */         deep = backupDeep;
/* 415 */         backupDeep = backupShallow;
/* 416 */         backupShallow = deep;
/*     */ 
/* 418 */         deep = shallow;
/* 419 */         shallow = backupShallow;
/* 420 */         break;
/*     */       }
/*     */ 
/* 423 */       deep = deepSuper;
/* 424 */       shallow = shallowSuper;
/*     */     }
/*     */ 
/*     */     while (true)
/*     */     {
/* 429 */       deep = deep.getSuperclass();
/* 430 */       if (deep == null) {
/*     */         break;
/*     */       }
/* 433 */       backupDeep = backupDeep.getSuperclass();
/*     */     }
/*     */ 
/* 436 */     deep = backupDeep;
/*     */ 
/* 440 */     while (!eq(deep, shallow)) {
/* 441 */       deep = deep.getSuperclass();
/* 442 */       shallow = shallow.getSuperclass();
/*     */     }
/*     */ 
/* 445 */     return deep;
/*     */   }
/*     */ 
/*     */   private Type mergeClasses(Type type) throws NotFoundException {
/* 449 */     CtClass superClass = findCommonSuperClass(this.clazz, type.clazz);
/*     */ 
/* 452 */     if (superClass.getSuperclass() == null) {
/* 453 */       Map interfaces = findCommonInterfaces(type);
/* 454 */       if (interfaces.size() == 1)
/* 455 */         return new Type((CtClass)interfaces.values().iterator().next());
/* 456 */       if (interfaces.size() > 1) {
/* 457 */         return new MultiType(interfaces);
/*     */       }
/*     */ 
/* 460 */       return new Type(superClass);
/*     */     }
/*     */ 
/* 464 */     Map commonDeclared = findExclusiveDeclaredInterfaces(type, superClass);
/* 465 */     if (commonDeclared.size() > 0) {
/* 466 */       return new MultiType(commonDeclared, new Type(superClass));
/*     */     }
/*     */ 
/* 469 */     return new Type(superClass);
/*     */   }
/*     */ 
/*     */   private Map findCommonInterfaces(Type type) {
/* 473 */     Map typeMap = getAllInterfaces(type.clazz, null);
/* 474 */     Map thisMap = getAllInterfaces(this.clazz, null);
/*     */ 
/* 476 */     return findCommonInterfaces(typeMap, thisMap);
/*     */   }
/*     */ 
/*     */   private Map findExclusiveDeclaredInterfaces(Type type, CtClass exclude) {
/* 480 */     Map typeMap = getDeclaredInterfaces(type.clazz, null);
/* 481 */     Map thisMap = getDeclaredInterfaces(this.clazz, null);
/* 482 */     Map excludeMap = getAllInterfaces(exclude, null);
/*     */ 
/* 484 */     Iterator i = excludeMap.keySet().iterator();
/* 485 */     while (i.hasNext()) {
/* 486 */       Object intf = i.next();
/* 487 */       typeMap.remove(intf);
/* 488 */       thisMap.remove(intf);
/*     */     }
/*     */ 
/* 491 */     return findCommonInterfaces(typeMap, thisMap);
/*     */   }
/*     */ 
/*     */   Map findCommonInterfaces(Map typeMap, Map alterMap)
/*     */   {
/* 496 */     Iterator i = alterMap.keySet().iterator();
/* 497 */     while (i.hasNext()) {
/* 498 */       if (!typeMap.containsKey(i.next())) {
/* 499 */         i.remove();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 505 */     i = new ArrayList(alterMap.values()).iterator();
/* 506 */     while (i.hasNext()) { CtClass intf = (CtClass)i.next();
/*     */       CtClass[] interfaces;
/*     */       try {
/* 510 */         interfaces = intf.getInterfaces();
/*     */       } catch (NotFoundException e) {
/* 512 */         throw new RuntimeException(e);
/*     */       }
/*     */ 
/* 515 */       for (int c = 0; c < interfaces.length; c++) {
/* 516 */         alterMap.remove(interfaces[c].getName());
/*     */       }
/*     */     }
/* 519 */     return alterMap;
/*     */   }
/*     */ 
/*     */   Map getAllInterfaces(CtClass clazz, Map map) {
/* 523 */     if (map == null) {
/* 524 */       map = new HashMap();
/*     */     }
/* 526 */     if (clazz.isInterface())
/* 527 */       map.put(clazz.getName(), clazz);
/*     */     do
/*     */       try {
/* 530 */         CtClass[] interfaces = clazz.getInterfaces();
/* 531 */         for (int i = 0; i < interfaces.length; i++) {
/* 532 */           CtClass intf = interfaces[i];
/* 533 */           map.put(intf.getName(), intf);
/* 534 */           getAllInterfaces(intf, map);
/*     */         }
/*     */ 
/* 537 */         clazz = clazz.getSuperclass();
/*     */       } catch (NotFoundException e) {
/* 539 */         throw new RuntimeException(e);
/*     */       }
/* 541 */     while (clazz != null);
/*     */ 
/* 543 */     return map;
/*     */   }
/*     */ 
/*     */   Map getDeclaredInterfaces(CtClass clazz, Map map) {
/* 547 */     if (map == null) {
/* 548 */       map = new HashMap();
/*     */     }
/* 550 */     if (clazz.isInterface())
/* 551 */       map.put(clazz.getName(), clazz);
/*     */     CtClass[] interfaces;
/*     */     try
/*     */     {
/* 555 */       interfaces = clazz.getInterfaces();
/*     */     } catch (NotFoundException e) {
/* 557 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 560 */     for (int i = 0; i < interfaces.length; i++) {
/* 561 */       CtClass intf = interfaces[i];
/* 562 */       map.put(intf.getName(), intf);
/* 563 */       getDeclaredInterfaces(intf, map);
/*     */     }
/*     */ 
/* 566 */     return map;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 570 */     if (!(o instanceof Type)) {
/* 571 */       return false;
/*     */     }
/* 573 */     return (o.getClass() == getClass()) && (eq(this.clazz, ((Type)o).clazz));
/*     */   }
/*     */ 
/*     */   static boolean eq(CtClass one, CtClass two) {
/* 577 */     return (one == two) || ((one != null) && (two != null) && (one.getName().equals(two.getName())));
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 581 */     if (this == BOGUS)
/* 582 */       return "BOGUS";
/* 583 */     if (this == UNINIT)
/* 584 */       return "UNINIT";
/* 585 */     if (this == RETURN_ADDRESS)
/* 586 */       return "RETURN ADDRESS";
/* 587 */     if (this == TOP) {
/* 588 */       return "TOP";
/*     */     }
/* 590 */     return this.clazz == null ? "null" : this.clazz.getName();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 105 */     prims.put(CtClass.doubleType, DOUBLE);
/* 106 */     prims.put(CtClass.longType, LONG);
/* 107 */     prims.put(CtClass.charType, CHAR);
/* 108 */     prims.put(CtClass.shortType, SHORT);
/* 109 */     prims.put(CtClass.intType, INTEGER);
/* 110 */     prims.put(CtClass.floatType, FLOAT);
/* 111 */     prims.put(CtClass.byteType, BYTE);
/* 112 */     prims.put(CtClass.booleanType, BOOLEAN);
/* 113 */     prims.put(CtClass.voidType, VOID);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.analysis.Type
 * JD-Core Version:    0.6.2
 */