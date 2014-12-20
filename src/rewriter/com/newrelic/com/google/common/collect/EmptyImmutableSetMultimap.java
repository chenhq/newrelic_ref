/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ class EmptyImmutableSetMultimap extends ImmutableSetMultimap<Object, Object>
/*    */ {
/* 28 */   static final EmptyImmutableSetMultimap INSTANCE = new EmptyImmutableSetMultimap();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   private EmptyImmutableSetMultimap()
/*    */   {
/* 32 */     super(ImmutableMap.of(), 0, null);
/*    */   }
/*    */ 
/*    */   private Object readResolve() {
/* 36 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.EmptyImmutableSetMultimap
 * JD-Core Version:    0.6.2
 */