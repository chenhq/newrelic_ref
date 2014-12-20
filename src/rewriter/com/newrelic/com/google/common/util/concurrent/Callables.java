/*     */ package com.newrelic.com.google.common.util.concurrent;
/*     */ 
/*     */ import com.newrelic.com.google.common.base.Preconditions;
/*     */ import com.newrelic.com.google.common.base.Supplier;
/*     */ import java.util.concurrent.Callable;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public final class Callables
/*     */ {
/*     */   public static <T> Callable<T> returning(@Nullable T value)
/*     */   {
/*  41 */     return new Callable() {
/*     */       public T call() {
/*  43 */         return this.val$value;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static <T> Callable<T> threadRenaming(final Callable<T> callable, Supplier<String> nameSupplier)
/*     */   {
/*  59 */     Preconditions.checkNotNull(nameSupplier);
/*  60 */     Preconditions.checkNotNull(callable);
/*  61 */     return new Callable() {
/*     */       public T call() throws Exception {
/*  63 */         Thread currentThread = Thread.currentThread();
/*  64 */         String oldName = currentThread.getName();
/*  65 */         boolean restoreName = Callables.trySetName((String)this.val$nameSupplier.get(), currentThread);
/*     */         try {
/*  67 */           return callable.call();
/*     */         } finally {
/*  69 */           if (restoreName)
/*  70 */             Callables.trySetName(oldName, currentThread);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static Runnable threadRenaming(final Runnable task, Supplier<String> nameSupplier)
/*     */   {
/*  87 */     Preconditions.checkNotNull(nameSupplier);
/*  88 */     Preconditions.checkNotNull(task);
/*  89 */     return new Runnable() {
/*     */       public void run() {
/*  91 */         Thread currentThread = Thread.currentThread();
/*  92 */         String oldName = currentThread.getName();
/*  93 */         boolean restoreName = Callables.trySetName((String)this.val$nameSupplier.get(), currentThread);
/*     */         try {
/*  95 */           task.run();
/*     */         } finally {
/*  97 */           if (restoreName)
/*  98 */             Callables.trySetName(oldName, currentThread);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static boolean trySetName(String threadName, Thread currentThread)
/*     */   {
/*     */     try
/*     */     {
/* 111 */       currentThread.setName(threadName);
/* 112 */       return true; } catch (SecurityException e) {
/*     */     }
/* 114 */     return false;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.util.concurrent.Callables
 * JD-Core Version:    0.6.2
 */