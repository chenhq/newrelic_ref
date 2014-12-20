/*    */ package com.newrelic.agent.android.instrumentation.httpclient;
/*    */ 
/*    */ import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HttpEntity;
/*    */ 
/*    */ public class ContentBufferingResponseEntityImpl
/*    */   implements HttpEntity
/*    */ {
/*    */   final HttpEntity impl;
/*    */   private CountingInputStream contentStream;
/*    */ 
/*    */   public ContentBufferingResponseEntityImpl(HttpEntity impl)
/*    */   {
/* 17 */     if (impl == null) {
/* 18 */       throw new IllegalArgumentException("Missing wrapped entity");
/*    */     }
/* 20 */     this.impl = impl;
/*    */   }
/*    */ 
/*    */   public void consumeContent() throws IOException
/*    */   {
/* 25 */     this.impl.consumeContent();
/*    */   }
/*    */ 
/*    */   public InputStream getContent() throws IOException, IllegalStateException
/*    */   {
/* 30 */     if (this.contentStream != null) {
/* 31 */       return this.contentStream;
/*    */     }
/* 33 */     this.contentStream = new CountingInputStream(this.impl.getContent(), true);
/* 34 */     return this.contentStream;
/*    */   }
/*    */ 
/*    */   public Header getContentEncoding()
/*    */   {
/* 39 */     return this.impl.getContentEncoding();
/*    */   }
/*    */ 
/*    */   public long getContentLength()
/*    */   {
/* 44 */     return this.impl.getContentLength();
/*    */   }
/*    */ 
/*    */   public Header getContentType()
/*    */   {
/* 49 */     return this.impl.getContentType();
/*    */   }
/*    */ 
/*    */   public boolean isChunked()
/*    */   {
/* 54 */     return this.impl.isChunked();
/*    */   }
/*    */ 
/*    */   public boolean isRepeatable()
/*    */   {
/* 59 */     return this.impl.isRepeatable();
/*    */   }
/*    */ 
/*    */   public boolean isStreaming()
/*    */   {
/* 64 */     return this.impl.isStreaming();
/*    */   }
/*    */ 
/*    */   public void writeTo(OutputStream outputStream) throws IOException
/*    */   {
/* 69 */     this.impl.writeTo(outputStream);
/*    */   }
/*    */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/newrelic.android.jar
 * Qualified Name:     com.newrelic.agent.android.instrumentation.httpclient.ContentBufferingResponseEntityImpl
 * JD-Core Version:    0.6.2
 */