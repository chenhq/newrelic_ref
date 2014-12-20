/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ import com.newrelic.com.google.common.annotations.Beta;
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Collection;
/*     */ import java.util.Deque;
/*     */ import java.util.PriorityQueue;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.LinkedBlockingDeque;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.PriorityBlockingQueue;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public final class Queues
/*     */ {
/*     */   public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity)
/*     */   {
/*  51 */     return new ArrayBlockingQueue(capacity);
/*     */   }
/*     */ 
/*     */   public static <E> ArrayDeque<E> newArrayDeque()
/*     */   {
/*  62 */     return new ArrayDeque();
/*     */   }
/*     */ 
/*     */   public static <E> ArrayDeque<E> newArrayDeque(Iterable<? extends E> elements)
/*     */   {
/*  72 */     if ((elements instanceof Collection)) {
/*  73 */       return new ArrayDeque(Collections2.cast(elements));
/*     */     }
/*  75 */     ArrayDeque deque = new ArrayDeque();
/*  76 */     Iterables.addAll(deque, elements);
/*  77 */     return deque;
/*     */   }
/*     */ 
/*     */   public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue()
/*     */   {
/*  86 */     return new ConcurrentLinkedQueue();
/*     */   }
/*     */ 
/*     */   public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(Iterable<? extends E> elements)
/*     */   {
/*  95 */     if ((elements instanceof Collection)) {
/*  96 */       return new ConcurrentLinkedQueue(Collections2.cast(elements));
/*     */     }
/*  98 */     ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
/*  99 */     Iterables.addAll(queue, elements);
/* 100 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque()
/*     */   {
/* 111 */     return new LinkedBlockingDeque();
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity)
/*     */   {
/* 121 */     return new LinkedBlockingDeque(capacity);
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(Iterable<? extends E> elements)
/*     */   {
/* 132 */     if ((elements instanceof Collection)) {
/* 133 */       return new LinkedBlockingDeque(Collections2.cast(elements));
/*     */     }
/* 135 */     LinkedBlockingDeque deque = new LinkedBlockingDeque();
/* 136 */     Iterables.addAll(deque, elements);
/* 137 */     return deque;
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue()
/*     */   {
/* 146 */     return new LinkedBlockingQueue();
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int capacity)
/*     */   {
/* 155 */     return new LinkedBlockingQueue(capacity);
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Iterable<? extends E> elements)
/*     */   {
/* 167 */     if ((elements instanceof Collection)) {
/* 168 */       return new LinkedBlockingQueue(Collections2.cast(elements));
/*     */     }
/* 170 */     LinkedBlockingQueue queue = new LinkedBlockingQueue();
/* 171 */     Iterables.addAll(queue, elements);
/* 172 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue()
/*     */   {
/* 186 */     return new PriorityBlockingQueue();
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue(Iterable<? extends E> elements)
/*     */   {
/* 199 */     if ((elements instanceof Collection)) {
/* 200 */       return new PriorityBlockingQueue(Collections2.cast(elements));
/*     */     }
/* 202 */     PriorityBlockingQueue queue = new PriorityBlockingQueue();
/* 203 */     Iterables.addAll(queue, elements);
/* 204 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable> PriorityQueue<E> newPriorityQueue()
/*     */   {
/* 216 */     return new PriorityQueue();
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable> PriorityQueue<E> newPriorityQueue(Iterable<? extends E> elements)
/*     */   {
/* 229 */     if ((elements instanceof Collection)) {
/* 230 */       return new PriorityQueue(Collections2.cast(elements));
/*     */     }
/* 232 */     PriorityQueue queue = new PriorityQueue();
/* 233 */     Iterables.addAll(queue, elements);
/* 234 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E> SynchronousQueue<E> newSynchronousQueue()
/*     */   {
/* 243 */     return new SynchronousQueue();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> int drain(BlockingQueue<E> q, Collection<? super E> buffer, int numElements, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 262 */     Preconditions.checkNotNull(buffer);
/*     */ 
/* 268 */     long deadline = System.nanoTime() + unit.toNanos(timeout);
/* 269 */     int added = 0;
/* 270 */     while (added < numElements)
/*     */     {
/* 273 */       added += q.drainTo(buffer, numElements - added);
/* 274 */       if (added < numElements) {
/* 275 */         Object e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
/* 276 */         if (e == null) {
/*     */           break;
/*     */         }
/* 279 */         buffer.add(e);
/* 280 */         added++;
/*     */       }
/*     */     }
/* 283 */     return added;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> int drainUninterruptibly(BlockingQueue<E> q, Collection<? super E> buffer, int numElements, long timeout, TimeUnit unit)
/*     */   {
/* 302 */     Preconditions.checkNotNull(buffer);
/* 303 */     long deadline = System.nanoTime() + unit.toNanos(timeout);
/* 304 */     int added = 0;
/* 305 */     boolean interrupted = false;
/*     */     try {
/* 307 */       while (added < numElements)
/*     */       {
/* 310 */         added += q.drainTo(buffer, numElements - added);
/* 311 */         if (added < numElements) {
/*     */           Object e;
/*     */           while (true) {
/*     */             try {
/* 315 */               e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
/*     */             }
/*     */             catch (InterruptedException ex) {
/* 318 */               interrupted = true;
/*     */             }
/*     */           }
/* 321 */           if (e == null) {
/*     */             break;
/*     */           }
/* 324 */           buffer.add(e);
/* 325 */           added++;
/*     */         }
/*     */       }
/*     */     } finally {
/* 329 */       if (interrupted) {
/* 330 */         Thread.currentThread().interrupt();
/*     */       }
/*     */     }
/* 333 */     return added;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> Queue<E> synchronizedQueue(Queue<E> queue)
/*     */   {
/* 365 */     return Synchronized.queue(queue, null);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> Deque<E> synchronizedDeque(Deque<E> deque)
/*     */   {
/* 397 */     return Synchronized.deque(deque, null);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.Queues
 * JD-Core Version:    0.6.2
 */