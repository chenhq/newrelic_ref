/*    */ package com.newrelic.com.google.common.base;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.util.Map;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/*    */   static long systemNanoTime()
/*    */   {
/* 34 */     return System.nanoTime();
/*    */   }
/*    */ 
/*    */   static CharMatcher precomputeCharMatcher(CharMatcher matcher) {
/* 38 */     return matcher.precomputedInternal();
/*    */   }
/*    */ 
/*    */   static <T extends Enum<T>> Optional<T> getEnumIfPresent(Class<T> enumClass, String value) {
/* 42 */     WeakReference ref = (WeakReference)Enums.getEnumConstants(enumClass).get(value);
/* 43 */     return ref == null ? Optional.absent() : Optional.of(enumClass.cast(ref.get()));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.base.Platform
 * JD-Core Version:    0.6.2
 */