/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LocalVariableAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "LocalVariableTable";
/*     */   public static final String typeTag = "LocalVariableTypeTable";
/*     */ 
/*     */   public LocalVariableAttribute(ConstPool cp)
/*     */   {
/*  40 */     super(cp, "LocalVariableTable", new byte[2]);
/*  41 */     ByteArray.write16bit(0, this.info, 0);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public LocalVariableAttribute(ConstPool cp, String name)
/*     */   {
/*  56 */     super(cp, name, new byte[2]);
/*  57 */     ByteArray.write16bit(0, this.info, 0);
/*     */   }
/*     */ 
/*     */   LocalVariableAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  63 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   LocalVariableAttribute(ConstPool cp, String name, byte[] i) {
/*  67 */     super(cp, name, i);
/*     */   }
/*     */ 
/*     */   public void addEntry(int startPc, int length, int nameIndex, int descriptorIndex, int index)
/*     */   {
/*  81 */     int size = this.info.length;
/*  82 */     byte[] newInfo = new byte[size + 10];
/*  83 */     ByteArray.write16bit(tableLength() + 1, newInfo, 0);
/*  84 */     for (int i = 2; i < size; i++) {
/*  85 */       newInfo[i] = this.info[i];
/*     */     }
/*  87 */     ByteArray.write16bit(startPc, newInfo, size);
/*  88 */     ByteArray.write16bit(length, newInfo, size + 2);
/*  89 */     ByteArray.write16bit(nameIndex, newInfo, size + 4);
/*  90 */     ByteArray.write16bit(descriptorIndex, newInfo, size + 6);
/*  91 */     ByteArray.write16bit(index, newInfo, size + 8);
/*  92 */     this.info = newInfo;
/*     */   }
/*     */ 
/*     */   void renameClass(String oldname, String newname) {
/*  96 */     ConstPool cp = getConstPool();
/*  97 */     int n = tableLength();
/*  98 */     for (int i = 0; i < n; i++) {
/*  99 */       int pos = i * 10 + 2;
/* 100 */       int index = ByteArray.readU16bit(this.info, pos + 6);
/* 101 */       if (index != 0) {
/* 102 */         String desc = cp.getUtf8Info(index);
/* 103 */         desc = renameEntry(desc, oldname, newname);
/* 104 */         ByteArray.write16bit(cp.addUtf8Info(desc), this.info, pos + 6);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   String renameEntry(String desc, String oldname, String newname) {
/* 110 */     return Descriptor.rename(desc, oldname, newname);
/*     */   }
/*     */ 
/*     */   void renameClass(Map classnames) {
/* 114 */     ConstPool cp = getConstPool();
/* 115 */     int n = tableLength();
/* 116 */     for (int i = 0; i < n; i++) {
/* 117 */       int pos = i * 10 + 2;
/* 118 */       int index = ByteArray.readU16bit(this.info, pos + 6);
/* 119 */       if (index != 0) {
/* 120 */         String desc = cp.getUtf8Info(index);
/* 121 */         desc = renameEntry(desc, classnames);
/* 122 */         ByteArray.write16bit(cp.addUtf8Info(desc), this.info, pos + 6);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   String renameEntry(String desc, Map classnames) {
/* 128 */     return Descriptor.rename(desc, classnames);
/*     */   }
/*     */ 
/*     */   public void shiftIndex(int lessThan, int delta)
/*     */   {
/* 139 */     int size = this.info.length;
/* 140 */     for (int i = 2; i < size; i += 10) {
/* 141 */       int org = ByteArray.readU16bit(this.info, i + 8);
/* 142 */       if (org >= lessThan)
/* 143 */         ByteArray.write16bit(org + delta, this.info, i + 8);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int tableLength()
/*     */   {
/* 152 */     return ByteArray.readU16bit(this.info, 0);
/*     */   }
/*     */ 
/*     */   public int startPc(int i)
/*     */   {
/* 163 */     return ByteArray.readU16bit(this.info, i * 10 + 2);
/*     */   }
/*     */ 
/*     */   public int codeLength(int i)
/*     */   {
/* 174 */     return ByteArray.readU16bit(this.info, i * 10 + 4);
/*     */   }
/*     */ 
/*     */   void shiftPc(int where, int gapLength, boolean exclusive)
/*     */   {
/* 181 */     int n = tableLength();
/* 182 */     for (int i = 0; i < n; i++) {
/* 183 */       int pos = i * 10 + 2;
/* 184 */       int pc = ByteArray.readU16bit(this.info, pos);
/* 185 */       int len = ByteArray.readU16bit(this.info, pos + 2);
/*     */ 
/* 189 */       if ((pc > where) || ((exclusive) && (pc == where) && (pc != 0)))
/* 190 */         ByteArray.write16bit(pc + gapLength, this.info, pos);
/* 191 */       else if ((pc + len > where) || ((exclusive) && (pc + len == where)))
/* 192 */         ByteArray.write16bit(len + gapLength, this.info, pos + 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int nameIndex(int i)
/*     */   {
/* 203 */     return ByteArray.readU16bit(this.info, i * 10 + 6);
/*     */   }
/*     */ 
/*     */   public String variableName(int i)
/*     */   {
/* 213 */     return getConstPool().getUtf8Info(nameIndex(i));
/*     */   }
/*     */ 
/*     */   public int descriptorIndex(int i)
/*     */   {
/* 229 */     return ByteArray.readU16bit(this.info, i * 10 + 8);
/*     */   }
/*     */ 
/*     */   public int signatureIndex(int i)
/*     */   {
/* 243 */     return descriptorIndex(i);
/*     */   }
/*     */ 
/*     */   public String descriptor(int i)
/*     */   {
/* 257 */     return getConstPool().getUtf8Info(descriptorIndex(i));
/*     */   }
/*     */ 
/*     */   public String signature(int i)
/*     */   {
/* 274 */     return descriptor(i);
/*     */   }
/*     */ 
/*     */   public int index(int i)
/*     */   {
/* 284 */     return ByteArray.readU16bit(this.info, i * 10 + 10);
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 294 */     byte[] src = get();
/* 295 */     byte[] dest = new byte[src.length];
/* 296 */     ConstPool cp = getConstPool();
/* 297 */     LocalVariableAttribute attr = makeThisAttr(newCp, dest);
/* 298 */     int n = ByteArray.readU16bit(src, 0);
/* 299 */     ByteArray.write16bit(n, dest, 0);
/* 300 */     int j = 2;
/* 301 */     for (int i = 0; i < n; i++) {
/* 302 */       int start = ByteArray.readU16bit(src, j);
/* 303 */       int len = ByteArray.readU16bit(src, j + 2);
/* 304 */       int name = ByteArray.readU16bit(src, j + 4);
/* 305 */       int type = ByteArray.readU16bit(src, j + 6);
/* 306 */       int index = ByteArray.readU16bit(src, j + 8);
/*     */ 
/* 308 */       ByteArray.write16bit(start, dest, j);
/* 309 */       ByteArray.write16bit(len, dest, j + 2);
/* 310 */       if (name != 0) {
/* 311 */         name = cp.copy(name, newCp, null);
/*     */       }
/* 313 */       ByteArray.write16bit(name, dest, j + 4);
/*     */ 
/* 315 */       if (type != 0) {
/* 316 */         String sig = cp.getUtf8Info(type);
/* 317 */         sig = Descriptor.rename(sig, classnames);
/* 318 */         type = newCp.addUtf8Info(sig);
/*     */       }
/*     */ 
/* 321 */       ByteArray.write16bit(type, dest, j + 6);
/* 322 */       ByteArray.write16bit(index, dest, j + 8);
/* 323 */       j += 10;
/*     */     }
/*     */ 
/* 326 */     return attr;
/*     */   }
/*     */ 
/*     */   LocalVariableAttribute makeThisAttr(ConstPool cp, byte[] dest)
/*     */   {
/* 331 */     return new LocalVariableAttribute(cp, "LocalVariableTable", dest);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.LocalVariableAttribute
 * JD-Core Version:    0.6.2
 */