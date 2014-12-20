/*      */ package com.newrelic.javassist.bytecode;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Map;
/*      */ 
/*      */ abstract class ConstInfo
/*      */ {
/*      */   public abstract int getTag();
/*      */ 
/*      */   public String getClassName(ConstPool cp)
/*      */   {
/* 1131 */     return null;
/*      */   }
/*      */   public void renameClass(ConstPool cp, String oldName, String newName) {  } 
/*      */   public void renameClass(ConstPool cp, Map classnames) {  } 
/*      */   public abstract int copy(ConstPool paramConstPool1, ConstPool paramConstPool2, Map paramMap);
/*      */ 
/*      */   public abstract void write(DataOutputStream paramDataOutputStream) throws IOException;
/*      */ 
/*      */   public abstract void print(PrintWriter paramPrintWriter);
/*      */ 
/*      */   void makeHashtable(ConstPool cp) {  } 
/* 1142 */   boolean hashCheck(int a, int b) { return false; }
/*      */ 
/*      */   public String toString() {
/* 1145 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 1146 */     PrintWriter out = new PrintWriter(bout);
/* 1147 */     print(out);
/* 1148 */     return bout.toString();
/*      */   }
/*      */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ConstInfo
 * JD-Core Version:    0.6.2
 */