/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import com.newrelic.javassist.CtClass;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ 
/*      */ public final class ConstPool
/*      */ {
/*      */   LongVector items;
/*      */   int numOfItems;
/*      */   HashMap classes;
/*      */   HashMap strings;
/*      */   ConstInfo[] constInfoCache;
/*      */   int[] constInfoIndexCache;
/*      */   int thisClassInfo;
/*      */   private static final int CACHE_SIZE = 32;
/*      */   public static final int CONST_Class = 7;
/*      */   public static final int CONST_Fieldref = 9;
/*      */   public static final int CONST_Methodref = 10;
/*      */   public static final int CONST_InterfaceMethodref = 11;
/*      */   public static final int CONST_String = 8;
/*      */   public static final int CONST_Integer = 3;
/*      */   public static final int CONST_Float = 4;
/*      */   public static final int CONST_Long = 5;
/*      */   public static final int CONST_Double = 6;
/*      */   public static final int CONST_NameAndType = 12;
/*      */   public static final int CONST_Utf8 = 1;
/*  117 */   public static final CtClass THIS = null;
/*      */ 
/*      */   private static int hashFunc(int a, int b)
/*      */   {
/*   48 */     int h = -2128831035;
/*   49 */     int prime = 16777619;
/*   50 */     h = (h ^ a & 0xFF) * 16777619;
/*   51 */     h = (h ^ b & 0xFF) * 16777619;
/*      */ 
/*   54 */     h = h >> 5 ^ h & 0x1F;
/*   55 */     return h & 0x1F;
/*      */   }
/*      */ 
/*      */   public ConstPool(String thisclass)
/*      */   {
/*  126 */     this.items = new LongVector();
/*  127 */     this.numOfItems = 0;
/*  128 */     addItem(null);
/*  129 */     this.classes = new HashMap();
/*  130 */     this.strings = new HashMap();
/*  131 */     this.constInfoCache = new ConstInfo[32];
/*  132 */     this.constInfoIndexCache = new int[32];
/*  133 */     this.thisClassInfo = addClassInfo(thisclass);
/*      */   }
/*      */ 
/*      */   public ConstPool(DataInputStream in)
/*      */     throws IOException
/*      */   {
/*  142 */     this.classes = new HashMap();
/*  143 */     this.strings = new HashMap();
/*  144 */     this.constInfoCache = new ConstInfo[32];
/*  145 */     this.constInfoIndexCache = new int[32];
/*  146 */     this.thisClassInfo = 0;
/*      */ 
/*  149 */     read(in);
/*      */   }
/*      */ 
/*      */   void prune() {
/*  153 */     this.classes = new HashMap();
/*  154 */     this.strings = new HashMap();
/*  155 */     this.constInfoCache = new ConstInfo[32];
/*  156 */     this.constInfoIndexCache = new int[32];
/*      */   }
/*      */ 
/*      */   public int getSize()
/*      */   {
/*  163 */     return this.numOfItems;
/*      */   }
/*      */ 
/*      */   public String getClassName()
/*      */   {
/*  170 */     return getClassInfo(this.thisClassInfo);
/*      */   }
/*      */ 
/*      */   public int getThisClassInfo()
/*      */   {
/*  178 */     return this.thisClassInfo;
/*      */   }
/*      */ 
/*      */   void setThisClassInfo(int i) {
/*  182 */     this.thisClassInfo = i;
/*      */   }
/*      */ 
/*      */   ConstInfo getItem(int n) {
/*  186 */     return this.items.elementAt(n);
/*      */   }
/*      */ 
/*      */   public int getTag(int index)
/*      */   {
/*  194 */     return getItem(index).getTag();
/*      */   }
/*      */ 
/*      */   public String getClassInfo(int index)
/*      */   {
/*  209 */     ClassInfo c = (ClassInfo)getItem(index);
/*  210 */     if (c == null) {
/*  211 */       return null;
/*      */     }
/*  213 */     return Descriptor.toJavaName(getUtf8Info(c.name));
/*      */   }
/*      */ 
/*      */   public int getNameAndTypeName(int index)
/*      */   {
/*  222 */     NameAndTypeInfo ntinfo = (NameAndTypeInfo)getItem(index);
/*  223 */     return ntinfo.memberName;
/*      */   }
/*      */ 
/*      */   public int getNameAndTypeDescriptor(int index)
/*      */   {
/*  232 */     NameAndTypeInfo ntinfo = (NameAndTypeInfo)getItem(index);
/*  233 */     return ntinfo.typeDescriptor;
/*      */   }
/*      */ 
/*      */   public int getMemberClass(int index)
/*      */   {
/*  246 */     MemberrefInfo minfo = (MemberrefInfo)getItem(index);
/*  247 */     return minfo.classIndex;
/*      */   }
/*      */ 
/*      */   public int getMemberNameAndType(int index)
/*      */   {
/*  260 */     MemberrefInfo minfo = (MemberrefInfo)getItem(index);
/*  261 */     return minfo.nameAndTypeIndex;
/*      */   }
/*      */ 
/*      */   public int getFieldrefClass(int index)
/*      */   {
/*  270 */     FieldrefInfo finfo = (FieldrefInfo)getItem(index);
/*  271 */     return finfo.classIndex;
/*      */   }
/*      */ 
/*      */   public String getFieldrefClassName(int index)
/*      */   {
/*  282 */     FieldrefInfo f = (FieldrefInfo)getItem(index);
/*  283 */     if (f == null) {
/*  284 */       return null;
/*      */     }
/*  286 */     return getClassInfo(f.classIndex);
/*      */   }
/*      */ 
/*      */   public int getFieldrefNameAndType(int index)
/*      */   {
/*  295 */     FieldrefInfo finfo = (FieldrefInfo)getItem(index);
/*  296 */     return finfo.nameAndTypeIndex;
/*      */   }
/*      */ 
/*      */   public String getFieldrefName(int index)
/*      */   {
/*  308 */     FieldrefInfo f = (FieldrefInfo)getItem(index);
/*  309 */     if (f == null) {
/*  310 */       return null;
/*      */     }
/*  312 */     NameAndTypeInfo n = (NameAndTypeInfo)getItem(f.nameAndTypeIndex);
/*  313 */     if (n == null) {
/*  314 */       return null;
/*      */     }
/*  316 */     return getUtf8Info(n.memberName);
/*      */   }
/*      */ 
/*      */   public String getFieldrefType(int index)
/*      */   {
/*  329 */     FieldrefInfo f = (FieldrefInfo)getItem(index);
/*  330 */     if (f == null) {
/*  331 */       return null;
/*      */     }
/*  333 */     NameAndTypeInfo n = (NameAndTypeInfo)getItem(f.nameAndTypeIndex);
/*  334 */     if (n == null) {
/*  335 */       return null;
/*      */     }
/*  337 */     return getUtf8Info(n.typeDescriptor);
/*      */   }
/*      */ 
/*      */   public int getMethodrefClass(int index)
/*      */   {
/*  347 */     MethodrefInfo minfo = (MethodrefInfo)getItem(index);
/*  348 */     return minfo.classIndex;
/*      */   }
/*      */ 
/*      */   public String getMethodrefClassName(int index)
/*      */   {
/*  359 */     MethodrefInfo minfo = (MethodrefInfo)getItem(index);
/*  360 */     if (minfo == null) {
/*  361 */       return null;
/*      */     }
/*  363 */     return getClassInfo(minfo.classIndex);
/*      */   }
/*      */ 
/*      */   public int getMethodrefNameAndType(int index)
/*      */   {
/*  372 */     MethodrefInfo minfo = (MethodrefInfo)getItem(index);
/*  373 */     return minfo.nameAndTypeIndex;
/*      */   }
/*      */ 
/*      */   public String getMethodrefName(int index)
/*      */   {
/*  385 */     MethodrefInfo minfo = (MethodrefInfo)getItem(index);
/*  386 */     if (minfo == null) {
/*  387 */       return null;
/*      */     }
/*  389 */     NameAndTypeInfo n = (NameAndTypeInfo)getItem(minfo.nameAndTypeIndex);
/*      */ 
/*  391 */     if (n == null) {
/*  392 */       return null;
/*      */     }
/*  394 */     return getUtf8Info(n.memberName);
/*      */   }
/*      */ 
/*      */   public String getMethodrefType(int index)
/*      */   {
/*  407 */     MethodrefInfo minfo = (MethodrefInfo)getItem(index);
/*  408 */     if (minfo == null) {
/*  409 */       return null;
/*      */     }
/*  411 */     NameAndTypeInfo n = (NameAndTypeInfo)getItem(minfo.nameAndTypeIndex);
/*      */ 
/*  413 */     if (n == null) {
/*  414 */       return null;
/*      */     }
/*  416 */     return getUtf8Info(n.typeDescriptor);
/*      */   }
/*      */ 
/*      */   public int getInterfaceMethodrefClass(int index)
/*      */   {
/*  426 */     InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)getItem(index);
/*      */ 
/*  428 */     return minfo.classIndex;
/*      */   }
/*      */ 
/*      */   public String getInterfaceMethodrefClassName(int index)
/*      */   {
/*  439 */     InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)getItem(index);
/*      */ 
/*  441 */     return getClassInfo(minfo.classIndex);
/*      */   }
/*      */ 
/*      */   public int getInterfaceMethodrefNameAndType(int index)
/*      */   {
/*  450 */     InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)getItem(index);
/*      */ 
/*  452 */     return minfo.nameAndTypeIndex;
/*      */   }
/*      */ 
/*      */   public String getInterfaceMethodrefName(int index)
/*      */   {
/*  465 */     InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)getItem(index);
/*      */ 
/*  467 */     if (minfo == null) {
/*  468 */       return null;
/*      */     }
/*  470 */     NameAndTypeInfo n = (NameAndTypeInfo)getItem(minfo.nameAndTypeIndex);
/*      */ 
/*  472 */     if (n == null) {
/*  473 */       return null;
/*      */     }
/*  475 */     return getUtf8Info(n.memberName);
/*      */   }
/*      */ 
/*      */   public String getInterfaceMethodrefType(int index)
/*      */   {
/*  489 */     InterfaceMethodrefInfo minfo = (InterfaceMethodrefInfo)getItem(index);
/*      */ 
/*  491 */     if (minfo == null) {
/*  492 */       return null;
/*      */     }
/*  494 */     NameAndTypeInfo n = (NameAndTypeInfo)getItem(minfo.nameAndTypeIndex);
/*      */ 
/*  496 */     if (n == null) {
/*  497 */       return null;
/*      */     }
/*  499 */     return getUtf8Info(n.typeDescriptor);
/*      */   }
/*      */ 
/*      */   public Object getLdcValue(int index)
/*      */   {
/*  512 */     ConstInfo constInfo = getItem(index);
/*  513 */     Object value = null;
/*  514 */     if ((constInfo instanceof StringInfo))
/*  515 */       value = getStringInfo(index);
/*  516 */     else if ((constInfo instanceof FloatInfo))
/*  517 */       value = new Float(getFloatInfo(index));
/*  518 */     else if ((constInfo instanceof IntegerInfo))
/*  519 */       value = new Integer(getIntegerInfo(index));
/*  520 */     else if ((constInfo instanceof LongInfo))
/*  521 */       value = new Long(getLongInfo(index));
/*  522 */     else if ((constInfo instanceof DoubleInfo))
/*  523 */       value = new Double(getDoubleInfo(index));
/*      */     else {
/*  525 */       value = null;
/*      */     }
/*  527 */     return value;
/*      */   }
/*      */ 
/*      */   public int getIntegerInfo(int index)
/*      */   {
/*  537 */     IntegerInfo i = (IntegerInfo)getItem(index);
/*  538 */     return i.value;
/*      */   }
/*      */ 
/*      */   public float getFloatInfo(int index)
/*      */   {
/*  548 */     FloatInfo i = (FloatInfo)getItem(index);
/*  549 */     return i.value;
/*      */   }
/*      */ 
/*      */   public long getLongInfo(int index)
/*      */   {
/*  559 */     LongInfo i = (LongInfo)getItem(index);
/*  560 */     return i.value;
/*      */   }
/*      */ 
/*      */   public double getDoubleInfo(int index)
/*      */   {
/*  570 */     DoubleInfo i = (DoubleInfo)getItem(index);
/*  571 */     return i.value;
/*      */   }
/*      */ 
/*      */   public String getStringInfo(int index)
/*      */   {
/*  581 */     StringInfo si = (StringInfo)getItem(index);
/*  582 */     return getUtf8Info(si.string);
/*      */   }
/*      */ 
/*      */   public String getUtf8Info(int index)
/*      */   {
/*  592 */     Utf8Info utf = (Utf8Info)getItem(index);
/*  593 */     return utf.string;
/*      */   }
/*      */ 
/*      */   public int isConstructor(String classname, int index)
/*      */   {
/*  607 */     return isMember(classname, "<init>", index);
/*      */   }
/*      */ 
/*      */   public int isMember(String classname, String membername, int index)
/*      */   {
/*  627 */     MemberrefInfo minfo = (MemberrefInfo)getItem(index);
/*  628 */     if (getClassInfo(minfo.classIndex).equals(classname)) {
/*  629 */       NameAndTypeInfo ntinfo = (NameAndTypeInfo)getItem(minfo.nameAndTypeIndex);
/*      */ 
/*  631 */       if (getUtf8Info(ntinfo.memberName).equals(membername)) {
/*  632 */         return ntinfo.typeDescriptor;
/*      */       }
/*      */     }
/*  635 */     return 0;
/*      */   }
/*      */ 
/*      */   public String eqMember(String membername, String desc, int index)
/*      */   {
/*  656 */     MemberrefInfo minfo = (MemberrefInfo)getItem(index);
/*  657 */     NameAndTypeInfo ntinfo = (NameAndTypeInfo)getItem(minfo.nameAndTypeIndex);
/*      */ 
/*  659 */     if ((getUtf8Info(ntinfo.memberName).equals(membername)) && (getUtf8Info(ntinfo.typeDescriptor).equals(desc)))
/*      */     {
/*  661 */       return getClassInfo(minfo.classIndex);
/*      */     }
/*  663 */     return null;
/*      */   }
/*      */ 
/*      */   private int addItem(ConstInfo info) {
/*  667 */     this.items.addElement(info);
/*  668 */     return this.numOfItems++;
/*      */   }
/*      */ 
/*      */   public int copy(int n, ConstPool dest, Map classnames)
/*      */   {
/*  683 */     if (n == 0) {
/*  684 */       return 0;
/*      */     }
/*  686 */     ConstInfo info = getItem(n);
/*  687 */     return info.copy(this, dest, classnames);
/*      */   }
/*      */ 
/*      */   int addConstInfoPadding() {
/*  691 */     return addItem(new ConstInfoPadding());
/*      */   }
/*      */ 
/*      */   public int addClassInfo(CtClass c)
/*      */   {
/*  703 */     if (c == THIS)
/*  704 */       return this.thisClassInfo;
/*  705 */     if (!c.isArray()) {
/*  706 */       return addClassInfo(c.getName());
/*      */     }
/*      */ 
/*  713 */     return addClassInfo(Descriptor.toJvmName(c));
/*      */   }
/*      */ 
/*      */   public int addClassInfo(String qname)
/*      */   {
/*  728 */     ClassInfo info = (ClassInfo)this.classes.get(qname);
/*  729 */     if (info != null) {
/*  730 */       return info.index;
/*      */     }
/*  732 */     int utf8 = addUtf8Info(Descriptor.toJvmName(qname));
/*  733 */     info = new ClassInfo(utf8, this.numOfItems);
/*  734 */     this.classes.put(qname, info);
/*  735 */     return addItem(info);
/*      */   }
/*      */ 
/*      */   public int addNameAndTypeInfo(String name, String type)
/*      */   {
/*  749 */     return addNameAndTypeInfo(addUtf8Info(name), addUtf8Info(type));
/*      */   }
/*      */ 
/*      */   public int addNameAndTypeInfo(int name, int type)
/*      */   {
/*  760 */     int h = hashFunc(name, type);
/*  761 */     ConstInfo ci = this.constInfoCache[h];
/*  762 */     if ((ci != null) && ((ci instanceof NameAndTypeInfo)) && (ci.hashCheck(name, type))) {
/*  763 */       return this.constInfoIndexCache[h];
/*      */     }
/*  765 */     NameAndTypeInfo item = new NameAndTypeInfo(name, type);
/*  766 */     this.constInfoCache[h] = item;
/*  767 */     int i = addItem(item);
/*  768 */     this.constInfoIndexCache[h] = i;
/*  769 */     return i;
/*      */   }
/*      */ 
/*      */   public int addFieldrefInfo(int classInfo, String name, String type)
/*      */   {
/*  787 */     int nt = addNameAndTypeInfo(name, type);
/*  788 */     return addFieldrefInfo(classInfo, nt);
/*      */   }
/*      */ 
/*      */   public int addFieldrefInfo(int classInfo, int nameAndTypeInfo)
/*      */   {
/*  799 */     int h = hashFunc(classInfo, nameAndTypeInfo);
/*  800 */     ConstInfo ci = this.constInfoCache[h];
/*  801 */     if ((ci != null) && ((ci instanceof FieldrefInfo)) && (ci.hashCheck(classInfo, nameAndTypeInfo))) {
/*  802 */       return this.constInfoIndexCache[h];
/*      */     }
/*  804 */     FieldrefInfo item = new FieldrefInfo(classInfo, nameAndTypeInfo);
/*  805 */     this.constInfoCache[h] = item;
/*  806 */     int i = addItem(item);
/*  807 */     this.constInfoIndexCache[h] = i;
/*  808 */     return i;
/*      */   }
/*      */ 
/*      */   public int addMethodrefInfo(int classInfo, String name, String type)
/*      */   {
/*  826 */     int nt = addNameAndTypeInfo(name, type);
/*  827 */     return addMethodrefInfo(classInfo, nt);
/*      */   }
/*      */ 
/*      */   public int addMethodrefInfo(int classInfo, int nameAndTypeInfo)
/*      */   {
/*  838 */     int h = hashFunc(classInfo, nameAndTypeInfo);
/*  839 */     ConstInfo ci = this.constInfoCache[h];
/*  840 */     if ((ci != null) && ((ci instanceof MethodrefInfo)) && (ci.hashCheck(classInfo, nameAndTypeInfo))) {
/*  841 */       return this.constInfoIndexCache[h];
/*      */     }
/*  843 */     MethodrefInfo item = new MethodrefInfo(classInfo, nameAndTypeInfo);
/*  844 */     this.constInfoCache[h] = item;
/*  845 */     int i = addItem(item);
/*  846 */     this.constInfoIndexCache[h] = i;
/*  847 */     return i;
/*      */   }
/*      */ 
/*      */   public int addInterfaceMethodrefInfo(int classInfo, String name, String type)
/*      */   {
/*  867 */     int nt = addNameAndTypeInfo(name, type);
/*  868 */     return addInterfaceMethodrefInfo(classInfo, nt);
/*      */   }
/*      */ 
/*      */   public int addInterfaceMethodrefInfo(int classInfo, int nameAndTypeInfo)
/*      */   {
/*  881 */     int h = hashFunc(classInfo, nameAndTypeInfo);
/*  882 */     ConstInfo ci = this.constInfoCache[h];
/*  883 */     if ((ci != null) && ((ci instanceof InterfaceMethodrefInfo)) && (ci.hashCheck(classInfo, nameAndTypeInfo))) {
/*  884 */       return this.constInfoIndexCache[h];
/*      */     }
/*  886 */     InterfaceMethodrefInfo item = new InterfaceMethodrefInfo(classInfo, nameAndTypeInfo);
/*  887 */     this.constInfoCache[h] = item;
/*  888 */     int i = addItem(item);
/*  889 */     this.constInfoIndexCache[h] = i;
/*  890 */     return i;
/*      */   }
/*      */ 
/*      */   public int addStringInfo(String str)
/*      */   {
/*  904 */     return addItem(new StringInfo(addUtf8Info(str)));
/*      */   }
/*      */ 
/*      */   public int addIntegerInfo(int i)
/*      */   {
/*  914 */     return addItem(new IntegerInfo(i));
/*      */   }
/*      */ 
/*      */   public int addFloatInfo(float f)
/*      */   {
/*  924 */     return addItem(new FloatInfo(f));
/*      */   }
/*      */ 
/*      */   public int addLongInfo(long l)
/*      */   {
/*  934 */     int i = addItem(new LongInfo(l));
/*  935 */     addItem(new ConstInfoPadding());
/*  936 */     return i;
/*      */   }
/*      */ 
/*      */   public int addDoubleInfo(double d)
/*      */   {
/*  946 */     int i = addItem(new DoubleInfo(d));
/*  947 */     addItem(new ConstInfoPadding());
/*  948 */     return i;
/*      */   }
/*      */ 
/*      */   public int addUtf8Info(String utf8)
/*      */   {
/*  963 */     Utf8Info info = (Utf8Info)this.strings.get(utf8);
/*  964 */     if (info != null) {
/*  965 */       return info.index;
/*      */     }
/*  967 */     info = new Utf8Info(utf8, this.numOfItems);
/*  968 */     this.strings.put(utf8, info);
/*  969 */     return addItem(info);
/*      */   }
/*      */ 
/*      */   public Set getClassNames()
/*      */   {
/*  980 */     HashSet result = new HashSet();
/*  981 */     LongVector v = this.items;
/*  982 */     int size = this.numOfItems;
/*  983 */     for (int i = 1; i < size; i++) {
/*  984 */       String className = v.elementAt(i).getClassName(this);
/*  985 */       if (className != null)
/*  986 */         result.add(className);
/*      */     }
/*  988 */     return result;
/*      */   }
/*      */ 
/*      */   public void renameClass(String oldName, String newName)
/*      */   {
/*  998 */     LongVector v = this.items;
/*  999 */     int size = this.numOfItems;
/* 1000 */     this.classes = new HashMap(this.classes.size() * 2);
/* 1001 */     for (int i = 1; i < size; i++) {
/* 1002 */       ConstInfo ci = v.elementAt(i);
/* 1003 */       ci.renameClass(this, oldName, newName);
/* 1004 */       ci.makeHashtable(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void renameClass(Map classnames)
/*      */   {
/* 1015 */     LongVector v = this.items;
/* 1016 */     int size = this.numOfItems;
/* 1017 */     this.classes = new HashMap(this.classes.size() * 2);
/* 1018 */     for (int i = 1; i < size; i++) {
/* 1019 */       ConstInfo ci = v.elementAt(i);
/* 1020 */       ci.renameClass(this, classnames);
/* 1021 */       ci.makeHashtable(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void read(DataInputStream in) throws IOException {
/* 1026 */     int n = in.readUnsignedShort();
/*      */ 
/* 1028 */     this.items = new LongVector(n);
/* 1029 */     this.numOfItems = 0;
/* 1030 */     addItem(null);
/*      */     while (true) {
/* 1032 */       n--; if (n <= 0) break;
/* 1033 */       int tag = readOne(in);
/* 1034 */       if ((tag == 5) || (tag == 6)) {
/* 1035 */         addItem(new ConstInfoPadding());
/* 1036 */         n--;
/*      */       }
/*      */     }
/*      */ 
/* 1040 */     int i = 1;
/*      */     while (true) {
/* 1042 */       ConstInfo info = this.items.elementAt(i++);
/* 1043 */       if (info == null) {
/*      */         break;
/*      */       }
/* 1046 */       info.makeHashtable(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int readOne(DataInputStream in) throws IOException
/*      */   {
/* 1052 */     int tag = in.readUnsignedByte();
/*      */     ConstInfo info;
/* 1053 */     switch (tag) {
/*      */     case 1:
/* 1055 */       info = new Utf8Info(in, this.numOfItems);
/* 1056 */       this.strings.put(((Utf8Info)info).string, info);
/* 1057 */       break;
/*      */     case 3:
/* 1059 */       info = new IntegerInfo(in);
/* 1060 */       break;
/*      */     case 4:
/* 1062 */       info = new FloatInfo(in);
/* 1063 */       break;
/*      */     case 5:
/* 1065 */       info = new LongInfo(in);
/* 1066 */       break;
/*      */     case 6:
/* 1068 */       info = new DoubleInfo(in);
/* 1069 */       break;
/*      */     case 7:
/* 1071 */       info = new ClassInfo(in, this.numOfItems);
/*      */ 
/* 1073 */       break;
/*      */     case 8:
/* 1075 */       info = new StringInfo(in);
/* 1076 */       break;
/*      */     case 9:
/* 1078 */       info = new FieldrefInfo(in);
/* 1079 */       break;
/*      */     case 10:
/* 1081 */       info = new MethodrefInfo(in);
/* 1082 */       break;
/*      */     case 11:
/* 1084 */       info = new InterfaceMethodrefInfo(in);
/* 1085 */       break;
/*      */     case 12:
/* 1087 */       info = new NameAndTypeInfo(in);
/* 1088 */       break;
/*      */     case 2:
/*      */     default:
/* 1090 */       throw new IOException("invalid constant type: " + tag);
/*      */     }
/*      */ 
/* 1093 */     addItem(info);
/* 1094 */     return tag;
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out)
/*      */     throws IOException
/*      */   {
/* 1101 */     out.writeShort(this.numOfItems);
/* 1102 */     LongVector v = this.items;
/* 1103 */     int size = this.numOfItems;
/* 1104 */     for (int i = 1; i < size; i++)
/* 1105 */       v.elementAt(i).write(out);
/*      */   }
/*      */ 
/*      */   public void print()
/*      */   {
/* 1112 */     print(new PrintWriter(System.out, true));
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out)
/*      */   {
/* 1119 */     int size = this.numOfItems;
/* 1120 */     for (int i = 1; i < size; i++) {
/* 1121 */       out.print(i);
/* 1122 */       out.print(" ");
/* 1123 */       this.items.elementAt(i).print(out);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ConstPool
 * JD-Core Version:    0.6.2
 */