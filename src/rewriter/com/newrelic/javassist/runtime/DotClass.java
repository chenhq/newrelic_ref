/*    */ package com.newrelic.javassist.runtime;
/*    */ 
/*    */ public class DotClass
/*    */ {
/*    */   public static NoClassDefFoundError fail(ClassNotFoundException e)
/*    */   {
/* 26 */     return new NoClassDefFoundError(e.getMessage());
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.runtime.DotClass
 * JD-Core Version:    0.6.2
 */