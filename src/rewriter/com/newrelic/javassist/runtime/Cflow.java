/*    */ package com.newrelic.javassist.runtime;
/*    */ 
/*    */ public class Cflow extends ThreadLocal
/*    */ {
/*    */   protected synchronized Object initialValue()
/*    */   {
/* 35 */     return new Depth();
/*    */   }
/*    */ 
/*    */   public void enter()
/*    */   {
/* 41 */     ((Depth)get()).inc();
/*    */   }
/*    */ 
/*    */   public void exit()
/*    */   {
/* 46 */     ((Depth)get()).dec();
/*    */   }
/*    */ 
/*    */   public int value()
/*    */   {
/* 51 */     return ((Depth)get()).get();
/*    */   }
/*    */ 
/*    */   private static class Depth
/*    */   {
/* 28 */     private int depth = 0;
/*    */ 
/* 29 */     int get() { return this.depth; } 
/* 30 */     void inc() { this.depth += 1; } 
/* 31 */     void dec() { this.depth -= 1; }
/*    */ 
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.javassist.runtime.Cflow
 * JD-Core Version:    0.6.2
 */