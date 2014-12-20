/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class LocalVariableTypeAttribute extends LocalVariableAttribute
/*    */ {
/*    */   public static final String tag = "LocalVariableTypeTable";
/*    */ 
/*    */   public LocalVariableTypeAttribute(ConstPool cp)
/*    */   {
/* 37 */     super(cp, "LocalVariableTypeTable", new byte[2]);
/* 38 */     ByteArray.write16bit(0, this.info, 0);
/*    */   }
/*    */ 
/*    */   LocalVariableTypeAttribute(ConstPool cp, int n, DataInputStream in)
/*    */     throws IOException
/*    */   {
/* 44 */     super(cp, n, in);
/*    */   }
/*    */ 
/*    */   private LocalVariableTypeAttribute(ConstPool cp, byte[] dest) {
/* 48 */     super(cp, "LocalVariableTypeTable", dest);
/*    */   }
/*    */ 
/*    */   String renameEntry(String desc, String oldname, String newname) {
/* 52 */     return SignatureAttribute.renameClass(desc, oldname, newname);
/*    */   }
/*    */ 
/*    */   String renameEntry(String desc, Map classnames) {
/* 56 */     return SignatureAttribute.renameClass(desc, classnames);
/*    */   }
/*    */ 
/*    */   LocalVariableAttribute makeThisAttr(ConstPool cp, byte[] dest) {
/* 60 */     return new LocalVariableTypeAttribute(cp, dest);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.LocalVariableTypeAttribute
 * JD-Core Version:    0.6.2
 */