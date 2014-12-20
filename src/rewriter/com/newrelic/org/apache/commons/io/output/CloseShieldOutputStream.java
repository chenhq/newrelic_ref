/*    */ package com.newrelic.org.apache.commons.io.output;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class CloseShieldOutputStream extends ProxyOutputStream
/*    */ {
/*    */   public CloseShieldOutputStream(OutputStream out)
/*    */   {
/* 40 */     super(out);
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/* 50 */     this.out = new ClosedOutputStream();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.CloseShieldOutputStream
 * JD-Core Version:    0.6.2
 */