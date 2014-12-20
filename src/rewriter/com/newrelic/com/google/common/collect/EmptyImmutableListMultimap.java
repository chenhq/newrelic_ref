/*    */ package com.newrelic.com.google.common.collect;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ class EmptyImmutableListMultimap extends ImmutableListMultimap<Object, Object>
/*    */ {
/* 28 */   static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   private EmptyImmutableListMultimap()
/*    */   {
/* 32 */     super(ImmutableMap.of(), 0);
/*    */   }
/*    */ 
/*    */   private Object readResolve() {
/* 36 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.collect.EmptyImmutableListMultimap
 * JD-Core Version:    0.6.2
 */