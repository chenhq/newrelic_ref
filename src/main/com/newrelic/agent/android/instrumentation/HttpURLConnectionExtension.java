/*     */ package com.newrelic.agent.android.instrumentation;
/*     */ 
/*     */ import com.newrelic.agent.android.Measurements;
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
/*     */ import com.newrelic.agent.android.instrumentation.io.CountingOutputStream;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteEvent;
/*     */ import com.newrelic.agent.android.instrumentation.io.StreamCompleteListener;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.ProtocolException;
/*     */ import java.net.URL;
/*     */ import java.security.Permission;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class HttpURLConnectionExtension extends HttpURLConnection
/*     */ {
/*     */   private HttpURLConnection impl;
/*     */   private TransactionState transactionState;
/*  40 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*     */   public HttpURLConnectionExtension(HttpURLConnection impl) {
/*  43 */     super(impl.getURL());
/*  44 */     this.impl = impl;
/*  45 */     TransactionStateUtil.setCrossProcessHeader(impl);
/*     */   }
/*     */ 
/*     */   public void addRequestProperty(String field, String newValue)
/*     */   {
/*  50 */     this.impl.addRequestProperty(field, newValue);
/*     */   }
/*     */ 
/*     */   public void disconnect()
/*     */   {
/*  58 */     if ((this.transactionState != null) && (!this.transactionState.isComplete())) {
/*  59 */       addTransactionAndErrorData(this.transactionState);
/*     */     }
/*  61 */     this.impl.disconnect();
/*     */   }
/*     */ 
/*     */   public boolean usingProxy()
/*     */   {
/*  66 */     return this.impl.usingProxy();
/*     */   }
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/*  77 */     getTransactionState();
/*     */     try
/*     */     {
/*  80 */       this.impl.connect();
/*     */     }
/*     */     catch (IOException e) {
/*  83 */       error(e);
/*  84 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getAllowUserInteraction()
/*     */   {
/*  90 */     return this.impl.getAllowUserInteraction();
/*     */   }
/*     */ 
/*     */   public int getConnectTimeout()
/*     */   {
/*  95 */     return this.impl.getConnectTimeout();
/*     */   }
/*     */ 
/*     */   public Object getContent()
/*     */     throws IOException
/*     */   {
/* 103 */     getTransactionState();
/*     */     Object object;
/*     */     try
/*     */     {
/* 106 */       object = this.impl.getContent();
/*     */     }
/*     */     catch (IOException e) {
/* 109 */       error(e);
/* 110 */       throw e;
/*     */     }
/* 112 */     int contentLength = this.impl.getContentLength();
/* 113 */     if (contentLength >= 0) {
/* 114 */       TransactionState transactionState = getTransactionState();
/* 115 */       if (!transactionState.isComplete()) {
/* 116 */         transactionState.setBytesReceived(contentLength);
/* 117 */         addTransactionAndErrorData(transactionState);
/*     */       }
/*     */     }
/* 120 */     return object;
/*     */   }
/*     */ 
/*     */   public Object getContent(Class[] types)
/*     */     throws IOException
/*     */   {
/* 128 */     getTransactionState();
/*     */     Object object;
/*     */     try
/*     */     {
/* 131 */       object = this.impl.getContent(types);
/*     */     }
/*     */     catch (IOException e) {
/* 134 */       error(e);
/* 135 */       throw e;
/*     */     }
/* 137 */     checkResponse();
/* 138 */     return object;
/*     */   }
/*     */ 
/*     */   public String getContentEncoding()
/*     */   {
/* 143 */     getTransactionState();
/* 144 */     String contentEncoding = this.impl.getContentEncoding();
/* 145 */     checkResponse();
/* 146 */     return contentEncoding;
/*     */   }
/*     */ 
/*     */   public int getContentLength()
/*     */   {
/* 151 */     getTransactionState();
/* 152 */     int contentLength = this.impl.getContentLength();
/* 153 */     checkResponse();
/* 154 */     return contentLength;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 159 */     getTransactionState();
/* 160 */     String contentType = this.impl.getContentType();
/* 161 */     checkResponse();
/* 162 */     return contentType;
/*     */   }
/*     */ 
/*     */   public long getDate()
/*     */   {
/* 167 */     getTransactionState();
/* 168 */     long date = this.impl.getDate();
/* 169 */     checkResponse();
/* 170 */     return date;
/*     */   }
/*     */ 
/*     */   public InputStream getErrorStream() {
/* 175 */     getTransactionState();
/*     */     CountingInputStream in;
/*     */     try {
/* 178 */       in = new CountingInputStream(this.impl.getErrorStream(), true);
/*     */     } catch (Exception e) {
/* 180 */       log.error(e.toString());
/* 181 */       return this.impl.getErrorStream();
/*     */     }
/* 183 */     return in;
/*     */   }
/*     */ 
/*     */   public long getHeaderFieldDate(String field, long defaultValue)
/*     */   {
/* 188 */     getTransactionState();
/* 189 */     long date = this.impl.getHeaderFieldDate(field, defaultValue);
/* 190 */     checkResponse();
/* 191 */     return date;
/*     */   }
/*     */ 
/*     */   public boolean getInstanceFollowRedirects()
/*     */   {
/* 196 */     return this.impl.getInstanceFollowRedirects();
/*     */   }
/*     */ 
/*     */   public Permission getPermission() throws IOException
/*     */   {
/* 201 */     return this.impl.getPermission();
/*     */   }
/*     */ 
/*     */   public String getRequestMethod()
/*     */   {
/* 206 */     return this.impl.getRequestMethod();
/*     */   }
/*     */ 
/*     */   public int getResponseCode() throws IOException {
/* 211 */     getTransactionState();
/*     */     int responseCode;
/*     */     try {
/* 214 */       responseCode = this.impl.getResponseCode();
/*     */     }
/*     */     catch (IOException e) {
/* 217 */       error(e);
/* 218 */       throw e;
/*     */     }
/* 220 */     checkResponse();
/* 221 */     return responseCode;
/*     */   }
/*     */ 
/*     */   public String getResponseMessage() throws IOException {
/* 226 */     getTransactionState();
/*     */     String message;
/*     */     try {
/* 229 */       message = this.impl.getResponseMessage();
/*     */     }
/*     */     catch (IOException e) {
/* 232 */       error(e);
/* 233 */       throw e;
/*     */     }
/* 235 */     checkResponse();
/* 236 */     return message;
/*     */   }
/*     */ 
/*     */   public void setChunkedStreamingMode(int chunkLength)
/*     */   {
/* 241 */     this.impl.setChunkedStreamingMode(chunkLength);
/*     */   }
/*     */ 
/*     */   public void setFixedLengthStreamingMode(int contentLength)
/*     */   {
/* 246 */     this.impl.setFixedLengthStreamingMode(contentLength);
/*     */   }
/*     */ 
/*     */   public void setInstanceFollowRedirects(boolean followRedirects)
/*     */   {
/* 251 */     this.impl.setInstanceFollowRedirects(followRedirects);
/*     */   }
/*     */ 
/*     */   public void setRequestMethod(String method) throws ProtocolException
/*     */   {
/* 256 */     getTransactionState();
/*     */     try {
/* 258 */       this.impl.setRequestMethod(method);
/*     */     }
/*     */     catch (ProtocolException e) {
/* 261 */       error(e);
/* 262 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getDefaultUseCaches()
/*     */   {
/* 268 */     return this.impl.getDefaultUseCaches();
/*     */   }
/*     */ 
/*     */   public boolean getDoInput()
/*     */   {
/* 273 */     return this.impl.getDoInput();
/*     */   }
/*     */ 
/*     */   public boolean getDoOutput()
/*     */   {
/* 278 */     return this.impl.getDoOutput();
/*     */   }
/*     */ 
/*     */   public long getExpiration()
/*     */   {
/* 283 */     getTransactionState();
/* 284 */     long expiration = this.impl.getExpiration();
/* 285 */     checkResponse();
/* 286 */     return expiration;
/*     */   }
/*     */ 
/*     */   public String getHeaderField(int pos)
/*     */   {
/* 291 */     getTransactionState();
/* 292 */     String header = this.impl.getHeaderField(pos);
/* 293 */     checkResponse();
/* 294 */     return header;
/*     */   }
/*     */ 
/*     */   public String getHeaderField(String key)
/*     */   {
/* 299 */     getTransactionState();
/* 300 */     String header = this.impl.getHeaderField(key);
/* 301 */     checkResponse();
/* 302 */     return header;
/*     */   }
/*     */ 
/*     */   public int getHeaderFieldInt(String field, int defaultValue)
/*     */   {
/* 307 */     getTransactionState();
/* 308 */     int header = this.impl.getHeaderFieldInt(field, defaultValue);
/* 309 */     checkResponse();
/* 310 */     return header;
/*     */   }
/*     */ 
/*     */   public String getHeaderFieldKey(int posn)
/*     */   {
/* 315 */     getTransactionState();
/* 316 */     String key = this.impl.getHeaderFieldKey(posn);
/* 317 */     checkResponse();
/* 318 */     return key;
/*     */   }
/*     */ 
/*     */   public Map<String, List<String>> getHeaderFields()
/*     */   {
/* 323 */     getTransactionState();
/* 324 */     Map fields = this.impl.getHeaderFields();
/* 325 */     checkResponse();
/* 326 */     return fields;
/*     */   }
/*     */ 
/*     */   public long getIfModifiedSince()
/*     */   {
/* 331 */     getTransactionState();
/* 332 */     long ifModifiedSince = this.impl.getIfModifiedSince();
/* 333 */     checkResponse();
/* 334 */     return ifModifiedSince;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream() throws IOException {
/* 339 */     final TransactionState transactionState = getTransactionState();
/*     */     CountingInputStream in;
/*     */     try {
/* 342 */       in = new CountingInputStream(this.impl.getInputStream());
/* 343 */       TransactionStateUtil.inspectAndInstrumentResponse(transactionState, this.impl);
/*     */     }
/*     */     catch (IOException e) {
/* 346 */       error(e);
/* 347 */       throw e;
/*     */     }
/* 349 */     in.addStreamCompleteListener(new StreamCompleteListener()
/*     */     {
/*     */       public void streamError(StreamCompleteEvent e) {
/* 352 */         if (!transactionState.isComplete()) {
/* 353 */           transactionState.setBytesReceived(e.getBytes());
/*     */         }
/* 355 */         HttpURLConnectionExtension.this.error(e.getException());
/*     */       }
/*     */ 
/*     */       public void streamComplete(StreamCompleteEvent e)
/*     */       {
/* 360 */         if (!transactionState.isComplete()) {
/* 361 */           long contentLength = HttpURLConnectionExtension.this.impl.getContentLength();
/* 362 */           long numBytes = e.getBytes();
/* 363 */           if (contentLength >= 0L) {
/* 364 */             numBytes = contentLength;
/*     */           }
/* 366 */           transactionState.setBytesReceived(numBytes);
/* 367 */           HttpURLConnectionExtension.this.addTransactionAndErrorData(transactionState);
/*     */         }
/*     */       }
/*     */     });
/* 371 */     return in;
/*     */   }
/*     */ 
/*     */   public long getLastModified()
/*     */   {
/* 376 */     getTransactionState();
/* 377 */     long lastModified = this.impl.getLastModified();
/* 378 */     checkResponse();
/* 379 */     return lastModified;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() throws IOException {
/* 384 */     final TransactionState transactionState = getTransactionState();
/*     */     CountingOutputStream out;
/*     */     try {
/* 387 */       out = new CountingOutputStream(this.impl.getOutputStream());
/*     */     }
/*     */     catch (IOException e) {
/* 390 */       error(e);
/* 391 */       throw e;
/*     */     }
/* 393 */     out.addStreamCompleteListener(new StreamCompleteListener()
/*     */     {
/*     */       public void streamError(StreamCompleteEvent e) {
/* 396 */         if (!transactionState.isComplete()) {
/* 397 */           transactionState.setBytesSent(e.getBytes());
/*     */         }
/* 399 */         HttpURLConnectionExtension.this.error(e.getException());
/*     */       }
/*     */ 
/*     */       public void streamComplete(StreamCompleteEvent e)
/*     */       {
/* 404 */         if (!transactionState.isComplete()) {
/* 405 */           String header = HttpURLConnectionExtension.this.impl.getRequestProperty("content-length");
/* 406 */           long numBytes = e.getBytes();
/* 407 */           if (header != null) {
/*     */             try {
/* 409 */               numBytes = Long.parseLong(header);
/*     */             }
/*     */             catch (NumberFormatException ex)
/*     */             {
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 417 */           transactionState.setBytesSent(numBytes);
/* 418 */           HttpURLConnectionExtension.this.addTransactionAndErrorData(transactionState);
/*     */         }
/*     */       }
/*     */     });
/* 422 */     return out;
/*     */   }
/*     */ 
/*     */   public int getReadTimeout()
/*     */   {
/* 427 */     return this.impl.getReadTimeout();
/*     */   }
/*     */ 
/*     */   public Map<String, List<String>> getRequestProperties()
/*     */   {
/* 432 */     return this.impl.getRequestProperties();
/*     */   }
/*     */ 
/*     */   public String getRequestProperty(String field)
/*     */   {
/* 437 */     return this.impl.getRequestProperty(field);
/*     */   }
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 442 */     return this.impl.getURL();
/*     */   }
/*     */ 
/*     */   public boolean getUseCaches()
/*     */   {
/* 447 */     return this.impl.getUseCaches();
/*     */   }
/*     */ 
/*     */   public void setAllowUserInteraction(boolean newValue)
/*     */   {
/* 452 */     this.impl.setAllowUserInteraction(newValue);
/*     */   }
/*     */ 
/*     */   public void setConnectTimeout(int timeoutMillis)
/*     */   {
/* 457 */     this.impl.setConnectTimeout(timeoutMillis);
/*     */   }
/*     */ 
/*     */   public void setDefaultUseCaches(boolean newValue)
/*     */   {
/* 462 */     this.impl.setDefaultUseCaches(newValue);
/*     */   }
/*     */ 
/*     */   public void setDoInput(boolean newValue)
/*     */   {
/* 467 */     this.impl.setDoInput(newValue);
/*     */   }
/*     */ 
/*     */   public void setDoOutput(boolean newValue)
/*     */   {
/* 472 */     this.impl.setDoOutput(newValue);
/*     */   }
/*     */ 
/*     */   public void setIfModifiedSince(long newValue)
/*     */   {
/* 477 */     this.impl.setIfModifiedSince(newValue);
/*     */   }
/*     */ 
/*     */   public void setReadTimeout(int timeoutMillis)
/*     */   {
/* 482 */     this.impl.setReadTimeout(timeoutMillis);
/*     */   }
/*     */ 
/*     */   public void setRequestProperty(String field, String newValue)
/*     */   {
/* 487 */     this.impl.setRequestProperty(field, newValue);
/*     */   }
/*     */ 
/*     */   public void setUseCaches(boolean newValue)
/*     */   {
/* 492 */     this.impl.setUseCaches(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 497 */     return this.impl.toString();
/*     */   }
/*     */ 
/*     */   private void checkResponse() {
/* 501 */     if (!getTransactionState().isComplete())
/* 502 */       TransactionStateUtil.inspectAndInstrumentResponse(getTransactionState(), this.impl);
/*     */   }
/*     */ 
/*     */   private TransactionState getTransactionState()
/*     */   {
/* 507 */     if (this.transactionState == null) {
/* 508 */       this.transactionState = new TransactionState();
/* 509 */       TransactionStateUtil.inspectAndInstrument(this.transactionState, this.impl);
/*     */     }
/* 511 */     return this.transactionState;
/*     */   }
/*     */ 
/*     */   private void error(Exception e) {
/* 515 */     TransactionState transactionState = getTransactionState();
/* 516 */     TransactionStateUtil.setErrorCodeFromException(transactionState, e);
/* 517 */     if (!transactionState.isComplete()) {
/* 518 */       TransactionStateUtil.inspectAndInstrumentResponse(transactionState, this.impl);
/* 519 */       TransactionData transactionData = transactionState.end();
/*     */ 
/* 522 */       if (transactionData != null)
/* 523 */         TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addTransactionAndErrorData(TransactionState transactionState)
/*     */   {
/* 530 */     TransactionData transactionData = transactionState.end();
/*     */ 
/* 533 */     if (transactionData == null) {
/* 534 */       return;
/*     */     }
/*     */ 
/* 537 */     TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */ 
/* 539 */     if (transactionState.getStatusCode() >= 400L) {
/* 540 */       StringBuilder responseBody = new StringBuilder();
/*     */       try {
/* 542 */         InputStream errorStream = getErrorStream();
/* 543 */         if ((errorStream instanceof CountingInputStream))
/* 544 */           responseBody.append(((CountingInputStream)errorStream).getBufferAsString());
/*     */       }
/*     */       catch (Exception e) {
/* 547 */         log.error(e.toString());
/*     */       }
/*     */ 
/* 551 */       Map params = new TreeMap();
/* 552 */       String contentType = this.impl.getContentType();
/*     */ 
/* 554 */       if ((contentType != null) && (!"".equals(contentType))) {
/* 555 */         params.put("content_type", contentType);
/*     */       }
/*     */ 
/* 562 */       params.put("content_length", transactionState.getBytesReceived() + "");
/*     */ 
/* 565 */       Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), responseBody.toString(), params);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.HttpURLConnectionExtension
 * JD-Core Version:    0.6.2
 */