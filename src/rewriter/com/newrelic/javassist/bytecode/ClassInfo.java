/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ 
/*      */ class ClassInfo extends ConstInfo
/*      */ {
/*      */   static final int tag = 7;
/*      */   int name;
/*      */   int index;
/*      */ 
/*      */   public ClassInfo(int className, int i)
/*      */   {
/* 1174 */     this.name = className;
/* 1175 */     this.index = i;
/*      */   }
/*      */ 
/*      */   public ClassInfo(DataInputStream in, int i) throws IOException {
/* 1179 */     this.name = in.readUnsignedShort();
/* 1180 */     this.index = i;
/*      */   }
/*      */   public int getTag() {
/* 1183 */     return 7;
/*      */   }
/*      */   public String getClassName(ConstPool cp) {
/* 1186 */     return cp.getUtf8Info(this.name);
/*      */   }
/*      */ 
/*      */   public void renameClass(ConstPool cp, String oldName, String newName) {
/* 1190 */     String nameStr = cp.getUtf8Info(this.name);
/* 1191 */     if (nameStr.equals(oldName)) {
/* 1192 */       this.name = cp.addUtf8Info(newName);
/* 1193 */     } else if (nameStr.charAt(0) == '[') {
/* 1194 */       String nameStr2 = Descriptor.rename(nameStr, oldName, newName);
/* 1195 */       if (nameStr != nameStr2)
/* 1196 */         this.name = cp.addUtf8Info(nameStr2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void renameClass(ConstPool cp, Map map) {
/* 1201 */     String oldName = cp.getUtf8Info(this.name);
/* 1202 */     if (oldName.charAt(0) == '[') {
/* 1203 */       String newName = Descriptor.rename(oldName, map);
/* 1204 */       if (oldName != newName)
/* 1205 */         this.name = cp.addUtf8Info(newName);
/*      */     }
/*      */     else {
/* 1208 */       String newName = (String)map.get(oldName);
/* 1209 */       if ((newName != null) && (!newName.equals(oldName)))
/* 1210 */         this.name = cp.addUtf8Info(newName);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int copy(ConstPool src, ConstPool dest, Map map) {
/* 1215 */     String classname = src.getUtf8Info(this.name);
/* 1216 */     if (map != null) {
/* 1217 */       String newname = (String)map.get(classname);
/* 1218 */       if (newname != null) {
/* 1219 */         classname = newname;
/*      */       }
/*      */     }
/* 1222 */     return dest.addClassInfo(classname);
/*      */   }
/*      */ 
/*      */   public void write(DataOutputStream out) throws IOException {
/* 1226 */     out.writeByte(7);
/* 1227 */     out.writeShort(this.name);
/*      */   }
/*      */ 
/*      */   public void print(PrintWriter out) {
/* 1231 */     out.print("Class #");
/* 1232 */     out.println(this.name);
/*      */   }
/*      */ 
/*      */   void makeHashtable(ConstPool cp) {
/* 1236 */     String name = Descriptor.toJavaName(getClassName(cp));
/* 1237 */     cp.classes.put(name, this);
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ClassInfo
 * JD-Core Version:    0.6.2
 */