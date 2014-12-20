/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class Absent<T> extends Optional<T>
/*    */ {
/* 33 */   static final Absent<Object> INSTANCE = new Absent();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   static <T> Optional<T> withType()
/*    */   {
/* 37 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public boolean isPresent()
/*    */   {
/* 43 */     return false;
/*    */   }
/*    */ 
/*    */   public T get() {
/* 47 */     throw new IllegalStateException("Optional.get() cannot be called on an absent value");
/*    */   }
/*    */ 
/*    */   public T or(T defaultValue) {
/* 51 */     return Preconditions.checkNotNull(defaultValue, "use Optional.orNull() instead of Optional.or(null)");
/*    */   }
/*    */ 
/*    */   public Optional<T> or(Optional<? extends T> secondChoice)
/*    */   {
/* 56 */     return (Optional)Preconditions.checkNotNull(secondChoice);
/*    */   }
/*    */ 
/*    */   public T or(Supplier<? extends T> supplier) {
/* 60 */     return Preconditions.checkNotNull(supplier.get(), "use Optional.orNull() instead of a Supplier that returns null");
/*    */   }
/*    */ 
/*    */   @Nullable
/*    */   public T orNull() {
/* 65 */     return null;
/*    */   }
/*    */ 
/*    */   public Set<T> asSet() {
/* 69 */     return Collections.emptySet();
/*    */   }
/*    */ 
/*    */   public <V> Optional<V> transform(Function<? super T, V> function) {
/* 73 */     Preconditions.checkNotNull(function);
/* 74 */     return Optional.absent();
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 78 */     return object == this;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 82 */     return 1502476572;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 86 */     return "Optional.absent()";
/*    */   }
/*    */ 
/*    */   private Object readResolve() {
/* 90 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Absent
 * JD-Core Version:    0.6.2
 */