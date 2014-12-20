/*    */ package com.newrelic.com.google.common.cache;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Supplier;
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ final class LongAddables
/*    */ {
/* 52 */   private static final Supplier<LongAddable> SUPPLIER = supplier;
/*    */ 
/*    */   public static LongAddable create()
/*    */   {
/* 56 */     return (LongAddable)SUPPLIER.get();
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     Supplier supplier;
/*    */     try
/*    */     {
/* 37 */       new LongAdder();
/* 38 */       supplier = new Supplier()
/*    */       {
/*    */         public LongAddable get() {
/* 41 */           return new LongAdder();
/*    */         } } ;
/*    */     }
/*    */     catch (Throwable t) {
/* 45 */       supplier = new Supplier()
/*    */       {
/*    */         public LongAddable get() {
/* 48 */           return new LongAddables.PureJavaLongAddable(null);
/*    */         }
/*    */       };
/*    */     }
/*    */   }
/*    */ 
/*    */   private static final class PureJavaLongAddable extends AtomicLong
/*    */     implements LongAddable
/*    */   {
/*    */     public void increment()
/*    */     {
/* 62 */       getAndIncrement();
/*    */     }
/*    */ 
/*    */     public void add(long x)
/*    */     {
/* 67 */       getAndAdd(x);
/*    */     }
/*    */ 
/*    */     public long sum()
/*    */     {
/* 72 */       return get();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.LongAddables
 * JD-Core Version:    0.6.2
 */