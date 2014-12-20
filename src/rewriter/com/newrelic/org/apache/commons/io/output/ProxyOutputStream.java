/*     */ package com.newrelic.org.apache.commons.io.output;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public class ProxyOutputStream extends FilterOutputStream
/*     */ {
/*     */   public ProxyOutputStream(OutputStream proxy)
/*     */   {
/*  42 */     super(proxy);
/*     */   }
/*     */ 
/*     */   public void write(int idx)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  54 */       beforeWrite(1);
/*  55 */       this.out.write(idx);
/*  56 */       afterWrite(1);
/*     */     } catch (IOException e) {
/*  58 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(byte[] bts)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  70 */       int len = bts != null ? bts.length : 0;
/*  71 */       beforeWrite(len);
/*  72 */       this.out.write(bts);
/*  73 */       afterWrite(len);
/*     */     } catch (IOException e) {
/*  75 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(byte[] bts, int st, int end)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/*  89 */       beforeWrite(end);
/*  90 */       this.out.write(bts, st, end);
/*  91 */       afterWrite(end);
/*     */     } catch (IOException e) {
/*  93 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 104 */       this.out.flush();
/*     */     } catch (IOException e) {
/* 106 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 117 */       this.out.close();
/*     */     } catch (IOException e) {
/* 119 */       handleIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void beforeWrite(int n)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void afterWrite(int n)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void handleIOException(IOException e)
/*     */     throws IOException
/*     */   {
/* 166 */     throw e;
/*     */   }
/*     */ }

/* Location:           /home/think/Downloads/newrelic-android-4.120.0/lib/class.rewriter.jar
 * Qualified Name:     com.newrelic.org.apache.commons.io.output.ProxyOutputStream
 * JD-Core Version:    0.6.2
 */