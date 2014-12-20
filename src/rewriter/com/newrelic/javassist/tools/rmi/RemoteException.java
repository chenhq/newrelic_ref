/*    */ package com.newrelic.javassist.tools.rmi;
/*    */ 
/*    */ public class RemoteException extends RuntimeException
/*    */ {
/*    */   public RemoteException(String msg)
/*    */   {
/* 24 */     super(msg);
/*    */   }
/*    */ 
/*    */   public RemoteException(Exception e) {
/* 28 */     super("by " + e.toString());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.rmi.RemoteException
 * JD-Core Version:    0.6.2
 */