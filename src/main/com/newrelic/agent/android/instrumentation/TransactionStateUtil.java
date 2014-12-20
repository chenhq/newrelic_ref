/*     */ package com.newrelic.agent.android.instrumentation;
/*     */ 
/*     */ import com.newrelic.agent.android.Agent;
/*     */ import com.newrelic.agent.android.Measurements;
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.instrumentation.httpclient.ContentBufferingResponseEntityImpl;
/*     */ import com.newrelic.agent.android.instrumentation.httpclient.HttpRequestEntityImpl;
/*     */ import com.newrelic.agent.android.instrumentation.httpclient.HttpResponseEntityImpl;
/*     */ import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import com.newrelic.agent.android.tracing.TraceMachine;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.ConnectException;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import javax.net.ssl.SSLException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.HttpResponseException;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.conn.ConnectTimeoutException;
/*     */ 
/*     */ public class TransactionStateUtil
/*     */ {
/*  32 */   private static final AgentLog log = AgentLogManager.getAgentLog();
/*     */   public static final int NSURLErrorUnknown = -1;
/*     */   public static final int NSURLErrorBadURL = -1000;
/*     */   public static final int NSURLErrorTimedOut = -1001;
/*     */   public static final int NSURLErrorCannotConnectToHost = -1004;
/*     */   public static final int NSURLErrorDNSLookupFailed = -1006;
/*     */   public static final int NSURLErrorBadServerResponse = -1011;
/*     */   public static final int NSURLErrorSecureConnectionFailed = -1200;
/*     */   private static final String CONTENT_LENGTH_HEADER = "Content-Length";
/*     */   private static final String CONTENT_TYPE_HEADER = "Content-Type";
/*     */   public static final String APP_DATA_HEADER = "X-NewRelic-App-Data";
/*     */   public static final String CROSS_PROCESS_ID_HEADER = "X-NewRelic-ID";
/*     */ 
/*     */   public static void inspectAndInstrument(TransactionState transactionState, HttpURLConnection conn)
/*     */   {
/*  53 */     transactionState.setUrl(conn.getURL().toString());
/*  54 */     transactionState.setHttpMethod(conn.getRequestMethod());
/*  55 */     transactionState.setCarrier(Agent.getActiveNetworkCarrier());
/*  56 */     transactionState.setWanType(Agent.getActiveNetworkWanType());
/*     */   }
/*     */ 
/*     */   public static void setCrossProcessHeader(HttpURLConnection conn) {
/*  60 */     String crossProcessId = Agent.getCrossProcessId();
/*  61 */     if (crossProcessId != null)
/*  62 */       conn.setRequestProperty("X-NewRelic-ID".toLowerCase(Locale.ENGLISH), crossProcessId);
/*     */   }
/*     */ 
/*     */   public static void inspectAndInstrumentResponse(TransactionState transactionState, HttpURLConnection conn)
/*     */   {
/*  67 */     String appData = conn.getHeaderField("X-NewRelic-App-Data");
/*  68 */     if ((appData != null) && (!"".equals(appData))) {
/*  69 */       transactionState.setAppData(appData);
/*     */     }
/*  71 */     int contentLength = conn.getContentLength();
/*  72 */     if (contentLength >= 0) {
/*  73 */       transactionState.setBytesReceived(contentLength);
/*     */     }
/*  75 */     int statusCode = 0;
/*     */     try {
/*  77 */       statusCode = conn.getResponseCode();
/*     */     }
/*     */     catch (IOException e) {
/*  80 */       log.debug("Failed to retrieve response code due to an I/O exception: " + e.getMessage());
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*  87 */       log.error("Failed to retrieve response code due to underlying (Harmony?) NPE", e);
/*     */     }
/*  89 */     transactionState.setStatusCode(statusCode);
/*     */   }
/*     */ 
/*     */   public static HttpRequest inspectAndInstrument(TransactionState transactionState, HttpHost host, HttpRequest request) {
/*  93 */     addCrossProcessIdHeader(request);
/*     */ 
/*  98 */     RequestLine requestLine = request.getRequestLine();
/*  99 */     if (requestLine != null) {
/* 100 */       String uri = requestLine.getUri();
/* 101 */       boolean isAbsoluteUri = (uri != null) && (uri.length() >= 10) && (uri.substring(0, 10).indexOf("://") >= 0);
/* 102 */       if ((!isAbsoluteUri) && (uri != null) && (host != null)) {
/* 103 */         String prefix = host.toURI().toString();
/* 104 */         transactionState.setUrl(prefix + ((prefix.endsWith("/")) || (uri.startsWith("/")) ? "" : "/") + uri);
/*     */       }
/* 106 */       else if (isAbsoluteUri) {
/* 107 */         transactionState.setUrl(uri);
/*     */       }
/*     */ 
/* 110 */       transactionState.setHttpMethod(requestLine.getMethod());
/*     */     }
/*     */ 
/* 116 */     if ((transactionState.getUrl() == null) || (transactionState.getHttpMethod() == null))
/*     */     {
/*     */       try
/*     */       {
/* 121 */         throw new Exception("TransactionData constructor was not provided with a valid URL, host or HTTP method");
/*     */       }
/*     */       catch (Exception e) {
/* 124 */         AgentLogManager.getAgentLog().error(MessageFormat.format("TransactionStateUtil.inspectAndInstrument(...) for {0} could not determine request URL or HTTP method [host={1}, requestLine={2}]", new Object[] { request.getClass().getCanonicalName(), host, requestLine }), e);
/*     */ 
/* 126 */         return request;
/*     */       }
/*     */     }
/*     */ 
/* 130 */     transactionState.setCarrier(Agent.getActiveNetworkCarrier());
/* 131 */     transactionState.setWanType(Agent.getActiveNetworkWanType());
/* 132 */     wrapRequestEntity(transactionState, request);
/* 133 */     return request;
/*     */   }
/*     */ 
/*     */   public static HttpUriRequest inspectAndInstrument(TransactionState transactionState, HttpUriRequest request) {
/* 137 */     addCrossProcessIdHeader(request);
/* 138 */     transactionState.setUrl(request.getURI().toString());
/* 139 */     transactionState.setHttpMethod(request.getMethod());
/* 140 */     transactionState.setCarrier(Agent.getActiveNetworkCarrier());
/* 141 */     transactionState.setWanType(Agent.getActiveNetworkWanType());
/* 142 */     wrapRequestEntity(transactionState, request);
/* 143 */     return request;
/*     */   }
/*     */ 
/*     */   private static void addCrossProcessIdHeader(HttpRequest request) {
/* 147 */     String crossProcessId = Agent.getCrossProcessId();
/* 148 */     if (crossProcessId != null) {
/* 149 */       TraceMachine.setCurrentTraceParam("cross_process_data", crossProcessId);
/* 150 */       request.setHeader("X-NewRelic-ID", crossProcessId);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void wrapRequestEntity(TransactionState transactionState, HttpRequest request) {
/* 155 */     if ((request instanceof HttpEntityEnclosingRequest)) {
/* 156 */       HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)request;
/* 157 */       if (entityEnclosingRequest.getEntity() != null)
/* 158 */         entityEnclosingRequest.setEntity(new HttpRequestEntityImpl(entityEnclosingRequest.getEntity(), transactionState));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static HttpResponse inspectAndInstrument(TransactionState transactionState, HttpResponse response)
/*     */   {
/* 164 */     transactionState.setStatusCode(response.getStatusLine().getStatusCode());
/*     */ 
/* 166 */     Header[] appDataHeader = response.getHeaders("X-NewRelic-App-Data");
/* 167 */     if ((appDataHeader != null) && (appDataHeader.length > 0) && (!"".equals(appDataHeader[0].getValue()))) {
/* 168 */       transactionState.setAppData(appDataHeader[0].getValue());
/*     */     }
/*     */ 
/* 171 */     Header[] contentLengthHeader = response.getHeaders("Content-Length");
/* 172 */     long contentLengthFromHeader = -1L;
/* 173 */     if ((contentLengthHeader != null) && (contentLengthHeader.length > 0)) {
/*     */       try {
/* 175 */         contentLengthFromHeader = Long.parseLong(contentLengthHeader[0].getValue());
/* 176 */         transactionState.setBytesReceived(contentLengthFromHeader);
/*     */ 
/* 178 */         addTransactionAndErrorData(transactionState, response);
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 181 */         log.warning("Failed to parse content length: " + e.toString());
/*     */       }
/*     */     }
/* 184 */     else if (response.getEntity() != null) {
/* 185 */       response.setEntity(new HttpResponseEntityImpl(response.getEntity(), transactionState, contentLengthFromHeader));
/*     */     }
/*     */     else
/*     */     {
/* 191 */       transactionState.setBytesReceived(0L);
/* 192 */       addTransactionAndErrorData(transactionState, null);
/*     */     }
/* 194 */     return response;
/*     */   }
/*     */ 
/*     */   public static void setErrorCodeFromException(TransactionState transactionState, Exception e) {
/* 198 */     if ((e instanceof UnknownHostException)) {
/* 199 */       transactionState.setErrorCode(-1006);
/*     */     }
/* 201 */     else if (((e instanceof SocketTimeoutException)) || ((e instanceof ConnectTimeoutException))) {
/* 202 */       transactionState.setErrorCode(-1001);
/*     */     }
/* 204 */     else if ((e instanceof ConnectException))
/*     */     {
/* 208 */       transactionState.setErrorCode(-1004);
/*     */     }
/* 210 */     else if ((e instanceof MalformedURLException)) {
/* 211 */       transactionState.setErrorCode(-1000);
/*     */     }
/* 213 */     else if ((e instanceof SSLException)) {
/* 214 */       transactionState.setErrorCode(-1200);
/*     */     }
/* 216 */     else if ((e instanceof HttpResponseException)) {
/* 217 */       transactionState.setStatusCode(((HttpResponseException)e).getStatusCode());
/*     */     }
/* 219 */     else if ((e instanceof ClientProtocolException)) {
/* 220 */       transactionState.setErrorCode(-1011);
/*     */     }
/*     */     else
/* 223 */       transactionState.setErrorCode(-1);
/*     */   }
/*     */ 
/*     */   private static void addTransactionAndErrorData(TransactionState transactionState, HttpResponse response)
/*     */   {
/* 228 */     TransactionData transactionData = transactionState.end();
/*     */ 
/* 235 */     if (transactionData == null) {
/* 236 */       return;
/*     */     }
/*     */ 
/* 239 */     TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */ 
/* 242 */     if (transactionState.getStatusCode() >= 400L) {
/* 243 */       StringBuilder responseBody = new StringBuilder();
/*     */       try {
/* 245 */         if (!(response.getEntity() instanceof HttpRequestEntityImpl))
/*     */         {
/* 247 */           response.setEntity(new ContentBufferingResponseEntityImpl(response.getEntity()));
/*     */         }
/* 249 */         InputStream content = response.getEntity().getContent();
/* 250 */         if ((content instanceof CountingInputStream))
/* 251 */           responseBody.append(((CountingInputStream)content).getBufferAsString());
/*     */         else
/* 253 */           log.error("Unable to wrap content stream for entity");
/*     */       }
/*     */       catch (IllegalStateException e) {
/* 256 */         log.error(e.toString());
/*     */       } catch (IOException e) {
/* 258 */         log.error(e.toString());
/*     */       }
/*     */ 
/* 262 */       Header[] contentTypeHeader = response.getHeaders("Content-Type");
/* 263 */       String contentType = null;
/*     */ 
/* 265 */       if ((contentTypeHeader != null) && (contentTypeHeader.length > 0) && (!"".equals(contentTypeHeader[0].getValue()))) {
/* 266 */         contentType = contentTypeHeader[0].getValue();
/*     */       }
/*     */ 
/* 269 */       Map params = new TreeMap();
/* 270 */       if ((contentType != null) && (contentType.length() > 0)) {
/* 271 */         params.put("content_type", contentType);
/*     */       }
/*     */ 
/* 278 */       params.put("content_length", transactionState.getBytesReceived() + "");
/*     */ 
/* 281 */       Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), responseBody.toString(), params);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.TransactionStateUtil
 * JD-Core Version:    0.6.2
 */