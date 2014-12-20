/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.IOException;
/*      */ 
/*      */ class MethodrefInfo extends MemberrefInfo
/*      */ {
/*      */   static final int tag = 10;
/*      */ 
/*      */   public MethodrefInfo(int cindex, int ntindex)
/*      */   {
/* 1360 */     super(cindex, ntindex);
/*      */   }
/*      */ 
/*      */   public MethodrefInfo(DataInputStream in) throws IOException {
/* 1364 */     super(in);
/*      */   }
/*      */   public int getTag() {
/* 1367 */     return 10;
/*      */   }
/* 1369 */   public String getTagName() { return "Method"; }
/*      */ 
/*      */   protected int copy2(ConstPool dest, int cindex, int ntindex) {
/* 1372 */     return dest.addMethodrefInfo(cindex, ntindex);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.MethodrefInfo
 * JD-Core Version:    0.6.2
 */