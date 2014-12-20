/*    */ package com.newrelic.javassist.compiler.ast;
/*    */ 
/*    */ import com.newrelic.javassist.compiler.CompileError;
/*    */ 
/*    */ public class DoubleConst extends ASTree
/*    */ {
/*    */   protected double value;
/*    */   protected int type;
/*    */ 
/*    */   public DoubleConst(double v, int tokenId)
/*    */   {
/* 28 */     this.value = v; this.type = tokenId;
/*    */   }
/* 30 */   public double get() { return this.value; } 
/*    */   public void set(double v) {
/* 32 */     this.value = v;
/*    */   }
/*    */ 
/*    */   public int getType() {
/* 36 */     return this.type;
/*    */   }
/* 38 */   public String toString() { return Double.toString(this.value); }
/*    */ 
/*    */   public void accept(Visitor v) throws CompileError {
/* 41 */     v.atDoubleConst(this);
/*    */   }
/*    */ 
/*    */   public ASTree compute(int op, ASTree right) {
/* 45 */     if ((right instanceof IntConst))
/* 46 */       return compute0(op, (IntConst)right);
/* 47 */     if ((right instanceof DoubleConst)) {
/* 48 */       return compute0(op, (DoubleConst)right);
/*    */     }
/* 50 */     return null;
/*    */   }
/*    */ 
/*    */   private DoubleConst compute0(int op, DoubleConst right)
/*    */   {
/*    */     int newType;
/*    */     int newType;
/* 55 */     if ((this.type == 405) || (right.type == 405))
/*    */     {
/* 57 */       newType = 405;
/*    */     }
/* 59 */     else newType = 404;
/*    */ 
/* 61 */     return compute(op, this.value, right.value, newType);
/*    */   }
/*    */ 
/*    */   private DoubleConst compute0(int op, IntConst right) {
/* 65 */     return compute(op, this.value, right.value, this.type);
/*    */   }
/*    */ 
/*    */   private static DoubleConst compute(int op, double value1, double value2, int newType)
/*    */   {
/*    */     double newValue;
/* 72 */     switch (op) {
/*    */     case 43:
/* 74 */       newValue = value1 + value2;
/* 75 */       break;
/*    */     case 45:
/* 77 */       newValue = value1 - value2;
/* 78 */       break;
/*    */     case 42:
/* 80 */       newValue = value1 * value2;
/* 81 */       break;
/*    */     case 47:
/* 83 */       newValue = value1 / value2;
/* 84 */       break;
/*    */     case 37:
/* 86 */       newValue = value1 % value2;
/* 87 */       break;
/*    */     case 38:
/*    */     case 39:
/*    */     case 40:
/*    */     case 41:
/*    */     case 44:
/*    */     case 46:
/*    */     default:
/* 89 */       return null;
/*    */     }
/*    */ 
/* 92 */     return new DoubleConst(newValue, newType);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.compiler.ast.DoubleConst
 * JD-Core Version:    0.6.2
 */