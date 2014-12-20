/*    */ package com.newrelic.agent.android.instrumentation.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public final class CountingOutputStream extends OutputStream
/*    */   implements StreamCompleteListenerSource
/*    */ {
/*    */   private final OutputStream impl;
/* 10 */   private long count = 0L;
/* 11 */   private final StreamCompleteListenerManager listenerManager = new StreamCompleteListenerManager();
/*    */ 
/*    */   public CountingOutputStream(OutputStream impl) {
/* 14 */     this.impl = impl;
/*    */   }
/*    */ 
/*    */   public void addStreamCompleteListener(StreamCompleteListener streamCompleteListener) {
/* 18 */     this.listenerManager.addStreamCompleteListener(streamCompleteListener);
/*    */   }
/*    */ 
/*    */   public void removeStreamCompleteListener(StreamCompleteListener streamCompleteListener) {
/* 22 */     this.listenerManager.removeStreamCompleteListener(streamCompleteListener);
/*    */   }
/*    */ 
/*    */   public long getCount() {
/* 26 */     return this.count;
/*    */   }
/*    */ 
/*    */   public void write(int oneByte) throws IOException
/*    */   {
/*    */     try {
/* 32 */       this.impl.write(oneByte);
/* 33 */       this.count += 1L;
/*    */     }
/*    */     catch (IOException e) {
/* 36 */       notifyStreamError(e);
/* 37 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void write(byte[] buffer) throws IOException
/*    */   {
/*    */     try {
/* 44 */       this.impl.write(buffer);
/* 45 */       this.count += buffer.length;
/*    */     }
/*    */     catch (IOException e) {
/* 48 */       notifyStreamError(e);
/* 49 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void write(byte[] buffer, int offset, int count) throws IOException
/*    */   {
/*    */     try {
/* 56 */       this.impl.write(buffer, offset, count);
/* 57 */       this.count += count;
/*    */     }
/*    */     catch (IOException e) {
/* 60 */       notifyStreamError(e);
/* 61 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void flush() throws IOException
/*    */   {
/*    */     try {
/* 68 */       this.impl.flush();
/*    */     }
/*    */     catch (IOException e) {
/* 71 */       notifyStreamError(e);
/* 72 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/*    */     try {
/* 79 */       this.impl.close();
/* 80 */       notifyStreamComplete();
/*    */     }
/*    */     catch (IOException e) {
/* 83 */       notifyStreamError(e);
/* 84 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   private void notifyStreamComplete() {
/* 89 */     if (!this.listenerManager.isComplete())
/* 90 */       this.listenerManager.notifyStreamComplete(new StreamCompleteEvent(this, this.count));
/*    */   }
/*    */ 
/*    */   private void notifyStreamError(Exception e)
/*    */   {
/* 95 */     if (!this.listenerManager.isComplete())
/* 96 */       this.listenerManager.notifyStreamError(new StreamCompleteEvent(this, this.count, e));
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.io.CountingOutputStream
 * JD-Core Version:    0.6.2
 */