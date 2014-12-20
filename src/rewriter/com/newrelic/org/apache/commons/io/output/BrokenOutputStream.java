/*    */ package com.newrelic.org.apache.commons.io.output;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class BrokenOutputStream extends OutputStream
/*    */ {
/*    */   private final IOException exception;
/*    */ 
/*    */   public BrokenOutputStream(IOException exception)
/*    */   {
/* 44 */     this.exception = exception;
/*    */   }
/*    */ 
/*    */   public BrokenOutputStream()
/*    */   {
/* 51 */     this(new IOException("Broken output stream"));
/*    */   }
/*    */ 
/*    */   public void write(int b)
/*    */     throws IOException
/*    */   {
/* 62 */     throw this.exception;
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/* 72 */     throw this.exception;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 82 */     throw this.exception;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.BrokenOutputStream
 * JD-Core Version:    0.6.2
 */