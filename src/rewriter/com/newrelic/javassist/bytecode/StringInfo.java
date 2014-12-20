/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class StringInfo extends ConstInfo
/*      */ {
/*      */   static final int tag = 8;
/*      */   int string;
/*      */ 
/*      */   public StringInfo(int str)
/*      */   {
/* 1401 */     this.string = str;
/*      */   }
/*      */ 
/*      */   public StringInfo(DataInputStream in) throws IOException {
/* 1405 */     this.string = in.readUnsignedShort();
/*      */   }
/*      */   public int getTag() {
/* 1408 */     return 8;
/*      */   }
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1411 */     return dest.addStringInfo(src.getUtf8Info(this.string));
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1415 */     out.writeByte(8);
/* 1416 */     out.writeShort(this.string);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1420 */     out.print("String #");
/* 1421 */     out.println(this.string);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.StringInfo
 * JD-Core Version:    0.6.2
 */