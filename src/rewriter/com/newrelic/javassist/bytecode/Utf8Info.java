/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class Utf8Info extends ConstInfo
/*      */ {
/*      */   static final int tag = 1;
/*      */   String string;
/*      */   int index;
/*      */ 
/*      */   public Utf8Info(String utf8, int i)
/*      */   {
/* 1547 */     this.string = utf8;
/* 1548 */     this.index = i;
/*      */   }
/*      */ 
/*      */   public Utf8Info(DataInputStream in, int i) throws IOException {
/* 1552 */     this.string = in.readUTF();
/* 1553 */     this.index = i;
/*      */   }
/*      */   public int getTag() {
/* 1556 */     return 1;
/*      */   }
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1559 */     return dest.addUtf8Info(this.string);
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1563 */     out.writeByte(1);
/* 1564 */     out.writeUTF(this.string);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1568 */     out.print("UTF8 \"");
/* 1569 */     out.print(this.string);
/* 1570 */     out.println("\"");
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.Utf8Info
 * JD-Core Version:    0.6.2
 */