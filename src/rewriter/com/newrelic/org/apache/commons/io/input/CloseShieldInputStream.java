/*    */ package com.newrelic.org.apache.commons.io.input;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class CloseShieldInputStream extends ProxyInputStream
/*    */ {
/*    */   public CloseShieldInputStream(InputStream in)
/*    */   {
/* 40 */     super(in);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 50 */     this.in = new ClosedInputStream();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.input.CloseShieldInputStream
 * JD-Core Version:    0.6.2
 */