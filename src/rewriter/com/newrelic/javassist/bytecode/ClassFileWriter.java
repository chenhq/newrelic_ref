/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class ClassFileWriter
/*     */ {
/*     */   private ByteStream output;
/*     */   private ConstPoolWriter constPool;
/*     */   private FieldWriter fields;
/*     */   private MethodWriter methods;
/*     */   int thisClass;
/*     */   int superClass;
/*     */ 
/*     */   public ClassFileWriter(int major, int minor)
/*     */   {
/*  89 */     this.output = new ByteStream(512);
/*  90 */     this.output.writeInt(-889275714);
/*  91 */     this.output.writeShort(minor);
/*  92 */     this.output.writeShort(major);
/*  93 */     this.constPool = new ConstPoolWriter(this.output);
/*  94 */     this.fields = new FieldWriter(this.constPool);
/*  95 */     this.methods = new MethodWriter(this.constPool);
/*     */   }
/*     */ 
/*     */   public ConstPoolWriter getConstPool()
/*     */   {
/* 102 */     return this.constPool;
/*     */   }
/*     */ 
/*     */   public FieldWriter getFieldWriter()
/*     */   {
/* 107 */     return this.fields;
/*     */   }
/*     */ 
/*     */   public MethodWriter getMethodWriter()
/*     */   {
/* 112 */     return this.methods;
/*     */   }
/*     */ 
/*     */   public byte[] end(int accessFlags, int thisClass, int superClass, int[] interfaces, AttributeWriter aw)
/*     */   {
/* 129 */     this.constPool.end();
/* 130 */     this.output.writeShort(accessFlags);
/* 131 */     this.output.writeShort(thisClass);
/* 132 */     this.output.writeShort(superClass);
/* 133 */     if (interfaces == null) {
/* 134 */       this.output.writeShort(0);
/*     */     } else {
/* 136 */       int n = interfaces.length;
/* 137 */       this.output.writeShort(n);
/* 138 */       for (int i = 0; i < n; i++) {
/* 139 */         this.output.writeShort(interfaces[i]);
/*     */       }
/*     */     }
/* 142 */     this.output.enlarge(this.fields.dataSize() + this.methods.dataSize() + 6);
/*     */     try {
/* 144 */       this.output.writeShort(this.fields.size());
/* 145 */       this.fields.write(this.output);
/*     */ 
/* 147 */       this.output.writeShort(this.methods.size());
/* 148 */       this.methods.write(this.output);
/*     */     }
/*     */     catch (IOException e) {
/*     */     }
/* 152 */     writeAttribute(this.output, aw, 0);
/* 153 */     return this.output.toByteArray();
/*     */   }
/*     */ 
/*     */   public void end(DataOutputStream out, int accessFlags, int thisClass, int superClass, int[] interfaces, AttributeWriter aw)
/*     */     throws IOException
/*     */   {
/* 175 */     this.constPool.end();
/* 176 */     this.output.writeTo(out);
/* 177 */     out.writeShort(accessFlags);
/* 178 */     out.writeShort(thisClass);
/* 179 */     out.writeShort(superClass);
/* 180 */     if (interfaces == null) {
/* 181 */       out.writeShort(0);
/*     */     } else {
/* 183 */       int n = interfaces.length;
/* 184 */       out.writeShort(n);
/* 185 */       for (int i = 0; i < n; i++) {
/* 186 */         out.writeShort(interfaces[i]);
/*     */       }
/*     */     }
/* 189 */     out.writeShort(this.fields.size());
/* 190 */     this.fields.write(out);
/*     */ 
/* 192 */     out.writeShort(this.methods.size());
/* 193 */     this.methods.write(out);
/* 194 */     if (aw == null) {
/* 195 */       out.writeShort(0);
/*     */     } else {
/* 197 */       out.writeShort(aw.size());
/* 198 */       aw.write(out);
/*     */     }
/*     */   }
/*     */ 
/*     */   static void writeAttribute(ByteStream bs, AttributeWriter aw, int attrCount)
/*     */   {
/* 236 */     if (aw == null) {
/* 237 */       bs.writeShort(attrCount);
/* 238 */       return;
/*     */     }
/*     */ 
/* 241 */     bs.writeShort(aw.size() + attrCount);
/* 242 */     DataOutputStream dos = new DataOutputStream(bs);
/*     */     try {
/* 244 */       aw.write(dos);
/* 245 */       dos.flush();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class ConstPoolWriter
/*     */   {
/*     */     ByteStream output;
/*     */     protected int startPos;
/*     */     protected int num;
/*     */ 
/*     */     ConstPoolWriter(ByteStream out)
/*     */     {
/* 526 */       this.output = out;
/* 527 */       this.startPos = out.getPos();
/* 528 */       this.num = 1;
/* 529 */       this.output.writeShort(1);
/*     */     }
/*     */ 
/*     */     public int[] addClassInfo(String[] classNames)
/*     */     {
/* 538 */       int n = classNames.length;
/* 539 */       int[] result = new int[n];
/* 540 */       for (int i = 0; i < n; i++) {
/* 541 */         result[i] = addClassInfo(classNames[i]);
/*     */       }
/* 543 */       return result;
/*     */     }
/*     */ 
/*     */     public int addClassInfo(String jvmname)
/*     */     {
/* 557 */       int utf8 = addUtf8Info(jvmname);
/* 558 */       this.output.write(7);
/* 559 */       this.output.writeShort(utf8);
/* 560 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addClassInfo(int name)
/*     */     {
/* 570 */       this.output.write(7);
/* 571 */       this.output.writeShort(name);
/* 572 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addNameAndTypeInfo(String name, String type)
/*     */     {
/* 583 */       return addNameAndTypeInfo(addUtf8Info(name), addUtf8Info(type));
/*     */     }
/*     */ 
/*     */     public int addNameAndTypeInfo(int name, int type)
/*     */     {
/* 594 */       this.output.write(12);
/* 595 */       this.output.writeShort(name);
/* 596 */       this.output.writeShort(type);
/* 597 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addFieldrefInfo(int classInfo, int nameAndTypeInfo)
/*     */     {
/* 608 */       this.output.write(9);
/* 609 */       this.output.writeShort(classInfo);
/* 610 */       this.output.writeShort(nameAndTypeInfo);
/* 611 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addMethodrefInfo(int classInfo, int nameAndTypeInfo)
/*     */     {
/* 622 */       this.output.write(10);
/* 623 */       this.output.writeShort(classInfo);
/* 624 */       this.output.writeShort(nameAndTypeInfo);
/* 625 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addInterfaceMethodrefInfo(int classInfo, int nameAndTypeInfo)
/*     */     {
/* 638 */       this.output.write(11);
/* 639 */       this.output.writeShort(classInfo);
/* 640 */       this.output.writeShort(nameAndTypeInfo);
/* 641 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addStringInfo(String str)
/*     */     {
/* 654 */       this.output.write(8);
/* 655 */       this.output.writeShort(addUtf8Info(str));
/* 656 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addIntegerInfo(int i)
/*     */     {
/* 666 */       this.output.write(3);
/* 667 */       this.output.writeInt(i);
/* 668 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addFloatInfo(float f)
/*     */     {
/* 678 */       this.output.write(4);
/* 679 */       this.output.writeFloat(f);
/* 680 */       return this.num++;
/*     */     }
/*     */ 
/*     */     public int addLongInfo(long l)
/*     */     {
/* 690 */       this.output.write(5);
/* 691 */       this.output.writeLong(l);
/* 692 */       int n = this.num;
/* 693 */       this.num += 2;
/* 694 */       return n;
/*     */     }
/*     */ 
/*     */     public int addDoubleInfo(double d)
/*     */     {
/* 704 */       this.output.write(6);
/* 705 */       this.output.writeDouble(d);
/* 706 */       int n = this.num;
/* 707 */       this.num += 2;
/* 708 */       return n;
/*     */     }
/*     */ 
/*     */     public int addUtf8Info(String utf8)
/*     */     {
/* 718 */       this.output.write(1);
/* 719 */       this.output.writeUTF(utf8);
/* 720 */       return this.num++;
/*     */     }
/*     */ 
/*     */     void end()
/*     */     {
/* 727 */       this.output.writeShort(this.startPos, this.num);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class MethodWriter
/*     */   {
/*     */     protected ByteStream output;
/*     */     protected ClassFileWriter.ConstPoolWriter constPool;
/*     */     private int methodCount;
/*     */     protected int codeIndex;
/*     */     protected int throwsIndex;
/*     */     protected int stackIndex;
/*     */     private int startPos;
/*     */     private boolean isAbstract;
/*     */     private int catchPos;
/*     */     private int catchCount;
/*     */ 
/*     */     MethodWriter(ClassFileWriter.ConstPoolWriter cp)
/*     */     {
/* 325 */       this.output = new ByteStream(256);
/* 326 */       this.constPool = cp;
/* 327 */       this.methodCount = 0;
/* 328 */       this.codeIndex = 0;
/* 329 */       this.throwsIndex = 0;
/* 330 */       this.stackIndex = 0;
/*     */     }
/*     */ 
/*     */     public void begin(int accessFlags, String name, String descriptor, String[] exceptions, ClassFileWriter.AttributeWriter aw)
/*     */     {
/* 346 */       int nameIndex = this.constPool.addUtf8Info(name);
/* 347 */       int descIndex = this.constPool.addUtf8Info(descriptor);
/*     */       int[] intfs;
/*     */       int[] intfs;
/* 349 */       if (exceptions == null)
/* 350 */         intfs = null;
/*     */       else {
/* 352 */         intfs = this.constPool.addClassInfo(exceptions);
/*     */       }
/* 354 */       begin(accessFlags, nameIndex, descIndex, intfs, aw);
/*     */     }
/*     */ 
/*     */     public void begin(int accessFlags, int name, int descriptor, int[] exceptions, ClassFileWriter.AttributeWriter aw)
/*     */     {
/* 368 */       this.methodCount += 1;
/* 369 */       this.output.writeShort(accessFlags);
/* 370 */       this.output.writeShort(name);
/* 371 */       this.output.writeShort(descriptor);
/* 372 */       this.isAbstract = ((accessFlags & 0x400) != 0);
/*     */ 
/* 374 */       int attrCount = this.isAbstract ? 0 : 1;
/* 375 */       if (exceptions != null) {
/* 376 */         attrCount++;
/*     */       }
/* 378 */       ClassFileWriter.writeAttribute(this.output, aw, attrCount);
/*     */ 
/* 380 */       if (exceptions != null) {
/* 381 */         writeThrows(exceptions);
/*     */       }
/* 383 */       if (!this.isAbstract) {
/* 384 */         if (this.codeIndex == 0) {
/* 385 */           this.codeIndex = this.constPool.addUtf8Info("Code");
/*     */         }
/* 387 */         this.startPos = this.output.getPos();
/* 388 */         this.output.writeShort(this.codeIndex);
/* 389 */         this.output.writeBlank(12);
/*     */       }
/*     */ 
/* 392 */       this.catchPos = -1;
/* 393 */       this.catchCount = 0;
/*     */     }
/*     */ 
/*     */     private void writeThrows(int[] exceptions) {
/* 397 */       if (this.throwsIndex == 0) {
/* 398 */         this.throwsIndex = this.constPool.addUtf8Info("Exceptions");
/*     */       }
/* 400 */       this.output.writeShort(this.throwsIndex);
/* 401 */       this.output.writeInt(exceptions.length * 2 + 2);
/* 402 */       this.output.writeShort(exceptions.length);
/* 403 */       for (int i = 0; i < exceptions.length; i++)
/* 404 */         this.output.writeShort(exceptions[i]);
/*     */     }
/*     */ 
/*     */     public void add(int b)
/*     */     {
/* 413 */       this.output.write(b);
/*     */     }
/*     */ 
/*     */     public void add16(int b)
/*     */     {
/* 420 */       this.output.writeShort(b);
/*     */     }
/*     */ 
/*     */     public void add32(int b)
/*     */     {
/* 427 */       this.output.writeInt(b);
/*     */     }
/*     */ 
/*     */     public void addInvoke(int opcode, String targetClass, String methodName, String descriptor)
/*     */     {
/* 437 */       int target = this.constPool.addClassInfo(targetClass);
/* 438 */       int nt = this.constPool.addNameAndTypeInfo(methodName, descriptor);
/* 439 */       int method = this.constPool.addMethodrefInfo(target, nt);
/* 440 */       add(opcode);
/* 441 */       add16(method);
/*     */     }
/*     */ 
/*     */     public void codeEnd(int maxStack, int maxLocals)
/*     */     {
/* 448 */       if (!this.isAbstract) {
/* 449 */         this.output.writeShort(this.startPos + 6, maxStack);
/* 450 */         this.output.writeShort(this.startPos + 8, maxLocals);
/* 451 */         this.output.writeInt(this.startPos + 10, this.output.getPos() - this.startPos - 14);
/* 452 */         this.catchPos = this.output.getPos();
/* 453 */         this.catchCount = 0;
/* 454 */         this.output.writeShort(0);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void addCatch(int startPc, int endPc, int handlerPc, int catchType)
/*     */     {
/* 466 */       this.catchCount += 1;
/* 467 */       this.output.writeShort(startPc);
/* 468 */       this.output.writeShort(endPc);
/* 469 */       this.output.writeShort(handlerPc);
/* 470 */       this.output.writeShort(catchType);
/*     */     }
/*     */ 
/*     */     public void end(StackMapTable.Writer smap, ClassFileWriter.AttributeWriter aw)
/*     */     {
/* 482 */       if (this.isAbstract) {
/* 483 */         return;
/*     */       }
/*     */ 
/* 486 */       this.output.writeShort(this.catchPos, this.catchCount);
/*     */ 
/* 488 */       int attrCount = smap == null ? 0 : 1;
/* 489 */       ClassFileWriter.writeAttribute(this.output, aw, attrCount);
/*     */ 
/* 491 */       if (smap != null) {
/* 492 */         if (this.stackIndex == 0) {
/* 493 */           this.stackIndex = this.constPool.addUtf8Info("StackMapTable");
/*     */         }
/* 495 */         this.output.writeShort(this.stackIndex);
/* 496 */         byte[] data = smap.toByteArray();
/* 497 */         this.output.writeInt(data.length);
/* 498 */         this.output.write(data);
/*     */       }
/*     */ 
/* 502 */       this.output.writeInt(this.startPos + 2, this.output.getPos() - this.startPos - 6);
/*     */     }
/*     */     int size() {
/* 505 */       return this.methodCount;
/*     */     }
/* 507 */     int dataSize() { return this.output.size(); }
/*     */ 
/*     */ 
/*     */     void write(OutputStream out)
/*     */       throws IOException
/*     */     {
/* 513 */       this.output.writeTo(out);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class FieldWriter
/*     */   {
/*     */     protected ByteStream output;
/*     */     protected ClassFileWriter.ConstPoolWriter constPool;
/*     */     private int fieldCount;
/*     */ 
/*     */     FieldWriter(ClassFileWriter.ConstPoolWriter cp)
/*     */     {
/* 259 */       this.output = new ByteStream(128);
/* 260 */       this.constPool = cp;
/* 261 */       this.fieldCount = 0;
/*     */     }
/*     */ 
/*     */     public void add(int accessFlags, String name, String descriptor, ClassFileWriter.AttributeWriter aw)
/*     */     {
/* 274 */       int nameIndex = this.constPool.addUtf8Info(name);
/* 275 */       int descIndex = this.constPool.addUtf8Info(descriptor);
/* 276 */       add(accessFlags, nameIndex, descIndex, aw);
/*     */     }
/*     */ 
/*     */     public void add(int accessFlags, int name, int descriptor, ClassFileWriter.AttributeWriter aw)
/*     */     {
/* 289 */       this.fieldCount += 1;
/* 290 */       this.output.writeShort(accessFlags);
/* 291 */       this.output.writeShort(name);
/* 292 */       this.output.writeShort(descriptor);
/* 293 */       ClassFileWriter.writeAttribute(this.output, aw, 0);
/*     */     }
/*     */     int size() {
/* 296 */       return this.fieldCount;
/*     */     }
/* 298 */     int dataSize() { return this.output.size(); }
/*     */ 
/*     */ 
/*     */     void write(OutputStream out)
/*     */       throws IOException
/*     */     {
/* 304 */       this.output.writeTo(out);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface AttributeWriter
/*     */   {
/*     */     public abstract int size();
/*     */ 
/*     */     public abstract void write(DataOutputStream paramDataOutputStream)
/*     */       throws IOException;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ClassFileWriter
 * JD-Core Version:    0.6.2
 */