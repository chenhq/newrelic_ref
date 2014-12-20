/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.util.Comparator;
/*    */ import java.util.SortedSet;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class SortedIterables
/*    */ {
/*    */   public static boolean hasSameComparator(Comparator<?> comparator, Iterable<?> elements)
/*    */   {
/* 38 */     Preconditions.checkNotNull(comparator);
/* 39 */     Preconditions.checkNotNull(elements);
/*    */     Comparator comparator2;
/* 41 */     if ((elements instanceof SortedSet)) {
/* 42 */       comparator2 = comparator((SortedSet)elements);
/*    */     }
/*    */     else
/*    */     {
/*    */       Comparator comparator2;
/* 43 */       if ((elements instanceof SortedIterable))
/* 44 */         comparator2 = ((SortedIterable)elements).comparator();
/*    */       else
/* 46 */         return false;
/*    */     }
/*    */     Comparator comparator2;
/* 48 */     return comparator.equals(comparator2);
/*    */   }
/*    */ 
/*    */   public static <E> Comparator<? super E> comparator(SortedSet<E> sortedSet)
/*    */   {
/* 54 */     Comparator result = sortedSet.comparator();
/* 55 */     if (result == null) {
/* 56 */       result = Ordering.natural();
/*    */     }
/* 58 */     return result;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.SortedIterables
 * JD-Core Version:    0.6.2
 */