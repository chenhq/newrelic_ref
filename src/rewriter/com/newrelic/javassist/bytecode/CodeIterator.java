/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ 
/*      */ public class CodeIterator
/*      */   implements Opcode
/*      */ {
/*      */   protected CodeAttribute codeAttr;
/*      */   protected byte[] bytecode;
/*      */   protected int endPos;
/*      */   protected int currentPos;
/*      */   protected int mark;
/*  717 */   private static final int[] opcodeLength = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 0, 0, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 5, 0, 3, 2, 3, 1, 1, 3, 3, 1, 1, 0, 4, 3, 3, 5, 5 };
/*      */ 
/*      */   protected CodeIterator(CodeAttribute ca)
/*      */   {
/*   42 */     this.codeAttr = ca;
/*   43 */     this.bytecode = ca.getCode();
/*   44 */     begin();
/*      */   }
/*      */ 
/*      */   public void begin()
/*      */   {
/*   51 */     this.currentPos = (this.mark = 0);
/*   52 */     this.endPos = getCodeLength();
/*      */   }
/*      */ 
/*      */   public void move(int index)
/*      */   {
/*   68 */     this.currentPos = index;
/*      */   }
/*      */ 
/*      */   public void setMark(int index)
/*      */   {
/*   82 */     this.mark = index;
/*      */   }
/*      */ 
/*      */   public int getMark()
/*      */   {
/*   93 */     return this.mark;
/*      */   }
/*      */ 
/*      */   public CodeAttribute get()
/*      */   {
/*   99 */     return this.codeAttr;
/*      */   }
/*      */ 
/*      */   public int getCodeLength()
/*      */   {
/*  106 */     return this.bytecode.length;
/*      */   }
/*      */ 
/*      */   public int byteAt(int index)
/*      */   {
/*  112 */     return this.bytecode[index] & 0xFF;
/*      */   }
/*      */ 
/*      */   public void writeByte(int value, int index)
/*      */   {
/*  118 */     this.bytecode[index] = ((byte)value);
/*      */   }
/*      */ 
/*      */   public int u16bitAt(int index)
/*      */   {
/*  125 */     return ByteArray.readU16bit(this.bytecode, index);
/*      */   }
/*      */ 
/*      */   public int s16bitAt(int index)
/*      */   {
/*  132 */     return ByteArray.readS16bit(this.bytecode, index);
/*      */   }
/*      */ 
/*      */   public void write16bit(int value, int index)
/*      */   {
/*  139 */     ByteArray.write16bit(value, this.bytecode, index);
/*      */   }
/*      */ 
/*      */   public int s32bitAt(int index)
/*      */   {
/*  146 */     return ByteArray.read32bit(this.bytecode, index);
/*      */   }
/*      */ 
/*      */   public void write32bit(int value, int index)
/*      */   {
/*  153 */     ByteArray.write32bit(value, this.bytecode, index);
/*      */   }
/*      */ 
/*      */   public void write(byte[] code, int index)
/*      */   {
/*  162 */     int len = code.length;
/*  163 */     for (int j = 0; j < len; j++)
/*  164 */       this.bytecode[(index++)] = code[j];
/*      */   }
/*      */ 
/*      */   public boolean hasNext()
/*      */   {
/*  170 */     return this.currentPos < this.endPos;
/*      */   }
/*      */ 
/*      */   public int next()
/*      */     throws BadBytecode
/*      */   {
/*  183 */     int pos = this.currentPos;
/*  184 */     this.currentPos = nextOpcode(this.bytecode, pos);
/*  185 */     return pos;
/*      */   }
/*      */ 
/*      */   public int lookAhead()
/*      */   {
/*  197 */     return this.currentPos;
/*      */   }
/*      */ 
/*      */   public int skipConstructor()
/*      */     throws BadBytecode
/*      */   {
/*  219 */     return skipSuperConstructor0(-1);
/*      */   }
/*      */ 
/*      */   public int skipSuperConstructor()
/*      */     throws BadBytecode
/*      */   {
/*  241 */     return skipSuperConstructor0(0);
/*      */   }
/*      */ 
/*      */   public int skipThisConstructor()
/*      */     throws BadBytecode
/*      */   {
/*  263 */     return skipSuperConstructor0(1);
/*      */   }
/*      */ 
/*      */   private int skipSuperConstructor0(int skipThis)
/*      */     throws BadBytecode
/*      */   {
/*  269 */     begin();
/*  270 */     ConstPool cp = this.codeAttr.getConstPool();
/*  271 */     String thisClassName = this.codeAttr.getDeclaringClass();
/*  272 */     int nested = 0;
/*  273 */     while (hasNext()) {
/*  274 */       int index = next();
/*  275 */       int c = byteAt(index);
/*  276 */       if (c == 187) {
/*  277 */         nested++;
/*  278 */       } else if (c == 183) {
/*  279 */         int mref = ByteArray.readU16bit(this.bytecode, index + 1);
/*  280 */         if (cp.getMethodrefName(mref).equals("<init>")) {
/*  281 */           nested--; if (nested < 0) {
/*  282 */             if (skipThis < 0) {
/*  283 */               return index;
/*      */             }
/*  285 */             String cname = cp.getMethodrefClassName(mref);
/*  286 */             if (cname.equals(thisClassName) != skipThis > 0) break;
/*  287 */             return index;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  294 */     begin();
/*  295 */     return -1;
/*      */   }
/*      */ 
/*      */   public int insert(byte[] code)
/*      */     throws BadBytecode
/*      */   {
/*  319 */     return insert0(this.currentPos, code, false);
/*      */   }
/*      */ 
/*      */   public void insert(int pos, byte[] code)
/*      */     throws BadBytecode
/*      */   {
/*  344 */     insert0(pos, code, false);
/*      */   }
/*      */ 
/*      */   public int insertAt(int pos, byte[] code)
/*      */     throws BadBytecode
/*      */   {
/*  368 */     return insert0(pos, code, false);
/*      */   }
/*      */ 
/*      */   public int insertEx(byte[] code)
/*      */     throws BadBytecode
/*      */   {
/*  392 */     return insert0(this.currentPos, code, true);
/*      */   }
/*      */ 
/*      */   public void insertEx(int pos, byte[] code)
/*      */     throws BadBytecode
/*      */   {
/*  417 */     insert0(pos, code, true);
/*      */   }
/*      */ 
/*      */   public int insertExAt(int pos, byte[] code)
/*      */     throws BadBytecode
/*      */   {
/*  441 */     return insert0(pos, code, true);
/*      */   }
/*      */ 
/*      */   private int insert0(int pos, byte[] code, boolean exclusive)
/*      */     throws BadBytecode
/*      */   {
/*  451 */     int len = code.length;
/*  452 */     if (len <= 0) {
/*  453 */       return pos;
/*      */     }
/*      */ 
/*  456 */     pos = insertGapAt(pos, len, exclusive).position;
/*      */ 
/*  458 */     int p = pos;
/*  459 */     for (int j = 0; j < len; j++) {
/*  460 */       this.bytecode[(p++)] = code[j];
/*      */     }
/*  462 */     return pos;
/*      */   }
/*      */ 
/*      */   public int insertGap(int length)
/*      */     throws BadBytecode
/*      */   {
/*  481 */     return insertGapAt(this.currentPos, length, false).position;
/*      */   }
/*      */ 
/*      */   public int insertGap(int pos, int length)
/*      */     throws BadBytecode
/*      */   {
/*  501 */     return insertGapAt(pos, length, false).length;
/*      */   }
/*      */ 
/*      */   public int insertExGap(int length)
/*      */     throws BadBytecode
/*      */   {
/*  520 */     return insertGapAt(this.currentPos, length, true).position;
/*      */   }
/*      */ 
/*      */   public int insertExGap(int pos, int length)
/*      */     throws BadBytecode
/*      */   {
/*  540 */     return insertGapAt(pos, length, true).length;
/*      */   }
/*      */ 
/*      */   public Gap insertGapAt(int pos, int length, boolean exclusive)
/*      */     throws BadBytecode
/*      */   {
/*  599 */     Gap gap = new Gap();
/*  600 */     if (length <= 0) {
/*  601 */       gap.position = pos;
/*  602 */       gap.length = 0;
/*  603 */       return gap;
/*      */     }
/*      */     int length2;
/*      */     byte[] c;
/*      */     int length2;
/*  608 */     if (this.bytecode.length + length > 32767)
/*      */     {
/*  610 */       byte[] c = insertGapCore0w(this.bytecode, pos, length, exclusive, get().getExceptionTable(), this.codeAttr, gap);
/*      */ 
/*  612 */       pos = gap.position;
/*  613 */       length2 = length;
/*      */     }
/*      */     else {
/*  616 */       int cur = this.currentPos;
/*  617 */       c = insertGapCore0(this.bytecode, pos, length, exclusive, get().getExceptionTable(), this.codeAttr);
/*      */ 
/*  620 */       length2 = c.length - this.bytecode.length;
/*  621 */       gap.position = pos;
/*  622 */       gap.length = length2;
/*  623 */       if (cur >= pos) {
/*  624 */         this.currentPos = (cur + length2);
/*      */       }
/*  626 */       if ((this.mark > pos) || ((this.mark == pos) && (exclusive))) {
/*  627 */         this.mark += length2;
/*      */       }
/*      */     }
/*  630 */     this.codeAttr.setCode(c);
/*  631 */     this.bytecode = c;
/*  632 */     this.endPos = getCodeLength();
/*  633 */     updateCursors(pos, length2);
/*  634 */     return gap;
/*      */   }
/*      */ 
/*      */   protected void updateCursors(int pos, int length)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void insert(ExceptionTable et, int offset)
/*      */   {
/*  657 */     this.codeAttr.getExceptionTable().add(0, et, offset);
/*      */   }
/*      */ 
/*      */   public int append(byte[] code)
/*      */   {
/*  667 */     int size = getCodeLength();
/*  668 */     int len = code.length;
/*  669 */     if (len <= 0) {
/*  670 */       return size;
/*      */     }
/*  672 */     appendGap(len);
/*  673 */     byte[] dest = this.bytecode;
/*  674 */     for (int i = 0; i < len; i++) {
/*  675 */       dest[(i + size)] = code[i];
/*      */     }
/*  677 */     return size;
/*      */   }
/*      */ 
/*      */   public void appendGap(int gapLength)
/*      */   {
/*  686 */     byte[] code = this.bytecode;
/*  687 */     int codeLength = code.length;
/*  688 */     byte[] newcode = new byte[codeLength + gapLength];
/*      */ 
/*  691 */     for (int i = 0; i < codeLength; i++) {
/*  692 */       newcode[i] = code[i];
/*      */     }
/*  694 */     for (i = codeLength; i < codeLength + gapLength; i++) {
/*  695 */       newcode[i] = 0;
/*      */     }
/*  697 */     this.codeAttr.setCode(newcode);
/*  698 */     this.bytecode = newcode;
/*  699 */     this.endPos = getCodeLength();
/*      */   }
/*      */ 
/*      */   public void append(ExceptionTable et, int offset)
/*      */   {
/*  711 */     ExceptionTable table = this.codeAttr.getExceptionTable();
/*  712 */     table.add(table.size(), et, offset);
/*      */   }
/*      */ 
/*      */   static int nextOpcode(byte[] code, int index)
/*      */     throws BadBytecode
/*      */   {
/*      */     int opcode;
/*      */     try
/*      */     {
/*  740 */       opcode = code[index] & 0xFF;
/*      */     }
/*      */     catch (IndexOutOfBoundsException e) {
/*  743 */       throw new BadBytecode("invalid opcode address");
/*      */     }
/*      */     try
/*      */     {
/*  747 */       int len = opcodeLength[opcode];
/*  748 */       if (len > 0)
/*  749 */         return index + len;
/*  750 */       if (opcode == 196) {
/*  751 */         if (code[(index + 1)] == -124) {
/*  752 */           return index + 6;
/*      */         }
/*  754 */         return index + 4;
/*      */       }
/*  756 */       int index2 = (index & 0xFFFFFFFC) + 8;
/*  757 */       if (opcode == 171) {
/*  758 */         int npairs = ByteArray.read32bit(code, index2);
/*  759 */         return index2 + npairs * 8 + 4;
/*      */       }
/*  761 */       if (opcode == 170) {
/*  762 */         int low = ByteArray.read32bit(code, index2);
/*  763 */         int high = ByteArray.read32bit(code, index2 + 4);
/*  764 */         return index2 + (high - low + 1) * 4 + 8;
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (IndexOutOfBoundsException e)
/*      */     {
/*      */     }
/*      */ 
/*  774 */     throw new BadBytecode(opcode);
/*      */   }
/*      */ 
/*      */   static byte[] insertGapCore0(byte[] code, int where, int gapLength, boolean exclusive, ExceptionTable etable, CodeAttribute ca)
/*      */     throws BadBytecode
/*      */   {
/*  800 */     if (gapLength <= 0)
/*  801 */       return code;
/*      */     try
/*      */     {
/*  804 */       return insertGapCore1(code, where, gapLength, exclusive, etable, ca);
/*      */     }
/*      */     catch (AlignmentException e) {
/*      */       try {
/*  808 */         return insertGapCore1(code, where, gapLength + 3 & 0xFFFFFFFC, exclusive, etable, ca);
/*      */       } catch (AlignmentException e2) {
/*      */       }
/*      */     }
/*  812 */     throw new RuntimeException("fatal error?");
/*      */   }
/*      */ 
/*      */   private static byte[] insertGapCore1(byte[] code, int where, int gapLength, boolean exclusive, ExceptionTable etable, CodeAttribute ca)
/*      */     throws BadBytecode, CodeIterator.AlignmentException
/*      */   {
/*  822 */     int codeLength = code.length;
/*  823 */     byte[] newcode = new byte[codeLength + gapLength];
/*  824 */     insertGap2(code, where, gapLength, codeLength, newcode, exclusive);
/*  825 */     etable.shiftPc(where, gapLength, exclusive);
/*  826 */     LineNumberAttribute na = (LineNumberAttribute)ca.getAttribute("LineNumberTable");
/*      */ 
/*  828 */     if (na != null) {
/*  829 */       na.shiftPc(where, gapLength, exclusive);
/*      */     }
/*  831 */     LocalVariableAttribute va = (LocalVariableAttribute)ca.getAttribute("LocalVariableTable");
/*      */ 
/*  833 */     if (va != null) {
/*  834 */       va.shiftPc(where, gapLength, exclusive);
/*      */     }
/*  836 */     LocalVariableAttribute vta = (LocalVariableAttribute)ca.getAttribute("LocalVariableTypeTable");
/*      */ 
/*  839 */     if (vta != null) {
/*  840 */       vta.shiftPc(where, gapLength, exclusive);
/*      */     }
/*  842 */     StackMapTable smt = (StackMapTable)ca.getAttribute("StackMapTable");
/*  843 */     if (smt != null) {
/*  844 */       smt.shiftPc(where, gapLength, exclusive);
/*      */     }
/*  846 */     StackMap sm = (StackMap)ca.getAttribute("StackMap");
/*  847 */     if (sm != null) {
/*  848 */       sm.shiftPc(where, gapLength, exclusive);
/*      */     }
/*  850 */     return newcode;
/*      */   }
/*      */ 
/*      */   private static void insertGap2(byte[] code, int where, int gapLength, int endPos, byte[] newcode, boolean exclusive)
/*      */     throws BadBytecode, CodeIterator.AlignmentException
/*      */   {
/*  858 */     int i = 0;
/*  859 */     int j = 0;
/*      */     int nextPos;
/*  860 */     for (; i < endPos; i = nextPos) {
/*  861 */       if (i == where) {
/*  862 */         int j2 = j + gapLength;
/*  863 */         while (j < j2) {
/*  864 */           newcode[(j++)] = 0;
/*      */         }
/*      */       }
/*  867 */       nextPos = nextOpcode(code, i);
/*  868 */       int inst = code[i] & 0xFF;
/*      */ 
/*  870 */       if (((153 <= inst) && (inst <= 168)) || (inst == 198) || (inst == 199))
/*      */       {
/*  873 */         int offset = code[(i + 1)] << 8 | code[(i + 2)] & 0xFF;
/*  874 */         offset = newOffset(i, offset, where, gapLength, exclusive);
/*  875 */         newcode[j] = code[i];
/*  876 */         ByteArray.write16bit(offset, newcode, j + 1);
/*  877 */         j += 3;
/*      */       }
/*  879 */       else if ((inst == 200) || (inst == 201))
/*      */       {
/*  881 */         int offset = ByteArray.read32bit(code, i + 1);
/*  882 */         offset = newOffset(i, offset, where, gapLength, exclusive);
/*  883 */         newcode[(j++)] = code[i];
/*  884 */         ByteArray.write32bit(offset, newcode, j);
/*  885 */         j += 4;
/*      */       }
/*  887 */       else if (inst == 170) {
/*  888 */         if ((i != j) && ((gapLength & 0x3) != 0)) {
/*  889 */           throw new AlignmentException();
/*      */         }
/*  891 */         int i2 = (i & 0xFFFFFFFC) + 4;
/*      */ 
/*  898 */         j = copyGapBytes(newcode, j, code, i, i2);
/*      */ 
/*  900 */         int defaultbyte = newOffset(i, ByteArray.read32bit(code, i2), where, gapLength, exclusive);
/*      */ 
/*  902 */         ByteArray.write32bit(defaultbyte, newcode, j);
/*  903 */         int lowbyte = ByteArray.read32bit(code, i2 + 4);
/*  904 */         ByteArray.write32bit(lowbyte, newcode, j + 4);
/*  905 */         int highbyte = ByteArray.read32bit(code, i2 + 8);
/*  906 */         ByteArray.write32bit(highbyte, newcode, j + 8);
/*  907 */         j += 12;
/*  908 */         int i0 = i2 + 12;
/*  909 */         i2 = i0 + (highbyte - lowbyte + 1) * 4;
/*  910 */         while (i0 < i2) {
/*  911 */           int offset = newOffset(i, ByteArray.read32bit(code, i0), where, gapLength, exclusive);
/*      */ 
/*  913 */           ByteArray.write32bit(offset, newcode, j);
/*  914 */           j += 4;
/*  915 */           i0 += 4;
/*      */         }
/*      */       }
/*  918 */       else if (inst == 171) {
/*  919 */         if ((i != j) && ((gapLength & 0x3) != 0)) {
/*  920 */           throw new AlignmentException();
/*      */         }
/*  922 */         int i2 = (i & 0xFFFFFFFC) + 4;
/*      */ 
/*  930 */         j = copyGapBytes(newcode, j, code, i, i2);
/*      */ 
/*  932 */         int defaultbyte = newOffset(i, ByteArray.read32bit(code, i2), where, gapLength, exclusive);
/*      */ 
/*  934 */         ByteArray.write32bit(defaultbyte, newcode, j);
/*  935 */         int npairs = ByteArray.read32bit(code, i2 + 4);
/*  936 */         ByteArray.write32bit(npairs, newcode, j + 4);
/*  937 */         j += 8;
/*  938 */         int i0 = i2 + 8;
/*  939 */         i2 = i0 + npairs * 8;
/*  940 */         while (i0 < i2) {
/*  941 */           ByteArray.copy32bit(code, i0, newcode, j);
/*  942 */           int offset = newOffset(i, ByteArray.read32bit(code, i0 + 4), where, gapLength, exclusive);
/*      */ 
/*  945 */           ByteArray.write32bit(offset, newcode, j + 4);
/*  946 */           j += 8;
/*  947 */           i0 += 8;
/*      */         }
/*      */       }
/*      */       else {
/*  951 */         while (i < nextPos)
/*  952 */           newcode[(j++)] = code[(i++)];
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static int copyGapBytes(byte[] newcode, int j, byte[] code, int i, int iEnd) {
/*  958 */     switch (iEnd - i) {
/*      */     case 4:
/*  960 */       newcode[(j++)] = code[(i++)];
/*      */     case 3:
/*  962 */       newcode[(j++)] = code[(i++)];
/*      */     case 2:
/*  964 */       newcode[(j++)] = code[(i++)];
/*      */     case 1:
/*  966 */       newcode[(j++)] = code[(i++)];
/*      */     }
/*      */ 
/*  970 */     return j;
/*      */   }
/*      */ 
/*      */   private static int newOffset(int i, int offset, int where, int gapLength, boolean exclusive)
/*      */   {
/*  975 */     int target = i + offset;
/*  976 */     if (i < where) {
/*  977 */       if ((where < target) || ((exclusive) && (where == target)))
/*  978 */         offset += gapLength;
/*      */     }
/*  980 */     else if (i == where) {
/*  981 */       if ((target < where) && (exclusive))
/*  982 */         offset -= gapLength;
/*  983 */       else if ((where < target) && (!exclusive)) {
/*  984 */         offset += gapLength;
/*      */       }
/*      */     }
/*  987 */     else if ((target < where) || ((!exclusive) && (where == target))) {
/*  988 */       offset -= gapLength;
/*      */     }
/*  990 */     return offset;
/*      */   }
/*      */ 
/*      */   static byte[] changeLdcToLdcW(byte[] code, ExceptionTable etable, CodeAttribute ca, CodeAttribute.LdcEntry ldcs)
/*      */     throws BadBytecode
/*      */   {
/* 1049 */     ArrayList jumps = makeJumpList(code, code.length);
/* 1050 */     while (ldcs != null) {
/* 1051 */       addLdcW(ldcs, jumps);
/* 1052 */       ldcs = ldcs.next;
/*      */     }
/*      */ 
/* 1055 */     Pointers pointers = new Pointers(0, 0, 0, etable, ca);
/* 1056 */     byte[] r = insertGap2w(code, 0, 0, false, jumps, pointers);
/* 1057 */     return r;
/*      */   }
/*      */ 
/*      */   private static void addLdcW(CodeAttribute.LdcEntry ldcs, ArrayList jumps) {
/* 1061 */     int where = ldcs.where;
/* 1062 */     LdcW ldcw = new LdcW(where, ldcs.index);
/* 1063 */     int s = jumps.size();
/* 1064 */     for (int i = 0; i < s; i++) {
/* 1065 */       if (where < ((Branch)jumps.get(i)).orgPos) {
/* 1066 */         jumps.add(i, ldcw);
/* 1067 */         return;
/*      */       }
/*      */     }
/* 1070 */     jumps.add(ldcw);
/*      */   }
/*      */ 
/*      */   private byte[] insertGapCore0w(byte[] code, int where, int gapLength, boolean exclusive, ExceptionTable etable, CodeAttribute ca, Gap newWhere)
/*      */     throws BadBytecode
/*      */   {
/* 1090 */     if (gapLength <= 0) {
/* 1091 */       return code;
/*      */     }
/* 1093 */     ArrayList jumps = makeJumpList(code, code.length);
/* 1094 */     Pointers pointers = new Pointers(this.currentPos, this.mark, where, etable, ca);
/* 1095 */     byte[] r = insertGap2w(code, where, gapLength, exclusive, jumps, pointers);
/* 1096 */     this.currentPos = pointers.cursor;
/* 1097 */     this.mark = pointers.mark;
/* 1098 */     int where2 = pointers.mark0;
/* 1099 */     if ((where2 == this.currentPos) && (!exclusive)) {
/* 1100 */       this.currentPos += gapLength;
/*      */     }
/* 1102 */     if (exclusive) {
/* 1103 */       where2 -= gapLength;
/*      */     }
/* 1105 */     newWhere.position = where2;
/* 1106 */     newWhere.length = gapLength;
/* 1107 */     return r;
/*      */   }
/*      */ 
/*      */   private static byte[] insertGap2w(byte[] code, int where, int gapLength, boolean exclusive, ArrayList jumps, Pointers ptrs)
/*      */     throws BadBytecode
/*      */   {
/* 1114 */     int n = jumps.size();
/* 1115 */     if (gapLength > 0) {
/* 1116 */       ptrs.shiftPc(where, gapLength, exclusive);
/* 1117 */       for (int i = 0; i < n; i++) {
/* 1118 */         ((Branch)jumps.get(i)).shift(where, gapLength, exclusive);
/*      */       }
/*      */     }
/* 1121 */     boolean unstable = true;
/*      */     do {
/* 1123 */       while (unstable) {
/* 1124 */         unstable = false;
/* 1125 */         for (int i = 0; i < n; i++) {
/* 1126 */           Branch b = (Branch)jumps.get(i);
/* 1127 */           if (b.expanded()) {
/* 1128 */             unstable = true;
/* 1129 */             int p = b.pos;
/* 1130 */             int delta = b.deltaSize();
/* 1131 */             ptrs.shiftPc(p, delta, false);
/* 1132 */             for (int j = 0; j < n; j++) {
/* 1133 */               ((Branch)jumps.get(j)).shift(p, delta, false);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1138 */       for (int i = 0; i < n; i++) {
/* 1139 */         Branch b = (Branch)jumps.get(i);
/* 1140 */         int diff = b.gapChanged();
/* 1141 */         if (diff > 0) {
/* 1142 */           unstable = true;
/* 1143 */           int p = b.pos;
/* 1144 */           ptrs.shiftPc(p, diff, false);
/* 1145 */           for (int j = 0; j < n; j++)
/* 1146 */             ((Branch)jumps.get(j)).shift(p, diff, false); 
/*      */         }
/*      */       }
/*      */     }
/* 1149 */     while (unstable);
/*      */ 
/* 1151 */     return makeExapndedCode(code, jumps, where, gapLength);
/*      */   }
/*      */ 
/*      */   private static ArrayList makeJumpList(byte[] code, int endPos)
/*      */     throws BadBytecode
/*      */   {
/* 1157 */     ArrayList jumps = new ArrayList();
/*      */     int nextPos;
/* 1159 */     for (int i = 0; i < endPos; i = nextPos) {
/* 1160 */       nextPos = nextOpcode(code, i);
/* 1161 */       int inst = code[i] & 0xFF;
/*      */ 
/* 1163 */       if (((153 <= inst) && (inst <= 168)) || (inst == 198) || (inst == 199))
/*      */       {
/* 1166 */         int offset = code[(i + 1)] << 8 | code[(i + 2)] & 0xFF;
/*      */         Branch b;
/*      */         Branch b;
/* 1168 */         if ((inst == 167) || (inst == 168))
/* 1169 */           b = new Jump16(i, offset);
/*      */         else {
/* 1171 */           b = new If16(i, offset);
/*      */         }
/* 1173 */         jumps.add(b);
/*      */       }
/* 1175 */       else if ((inst == 200) || (inst == 201))
/*      */       {
/* 1177 */         int offset = ByteArray.read32bit(code, i + 1);
/* 1178 */         jumps.add(new Jump32(i, offset));
/*      */       }
/* 1180 */       else if (inst == 170) {
/* 1181 */         int i2 = (i & 0xFFFFFFFC) + 4;
/* 1182 */         int defaultbyte = ByteArray.read32bit(code, i2);
/* 1183 */         int lowbyte = ByteArray.read32bit(code, i2 + 4);
/* 1184 */         int highbyte = ByteArray.read32bit(code, i2 + 8);
/* 1185 */         int i0 = i2 + 12;
/* 1186 */         int size = highbyte - lowbyte + 1;
/* 1187 */         int[] offsets = new int[size];
/* 1188 */         for (int j = 0; j < size; j++) {
/* 1189 */           offsets[j] = ByteArray.read32bit(code, i0);
/* 1190 */           i0 += 4;
/*      */         }
/*      */ 
/* 1193 */         jumps.add(new Table(i, defaultbyte, lowbyte, highbyte, offsets));
/*      */       }
/* 1195 */       else if (inst == 171) {
/* 1196 */         int i2 = (i & 0xFFFFFFFC) + 4;
/* 1197 */         int defaultbyte = ByteArray.read32bit(code, i2);
/* 1198 */         int npairs = ByteArray.read32bit(code, i2 + 4);
/* 1199 */         int i0 = i2 + 8;
/* 1200 */         int[] matches = new int[npairs];
/* 1201 */         int[] offsets = new int[npairs];
/* 1202 */         for (int j = 0; j < npairs; j++) {
/* 1203 */           matches[j] = ByteArray.read32bit(code, i0);
/* 1204 */           offsets[j] = ByteArray.read32bit(code, i0 + 4);
/* 1205 */           i0 += 8;
/*      */         }
/*      */ 
/* 1208 */         jumps.add(new Lookup(i, defaultbyte, matches, offsets));
/*      */       }
/*      */     }
/*      */ 
/* 1212 */     return jumps;
/*      */   }
/*      */ 
/*      */   private static byte[] makeExapndedCode(byte[] code, ArrayList jumps, int where, int gapLength)
/*      */     throws BadBytecode
/*      */   {
/* 1219 */     int n = jumps.size();
/* 1220 */     int size = code.length + gapLength;
/* 1221 */     for (int i = 0; i < n; i++) {
/* 1222 */       Branch b = (Branch)jumps.get(i);
/* 1223 */       size += b.deltaSize();
/*      */     }
/*      */ 
/* 1226 */     byte[] newcode = new byte[size];
/* 1227 */     int src = 0; int dest = 0; int bindex = 0;
/* 1228 */     int len = code.length;
/*      */     int bpos;
/*      */     Branch b;
/*      */     int bpos;
/* 1231 */     if (0 < n) {
/* 1232 */       Branch b = (Branch)jumps.get(0);
/* 1233 */       bpos = b.orgPos;
/*      */     }
/*      */     else {
/* 1236 */       b = null;
/* 1237 */       bpos = len;
/*      */     }
/*      */ 
/* 1240 */     while (src < len) {
/* 1241 */       if (src == where) {
/* 1242 */         int pos2 = dest + gapLength;
/* 1243 */         while (dest < pos2) {
/* 1244 */           newcode[(dest++)] = 0;
/*      */         }
/*      */       }
/* 1247 */       if (src != bpos) {
/* 1248 */         newcode[(dest++)] = code[(src++)];
/*      */       } else {
/* 1250 */         int s = b.write(src, code, dest, newcode);
/* 1251 */         src += s;
/* 1252 */         dest += s + b.deltaSize();
/* 1253 */         bindex++; if (bindex < n) {
/* 1254 */           b = (Branch)jumps.get(bindex);
/* 1255 */           bpos = b.orgPos;
/*      */         }
/*      */         else {
/* 1258 */           b = null;
/* 1259 */           bpos = len;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1264 */     return newcode;
/*      */   }
/*      */ 
/*      */   static class Lookup extends CodeIterator.Switcher
/*      */   {
/*      */     int[] matches;
/*      */ 
/*      */     Lookup(int pos, int defaultByte, int[] matches, int[] offsets)
/*      */     {
/* 1530 */       super(defaultByte, offsets);
/* 1531 */       this.matches = matches;
/*      */     }
/*      */ 
/*      */     int write2(int dest, byte[] newcode) {
/* 1535 */       int n = this.matches.length;
/* 1536 */       ByteArray.write32bit(n, newcode, dest);
/* 1537 */       dest += 4;
/* 1538 */       for (int i = 0; i < n; i++) {
/* 1539 */         ByteArray.write32bit(this.matches[i], newcode, dest);
/* 1540 */         ByteArray.write32bit(this.offsets[i], newcode, dest + 4);
/* 1541 */         dest += 8;
/*      */       }
/*      */ 
/* 1544 */       return 4 + 8 * n;
/*      */     }
/*      */     int tableSize() {
/* 1547 */       return 4 + 8 * this.matches.length;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class Table extends CodeIterator.Switcher
/*      */   {
/*      */     int low;
/*      */     int high;
/*      */ 
/*      */     Table(int pos, int defaultByte, int low, int high, int[] offsets)
/*      */     {
/* 1505 */       super(defaultByte, offsets);
/* 1506 */       this.low = low;
/* 1507 */       this.high = high;
/*      */     }
/*      */ 
/*      */     int write2(int dest, byte[] newcode) {
/* 1511 */       ByteArray.write32bit(this.low, newcode, dest);
/* 1512 */       ByteArray.write32bit(this.high, newcode, dest + 4);
/* 1513 */       int n = this.offsets.length;
/* 1514 */       dest += 8;
/* 1515 */       for (int i = 0; i < n; i++) {
/* 1516 */         ByteArray.write32bit(this.offsets[i], newcode, dest);
/* 1517 */         dest += 4;
/*      */       }
/*      */ 
/* 1520 */       return 8 + 4 * n;
/*      */     }
/*      */     int tableSize() {
/* 1523 */       return 8 + 4 * this.offsets.length;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class Switcher extends CodeIterator.Branch
/*      */   {
/*      */     int gap;
/*      */     int defaultByte;
/*      */     int[] offsets;
/*      */ 
/*      */     Switcher(int pos, int defaultByte, int[] offsets)
/*      */     {
/* 1430 */       super();
/* 1431 */       this.gap = (3 - (pos & 0x3));
/* 1432 */       this.defaultByte = defaultByte;
/* 1433 */       this.offsets = offsets;
/*      */     }
/*      */ 
/*      */     void shift(int where, int gapLength, boolean exclusive) {
/* 1437 */       int p = this.pos;
/* 1438 */       this.defaultByte = CodeIterator.newOffset(p, this.defaultByte, where, gapLength, exclusive);
/* 1439 */       int num = this.offsets.length;
/* 1440 */       for (int i = 0; i < num; i++) {
/* 1441 */         this.offsets[i] = CodeIterator.newOffset(p, this.offsets[i], where, gapLength, exclusive);
/*      */       }
/* 1443 */       super.shift(where, gapLength, exclusive);
/*      */     }
/*      */ 
/*      */     int gapChanged() {
/* 1447 */       int newGap = 3 - (this.pos & 0x3);
/* 1448 */       if (newGap > this.gap) {
/* 1449 */         int diff = newGap - this.gap;
/* 1450 */         this.gap = newGap;
/* 1451 */         return diff;
/*      */       }
/*      */ 
/* 1454 */       return 0;
/*      */     }
/*      */ 
/*      */     int deltaSize() {
/* 1458 */       return this.gap - (3 - (this.orgPos & 0x3));
/*      */     }
/*      */ 
/*      */     int write(int src, byte[] code, int dest, byte[] newcode) {
/* 1462 */       int padding = 3 - (this.pos & 0x3);
/* 1463 */       int nops = this.gap - padding;
/* 1464 */       int bytecodeSize = 5 + (3 - (this.orgPos & 0x3)) + tableSize();
/* 1465 */       adjustOffsets(bytecodeSize, nops);
/* 1466 */       newcode[(dest++)] = code[src];
/* 1467 */       while (padding-- > 0) {
/* 1468 */         newcode[(dest++)] = 0;
/*      */       }
/* 1470 */       ByteArray.write32bit(this.defaultByte, newcode, dest);
/* 1471 */       int size = write2(dest + 4, newcode);
/* 1472 */       dest += size + 4;
/* 1473 */       while (nops-- > 0) {
/* 1474 */         newcode[(dest++)] = 0;
/*      */       }
/* 1476 */       return 5 + (3 - (this.orgPos & 0x3)) + size;
/*      */     }
/*      */ 
/*      */     abstract int write2(int paramInt, byte[] paramArrayOfByte);
/*      */ 
/*      */     abstract int tableSize();
/*      */ 
/*      */     void adjustOffsets(int size, int nops)
/*      */     {
/* 1492 */       if (this.defaultByte == size) {
/* 1493 */         this.defaultByte -= nops;
/*      */       }
/* 1495 */       for (int i = 0; i < this.offsets.length; i++)
/* 1496 */         if (this.offsets[i] == size)
/* 1497 */           this.offsets[i] -= nops;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class Jump32 extends CodeIterator.Branch
/*      */   {
/*      */     int offset;
/*      */ 
/*      */     Jump32(int p, int off)
/*      */     {
/* 1409 */       super();
/* 1410 */       this.offset = off;
/*      */     }
/*      */ 
/*      */     void shift(int where, int gapLength, boolean exclusive) {
/* 1414 */       this.offset = CodeIterator.newOffset(this.pos, this.offset, where, gapLength, exclusive);
/* 1415 */       super.shift(where, gapLength, exclusive);
/*      */     }
/*      */ 
/*      */     int write(int src, byte[] code, int dest, byte[] newcode) {
/* 1419 */       newcode[dest] = code[src];
/* 1420 */       ByteArray.write32bit(this.offset, newcode, dest + 1);
/* 1421 */       return 5;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class If16 extends CodeIterator.Branch16
/*      */   {
/*      */     If16(int p, int off)
/*      */     {
/* 1376 */       super(off);
/*      */     }
/*      */ 
/*      */     int deltaSize() {
/* 1380 */       return this.state == 2 ? 5 : 0;
/*      */     }
/*      */ 
/*      */     void write32(int src, byte[] code, int dest, byte[] newcode) {
/* 1384 */       newcode[dest] = ((byte)opcode(code[src] & 0xFF));
/* 1385 */       newcode[(dest + 1)] = 0;
/* 1386 */       newcode[(dest + 2)] = 8;
/* 1387 */       newcode[(dest + 3)] = -56;
/* 1388 */       ByteArray.write32bit(this.offset - 3, newcode, dest + 4);
/*      */     }
/*      */ 
/*      */     int opcode(int op) {
/* 1392 */       if (op == 198)
/* 1393 */         return 199;
/* 1394 */       if (op == 199) {
/* 1395 */         return 198;
/*      */       }
/* 1397 */       if ((op - 153 & 0x1) == 0) {
/* 1398 */         return op + 1;
/*      */       }
/* 1400 */       return op - 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class Jump16 extends CodeIterator.Branch16
/*      */   {
/*      */     Jump16(int p, int off)
/*      */     {
/* 1360 */       super(off);
/*      */     }
/*      */ 
/*      */     int deltaSize() {
/* 1364 */       return this.state == 2 ? 2 : 0;
/*      */     }
/*      */ 
/*      */     void write32(int src, byte[] code, int dest, byte[] newcode) {
/* 1368 */       newcode[dest] = ((byte)((code[src] & 0xFF) == 167 ? 'È' : 'É'));
/* 1369 */       ByteArray.write32bit(this.offset, newcode, dest + 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class Branch16 extends CodeIterator.Branch
/*      */   {
/*      */     int offset;
/*      */     int state;
/*      */     static final int BIT16 = 0;
/*      */     static final int EXPAND = 1;
/*      */     static final int BIT32 = 2;
/*      */ 
/*      */     Branch16(int p, int off)
/*      */     {
/* 1320 */       super();
/* 1321 */       this.offset = off;
/* 1322 */       this.state = 0;
/*      */     }
/*      */ 
/*      */     void shift(int where, int gapLength, boolean exclusive) {
/* 1326 */       this.offset = CodeIterator.newOffset(this.pos, this.offset, where, gapLength, exclusive);
/* 1327 */       super.shift(where, gapLength, exclusive);
/* 1328 */       if ((this.state == 0) && (
/* 1329 */         (this.offset < -32768) || (32767 < this.offset)))
/* 1330 */         this.state = 1;
/*      */     }
/*      */ 
/*      */     boolean expanded() {
/* 1334 */       if (this.state == 1) {
/* 1335 */         this.state = 2;
/* 1336 */         return true;
/*      */       }
/*      */ 
/* 1339 */       return false;
/*      */     }
/*      */     abstract int deltaSize();
/*      */ 
/*      */     abstract void write32(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2);
/*      */ 
/*      */     int write(int src, byte[] code, int dest, byte[] newcode) {
/* 1346 */       if (this.state == 2) {
/* 1347 */         write32(src, code, dest, newcode);
/*      */       } else {
/* 1349 */         newcode[dest] = code[src];
/* 1350 */         ByteArray.write16bit(this.offset, newcode, dest + 1);
/*      */       }
/*      */ 
/* 1353 */       return 3;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LdcW extends CodeIterator.Branch
/*      */   {
/*      */     int index;
/*      */     boolean state;
/*      */ 
/*      */     LdcW(int p, int i)
/*      */     {
/* 1289 */       super();
/* 1290 */       this.index = i;
/* 1291 */       this.state = true;
/*      */     }
/*      */ 
/*      */     boolean expanded() {
/* 1295 */       if (this.state) {
/* 1296 */         this.state = false;
/* 1297 */         return true;
/*      */       }
/*      */ 
/* 1300 */       return false;
/*      */     }
/*      */     int deltaSize() {
/* 1303 */       return 1;
/*      */     }
/*      */     int write(int srcPos, byte[] code, int destPos, byte[] newcode) {
/* 1306 */       newcode[destPos] = 19;
/* 1307 */       ByteArray.write16bit(this.index, newcode, destPos + 1);
/* 1308 */       return 2;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class Branch
/*      */   {
/*      */     int pos;
/*      */     int orgPos;
/*      */ 
/*      */     Branch(int p)
/*      */     {
/* 1269 */       this.pos = (this.orgPos = p);
/*      */     }
/* 1271 */     void shift(int where, int gapLength, boolean exclusive) { if ((where < this.pos) || ((where == this.pos) && (exclusive)))
/* 1272 */         this.pos += gapLength; }
/*      */ 
/*      */     boolean expanded() {
/* 1275 */       return false; } 
/* 1276 */     int gapChanged() { return 0; } 
/* 1277 */     int deltaSize() { return 0; }
/*      */ 
/*      */ 
/*      */     abstract int write(int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2);
/*      */   }
/*      */ 
/*      */   static class Pointers
/*      */   {
/*      */     int cursor;
/*      */     int mark0;
/*      */     int mark;
/*      */     ExceptionTable etable;
/*      */     LineNumberAttribute line;
/*      */     LocalVariableAttribute vars;
/*      */     LocalVariableAttribute types;
/*      */     StackMapTable stack;
/*      */     StackMap stack2;
/*      */ 
/*      */     Pointers(int cur, int m, int m0, ExceptionTable et, CodeAttribute ca)
/*      */     {
/* 1003 */       this.cursor = cur;
/* 1004 */       this.mark = m;
/* 1005 */       this.mark0 = m0;
/* 1006 */       this.etable = et;
/* 1007 */       this.line = ((LineNumberAttribute)ca.getAttribute("LineNumberTable"));
/* 1008 */       this.vars = ((LocalVariableAttribute)ca.getAttribute("LocalVariableTable"));
/* 1009 */       this.types = ((LocalVariableAttribute)ca.getAttribute("LocalVariableTypeTable"));
/* 1010 */       this.stack = ((StackMapTable)ca.getAttribute("StackMapTable"));
/* 1011 */       this.stack2 = ((StackMap)ca.getAttribute("StackMap"));
/*      */     }
/*      */ 
/*      */     void shiftPc(int where, int gapLength, boolean exclusive) throws BadBytecode {
/* 1015 */       if ((where < this.cursor) || ((where == this.cursor) && (exclusive))) {
/* 1016 */         this.cursor += gapLength;
/*      */       }
/* 1018 */       if ((where < this.mark) || ((where == this.mark) && (exclusive))) {
/* 1019 */         this.mark += gapLength;
/*      */       }
/* 1021 */       if ((where < this.mark0) || ((where == this.mark0) && (exclusive))) {
/* 1022 */         this.mark0 += gapLength;
/*      */       }
/* 1024 */       this.etable.shiftPc(where, gapLength, exclusive);
/* 1025 */       if (this.line != null) {
/* 1026 */         this.line.shiftPc(where, gapLength, exclusive);
/*      */       }
/* 1028 */       if (this.vars != null) {
/* 1029 */         this.vars.shiftPc(where, gapLength, exclusive);
/*      */       }
/* 1031 */       if (this.types != null) {
/* 1032 */         this.types.shiftPc(where, gapLength, exclusive);
/*      */       }
/* 1034 */       if (this.stack != null) {
/* 1035 */         this.stack.shiftPc(where, gapLength, exclusive);
/*      */       }
/* 1037 */       if (this.stack2 != null)
/* 1038 */         this.stack2.shiftPc(where, gapLength, exclusive);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class AlignmentException extends Exception
/*      */   {
/*      */   }
/*      */ 
/*      */   public static class Gap
/*      */   {
/*      */     public int position;
/*      */     public int length;
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.CodeIterator
 * JD-Core Version:    0.6.2
 */