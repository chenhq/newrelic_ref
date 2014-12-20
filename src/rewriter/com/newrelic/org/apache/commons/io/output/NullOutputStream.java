/*    */ package com.newrelic.org.apache.commons.io.output;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class NullOutputStream extends OutputStream
/*    */ {
/* 35 */   public static final NullOutputStream NULL_OUTPUT_STREAM = new NullOutputStream();
/*    */ 
/*    */   public void write(byte[] b, int off, int len)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void write(int b)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void write(byte[] b)
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.NullOutputStream
 * JD-Core Version:    0.6.2
 */