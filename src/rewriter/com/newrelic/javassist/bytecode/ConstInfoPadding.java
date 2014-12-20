/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class ConstInfoPadding extends ConstInfo
/*      */ {
/*      */   public int getTag()
/*      */   {
/* 1155 */     return 0;
/*      */   }
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1158 */     return dest.addConstInfoPadding();
/*      */   }
/*      */   public void write(DataOutputStream out) throws IOException {
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1164 */     out.println("padding");
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ConstInfoPadding
 * JD-Core Version:    0.6.2
 */