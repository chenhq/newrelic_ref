/*    */ package com.newrelic.javassist.tools.rmi;
/*    */ 
/*    */ public class Sample
/*    */ {
/*    */   private ObjectImporter importer;
/*    */   private int objectId;
/*    */ 
/*    */   public Object forward(Object[] args, int identifier)
/*    */   {
/* 28 */     return this.importer.call(this.objectId, identifier, args);
/*    */   }
/*    */ 
/*    */   public static Object forwardStatic(Object[] args, int identifier)
/*    */     throws RemoteException
/*    */   {
/* 34 */     throw new RemoteException("cannot call a static method.");
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.rmi.Sample
 * JD-Core Version:    0.6.2
 */