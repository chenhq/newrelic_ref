/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ class ByteVector
/*    */   implements Cloneable
/*    */ {
/*    */   private byte[] buffer;
/*    */   private int size;
/*    */ 
/*    */   public ByteVector()
/*    */   {
/* 26 */     this.buffer = new byte[64];
/* 27 */     this.size = 0;
/*    */   }
/*    */ 
/*    */   public Object clone() throws CloneNotSupportedException {
/* 31 */     ByteVector bv = (ByteVector)super.clone();
/* 32 */     bv.buffer = ((byte[])this.buffer.clone());
/* 33 */     return bv;
/*    */   }
/*    */   public final int getSize() {
/* 36 */     return this.size;
/*    */   }
/*    */   public final byte[] copy() {
/* 39 */     byte[] b = new byte[this.size];
/* 40 */     System.arraycopy(this.buffer, 0, b, 0, this.size);
/* 41 */     return b;
/*    */   }
/*    */ 
/*    */   public int read(int offset) {
/* 45 */     if ((offset < 0) || (this.size <= offset)) {
/* 46 */       throw new ArrayIndexOutOfBoundsException(offset);
/*    */     }
/* 48 */     return this.buffer[offset];
/*    */   }
/*    */ 
/*    */   public void write(int offset, int value) {
/* 52 */     if ((offset < 0) || (this.size <= offset)) {
/* 53 */       throw new ArrayIndexOutOfBoundsException(offset);
/*    */     }
/* 55 */     this.buffer[offset] = ((byte)value);
/*    */   }
/*    */ 
/*    */   public void add(int code) {
/* 59 */     addGap(1);
/* 60 */     this.buffer[(this.size - 1)] = ((byte)code);
/*    */   }
/*    */ 
/*    */   public void add(int b1, int b2) {
/* 64 */     addGap(2);
/* 65 */     this.buffer[(this.size - 2)] = ((byte)b1);
/* 66 */     this.buffer[(this.size - 1)] = ((byte)b2);
/*    */   }
/*    */ 
/*    */   public void add(int b1, int b2, int b3, int b4) {
/* 70 */     addGap(4);
/* 71 */     this.buffer[(this.size - 4)] = ((byte)b1);
/* 72 */     this.buffer[(this.size - 3)] = ((byte)b2);
/* 73 */     this.buffer[(this.size - 2)] = ((byte)b3);
/* 74 */     this.buffer[(this.size - 1)] = ((byte)b4);
/*    */   }
/*    */ 
/*    */   public void addGap(int length) {
/* 78 */     if (this.size + length > this.buffer.length) {
/* 79 */       int newSize = this.size << 1;
/* 80 */       if (newSize < this.size + length) {
/* 81 */         newSize = this.size + length;
/*    */       }
/* 83 */       byte[] newBuf = new byte[newSize];
/* 84 */       System.arraycopy(this.buffer, 0, newBuf, 0, this.size);
/* 85 */       this.buffer = newBuf;
/*    */     }
/*    */ 
/* 88 */     this.size += length;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ByteVector
 * JD-Core Version:    0.6.2
 */