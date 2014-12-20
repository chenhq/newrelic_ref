/*    */ package com.newrelic.org.apache.commons.io.input;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class AutoCloseInputStream extends ProxyInputStream
/*    */ {
/*    */   public AutoCloseInputStream(InputStream in)
/*    */   {
/* 45 */     super(in);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 63 */     this.in.close();
/* 64 */     this.in = new ClosedInputStream();
/*    */   }
/*    */ 
/*    */   protected void afterRead(int n)
/*    */     throws IOException
/*    */   {
/* 76 */     if (n == -1)
/* 77 */       close();
/*    */   }
/*    */ 
/*    */   protected void finalize()
/*    */     throws Throwable
/*    */   {
/* 89 */     close();
/* 90 */     super.finalize();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.AutoCloseInputStream
 * JD-Core Version:    0.6.2
 */