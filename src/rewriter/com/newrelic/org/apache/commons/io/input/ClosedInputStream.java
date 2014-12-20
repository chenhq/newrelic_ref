/*    */ package com.newrelic.org.apache.commons.io.input;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class ClosedInputStream extends InputStream
/*    */ {
/* 37 */   public static final ClosedInputStream CLOSED_INPUT_STREAM = new ClosedInputStream();
/*    */ 
/*    */   public int read()
/*    */   {
/* 46 */     return -1;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.ClosedInputStream
 * JD-Core Version:    0.6.2
 */