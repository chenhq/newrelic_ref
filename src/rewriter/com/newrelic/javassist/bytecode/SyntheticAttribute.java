/*    */ package com.newrelic.javassist.bytecode;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SyntheticAttribute extends AttributeInfo
/*    */ {
/*    */   public static final String tag = "Synthetic";
/*    */ 
/*    */   SyntheticAttribute(ConstPool cp, int n, DataInputStream in)
/*    */     throws IOException
/*    */   {
/* 34 */     super(cp, n, in);
/*    */   }
/*    */ 
/*    */   public SyntheticAttribute(ConstPool cp)
/*    */   {
/* 43 */     super(cp, "Synthetic", new byte[0]);
/*    */   }
/*    */ 
/*    */   public AttributeInfo copy(ConstPool newCp, Map classnames)
/*    */   {
/* 53 */     return new SyntheticAttribute(newCp);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.bytecode.SyntheticAttribute
 * JD-Core Version:    0.6.2
 */