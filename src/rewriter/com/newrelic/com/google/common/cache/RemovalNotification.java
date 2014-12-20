/*    */ package com.newrelic.com.google.common.cache;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import com.newrelic.com.google.common.base.Objects;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ import java.util.Map.Entry;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class RemovalNotification<K, V>
/*    */   implements Map.Entry<K, V>
/*    */ {
/*    */ 
/*    */   @Nullable
/*    */   private final K key;
/*    */ 
/*    */   @Nullable
/*    */   private final V value;
/*    */   private final RemovalCause cause;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   RemovalNotification(@Nullable K key, @Nullable V value, RemovalCause cause)
/*    */   {
/* 48 */     this.key = key;
/* 49 */     this.value = value;
/* 50 */     this.cause = ((RemovalCause)Preconditions.checkNotNull(cause));
/*    */   }
/*    */ 
/*    */   public RemovalCause getCause()
/*    */   {
/* 57 */     return this.cause;
/*    */   }
/*    */ 
/*    */   public boolean wasEvicted()
/*    */   {
/* 65 */     return this.cause.wasEvicted();
/*    */   }
/*    */   @Nullable
/*    */   public K getKey() {
/* 69 */     return this.key;
/*    */   }
/*    */   @Nullable
/*    */   public V getValue() {
/* 73 */     return this.value;
/*    */   }
/*    */ 
/*    */   public final V setValue(V value) {
/* 77 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 81 */     if ((object instanceof Map.Entry)) {
/* 82 */       Map.Entry that = (Map.Entry)object;
/* 83 */       return (Objects.equal(getKey(), that.getKey())) && (Objects.equal(getValue(), that.getValue()));
/*    */     }
/*    */ 
/* 86 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 90 */     Object k = getKey();
/* 91 */     Object v = getValue();
/* 92 */     return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 99 */     return getKey() + "=" + getValue();
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.cache.RemovalNotification
 * JD-Core Version:    0.6.2
 */