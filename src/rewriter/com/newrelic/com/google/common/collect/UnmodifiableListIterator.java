/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E>
/*    */   implements ListIterator<E>
/*    */ {
/*    */   @Deprecated
/*    */   public final void add(E e)
/*    */   {
/* 43 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   @Deprecated
/*    */   public final void set(E e)
/*    */   {
/* 53 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.UnmodifiableListIterator
 * JD-Core Version:    0.6.2
 */