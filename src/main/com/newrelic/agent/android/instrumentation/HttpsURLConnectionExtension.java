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
/*     */ import java.net.ProtocolException;
/*     */ import java.net.URL;
/*     */ import java.security.Permission;
/*     */ import java.security.Principal;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ 
/*     */ public class HttpsURLConnectionExtension extends HttpsURLConnection
/*     */ {
/*     */   private HttpsURLConnection impl;
/*     */   private TransactionState transactionState;
/*  42 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */ 
/*     */   public HttpsURLConnectionExtension(HttpsURLConnection impl) {
/*  45 */     super(impl.getURL());
/*  46 */     this.impl = impl;
/*  47 */     TransactionStateUtil.setCrossProcessHeader(impl);
/*     */   }
/*     */ 
/*     */   public String getCipherSuite()
/*     */   {
/*  52 */     return this.impl.getCipherSuite();
/*     */   }
/*     */ 
/*     */   public Certificate[] getLocalCertificates()
/*     */   {
/*  57 */     return this.impl.getLocalCertificates();
/*     */   }
/*     */ 
/*     */   public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException
/*     */   {
/*     */     try {
/*  63 */       return this.impl.getServerCertificates();
/*     */     } catch (SSLPeerUnverifiedException e) {
/*  65 */       error(e);
/*  66 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addRequestProperty(String field, String newValue)
/*     */   {
/*  72 */     this.impl.addRequestProperty(field, newValue);
/*     */   }
/*     */ 
/*     */   public void disconnect()
/*     */   {
/*  80 */     if ((this.transactionState != null) && (!this.transactionState.isComplete())) {
/*  81 */       addTransactionAndErrorData(this.transactionState);
/*     */     }
/*  83 */     this.impl.disconnect();
/*     */   }
/*     */ 
/*     */   public boolean usingProxy()
/*     */   {
/*  88 */     return this.impl.usingProxy();
/*     */   }
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/*  99 */     getTransactionState();
/*     */     try
/*     */     {
/* 102 */       this.impl.connect();
/*     */     }
/*     */     catch (IOException e) {
/* 105 */       error(e);
/* 106 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getAllowUserInteraction()
/*     */   {
/* 112 */     return this.impl.getAllowUserInteraction();
/*     */   }
/*     */ 
/*     */   public int getConnectTimeout()
/*     */   {
/* 117 */     return this.impl.getConnectTimeout();
/*     */   }
/*     */ 
/*     */   public Object getContent()
/*     */     throws IOException
/*     */   {
/* 125 */     getTransactionState();
/*     */     Object object;
/*     */     try
/*     */     {
/* 128 */       object = this.impl.getContent();
/*     */     }
/*     */     catch (IOException e) {
/* 131 */       error(e);
/* 132 */       throw e;
/*     */     }
/* 134 */     int contentLength = this.impl.getContentLength();
/* 135 */     if (contentLength >= 0) {
/* 136 */       TransactionState transactionState = getTransactionState();
/* 137 */       if (!transactionState.isComplete()) {
/* 138 */         transactionState.setBytesReceived(contentLength);
/* 139 */         addTransactionAndErrorData(transactionState);
/*     */       }
/*     */     }
/* 142 */     return object;
/*     */   }
/*     */ 
/*     */   public Object getContent(Class[] types)
/*     */     throws IOException
/*     */   {
/* 150 */     getTransactionState();
/*     */     Object object;
/*     */     try
/*     */     {
/* 153 */       object = this.impl.getContent(types);
/*     */     }
/*     */     catch (IOException e) {
/* 156 */       error(e);
/* 157 */       throw e;
/*     */     }
/* 159 */     checkResponse();
/* 160 */     return object;
/*     */   }
/*     */ 
/*     */   public String getContentEncoding()
/*     */   {
/* 165 */     getTransactionState();
/* 166 */     String contentEncoding = this.impl.getContentEncoding();
/* 167 */     checkResponse();
/* 168 */     return contentEncoding;
/*     */   }
/*     */ 
/*     */   public int getContentLength()
/*     */   {
/* 173 */     getTransactionState();
/* 174 */     int contentLength = this.impl.getContentLength();
/* 175 */     checkResponse();
/* 176 */     return contentLength;
/*     */   }
/*     */ 
/*     */   public String getContentType()
/*     */   {
/* 181 */     getTransactionState();
/* 182 */     String contentType = this.impl.getContentType();
/* 183 */     checkResponse();
/* 184 */     return contentType;
/*     */   }
/*     */ 
/*     */   public long getDate()
/*     */   {
/* 189 */     getTransactionState();
/* 190 */     long date = this.impl.getDate();
/* 191 */     checkResponse();
/* 192 */     return date;
/*     */   }
/*     */ 
/*     */   public InputStream getErrorStream() {
/* 197 */     getTransactionState();
/*     */     CountingInputStream in;
/*     */     try {
/* 200 */       in = new CountingInputStream(this.impl.getErrorStream(), true);
/*     */     } catch (Exception e) {
/* 202 */       log.error(e.toString());
/* 203 */       return this.impl.getErrorStream();
/*     */     }
/* 205 */     return in;
/*     */   }
/*     */ 
/*     */   public long getHeaderFieldDate(String field, long defaultValue)
/*     */   {
/* 210 */     getTransactionState();
/* 211 */     long date = this.impl.getHeaderFieldDate(field, defaultValue);
/* 212 */     checkResponse();
/* 213 */     return date;
/*     */   }
/*     */ 
/*     */   public boolean getInstanceFollowRedirects()
/*     */   {
/* 218 */     return this.impl.getInstanceFollowRedirects();
/*     */   }
/*     */ 
/*     */   public Permission getPermission() throws IOException
/*     */   {
/* 223 */     return this.impl.getPermission();
/*     */   }
/*     */ 
/*     */   public String getRequestMethod()
/*     */   {
/* 228 */     return this.impl.getRequestMethod();
/*     */   }
/*     */ 
/*     */   public int getResponseCode() throws IOException {
/* 233 */     getTransactionState();
/*     */     int responseCode;
/*     */     try {
/* 236 */       responseCode = this.impl.getResponseCode();
/*     */     }
/*     */     catch (IOException e) {
/* 239 */       error(e);
/* 240 */       throw e;
/*     */     }
/* 242 */     checkResponse();
/* 243 */     return responseCode;
/*     */   }
/*     */ 
/*     */   public String getResponseMessage() throws IOException {
/* 248 */     getTransactionState();
/*     */     String message;
/*     */     try {
/* 251 */       message = this.impl.getResponseMessage();
/*     */     }
/*     */     catch (IOException e) {
/* 254 */       error(e);
/* 255 */       throw e;
/*     */     }
/* 257 */     checkResponse();
/* 258 */     return message;
/*     */   }
/*     */ 
/*     */   public void setChunkedStreamingMode(int chunkLength)
/*     */   {
/* 263 */     this.impl.setChunkedStreamingMode(chunkLength);
/*     */   }
/*     */ 
/*     */   public void setFixedLengthStreamingMode(int contentLength)
/*     */   {
/* 268 */     this.impl.setFixedLengthStreamingMode(contentLength);
/*     */   }
/*     */ 
/*     */   public void setInstanceFollowRedirects(boolean followRedirects)
/*     */   {
/* 273 */     this.impl.setInstanceFollowRedirects(followRedirects);
/*     */   }
/*     */ 
/*     */   public void setRequestMethod(String method) throws ProtocolException
/*     */   {
/*     */     try {
/* 279 */       this.impl.setRequestMethod(method);
/*     */     }
/*     */     catch (ProtocolException e) {
/* 282 */       error(e);
/* 283 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getDefaultUseCaches()
/*     */   {
/* 289 */     return this.impl.getDefaultUseCaches();
/*     */   }
/*     */ 
/*     */   public boolean getDoInput()
/*     */   {
/* 294 */     return this.impl.getDoInput();
/*     */   }
/*     */ 
/*     */   public boolean getDoOutput()
/*     */   {
/* 299 */     return this.impl.getDoOutput();
/*     */   }
/*     */ 
/*     */   public long getExpiration()
/*     */   {
/* 304 */     getTransactionState();
/* 305 */     long expiration = this.impl.getExpiration();
/* 306 */     checkResponse();
/* 307 */     return expiration;
/*     */   }
/*     */ 
/*     */   public String getHeaderField(int pos)
/*     */   {
/* 312 */     getTransactionState();
/* 313 */     String header = this.impl.getHeaderField(pos);
/* 314 */     checkResponse();
/* 315 */     return header;
/*     */   }
/*     */ 
/*     */   public String getHeaderField(String key)
/*     */   {
/* 320 */     getTransactionState();
/* 321 */     String header = this.impl.getHeaderField(key);
/* 322 */     checkResponse();
/* 323 */     return header;
/*     */   }
/*     */ 
/*     */   public int getHeaderFieldInt(String field, int defaultValue)
/*     */   {
/* 328 */     getTransactionState();
/* 329 */     int header = this.impl.getHeaderFieldInt(field, defaultValue);
/* 330 */     checkResponse();
/* 331 */     return header;
/*     */   }
/*     */ 
/*     */   public String getHeaderFieldKey(int posn)
/*     */   {
/* 336 */     getTransactionState();
/* 337 */     String key = this.impl.getHeaderFieldKey(posn);
/* 338 */     checkResponse();
/* 339 */     return key;
/*     */   }
/*     */ 
/*     */   public Map<String, List<String>> getHeaderFields()
/*     */   {
/* 344 */     getTransactionState();
/* 345 */     Map fields = this.impl.getHeaderFields();
/* 346 */     checkResponse();
/* 347 */     return fields;
/*     */   }
/*     */ 
/*     */   public long getIfModifiedSince()
/*     */   {
/* 352 */     getTransactionState();
/* 353 */     long ifModifiedSince = this.impl.getIfModifiedSince();
/* 354 */     checkResponse();
/* 355 */     return ifModifiedSince;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream() throws IOException {
/* 360 */     final TransactionState transactionState = getTransactionState();
/*     */     CountingInputStream in;
/*     */     try {
/* 363 */       in = new CountingInputStream(this.impl.getInputStream());
/* 364 */       TransactionStateUtil.inspectAndInstrumentResponse(transactionState, this.impl);
/*     */     }
/*     */     catch (IOException e) {
/* 367 */       error(e);
/* 368 */       throw e;
/*     */     }
/* 370 */     in.addStreamCompleteListener(new StreamCompleteListener()
/*     */     {
/*     */       public void streamError(StreamCompleteEvent e) {
/* 373 */         if (!transactionState.isComplete()) {
/* 374 */           transactionState.setBytesReceived(e.getBytes());
/*     */         }
/* 376 */         HttpsURLConnectionExtension.this.error(e.getException());
/*     */       }
/*     */ 
/*     */       public void streamComplete(StreamCompleteEvent e)
/*     */       {
/* 381 */         if (!transactionState.isComplete()) {
/* 382 */           long contentLength = HttpsURLConnectionExtension.this.impl.getContentLength();
/* 383 */           long numBytes = e.getBytes();
/* 384 */           if (contentLength >= 0L) {
/* 385 */             numBytes = contentLength;
/*     */           }
/* 387 */           transactionState.setBytesReceived(numBytes);
/* 388 */           HttpsURLConnectionExtension.this.addTransactionAndErrorData(transactionState);
/*     */         }
/*     */       }
/*     */     });
/* 392 */     return in;
/*     */   }
/*     */ 
/*     */   public long getLastModified()
/*     */   {
/* 397 */     getTransactionState();
/* 398 */     long lastModified = this.impl.getLastModified();
/* 399 */     checkResponse();
/* 400 */     return lastModified;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() throws IOException {
/* 405 */     final TransactionState transactionState = getTransactionState();
/*     */     CountingOutputStream out;
/*     */     try {
/* 408 */       out = new CountingOutputStream(this.impl.getOutputStream());
/*     */     }
/*     */     catch (IOException e) {
/* 411 */       error(e);
/* 412 */       throw e;
/*     */     }
/* 414 */     out.addStreamCompleteListener(new StreamCompleteListener()
/*     */     {
/*     */       public void streamError(StreamCompleteEvent e) {
/* 417 */         if (!transactionState.isComplete()) {
/* 418 */           transactionState.setBytesSent(e.getBytes());
/*     */         }
/* 420 */         HttpsURLConnectionExtension.this.error(e.getException());
/*     */       }
/*     */ 
/*     */       public void streamComplete(StreamCompleteEvent e)
/*     */       {
/* 425 */         if (!transactionState.isComplete()) {
/* 426 */           String header = HttpsURLConnectionExtension.this.impl.getRequestProperty("content-length");
/* 427 */           long numBytes = e.getBytes();
/* 428 */           if (header != null) {
/*     */             try {
/* 430 */               numBytes = Long.parseLong(header);
/*     */             }
/*     */             catch (NumberFormatException ex)
/*     */             {
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 438 */           transactionState.setBytesSent(numBytes);
/* 439 */           HttpsURLConnectionExtension.this.addTransactionAndErrorData(transactionState);
/*     */         }
/*     */       }
/*     */     });
/* 443 */     return out;
/*     */   }
/*     */ 
/*     */   public int getReadTimeout()
/*     */   {
/* 448 */     return this.impl.getReadTimeout();
/*     */   }
/*     */ 
/*     */   public Map<String, List<String>> getRequestProperties()
/*     */   {
/* 453 */     return this.impl.getRequestProperties();
/*     */   }
/*     */ 
/*     */   public String getRequestProperty(String field)
/*     */   {
/* 458 */     return this.impl.getRequestProperty(field);
/*     */   }
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 463 */     return this.impl.getURL();
/*     */   }
/*     */ 
/*     */   public boolean getUseCaches()
/*     */   {
/* 468 */     return this.impl.getUseCaches();
/*     */   }
/*     */ 
/*     */   public void setAllowUserInteraction(boolean newValue)
/*     */   {
/* 473 */     this.impl.setAllowUserInteraction(newValue);
/*     */   }
/*     */ 
/*     */   public void setConnectTimeout(int timeoutMillis)
/*     */   {
/* 478 */     this.impl.setConnectTimeout(timeoutMillis);
/*     */   }
/*     */ 
/*     */   public void setDefaultUseCaches(boolean newValue)
/*     */   {
/* 483 */     this.impl.setDefaultUseCaches(newValue);
/*     */   }
/*     */ 
/*     */   public void setDoInput(boolean newValue)
/*     */   {
/* 488 */     this.impl.setDoInput(newValue);
/*     */   }
/*     */ 
/*     */   public void setDoOutput(boolean newValue)
/*     */   {
/* 493 */     this.impl.setDoOutput(newValue);
/*     */   }
/*     */ 
/*     */   public void setIfModifiedSince(long newValue)
/*     */   {
/* 498 */     this.impl.setIfModifiedSince(newValue);
/*     */   }
/*     */ 
/*     */   public void setReadTimeout(int timeoutMillis)
/*     */   {
/* 503 */     this.impl.setReadTimeout(timeoutMillis);
/*     */   }
/*     */ 
/*     */   public void setRequestProperty(String field, String newValue)
/*     */   {
/* 508 */     this.impl.setRequestProperty(field, newValue);
/*     */   }
/*     */ 
/*     */   public void setUseCaches(boolean newValue)
/*     */   {
/* 513 */     this.impl.setUseCaches(newValue);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 518 */     return this.impl.toString();
/*     */   }
/*     */ 
/*     */   public Principal getPeerPrincipal() throws SSLPeerUnverifiedException
/*     */   {
/* 523 */     return this.impl.getPeerPrincipal();
/*     */   }
/*     */ 
/*     */   public Principal getLocalPrincipal()
/*     */   {
/* 528 */     return this.impl.getLocalPrincipal();
/*     */   }
/*     */ 
/*     */   public void setHostnameVerifier(HostnameVerifier hostnameVerifier)
/*     */   {
/* 533 */     this.impl.setHostnameVerifier(hostnameVerifier);
/*     */   }
/*     */ 
/*     */   public HostnameVerifier getHostnameVerifier()
/*     */   {
/* 538 */     return this.impl.getHostnameVerifier();
/*     */   }
/*     */ 
/*     */   public void setSSLSocketFactory(SSLSocketFactory sf)
/*     */   {
/* 543 */     this.impl.setSSLSocketFactory(sf);
/*     */   }
/*     */ 
/*     */   public SSLSocketFactory getSSLSocketFactory()
/*     */   {
/* 548 */     return this.impl.getSSLSocketFactory();
/*     */   }
/*     */ 
/*     */   private void checkResponse() {
/* 552 */     if (!getTransactionState().isComplete())
/* 553 */       TransactionStateUtil.inspectAndInstrumentResponse(getTransactionState(), this.impl);
/*     */   }
/*     */ 
/*     */   private TransactionState getTransactionState()
/*     */   {
/* 558 */     if (this.transactionState == null) {
/* 559 */       this.transactionState = new TransactionState();
/* 560 */       TransactionStateUtil.inspectAndInstrument(this.transactionState, this.impl);
/*     */     }
/* 562 */     return this.transactionState;
/*     */   }
/*     */ 
/*     */   private void error(Exception e) {
/* 566 */     TransactionState transactionState = getTransactionState();
/* 567 */     TransactionStateUtil.setErrorCodeFromException(transactionState, e);
/* 568 */     if (!transactionState.isComplete()) {
/* 569 */       TransactionStateUtil.inspectAndInstrumentResponse(transactionState, this.impl);
/* 570 */       TransactionData transactionData = transactionState.end();
/*     */ 
/* 573 */       if (transactionData != null)
/* 574 */         TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addTransactionAndErrorData(TransactionState transactionState)
/*     */   {
/* 580 */     TransactionData transactionData = transactionState.end();
/*     */ 
/* 583 */     if (transactionData == null) {
/* 584 */       return;
/*     */     }
/*     */ 
/* 587 */     TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */ 
/* 589 */     if (transactionState.getStatusCode() >= 400L) {
/* 590 */       StringBuilder responseBody = new StringBuilder();
/*     */       try {
/* 592 */         InputStream errorStream = getErrorStream();
/* 593 */         if ((errorStream instanceof CountingInputStream))
/* 594 */           responseBody.append(((CountingInputStream)errorStream).getBufferAsString());
/*     */       }
/*     */       catch (Exception e) {
/* 597 */         log.error(e.toString());
/*     */       }
/*     */ 
/* 601 */       Map params = new TreeMap();
/* 602 */       String contentType = this.impl.getContentType();
/*     */ 
/* 604 */       if ((contentType != null) && (!"".equals(contentType))) {
/* 605 */         params.put("content_type", contentType);
/*     */       }
/*     */ 
/* 611 */       params.put("content_length", transactionState.getBytesReceived() + "");
/*     */ 
/* 613 */       Measurements.addHttpError(transactionData, responseBody.toString(), params);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.HttpsURLConnectionExtension
 * JD-Core Version:    0.6.2
 */