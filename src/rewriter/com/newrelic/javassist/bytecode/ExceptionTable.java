/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ExceptionTable
/*     */   implements Cloneable
/*     */ {
/*     */   private ConstPool constPool;
/*     */   private ArrayList entries;
/*     */ 
/*     */   public ExceptionTable(ConstPool cp)
/*     */   {
/*  51 */     this.constPool = cp;
/*  52 */     this.entries = new ArrayList();
/*     */   }
/*     */ 
/*     */   ExceptionTable(ConstPool cp, DataInputStream in) throws IOException {
/*  56 */     this.constPool = cp;
/*  57 */     int length = in.readUnsignedShort();
/*  58 */     ArrayList list = new ArrayList(length);
/*  59 */     for (int i = 0; i < length; i++) {
/*  60 */       int start = in.readUnsignedShort();
/*  61 */       int end = in.readUnsignedShort();
/*  62 */       int handle = in.readUnsignedShort();
/*  63 */       int type = in.readUnsignedShort();
/*  64 */       list.add(new ExceptionTableEntry(start, end, handle, type));
/*     */     }
/*     */ 
/*  67 */     this.entries = list;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */     throws CloneNotSupportedException
/*     */   {
/*  76 */     ExceptionTable r = (ExceptionTable)super.clone();
/*  77 */     r.entries = new ArrayList(this.entries);
/*  78 */     return r;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  86 */     return this.entries.size();
/*     */   }
/*     */ 
/*     */   public int startPc(int nth)
/*     */   {
/*  95 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/*  96 */     return e.startPc;
/*     */   }
/*     */ 
/*     */   public void setStartPc(int nth, int value)
/*     */   {
/* 106 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/* 107 */     e.startPc = value;
/*     */   }
/*     */ 
/*     */   public int endPc(int nth)
/*     */   {
/* 116 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/* 117 */     return e.endPc;
/*     */   }
/*     */ 
/*     */   public void setEndPc(int nth, int value)
/*     */   {
/* 127 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/* 128 */     e.endPc = value;
/*     */   }
/*     */ 
/*     */   public int handlerPc(int nth)
/*     */   {
/* 137 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/* 138 */     return e.handlerPc;
/*     */   }
/*     */ 
/*     */   public void setHandlerPc(int nth, int value)
/*     */   {
/* 148 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/* 149 */     e.handlerPc = value;
/*     */   }
/*     */ 
/*     */   public int catchType(int nth)
/*     */   {
/* 160 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/* 161 */     return e.catchType;
/*     */   }
/*     */ 
/*     */   public void setCatchType(int nth, int value)
/*     */   {
/* 171 */     ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(nth);
/* 172 */     e.catchType = value;
/*     */   }
/*     */ 
/*     */   public void add(int index, ExceptionTable table, int offset)
/*     */   {
/* 183 */     int len = table.size();
/*     */     while (true) { len--; if (len < 0) break;
/* 185 */       ExceptionTableEntry e = (ExceptionTableEntry)table.entries.get(len);
/*     */ 
/* 187 */       add(index, e.startPc + offset, e.endPc + offset, e.handlerPc + offset, e.catchType);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void add(int index, int start, int end, int handler, int type)
/*     */   {
/* 202 */     if (start < end)
/* 203 */       this.entries.add(index, new ExceptionTableEntry(start, end, handler, type));
/*     */   }
/*     */ 
/*     */   public void add(int start, int end, int handler, int type)
/*     */   {
/* 216 */     if (start < end)
/* 217 */       this.entries.add(new ExceptionTableEntry(start, end, handler, type));
/*     */   }
/*     */ 
/*     */   public void remove(int index)
/*     */   {
/* 226 */     this.entries.remove(index);
/*     */   }
/*     */ 
/*     */   public ExceptionTable copy(ConstPool newCp, Map classnames)
/*     */   {
/* 239 */     ExceptionTable et = new ExceptionTable(newCp);
/* 240 */     ConstPool srcCp = this.constPool;
/* 241 */     int len = size();
/* 242 */     for (int i = 0; i < len; i++) {
/* 243 */       ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(i);
/* 244 */       int type = srcCp.copy(e.catchType, newCp, classnames);
/* 245 */       et.add(e.startPc, e.endPc, e.handlerPc, type);
/*     */     }
/*     */ 
/* 248 */     return et;
/*     */   }
/*     */ 
/*     */   void shiftPc(int where, int gapLength, boolean exclusive) {
/* 252 */     int len = size();
/* 253 */     for (int i = 0; i < len; i++) {
/* 254 */       ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(i);
/* 255 */       e.startPc = shiftPc(e.startPc, where, gapLength, exclusive);
/* 256 */       e.endPc = shiftPc(e.endPc, where, gapLength, exclusive);
/* 257 */       e.handlerPc = shiftPc(e.handlerPc, where, gapLength, exclusive);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int shiftPc(int pc, int where, int gapLength, boolean exclusive)
/*     */   {
/* 263 */     if ((pc > where) || ((exclusive) && (pc == where))) {
/* 264 */       pc += gapLength;
/*     */     }
/* 266 */     return pc;
/*     */   }
/*     */ 
/*     */   void write(DataOutputStream out) throws IOException {
/* 270 */     int len = size();
/* 271 */     out.writeShort(len);
/* 272 */     for (int i = 0; i < len; i++) {
/* 273 */       ExceptionTableEntry e = (ExceptionTableEntry)this.entries.get(i);
/* 274 */       out.writeShort(e.startPc);
/* 275 */       out.writeShort(e.endPc);
/* 276 */       out.writeShort(e.handlerPc);
/* 277 */       out.writeShort(e.catchType);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ExceptionTable
 * JD-Core Version:    0.6.2
 */