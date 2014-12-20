/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.ClassPool;
/*     */ import com.newrelic.javassist.bytecode.stackmap.MapMaker;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MethodInfo
/*     */ {
/*     */   ConstPool constPool;
/*     */   int accessFlags;
/*     */   int name;
/*     */   String cachedName;
/*     */   int descriptor;
/*     */   ArrayList attribute;
/*  46 */   public static boolean doPreverify = false;
/*     */   public static final String nameInit = "<init>";
/*     */   public static final String nameClinit = "<clinit>";
/*     */ 
/*     */   private MethodInfo(ConstPool cp)
/*     */   {
/*  60 */     this.constPool = cp;
/*  61 */     this.attribute = null;
/*     */   }
/*     */ 
/*     */   public MethodInfo(ConstPool cp, String methodname, String desc)
/*     */   {
/*  77 */     this(cp);
/*  78 */     this.accessFlags = 0;
/*  79 */     this.name = cp.addUtf8Info(methodname);
/*  80 */     this.cachedName = methodname;
/*  81 */     this.descriptor = this.constPool.addUtf8Info(desc);
/*     */   }
/*     */ 
/*     */   MethodInfo(ConstPool cp, DataInputStream in) throws IOException {
/*  85 */     this(cp);
/*  86 */     read(in);
/*     */   }
/*     */ 
/*     */   public MethodInfo(ConstPool cp, String methodname, MethodInfo src, Map classnameMap)
/*     */     throws BadBytecode
/*     */   {
/* 110 */     this(cp);
/* 111 */     read(src, methodname, classnameMap);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 118 */     return getName() + " " + getDescriptor();
/*     */   }
/*     */ 
/*     */   void compact(ConstPool cp)
/*     */   {
/* 130 */     this.name = cp.addUtf8Info(getName());
/* 131 */     this.descriptor = cp.addUtf8Info(getDescriptor());
/* 132 */     this.attribute = AttributeInfo.copyAll(this.attribute, cp);
/* 133 */     this.constPool = cp;
/*     */   }
/*     */ 
/*     */   void prune(ConstPool cp) {
/* 137 */     ArrayList newAttributes = new ArrayList();
/*     */ 
/* 139 */     AttributeInfo invisibleAnnotations = getAttribute("RuntimeInvisibleAnnotations");
/*     */ 
/* 141 */     if (invisibleAnnotations != null) {
/* 142 */       invisibleAnnotations = invisibleAnnotations.copy(cp, null);
/* 143 */       newAttributes.add(invisibleAnnotations);
/*     */     }
/*     */ 
/* 146 */     AttributeInfo visibleAnnotations = getAttribute("RuntimeVisibleAnnotations");
/*     */ 
/* 148 */     if (visibleAnnotations != null) {
/* 149 */       visibleAnnotations = visibleAnnotations.copy(cp, null);
/* 150 */       newAttributes.add(visibleAnnotations);
/*     */     }
/*     */ 
/* 153 */     AttributeInfo parameterInvisibleAnnotations = getAttribute("RuntimeInvisibleParameterAnnotations");
/*     */ 
/* 155 */     if (parameterInvisibleAnnotations != null) {
/* 156 */       parameterInvisibleAnnotations = parameterInvisibleAnnotations.copy(cp, null);
/* 157 */       newAttributes.add(parameterInvisibleAnnotations);
/*     */     }
/*     */ 
/* 160 */     AttributeInfo parameterVisibleAnnotations = getAttribute("RuntimeVisibleParameterAnnotations");
/*     */ 
/* 162 */     if (parameterVisibleAnnotations != null) {
/* 163 */       parameterVisibleAnnotations = parameterVisibleAnnotations.copy(cp, null);
/* 164 */       newAttributes.add(parameterVisibleAnnotations);
/*     */     }
/*     */ 
/* 167 */     AnnotationDefaultAttribute defaultAttribute = (AnnotationDefaultAttribute)getAttribute("AnnotationDefault");
/*     */ 
/* 169 */     if (defaultAttribute != null) {
/* 170 */       newAttributes.add(defaultAttribute);
/*     */     }
/* 172 */     ExceptionsAttribute ea = getExceptionsAttribute();
/* 173 */     if (ea != null) {
/* 174 */       newAttributes.add(ea);
/*     */     }
/* 176 */     AttributeInfo signature = getAttribute("Signature");
/*     */ 
/* 178 */     if (signature != null) {
/* 179 */       signature = signature.copy(cp, null);
/* 180 */       newAttributes.add(signature);
/*     */     }
/*     */ 
/* 183 */     this.attribute = newAttributes;
/* 184 */     this.name = cp.addUtf8Info(getName());
/* 185 */     this.descriptor = cp.addUtf8Info(getDescriptor());
/* 186 */     this.constPool = cp;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 193 */     if (this.cachedName == null) {
/* 194 */       this.cachedName = this.constPool.getUtf8Info(this.name);
/*     */     }
/* 196 */     return this.cachedName;
/*     */   }
/*     */ 
/*     */   public void setName(String newName)
/*     */   {
/* 203 */     this.name = this.constPool.addUtf8Info(newName);
/* 204 */     this.cachedName = newName;
/*     */   }
/*     */ 
/*     */   public boolean isMethod()
/*     */   {
/* 212 */     String n = getName();
/* 213 */     return (!n.equals("<init>")) && (!n.equals("<clinit>"));
/*     */   }
/*     */ 
/*     */   public ConstPool getConstPool()
/*     */   {
/* 220 */     return this.constPool;
/*     */   }
/*     */ 
/*     */   public boolean isConstructor()
/*     */   {
/* 227 */     return getName().equals("<init>");
/*     */   }
/*     */ 
/*     */   public boolean isStaticInitializer()
/*     */   {
/* 234 */     return getName().equals("<clinit>");
/*     */   }
/*     */ 
/*     */   public int getAccessFlags()
/*     */   {
/* 243 */     return this.accessFlags;
/*     */   }
/*     */ 
/*     */   public void setAccessFlags(int acc)
/*     */   {
/* 252 */     this.accessFlags = acc;
/*     */   }
/*     */ 
/*     */   public String getDescriptor()
/*     */   {
/* 261 */     return this.constPool.getUtf8Info(this.descriptor);
/*     */   }
/*     */ 
/*     */   public void setDescriptor(String desc)
/*     */   {
/* 270 */     if (!desc.equals(getDescriptor()))
/* 271 */       this.descriptor = this.constPool.addUtf8Info(desc);
/*     */   }
/*     */ 
/*     */   public List getAttributes()
/*     */   {
/* 285 */     if (this.attribute == null) {
/* 286 */       this.attribute = new ArrayList();
/*     */     }
/* 288 */     return this.attribute;
/*     */   }
/*     */ 
/*     */   public AttributeInfo getAttribute(String name)
/*     */   {
/* 300 */     return AttributeInfo.lookup(this.attribute, name);
/*     */   }
/*     */ 
/*     */   public void addAttribute(AttributeInfo info)
/*     */   {
/* 310 */     if (this.attribute == null) {
/* 311 */       this.attribute = new ArrayList();
/*     */     }
/* 313 */     AttributeInfo.remove(this.attribute, info.getName());
/* 314 */     this.attribute.add(info);
/*     */   }
/*     */ 
/*     */   public ExceptionsAttribute getExceptionsAttribute()
/*     */   {
/* 323 */     AttributeInfo info = AttributeInfo.lookup(this.attribute, "Exceptions");
/*     */ 
/* 325 */     return (ExceptionsAttribute)info;
/*     */   }
/*     */ 
/*     */   public CodeAttribute getCodeAttribute()
/*     */   {
/* 334 */     AttributeInfo info = AttributeInfo.lookup(this.attribute, "Code");
/* 335 */     return (CodeAttribute)info;
/*     */   }
/*     */ 
/*     */   public void removeExceptionsAttribute()
/*     */   {
/* 342 */     AttributeInfo.remove(this.attribute, "Exceptions");
/*     */   }
/*     */ 
/*     */   public void setExceptionsAttribute(ExceptionsAttribute cattr)
/*     */   {
/* 353 */     removeExceptionsAttribute();
/* 354 */     if (this.attribute == null) {
/* 355 */       this.attribute = new ArrayList();
/*     */     }
/* 357 */     this.attribute.add(cattr);
/*     */   }
/*     */ 
/*     */   public void removeCodeAttribute()
/*     */   {
/* 364 */     AttributeInfo.remove(this.attribute, "Code");
/*     */   }
/*     */ 
/*     */   public void setCodeAttribute(CodeAttribute cattr)
/*     */   {
/* 375 */     removeCodeAttribute();
/* 376 */     if (this.attribute == null) {
/* 377 */       this.attribute = new ArrayList();
/*     */     }
/* 379 */     this.attribute.add(cattr);
/*     */   }
/*     */ 
/*     */   public void rebuildStackMapIf6(ClassPool pool, ClassFile cf)
/*     */     throws BadBytecode
/*     */   {
/* 397 */     if (cf.getMajorVersion() >= 50) {
/* 398 */       rebuildStackMap(pool);
/*     */     }
/* 400 */     if (doPreverify)
/* 401 */       rebuildStackMapForME(pool);
/*     */   }
/*     */ 
/*     */   public void rebuildStackMap(ClassPool pool)
/*     */     throws BadBytecode
/*     */   {
/* 414 */     CodeAttribute ca = getCodeAttribute();
/* 415 */     if (ca != null) {
/* 416 */       StackMapTable smt = MapMaker.make(pool, this);
/* 417 */       ca.setAttribute(smt);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void rebuildStackMapForME(ClassPool pool)
/*     */     throws BadBytecode
/*     */   {
/* 431 */     CodeAttribute ca = getCodeAttribute();
/* 432 */     if (ca != null) {
/* 433 */       StackMap sm = MapMaker.make2(pool, this);
/* 434 */       ca.setAttribute(sm);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLineNumber(int pos)
/*     */   {
/* 448 */     CodeAttribute ca = getCodeAttribute();
/* 449 */     if (ca == null) {
/* 450 */       return -1;
/*     */     }
/* 452 */     LineNumberAttribute ainfo = (LineNumberAttribute)ca.getAttribute("LineNumberTable");
/*     */ 
/* 454 */     if (ainfo == null) {
/* 455 */       return -1;
/*     */     }
/* 457 */     return ainfo.toLineNumber(pos);
/*     */   }
/*     */ 
/*     */   public void setSuperclass(String superclass)
/*     */     throws BadBytecode
/*     */   {
/* 482 */     if (!isConstructor()) {
/* 483 */       return;
/*     */     }
/* 485 */     CodeAttribute ca = getCodeAttribute();
/* 486 */     byte[] code = ca.getCode();
/* 487 */     CodeIterator iterator = ca.iterator();
/* 488 */     int pos = iterator.skipSuperConstructor();
/* 489 */     if (pos >= 0) {
/* 490 */       ConstPool cp = this.constPool;
/* 491 */       int mref = ByteArray.readU16bit(code, pos + 1);
/* 492 */       int nt = cp.getMethodrefNameAndType(mref);
/* 493 */       int sc = cp.addClassInfo(superclass);
/* 494 */       int mref2 = cp.addMethodrefInfo(sc, nt);
/* 495 */       ByteArray.write16bit(mref2, code, pos + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void read(MethodInfo src, String methodname, Map classnames) throws BadBytecode
/*     */   {
/* 501 */     ConstPool destCp = this.constPool;
/* 502 */     this.accessFlags = src.accessFlags;
/* 503 */     this.name = destCp.addUtf8Info(methodname);
/* 504 */     this.cachedName = methodname;
/* 505 */     ConstPool srcCp = src.constPool;
/* 506 */     String desc = srcCp.getUtf8Info(src.descriptor);
/* 507 */     String desc2 = Descriptor.rename(desc, classnames);
/* 508 */     this.descriptor = destCp.addUtf8Info(desc2);
/*     */ 
/* 510 */     this.attribute = new ArrayList();
/* 511 */     ExceptionsAttribute eattr = src.getExceptionsAttribute();
/* 512 */     if (eattr != null) {
/* 513 */       this.attribute.add(eattr.copy(destCp, classnames));
/*     */     }
/* 515 */     CodeAttribute cattr = src.getCodeAttribute();
/* 516 */     if (cattr != null)
/* 517 */       this.attribute.add(cattr.copy(destCp, classnames));
/*     */   }
/*     */ 
/*     */   private void read(DataInputStream in) throws IOException {
/* 521 */     this.accessFlags = in.readUnsignedShort();
/* 522 */     this.name = in.readUnsignedShort();
/* 523 */     this.descriptor = in.readUnsignedShort();
/* 524 */     int n = in.readUnsignedShort();
/* 525 */     this.attribute = new ArrayList();
/* 526 */     for (int i = 0; i < n; i++)
/* 527 */       this.attribute.add(AttributeInfo.read(this.constPool, in));
/*     */   }
/*     */ 
/*     */   void write(DataOutputStream out) throws IOException {
/* 531 */     out.writeShort(this.accessFlags);
/* 532 */     out.writeShort(this.name);
/* 533 */     out.writeShort(this.descriptor);
/*     */ 
/* 535 */     if (this.attribute == null) {
/* 536 */       out.writeShort(0);
/*     */     } else {
/* 538 */       out.writeShort(this.attribute.size());
/* 539 */       AttributeInfo.writeAll(this.attribute, out);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.MethodInfo
 * JD-Core Version:    0.6.2
 */