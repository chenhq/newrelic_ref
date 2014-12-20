/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import com.newrelic.javassist.CtClass;
/*      */ import com.newrelic.javassist.CtPrimitiveType;
/*      */ 
/*      */ public class Bytecode extends ByteVector
/*      */   implements Cloneable, Opcode
/*      */ {
/*  118 */   public static final CtClass THIS = ConstPool.THIS;
/*      */   ConstPool constPool;
/*      */   int maxStack;
/*      */   int maxLocals;
/*      */   ExceptionTable tryblocks;
/*      */   private int stackDepth;
/*      */ 
/*      */   public Bytecode(ConstPool cp, int stacksize, int localvars)
/*      */   {
/*  139 */     this.constPool = cp;
/*  140 */     this.maxStack = stacksize;
/*  141 */     this.maxLocals = localvars;
/*  142 */     this.tryblocks = new ExceptionTable(cp);
/*  143 */     this.stackDepth = 0;
/*      */   }
/*      */ 
/*      */   public Bytecode(ConstPool cp)
/*      */   {
/*  156 */     this(cp, 0, 0);
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  166 */       Bytecode bc = (Bytecode)super.clone();
/*  167 */       bc.tryblocks = ((ExceptionTable)this.tryblocks.clone());
/*  168 */       return bc;
/*      */     }
/*      */     catch (CloneNotSupportedException cnse) {
/*  171 */       throw new RuntimeException(cnse);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ConstPool getConstPool()
/*      */   {
/*  178 */     return this.constPool;
/*      */   }
/*      */ 
/*      */   public ExceptionTable getExceptionTable()
/*      */   {
/*  183 */     return this.tryblocks;
/*      */   }
/*      */ 
/*      */   public CodeAttribute toCodeAttribute()
/*      */   {
/*  189 */     return new CodeAttribute(this.constPool, this.maxStack, this.maxLocals, get(), this.tryblocks);
/*      */   }
/*      */ 
/*      */   public int length()
/*      */   {
/*  197 */     return getSize();
/*      */   }
/*      */ 
/*      */   public byte[] get()
/*      */   {
/*  204 */     return copy();
/*      */   }
/*      */ 
/*      */   public int getMaxStack()
/*      */   {
/*  210 */     return this.maxStack;
/*      */   }
/*      */ 
/*      */   public void setMaxStack(int size)
/*      */   {
/*  227 */     this.maxStack = size;
/*      */   }
/*      */ 
/*      */   public int getMaxLocals()
/*      */   {
/*  233 */     return this.maxLocals;
/*      */   }
/*      */ 
/*      */   public void setMaxLocals(int size)
/*      */   {
/*  239 */     this.maxLocals = size;
/*      */   }
/*      */ 
/*      */   public void setMaxLocals(boolean isStatic, CtClass[] params, int locals)
/*      */   {
/*  257 */     if (!isStatic) {
/*  258 */       locals++;
/*      */     }
/*  260 */     if (params != null) {
/*  261 */       CtClass doubleType = CtClass.doubleType;
/*  262 */       CtClass longType = CtClass.longType;
/*  263 */       int n = params.length;
/*  264 */       for (int i = 0; i < n; i++) {
/*  265 */         CtClass type = params[i];
/*  266 */         if ((type == doubleType) || (type == longType))
/*  267 */           locals += 2;
/*      */         else {
/*  269 */           locals++;
/*      */         }
/*      */       }
/*      */     }
/*  273 */     this.maxLocals = locals;
/*      */   }
/*      */ 
/*      */   public void incMaxLocals(int diff)
/*      */   {
/*  280 */     this.maxLocals += diff;
/*      */   }
/*      */ 
/*      */   public void addExceptionHandler(int start, int end, int handler, CtClass type)
/*      */   {
/*  288 */     addExceptionHandler(start, end, handler, this.constPool.addClassInfo(type));
/*      */   }
/*      */ 
/*      */   public void addExceptionHandler(int start, int end, int handler, String type)
/*      */   {
/*  299 */     addExceptionHandler(start, end, handler, this.constPool.addClassInfo(type));
/*      */   }
/*      */ 
/*      */   public void addExceptionHandler(int start, int end, int handler, int type)
/*      */   {
/*  308 */     this.tryblocks.add(start, end, handler, type);
/*      */   }
/*      */ 
/*      */   public int currentPc()
/*      */   {
/*  316 */     return getSize();
/*      */   }
/*      */ 
/*      */   public int read(int offset)
/*      */   {
/*  326 */     return super.read(offset);
/*      */   }
/*      */ 
/*      */   public int read16bit(int offset)
/*      */   {
/*  334 */     int v1 = read(offset);
/*  335 */     int v2 = read(offset + 1);
/*  336 */     return (v1 << 8) + (v2 & 0xFF);
/*      */   }
/*      */ 
/*      */   public int read32bit(int offset)
/*      */   {
/*  344 */     int v1 = read16bit(offset);
/*  345 */     int v2 = read16bit(offset + 2);
/*  346 */     return (v1 << 16) + (v2 & 0xFFFF);
/*      */   }
/*      */ 
/*      */   public void write(int offset, int value)
/*      */   {
/*  356 */     super.write(offset, value);
/*      */   }
/*      */ 
/*      */   public void write16bit(int offset, int value)
/*      */   {
/*  364 */     write(offset, value >> 8);
/*  365 */     write(offset + 1, value);
/*      */   }
/*      */ 
/*      */   public void write32bit(int offset, int value)
/*      */   {
/*  373 */     write16bit(offset, value >> 16);
/*  374 */     write16bit(offset + 2, value);
/*      */   }
/*      */ 
/*      */   public void add(int code)
/*      */   {
/*  381 */     super.add(code);
/*      */   }
/*      */ 
/*      */   public void add32bit(int value)
/*      */   {
/*  388 */     add(value >> 24, value >> 16, value >> 8, value);
/*      */   }
/*      */ 
/*      */   public void addGap(int length)
/*      */   {
/*  397 */     super.addGap(length);
/*      */   }
/*      */ 
/*      */   public void addOpcode(int code)
/*      */   {
/*  412 */     add(code);
/*  413 */     growStack(STACK_GROW[code]);
/*      */   }
/*      */ 
/*      */   public void growStack(int diff)
/*      */   {
/*  424 */     setStackDepth(this.stackDepth + diff);
/*      */   }
/*      */ 
/*      */   public int getStackDepth()
/*      */   {
/*  430 */     return this.stackDepth;
/*      */   }
/*      */ 
/*      */   public void setStackDepth(int depth)
/*      */   {
/*  440 */     this.stackDepth = depth;
/*  441 */     if (this.stackDepth > this.maxStack)
/*  442 */       this.maxStack = this.stackDepth;
/*      */   }
/*      */ 
/*      */   public void addIndex(int index)
/*      */   {
/*  450 */     add(index >> 8, index);
/*      */   }
/*      */ 
/*      */   public void addAload(int n)
/*      */   {
/*  459 */     if (n < 4) {
/*  460 */       addOpcode(42 + n);
/*  461 */     } else if (n < 256) {
/*  462 */       addOpcode(25);
/*  463 */       add(n);
/*      */     }
/*      */     else {
/*  466 */       addOpcode(196);
/*  467 */       addOpcode(25);
/*  468 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addAstore(int n)
/*      */   {
/*  478 */     if (n < 4) {
/*  479 */       addOpcode(75 + n);
/*  480 */     } else if (n < 256) {
/*  481 */       addOpcode(58);
/*  482 */       add(n);
/*      */     }
/*      */     else {
/*  485 */       addOpcode(196);
/*  486 */       addOpcode(58);
/*  487 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addIconst(int n)
/*      */   {
/*  497 */     if ((n < 6) && (-2 < n)) {
/*  498 */       addOpcode(3 + n);
/*  499 */     } else if ((n <= 127) && (-128 <= n)) {
/*  500 */       addOpcode(16);
/*  501 */       add(n);
/*      */     }
/*  503 */     else if ((n <= 32767) && (-32768 <= n)) {
/*  504 */       addOpcode(17);
/*  505 */       add(n >> 8);
/*  506 */       add(n);
/*      */     }
/*      */     else {
/*  509 */       addLdc(this.constPool.addIntegerInfo(n));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addConstZero(CtClass type)
/*      */   {
/*  519 */     if (type.isPrimitive()) {
/*  520 */       if (type == CtClass.longType) {
/*  521 */         addOpcode(9);
/*  522 */       } else if (type == CtClass.floatType) {
/*  523 */         addOpcode(11);
/*  524 */       } else if (type == CtClass.doubleType) {
/*  525 */         addOpcode(14); } else {
/*  526 */         if (type == CtClass.voidType) {
/*  527 */           throw new RuntimeException("void type?");
/*      */         }
/*  529 */         addOpcode(3);
/*      */       }
/*      */     }
/*  532 */     else addOpcode(1);
/*      */   }
/*      */ 
/*      */   public void addIload(int n)
/*      */   {
/*  541 */     if (n < 4) {
/*  542 */       addOpcode(26 + n);
/*  543 */     } else if (n < 256) {
/*  544 */       addOpcode(21);
/*  545 */       add(n);
/*      */     }
/*      */     else {
/*  548 */       addOpcode(196);
/*  549 */       addOpcode(21);
/*  550 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addIstore(int n)
/*      */   {
/*  560 */     if (n < 4) {
/*  561 */       addOpcode(59 + n);
/*  562 */     } else if (n < 256) {
/*  563 */       addOpcode(54);
/*  564 */       add(n);
/*      */     }
/*      */     else {
/*  567 */       addOpcode(196);
/*  568 */       addOpcode(54);
/*  569 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addLconst(long n)
/*      */   {
/*  579 */     if ((n == 0L) || (n == 1L))
/*  580 */       addOpcode(9 + (int)n);
/*      */     else
/*  582 */       addLdc2w(n);
/*      */   }
/*      */ 
/*      */   public void addLload(int n)
/*      */   {
/*  591 */     if (n < 4) {
/*  592 */       addOpcode(30 + n);
/*  593 */     } else if (n < 256) {
/*  594 */       addOpcode(22);
/*  595 */       add(n);
/*      */     }
/*      */     else {
/*  598 */       addOpcode(196);
/*  599 */       addOpcode(22);
/*  600 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addLstore(int n)
/*      */   {
/*  610 */     if (n < 4) {
/*  611 */       addOpcode(63 + n);
/*  612 */     } else if (n < 256) {
/*  613 */       addOpcode(55);
/*  614 */       add(n);
/*      */     }
/*      */     else {
/*  617 */       addOpcode(196);
/*  618 */       addOpcode(55);
/*  619 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addDconst(double d)
/*      */   {
/*  629 */     if ((d == 0.0D) || (d == 1.0D))
/*  630 */       addOpcode(14 + (int)d);
/*      */     else
/*  632 */       addLdc2w(d);
/*      */   }
/*      */ 
/*      */   public void addDload(int n)
/*      */   {
/*  641 */     if (n < 4) {
/*  642 */       addOpcode(38 + n);
/*  643 */     } else if (n < 256) {
/*  644 */       addOpcode(24);
/*  645 */       add(n);
/*      */     }
/*      */     else {
/*  648 */       addOpcode(196);
/*  649 */       addOpcode(24);
/*  650 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addDstore(int n)
/*      */   {
/*  660 */     if (n < 4) {
/*  661 */       addOpcode(71 + n);
/*  662 */     } else if (n < 256) {
/*  663 */       addOpcode(57);
/*  664 */       add(n);
/*      */     }
/*      */     else {
/*  667 */       addOpcode(196);
/*  668 */       addOpcode(57);
/*  669 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addFconst(float f)
/*      */   {
/*  679 */     if ((f == 0.0F) || (f == 1.0F) || (f == 2.0F))
/*  680 */       addOpcode(11 + (int)f);
/*      */     else
/*  682 */       addLdc(this.constPool.addFloatInfo(f));
/*      */   }
/*      */ 
/*      */   public void addFload(int n)
/*      */   {
/*  691 */     if (n < 4) {
/*  692 */       addOpcode(34 + n);
/*  693 */     } else if (n < 256) {
/*  694 */       addOpcode(23);
/*  695 */       add(n);
/*      */     }
/*      */     else {
/*  698 */       addOpcode(196);
/*  699 */       addOpcode(23);
/*  700 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addFstore(int n)
/*      */   {
/*  710 */     if (n < 4) {
/*  711 */       addOpcode(67 + n);
/*  712 */     } else if (n < 256) {
/*  713 */       addOpcode(56);
/*  714 */       add(n);
/*      */     }
/*      */     else {
/*  717 */       addOpcode(196);
/*  718 */       addOpcode(56);
/*  719 */       addIndex(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int addLoad(int n, CtClass type)
/*      */   {
/*  732 */     if (type.isPrimitive()) {
/*  733 */       if ((type == CtClass.booleanType) || (type == CtClass.charType) || (type == CtClass.byteType) || (type == CtClass.shortType) || (type == CtClass.intType))
/*      */       {
/*  736 */         addIload(n); } else {
/*  737 */         if (type == CtClass.longType) {
/*  738 */           addLload(n);
/*  739 */           return 2;
/*      */         }
/*  741 */         if (type == CtClass.floatType) {
/*  742 */           addFload(n); } else {
/*  743 */           if (type == CtClass.doubleType) {
/*  744 */             addDload(n);
/*  745 */             return 2;
/*      */           }
/*      */ 
/*  748 */           throw new RuntimeException("void type?");
/*      */         }
/*      */       }
/*      */     } else addAload(n);
/*      */ 
/*  753 */     return 1;
/*      */   }
/*      */ 
/*      */   public int addStore(int n, CtClass type)
/*      */   {
/*  765 */     if (type.isPrimitive()) {
/*  766 */       if ((type == CtClass.booleanType) || (type == CtClass.charType) || (type == CtClass.byteType) || (type == CtClass.shortType) || (type == CtClass.intType))
/*      */       {
/*  769 */         addIstore(n); } else {
/*  770 */         if (type == CtClass.longType) {
/*  771 */           addLstore(n);
/*  772 */           return 2;
/*      */         }
/*  774 */         if (type == CtClass.floatType) {
/*  775 */           addFstore(n); } else {
/*  776 */           if (type == CtClass.doubleType) {
/*  777 */             addDstore(n);
/*  778 */             return 2;
/*      */           }
/*      */ 
/*  781 */           throw new RuntimeException("void type?");
/*      */         }
/*      */       }
/*      */     } else addAstore(n);
/*      */ 
/*  786 */     return 1;
/*      */   }
/*      */ 
/*      */   public int addLoadParameters(CtClass[] params, int offset)
/*      */   {
/*  797 */     int stacksize = 0;
/*  798 */     if (params != null) {
/*  799 */       int n = params.length;
/*  800 */       for (int i = 0; i < n; i++) {
/*  801 */         stacksize += addLoad(stacksize + offset, params[i]);
/*      */       }
/*      */     }
/*  804 */     return stacksize;
/*      */   }
/*      */ 
/*      */   public void addCheckcast(CtClass c)
/*      */   {
/*  813 */     addOpcode(192);
/*  814 */     addIndex(this.constPool.addClassInfo(c));
/*      */   }
/*      */ 
/*      */   public void addCheckcast(String classname)
/*      */   {
/*  823 */     addOpcode(192);
/*  824 */     addIndex(this.constPool.addClassInfo(classname));
/*      */   }
/*      */ 
/*      */   public void addInstanceof(String classname)
/*      */   {
/*  833 */     addOpcode(193);
/*  834 */     addIndex(this.constPool.addClassInfo(classname));
/*      */   }
/*      */ 
/*      */   public void addGetfield(CtClass c, String name, String type)
/*      */   {
/*  847 */     add(180);
/*  848 */     int ci = this.constPool.addClassInfo(c);
/*  849 */     addIndex(this.constPool.addFieldrefInfo(ci, name, type));
/*  850 */     growStack(Descriptor.dataSize(type) - 1);
/*      */   }
/*      */ 
/*      */   public void addGetfield(String c, String name, String type)
/*      */   {
/*  863 */     add(180);
/*  864 */     int ci = this.constPool.addClassInfo(c);
/*  865 */     addIndex(this.constPool.addFieldrefInfo(ci, name, type));
/*  866 */     growStack(Descriptor.dataSize(type) - 1);
/*      */   }
/*      */ 
/*      */   public void addGetstatic(CtClass c, String name, String type)
/*      */   {
/*  879 */     add(178);
/*  880 */     int ci = this.constPool.addClassInfo(c);
/*  881 */     addIndex(this.constPool.addFieldrefInfo(ci, name, type));
/*  882 */     growStack(Descriptor.dataSize(type));
/*      */   }
/*      */ 
/*      */   public void addGetstatic(String c, String name, String type)
/*      */   {
/*  895 */     add(178);
/*  896 */     int ci = this.constPool.addClassInfo(c);
/*  897 */     addIndex(this.constPool.addFieldrefInfo(ci, name, type));
/*  898 */     growStack(Descriptor.dataSize(type));
/*      */   }
/*      */ 
/*      */   public void addInvokespecial(CtClass clazz, String name, CtClass returnType, CtClass[] paramTypes)
/*      */   {
/*  911 */     String desc = Descriptor.ofMethod(returnType, paramTypes);
/*  912 */     addInvokespecial(clazz, name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokespecial(CtClass clazz, String name, String desc)
/*      */   {
/*  926 */     addInvokespecial(this.constPool.addClassInfo(clazz), name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokespecial(String clazz, String name, String desc)
/*      */   {
/*  940 */     addInvokespecial(this.constPool.addClassInfo(clazz), name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokespecial(int clazz, String name, String desc)
/*      */   {
/*  955 */     add(183);
/*  956 */     addIndex(this.constPool.addMethodrefInfo(clazz, name, desc));
/*  957 */     growStack(Descriptor.dataSize(desc) - 1);
/*      */   }
/*      */ 
/*      */   public void addInvokestatic(CtClass clazz, String name, CtClass returnType, CtClass[] paramTypes)
/*      */   {
/*  970 */     String desc = Descriptor.ofMethod(returnType, paramTypes);
/*  971 */     addInvokestatic(clazz, name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokestatic(CtClass clazz, String name, String desc)
/*      */   {
/*  984 */     addInvokestatic(this.constPool.addClassInfo(clazz), name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokestatic(String classname, String name, String desc)
/*      */   {
/*  997 */     addInvokestatic(this.constPool.addClassInfo(classname), name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokestatic(int clazz, String name, String desc)
/*      */   {
/* 1011 */     add(184);
/* 1012 */     addIndex(this.constPool.addMethodrefInfo(clazz, name, desc));
/* 1013 */     growStack(Descriptor.dataSize(desc));
/*      */   }
/*      */ 
/*      */   public void addInvokevirtual(CtClass clazz, String name, CtClass returnType, CtClass[] paramTypes)
/*      */   {
/* 1030 */     String desc = Descriptor.ofMethod(returnType, paramTypes);
/* 1031 */     addInvokevirtual(clazz, name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokevirtual(CtClass clazz, String name, String desc)
/*      */   {
/* 1048 */     addInvokevirtual(this.constPool.addClassInfo(clazz), name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokevirtual(String classname, String name, String desc)
/*      */   {
/* 1065 */     addInvokevirtual(this.constPool.addClassInfo(classname), name, desc);
/*      */   }
/*      */ 
/*      */   public void addInvokevirtual(int clazz, String name, String desc)
/*      */   {
/* 1083 */     add(182);
/* 1084 */     addIndex(this.constPool.addMethodrefInfo(clazz, name, desc));
/* 1085 */     growStack(Descriptor.dataSize(desc) - 1);
/*      */   }
/*      */ 
/*      */   public void addInvokeinterface(CtClass clazz, String name, CtClass returnType, CtClass[] paramTypes, int count)
/*      */   {
/* 1100 */     String desc = Descriptor.ofMethod(returnType, paramTypes);
/* 1101 */     addInvokeinterface(clazz, name, desc, count);
/*      */   }
/*      */ 
/*      */   public void addInvokeinterface(CtClass clazz, String name, String desc, int count)
/*      */   {
/* 1116 */     addInvokeinterface(this.constPool.addClassInfo(clazz), name, desc, count);
/*      */   }
/*      */ 
/*      */   public void addInvokeinterface(String classname, String name, String desc, int count)
/*      */   {
/* 1132 */     addInvokeinterface(this.constPool.addClassInfo(classname), name, desc, count);
/*      */   }
/*      */ 
/*      */   public void addInvokeinterface(int clazz, String name, String desc, int count)
/*      */   {
/* 1149 */     add(185);
/* 1150 */     addIndex(this.constPool.addInterfaceMethodrefInfo(clazz, name, desc));
/* 1151 */     add(count);
/* 1152 */     add(0);
/* 1153 */     growStack(Descriptor.dataSize(desc) - 1);
/*      */   }
/*      */ 
/*      */   public void addLdc(String s)
/*      */   {
/* 1163 */     addLdc(this.constPool.addStringInfo(s));
/*      */   }
/*      */ 
/*      */   public void addLdc(int i)
/*      */   {
/* 1172 */     if (i > 255) {
/* 1173 */       addOpcode(19);
/* 1174 */       addIndex(i);
/*      */     }
/*      */     else {
/* 1177 */       addOpcode(18);
/* 1178 */       add(i);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addLdc2w(long l)
/*      */   {
/* 1186 */     addOpcode(20);
/* 1187 */     addIndex(this.constPool.addLongInfo(l));
/*      */   }
/*      */ 
/*      */   public void addLdc2w(double d)
/*      */   {
/* 1194 */     addOpcode(20);
/* 1195 */     addIndex(this.constPool.addDoubleInfo(d));
/*      */   }
/*      */ 
/*      */   public void addNew(CtClass clazz)
/*      */   {
/* 1204 */     addOpcode(187);
/* 1205 */     addIndex(this.constPool.addClassInfo(clazz));
/*      */   }
/*      */ 
/*      */   public void addNew(String classname)
/*      */   {
/* 1214 */     addOpcode(187);
/* 1215 */     addIndex(this.constPool.addClassInfo(classname));
/*      */   }
/*      */ 
/*      */   public void addAnewarray(String classname)
/*      */   {
/* 1224 */     addOpcode(189);
/* 1225 */     addIndex(this.constPool.addClassInfo(classname));
/*      */   }
/*      */ 
/*      */   public void addAnewarray(CtClass clazz, int length)
/*      */   {
/* 1235 */     addIconst(length);
/* 1236 */     addOpcode(189);
/* 1237 */     addIndex(this.constPool.addClassInfo(clazz));
/*      */   }
/*      */ 
/*      */   public void addNewarray(int atype, int length)
/*      */   {
/* 1247 */     addIconst(length);
/* 1248 */     addOpcode(188);
/* 1249 */     add(atype);
/*      */   }
/*      */ 
/*      */   public int addMultiNewarray(CtClass clazz, int[] dimensions)
/*      */   {
/* 1260 */     int len = dimensions.length;
/* 1261 */     for (int i = 0; i < len; i++) {
/* 1262 */       addIconst(dimensions[i]);
/*      */     }
/* 1264 */     growStack(len);
/* 1265 */     return addMultiNewarray(clazz, len);
/*      */   }
/*      */ 
/*      */   public int addMultiNewarray(CtClass clazz, int dim)
/*      */   {
/* 1277 */     add(197);
/* 1278 */     addIndex(this.constPool.addClassInfo(clazz));
/* 1279 */     add(dim);
/* 1280 */     growStack(1 - dim);
/* 1281 */     return dim;
/*      */   }
/*      */ 
/*      */   public int addMultiNewarray(String desc, int dim)
/*      */   {
/* 1292 */     add(197);
/* 1293 */     addIndex(this.constPool.addClassInfo(desc));
/* 1294 */     add(dim);
/* 1295 */     growStack(1 - dim);
/* 1296 */     return dim;
/*      */   }
/*      */ 
/*      */   public void addPutfield(CtClass c, String name, String desc)
/*      */   {
/* 1307 */     addPutfield0(c, null, name, desc);
/*      */   }
/*      */ 
/*      */   public void addPutfield(String classname, String name, String desc)
/*      */   {
/* 1319 */     addPutfield0(null, classname, name, desc);
/*      */   }
/*      */ 
/*      */   private void addPutfield0(CtClass target, String classname, String name, String desc)
/*      */   {
/* 1324 */     add(181);
/*      */ 
/* 1326 */     int ci = classname == null ? this.constPool.addClassInfo(target) : this.constPool.addClassInfo(classname);
/*      */ 
/* 1328 */     addIndex(this.constPool.addFieldrefInfo(ci, name, desc));
/* 1329 */     growStack(-1 - Descriptor.dataSize(desc));
/*      */   }
/*      */ 
/*      */   public void addPutstatic(CtClass c, String name, String desc)
/*      */   {
/* 1340 */     addPutstatic0(c, null, name, desc);
/*      */   }
/*      */ 
/*      */   public void addPutstatic(String classname, String fieldName, String desc)
/*      */   {
/* 1352 */     addPutstatic0(null, classname, fieldName, desc);
/*      */   }
/*      */ 
/*      */   private void addPutstatic0(CtClass target, String classname, String fieldName, String desc)
/*      */   {
/* 1357 */     add(179);
/*      */ 
/* 1359 */     int ci = classname == null ? this.constPool.addClassInfo(target) : this.constPool.addClassInfo(classname);
/*      */ 
/* 1361 */     addIndex(this.constPool.addFieldrefInfo(ci, fieldName, desc));
/* 1362 */     growStack(-Descriptor.dataSize(desc));
/*      */   }
/*      */ 
/*      */   public void addReturn(CtClass type)
/*      */   {
/* 1371 */     if (type == null) {
/* 1372 */       addOpcode(177);
/* 1373 */     } else if (type.isPrimitive()) {
/* 1374 */       CtPrimitiveType ptype = (CtPrimitiveType)type;
/* 1375 */       addOpcode(ptype.getReturnOp());
/*      */     }
/*      */     else {
/* 1378 */       addOpcode(176);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addRet(int var)
/*      */   {
/* 1387 */     if (var < 256) {
/* 1388 */       addOpcode(169);
/* 1389 */       add(var);
/*      */     }
/*      */     else {
/* 1392 */       addOpcode(196);
/* 1393 */       addOpcode(169);
/* 1394 */       addIndex(var);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addPrintln(String message)
/*      */   {
/* 1405 */     addGetstatic("java.lang.System", "err", "Ljava/io/PrintStream;");
/* 1406 */     addLdc(message);
/* 1407 */     addInvokevirtual("java.io.PrintStream", "println", "(Ljava/lang/String;)V");
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.Bytecode
 * JD-Core Version:    0.6.2
 */