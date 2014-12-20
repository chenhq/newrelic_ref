/*    */ package com.newrelic.org.apache.commons.io.output;
/*    */ 
/*    */ import java.io.Writer;
/*    */ 
/*    */ public class NullWriter extends Writer
/*    */ {
/* 34 */   public static final NullWriter NULL_WRITER = new NullWriter();
/*    */ 
/*    */   public Writer append(char c)
/*    */   {
/* 51 */     return this;
/*    */   }
/*    */ 
/*    */   public Writer append(CharSequence csq, int start, int end)
/*    */   {
/* 65 */     return this;
/*    */   }
/*    */ 
/*    */   public Writer append(CharSequence csq)
/*    */   {
/* 77 */     return this;
/*    */   }
/*    */ 
/*    */   public void write(int idx)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void write(char[] chr)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void write(char[] chr, int st, int end)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void write(String str)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void write(String str, int st, int end)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.NullWriter
 * JD-Core Version:    0.6.2
 */