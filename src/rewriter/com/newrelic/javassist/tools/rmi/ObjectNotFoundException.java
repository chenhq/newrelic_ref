/*    */ package com.newrelic.javassist.tools.rmi;
/*    */ 
/*    */ public class ObjectNotFoundException extends Exception
/*    */ {
/*    */   public ObjectNotFoundException(String name)
/*    */   {
/* 20 */     super(name + " is not exported");
/*    */   }
/*    */ 
/*    */   public ObjectNotFoundException(String name, Exception e) {
/* 24 */     super(name + " because of " + e.toString());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.rmi.ObjectNotFoundException
 * JD-Core Version:    0.6.2
 */