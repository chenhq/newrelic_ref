/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.annotations.GwtIncompatible;
/*    */ import java.io.InvalidObjectException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ @GwtCompatible(serializable=true, emulated=true)
/*    */ abstract class ImmutableAsList<E> extends ImmutableList<E>
/*    */ {
/*    */   abstract ImmutableCollection<E> delegateCollection();
/*    */ 
/*    */   public boolean contains(Object target)
/*    */   {
/* 41 */     return delegateCollection().contains(target);
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 46 */     return delegateCollection().size();
/*    */   }
/*    */ 
/*    */   public boolean isEmpty()
/*    */   {
/* 51 */     return delegateCollection().isEmpty();
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 56 */     return delegateCollection().isPartialView();
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   private void readObject(ObjectInputStream stream)
/*    */     throws InvalidObjectException
/*    */   {
/* 77 */     throw new InvalidObjectException("Use SerializedForm");
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   Object writeReplace() {
/* 82 */     return new SerializedForm(delegateCollection());
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   static class SerializedForm
/*    */     implements Serializable
/*    */   {
/*    */     final ImmutableCollection<?> collection;
/*    */     private static final long serialVersionUID = 0L;
/*    */ 
/*    */     SerializedForm(ImmutableCollection<?> collection)
/*    */     {
/* 66 */       this.collection = collection;
/*    */     }
/*    */     Object readResolve() {
/* 69 */       return this.collection.asList();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.ImmutableAsList
 * JD-Core Version:    0.6.2
 */