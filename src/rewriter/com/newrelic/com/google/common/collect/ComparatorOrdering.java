/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class ComparatorOrdering<T> extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final Comparator<T> comparator;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   ComparatorOrdering(Comparator<T> comparator)
/*    */   {
/* 34 */     this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*    */   }
/*    */ 
/*    */   public int compare(T a, T b) {
/* 38 */     return this.comparator.compare(a, b);
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 42 */     if (object == this) {
/* 43 */       return true;
/*    */     }
/* 45 */     if ((object instanceof ComparatorOrdering)) {
/* 46 */       ComparatorOrdering that = (ComparatorOrdering)object;
/* 47 */       return this.comparator.equals(that.comparator);
/*    */     }
/* 49 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 53 */     return this.comparator.hashCode();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 57 */     return this.comparator.toString();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ComparatorOrdering
 * JD-Core Version:    0.6.2
 */