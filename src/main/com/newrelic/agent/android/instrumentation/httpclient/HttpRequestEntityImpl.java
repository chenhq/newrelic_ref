/*     */ package com.newrelic.agent.android.instrumentation.httpclient;
/*     */ 
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.instrumentation.TransactionState;
/*     */ import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
/*     */ import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
/*     */ import com.newrelic.agent.android.instrumentation.io.CountingOutputStream;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteEvent;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteListener;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteListenerSource;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ 
/*     */ public final class HttpRequestEntityImpl
/*     */   implements HttpEntity, StreamCompleteListener
/*     */ {
/*     */   private final HttpEntity impl;
/*     */   private final TransactionState transactionState;
/*     */ 
/*     */   public HttpRequestEntityImpl(HttpEntity impl, TransactionState transactionState)
/*     */   {
/*  21 */     this.impl = impl;
/*  22 */     this.transactionState = transactionState;
/*     */   }
/*     */ 
/*     */   public void consumeContent() throws IOException
/*     */   {
/*     */     try {
/*  28 */       this.impl.consumeContent();
/*     */     }
/*     */     catch (IOException e) {
/*  31 */       handleException(e);
/*  32 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream getContent() throws IOException, IllegalStateException
/*     */   {
/*     */     try {
/*  39 */       if (!this.transactionState.isSent()) {
/*  40 */         CountingInputStream stream = new CountingInputStream(this.impl.getContent());
/*  41 */         stream.addStreamCompleteListener(this);
/*  42 */         return stream;
/*     */       }
/*     */ 
/*  45 */       return this.impl.getContent();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  49 */       handleException(e);
/*  50 */       throw e;
/*     */     }
/*     */     catch (IllegalStateException e) {
/*  53 */       handleException(e);
/*  54 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Header getContentEncoding()
/*     */   {
/*  60 */     return this.impl.getContentEncoding();
/*     */   }
/*     */ 
/*     */   public long getContentLength()
/*     */   {
/*  65 */     return this.impl.getContentLength();
/*     */   }
/*     */ 
/*     */   public Header getContentType()
/*     */   {
/*  70 */     return this.impl.getContentType();
/*     */   }
/*     */ 
/*     */   public boolean isChunked()
/*     */   {
/*  75 */     return this.impl.isChunked();
/*     */   }
/*     */ 
/*     */   public boolean isRepeatable()
/*     */   {
/*  80 */     return this.impl.isRepeatable();
/*     */   }
/*     */ 
/*     */   public boolean isStreaming()
/*     */   {
/*  85 */     return this.impl.isStreaming();
/*     */   }
/*     */ 
/*     */   public void writeTo(OutputStream outstream) throws IOException
/*     */   {
/*     */     try {
/*  91 */       if (!this.transactionState.isSent()) {
/*  92 */         CountingOutputStream stream = new CountingOutputStream(outstream);
/*  93 */         this.impl.writeTo(stream);
/*  94 */         this.transactionState.setBytesSent(stream.getCount());
/*     */       }
/*     */       else {
/*  97 */         this.impl.writeTo(outstream);
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 101 */       handleException(e);
/* 102 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void streamComplete(StreamCompleteEvent e)
/*     */   {
/* 108 */     StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
/* 109 */     source.removeStreamCompleteListener(this);
/* 110 */     this.transactionState.setBytesSent(e.getBytes());
/*     */   }
/*     */ 
/*     */   public void streamError(StreamCompleteEvent e)
/*     */   {
/* 115 */     StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
/* 116 */     source.removeStreamCompleteListener(this);
/* 117 */     handleException(e.getException(), Long.valueOf(e.getBytes()));
/*     */   }
/*     */ 
/*     */   private void handleException(Exception e) {
/* 121 */     handleException(e, null);
/*     */   }
/*     */ 
/*     */   private void handleException(Exception e, Long streamBytes) {
/* 125 */     TransactionStateUtil.setErrorCodeFromException(this.transactionState, e);
/* 126 */     if (!this.transactionState.isComplete()) {
/* 127 */       if (streamBytes != null) {
/* 128 */         this.transactionState.setBytesSent(streamBytes.longValue());
/*     */       }
/* 130 */       TransactionData transactionData = this.transactionState.end();
/*     */ 
/* 133 */       if (transactionData != null)
/* 134 */         TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.httpclient.HttpRequestEntityImpl
 * JD-Core Version:    0.6.2
 */