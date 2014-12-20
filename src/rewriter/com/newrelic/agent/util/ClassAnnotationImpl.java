/*    */ package com.newrelic.agent.util;
/*    */ 
/*    */ class ClassAnnotationImpl extends AnnotationImpl
/*    */   implements ClassAnnotation
/*    */ {
/*    */   private final String className;
/*    */ 
/*    */   public ClassAnnotationImpl(String className, String name)
/*    */   {
/*  8 */     super(name);
/*  9 */     this.className = className;
/*    */   }
/*    */ 
/*    */   public String getClassName() {
/* 13 */     return this.className;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.agent.util.ClassAnnotationImpl
 * JD-Core Version:    0.6.2
 */