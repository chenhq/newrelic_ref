/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class StackMapTable extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "StackMapTable";
/*     */   public static final int TOP = 0;
/*     */   public static final int INTEGER = 1;
/*     */   public static final int FLOAT = 2;
/*     */   public static final int DOUBLE = 3;
/*     */   public static final int LONG = 4;
/*     */   public static final int NULL = 5;
/*     */   public static final int THIS = 6;
/*     */   public static final int OBJECT = 7;
/*     */   public static final int UNINIT = 8;
/*     */ 
/*     */   StackMapTable(ConstPool cp, byte[] newInfo)
/*     */   {
/*  46 */     super(cp, "StackMapTable", newInfo);
/*     */   }
/*     */ 
/*     */   StackMapTable(ConstPool cp, int name_id, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  52 */     super(cp, name_id, in);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */     throws StackMapTable.RuntimeCopyException
/*     */   {
/*     */     try
/*     */     {
/*  68 */       return new StackMapTable(newCp, new Copier(this.constPool, this.info, newCp).doit());
/*     */     }
/*     */     catch (BadBytecode e) {
/*     */     }
/*  72 */     throw new RuntimeCopyException("bad bytecode. fatal?");
/*     */   }
/*     */ 
/*     */   void write(DataOutputStream out)
/*     */     throws IOException
/*     */   {
/*  90 */     super.write(out);
/*     */   }
/*     */ 
/*     */   public void insertLocal(int index, int tag, int classInfo)
/*     */     throws BadBytecode
/*     */   {
/* 445 */     byte[] data = new InsertLocal(get(), index, tag, classInfo).doit();
/* 446 */     set(data);
/*     */   }
/*     */ 
/*     */   public static int typeTagOf(char descriptor)
/*     */   {
/* 459 */     switch (descriptor) {
/*     */     case 'D':
/* 461 */       return 3;
/*     */     case 'F':
/* 463 */       return 2;
/*     */     case 'J':
/* 465 */       return 4;
/*     */     case 'L':
/*     */     case '[':
/* 468 */       return 7;
/*     */     }
/*     */ 
/* 471 */     return 1;
/*     */   }
/*     */ 
/*     */   public void println(PrintWriter w)
/*     */   {
/* 681 */     Printer.print(this, w);
/*     */   }
/*     */ 
/*     */   public void println(PrintStream ps)
/*     */   {
/* 690 */     Printer.print(this, new PrintWriter(ps, true));
/*     */   }
/*     */ 
/*     */   void shiftPc(int where, int gapSize, boolean exclusive)
/*     */     throws BadBytecode
/*     */   {
/* 791 */     new Shifter(this, where, gapSize, exclusive).doit();
/*     */   }
/*     */ 
/*     */   public void removeNew(int where)
/*     */     throws CannotCompileException
/*     */   {
/*     */     try
/*     */     {
/* 900 */       byte[] data = new NewRemover(get(), where).doit();
/* 901 */       set(data);
/*     */     }
/*     */     catch (BadBytecode e) {
/* 904 */       throw new CannotCompileException("bad stack map table", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class NewRemover extends StackMapTable.SimpleCopy {
/*     */     int posOfNew;
/*     */ 
/*     */     public NewRemover(byte[] data, int pos) {
/* 912 */       super();
/* 913 */       this.posOfNew = pos;
/*     */     }
/*     */ 
/*     */     public void sameLocals(int pos, int offsetDelta, int stackTag, int stackData) {
/* 917 */       if ((stackTag == 8) && (stackData == this.posOfNew))
/* 918 */         super.sameFrame(pos, offsetDelta);
/*     */       else
/* 920 */         super.sameLocals(pos, offsetDelta, stackTag, stackData);
/*     */     }
/*     */ 
/*     */     public void fullFrame(int pos, int offsetDelta, int[] localTags, int[] localData, int[] stackTags, int[] stackData)
/*     */     {
/* 925 */       int n = stackTags.length - 1;
/* 926 */       for (int i = 0; i < n; i++) {
/* 927 */         if ((stackTags[i] == 8) && (stackData[i] == this.posOfNew) && (stackTags[(i + 1)] == 8) && (stackData[(i + 1)] == this.posOfNew))
/*     */         {
/* 929 */           n++;
/* 930 */           int[] stackTags2 = new int[n - 2];
/* 931 */           int[] stackData2 = new int[n - 2];
/* 932 */           int k = 0;
/* 933 */           for (int j = 0; j < n; j++) {
/* 934 */             if (j == i) {
/* 935 */               j++;
/*     */             } else {
/* 937 */               stackTags2[k] = stackTags[j];
/* 938 */               stackData2[(k++)] = stackData[j];
/*     */             }
/*     */           }
/* 941 */           stackTags = stackTags2;
/* 942 */           stackData = stackData2;
/* 943 */           break;
/*     */         }
/*     */       }
/* 946 */       super.fullFrame(pos, offsetDelta, localTags, localData, stackTags, stackData);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Shifter extends StackMapTable.Walker
/*     */   {
/*     */     private StackMapTable stackMap;
/*     */     private int where;
/*     */     private int gap;
/*     */     private int position;
/*     */     private byte[] updatedInfo;
/*     */     private boolean exclusive;
/*     */ 
/*     */     public Shifter(StackMapTable smt, int where, int gap, boolean exclusive)
/*     */     {
/* 802 */       super();
/* 803 */       this.stackMap = smt;
/* 804 */       this.where = where;
/* 805 */       this.gap = gap;
/* 806 */       this.position = 0;
/* 807 */       this.updatedInfo = null;
/* 808 */       this.exclusive = exclusive;
/*     */     }
/*     */ 
/*     */     public void doit() throws BadBytecode {
/* 812 */       parse();
/* 813 */       if (this.updatedInfo != null)
/* 814 */         this.stackMap.set(this.updatedInfo);
/*     */     }
/*     */ 
/*     */     public void sameFrame(int pos, int offsetDelta) {
/* 818 */       update(pos, offsetDelta, 0, 251);
/*     */     }
/*     */ 
/*     */     public void sameLocals(int pos, int offsetDelta, int stackTag, int stackData) {
/* 822 */       update(pos, offsetDelta, 64, 247);
/*     */     }
/*     */ 
/*     */     private void update(int pos, int offsetDelta, int base, int entry) {
/* 826 */       int oldPos = this.position;
/* 827 */       this.position = (oldPos + offsetDelta + (oldPos == 0 ? 0 : 1));
/*     */       boolean match;
/*     */       boolean match;
/* 829 */       if (this.exclusive)
/* 830 */         match = (oldPos < this.where) && (this.where <= this.position);
/*     */       else {
/* 832 */         match = (oldPos <= this.where) && (this.where < this.position);
/*     */       }
/* 834 */       if (match) {
/* 835 */         int newDelta = offsetDelta + this.gap;
/* 836 */         this.position += this.gap;
/* 837 */         if (newDelta < 64) {
/* 838 */           this.info[pos] = ((byte)(newDelta + base));
/* 839 */         } else if (offsetDelta < 64) {
/* 840 */           byte[] newinfo = insertGap(this.info, pos, 2);
/* 841 */           newinfo[pos] = ((byte)entry);
/* 842 */           ByteArray.write16bit(newDelta, newinfo, pos + 1);
/* 843 */           this.updatedInfo = newinfo;
/*     */         }
/*     */         else {
/* 846 */           ByteArray.write16bit(newDelta, this.info, pos + 1);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 851 */     private static byte[] insertGap(byte[] info, int where, int gap) { int len = info.length;
/* 852 */       byte[] newinfo = new byte[len + gap];
/* 853 */       for (int i = 0; i < len; i++) {
/* 854 */         newinfo[(i + (i < where ? 0 : gap))] = info[i];
/*     */       }
/* 856 */       return newinfo; }
/*     */ 
/*     */     public void chopFrame(int pos, int offsetDelta, int k)
/*     */     {
/* 860 */       update(pos, offsetDelta);
/*     */     }
/*     */ 
/*     */     public void appendFrame(int pos, int offsetDelta, int[] tags, int[] data) {
/* 864 */       update(pos, offsetDelta);
/*     */     }
/*     */ 
/*     */     public void fullFrame(int pos, int offsetDelta, int[] localTags, int[] localData, int[] stackTags, int[] stackData)
/*     */     {
/* 869 */       update(pos, offsetDelta);
/*     */     }
/*     */ 
/*     */     private void update(int pos, int offsetDelta) {
/* 873 */       int oldPos = this.position;
/* 874 */       this.position = (oldPos + offsetDelta + (oldPos == 0 ? 0 : 1));
/*     */       boolean match;
/*     */       boolean match;
/* 876 */       if (this.exclusive)
/* 877 */         match = (oldPos < this.where) && (this.where <= this.position);
/*     */       else {
/* 879 */         match = (oldPos <= this.where) && (this.where < this.position);
/*     */       }
/* 881 */       if (match) {
/* 882 */         int newDelta = offsetDelta + this.gap;
/* 883 */         ByteArray.write16bit(newDelta, this.info, pos + 1);
/* 884 */         this.position += this.gap;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Printer extends StackMapTable.Walker
/*     */   {
/*     */     private PrintWriter writer;
/*     */     private int offset;
/*     */ 
/*     */     public static void print(StackMapTable smt, PrintWriter writer)
/*     */     {
/*     */       try
/*     */       {
/* 702 */         new Printer(smt.get(), writer).parse();
/*     */       }
/*     */       catch (BadBytecode e) {
/* 705 */         writer.println(e.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*     */     Printer(byte[] data, PrintWriter pw) {
/* 710 */       super();
/* 711 */       this.writer = pw;
/* 712 */       this.offset = -1;
/*     */     }
/*     */ 
/*     */     public void sameFrame(int pos, int offsetDelta) {
/* 716 */       this.offset += offsetDelta + 1;
/* 717 */       this.writer.println(this.offset + " same frame: " + offsetDelta);
/*     */     }
/*     */ 
/*     */     public void sameLocals(int pos, int offsetDelta, int stackTag, int stackData) {
/* 721 */       this.offset += offsetDelta + 1;
/* 722 */       this.writer.println(this.offset + " same locals: " + offsetDelta);
/* 723 */       printTypeInfo(stackTag, stackData);
/*     */     }
/*     */ 
/*     */     public void chopFrame(int pos, int offsetDelta, int k) {
/* 727 */       this.offset += offsetDelta + 1;
/* 728 */       this.writer.println(this.offset + " chop frame: " + offsetDelta + ",    " + k + " last locals");
/*     */     }
/*     */ 
/*     */     public void appendFrame(int pos, int offsetDelta, int[] tags, int[] data) {
/* 732 */       this.offset += offsetDelta + 1;
/* 733 */       this.writer.println(this.offset + " append frame: " + offsetDelta);
/* 734 */       for (int i = 0; i < tags.length; i++)
/* 735 */         printTypeInfo(tags[i], data[i]);
/*     */     }
/*     */ 
/*     */     public void fullFrame(int pos, int offsetDelta, int[] localTags, int[] localData, int[] stackTags, int[] stackData)
/*     */     {
/* 740 */       this.offset += offsetDelta + 1;
/* 741 */       this.writer.println(this.offset + " full frame: " + offsetDelta);
/* 742 */       this.writer.println("[locals]");
/* 743 */       for (int i = 0; i < localTags.length; i++) {
/* 744 */         printTypeInfo(localTags[i], localData[i]);
/*     */       }
/* 746 */       this.writer.println("[stack]");
/* 747 */       for (int i = 0; i < stackTags.length; i++)
/* 748 */         printTypeInfo(stackTags[i], stackData[i]);
/*     */     }
/*     */ 
/*     */     private void printTypeInfo(int tag, int data) {
/* 752 */       String msg = null;
/* 753 */       switch (tag) {
/*     */       case 0:
/* 755 */         msg = "top";
/* 756 */         break;
/*     */       case 1:
/* 758 */         msg = "integer";
/* 759 */         break;
/*     */       case 2:
/* 761 */         msg = "float";
/* 762 */         break;
/*     */       case 3:
/* 764 */         msg = "double";
/* 765 */         break;
/*     */       case 4:
/* 767 */         msg = "long";
/* 768 */         break;
/*     */       case 5:
/* 770 */         msg = "null";
/* 771 */         break;
/*     */       case 6:
/* 773 */         msg = "this";
/* 774 */         break;
/*     */       case 7:
/* 776 */         msg = "object (cpool_index " + data + ")";
/* 777 */         break;
/*     */       case 8:
/* 779 */         msg = "uninitialized (offset " + data + ")";
/*     */       }
/*     */ 
/* 783 */       this.writer.print("    ");
/* 784 */       this.writer.println(msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Writer
/*     */   {
/*     */     ByteArrayOutputStream output;
/*     */     int numOfEntries;
/*     */ 
/*     */     public Writer(int size)
/*     */     {
/* 535 */       this.output = new ByteArrayOutputStream(size);
/* 536 */       this.numOfEntries = 0;
/* 537 */       this.output.write(0);
/* 538 */       this.output.write(0);
/*     */     }
/*     */ 
/*     */     public byte[] toByteArray()
/*     */     {
/* 545 */       byte[] b = this.output.toByteArray();
/* 546 */       ByteArray.write16bit(this.numOfEntries, b, 0);
/* 547 */       return b;
/*     */     }
/*     */ 
/*     */     public StackMapTable toStackMapTable(ConstPool cp)
/*     */     {
/* 558 */       return new StackMapTable(cp, toByteArray());
/*     */     }
/*     */ 
/*     */     public void sameFrame(int offsetDelta)
/*     */     {
/* 565 */       this.numOfEntries += 1;
/* 566 */       if (offsetDelta < 64) {
/* 567 */         this.output.write(offsetDelta);
/*     */       } else {
/* 569 */         this.output.write(251);
/* 570 */         write16(offsetDelta);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void sameLocals(int offsetDelta, int tag, int data)
/*     */     {
/* 586 */       this.numOfEntries += 1;
/* 587 */       if (offsetDelta < 64) {
/* 588 */         this.output.write(offsetDelta + 64);
/*     */       } else {
/* 590 */         this.output.write(247);
/* 591 */         write16(offsetDelta);
/*     */       }
/*     */ 
/* 594 */       writeTypeInfo(tag, data);
/*     */     }
/*     */ 
/*     */     public void chopFrame(int offsetDelta, int k)
/*     */     {
/* 603 */       this.numOfEntries += 1;
/* 604 */       this.output.write(251 - k);
/* 605 */       write16(offsetDelta);
/*     */     }
/*     */ 
/*     */     public void appendFrame(int offsetDelta, int[] tags, int[] data)
/*     */     {
/* 622 */       this.numOfEntries += 1;
/* 623 */       int k = tags.length;
/* 624 */       this.output.write(k + 251);
/* 625 */       write16(offsetDelta);
/* 626 */       for (int i = 0; i < k; i++)
/* 627 */         writeTypeInfo(tags[i], data[i]);
/*     */     }
/*     */ 
/*     */     public void fullFrame(int offsetDelta, int[] localTags, int[] localData, int[] stackTags, int[] stackData)
/*     */     {
/* 651 */       this.numOfEntries += 1;
/* 652 */       this.output.write(255);
/* 653 */       write16(offsetDelta);
/* 654 */       int n = localTags.length;
/* 655 */       write16(n);
/* 656 */       for (int i = 0; i < n; i++) {
/* 657 */         writeTypeInfo(localTags[i], localData[i]);
/*     */       }
/* 659 */       n = stackTags.length;
/* 660 */       write16(n);
/* 661 */       for (int i = 0; i < n; i++)
/* 662 */         writeTypeInfo(stackTags[i], stackData[i]);
/*     */     }
/*     */ 
/*     */     private void writeTypeInfo(int tag, int data) {
/* 666 */       this.output.write(tag);
/* 667 */       if ((tag == 7) || (tag == 8))
/* 668 */         write16(data);
/*     */     }
/*     */ 
/*     */     private void write16(int value) {
/* 672 */       this.output.write(value >>> 8 & 0xFF);
/* 673 */       this.output.write(value & 0xFF);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class InsertLocal extends StackMapTable.SimpleCopy
/*     */   {
/*     */     private int varIndex;
/*     */     private int varTag;
/*     */     private int varData;
/*     */ 
/*     */     public InsertLocal(byte[] data, int varIndex, int varTag, int varData)
/*     */     {
/* 485 */       super();
/* 486 */       this.varIndex = varIndex;
/* 487 */       this.varTag = varTag;
/* 488 */       this.varData = varData;
/*     */     }
/*     */ 
/*     */     public void fullFrame(int pos, int offsetDelta, int[] localTags, int[] localData, int[] stackTags, int[] stackData)
/*     */     {
/* 493 */       int len = localTags.length;
/* 494 */       if (len < this.varIndex) {
/* 495 */         super.fullFrame(pos, offsetDelta, localTags, localData, stackTags, stackData);
/* 496 */         return;
/*     */       }
/*     */ 
/* 499 */       int typeSize = (this.varTag == 4) || (this.varTag == 3) ? 2 : 1;
/* 500 */       int[] localTags2 = new int[len + typeSize];
/* 501 */       int[] localData2 = new int[len + typeSize];
/* 502 */       int index = this.varIndex;
/* 503 */       int j = 0;
/* 504 */       for (int i = 0; i < len; i++) {
/* 505 */         if (j == index) {
/* 506 */           j += typeSize;
/*     */         }
/* 508 */         localTags2[j] = localTags[i];
/* 509 */         localData2[(j++)] = localData[i];
/*     */       }
/*     */ 
/* 512 */       localTags2[index] = this.varTag;
/* 513 */       localData2[index] = this.varData;
/* 514 */       if (typeSize > 1) {
/* 515 */         localTags2[(index + 1)] = 0;
/* 516 */         localData2[(index + 1)] = 0;
/*     */       }
/*     */ 
/* 519 */       super.fullFrame(pos, offsetDelta, localTags2, localData2, stackTags, stackData);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Copier extends StackMapTable.SimpleCopy
/*     */   {
/*     */     private ConstPool srcPool;
/*     */     private ConstPool destPool;
/*     */ 
/*     */     public Copier(ConstPool src, byte[] data, ConstPool dest)
/*     */     {
/* 404 */       super();
/* 405 */       this.srcPool = src;
/* 406 */       this.destPool = dest;
/*     */     }
/*     */ 
/*     */     protected int copyData(int tag, int data) {
/* 410 */       if (tag == 7) {
/* 411 */         return this.srcPool.copy(data, this.destPool, null);
/*     */       }
/* 413 */       return data;
/*     */     }
/*     */ 
/*     */     protected int[] copyData(int[] tags, int[] data) {
/* 417 */       int[] newData = new int[data.length];
/* 418 */       for (int i = 0; i < data.length; i++) {
/* 419 */         if (tags[i] == 7)
/* 420 */           newData[i] = this.srcPool.copy(data[i], this.destPool, null);
/*     */         else
/* 422 */           newData[i] = data[i];
/*     */       }
/* 424 */       return newData;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class SimpleCopy extends StackMapTable.Walker
/*     */   {
/*     */     private StackMapTable.Writer writer;
/*     */ 
/*     */     public SimpleCopy(byte[] data)
/*     */     {
/* 360 */       super();
/* 361 */       this.writer = new StackMapTable.Writer(data.length);
/*     */     }
/*     */ 
/*     */     public byte[] doit() throws BadBytecode {
/* 365 */       parse();
/* 366 */       return this.writer.toByteArray();
/*     */     }
/*     */ 
/*     */     public void sameFrame(int pos, int offsetDelta) {
/* 370 */       this.writer.sameFrame(offsetDelta);
/*     */     }
/*     */ 
/*     */     public void sameLocals(int pos, int offsetDelta, int stackTag, int stackData) {
/* 374 */       this.writer.sameLocals(offsetDelta, stackTag, copyData(stackTag, stackData));
/*     */     }
/*     */ 
/*     */     public void chopFrame(int pos, int offsetDelta, int k) {
/* 378 */       this.writer.chopFrame(offsetDelta, k);
/*     */     }
/*     */ 
/*     */     public void appendFrame(int pos, int offsetDelta, int[] tags, int[] data) {
/* 382 */       this.writer.appendFrame(offsetDelta, tags, copyData(tags, data));
/*     */     }
/*     */ 
/*     */     public void fullFrame(int pos, int offsetDelta, int[] localTags, int[] localData, int[] stackTags, int[] stackData)
/*     */     {
/* 387 */       this.writer.fullFrame(offsetDelta, localTags, copyData(localTags, localData), stackTags, copyData(stackTags, stackData));
/*     */     }
/*     */ 
/*     */     protected int copyData(int tag, int data)
/*     */     {
/* 392 */       return data;
/*     */     }
/*     */ 
/*     */     protected int[] copyData(int[] tags, int[] data) {
/* 396 */       return data;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Walker
/*     */   {
/*     */     byte[] info;
/*     */     int numOfEntries;
/*     */ 
/*     */     public Walker(StackMapTable smt)
/*     */     {
/* 152 */       this(smt.get());
/*     */     }
/*     */ 
/*     */     public Walker(byte[] data)
/*     */     {
/* 164 */       this.info = data;
/* 165 */       this.numOfEntries = ByteArray.readU16bit(data, 0);
/*     */     }
/*     */ 
/*     */     public final int size()
/*     */     {
/* 171 */       return this.numOfEntries;
/*     */     }
/*     */ 
/*     */     public void parse()
/*     */       throws BadBytecode
/*     */     {
/* 177 */       int n = this.numOfEntries;
/* 178 */       int pos = 2;
/* 179 */       for (int i = 0; i < n; i++)
/* 180 */         pos = stackMapFrames(pos, i);
/*     */     }
/*     */ 
/*     */     int stackMapFrames(int pos, int nth)
/*     */       throws BadBytecode
/*     */     {
/* 193 */       int type = this.info[pos] & 0xFF;
/* 194 */       if (type < 64) {
/* 195 */         sameFrame(pos, type);
/* 196 */         pos++;
/*     */       }
/* 198 */       else if (type < 128) {
/* 199 */         pos = sameLocals(pos, type); } else {
/* 200 */         if (type < 247)
/* 201 */           throw new BadBytecode("bad frame_type in StackMapTable");
/* 202 */         if (type == 247) {
/* 203 */           pos = sameLocals(pos, type);
/* 204 */         } else if (type < 251) {
/* 205 */           int offset = ByteArray.readU16bit(this.info, pos + 1);
/* 206 */           chopFrame(pos, offset, 251 - type);
/* 207 */           pos += 3;
/*     */         }
/* 209 */         else if (type == 251) {
/* 210 */           int offset = ByteArray.readU16bit(this.info, pos + 1);
/* 211 */           sameFrame(pos, offset);
/* 212 */           pos += 3;
/*     */         }
/* 214 */         else if (type < 255) {
/* 215 */           pos = appendFrame(pos, type);
/*     */         } else {
/* 217 */           pos = fullFrame(pos);
/*     */         }
/*     */       }
/* 219 */       return pos;
/*     */     }
/*     */ 
/*     */     public void sameFrame(int pos, int offsetDelta)
/*     */       throws BadBytecode
/*     */     {
/*     */     }
/*     */ 
/*     */     private int sameLocals(int pos, int type)
/*     */       throws BadBytecode
/*     */     {
/* 233 */       int top = pos;
/*     */       int offset;
/*     */       int offset;
/* 235 */       if (type < 128) {
/* 236 */         offset = type - 64;
/*     */       } else {
/* 238 */         offset = ByteArray.readU16bit(this.info, pos + 1);
/* 239 */         pos += 2;
/*     */       }
/*     */ 
/* 242 */       int tag = this.info[(pos + 1)] & 0xFF;
/* 243 */       int data = 0;
/* 244 */       if ((tag == 7) || (tag == 8)) {
/* 245 */         data = ByteArray.readU16bit(this.info, pos + 2);
/* 246 */         pos += 2;
/*     */       }
/*     */ 
/* 249 */       sameLocals(top, offset, tag, data);
/* 250 */       return pos + 2;
/*     */     }
/*     */ 
/*     */     public void sameLocals(int pos, int offsetDelta, int stackTag, int stackData)
/*     */       throws BadBytecode
/*     */     {
/*     */     }
/*     */ 
/*     */     public void chopFrame(int pos, int offsetDelta, int k)
/*     */       throws BadBytecode
/*     */     {
/*     */     }
/*     */ 
/*     */     private int appendFrame(int pos, int type)
/*     */       throws BadBytecode
/*     */     {
/* 278 */       int k = type - 251;
/* 279 */       int offset = ByteArray.readU16bit(this.info, pos + 1);
/* 280 */       int[] tags = new int[k];
/* 281 */       int[] data = new int[k];
/* 282 */       int p = pos + 3;
/* 283 */       for (int i = 0; i < k; i++) {
/* 284 */         int tag = this.info[p] & 0xFF;
/* 285 */         tags[i] = tag;
/* 286 */         if ((tag == 7) || (tag == 8)) {
/* 287 */           data[i] = ByteArray.readU16bit(this.info, p + 1);
/* 288 */           p += 3;
/*     */         }
/*     */         else {
/* 291 */           data[i] = 0;
/* 292 */           p++;
/*     */         }
/*     */       }
/*     */ 
/* 296 */       appendFrame(pos, offset, tags, data);
/* 297 */       return p;
/*     */     }
/*     */ 
/*     */     public void appendFrame(int pos, int offsetDelta, int[] tags, int[] data)
/*     */       throws BadBytecode
/*     */     {
/*     */     }
/*     */ 
/*     */     private int fullFrame(int pos)
/*     */       throws BadBytecode
/*     */     {
/* 313 */       int offset = ByteArray.readU16bit(this.info, pos + 1);
/* 314 */       int numOfLocals = ByteArray.readU16bit(this.info, pos + 3);
/* 315 */       int[] localsTags = new int[numOfLocals];
/* 316 */       int[] localsData = new int[numOfLocals];
/* 317 */       int p = verifyTypeInfo(pos + 5, numOfLocals, localsTags, localsData);
/* 318 */       int numOfItems = ByteArray.readU16bit(this.info, p);
/* 319 */       int[] itemsTags = new int[numOfItems];
/* 320 */       int[] itemsData = new int[numOfItems];
/* 321 */       p = verifyTypeInfo(p + 2, numOfItems, itemsTags, itemsData);
/* 322 */       fullFrame(pos, offset, localsTags, localsData, itemsTags, itemsData);
/* 323 */       return p;
/*     */     }
/*     */ 
/*     */     public void fullFrame(int pos, int offsetDelta, int[] localTags, int[] localData, int[] stackTags, int[] stackData)
/*     */       throws BadBytecode
/*     */     {
/*     */     }
/*     */ 
/*     */     private int verifyTypeInfo(int pos, int n, int[] tags, int[] data)
/*     */     {
/* 343 */       for (int i = 0; i < n; i++) {
/* 344 */         int tag = this.info[(pos++)] & 0xFF;
/* 345 */         tags[i] = tag;
/* 346 */         if ((tag == 7) || (tag == 8)) {
/* 347 */           data[i] = ByteArray.readU16bit(this.info, pos);
/* 348 */           pos += 2;
/*     */         }
/*     */       }
/*     */ 
/* 352 */       return pos;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class RuntimeCopyException extends RuntimeException
/*     */   {
/*     */     public RuntimeCopyException(String s)
/*     */     {
/*  85 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.StackMapTable
 * JD-Core Version:    0.6.2
 */