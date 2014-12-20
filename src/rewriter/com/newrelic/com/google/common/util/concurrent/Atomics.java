/*    */ package com.newrelic.com.google.common.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicReference;
/*    */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public final class Atomics
/*    */ {
/*    */   public static <V> AtomicReference<V> newReference()
/*    */   {
/* 40 */     return new AtomicReference();
/*    */   }
/*    */ 
/*    */   public static <V> AtomicReference<V> newReference(@Nullable V initialValue)
/*    */   {
/* 50 */     return new AtomicReference(initialValue);
/*    */   }
/*    */ 
/*    */   public static <E> AtomicReferenceArray<E> newReferenceArray(int length)
/*    */   {
/* 60 */     return new AtomicReferenceArray(length);
/*    */   }
/*    */ 
/*    */   public static <E> AtomicReferenceArray<E> newReferenceArray(E[] array)
/*    */   {
/* 71 */     return new AtomicReferenceArray(array);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.Atomics
 * JD-Core Version:    0.6.2
 */