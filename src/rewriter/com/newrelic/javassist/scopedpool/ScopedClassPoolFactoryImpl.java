/*    */ package com.newrelic.javassist.scopedpool;
/*    */ 
/*    */ import com.newrelic.javassist.ClassPool;
/*    */ 
/*    */ public class ScopedClassPoolFactoryImpl
/*    */   implements ScopedClassPoolFactory
/*    */ {
/*    */   public ScopedClassPool create(ClassLoader cl, ClassPool src, ScopedClassPoolRepository repository)
/*    */   {
/* 32 */     return new ScopedClassPool(cl, src, repository, false);
/*    */   }
/*    */ 
/*    */   public ScopedClassPool create(ClassPool src, ScopedClassPoolRepository repository)
/*    */   {
/* 40 */     return new ScopedClassPool(null, src, repository, true);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.scopedpool.ScopedClassPoolFactoryImpl
 * JD-Core Version:    0.6.2
 */