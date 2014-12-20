/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.IOException;
/*      */ 
/*      */ class InterfaceMethodrefInfo extends MemberrefInfo
/*      */ {
/*      */   static final int tag = 11;
/*      */ 
/*      */   public InterfaceMethodrefInfo(int cindex, int ntindex)
/*      */   {
/* 1380 */     super(cindex, ntindex);
/*      */   }
/*      */ 
/*      */   public InterfaceMethodrefInfo(DataInputStream in) throws IOException {
/* 1384 */     super(in);
/*      */   }
/*      */   public int getTag() {
/* 1387 */     return 11;
/*      */   }
/* 1389 */   public String getTagName() { return "Interface"; }
/*      */ 
/*      */   protected int copy2(ConstPool dest, int cindex, int ntindex) {
/* 1392 */     return dest.addInterfaceMethodrefInfo(cindex, ntindex);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.InterfaceMethodrefInfo
 * JD-Core Version:    0.6.2
 */