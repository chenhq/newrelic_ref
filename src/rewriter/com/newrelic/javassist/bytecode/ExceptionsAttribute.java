/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ExceptionsAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "Exceptions";
/*     */ 
/*     */   ExceptionsAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  34 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   private ExceptionsAttribute(ConstPool cp, ExceptionsAttribute src, Map classnames)
/*     */   {
/*  45 */     super(cp, "Exceptions");
/*  46 */     copyFrom(src, classnames);
/*     */   }
/*     */ 
/*     */   public ExceptionsAttribute(ConstPool cp)
/*     */   {
/*  55 */     super(cp, "Exceptions");
/*  56 */     byte[] data = new byte[2];
/*     */     int tmp16_15 = 0; data[1] = tmp16_15; data[0] = tmp16_15;
/*  58 */     this.info = data;
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/*  70 */     return new ExceptionsAttribute(newCp, this, classnames);
/*     */   }
/*     */ 
/*     */   private void copyFrom(ExceptionsAttribute srcAttr, Map classnames)
/*     */   {
/*  82 */     ConstPool srcCp = srcAttr.constPool;
/*  83 */     ConstPool destCp = this.constPool;
/*  84 */     byte[] src = srcAttr.info;
/*  85 */     int num = src.length;
/*  86 */     byte[] dest = new byte[num];
/*  87 */     dest[0] = src[0];
/*  88 */     dest[1] = src[1];
/*  89 */     for (int i = 2; i < num; i += 2) {
/*  90 */       int index = ByteArray.readU16bit(src, i);
/*  91 */       ByteArray.write16bit(srcCp.copy(index, destCp, classnames), dest, i);
/*     */     }
/*     */ 
/*  95 */     this.info = dest;
/*     */   }
/*     */ 
/*     */   public int[] getExceptionIndexes()
/*     */   {
/* 102 */     byte[] blist = this.info;
/* 103 */     int n = blist.length;
/* 104 */     if (n <= 2) {
/* 105 */       return null;
/*     */     }
/* 107 */     int[] elist = new int[n / 2 - 1];
/* 108 */     int k = 0;
/* 109 */     for (int j = 2; j < n; j += 2) {
/* 110 */       elist[(k++)] = ((blist[j] & 0xFF) << 8 | blist[(j + 1)] & 0xFF);
/*     */     }
/* 112 */     return elist;
/*     */   }
/*     */ 
/*     */   public String[] getExceptions()
/*     */   {
/* 119 */     byte[] blist = this.info;
/* 120 */     int n = blist.length;
/* 121 */     if (n <= 2) {
/* 122 */       return null;
/*     */     }
/* 124 */     String[] elist = new String[n / 2 - 1];
/* 125 */     int k = 0;
/* 126 */     for (int j = 2; j < n; j += 2) {
/* 127 */       int index = (blist[j] & 0xFF) << 8 | blist[(j + 1)] & 0xFF;
/* 128 */       elist[(k++)] = this.constPool.getClassInfo(index);
/*     */     }
/*     */ 
/* 131 */     return elist;
/*     */   }
/*     */ 
/*     */   public void setExceptionIndexes(int[] elist)
/*     */   {
/* 138 */     int n = elist.length;
/* 139 */     byte[] blist = new byte[n * 2 + 2];
/* 140 */     ByteArray.write16bit(n, blist, 0);
/* 141 */     for (int i = 0; i < n; i++) {
/* 142 */       ByteArray.write16bit(elist[i], blist, i * 2 + 2);
/*     */     }
/* 144 */     this.info = blist;
/*     */   }
/*     */ 
/*     */   public void setExceptions(String[] elist)
/*     */   {
/* 151 */     int n = elist.length;
/* 152 */     byte[] blist = new byte[n * 2 + 2];
/* 153 */     ByteArray.write16bit(n, blist, 0);
/* 154 */     for (int i = 0; i < n; i++) {
/* 155 */       ByteArray.write16bit(this.constPool.addClassInfo(elist[i]), blist, i * 2 + 2);
/*     */     }
/*     */ 
/* 158 */     this.info = blist;
/*     */   }
/*     */ 
/*     */   public int tableLength()
/*     */   {
/* 164 */     return this.info.length / 2 - 1;
/*     */   }
/*     */ 
/*     */   public int getException(int nth)
/*     */   {
/* 170 */     int index = nth * 2 + 2;
/* 171 */     return (this.info[index] & 0xFF) << 8 | this.info[(index + 1)] & 0xFF;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ExceptionsAttribute
 * JD-Core Version:    0.6.2
 */