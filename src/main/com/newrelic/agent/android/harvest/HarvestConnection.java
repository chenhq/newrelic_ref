/*     */ package com.newrelic.agent.android.harvest;
/*     */ 
/*     */ import com.newrelic.agent.android.harvest.type.Harvestable;
/*     */ import com.newrelic.agent.android.logging.AgentLog;
/*     */ import com.newrelic.agent.android.logging.AgentLogManager;
/*     */ import com.newrelic.agent.android.stats.StatsEngine;
/*     */ import com.newrelic.agent.android.stats.TicToc;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.ConnectException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.zip.Deflater;
/*     */ import javax.net.ssl.SSLException;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.client.methods.HttpPost;
/*     */ import org.apache.http.conn.ConnectTimeoutException;
/*     */ import org.apache.http.entity.ByteArrayEntity;
/*     */ import org.apache.http.entity.StringEntity;
/*     */ import org.apache.http.impl.client.DefaultHttpClient;
/*     */ import org.apache.http.params.BasicHttpParams;
/*     */ import org.apache.http.params.HttpConnectionParams;
/*     */ 
/*     */ public class HarvestConnection
/*     */ {
/*     */   public static final int NSURLErrorUnknown = -1;
/*     */   public static final int NSURLErrorBadURL = -1000;
/*     */   public static final int NSURLErrorTimedOut = -1001;
/*     */   public static final int NSURLErrorCannotConnectToHost = -1004;
/*     */   public static final int NSURLErrorDNSLookupFailed = -1006;
/*     */   public static final int NSURLErrorBadServerResponse = -1011;
/*     */   public static final int NSURLErrorSecureConnectionFailed = -1200;
/*  40 */   private final AgentLog log = AgentLogManager.getAgentLog();
/*     */   private static final String COLLECTOR_CONNECT_URI = "/mobile/v2/connect";
/*     */   private static final String COLLECTOR_DATA_URI = "/mobile/v2/data";
/*     */   private static final String APPLICATION_TOKEN_HEADER = "X-App-License-Key";
/*     */   private static final String CONNECT_TIME_HEADER = "X-NewRelic-Connect-Time";
/*  48 */   private static final Boolean DISABLE_COMPRESSION_FOR_DEBUGGING = Boolean.valueOf(false);
/*     */   private String collectorHost;
/*     */   private String applicationToken;
/*     */   private long serverTimestamp;
/*     */   private final HttpClient collectorClient;
/*     */   private ConnectInformation connectInformation;
/*     */   private boolean useSsl;
/*     */ 
/*     */   public HarvestConnection()
/*     */   {
/*  62 */     int TIMEOUT_IN_SECONDS = 20;
/*  63 */     int CONNECTION_TIMEOUT = (int)TimeUnit.MILLISECONDS.convert(20L, TimeUnit.SECONDS);
/*  64 */     int SOCKET_BUFFER_SIZE = 8192;
/*     */ 
/*  66 */     BasicHttpParams params = new BasicHttpParams();
/*  67 */     HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
/*  68 */     HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
/*  69 */     HttpConnectionParams.setTcpNoDelay(params, true);
/*  70 */     HttpConnectionParams.setSocketBufferSize(params, 8192);
/*  71 */     this.collectorClient = new DefaultHttpClient(params);
/*     */   }
/*     */ 
/*     */   public HttpPost createPost(String uri, String message)
/*     */   {
/*  85 */     String contentEncoding = (message.length() <= 512) || (DISABLE_COMPRESSION_FOR_DEBUGGING.booleanValue()) ? "identity" : "deflate";
/*     */ 
/*  87 */     HttpPost post = new HttpPost(uri);
/*     */ 
/*  89 */     post.addHeader("Content-Type", "application/json");
/*  90 */     post.addHeader("Content-Encoding", contentEncoding);
/*  91 */     post.addHeader("User-Agent", System.getProperty("http.agent"));
/*     */ 
/*  93 */     if (this.applicationToken == null) {
/*  94 */       this.log.error("Cannot create POST without an Application Token.");
/*  95 */       return null;
/*     */     }
/*     */ 
/*  98 */     post.addHeader("X-App-License-Key", this.applicationToken);
/*     */ 
/* 100 */     if (this.serverTimestamp != 0L) {
/* 101 */       post.addHeader("X-NewRelic-Connect-Time", Long.valueOf(this.serverTimestamp).toString());
/*     */     }
/*     */ 
/* 104 */     if ("deflate".equals(contentEncoding)) {
/* 105 */       byte[] deflated = deflate(message);
/* 106 */       post.setEntity(new ByteArrayEntity(deflated));
/*     */     } else {
/*     */       try {
/* 109 */         post.setEntity(new StringEntity(message, "utf-8"));
/*     */       } catch (UnsupportedEncodingException e) {
/* 111 */         this.log.error("UTF-8 is unsupported");
/* 112 */         throw new IllegalArgumentException(e);
/*     */       }
/*     */     }
/* 115 */     return post;
/*     */   }
/*     */ 
/*     */   public HarvestResponse send(HttpPost post)
/*     */   {
/* 125 */     HarvestResponse harvestResponse = new HarvestResponse();
/*     */     HttpResponse response;
/*     */     try
/*     */     {
/* 129 */       TicToc timer = new TicToc();
/* 130 */       timer.tic();
/* 131 */       response = this.collectorClient.execute(post);
/* 132 */       harvestResponse.setResponseTime(timer.toc());
/*     */     } catch (Exception e) {
/* 134 */       this.log.error("Failed to send POST to collector: " + e.getMessage());
/* 135 */       recordCollectorError(e);
/* 136 */       return null;
/*     */     }
/* 138 */     harvestResponse.setStatusCode(response.getStatusLine().getStatusCode());
/*     */     try {
/* 140 */       harvestResponse.setResponseBody(readResponse(response));
/*     */     } catch (IOException e) {
/* 142 */       e.printStackTrace();
/* 143 */       this.log.error("Failed to retrieve collector response: " + e.getMessage());
/*     */     }
/* 145 */     return harvestResponse;
/*     */   }
/*     */ 
/*     */   public HarvestResponse sendConnect()
/*     */   {
/* 154 */     if (this.connectInformation == null) {
/* 155 */       throw new IllegalArgumentException();
/*     */     }
/* 157 */     HttpPost connectPost = createConnectPost(this.connectInformation.toJsonString());
/* 158 */     if (connectPost == null) {
/* 159 */       this.log.error("Failed to create connect POST");
/* 160 */       return null;
/*     */     }
/*     */ 
/* 163 */     TicToc timer = new TicToc();
/* 164 */     timer.tic();
/*     */ 
/* 166 */     HarvestResponse response = send(connectPost);
/*     */ 
/* 168 */     StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Collector/Connect", timer.toc());
/* 169 */     return response;
/*     */   }
/*     */ 
/*     */   public HarvestResponse sendData(Harvestable harvestable)
/*     */   {
/* 178 */     if (harvestable == null) {
/* 179 */       throw new IllegalArgumentException();
/*     */     }
/* 181 */     HttpPost dataPost = createDataPost(harvestable.toJsonString());
/* 182 */     if (dataPost == null) {
/* 183 */       this.log.error("Failed to create data POST");
/* 184 */       return null;
/*     */     }
/* 186 */     return send(dataPost);
/*     */   }
/*     */ 
/*     */   public HttpPost createConnectPost(String message)
/*     */   {
/* 196 */     return createPost(getCollectorConnectUri(), message);
/*     */   }
/*     */ 
/*     */   public HttpPost createDataPost(String message)
/*     */   {
/* 206 */     return createPost(getCollectorDataUri(), message);
/*     */   }
/*     */ 
/*     */   private byte[] deflate(String message)
/*     */   {
/* 211 */     int DEFLATE_BUFFER_SIZE = 8192;
/*     */ 
/* 213 */     Deflater deflater = new Deflater();
/* 214 */     deflater.setInput(message.getBytes());
/* 215 */     deflater.finish();
/*     */ 
/* 217 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 218 */     byte[] buf = new byte[8192];
/* 219 */     while (!deflater.finished()) {
/* 220 */       int byteCount = deflater.deflate(buf);
/* 221 */       if (byteCount <= 0) {
/* 222 */         this.log.error("HTTP request contains an incomplete payload");
/*     */       }
/* 224 */       baos.write(buf, 0, byteCount);
/*     */     }
/* 226 */     deflater.end();
/* 227 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   public static String readResponse(HttpResponse response) throws IOException
/*     */   {
/* 232 */     int RESPONSE_BUFFER_SIZE = 8192;
/* 233 */     char[] buf = new char[8192];
/* 234 */     StringBuilder sb = new StringBuilder();
/* 235 */     InputStream in = response.getEntity().getContent();
/*     */     try {
/* 237 */       BufferedReader reader = new BufferedReader(new InputStreamReader(in));
/*     */       while (true) {
/* 239 */         int n = reader.read(buf);
/* 240 */         if (n < 0) break;
/* 241 */         sb.append(buf, 0, n);
/*     */       }
/*     */     } finally {
/* 244 */       in.close();
/*     */     }
/* 246 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private void recordCollectorError(Exception e) {
/* 250 */     StatsEngine.get().inc("Supportability/AgentHealth/Collector/ResponseErrorCodes/" + exceptionToErrorCode(e));
/*     */   }
/*     */ 
/*     */   private int exceptionToErrorCode(Exception e) {
/* 254 */     if ((e instanceof ClientProtocolException))
/* 255 */       return -1011;
/* 256 */     if ((e instanceof UnknownHostException))
/* 257 */       return -1006;
/* 258 */     if ((e instanceof SocketTimeoutException))
/* 259 */       return -1001;
/* 260 */     if ((e instanceof ConnectTimeoutException))
/* 261 */       return -1001;
/* 262 */     if ((e instanceof ConnectException))
/* 263 */       return -1004;
/* 264 */     if ((e instanceof MalformedURLException))
/* 265 */       return -1000;
/* 266 */     if ((e instanceof SSLException)) {
/* 267 */       return -1200;
/*     */     }
/* 269 */     return -1;
/*     */   }
/*     */ 
/*     */   private String getCollectorUri(String resource) {
/* 273 */     String protocol = this.useSsl ? "https://" : "http://";
/* 274 */     return protocol + this.collectorHost + resource;
/*     */   }
/*     */ 
/*     */   private String getCollectorConnectUri() {
/* 278 */     return getCollectorUri("/mobile/v2/connect");
/*     */   }
/*     */ 
/*     */   private String getCollectorDataUri() {
/* 282 */     return getCollectorUri("/mobile/v2/data");
/*     */   }
/*     */ 
/*     */   public void setServerTimestamp(long serverTimestamp) {
/* 286 */     this.log.debug("Setting server timestamp: " + serverTimestamp);
/* 287 */     this.serverTimestamp = serverTimestamp;
/*     */   }
/*     */ 
/*     */   public void useSsl(boolean useSsl) {
/* 291 */     this.useSsl = useSsl;
/*     */   }
/*     */ 
/*     */   public void setApplicationToken(String applicationToken) {
/* 295 */     this.applicationToken = applicationToken;
/*     */   }
/*     */ 
/*     */   public void setCollectorHost(String collectorHost) {
/* 299 */     this.collectorHost = collectorHost;
/*     */   }
/*     */ 
/*     */   public void setConnectInformation(ConnectInformation connectInformation) {
/* 303 */     this.connectInformation = connectInformation;
/*     */   }
/*     */ 
/*     */   public ConnectInformation getConnectInformation() {
/* 307 */     return this.connectInformation;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.harvest.HarvestConnection
 * JD-Core Version:    0.6.2
 */