/*     */ package com.newrelic.agent.android.instrumentation;
/*     */ 
/*     */ import com.newrelic.agent.android.TaskQueue;
/*     */ import com.newrelic.agent.android.api.common.TransactionData;
/*     */ import com.newrelic.agent.android.instrumentation.httpclient.ResponseHandlerImpl;
/*     */ import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
/*     */ import java.io.IOException;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URLConnection;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.ResponseHandler;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ 
/*     */ public final class HttpInstrumentation
/*     */ {
/*     */   @WrapReturn(className="java/net/URL", methodName="openConnection", methodDesc="()Ljava/net/URLConnection;")
/*     */   public static URLConnection openConnection(URLConnection connection)
/*     */   {
/*  26 */     if ((connection instanceof HttpsURLConnection)) {
/*  27 */       return new HttpsURLConnectionExtension((HttpsURLConnection)connection);
/*     */     }
/*  29 */     if ((connection instanceof HttpURLConnection)) {
/*  30 */       return new HttpURLConnectionExtension((HttpURLConnection)connection);
/*     */     }
/*     */ 
/*  33 */     return connection;
/*     */   }
/*     */ 
/*     */   @WrapReturn(className="java.net.URL", methodName="openConnection", methodDesc="(Ljava/net/Proxy;)Ljava/net/URLConnection;")
/*     */   public static URLConnection openConnectionWithProxy(URLConnection connection)
/*     */   {
/*  39 */     if ((connection instanceof HttpsURLConnection)) {
/*  40 */       return new HttpsURLConnectionExtension((HttpsURLConnection)connection);
/*     */     }
/*  42 */     if ((connection instanceof HttpURLConnection)) {
/*  43 */       return new HttpURLConnectionExtension((HttpURLConnection)connection);
/*     */     }
/*     */ 
/*  46 */     return connection;
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static HttpResponse execute(HttpClient httpClient, HttpHost target, HttpRequest request, HttpContext context)
/*     */     throws IOException
/*     */   {
/*  53 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/*  55 */       return _(httpClient.execute(target, _(target, request, transactionState), context), transactionState);
/*     */     }
/*     */     catch (IOException e) {
/*  58 */       httpClientError(transactionState, e);
/*  59 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static <T> T execute(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
/*     */     throws IOException, ClientProtocolException
/*     */   {
/*  67 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/*  69 */       return httpClient.execute(target, _(target, request, transactionState), _(responseHandler, transactionState), context);
/*     */     }
/*     */     catch (ClientProtocolException e) {
/*  72 */       httpClientError(transactionState, e);
/*  73 */       throw e;
/*     */     }
/*     */     catch (IOException e) {
/*  76 */       httpClientError(transactionState, e);
/*  77 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static <T> T execute(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
/*     */     throws IOException, ClientProtocolException
/*     */   {
/*  85 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/*  87 */       return httpClient.execute(target, _(target, request, transactionState), _(responseHandler, transactionState));
/*     */     }
/*     */     catch (ClientProtocolException e) {
/*  90 */       httpClientError(transactionState, e);
/*  91 */       throw e;
/*     */     }
/*     */     catch (IOException e) {
/*  94 */       httpClientError(transactionState, e);
/*  95 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static HttpResponse execute(HttpClient httpClient, HttpHost target, HttpRequest request) throws IOException
/*     */   {
/* 102 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/* 104 */       return _(httpClient.execute(target, _(target, request, transactionState)), transactionState);
/*     */     }
/*     */     catch (IOException e) {
/* 107 */       httpClientError(transactionState, e);
/* 108 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static HttpResponse execute(HttpClient httpClient, HttpUriRequest request, HttpContext context) throws IOException
/*     */   {
/* 115 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/* 117 */       return _(httpClient.execute(_(request, transactionState), context), transactionState);
/*     */     }
/*     */     catch (IOException e) {
/* 120 */       httpClientError(transactionState, e);
/* 121 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static <T> T execute(HttpClient httpClient, HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
/*     */     throws IOException, ClientProtocolException
/*     */   {
/* 129 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/* 131 */       return httpClient.execute(_(request, transactionState), _(responseHandler, transactionState), context);
/*     */     }
/*     */     catch (ClientProtocolException e) {
/* 134 */       httpClientError(transactionState, e);
/* 135 */       throw e;
/*     */     }
/*     */     catch (IOException e) {
/* 138 */       httpClientError(transactionState, e);
/* 139 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static <T> T execute(HttpClient httpClient, HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
/*     */     throws IOException, ClientProtocolException
/*     */   {
/* 147 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/* 149 */       return httpClient.execute(_(request, transactionState), _(responseHandler, transactionState));
/*     */     }
/*     */     catch (ClientProtocolException e) {
/* 152 */       httpClientError(transactionState, e);
/* 153 */       throw e;
/*     */     }
/*     */     catch (IOException e) {
/* 156 */       httpClientError(transactionState, e);
/* 157 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   @ReplaceCallSite
/*     */   public static HttpResponse execute(HttpClient httpClient, HttpUriRequest request) throws IOException {
/* 163 */     TransactionState transactionState = new TransactionState();
/*     */     try {
/* 165 */       return _(httpClient.execute(_(request, transactionState)), transactionState);
/*     */     }
/*     */     catch (IOException e) {
/* 168 */       httpClientError(transactionState, e);
/* 169 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void httpClientError(TransactionState transactionState, Exception e) {
/* 174 */     if (!transactionState.isComplete()) {
/* 175 */       TransactionStateUtil.setErrorCodeFromException(transactionState, e);
/* 176 */       TransactionData transactionData = transactionState.end();
/*     */ 
/* 179 */       if (transactionData != null)
/* 180 */         TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static HttpUriRequest _(HttpUriRequest request, TransactionState transactionState)
/*     */   {
/* 190 */     return TransactionStateUtil.inspectAndInstrument(transactionState, request);
/*     */   }
/*     */ 
/*     */   private static HttpRequest _(HttpHost host, HttpRequest request, TransactionState transactionState) {
/* 194 */     return TransactionStateUtil.inspectAndInstrument(transactionState, host, request);
/*     */   }
/*     */ 
/*     */   private static HttpResponse _(HttpResponse response, TransactionState transactionState) {
/* 198 */     return TransactionStateUtil.inspectAndInstrument(transactionState, response);
/*     */   }
/*     */ 
/*     */   private static <T> ResponseHandler<? extends T> _(ResponseHandler<? extends T> handler, TransactionState transactionState) {
/* 202 */     return ResponseHandlerImpl.wrap(handler, transactionState);
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.HttpInstrumentation
 * JD-Core Version:    0.6.2
 */