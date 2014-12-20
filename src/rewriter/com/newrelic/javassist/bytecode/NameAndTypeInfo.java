/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ class NameAndTypeInfo extends ConstInfo
/*      */ {
/*      */   static final int tag = 12;
/*      */   int memberName;
/*      */   int typeDescriptor;
/*      */ 
/*      */   public NameAndTypeInfo(int name, int type)
/*      */   {
/* 1247 */     this.memberName = name;
/* 1248 */     this.typeDescriptor = type;
/*      */   }
/*      */ 
/*      */   public NameAndTypeInfo(DataInputStream in) throws IOException {
/* 1252 */     this.memberName = in.readUnsignedShort();
/* 1253 */     this.typeDescriptor = in.readUnsignedShort();
/*      */   }
/*      */   boolean hashCheck(int a, int b) {
/* 1256 */     return (a == this.memberName) && (b == this.typeDescriptor);
/*      */   }
/* 1258 */   public int getTag() { return 12; }
/*      */ 
/*      */   public void renameClass(ConstPool cp, String oldName, String newName) {
/* 1261 */     String type = cp.getUtf8Info(this.typeDescriptor);
/* 1262 */     String type2 = Descriptor.rename(type, oldName, newName);
/* 1263 */     if (type != type2)
/* 1264 */       this.typeDescriptor = cp.addUtf8Info(type2);
/*      */   }
/*      */ 
/*      */   public void renameClass(ConstPool cp, Map map) {
/* 1268 */     String type = cp.getUtf8Info(this.typeDescriptor);
/* 1269 */     String type2 = Descriptor.rename(type, map);
/* 1270 */     if (type != type2)
/* 1271 */       this.typeDescriptor = cp.addUtf8Info(type2);
/*      */   }
/*      */ 
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1275 */     String mname = src.getUtf8Info(this.memberName);
/* 1276 */     String tdesc = src.getUtf8Info(this.typeDescriptor);
/* 1277 */     tdesc = Descriptor.rename(tdesc, map);
/* 1278 */     return dest.addNameAndTypeInfo(dest.addUtf8Info(mname), dest.addUtf8Info(tdesc));
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException
/*      */   {
/* 1283 */     out.writeByte(12);
/* 1284 */     out.writeShort(this.memberName);
/* 1285 */     out.writeShort(this.typeDescriptor);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1289 */     out.print("NameAndType #");
/* 1290 */     out.print(this.memberName);
/* 1291 */     out.print(", type #");
/* 1292 */     out.println(this.typeDescriptor);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.NameAndTypeInfo
 * JD-Core Version:    0.6.2
 */