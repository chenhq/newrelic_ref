/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class DoubleInfo extends ConstInfo
/*      */ {
/*      */   static final int tag = 6;
/*      */   double value;
/*      */ 
/*      */   public DoubleInfo(double d)
/*      */   {
/* 1517 */     this.value = d;
/*      */   }
/*      */ 
/*      */   public DoubleInfo(DataInputStream in) throws IOException {
/* 1521 */     this.value = in.readDouble();
/*      */   }
/*      */   public int getTag() {
/* 1524 */     return 6;
/*      */   }
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1527 */     return dest.addDoubleInfo(this.value);
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1531 */     out.writeByte(6);
/* 1532 */     out.writeDouble(this.value);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1536 */     out.print("Double ");
/* 1537 */     out.println(this.value);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.DoubleInfo
 * JD-Core Version:    0.6.2
 */