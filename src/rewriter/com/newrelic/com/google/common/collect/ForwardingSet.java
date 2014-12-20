/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.util.Collection;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingSet<E> extends ForwardingCollection<E>
/*    */   implements Set<E>
/*    */ {
/*    */   protected abstract Set<E> delegate();
/*    */ 
/*    */   public boolean equals(@Nullable Object object)
/*    */   {
/* 59 */     return (object == this) || (delegate().equals(object));
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 63 */     return delegate().hashCode();
/*    */   }
/*    */ 
/*    */   protected boolean standardRemoveAll(Collection<?> collection)
/*    */   {
/* 76 */     return Sets.removeAllImpl(this, (Collection)Preconditions.checkNotNull(collection));
/*    */   }
/*    */ 
/*    */   protected boolean standardEquals(@Nullable Object object)
/*    */   {
/* 87 */     return Sets.equalsImpl(this, object);
/*    */   }
/*    */ 
/*    */   protected int standardHashCode()
/*    */   {
/* 98 */     return Sets.hashCodeImpl(this);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ForwardingSet
 * JD-Core Version:    0.6.2
 */