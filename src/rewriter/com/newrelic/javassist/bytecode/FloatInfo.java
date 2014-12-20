/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class FloatInfo extends ConstInfo
/*      */ {
/*      */   static final int tag = 4;
/*      */   float value;
/*      */ 
/*      */   public FloatInfo(float f)
/*      */   {
/* 1459 */     this.value = f;
/*      */   }
/*      */ 
/*      */   public FloatInfo(DataInputStream in) throws IOException {
/* 1463 */     this.value = in.readFloat();
/*      */   }
/*      */   public int getTag() {
/* 1466 */     return 4;
/*      */   }
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1469 */     return dest.addFloatInfo(this.value);
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1473 */     out.writeByte(4);
/* 1474 */     out.writeFloat(this.value);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1478 */     out.print("Float ");
/* 1479 */     out.println(this.value);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.FloatInfo
 * JD-Core Version:    0.6.2
 */