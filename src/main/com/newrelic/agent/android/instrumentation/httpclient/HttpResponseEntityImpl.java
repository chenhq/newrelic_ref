/*     */ package com.newrelic.agent.android.instrumentation.httpclient;
/*     */ 
/*     */ import com.newrelic.agent.android.Measurements;
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.instrumentation.TransactionState;
/*     */ import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
/*     */ import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
/*     */ import com.newrelic.agent.android.instrumentation.io.CountingOutputStream;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteEvent;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteListener;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteListenerSource;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.entity.HttpEntityWrapper;
/*     */ import org.apache.http.message.AbstractHttpMessage;
/*     */ 
/*     */ public final class HttpResponseEntityImpl
/*     */   implements HttpEntity, StreamCompleteListener
/*     */ {
/*     */   private static final String TRANSFER_ENCODING_HEADER = "Transfer-Encoding";
/*     */   private static final String ENCODING_CHUNKED = "chunked";
/*     */   private final HttpEntity impl;
/*     */   private final TransactionState transactionState;
/*     */   private final long contentLengthFromHeader;
/*     */   private CountingInputStream contentStream;
/*  32 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*     */   public HttpResponseEntityImpl(HttpEntity impl, TransactionState transactionState, long contentLengthFromHeader) {
/*  35 */     this.impl = impl;
/*  36 */     this.transactionState = transactionState;
/*  37 */     this.contentLengthFromHeader = contentLengthFromHeader;
/*     */   }
/*     */ 
/*     */   public void consumeContent() throws IOException
/*     */   {
/*     */     try {
/*  43 */       this.impl.consumeContent();
/*     */     }
/*     */     catch (IOException e) {
/*  46 */       handleException(e);
/*  47 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream getContent()
/*     */     throws IOException, IllegalStateException
/*     */   {
/*  54 */     if (this.contentStream != null)
/*  55 */       return this.contentStream;
/*     */     try
/*     */     {
/*  58 */       boolean shouldBuffer = true;
/*     */ 
/*  61 */       if ((this.impl instanceof AbstractHttpMessage)) {
/*  62 */         AbstractHttpMessage message = (AbstractHttpMessage)this.impl;
/*  63 */         Header transferEncodingHeader = message.getLastHeader("Transfer-Encoding");
/*  64 */         if ((transferEncodingHeader != null) && ("chunked".equalsIgnoreCase(transferEncodingHeader.getValue()))) {
/*  65 */           shouldBuffer = false;
/*     */         }
/*     */       }
/*  68 */       else if ((this.impl instanceof HttpEntityWrapper)) {
/*  69 */         HttpEntityWrapper entityWrapper = (HttpEntityWrapper)this.impl;
/*  70 */         shouldBuffer = !entityWrapper.isChunked();
/*     */       }
/*     */ 
/*  74 */       this.contentStream = new CountingInputStream(this.impl.getContent(), shouldBuffer);
/*  75 */       this.contentStream.addStreamCompleteListener(this);
/*  76 */       return this.contentStream;
/*     */     }
/*     */     catch (IOException e) {
/*  79 */       handleException(e);
/*  80 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Header getContentEncoding()
/*     */   {
/*  86 */     return this.impl.getContentEncoding();
/*     */   }
/*     */ 
/*     */   public long getContentLength()
/*     */   {
/*  91 */     return this.impl.getContentLength();
/*     */   }
/*     */ 
/*     */   public Header getContentType()
/*     */   {
/*  96 */     return this.impl.getContentType();
/*     */   }
/*     */ 
/*     */   public boolean isChunked()
/*     */   {
/* 101 */     return this.impl.isChunked();
/*     */   }
/*     */ 
/*     */   public boolean isRepeatable()
/*     */   {
/* 106 */     return this.impl.isRepeatable();
/*     */   }
/*     */ 
/*     */   public boolean isStreaming()
/*     */   {
/* 111 */     return this.impl.isStreaming();
/*     */   }
/*     */ 
/*     */   public void writeTo(OutputStream outstream) throws IOException
/*     */   {
/* 116 */     if (!this.transactionState.isComplete()) {
/* 117 */       CountingOutputStream outputStream = new CountingOutputStream(outstream);
/*     */       try {
/* 119 */         this.impl.writeTo(outputStream);
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 126 */         handleException(e, Long.valueOf(outputStream.getCount()));
/* 127 */         e.printStackTrace();
/* 128 */         throw e;
/*     */       }
/*     */ 
/* 131 */       if (!this.transactionState.isComplete()) {
/* 132 */         if (this.contentLengthFromHeader >= 0L) {
/* 133 */           this.transactionState.setBytesReceived(this.contentLengthFromHeader);
/*     */         }
/*     */         else {
/* 136 */           this.transactionState.setBytesReceived(outputStream.getCount());
/*     */         }
/* 138 */         addTransactionAndErrorData(this.transactionState);
/*     */       }
/*     */     }
/*     */     else {
/* 142 */       this.impl.writeTo(outstream);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void streamComplete(StreamCompleteEvent e)
/*     */   {
/* 148 */     StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
/* 149 */     source.removeStreamCompleteListener(this);
/* 150 */     if (!this.transactionState.isComplete()) {
/* 151 */       if (this.contentLengthFromHeader >= 0L) {
/* 152 */         this.transactionState.setBytesReceived(this.contentLengthFromHeader);
/*     */       }
/*     */       else {
/* 155 */         this.transactionState.setBytesReceived(e.getBytes());
/*     */       }
/* 157 */       addTransactionAndErrorData(this.transactionState);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void streamError(StreamCompleteEvent e)
/*     */   {
/* 163 */     StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
/* 164 */     source.removeStreamCompleteListener(this);
/* 165 */     TransactionStateUtil.setErrorCodeFromException(this.transactionState, e.getException());
/* 166 */     if (!this.transactionState.isComplete())
/* 167 */       this.transactionState.setBytesReceived(e.getBytes());
/*     */   }
/*     */ 
/*     */   private void addTransactionAndErrorData(TransactionState transactionState)
/*     */   {
/* 172 */     TransactionData transactionData = transactionState.end();
/*     */ 
/* 175 */     if (transactionData == null) {
/* 176 */       return;
/*     */     }
/*     */ 
/* 179 */     TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */ 
/* 181 */     if (transactionState.getStatusCode() >= 400L) {
/* 182 */       StringBuilder responseBody = new StringBuilder();
/*     */       try {
/* 184 */         InputStream errorStream = getContent();
/* 185 */         if ((errorStream instanceof CountingInputStream))
/* 186 */           responseBody.append(((CountingInputStream)errorStream).getBufferAsString());
/*     */       }
/*     */       catch (Exception e) {
/* 189 */         log.error(e.toString());
/*     */       }
/*     */ 
/* 192 */       Header contentType = this.impl.getContentType();
/*     */ 
/* 195 */       Map params = new TreeMap();
/* 196 */       if ((contentType != null) && (contentType.getValue() != null) && (!"".equals(contentType.getValue()))) {
/* 197 */         params.put("content_type", contentType.getValue());
/*     */       }
/*     */ 
/* 203 */       params.put("content_length", transactionState.getBytesReceived() + "");
/*     */ 
/* 205 */       Measurements.addHttpError(transactionData, responseBody.toString(), params);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void handleException(Exception e)
/*     */   {
/* 211 */     handleException(e, null);
/*     */   }
/*     */ 
/*     */   private void handleException(Exception e, Long streamBytes) {
/* 215 */     TransactionStateUtil.setErrorCodeFromException(this.transactionState, e);
/* 216 */     if (!this.transactionState.isComplete()) {
/* 217 */       if (streamBytes != null) {
/* 218 */         this.transactionState.setBytesReceived(streamBytes.longValue());
/*     */       }
/* 220 */       TransactionData transactionData = this.transactionState.end();
/*     */ 
/* 223 */       if (transactionData != null)
/* 224 */         TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.httpclient.HttpResponseEntityImpl
 * JD-Core Version:    0.6.2
 */