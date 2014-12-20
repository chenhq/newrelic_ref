/*     */ package com.newrelic.com.google.common.collect;
/*     */ 
/*     */ abstract class ImmutableSortedSetFauxverideShim<E> extends ImmutableSet<E>
/*     */ {
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet.Builder<E> builder()
/*     */   {
/*  46 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E element)
/*     */   {
/*  59 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2)
/*     */   {
/*  72 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3)
/*     */   {
/*  85 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 100 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 115 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] remaining)
/*     */   {
/* 130 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> copyOf(E[] elements)
/*     */   {
/* 143 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableSortedSetFauxverideShim
 * JD-Core Version:    0.6.2
 */