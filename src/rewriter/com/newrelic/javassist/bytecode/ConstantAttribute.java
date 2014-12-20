/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ConstantAttribute extends AttributeInfo
/*    */ {
/*    */   public static final String tag = "ConstantValue";
/*    */ 
/*    */   ConstantAttribute(ConstPool cp, int n, DataInputStream in)
/*    */     throws IOException
/*    */   {
/* 34 */     super(cp, n, in);
/*    */   }
/*    */ 
/*    */   public ConstantAttribute(ConstPool cp, int index)
/*    */   {
/* 45 */     super(cp, "ConstantValue");
/* 46 */     byte[] bvalue = new byte[2];
/* 47 */     bvalue[0] = ((byte)(index >>> 8));
/* 48 */     bvalue[1] = ((byte)index);
/* 49 */     set(bvalue);
/*    */   }
/*    */ 
/*    */   public int getConstantValue()
/*    */   {
/* 56 */     return ByteArray.readU16bit(get(), 0);
/*    */   }
/*    */ 
/*    */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*    */   {
/* 68 */     int index = getConstPool().copy(getConstantValue(), newCp, classnames);
/*    */ 
/* 70 */     return new ConstantAttribute(newCp, index);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.ConstantAttribute
 * JD-Core Version:    0.6.2
 */