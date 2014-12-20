/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SourceFileAttribute extends AttributeInfo
/*    */ {
/*    */   public static final String tag = "SourceFile";
/*    */ 
/*    */   SourceFileAttribute(ConstPool cp, int n, DataInputStream in)
/*    */     throws IOException
/*    */   {
/* 34 */     super(cp, n, in);
/*    */   }
/*    */ 
/*    */   public SourceFileAttribute(ConstPool cp, String filename)
/*    */   {
/* 44 */     super(cp, "SourceFile");
/* 45 */     int index = cp.addUtf8Info(filename);
/* 46 */     byte[] bvalue = new byte[2];
/* 47 */     bvalue[0] = ((byte)(index >>> 8));
/* 48 */     bvalue[1] = ((byte)index);
/* 49 */     set(bvalue);
/*    */   }
/*    */ 
/*    */   public String getFileName()
/*    */   {
/* 56 */     return getConstPool().getUtf8Info(ByteArray.readU16bit(get(), 0));
/*    */   }
/*    */ 
/*    */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*    */   {
/* 68 */     return new SourceFileAttribute(newCp, getFileName());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.SourceFileAttribute
 * JD-Core Version:    0.6.2
 */