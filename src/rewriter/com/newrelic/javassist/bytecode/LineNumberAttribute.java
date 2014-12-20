/*     */ package com.newrelic.javassist.bytecode;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LineNumberAttribute extends AttributeInfo
/*     */ {
/*     */   public static final String tag = "LineNumberTable";
/*     */ 
/*     */   LineNumberAttribute(ConstPool cp, int n, DataInputStream in)
/*     */     throws IOException
/*     */   {
/*  34 */     super(cp, n, in);
/*     */   }
/*     */ 
/*     */   private LineNumberAttribute(ConstPool cp, byte[] i) {
/*  38 */     super(cp, "LineNumberTable", i);
/*     */   }
/*     */ 
/*     */   public int tableLength()
/*     */   {
/*  46 */     return ByteArray.readU16bit(this.info, 0);
/*     */   }
/*     */ 
/*     */   public int startPc(int i)
/*     */   {
/*  57 */     return ByteArray.readU16bit(this.info, i * 4 + 2);
/*     */   }
/*     */ 
/*     */   public int lineNumber(int i)
/*     */   {
/*  68 */     return ByteArray.readU16bit(this.info, i * 4 + 4);
/*     */   }
/*     */ 
/*     */   public int toLineNumber(int pc)
/*     */   {
/*  77 */     int n = tableLength();
/*  78 */     for (int i = 0; 
/*  79 */       i < n; i++) {
/*  80 */       if (pc < startPc(i)) {
/*  81 */         if (i != 0) break;
/*  82 */         return lineNumber(0);
/*     */       }
/*     */     }
/*     */ 
/*  86 */     return lineNumber(i - 1);
/*     */   }
/*     */ 
/*     */   public int toStartPc(int line)
/*     */   {
/*  97 */     int n = tableLength();
/*  98 */     for (int i = 0; i < n; i++) {
/*  99 */       if (line == lineNumber(i))
/* 100 */         return startPc(i);
/*     */     }
/* 102 */     return -1;
/*     */   }
/*     */ 
/*     */   public Pc toNearPc(int line)
/*     */   {
/* 129 */     int n = tableLength();
/* 130 */     int nearPc = 0;
/* 131 */     int distance = 0;
/* 132 */     if (n > 0) {
/* 133 */       distance = lineNumber(0) - line;
/* 134 */       nearPc = startPc(0);
/*     */     }
/*     */ 
/* 137 */     for (int i = 1; i < n; i++) {
/* 138 */       int d = lineNumber(i) - line;
/* 139 */       if (((d < 0) && (d > distance)) || ((d >= 0) && ((d < distance) || (distance < 0))))
/*     */       {
/* 141 */         distance = d;
/* 142 */         nearPc = startPc(i);
/*     */       }
/*     */     }
/*     */ 
/* 146 */     Pc res = new Pc();
/* 147 */     res.index = nearPc;
/* 148 */     res.line = (line + distance);
/* 149 */     return res;
/*     */   }
/*     */ 
/*     */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*     */   {
/* 159 */     byte[] src = this.info;
/* 160 */     int num = src.length;
/* 161 */     byte[] dest = new byte[num];
/* 162 */     for (int i = 0; i < num; i++) {
/* 163 */       dest[i] = src[i];
/*     */     }
/* 165 */     LineNumberAttribute attr = new LineNumberAttribute(newCp, dest);
/* 166 */     return attr;
/*     */   }
/*     */ 
/*     */   void shiftPc(int where, int gapLength, boolean exclusive)
/*     */   {
/* 173 */     int n = tableLength();
/* 174 */     for (int i = 0; i < n; i++) {
/* 175 */       int pos = i * 4 + 2;
/* 176 */       int pc = ByteArray.readU16bit(this.info, pos);
/* 177 */       if ((pc > where) || ((exclusive) && (pc == where)))
/* 178 */         ByteArray.write16bit(pc + gapLength, this.info, pos);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Pc
/*     */   {
/*     */     public int index;
/*     */     public int line;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.LineNumberAttribute
 * JD-Core Version:    0.6.2
 */