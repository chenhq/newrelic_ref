/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ final class LongVector
/*    */ {
/*    */   static final int ASIZE = 128;
/*    */   static final int ABITS = 7;
/*    */   static final int VSIZE = 8;
/*    */   private ConstInfo[][] objects;
/*    */   private int elements;
/*    */ 
/*    */   public LongVector()
/*    */   {
/* 26 */     this.objects = new ConstInfo[8][];
/* 27 */     this.elements = 0;
/*    */   }
/*    */ 
/*    */   public LongVector(int initialSize) {
/* 31 */     int vsize = (initialSize >> 7 & 0xFFFFFFF8) + 8;
/* 32 */     this.objects = new ConstInfo[vsize][];
/* 33 */     this.elements = 0;
/*    */   }
/*    */   public int size() {
/* 36 */     return this.elements;
/*    */   }
/* 38 */   public int capacity() { return this.objects.length * 128; }
/*    */ 
/*    */   public ConstInfo elementAt(int i) {
/* 41 */     if ((i < 0) || (this.elements <= i)) {
/* 42 */       return null;
/*    */     }
/* 44 */     return this.objects[(i >> 7)][(i & 0x7F)];
/*    */   }
/*    */ 
/*    */   public void addElement(ConstInfo value) {
/* 48 */     int nth = this.elements >> 7;
/* 49 */     int offset = this.elements & 0x7F;
/* 50 */     int len = this.objects.length;
/* 51 */     if (nth >= len) {
/* 52 */       ConstInfo[][] newObj = new ConstInfo[len + 8][];
/* 53 */       System.arraycopy(this.objects, 0, newObj, 0, len);
/* 54 */       this.objects = newObj;
/*    */     }
/*    */ 
/* 57 */     if (this.objects[nth] == null) {
/* 58 */       this.objects[nth] = new ConstInfo[''];
/*    */     }
/* 60 */     this.objects[nth][offset] = value;
/* 61 */     this.elements += 1;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.LongVector
 * JD-Core Version:    0.6.2
 */