/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class LongInfo extends ConstInfo
/*      */ {
/*      */   static final int tag = 5;
/*      */   long value;
/*      */ 
/*      */   public LongInfo(long l)
/*      */   {
/* 1488 */     this.value = l;
/*      */   }
/*      */ 
/*      */   public LongInfo(DataInputStream in) throws IOException {
/* 1492 */     this.value = in.readLong();
/*      */   }
/*      */   public int getTag() {
/* 1495 */     return 5;
/*      */   }
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1498 */     return dest.addLongInfo(this.value);
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1502 */     out.writeByte(5);
/* 1503 */     out.writeLong(this.value);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1507 */     out.print("Long ");
/* 1508 */     out.println(this.value);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.LongInfo
 * JD-Core Version:    0.6.2
 */