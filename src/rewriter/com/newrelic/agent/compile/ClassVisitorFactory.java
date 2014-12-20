/*    */ package com.newrelic.agent.compile;
/*    */ 
/*    */ import com.newrelic.objectweb.asm.ClassAdapter;
/*    */ import com.newrelic.objectweb.asm.ClassVisitor;
/*    */ 
/*    */ abstract class ClassVisitorFactory
/*    */ {
/*    */   private final boolean retransformOkay;
/*    */ 
/*    */   public ClassVisitorFactory(boolean retransformOkay)
/*    */   {
/* 15 */     this.retransformOkay = retransformOkay;
/*    */   }
/*    */ 
/*    */   public boolean isRetransformOkay() {
/* 19 */     return this.retransformOkay;
/*    */   }
/*    */ 
/*    */   abstract ClassAdapter create(ClassVisitor paramClassVisitor);
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.compile.ClassVisitorFactory
 * JD-Core Version:    0.6.2
 */