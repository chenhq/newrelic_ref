/*    */ package com.newrelic.agent.android.instrumentation.io;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ class StreamCompleteListenerManager
/*    */ {
/*  8 */   private boolean streamComplete = false;
/*  9 */   private ArrayList<StreamCompleteListener> streamCompleteListeners = new ArrayList();
/*    */ 
/*    */   public boolean isComplete() {
/* 12 */     synchronized (this) {
/* 13 */       return this.streamComplete;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void addStreamCompleteListener(StreamCompleteListener streamCompleteListener) {
/* 18 */     synchronized (this.streamCompleteListeners) {
/* 19 */       this.streamCompleteListeners.add(streamCompleteListener);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void removeStreamCompleteListener(StreamCompleteListener streamCompleteListener) {
/* 24 */     synchronized (this.streamCompleteListeners) {
/* 25 */       this.streamCompleteListeners.remove(streamCompleteListener);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void notifyStreamComplete(StreamCompleteEvent ev) {
/* 30 */     if (!checkComplete())
/* 31 */       for (StreamCompleteListener listener : getStreamCompleteListeners())
/* 32 */         listener.streamComplete(ev);
/*    */   }
/*    */ 
/*    */   public void notifyStreamError(StreamCompleteEvent ev)
/*    */   {
/* 38 */     if (!checkComplete())
/* 39 */       for (StreamCompleteListener listener : getStreamCompleteListeners())
/* 40 */         listener.streamError(ev);
/*    */   }
/*    */ 
/*    */   private boolean checkComplete()
/*    */   {
/*    */     boolean streamComplete;
/* 47 */     synchronized (this) {
/* 48 */       streamComplete = isComplete();
/* 49 */       if (!streamComplete) this.streamComplete = true;
/*    */     }
/* 51 */     return streamComplete;
/*    */   }
/*    */ 
/*    */   private List<StreamCompleteListener> getStreamCompleteListeners()
/*    */   {
/*    */     ArrayList listeners;
/* 56 */     synchronized (this.streamCompleteListeners) {
/* 57 */       listeners = new ArrayList(this.streamCompleteListeners);
/* 58 */       this.streamCompleteListeners.clear();
/*    */     }
/* 60 */     return listeners;
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.io.StreamCompleteListenerManager
 * JD-Core Version:    0.6.2
 */