/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.IOException;
/*      */ 
/*      */ class FieldrefInfo extends MemberrefInfo
/*      */ {
/*      */   static final int tag = 9;
/*      */ 
/*      */   public FieldrefInfo(int cindex, int ntindex)
/*      */   {
/* 1340 */     super(cindex, ntindex);
/*      */   }
/*      */ 
/*      */   public FieldrefInfo(DataInputStream in) throws IOException {
/* 1344 */     super(in);
/*      */   }
/*      */   public int getTag() {
/* 1347 */     return 9;
/*      */   }
/* 1349 */   public String getTagName() { return "Field"; }
/*      */ 
/*      */   protected int copy2(ConstPool dest, int cindex, int ntindex) {
/* 1352 */     return dest.addFieldrefInfo(cindex, ntindex);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.FieldrefInfo
 * JD-Core Version:    0.6.2
 */