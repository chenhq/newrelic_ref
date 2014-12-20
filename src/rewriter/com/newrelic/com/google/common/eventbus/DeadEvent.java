/*    */ package com.newrelic.com.google.common.eventbus;
/*    */ 
/*    */ import com.newrelic.com.google.common.annotations.Beta;
/*    */ import com.newrelic.com.google.common.base.Preconditions;
/*    */ 
/*    */ @Beta
/*    */ public class DeadEvent
/*    */ {
/*    */   private final Object source;
/*    */   private final Object event;
/*    */ 
/*    */   public DeadEvent(Object source, Object event)
/*    */   {
/* 47 */     this.source = Preconditions.checkNotNull(source);
/* 48 */     this.event = Preconditions.checkNotNull(event);
/*    */   }
/*    */ 
/*    */   public Object getSource()
/*    */   {
/* 58 */     return this.source;
/*    */   }
/*    */ 
/*    */   public Object getEvent()
/*    */   {
/* 68 */     return this.event;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.com.google.common.eventbus.DeadEvent
 * JD-Core Version:    0.6.2
 */