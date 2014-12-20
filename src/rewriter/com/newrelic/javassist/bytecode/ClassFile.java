/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class ClassFile
/*     */ {
/*     */   int major;
/*     */   int minor;
/*     */   ConstPool constPool;
/*     */   int thisClass;
/*     */   int accessFlags;
/*     */   int superClass;
/*     */   int[] interfaces;
/*     */   ArrayList fields;
/*     */   ArrayList methods;
/*     */   ArrayList attributes;
/*     */   String thisclassname;
/*     */   String[] cachedInterfaces;
/*     */   String cachedSuperclass;
/*     */   public static final int JAVA_1 = 45;
/*     */   public static final int JAVA_2 = 46;
/*     */   public static final int JAVA_3 = 47;
/*     */   public static final int JAVA_4 = 48;
/*     */   public static final int JAVA_5 = 49;
/*     */   public static final int JAVA_6 = 50;
/*     */   public static final int JAVA_7 = 51;
/*  94 */   public static int MAJOR_VERSION = 47;
/*     */ 
/*     */   public ClassFile(DataInputStream in)
/*     */     throws IOException
/*     */   {
/* 108 */     read(in);
/*     */   }
/*     */ 
/*     */   public ClassFile(boolean isInterface, String classname, String superclass)
/*     */   {
/* 122 */     this.major = MAJOR_VERSION;
/* 123 */     this.minor = 0;
/* 124 */     this.constPool = new ConstPool(classname);
/* 125 */     this.thisClass = this.constPool.getThisClassInfo();
/* 126 */     if (isInterface)
/* 127 */       this.accessFlags = 1536;
/*     */     else {
/* 129 */       this.accessFlags = 32;
/*     */     }
/* 131 */     initSuperclass(superclass);
/* 132 */     this.interfaces = null;
/* 133 */     this.fields = new ArrayList();
/* 134 */     this.methods = new ArrayList();
/* 135 */     this.thisclassname = classname;
/*     */ 
/* 137 */     this.attributes = new ArrayList();
/* 138 */     this.attributes.add(new SourceFileAttribute(this.constPool, getSourcefileName(this.thisclassname)));
/*     */   }
/*     */ 
/*     */   private void initSuperclass(String superclass)
/*     */   {
/* 143 */     if (superclass != null) {
/* 144 */       this.superClass = this.constPool.addClassInfo(superclass);
/* 145 */       this.cachedSuperclass = superclass;
/*     */     }
/*     */     else {
/* 148 */       this.superClass = this.constPool.addClassInfo("java.lang.Object");
/* 149 */       this.cachedSuperclass = "java.lang.Object";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getSourcefileName(String qname) {
/* 154 */     int index = qname.lastIndexOf('.');
/* 155 */     if (index >= 0) {
/* 156 */       qname = qname.substring(index + 1);
/*     */     }
/* 158 */     return qname + ".java";
/*     */   }
/*     */ 
/*     */   public void compact()
/*     */   {
/* 167 */     ConstPool cp = compact0();
/* 168 */     ArrayList list = this.methods;
/* 169 */     int n = list.size();
/* 170 */     for (int i = 0; i < n; i++) {
/* 171 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 172 */       minfo.compact(cp);
/*     */     }
/*     */ 
/* 175 */     list = this.fields;
/* 176 */     n = list.size();
/* 177 */     for (int i = 0; i < n; i++) {
/* 178 */       FieldInfo finfo = (FieldInfo)list.get(i);
/* 179 */       finfo.compact(cp);
/*     */     }
/*     */ 
/* 182 */     this.attributes = AttributeInfo.copyAll(this.attributes, cp);
/* 183 */     this.constPool = cp;
/*     */   }
/*     */ 
/*     */   private ConstPool compact0() {
/* 187 */     ConstPool cp = new ConstPool(this.thisclassname);
/* 188 */     this.thisClass = cp.getThisClassInfo();
/* 189 */     String sc = getSuperclass();
/* 190 */     if (sc != null) {
/* 191 */       this.superClass = cp.addClassInfo(getSuperclass());
/*     */     }
/* 193 */     if (this.interfaces != null) {
/* 194 */       int n = this.interfaces.length;
/* 195 */       for (int i = 0; i < n; i++) {
/* 196 */         this.interfaces[i] = cp.addClassInfo(this.constPool.getClassInfo(this.interfaces[i]));
/*     */       }
/*     */     }
/*     */ 
/* 200 */     return cp;
/*     */   }
/*     */ 
/*     */   public void prune()
/*     */   {
/* 210 */     ConstPool cp = compact0();
/* 211 */     ArrayList newAttributes = new ArrayList();
/* 212 */     AttributeInfo invisibleAnnotations = getAttribute("RuntimeInvisibleAnnotations");
/*     */ 
/* 214 */     if (invisibleAnnotations != null) {
/* 215 */       invisibleAnnotations = invisibleAnnotations.copy(cp, null);
/* 216 */       newAttributes.add(invisibleAnnotations);
/*     */     }
/*     */ 
/* 219 */     AttributeInfo visibleAnnotations = getAttribute("RuntimeVisibleAnnotations");
/*     */ 
/* 221 */     if (visibleAnnotations != null) {
/* 222 */       visibleAnnotations = visibleAnnotations.copy(cp, null);
/* 223 */       newAttributes.add(visibleAnnotations);
/*     */     }
/*     */ 
/* 226 */     AttributeInfo signature = getAttribute("Signature");
/*     */ 
/* 228 */     if (signature != null) {
/* 229 */       signature = signature.copy(cp, null);
/* 230 */       newAttributes.add(signature);
/*     */     }
/*     */ 
/* 233 */     ArrayList list = this.methods;
/* 234 */     int n = list.size();
/* 235 */     for (int i = 0; i < n; i++) {
/* 236 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 237 */       minfo.prune(cp);
/*     */     }
/*     */ 
/* 240 */     list = this.fields;
/* 241 */     n = list.size();
/* 242 */     for (int i = 0; i < n; i++) {
/* 243 */       FieldInfo finfo = (FieldInfo)list.get(i);
/* 244 */       finfo.prune(cp);
/*     */     }
/*     */ 
/* 247 */     this.attributes = newAttributes;
/* 248 */     this.constPool = cp;
/*     */   }
/*     */ 
/*     */   public ConstPool getConstPool()
/*     */   {
/* 255 */     return this.constPool;
/*     */   }
/*     */ 
/*     */   public boolean isInterface()
/*     */   {
/* 262 */     return (this.accessFlags & 0x200) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isFinal()
/*     */   {
/* 269 */     return (this.accessFlags & 0x10) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isAbstract()
/*     */   {
/* 276 */     return (this.accessFlags & 0x400) != 0;
/*     */   }
/*     */ 
/*     */   public int getAccessFlags()
/*     */   {
/* 285 */     return this.accessFlags;
/*     */   }
/*     */ 
/*     */   public void setAccessFlags(int acc)
/*     */   {
/* 294 */     if ((acc & 0x200) == 0) {
/* 295 */       acc |= 32;
/*     */     }
/* 297 */     this.accessFlags = acc;
/*     */   }
/*     */ 
/*     */   public int getInnerAccessFlags()
/*     */   {
/* 309 */     InnerClassesAttribute ica = (InnerClassesAttribute)getAttribute("InnerClasses");
/*     */ 
/* 311 */     if (ica == null) {
/* 312 */       return -1;
/*     */     }
/* 314 */     String name = getName();
/* 315 */     int n = ica.tableLength();
/* 316 */     for (int i = 0; i < n; i++) {
/* 317 */       if (name.equals(ica.innerClass(i)))
/* 318 */         return ica.accessFlags(i);
/*     */     }
/* 320 */     return -1;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 327 */     return this.thisclassname;
/*     */   }
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 335 */     renameClass(this.thisclassname, name);
/*     */   }
/*     */ 
/*     */   public String getSuperclass()
/*     */   {
/* 342 */     if (this.cachedSuperclass == null) {
/* 343 */       this.cachedSuperclass = this.constPool.getClassInfo(this.superClass);
/*     */     }
/* 345 */     return this.cachedSuperclass;
/*     */   }
/*     */ 
/*     */   public int getSuperclassId()
/*     */   {
/* 353 */     return this.superClass;
/*     */   }
/*     */ 
/*     */   public void setSuperclass(String superclass)
/*     */     throws CannotCompileException
/*     */   {
/* 365 */     if (superclass == null)
/* 366 */       superclass = "java.lang.Object";
/*     */     try
/*     */     {
/* 369 */       this.superClass = this.constPool.addClassInfo(superclass);
/* 370 */       ArrayList list = this.methods;
/* 371 */       int n = list.size();
/* 372 */       for (int i = 0; i < n; i++) {
/* 373 */         MethodInfo minfo = (MethodInfo)list.get(i);
/* 374 */         minfo.setSuperclass(superclass);
/*     */       }
/*     */     }
/*     */     catch (BadBytecode e) {
/* 378 */       throw new CannotCompileException(e);
/*     */     }
/* 380 */     this.cachedSuperclass = superclass;
/*     */   }
/*     */ 
/*     */   public final void renameClass(String oldname, String newname)
/*     */   {
/* 401 */     if (oldname.equals(newname)) {
/* 402 */       return;
/*     */     }
/* 404 */     if (oldname.equals(this.thisclassname)) {
/* 405 */       this.thisclassname = newname;
/*     */     }
/* 407 */     oldname = Descriptor.toJvmName(oldname);
/* 408 */     newname = Descriptor.toJvmName(newname);
/* 409 */     this.constPool.renameClass(oldname, newname);
/*     */ 
/* 411 */     AttributeInfo.renameClass(this.attributes, oldname, newname);
/* 412 */     ArrayList list = this.methods;
/* 413 */     int n = list.size();
/* 414 */     for (int i = 0; i < n; i++) {
/* 415 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 416 */       String desc = minfo.getDescriptor();
/* 417 */       minfo.setDescriptor(Descriptor.rename(desc, oldname, newname));
/* 418 */       AttributeInfo.renameClass(minfo.getAttributes(), oldname, newname);
/*     */     }
/*     */ 
/* 421 */     list = this.fields;
/* 422 */     n = list.size();
/* 423 */     for (int i = 0; i < n; i++) {
/* 424 */       FieldInfo finfo = (FieldInfo)list.get(i);
/* 425 */       String desc = finfo.getDescriptor();
/* 426 */       finfo.setDescriptor(Descriptor.rename(desc, oldname, newname));
/* 427 */       AttributeInfo.renameClass(finfo.getAttributes(), oldname, newname);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void renameClass(Map classnames)
/*     */   {
/* 441 */     String jvmNewThisName = (String)classnames.get(Descriptor.toJvmName(this.thisclassname));
/*     */ 
/* 443 */     if (jvmNewThisName != null) {
/* 444 */       this.thisclassname = Descriptor.toJavaName(jvmNewThisName);
/*     */     }
/* 446 */     this.constPool.renameClass(classnames);
/*     */ 
/* 448 */     AttributeInfo.renameClass(this.attributes, classnames);
/* 449 */     ArrayList list = this.methods;
/* 450 */     int n = list.size();
/* 451 */     for (int i = 0; i < n; i++) {
/* 452 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 453 */       String desc = minfo.getDescriptor();
/* 454 */       minfo.setDescriptor(Descriptor.rename(desc, classnames));
/* 455 */       AttributeInfo.renameClass(minfo.getAttributes(), classnames);
/*     */     }
/*     */ 
/* 458 */     list = this.fields;
/* 459 */     n = list.size();
/* 460 */     for (int i = 0; i < n; i++) {
/* 461 */       FieldInfo finfo = (FieldInfo)list.get(i);
/* 462 */       String desc = finfo.getDescriptor();
/* 463 */       finfo.setDescriptor(Descriptor.rename(desc, classnames));
/* 464 */       AttributeInfo.renameClass(finfo.getAttributes(), classnames);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getInterfaces()
/*     */   {
/* 473 */     if (this.cachedInterfaces != null) {
/* 474 */       return this.cachedInterfaces;
/*     */     }
/* 476 */     String[] rtn = null;
/* 477 */     if (this.interfaces == null) {
/* 478 */       rtn = new String[0];
/*     */     } else {
/* 480 */       int n = this.interfaces.length;
/* 481 */       String[] list = new String[n];
/* 482 */       for (int i = 0; i < n; i++) {
/* 483 */         list[i] = this.constPool.getClassInfo(this.interfaces[i]);
/*     */       }
/* 485 */       rtn = list;
/*     */     }
/*     */ 
/* 488 */     this.cachedInterfaces = rtn;
/* 489 */     return rtn;
/*     */   }
/*     */ 
/*     */   public void setInterfaces(String[] nameList)
/*     */   {
/* 499 */     this.cachedInterfaces = null;
/* 500 */     if (nameList != null) {
/* 501 */       int n = nameList.length;
/* 502 */       this.interfaces = new int[n];
/* 503 */       for (int i = 0; i < n; i++)
/* 504 */         this.interfaces[i] = this.constPool.addClassInfo(nameList[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addInterface(String name)
/*     */   {
/* 512 */     this.cachedInterfaces = null;
/* 513 */     int info = this.constPool.addClassInfo(name);
/* 514 */     if (this.interfaces == null) {
/* 515 */       this.interfaces = new int[1];
/* 516 */       this.interfaces[0] = info;
/*     */     }
/*     */     else {
/* 519 */       int n = this.interfaces.length;
/* 520 */       int[] newarray = new int[n + 1];
/* 521 */       System.arraycopy(this.interfaces, 0, newarray, 0, n);
/* 522 */       newarray[n] = info;
/* 523 */       this.interfaces = newarray;
/*     */     }
/*     */   }
/*     */ 
/*     */   public List getFields()
/*     */   {
/* 534 */     return this.fields;
/*     */   }
/*     */ 
/*     */   public void addField(FieldInfo finfo)
/*     */     throws DuplicateMemberException
/*     */   {
/* 543 */     testExistingField(finfo.getName(), finfo.getDescriptor());
/* 544 */     this.fields.add(finfo);
/*     */   }
/*     */ 
/*     */   public final void addField2(FieldInfo finfo)
/*     */   {
/* 556 */     this.fields.add(finfo);
/*     */   }
/*     */ 
/*     */   private void testExistingField(String name, String descriptor) throws DuplicateMemberException
/*     */   {
/* 561 */     ListIterator it = this.fields.listIterator(0);
/* 562 */     while (it.hasNext()) {
/* 563 */       FieldInfo minfo = (FieldInfo)it.next();
/* 564 */       if (minfo.getName().equals(name))
/* 565 */         throw new DuplicateMemberException("duplicate field: " + name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List getMethods()
/*     */   {
/* 576 */     return this.methods;
/*     */   }
/*     */ 
/*     */   public MethodInfo getMethod(String name)
/*     */   {
/* 586 */     ArrayList list = this.methods;
/* 587 */     int n = list.size();
/* 588 */     for (int i = 0; i < n; i++) {
/* 589 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 590 */       if (minfo.getName().equals(name)) {
/* 591 */         return minfo;
/*     */       }
/*     */     }
/* 594 */     return null;
/*     */   }
/*     */ 
/*     */   public MethodInfo getStaticInitializer()
/*     */   {
/* 602 */     return getMethod("<clinit>");
/*     */   }
/*     */ 
/*     */   public void addMethod(MethodInfo minfo)
/*     */     throws DuplicateMemberException
/*     */   {
/* 613 */     testExistingMethod(minfo);
/* 614 */     this.methods.add(minfo);
/*     */   }
/*     */ 
/*     */   public final void addMethod2(MethodInfo minfo)
/*     */   {
/* 626 */     this.methods.add(minfo);
/*     */   }
/*     */ 
/*     */   private void testExistingMethod(MethodInfo newMinfo)
/*     */     throws DuplicateMemberException
/*     */   {
/* 632 */     String name = newMinfo.getName();
/* 633 */     String descriptor = newMinfo.getDescriptor();
/* 634 */     ListIterator it = this.methods.listIterator(0);
/* 635 */     while (it.hasNext())
/* 636 */       if (isDuplicated(newMinfo, name, descriptor, (MethodInfo)it.next(), it))
/* 637 */         throw new DuplicateMemberException("duplicate method: " + name + " in " + getName());
/*     */   }
/*     */ 
/*     */   private static boolean isDuplicated(MethodInfo newMethod, String newName, String newDesc, MethodInfo minfo, ListIterator it)
/*     */   {
/* 645 */     if (!minfo.getName().equals(newName)) {
/* 646 */       return false;
/*     */     }
/* 648 */     String desc = minfo.getDescriptor();
/* 649 */     if (!Descriptor.eqParamTypes(desc, newDesc)) {
/* 650 */       return false;
/*     */     }
/* 652 */     if (desc.equals(newDesc)) {
/* 653 */       if (notBridgeMethod(minfo)) {
/* 654 */         return true;
/*     */       }
/* 656 */       it.remove();
/* 657 */       return false;
/*     */     }
/*     */ 
/* 661 */     return (notBridgeMethod(minfo)) && (notBridgeMethod(newMethod));
/*     */   }
/*     */ 
/*     */   private static boolean notBridgeMethod(MethodInfo minfo)
/*     */   {
/* 667 */     return (minfo.getAccessFlags() & 0x40) == 0;
/*     */   }
/*     */ 
/*     */   public List getAttributes()
/*     */   {
/* 681 */     return this.attributes;
/*     */   }
/*     */ 
/*     */   public AttributeInfo getAttribute(String name)
/*     */   {
/* 693 */     ArrayList list = this.attributes;
/* 694 */     int n = list.size();
/* 695 */     for (int i = 0; i < n; i++) {
/* 696 */       AttributeInfo ai = (AttributeInfo)list.get(i);
/* 697 */       if (ai.getName().equals(name)) {
/* 698 */         return ai;
/*     */       }
/*     */     }
/* 701 */     return null;
/*     */   }
/*     */ 
/*     */   public void addAttribute(AttributeInfo info)
/*     */   {
/* 711 */     AttributeInfo.remove(this.attributes, info.getName());
/* 712 */     this.attributes.add(info);
/*     */   }
/*     */ 
/*     */   public String getSourceFile()
/*     */   {
/* 721 */     SourceFileAttribute sf = (SourceFileAttribute)getAttribute("SourceFile");
/*     */ 
/* 723 */     if (sf == null) {
/* 724 */       return null;
/*     */     }
/* 726 */     return sf.getFileName();
/*     */   }
/*     */ 
/*     */   private void read(DataInputStream in) throws IOException
/*     */   {
/* 731 */     int magic = in.readInt();
/* 732 */     if (magic != -889275714) {
/* 733 */       throw new IOException("bad magic number: " + Integer.toHexString(magic));
/*     */     }
/* 735 */     this.minor = in.readUnsignedShort();
/* 736 */     this.major = in.readUnsignedShort();
/* 737 */     this.constPool = new ConstPool(in);
/* 738 */     this.accessFlags = in.readUnsignedShort();
/* 739 */     this.thisClass = in.readUnsignedShort();
/* 740 */     this.constPool.setThisClassInfo(this.thisClass);
/* 741 */     this.superClass = in.readUnsignedShort();
/* 742 */     int n = in.readUnsignedShort();
/* 743 */     if (n == 0) {
/* 744 */       this.interfaces = null;
/*     */     } else {
/* 746 */       this.interfaces = new int[n];
/* 747 */       for (int i = 0; i < n; i++) {
/* 748 */         this.interfaces[i] = in.readUnsignedShort();
/*     */       }
/*     */     }
/* 751 */     ConstPool cp = this.constPool;
/* 752 */     n = in.readUnsignedShort();
/* 753 */     this.fields = new ArrayList();
/* 754 */     for (int i = 0; i < n; i++) {
/* 755 */       addField2(new FieldInfo(cp, in));
/*     */     }
/* 757 */     n = in.readUnsignedShort();
/* 758 */     this.methods = new ArrayList();
/* 759 */     for (i = 0; i < n; i++) {
/* 760 */       addMethod2(new MethodInfo(cp, in));
/*     */     }
/* 762 */     this.attributes = new ArrayList();
/* 763 */     n = in.readUnsignedShort();
/* 764 */     for (i = 0; i < n; i++) {
/* 765 */       addAttribute(AttributeInfo.read(cp, in));
/*     */     }
/* 767 */     this.thisclassname = this.constPool.getClassInfo(this.thisClass);
/*     */   }
/*     */ 
/*     */   public void write(DataOutputStream out)
/*     */     throws IOException
/*     */   {
/* 776 */     out.writeInt(-889275714);
/* 777 */     out.writeShort(this.minor);
/* 778 */     out.writeShort(this.major);
/* 779 */     this.constPool.write(out);
/* 780 */     out.writeShort(this.accessFlags);
/* 781 */     out.writeShort(this.thisClass);
/* 782 */     out.writeShort(this.superClass);
/*     */     int n;
/* 784 */     if (this.interfaces == null)
/* 785 */       n = 0;
/*     */     else {
/* 787 */       n = this.interfaces.length;
/*     */     }
/* 789 */     out.writeShort(n);
/* 790 */     for (int i = 0; i < n; i++) {
/* 791 */       out.writeShort(this.interfaces[i]);
/*     */     }
/* 793 */     ArrayList list = this.fields;
/* 794 */     int n = list.size();
/* 795 */     out.writeShort(n);
/* 796 */     for (i = 0; i < n; i++) {
/* 797 */       FieldInfo finfo = (FieldInfo)list.get(i);
/* 798 */       finfo.write(out);
/*     */     }
/*     */ 
/* 801 */     list = this.methods;
/* 802 */     n = list.size();
/* 803 */     out.writeShort(n);
/* 804 */     for (i = 0; i < n; i++) {
/* 805 */       MethodInfo minfo = (MethodInfo)list.get(i);
/* 806 */       minfo.write(out);
/*     */     }
/*     */ 
/* 809 */     out.writeShort(this.attributes.size());
/* 810 */     AttributeInfo.writeAll(this.attributes, out);
/*     */   }
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 819 */     return this.major;
/*     */   }
/*     */ 
/*     */   public void setMajorVersion(int major)
/*     */   {
/* 829 */     this.major = major;
/*     */   }
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 838 */     return this.minor;
/*     */   }
/*     */ 
/*     */   public void setMinorVersion(int minor)
/*     */   {
/* 848 */     this.minor = minor;
/*     */   }
/*     */ 
/*     */   public void setVersionToJava5()
/*     */   {
/* 859 */     this.major = 49;
/* 860 */     this.minor = 0;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  98 */       Class.forName("java.lang.StringBuilder");
/*  99 */       MAJOR_VERSION = 49;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ClassFile
 * JD-Core Version:    0.6.2
 */