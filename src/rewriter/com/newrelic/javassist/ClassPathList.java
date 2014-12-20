/*    */ package com.newrelic.javassist;
/*    */ 
/*    */ final class ClassPathList
/*    */ {
/*    */   ClassPathList next;
/*    */   ClassPath path;
/*    */ 
/*    */   ClassPathList(ClassPath p, ClassPathList n)
/*    */   {
/* 29 */     this.next = n;
/* 30 */     this.path = p;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.ClassPathList
 * JD-Core Version:    0.6.2
 */