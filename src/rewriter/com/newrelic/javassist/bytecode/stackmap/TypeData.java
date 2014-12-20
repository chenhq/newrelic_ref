/*     */ package com.newrelic.javassist.bytecode.stackmap;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.CtClass;
/*     */ import com.newrelic.javassist.NotFoundException;
/*     */ import com.newrelic.javassist.bytecode.BadBytecode;
/*     */ import com.newrelic.javassist.bytecode.ConstPool;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public abstract class TypeData
/*     */ {
/*     */   public abstract void merge(TypeData paramTypeData);
/*     */ 
/*     */   static void setType(TypeData td, String className, ClassPool cp)
/*     */     throws BadBytecode
/*     */   {
/*  43 */     if (td == TypeTag.TOP) {
/*  44 */       throw new BadBytecode("unset variable");
/*     */     }
/*  46 */     td.setType(className, cp);
/*     */   }
/*     */ 
/*     */   public abstract boolean equals(Object paramObject);
/*     */ 
/*     */   public abstract int getTypeTag();
/*     */ 
/*     */   public abstract int getTypeData(ConstPool paramConstPool);
/*     */ 
/*     */   public TypeData getSelf()
/*     */   {
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */   public abstract TypeData copy();
/*     */ 
/*     */   public abstract boolean isObjectType();
/*     */ 
/*     */   public boolean is2WordType() {
/*  65 */     return false; } 
/*  66 */   public boolean isNullType() { return false; }
/*     */ 
/*     */ 
/*     */   public abstract String getName()
/*     */     throws BadBytecode;
/*     */ 
/*     */   protected abstract void setType(String paramString, ClassPool paramClassPool)
/*     */     throws BadBytecode;
/*     */ 
/*     */   public abstract void evalExpectedType(ClassPool paramClassPool)
/*     */     throws BadBytecode;
/*     */ 
/*     */   public abstract String getExpected()
/*     */     throws BadBytecode;
/*     */ 
/*     */   public static class UninitThis extends TypeData.UninitData
/*     */   {
/*     */     UninitThis(String className)
/*     */     {
/* 497 */       super(className);
/*     */     }
/*     */     public int getTypeTag() {
/* 500 */       return 6; } 
/* 501 */     public int getTypeData(ConstPool cp) { return 0; }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 504 */       return obj instanceof UninitThis;
/*     */     }
/*     */     public String toString() {
/* 507 */       return "uninit:this";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class UninitData extends TypeData
/*     */   {
/*     */     String className;
/*     */     int offset;
/*     */     boolean initialized;
/*     */ 
/*     */     UninitData(int offset, String className)
/*     */     {
/* 448 */       this.className = className;
/* 449 */       this.offset = offset;
/* 450 */       this.initialized = false;
/*     */     }
/*     */     public void merge(TypeData neighbor) {
/*     */     }
/*     */     public int getTypeTag() {
/* 455 */       return 8; } 
/* 456 */     public int getTypeData(ConstPool cp) { return this.offset; }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 459 */       if ((obj instanceof UninitData)) {
/* 460 */         UninitData ud = (UninitData)obj;
/* 461 */         return (this.offset == ud.offset) && (this.className.equals(ud.className));
/*     */       }
/*     */ 
/* 464 */       return false;
/*     */     }
/*     */ 
/*     */     public TypeData getSelf() {
/* 468 */       if (this.initialized) {
/* 469 */         return copy();
/*     */       }
/* 471 */       return this;
/*     */     }
/*     */ 
/*     */     public TypeData copy() {
/* 475 */       return new TypeData.ClassName(this.className);
/*     */     }
/*     */     public boolean isObjectType() {
/* 478 */       return true;
/*     */     }
/*     */     protected void setType(String typeName, ClassPool cp) throws BadBytecode {
/* 481 */       this.initialized = true;
/*     */     }
/*     */     public void evalExpectedType(ClassPool cp) throws BadBytecode {
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 487 */       return this.className;
/*     */     }
/*     */     public String getExpected() {
/* 490 */       return this.className;
/*     */     }
/* 492 */     public String toString() { return "uninit:" + this.className + "@" + this.offset; }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static class ArrayElement extends TypeData.TypeName
/*     */   {
/*     */     TypeData array;
/*     */ 
/*     */     public ArrayElement(TypeData a)
/*     */     {
/* 395 */       this.array = a;
/*     */     }
/*     */ 
/*     */     public TypeData copy() {
/* 399 */       return new ArrayElement(this.array);
/*     */     }
/*     */ 
/*     */     protected void setType(String typeName, ClassPool cp) throws BadBytecode {
/* 403 */       super.setType(typeName, cp);
/* 404 */       this.array.setType(getArrayType(typeName), cp);
/*     */     }
/*     */ 
/*     */     public String getName() throws BadBytecode {
/* 408 */       String name = this.array.getName();
/* 409 */       if ((name.length() > 1) && (name.charAt(0) == '[')) {
/* 410 */         char c = name.charAt(1);
/* 411 */         if (c == 'L')
/* 412 */           return name.substring(2, name.length() - 1).replace('/', '.');
/* 413 */         if (c == '[') {
/* 414 */           return name.substring(1);
/*     */         }
/*     */       }
/* 417 */       throw new BadBytecode("bad array type for AALOAD: " + name);
/*     */     }
/*     */ 
/*     */     public static String getArrayType(String elementType)
/*     */     {
/* 422 */       if (elementType.charAt(0) == '[') {
/* 423 */         return "[" + elementType;
/*     */       }
/* 425 */       return "[L" + elementType.replace('.', '/') + ";";
/*     */     }
/*     */ 
/*     */     public static String getElementType(String arrayType) {
/* 429 */       char c = arrayType.charAt(1);
/* 430 */       if (c == 'L')
/* 431 */         return arrayType.substring(2, arrayType.length() - 1).replace('/', '.');
/* 432 */       if (c == '[') {
/* 433 */         return arrayType.substring(1);
/*     */       }
/* 435 */       return arrayType;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NullType extends TypeData.ClassName
/*     */   {
/*     */     public NullType()
/*     */     {
/* 345 */       super();
/*     */     }
/*     */ 
/*     */     public TypeData copy() {
/* 349 */       return new NullType();
/*     */     }
/*     */     public boolean isNullType() {
/* 352 */       return true;
/*     */     }
/*     */     public int getTypeTag() {
/*     */       try {
/* 356 */         if ("null".equals(getExpected())) {
/* 357 */           return 5;
/*     */         }
/* 359 */         return super.getTypeTag();
/*     */       }
/*     */       catch (BadBytecode e) {
/* 362 */         throw new RuntimeException("fatal error: ", e);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected int getTypeData2(ConstPool cp, String type) {
/* 367 */       if ("null".equals(type)) {
/* 368 */         return 0;
/*     */       }
/* 370 */       return super.getTypeData2(cp, type);
/*     */     }
/*     */ 
/*     */     public String getExpected() throws BadBytecode {
/* 374 */       String en = this.expectedName;
/* 375 */       if (en == null)
/*     */       {
/* 380 */         return "java.lang.Object";
/*     */       }
/*     */ 
/* 383 */       return en;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ClassName extends TypeData.TypeName
/*     */   {
/*     */     private String name;
/*     */ 
/*     */     public ClassName(String n)
/*     */     {
/* 326 */       this.name = n;
/*     */     }
/*     */ 
/*     */     public TypeData copy() {
/* 330 */       return new ClassName(this.name);
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 334 */       return this.name;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static abstract class TypeName extends TypeData
/*     */   {
/*     */     protected ArrayList equivalences;
/*     */     protected String expectedName;
/*     */     private CtClass cache;
/*     */     private boolean evalDone;
/*     */ 
/*     */     protected TypeName()
/*     */     {
/* 130 */       this.equivalences = new ArrayList();
/* 131 */       this.equivalences.add(this);
/* 132 */       this.expectedName = null;
/* 133 */       this.cache = null;
/* 134 */       this.evalDone = false;
/*     */     }
/*     */ 
/*     */     public void merge(TypeData neighbor) {
/* 138 */       if (this == neighbor) {
/* 139 */         return;
/*     */       }
/* 141 */       if (!(neighbor instanceof TypeName)) {
/* 142 */         return;
/*     */       }
/* 144 */       TypeName neighbor2 = (TypeName)neighbor;
/* 145 */       ArrayList list = this.equivalences;
/* 146 */       ArrayList list2 = neighbor2.equivalences;
/* 147 */       if (list == list2) {
/* 148 */         return;
/*     */       }
/* 150 */       int n = list2.size();
/* 151 */       for (int i = 0; i < n; i++) {
/* 152 */         TypeName tn = (TypeName)list2.get(i);
/* 153 */         add(list, tn);
/* 154 */         tn.equivalences = list;
/*     */       }
/*     */     }
/*     */ 
/*     */     private static void add(ArrayList list, TypeData td) {
/* 159 */       int n = list.size();
/* 160 */       for (int i = 0; i < n; i++) {
/* 161 */         if (list.get(i) == td)
/* 162 */           return;
/*     */       }
/* 164 */       list.add(td);
/*     */     }
/*     */ 
/*     */     public int getTypeTag()
/*     */     {
/* 169 */       return 7;
/*     */     }
/*     */     public int getTypeData(ConstPool cp) {
/*     */       String type;
/*     */       try {
/* 174 */         type = getExpected();
/*     */       } catch (BadBytecode e) {
/* 176 */         throw new RuntimeException("fatal error: ", e);
/*     */       }
/*     */ 
/* 179 */       return getTypeData2(cp, type);
/*     */     }
/*     */ 
/*     */     protected int getTypeData2(ConstPool cp, String type)
/*     */     {
/* 185 */       return cp.addClassInfo(type);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 189 */       if ((obj instanceof TypeName))
/*     */         try {
/* 191 */           TypeName tn = (TypeName)obj;
/* 192 */           return getExpected().equals(tn.getExpected());
/*     */         }
/*     */         catch (BadBytecode e)
/*     */         {
/*     */         }
/* 197 */       return false;
/*     */     }
/*     */     public boolean isObjectType() {
/* 200 */       return true;
/*     */     }
/*     */     protected void setType(String typeName, ClassPool cp) throws BadBytecode {
/* 203 */       if (update(cp, this.expectedName, typeName))
/* 204 */         this.expectedName = typeName;
/*     */     }
/*     */ 
/*     */     public void evalExpectedType(ClassPool cp) throws BadBytecode {
/* 208 */       if (this.evalDone) {
/* 209 */         return;
/*     */       }
/* 211 */       ArrayList equiv = this.equivalences;
/* 212 */       int n = equiv.size();
/* 213 */       String name = evalExpectedType2(equiv, n);
/* 214 */       if (name == null) {
/* 215 */         name = this.expectedName;
/* 216 */         for (int i = 0; i < n; i++) {
/* 217 */           TypeData td = (TypeData)equiv.get(i);
/* 218 */           if ((td instanceof TypeName)) {
/* 219 */             TypeName tn = (TypeName)td;
/* 220 */             if (update(cp, name, tn.expectedName)) {
/* 221 */               name = tn.expectedName;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 226 */       for (int i = 0; i < n; i++) {
/* 227 */         TypeData td = (TypeData)equiv.get(i);
/* 228 */         if ((td instanceof TypeName)) {
/* 229 */           TypeName tn = (TypeName)td;
/* 230 */           tn.expectedName = name;
/* 231 */           tn.cache = null;
/* 232 */           tn.evalDone = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private String evalExpectedType2(ArrayList equiv, int n) throws BadBytecode {
/* 238 */       String origName = null;
/* 239 */       for (int i = 0; i < n; i++) {
/* 240 */         TypeData td = (TypeData)equiv.get(i);
/* 241 */         if (!td.isNullType()) {
/* 242 */           if (origName == null)
/* 243 */             origName = td.getName();
/* 244 */           else if (!origName.equals(td.getName()))
/* 245 */             return null;
/*     */         }
/*     */       }
/* 248 */       return origName;
/*     */     }
/*     */     protected boolean isTypeName() {
/* 251 */       return true;
/*     */     }
/*     */     private boolean update(ClassPool cp, String oldName, String typeName) throws BadBytecode {
/* 254 */       if (typeName == null)
/* 255 */         return false;
/* 256 */       if (oldName == null)
/* 257 */         return true;
/* 258 */       if (oldName.equals(typeName))
/* 259 */         return false;
/* 260 */       if ((typeName.charAt(0) == '[') && (oldName.equals("[Ljava.lang.Object;")))
/*     */       {
/* 266 */         return true;
/*     */       }
/*     */       try
/*     */       {
/* 270 */         if (this.cache == null) {
/* 271 */           this.cache = cp.get(oldName);
/*     */         }
/* 273 */         CtClass cache2 = cp.get(typeName);
/* 274 */         if (cache2.subtypeOf(this.cache)) {
/* 275 */           this.cache = cache2;
/* 276 */           return true;
/*     */         }
/*     */ 
/* 279 */         return false;
/*     */       }
/*     */       catch (NotFoundException e) {
/* 282 */         throw new BadBytecode("cannot find " + e.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*     */     public String getExpected()
/*     */       throws BadBytecode
/*     */     {
/* 289 */       ArrayList equiv = this.equivalences;
/* 290 */       if (equiv.size() == 1) {
/* 291 */         return getName();
/*     */       }
/* 293 */       String en = this.expectedName;
/* 294 */       if (en == null) {
/* 295 */         return "java.lang.Object";
/*     */       }
/* 297 */       return en;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*     */       try {
/* 303 */         String en = this.expectedName;
/* 304 */         if (en != null) {
/* 305 */           return en;
/*     */         }
/* 307 */         String name = getName();
/* 308 */         if (this.equivalences.size() == 1) {
/* 309 */           return name;
/*     */         }
/* 311 */         return name + "?";
/*     */       }
/*     */       catch (BadBytecode e) {
/* 314 */         return "<" + e.getMessage() + ">";
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class BasicType extends TypeData
/*     */   {
/*     */     private String name;
/*     */     private int typeTag;
/*     */ 
/*     */     public BasicType(String type, int tag)
/*     */     {
/*  81 */       this.name = type;
/*  82 */       this.typeTag = tag;
/*     */     }
/*     */     public void merge(TypeData neighbor) {
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/*  88 */       return this == obj;
/*     */     }
/*     */     public int getTypeTag() {
/*  91 */       return this.typeTag; } 
/*  92 */     public int getTypeData(ConstPool cp) { return 0; } 
/*     */     public boolean isObjectType() {
/*  94 */       return false;
/*     */     }
/*     */     public boolean is2WordType() {
/*  97 */       return (this.typeTag == 4) || (this.typeTag == 3);
/*     */     }
/*     */ 
/*     */     public TypeData copy()
/*     */     {
/* 102 */       return this;
/*     */     }
/*     */     public void evalExpectedType(ClassPool cp) throws BadBytecode {
/*     */     }
/*     */ 
/*     */     public String getExpected() throws BadBytecode {
/* 108 */       return this.name;
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 112 */       return this.name;
/*     */     }
/*     */ 
/*     */     protected void setType(String s, ClassPool cp) throws BadBytecode {
/* 116 */       throw new BadBytecode("conflict: " + this.name + " and " + s);
/*     */     }
/*     */     public String toString() {
/* 119 */       return this.name;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.stackmap.TypeData
 * JD-Core Version:    0.6.2
 */