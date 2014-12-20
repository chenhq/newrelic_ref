/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingQueue<E> extends ForwardingCollection<E>
/*     */   implements Queue<E>
/*     */ {
/*     */   protected abstract Queue<E> delegate();
/*     */ 
/*     */   public boolean offer(E o)
/*     */   {
/*  55 */     return delegate().offer(o);
/*     */   }
/*     */ 
/*     */   public E poll()
/*     */   {
/*  60 */     return delegate().poll();
/*     */   }
/*     */ 
/*     */   public E remove()
/*     */   {
/*  65 */     return delegate().remove();
/*     */   }
/*     */ 
/*     */   public E peek()
/*     */   {
/*  70 */     return delegate().peek();
/*     */   }
/*     */ 
/*     */   public E element()
/*     */   {
/*  75 */     return delegate().element();
/*     */   }
/*     */ 
/*     */   protected boolean standardOffer(E e)
/*     */   {
/*     */     try
/*     */     {
/*  87 */       return add(e); } catch (IllegalStateException caught) {
/*     */     }
/*  89 */     return false;
/*     */   }
/*     */ 
/*     */   protected E standardPeek()
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return element(); } catch (NoSuchElementException caught) {
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */   protected E standardPoll()
/*     */   {
/*     */     try
/*     */     {
/* 117 */       return remove(); } catch (NoSuchElementException caught) {
/*     */     }
/* 119 */     return null;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingQueue
 * JD-Core Version:    0.6.2
 */