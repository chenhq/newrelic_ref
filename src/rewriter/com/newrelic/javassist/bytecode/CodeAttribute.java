/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class CodeAttribute extends AttributeInfo
/*     */   implements Opcode
/*     */ {
/*     */   public static final String tag = "Code";
/*     */   private int maxStack;
/*     */   private int maxLocals;
/*     */   private ExceptionTable exceptions;
/*     */   private ArrayList attributes;
/*     */ 
/*     */   public CodeAttribute(ConstPool cp, int stack, int locals, byte[] code, ExceptionTable etable)
/*     */   {
/*  60 */     super(cp, "Code");
/*  61 */     this.maxStack = stack;
/*  62 */     this.maxLocals = locals;
/*  63 */     this.info = code;
/*  64 */     this.exceptions = etable;
/*  65 */     this.attributes = new ArrayList();
/*     */   }
/*     */ 
/*     */   private CodeAttribute(ConstPool cp, CodeAttribute src, Map classnames)
/*     */     throws BadBytecode
/*     */   {
/*  80 */     super(cp, "Code");
/*     */ 
/*  82 */     this.maxStack = src.getMaxStack();
/*  83 */     this.maxLocals = src.getMaxLocals();
/*  84 */     this.exceptions = src.getExceptionTable().copy(cp, classnames);
/*  85 */     this.attributes = new ArrayList();
/*  86 */     List src_attr = src.getAttributes();
/*  87 */     int num = src_attr.size();
/*  88 */     for (int i = 0; i < num; i++) {
/*  89 */       AttributeInfo ai = (AttributeInfo)src_attr.get(i);
/*  90 */       this.attributes.add(ai.copy(cp, classnames));
/*     */     }
/*     */ 
/*  93 */     this.info = src.copyCode(cp, classnames, this.exceptions, this);
/*     */   }
/*     */ 
/*     */   CodeAttribute(ConstPool cp, int name_id, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  99 */     super(cp, name_id, (byte[])null);
/* 100 */     int attr_len = in.readInt();
/*     */ 
/* 102 */     this.maxStack = in.readUnsignedShort();
/* 103 */     this.maxLocals = in.readUnsignedShort();
/*     */ 
/* 105 */     int code_len = in.readInt();
/* 106 */     this.info = new byte[code_len];
/* 107 */     in.readFully(this.info);
/*     */ 
/* 109 */     this.exceptions = new ExceptionTable(cp, in);
/*     */ 
/* 111 */     this.attributes = new ArrayList();
/* 112 */     int num = in.readUnsignedShort();
/* 113 */     for (int i = 0; i < num; i++)
/* 114 */       this.attributes.add(AttributeInfo.read(cp, in));
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */     throws CodeAttribute.RuntimeCopyException
/*     */   {
/*     */     try
/*     */     {
/* 135 */       return new CodeAttribute(newCp, this, classnames);
/*     */     } catch (BadBytecode e) {
/*     */     }
/* 138 */     throw new RuntimeCopyException("bad bytecode. fatal?");
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 161 */     return 18 + this.info.length + this.exceptions.size() * 8 + AttributeInfo.getLength(this.attributes);
/*     */   }
/*     */ 
/*     */   void write(DataOutputStream out) throws IOException
/*     */   {
/* 166 */     out.writeShort(this.name);
/* 167 */     out.writeInt(length() - 6);
/* 168 */     out.writeShort(this.maxStack);
/* 169 */     out.writeShort(this.maxLocals);
/* 170 */     out.writeInt(this.info.length);
/* 171 */     out.write(this.info);
/* 172 */     this.exceptions.write(out);
/* 173 */     out.writeShort(this.attributes.size());
/* 174 */     AttributeInfo.writeAll(this.attributes, out);
/*     */   }
/*     */ 
/*     */   public byte[] get()
/*     */   {
/* 183 */     throw new UnsupportedOperationException("CodeAttribute.get()");
/*     */   }
/*     */ 
/*     */   public void set(byte[] newinfo)
/*     */   {
/* 192 */     throw new UnsupportedOperationException("CodeAttribute.set()");
/*     */   }
/*     */ 
/*     */   void renameClass(String oldname, String newname) {
/* 196 */     AttributeInfo.renameClass(this.attributes, oldname, newname);
/*     */   }
/*     */ 
/*     */   void renameClass(Map classnames) {
/* 200 */     AttributeInfo.renameClass(this.attributes, classnames);
/*     */   }
/*     */ 
/*     */   public String getDeclaringClass()
/*     */   {
/* 208 */     ConstPool cp = getConstPool();
/* 209 */     return cp.getClassName();
/*     */   }
/*     */ 
/*     */   public int getMaxStack()
/*     */   {
/* 216 */     return this.maxStack;
/*     */   }
/*     */ 
/*     */   public void setMaxStack(int value)
/*     */   {
/* 223 */     this.maxStack = value;
/*     */   }
/*     */ 
/*     */   public int computeMaxStack()
/*     */     throws BadBytecode
/*     */   {
/* 234 */     this.maxStack = new CodeAnalyzer(this).computeMaxStack();
/* 235 */     return this.maxStack;
/*     */   }
/*     */ 
/*     */   public int getMaxLocals()
/*     */   {
/* 242 */     return this.maxLocals;
/*     */   }
/*     */ 
/*     */   public void setMaxLocals(int value)
/*     */   {
/* 249 */     this.maxLocals = value;
/*     */   }
/*     */ 
/*     */   public int getCodeLength()
/*     */   {
/* 256 */     return this.info.length;
/*     */   }
/*     */ 
/*     */   public byte[] getCode()
/*     */   {
/* 263 */     return this.info;
/*     */   }
/*     */ 
/*     */   void setCode(byte[] newinfo)
/*     */   {
/* 269 */     super.set(newinfo);
/*     */   }
/*     */ 
/*     */   public CodeIterator iterator()
/*     */   {
/* 275 */     return new CodeIterator(this);
/*     */   }
/*     */ 
/*     */   public ExceptionTable getExceptionTable()
/*     */   {
/* 281 */     return this.exceptions;
/*     */   }
/*     */ 
/*     */   public List getAttributes()
/*     */   {
/* 291 */     return this.attributes;
/*     */   }
/*     */ 
/*     */   public AttributeInfo getAttribute(String name)
/*     */   {
/* 301 */     return AttributeInfo.lookup(this.attributes, name);
/*     */   }
/*     */ 
/*     */   public void setAttribute(StackMapTable smt)
/*     */   {
/* 313 */     AttributeInfo.remove(this.attributes, "StackMapTable");
/* 314 */     if (smt != null)
/* 315 */       this.attributes.add(smt);
/*     */   }
/*     */ 
/*     */   public void setAttribute(StackMap sm)
/*     */   {
/* 328 */     AttributeInfo.remove(this.attributes, "StackMap");
/* 329 */     if (sm != null)
/* 330 */       this.attributes.add(sm);
/*     */   }
/*     */ 
/*     */   private byte[] copyCode(ConstPool destCp, Map classnames, ExceptionTable etable, CodeAttribute destCa)
/*     */     throws BadBytecode
/*     */   {
/* 340 */     int len = getCodeLength();
/* 341 */     byte[] newCode = new byte[len];
/* 342 */     destCa.info = newCode;
/* 343 */     LdcEntry ldc = copyCode(this.info, 0, len, getConstPool(), newCode, destCp, classnames);
/*     */ 
/* 345 */     return LdcEntry.doit(newCode, ldc, etable, destCa);
/*     */   }
/*     */ 
/*     */   private static LdcEntry copyCode(byte[] code, int beginPos, int endPos, ConstPool srcCp, byte[] newcode, ConstPool destCp, Map classnameMap)
/*     */     throws BadBytecode
/*     */   {
/* 354 */     LdcEntry ldcEntry = null;
/*     */     int i2;
/* 356 */     for (int i = beginPos; i < endPos; i = i2) {
/* 357 */       i2 = CodeIterator.nextOpcode(code, i);
/* 358 */       byte c = code[i];
/* 359 */       newcode[i] = c;
/* 360 */       switch (c & 0xFF) {
/*     */       case 19:
/*     */       case 20:
/*     */       case 178:
/*     */       case 179:
/*     */       case 180:
/*     */       case 181:
/*     */       case 182:
/*     */       case 183:
/*     */       case 184:
/*     */       case 187:
/*     */       case 189:
/*     */       case 192:
/*     */       case 193:
/* 374 */         copyConstPoolInfo(i + 1, code, srcCp, newcode, destCp, classnameMap);
/*     */ 
/* 376 */         break;
/*     */       case 18:
/* 378 */         int index = code[(i + 1)] & 0xFF;
/* 379 */         index = srcCp.copy(index, destCp, classnameMap);
/* 380 */         if (index < 256) {
/* 381 */           newcode[(i + 1)] = ((byte)index);
/*     */         } else {
/* 383 */           newcode[i] = 0;
/* 384 */           newcode[(i + 1)] = 0;
/* 385 */           LdcEntry ldc = new LdcEntry();
/* 386 */           ldc.where = i;
/* 387 */           ldc.index = index;
/* 388 */           ldc.next = ldcEntry;
/* 389 */           ldcEntry = ldc;
/*     */         }
/* 391 */         break;
/*     */       case 185:
/* 393 */         copyConstPoolInfo(i + 1, code, srcCp, newcode, destCp, classnameMap);
/*     */ 
/* 395 */         newcode[(i + 3)] = code[(i + 3)];
/* 396 */         newcode[(i + 4)] = code[(i + 4)];
/* 397 */         break;
/*     */       case 197:
/* 399 */         copyConstPoolInfo(i + 1, code, srcCp, newcode, destCp, classnameMap);
/*     */ 
/* 401 */         newcode[(i + 3)] = code[(i + 3)];
/* 402 */         break;
/*     */       default:
/*     */         while (true) { i++; if (i >= i2) break;
/* 405 */           newcode[i] = code[i];
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 411 */     return ldcEntry;
/*     */   }
/*     */ 
/*     */   private static void copyConstPoolInfo(int i, byte[] code, ConstPool srcCp, byte[] newcode, ConstPool destCp, Map classnameMap)
/*     */   {
/* 417 */     int index = (code[i] & 0xFF) << 8 | code[(i + 1)] & 0xFF;
/* 418 */     index = srcCp.copy(index, destCp, classnameMap);
/* 419 */     newcode[i] = ((byte)(index >> 8));
/* 420 */     newcode[(i + 1)] = ((byte)index);
/*     */   }
/*     */ 
/*     */   public void insertLocalVar(int where, int size)
/*     */     throws BadBytecode
/*     */   {
/* 467 */     CodeIterator ci = iterator();
/* 468 */     while (ci.hasNext()) {
/* 469 */       shiftIndex(ci, where, size);
/*     */     }
/* 471 */     setMaxLocals(getMaxLocals() + size);
/*     */   }
/*     */ 
/*     */   private static void shiftIndex(CodeIterator ci, int lessThan, int delta)
/*     */     throws BadBytecode
/*     */   {
/* 482 */     int index = ci.next();
/* 483 */     int opcode = ci.byteAt(index);
/* 484 */     if (opcode < 21)
/* 485 */       return;
/* 486 */     if (opcode < 79) {
/* 487 */       if (opcode < 26)
/*     */       {
/* 489 */         shiftIndex8(ci, index, opcode, lessThan, delta);
/*     */       }
/* 491 */       else if (opcode < 46)
/*     */       {
/* 493 */         shiftIndex0(ci, index, opcode, lessThan, delta, 26, 21);
/*     */       } else {
/* 495 */         if (opcode < 54)
/* 496 */           return;
/* 497 */         if (opcode < 59)
/*     */         {
/* 499 */           shiftIndex8(ci, index, opcode, lessThan, delta);
/*     */         }
/*     */         else
/*     */         {
/* 503 */           shiftIndex0(ci, index, opcode, lessThan, delta, 59, 54);
/*     */         }
/*     */       }
/* 506 */     } else if (opcode == 132) {
/* 507 */       int var = ci.byteAt(index + 1);
/* 508 */       if (var < lessThan) {
/* 509 */         return;
/*     */       }
/* 511 */       var += delta;
/* 512 */       if (var < 256) {
/* 513 */         ci.writeByte(var, index + 1);
/*     */       } else {
/* 515 */         int plus = (byte)ci.byteAt(index + 2);
/* 516 */         int pos = ci.insertExGap(3);
/* 517 */         ci.writeByte(196, pos - 3);
/* 518 */         ci.writeByte(132, pos - 2);
/* 519 */         ci.write16bit(var, pos - 1);
/* 520 */         ci.write16bit(plus, pos + 1);
/*     */       }
/*     */     }
/* 523 */     else if (opcode == 169) {
/* 524 */       shiftIndex8(ci, index, opcode, lessThan, delta);
/* 525 */     } else if (opcode == 196) {
/* 526 */       int var = ci.u16bitAt(index + 2);
/* 527 */       if (var < lessThan) {
/* 528 */         return;
/*     */       }
/* 530 */       var += delta;
/* 531 */       ci.write16bit(var, index + 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void shiftIndex8(CodeIterator ci, int index, int opcode, int lessThan, int delta)
/*     */     throws BadBytecode
/*     */   {
/* 539 */     int var = ci.byteAt(index + 1);
/* 540 */     if (var < lessThan) {
/* 541 */       return;
/*     */     }
/* 543 */     var += delta;
/* 544 */     if (var < 256) {
/* 545 */       ci.writeByte(var, index + 1);
/*     */     } else {
/* 547 */       int pos = ci.insertExGap(2);
/* 548 */       ci.writeByte(196, pos - 2);
/* 549 */       ci.writeByte(opcode, pos - 1);
/* 550 */       ci.write16bit(var, pos);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void shiftIndex0(CodeIterator ci, int index, int opcode, int lessThan, int delta, int opcode_i_0, int opcode_i)
/*     */     throws BadBytecode
/*     */   {
/* 559 */     int var = (opcode - opcode_i_0) % 4;
/* 560 */     if (var < lessThan) {
/* 561 */       return;
/*     */     }
/* 563 */     var += delta;
/* 564 */     if (var < 4) {
/* 565 */       ci.writeByte(opcode + delta, index);
/*     */     } else {
/* 567 */       opcode = (opcode - opcode_i_0) / 4 + opcode_i;
/* 568 */       if (var < 256) {
/* 569 */         int pos = ci.insertExGap(1);
/* 570 */         ci.writeByte(opcode, pos - 1);
/* 571 */         ci.writeByte(var, pos);
/*     */       }
/*     */       else {
/* 574 */         int pos = ci.insertExGap(3);
/* 575 */         ci.writeByte(196, pos - 1);
/* 576 */         ci.writeByte(opcode, pos);
/* 577 */         ci.write16bit(var, pos + 1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class LdcEntry
/*     */   {
/*     */     LdcEntry next;
/*     */     int where;
/*     */     int index;
/*     */ 
/*     */     static byte[] doit(byte[] code, LdcEntry ldc, ExceptionTable etable, CodeAttribute ca)
/*     */       throws BadBytecode
/*     */     {
/* 432 */       if (ldc != null) {
/* 433 */         code = CodeIterator.changeLdcToLdcW(code, etable, ca, ldc);
/*     */       }
/*     */ 
/* 448 */       return code;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class RuntimeCopyException extends RuntimeException
/*     */   {
/*     */     public RuntimeCopyException(String s)
/*     */     {
/* 151 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.CodeAttribute
 * JD-Core Version:    0.6.2
 */