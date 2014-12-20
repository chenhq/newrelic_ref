/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ abstract class MemberrefInfo extends ConstInfo
/*      */ {
/*      */   int classIndex;
/*      */   int nameAndTypeIndex;
/*      */ 
/*      */   public MemberrefInfo(int cindex, int ntindex)
/*      */   {
/* 1301 */     this.classIndex = cindex;
/* 1302 */     this.nameAndTypeIndex = ntindex;
/*      */   }
/*      */ 
/*      */   public MemberrefInfo(DataInputStream in) throws IOException {
/* 1306 */     this.classIndex = in.readUnsignedShort();
/* 1307 */     this.nameAndTypeIndex = in.readUnsignedShort();
/*      */   }
/*      */ 
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1311 */     int classIndex2 = src.getItem(this.classIndex).copy(src, dest, map);
/* 1312 */     int ntIndex2 = src.getItem(this.nameAndTypeIndex).copy(src, dest, map);
/* 1313 */     return copy2(dest, classIndex2, ntIndex2);
/*      */   }
/*      */   boolean hashCheck(int a, int b) {
/* 1316 */     return (a == this.classIndex) && (b == this.nameAndTypeIndex);
/*      */   }
/*      */   protected abstract int copy2(ConstPool paramConstPool, int paramInt1, int paramInt2);
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1321 */     out.writeByte(getTag());
/* 1322 */     out.writeShort(this.classIndex);
/* 1323 */     out.writeShort(this.nameAndTypeIndex);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1327 */     out.print(getTagName() + " #");
/* 1328 */     out.print(this.classIndex);
/* 1329 */     out.print(", name&type #");
/* 1330 */     out.println(this.nameAndTypeIndex);
/*      */   }
/*      */ 
/*      */   public abstract String getTagName();
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.MemberrefInfo
 * JD-Core Version:    0.6.2
 */