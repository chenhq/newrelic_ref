/*    */ package com.newrelic.agent.android.api.v1;
/*    */ 
/*    */ import com.newrelic.agent.android.api.common.ConnectionState;
/*    */ import java.util.EventObject;
/*    */ 
/*    */ public final class ConnectionEvent extends EventObject
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final ConnectionState connectionState;
/*    */ 
/*    */   public ConnectionEvent(Object source)
/*    */   {
/* 13 */     this(source, null);
/*    */   }
/*    */ 
/*    */   public ConnectionEvent(Object source, ConnectionState connectionState) {
/* 17 */     super(source);
/* 18 */     this.connectionState = connectionState;
/*    */   }
/*    */ 
/*    */   public ConnectionState getConnectionState() {
/* 22 */     return this.connectionState;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.api.v1.ConnectionEvent
 * JD-Core Version:    0.6.2
 */