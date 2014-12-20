/*    */ package com.newrelic.javassist.tools.rmi;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class RemoteRef
/*    */   implements Serializable
/*    */ {
/*    */   public int oid;
/*    */   public String classname;
/*    */ 
/*    */   public RemoteRef(int i)
/*    */   {
/* 27 */     this.oid = i;
/* 28 */     this.classname = null;
/*    */   }
/*    */ 
/*    */   public RemoteRef(int i, String name) {
/* 32 */     this.oid = i;
/* 33 */     this.classname = name;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.tools.rmi.RemoteRef
 * JD-Core Version:    0.6.2
 */