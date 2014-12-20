/*    */ package com.newrelic.agent.android.instrumentation.io;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public final class StreamCompleteEvent extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final long bytes;
/*    */   private final Exception exception;
/*    */ 
/*    */   public StreamCompleteEvent(Object source, long bytes, Exception exception)
/*    */   {
/* 12 */     super(source);
/* 13 */     this.bytes = bytes;
/* 14 */     this.exception = exception;
/*    */   }
/*    */ 
/*    */   public StreamCompleteEvent(Object source, long bytes) {
/* 18 */     this(source, bytes, null);
/*    */   }
/*    */ 
/*    */   public long getBytes() {
/* 22 */     return this.bytes;
/*    */   }
/*    */ 
/*    */   public Exception getException() {
/* 26 */     return this.exception;
/*    */   }
/*    */ 
/*    */   public boolean isError() {
/* 30 */     return this.exception != null;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.io.StreamCompleteEvent
 * JD-Core Version:    0.6.2
 */