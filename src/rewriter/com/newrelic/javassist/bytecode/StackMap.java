/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import com.newrelic.javassist.CannotCompileException;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class StackMap extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "StackMap";
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
/*     */   StackMap(ConstPool cp, byte[] newInfo)
/*     */   {
/*  54 */     super(cp, "StackMap", newInfo);
/*     */   }
/*     */ 
/*     */   StackMap(ConstPool cp, int name_id, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  60 */     super(cp, name_id, in);
/*     */   }
/*     */ 
/*     */   public int numOfEntries()
/*     */   {
/*  67 */     return ByteArray.readU16bit(this.info, 0);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 119 */     Copier copier = new Copier(this, newCp, classnames);
/* 120 */     copier.visit();
/* 121 */     return copier.getStackMap();
/*     */   }
/*     */ 
/*     */   public void insertLocal(int index, int tag, int classInfo)
/*     */     throws BadBytecode
/*     */   {
/* 290 */     byte[] data = new InsertLocal(this, index, tag, classInfo).doit();
/* 291 */     set(data);
/*     */   }
/*     */ 
/*     */   void shiftPc(int where, int gapSize, boolean exclusive)
/*     */     throws BadBytecode
/*     */   {
/* 378 */     new Shifter(this, where, gapSize, exclusive).visit();
/*     */   }
/*     */ 
/*     */   public void removeNew(int where)
/*     */     throws CannotCompileException
/*     */   {
/* 410 */     byte[] data = new NewRemover(this, where).doit();
/* 411 */     set(data);
/*     */   }
/*     */ 
/*     */   public void print(PrintWriter out)
/*     */   {
/* 473 */     new Printer(this, out).print();
/*     */   }
/*     */ 
/*     */   public static class Writer
/*     */   {
/*     */     private ByteArrayOutputStream output;
/*     */ 
/*     */     public Writer()
/*     */     {
/* 508 */       this.output = new ByteArrayOutputStream();
/*     */     }
/*     */ 
/*     */     public byte[] toByteArray()
/*     */     {
/* 515 */       return this.output.toByteArray();
/*     */     }
/*     */ 
/*     */     public StackMap toStackMap(ConstPool cp)
/*     */     {
/* 522 */       return new StackMap(cp, this.output.toByteArray());
/*     */     }
/*     */ 
/*     */     public void writeVerifyTypeInfo(int tag, int data)
/*     */     {
/* 531 */       this.output.write(tag);
/* 532 */       if ((tag == 7) || (tag == 8))
/* 533 */         write16bit(data);
/*     */     }
/*     */ 
/*     */     public void write16bit(int value)
/*     */     {
/* 540 */       this.output.write(value >>> 8 & 0xFF);
/* 541 */       this.output.write(value & 0xFF);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Printer extends StackMap.Walker
/*     */   {
/*     */     private PrintWriter writer;
/*     */ 
/*     */     public Printer(StackMap map, PrintWriter out)
/*     */     {
/* 480 */       super();
/* 481 */       this.writer = out;
/*     */     }
/*     */ 
/*     */     public void print() {
/* 485 */       int num = ByteArray.readU16bit(this.info, 0);
/* 486 */       this.writer.println(num + " entries");
/* 487 */       visit();
/*     */     }
/*     */ 
/*     */     public int locals(int pos, int offset, int num) {
/* 491 */       this.writer.println("  * offset " + offset);
/* 492 */       return super.locals(pos, offset, num);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class NewRemover extends StackMap.SimpleCopy
/*     */   {
/*     */     int posOfNew;
/*     */ 
/*     */     NewRemover(StackMap map, int where)
/*     */     {
/* 418 */       super();
/* 419 */       this.posOfNew = where;
/*     */     }
/*     */ 
/*     */     public int stack(int pos, int offset, int num) {
/* 423 */       return stackTypeInfoArray(pos, offset, num);
/*     */     }
/*     */ 
/*     */     private int stackTypeInfoArray(int pos, int offset, int num) {
/* 427 */       int p = pos;
/* 428 */       int count = 0;
/* 429 */       for (int k = 0; k < num; k++) {
/* 430 */         byte tag = this.info[p];
/* 431 */         if (tag == 7) {
/* 432 */           p += 3;
/* 433 */         } else if (tag == 8) {
/* 434 */           int offsetOfNew = ByteArray.readU16bit(this.info, p + 1);
/* 435 */           if (offsetOfNew == this.posOfNew) {
/* 436 */             count++;
/*     */           }
/* 438 */           p += 3;
/*     */         }
/*     */         else {
/* 441 */           p++;
/*     */         }
/*     */       }
/* 444 */       this.writer.write16bit(num - count);
/* 445 */       for (int k = 0; k < num; k++) {
/* 446 */         byte tag = this.info[pos];
/* 447 */         if (tag == 7) {
/* 448 */           int clazz = ByteArray.readU16bit(this.info, pos + 1);
/* 449 */           objectVariable(pos, clazz);
/* 450 */           pos += 3;
/*     */         }
/* 452 */         else if (tag == 8) {
/* 453 */           int offsetOfNew = ByteArray.readU16bit(this.info, pos + 1);
/* 454 */           if (offsetOfNew != this.posOfNew) {
/* 455 */             uninitialized(pos, offsetOfNew);
/*     */           }
/* 457 */           pos += 3;
/*     */         }
/*     */         else {
/* 460 */           typeInfo(pos, tag);
/* 461 */           pos++;
/*     */         }
/*     */       }
/*     */ 
/* 465 */       return pos;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Shifter extends StackMap.Walker
/*     */   {
/*     */     private int where;
/*     */     private int gap;
/*     */     private boolean exclusive;
/*     */ 
/*     */     public Shifter(StackMap smt, int where, int gap, boolean exclusive)
/*     */     {
/* 386 */       super();
/* 387 */       this.where = where;
/* 388 */       this.gap = gap;
/* 389 */       this.exclusive = exclusive;
/*     */     }
/*     */ 
/*     */     public int locals(int pos, int offset, int num) {
/* 393 */       if (this.exclusive ? this.where <= offset : this.where < offset) {
/* 394 */         ByteArray.write16bit(offset + this.gap, this.info, pos - 4);
/*     */       }
/* 396 */       return super.locals(pos, offset, num);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class InsertLocal extends StackMap.SimpleCopy
/*     */   {
/*     */     private int varIndex;
/*     */     private int varTag;
/*     */     private int varData;
/*     */ 
/*     */     InsertLocal(StackMap map, int varIndex, int varTag, int varData)
/*     */     {
/* 341 */       super();
/* 342 */       this.varIndex = varIndex;
/* 343 */       this.varTag = varTag;
/* 344 */       this.varData = varData;
/*     */     }
/*     */ 
/*     */     public int typeInfoArray(int pos, int offset, int num, boolean isLocals) {
/* 348 */       if ((!isLocals) || (num < this.varIndex)) {
/* 349 */         return super.typeInfoArray(pos, offset, num, isLocals);
/*     */       }
/* 351 */       this.writer.write16bit(num + 1);
/* 352 */       for (int k = 0; k < num; k++) {
/* 353 */         if (k == this.varIndex) {
/* 354 */           writeVarTypeInfo();
/*     */         }
/* 356 */         pos = typeInfoArray2(k, pos);
/*     */       }
/*     */ 
/* 359 */       if (num == this.varIndex) {
/* 360 */         writeVarTypeInfo();
/*     */       }
/* 362 */       return pos;
/*     */     }
/*     */ 
/*     */     private void writeVarTypeInfo() {
/* 366 */       if (this.varTag == 7)
/* 367 */         this.writer.writeVerifyTypeInfo(7, this.varData);
/* 368 */       else if (this.varTag == 8)
/* 369 */         this.writer.writeVerifyTypeInfo(8, this.varData);
/*     */       else
/* 371 */         this.writer.writeVerifyTypeInfo(this.varTag, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class SimpleCopy extends StackMap.Walker
/*     */   {
/*     */     StackMap.Writer writer;
/*     */ 
/*     */     SimpleCopy(StackMap map)
/*     */     {
/* 298 */       super();
/* 299 */       this.writer = new StackMap.Writer();
/*     */     }
/*     */ 
/*     */     byte[] doit() {
/* 303 */       visit();
/* 304 */       return this.writer.toByteArray();
/*     */     }
/*     */ 
/*     */     public void visit() {
/* 308 */       int num = ByteArray.readU16bit(this.info, 0);
/* 309 */       this.writer.write16bit(num);
/* 310 */       super.visit();
/*     */     }
/*     */ 
/*     */     public int locals(int pos, int offset, int num) {
/* 314 */       this.writer.write16bit(offset);
/* 315 */       return super.locals(pos, offset, num);
/*     */     }
/*     */ 
/*     */     public int typeInfoArray(int pos, int offset, int num, boolean isLocals) {
/* 319 */       this.writer.write16bit(num);
/* 320 */       return super.typeInfoArray(pos, offset, num, isLocals);
/*     */     }
/*     */ 
/*     */     public void typeInfo(int pos, byte tag) {
/* 324 */       this.writer.writeVerifyTypeInfo(tag, 0);
/*     */     }
/*     */ 
/*     */     public void objectVariable(int pos, int clazz) {
/* 328 */       this.writer.writeVerifyTypeInfo(7, clazz);
/*     */     }
/*     */ 
/*     */     public void uninitialized(int pos, int offset) {
/* 332 */       this.writer.writeVerifyTypeInfo(8, offset);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Copier extends StackMap.Walker
/*     */   {
/*     */     byte[] dest;
/*     */     ConstPool srcCp;
/*     */     ConstPool destCp;
/*     */     Map classnames;
/*     */ 
/*     */     Copier(StackMap map, ConstPool newCp, Map classnames)
/*     */     {
/* 229 */       super();
/* 230 */       this.srcCp = map.getConstPool();
/* 231 */       this.dest = new byte[this.info.length];
/* 232 */       this.destCp = newCp;
/* 233 */       this.classnames = classnames;
/*     */     }
/*     */ 
/*     */     public void visit() {
/* 237 */       int num = ByteArray.readU16bit(this.info, 0);
/* 238 */       ByteArray.write16bit(num, this.dest, 0);
/* 239 */       super.visit();
/*     */     }
/*     */ 
/*     */     public int locals(int pos, int offset, int num) {
/* 243 */       ByteArray.write16bit(offset, this.dest, pos - 4);
/* 244 */       return super.locals(pos, offset, num);
/*     */     }
/*     */ 
/*     */     public int typeInfoArray(int pos, int offset, int num, boolean isLocals) {
/* 248 */       ByteArray.write16bit(num, this.dest, pos - 2);
/* 249 */       return super.typeInfoArray(pos, offset, num, isLocals);
/*     */     }
/*     */ 
/*     */     public void typeInfo(int pos, byte tag) {
/* 253 */       this.dest[pos] = tag;
/*     */     }
/*     */ 
/*     */     public void objectVariable(int pos, int clazz) {
/* 257 */       this.dest[pos] = 7;
/* 258 */       int newClazz = this.srcCp.copy(clazz, this.destCp, this.classnames);
/* 259 */       ByteArray.write16bit(newClazz, this.dest, pos + 1);
/*     */     }
/*     */ 
/*     */     public void uninitialized(int pos, int offset) {
/* 263 */       this.dest[pos] = 8;
/* 264 */       ByteArray.write16bit(offset, this.dest, pos + 1);
/*     */     }
/*     */ 
/*     */     public StackMap getStackMap() {
/* 268 */       return new StackMap(this.destCp, this.dest);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Walker
/*     */   {
/*     */     byte[] info;
/*     */ 
/*     */     public Walker(StackMap sm)
/*     */     {
/* 134 */       this.info = sm.get();
/*     */     }
/*     */ 
/*     */     public void visit()
/*     */     {
/* 141 */       int num = ByteArray.readU16bit(this.info, 0);
/* 142 */       int pos = 2;
/* 143 */       for (int i = 0; i < num; i++) {
/* 144 */         int offset = ByteArray.readU16bit(this.info, pos);
/* 145 */         int numLoc = ByteArray.readU16bit(this.info, pos + 2);
/* 146 */         pos = locals(pos + 4, offset, numLoc);
/* 147 */         int numStack = ByteArray.readU16bit(this.info, pos);
/* 148 */         pos = stack(pos + 2, offset, numStack);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int locals(int pos, int offset, int num)
/*     */     {
/* 157 */       return typeInfoArray(pos, offset, num, true);
/*     */     }
/*     */ 
/*     */     public int stack(int pos, int offset, int num)
/*     */     {
/* 165 */       return typeInfoArray(pos, offset, num, false);
/*     */     }
/*     */ 
/*     */     public int typeInfoArray(int pos, int offset, int num, boolean isLocals)
/*     */     {
/* 177 */       for (int k = 0; k < num; k++) {
/* 178 */         pos = typeInfoArray2(k, pos);
/*     */       }
/* 180 */       return pos;
/*     */     }
/*     */ 
/*     */     int typeInfoArray2(int k, int pos) {
/* 184 */       byte tag = this.info[pos];
/* 185 */       if (tag == 7) {
/* 186 */         int clazz = ByteArray.readU16bit(this.info, pos + 1);
/* 187 */         objectVariable(pos, clazz);
/* 188 */         pos += 3;
/*     */       }
/* 190 */       else if (tag == 8) {
/* 191 */         int offsetOfNew = ByteArray.readU16bit(this.info, pos + 1);
/* 192 */         uninitialized(pos, offsetOfNew);
/* 193 */         pos += 3;
/*     */       }
/*     */       else {
/* 196 */         typeInfo(pos, tag);
/* 197 */         pos++;
/*     */       }
/*     */ 
/* 200 */       return pos;
/*     */     }
/*     */ 
/*     */     public void typeInfo(int pos, byte tag)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void objectVariable(int pos, int clazz)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void uninitialized(int pos, int offset)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.StackMap
 * JD-Core Version:    0.6.2
 */