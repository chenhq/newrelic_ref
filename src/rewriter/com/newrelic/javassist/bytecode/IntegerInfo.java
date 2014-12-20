/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class IntegerInfo extends ConstInfo
/*      */ {
/*      */   static final int tag = 3;
/*      */   int value;
/*      */ 
/*      */   public IntegerInfo(int i)
/*      */   {
/* 1430 */     this.value = i;
/*      */   }
/*      */ 
/*      */   public IntegerInfo(DataInputStream in) throws IOException {
/* 1434 */     this.value = in.readInt();
/*      */   }
/*      */   public int getTag() {
/* 1437 */     return 3;
/*      */   }
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1440 */     return dest.addIntegerInfo(this.value);
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1444 */     out.writeByte(3);
/* 1445 */     out.writeInt(this.value);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1449 */     out.print("Integer ");
/* 1450 */     out.println(this.value);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.IntegerInfo
 * JD-Core Version:    0.6.2
 */