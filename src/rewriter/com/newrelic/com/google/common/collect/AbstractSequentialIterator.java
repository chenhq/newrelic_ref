/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.NoSuchElementException;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T>
/*    */ {
/*    */   private T nextOrNull;
/*    */ 
/*    */   protected AbstractSequentialIterator(@Nullable T firstOrNull)
/*    */   {
/* 53 */     this.nextOrNull = firstOrNull;
/*    */   }
/*    */ 
/*    */   protected abstract T computeNext(T paramT);
/*    */ 
/*    */   public final boolean hasNext()
/*    */   {
/* 66 */     return this.nextOrNull != null;
/*    */   }
/*    */ 
/*    */   public final T next()
/*    */   {
/* 71 */     if (!hasNext())
/* 72 */       throw new NoSuchElementException();
/*    */     try
/*    */     {
/* 75 */       return this.nextOrNull;
/*    */     } finally {
/* 77 */       this.nextOrNull = computeNext(this.nextOrNull);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.AbstractSequentialIterator
 * JD-Core Version:    0.6.2
 */