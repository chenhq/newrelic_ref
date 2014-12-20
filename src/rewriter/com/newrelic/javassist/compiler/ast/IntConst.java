/*     */ package com.newrelic.javassist.compiler.ast;
/*     */ 
/*     */ import com.newrelic.javassist.compiler.CompileError;
/*     */ 
/*     */ public class IntConst extends ASTree
/*     */ {
/*     */   protected long value;
/*     */   protected int type;
/*     */ 
/*     */   public IntConst(long v, int tokenId)
/*     */   {
/*  28 */     this.value = v; this.type = tokenId;
/*     */   }
/*  30 */   public long get() { return this.value; } 
/*     */   public void set(long v) {
/*  32 */     this.value = v;
/*     */   }
/*     */ 
/*     */   public int getType() {
/*  36 */     return this.type;
/*     */   }
/*  38 */   public String toString() { return Long.toString(this.value); }
/*     */ 
/*     */   public void accept(Visitor v) throws CompileError {
/*  41 */     v.atIntConst(this);
/*     */   }
/*     */ 
/*     */   public ASTree compute(int op, ASTree right) {
/*  45 */     if ((right instanceof IntConst))
/*  46 */       return compute0(op, (IntConst)right);
/*  47 */     if ((right instanceof DoubleConst)) {
/*  48 */       return compute0(op, (DoubleConst)right);
/*     */     }
/*  50 */     return null;
/*     */   }
/*     */ 
/*     */   private IntConst compute0(int op, IntConst right) {
/*  54 */     int type1 = this.type;
/*  55 */     int type2 = right.type;
/*     */     int newType;
/*     */     int newType;
/*  57 */     if ((type1 == 403) || (type2 == 403)) {
/*  58 */       newType = 403;
/*     */     }
/*     */     else
/*     */     {
/*     */       int newType;
/*  59 */       if ((type1 == 401) && (type2 == 401))
/*     */       {
/*  61 */         newType = 401;
/*     */       }
/*  63 */       else newType = 402;
/*     */     }
/*  65 */     long value1 = this.value;
/*  66 */     long value2 = right.value;
/*     */     long newValue;
/*  68 */     switch (op) {
/*     */     case 43:
/*  70 */       newValue = value1 + value2;
/*  71 */       break;
/*     */     case 45:
/*  73 */       newValue = value1 - value2;
/*  74 */       break;
/*     */     case 42:
/*  76 */       newValue = value1 * value2;
/*  77 */       break;
/*     */     case 47:
/*  79 */       newValue = value1 / value2;
/*  80 */       break;
/*     */     case 37:
/*  82 */       newValue = value1 % value2;
/*  83 */       break;
/*     */     case 124:
/*  85 */       newValue = value1 | value2;
/*  86 */       break;
/*     */     case 94:
/*  88 */       newValue = value1 ^ value2;
/*  89 */       break;
/*     */     case 38:
/*  91 */       newValue = value1 & value2;
/*  92 */       break;
/*     */     case 364:
/*  94 */       newValue = this.value << (int)value2;
/*  95 */       newType = type1;
/*  96 */       break;
/*     */     case 366:
/*  98 */       newValue = this.value >> (int)value2;
/*  99 */       newType = type1;
/* 100 */       break;
/*     */     case 370:
/* 102 */       newValue = this.value >>> (int)value2;
/* 103 */       newType = type1;
/* 104 */       break;
/*     */     default:
/* 106 */       return null;
/*     */     }
/*     */ 
/* 109 */     return new IntConst(newValue, newType);
/*     */   }
/*     */ 
/*     */   private DoubleConst compute0(int op, DoubleConst right) {
/* 113 */     double value1 = this.value;
/* 114 */     double value2 = right.value;
/*     */     double newValue;
/* 116 */     switch (op) {
/*     */     case 43:
/* 118 */       newValue = value1 + value2;
/* 119 */       break;
/*     */     case 45:
/* 121 */       newValue = value1 - value2;
/* 122 */       break;
/*     */     case 42:
/* 124 */       newValue = value1 * value2;
/* 125 */       break;
/*     */     case 47:
/* 127 */       newValue = value1 / value2;
/* 128 */       break;
/*     */     case 37:
/* 130 */       newValue = value1 % value2;
/* 131 */       break;
/*     */     case 38:
/*     */     case 39:
/*     */     case 40:
/*     */     case 41:
/*     */     case 44:
/*     */     case 46:
/*     */     default:
/* 133 */       return null;
/*     */     }
/*     */ 
/* 136 */     return new DoubleConst(newValue, right.type);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.IntConst
 * JD-Core Version:    0.6.2
 */