/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.concurrent.BlockingDeque;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public abstract class ForwardingBlockingDeque<E> extends ForwardingDeque<E>
/*     */   implements BlockingDeque<E>
/*     */ {
/*     */   protected abstract BlockingDeque<E> delegate();
/*     */ 
/*     */   public int remainingCapacity()
/*     */   {
/*  51 */     return delegate().remainingCapacity();
/*     */   }
/*     */ 
/*     */   public void putFirst(E e) throws InterruptedException
/*     */   {
/*  56 */     delegate().putFirst(e);
/*     */   }
/*     */ 
/*     */   public void putLast(E e) throws InterruptedException
/*     */   {
/*  61 */     delegate().putLast(e);
/*     */   }
/*     */ 
/*     */   public boolean offerFirst(E e, long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  66 */     return delegate().offerFirst(e, timeout, unit);
/*     */   }
/*     */ 
/*     */   public boolean offerLast(E e, long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  71 */     return delegate().offerLast(e, timeout, unit);
/*     */   }
/*     */ 
/*     */   public E takeFirst() throws InterruptedException
/*     */   {
/*  76 */     return delegate().takeFirst();
/*     */   }
/*     */ 
/*     */   public E takeLast() throws InterruptedException
/*     */   {
/*  81 */     return delegate().takeLast();
/*     */   }
/*     */ 
/*     */   public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  86 */     return delegate().pollFirst(timeout, unit);
/*     */   }
/*     */ 
/*     */   public E pollLast(long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/*  91 */     return delegate().pollLast(timeout, unit);
/*     */   }
/*     */ 
/*     */   public void put(E e) throws InterruptedException
/*     */   {
/*  96 */     delegate().put(e);
/*     */   }
/*     */ 
/*     */   public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/* 101 */     return delegate().offer(e, timeout, unit);
/*     */   }
/*     */ 
/*     */   public E take() throws InterruptedException
/*     */   {
/* 106 */     return delegate().take();
/*     */   }
/*     */ 
/*     */   public E poll(long timeout, TimeUnit unit) throws InterruptedException
/*     */   {
/* 111 */     return delegate().poll(timeout, unit);
/*     */   }
/*     */ 
/*     */   public int drainTo(Collection<? super E> c)
/*     */   {
/* 116 */     return delegate().drainTo(c);
/*     */   }
/*     */ 
/*     */   public int drainTo(Collection<? super E> c, int maxElements)
/*     */   {
/* 121 */     return delegate().drainTo(c, maxElements);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingBlockingDeque
 * JD-Core Version:    0.6.2
 */